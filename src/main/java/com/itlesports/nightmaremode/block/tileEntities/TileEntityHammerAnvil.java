package com.itlesports.nightmaremode.block.tileEntities;

import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import com.itlesports.nightmaremode.item.items.ItemHammer;
import com.itlesports.nightmaremode.network.HammerAnvilNet;
import com.itlesports.nightmaremode.util.AutoAnvilUpgrade;
import com.itlesports.nightmaremode.util.HammerAnvilAnimation;
import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import net.minecraft.src.ISidedInventory;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

import java.util.ArrayDeque;
import java.util.Deque;

public class TileEntityHammerAnvil extends TileEntity implements ISidedInventory, api.block.TileEntityDataPacketHandler {
    private static final int SOUND_INTERVAL_TICKS = 2;
    private static final int MAX_ANIMATIONS = 3;
    private static final int MIN_FOOD_LEVEL = 6;
    private static final float EXHAUSTION_PER_HIT = 0.2F;

    protected int maxUses = -1;
    protected int usesRemaining = -1;
    private int queuedHammerSounds;
    private int soundDelay;
    private boolean breakWhenSoundsFinish;
    private boolean hasBeenUsed;
    private boolean removalHandled;
    private boolean harvestedWithProperTool;
    public static final int HAMMER_SLOT = 0, INPUT_SLOT = 1, OUTPUT_SLOT = 2;
    private final ItemStack[] inventory = new ItemStack[3];
    private int upgradeTier;
    private int processTicks;
    private boolean automaticActive;
    private boolean soundMuted;
    private boolean hammerHidden;
    private ItemStack workInput;
    // transient, client-only visuals; never inventory or saved crafting state.
    private final Deque<HammerAnvilAnimation> animations = new ArrayDeque<>();

    public TileEntityHammerAnvil() {
    }

    protected TileEntityHammerAnvil(int maxUses) {
        this.maxUses = maxUses;
        this.usesRemaining = maxUses;
    }

    public boolean tryStartHammerOperation(EntityPlayer player) {
        if (this.isAutomatic()) return false;
        if (player == null || this.worldObj == null || this.worldObj.isRemote) {
            return false;
        }
        if (this.isBusy()) {
            return false;
        }
        if (!player.capabilities.isCreativeMode && player.getFoodStats().getFoodLevel() < MIN_FOOD_LEVEL) {
            this.sendStatus(player, "You are too hungry to use the anvil.");
            return false;
        }

        ItemStack input = player.getHeldItem();
        HammerRecipe recipe = HammerCraftingManager.instance.getRecipe(input);
        if (recipe == null) {
            this.sendStatus(player, "No hammer recipe for held item.");
            return false;
        }

        int hammerSlot = this.findUsableHammerSlot(player, recipe);
        if (hammerSlot < 0) {
            this.sendStatus(player, "A valid hammer is required.");
            return false;
        }

        int hits = recipe.getHitsRequired();
        if (!this.canSpendHits(hits)) {
            if (!this.isWaitingToBreak()) {
                this.sendStatus(player, "The anvil is too worn for this recipe.");
            }
            return false;
        }

        ItemStack visualInput = input.copy();
        ItemStack visualHammer = player.inventory.mainInventory[hammerSlot].copy();
        this.consumeInputAndDamageHammer(player, input, recipe.getInput(), hammerSlot, hits);
        recipe.chargePlayerExperience(player);
        player.addExhaustion(EXHAUSTION_PER_HIT * hits);
        this.deliverOutputs(player, recipe);
        this.spendHits(hits);
        player.inventoryContainer.detectAndSendChanges();
        player.inventory.onInventoryChanged();
        HammerAnvilNet.send(player, this, visualInput, visualHammer, hits);
        return true;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj == null) {
            return;
        }
        if (this.worldObj.isRemote) {
            if (this.automaticActive) ++this.processTicks;
            HammerAnvilAnimation animation = this.animations.peekFirst();
            if (animation != null && animation.tick()) this.animations.removeFirst();
            return;
        }

        if (this.isAutomatic()) {
            this.tickAutomatic();
            return;
        }

        if (this.queuedHammerSounds <= 0) {
            if (this.breakWhenSoundsFinish) {
                this.breakAnvil();
            }
            return;
        }

        if (this.soundDelay > 0) {
            --this.soundDelay;
            return;
        }

        this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
                "random.anvil_use", 0.5F, this.worldObj.rand.nextFloat() * 0.25F + 1.25F);
        --this.queuedHammerSounds;
        this.soundDelay = SOUND_INTERVAL_TICKS;

        if (this.queuedHammerSounds <= 0 && this.breakWhenSoundsFinish) {
            this.breakAnvil();
        }
    }

    public boolean canSpendHits(int hits) {
        return !this.isBusy() && (this.maxUses < 0 || (!this.breakWhenSoundsFinish && this.usesRemaining > 0));
    }

    public void queueAnimation(HammerAnvilAnimation animation) {
        if (this.worldObj != null && this.worldObj.isRemote && !this.isInvalid()
                && this.animations.size() < MAX_ANIMATIONS) {
            this.animations.addLast(animation);
        }
    }

    public HammerAnvilAnimation getAnimation() {
        return this.animations.peekFirst();
    }

    @Override
    public void invalidate() {
        this.animations.clear();
        super.invalidate();
    }

    public void spendHits(int hits) {
        this.hasBeenUsed = true;
        if (this.maxUses >= 0) {
            this.usesRemaining = Math.max(0, this.usesRemaining - hits);
            if (this.usesRemaining <= 0) {
                this.breakWhenSoundsFinish = true;
            }
        }
        this.queueHammerSounds(hits);
        this.syncState();
    }

    public int getUsesRemaining() {
        return this.usesRemaining;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public boolean isLimited() {
        return this.maxUses >= 0;
    }

    public boolean isPristine() {
        return !this.hasBeenUsed && (this.maxUses < 0 || this.usesRemaining == this.maxUses);
    }

    public void markHarvestedWithProperTool() {
        this.harvestedWithProperTool = true;
    }

    public boolean canRecoverPristineUpgrade() {
        return this.harvestedWithProperTool && this.isPristine();
    }

    public boolean claimRemoval() {
        if (this.removalHandled) return false;
        this.removalHandled = true;
        return true;
    }

    public boolean isBusy() {
        return this.queuedHammerSounds > 0;
    }

    public boolean isWaitingToBreak() {
        return this.breakWhenSoundsFinish && this.usesRemaining <= 0;
    }

    private void breakAnvil() {
        this.worldObj.playAuxSFX(2001, this.xCoord, this.yCoord, this.zCoord,
                this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord));
        this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
    }

    private void consumeInputAndDamageHammer(EntityPlayer player, ItemStack input, ItemStack recipeInput, int hammerSlot, int hits) {
        if (player.capabilities.isCreativeMode) {
            return;
        }

        input.stackSize -= recipeInput.stackSize;
        if (input.stackSize <= 0) {
            player.inventory.mainInventory[player.inventory.currentItem] = null;
        }

        ItemStack hammer = player.inventory.mainInventory[hammerSlot];
        hammer.damageItem(hits, player);
        if (hammer.stackSize <= 0) {
            player.inventory.mainInventory[hammerSlot] = null;
        }
    }

    private void deliverOutputs(EntityPlayer player, HammerRecipe recipe) {
        for (ItemStack output : recipe.getOutput()) {
            if (output == null) {
                continue;
            }

            ItemStack outputCopy = output.copy();
            player.inventory.addItemStackToInventory(outputCopy);
            if (outputCopy.stackSize > 0) {
                this.spawnOutputAboveAnvil(outputCopy);
            }
        }
    }

    private void spawnOutputAboveAnvil(ItemStack stack) {
        EntityItem entity = new EntityItem(this.worldObj, this.xCoord + 0.5D, this.yCoord + 1.2D, this.zCoord + 0.5D, stack);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        this.worldObj.spawnEntityInWorld(entity);
    }

    private int findUsableHammerSlot(EntityPlayer player, HammerRecipe recipe) {
        InventoryPlayer inventory = player.inventory;
        for (int i = 0; i < inventory.mainInventory.length; ++i) {
            ItemStack stack = inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemHammer && recipe.canPlayerUseHammer(stack, player)) {
                return i;
            }
        }
        return -1;
    }

    private void sendStatus(EntityPlayer player, String message) {
        if (player == null || player.worldObj.isRemote) {
            return;
        }
        String suffix = "";
        if (this.isLimited()) {
            suffix = " Uses: " + this.getUsesRemaining() + " / " + this.getMaxUses() + ".";
        }
        player.sendChatToPlayer(new ChatMessageComponent().addText(message + suffix));
    }

    private void queueHammerSounds(int hits) {
        this.queuedHammerSounds += Math.max(1, hits);
    }

    protected void syncState() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.onInventoryChanged();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("MaxUses")) {
            this.maxUses = tag.getInteger("MaxUses");
        }
        if (tag.hasKey("UsesRemaining")) {
            this.usesRemaining = tag.getInteger("UsesRemaining");
        }
        this.queuedHammerSounds = tag.getInteger("QueuedHammerSounds");
        this.soundDelay = tag.getInteger("SoundDelay");
        this.breakWhenSoundsFinish = tag.getBoolean("BreakWhenSoundsFinish");
        this.hasBeenUsed = tag.getBoolean("HasBeenUsed") || this.maxUses >= 0 && this.usesRemaining < this.maxUses;
        this.upgradeTier = Math.max(0, Math.min(4, tag.getInteger("AutoUpgrade")));
        this.processTicks = Math.max(0, tag.getInteger("AutoProgress"));
        this.automaticActive = tag.getBoolean("AutoActive") && this.isAutomatic();
        this.soundMuted = tag.getBoolean("AutoSoundMuted");
        this.hammerHidden = tag.getBoolean("AutoHammerHidden");
        this.workInput = tag.hasKey("AutoWorkInput") ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("AutoWorkInput")) : null;
        java.util.Arrays.fill(this.inventory, null);
        NBTTagList items = tag.getTagList("AutoItems");
        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound entry = (NBTTagCompound)items.tagAt(i);
            int slot = entry.getByte("Slot") & 255;
            if (slot < this.inventory.length) this.inventory[slot] = ItemStack.loadItemStackFromNBT(entry);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("MaxUses", this.maxUses);
        tag.setInteger("UsesRemaining", this.usesRemaining);
        tag.setInteger("QueuedHammerSounds", this.queuedHammerSounds);
        tag.setInteger("SoundDelay", this.soundDelay);
        tag.setBoolean("BreakWhenSoundsFinish", this.breakWhenSoundsFinish);
        tag.setBoolean("HasBeenUsed", this.hasBeenUsed);
        tag.setInteger("AutoUpgrade", this.upgradeTier);
        tag.setInteger("AutoProgress", this.processTicks);
        tag.setBoolean("AutoActive", this.automaticActive);
        tag.setBoolean("AutoSoundMuted", this.soundMuted);
        tag.setBoolean("AutoHammerHidden", this.hammerHidden);
        if (this.workInput != null) {
            NBTTagCompound input = new NBTTagCompound();
            this.workInput.writeToNBT(input);
            tag.setCompoundTag("AutoWorkInput", input);
        }
        NBTTagList items = new NBTTagList();
        for (int slot = 0; slot < this.inventory.length; ++slot) {
            if (this.inventory[slot] == null) continue;
            NBTTagCompound entry = new NBTTagCompound();
            entry.setByte("Slot", (byte)slot);
            this.inventory[slot].writeToNBT(entry);
            items.appendTag(entry);
        }
        tag.setTag("AutoItems", items);
    }

    public boolean isAutomatic() { return this.upgradeTier > 0; }
    public int getUpgradeTier() { return this.upgradeTier; }
    public AutoAnvilUpgrade getUpgrade() { return AutoAnvilUpgrade.fromTier(this.upgradeTier); }
    public int getProcessTicks() { return this.processTicks; }
    public boolean isAutomaticActive() { return this.automaticActive; }
    public boolean isSoundMuted() { return this.soundMuted; }
    public boolean isHammerHidden() { return this.hammerHidden; }

    public boolean togglePresentation(int button) {
        if (this.worldObj == null || this.worldObj.isRemote || !this.isAutomatic()) return false;
        if (button == 0) this.soundMuted = !this.soundMuted;
        else if (button == 1) this.hammerHidden = !this.hammerHidden;
        else return false;
        this.syncState();
        return true;
    }

    public boolean installUpgrade(AutoAnvilUpgrade upgrade) {
        if (this.worldObj == null || this.worldObj.isRemote || upgrade == null
                || upgrade.tier() != this.upgradeTier + 1 || this.isBusy() || this.isWaitingToBreak()) return false;
        this.upgradeTier = upgrade.tier();
        this.returnUnusedHammer();
        this.resetAutomaticProgress();
        this.syncState();
        return true;
    }

    public int getProcessTicksRequired() {
        HammerRecipe recipe = HammerCraftingManager.instance.getRecipe(this.inventory[INPUT_SLOT]);
        return recipe == null || !this.isAutomatic() ? 0 : recipe.getHitsRequired() * this.getUpgrade().ticksPerHit;
    }

    // gui properties use a separate client inventory, never the server's tile.
    public void setGuiProperty(int property, int value) {
        if (property == 0) this.upgradeTier = value;
        if (property == 1) this.processTicks = value;
        if (property == 2) this.usesRemaining = value;
        if (property == 3) this.maxUses = value;
        if (property == 4) this.automaticActive = value != 0;
        if (property == 5) this.soundMuted = value != 0;
        if (property == 6) this.hammerHidden = value != 0;
    }

    public int getGuiProperty(int property) {
        return switch (property) {
            case 0 -> this.upgradeTier;
            case 1 -> this.processTicks;
            case 2 -> this.usesRemaining;
            case 3 -> this.maxUses;
            case 4 -> this.automaticActive ? 1 : 0;
            case 5 -> this.soundMuted ? 1 : 0;
            case 6 -> this.hammerHidden ? 1 : 0;
            default -> 0;
        };
    }

    private void tickAutomatic() {
        this.returnUnusedHammer();
        HammerRecipe recipe = HammerCraftingManager.instance.getRecipe(this.inventory[INPUT_SLOT]);
        ItemStack recipeInput = recipe == null ? null : recipe.getInput();
        if (!ItemStack.areItemStacksEqual(this.workInput, recipeInput)) {
            this.resetAutomaticProgress();
            this.workInput = recipeInput;
        }
        ItemStack result = this.automaticResult(recipe);
        if (result == null || !this.canFitOutput(result)) {
            this.setAutomaticActive(false);
            return;
        }
        this.setAutomaticActive(true);
        ++this.processTicks;
        super.onInventoryChanged();
        if (!this.soundMuted && this.processTicks % this.getUpgrade().ticksPerHit == 0) {
            this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
                    "random.anvil_use", 0.25F, 1.25F);
        }
        if (this.processTicks < this.getProcessTicksRequired()) return;

        // commit only when the entire output fits; stalls never spend inputs or durability.
        this.inventory[INPUT_SLOT].stackSize -= recipeInput.stackSize;
        if (this.inventory[INPUT_SLOT].stackSize <= 0) this.inventory[INPUT_SLOT] = null;
        if (this.inventory[OUTPUT_SLOT] == null) this.inventory[OUTPUT_SLOT] = result;
        else this.inventory[OUTPUT_SLOT].stackSize += result.stackSize;
        if (this.getUpgrade().needsHammer()
                && this.inventory[HAMMER_SLOT].attemptDamageItem(recipe.getHitsRequired(), this.worldObj.rand)) {
            this.inventory[HAMMER_SLOT] = null;
        }
        this.hasBeenUsed = true;
        if (!this.getUpgrade().preventsAnvilWear() && this.maxUses >= 0) {
            this.usesRemaining = Math.max(0, this.usesRemaining - recipe.getHitsRequired());
        }
        this.resetAutomaticProgress();
        this.syncState();
        if (!this.getUpgrade().preventsAnvilWear() && this.maxUses >= 0 && this.usesRemaining <= 0) {
            this.breakAnvil();
        }
    }

    private ItemStack automaticResult(HammerRecipe recipe) {
        if (recipe == null || recipe.getExperienceCost() > 0 || SkillLockedCrafting.isWorldLocked(this.worldObj, recipe)) return null;
        AutoAnvilUpgrade upgrade = this.getUpgrade();
        ItemStack hammer = upgrade != null && upgrade.needsHammer() ? this.inventory[HAMMER_SLOT] : null;
        if (upgrade == null || upgrade.needsHammer() && (hammer == null || !(hammer.getItem() instanceof ItemHammer))) return null;
        if (!recipe.hammerMeetsEnchantmentRequirements(hammer)) return null;
        if (!upgrade.preventsAnvilWear() && this.maxUses >= 0 && this.usesRemaining <= 0) return null;
        ItemStack result = null;
        for (ItemStack output : recipe.getOutput()) {
            if (output == null) continue;
            if (result == null) result = output.copy();
            else if (sameItem(result, output)) result.stackSize += output.stackSize;
            else return null;
        }
        return result;
    }

    private boolean canFitOutput(ItemStack result) {
        int limit = Math.min(this.getInventoryStackLimit(), result.getMaxStackSize());
        ItemStack output = this.inventory[OUTPUT_SLOT];
        return result.stackSize > 0 && result.stackSize <= limit
                && (output == null || sameItem(output, result) && output.stackSize + result.stackSize <= limit);
    }

    private void returnUnusedHammer() {
        // return hammers on phase installation and migrate hammers stored by older saves.
        if (this.getUpgrade() == AutoAnvilUpgrade.PHASE && this.inventory[HAMMER_SLOT] != null) {
            this.spawnOutputAboveAnvil(this.inventory[HAMMER_SLOT]);
            this.inventory[HAMMER_SLOT] = null;
            this.syncState();
        }
    }

    private static boolean sameItem(ItemStack a, ItemStack b) {
        return a != null && b != null && a.isItemEqual(b) && ItemStack.areItemStackTagsEqual(a, b);
    }

    private void resetAutomaticProgress() {
        this.processTicks = 0;
        this.workInput = null;
        this.automaticActive = false;
    }

    private void setAutomaticActive(boolean active) {
        if (this.automaticActive == active) return;
        this.automaticActive = active;
        this.syncState();
    }

    @Override public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override public void readNBTFromPacket(NBTTagCompound tag) {
        this.readFromNBT(tag);
        if (this.worldObj != null) this.worldObj.markBlockRangeForRenderUpdate(
                this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord, this.zCoord);
    }

    @Override public void onInventoryChanged() {
        super.onInventoryChanged();
        if (this.worldObj != null && !this.worldObj.isRemote) this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override public int getSizeInventory() { return 3; }
    @Override public ItemStack getStackInSlot(int slot) { return this.inventory[slot]; }
    @Override public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = this.inventory[slot];
        if (stack == null || count <= 0) return null;
        ItemStack taken = stack.splitStack(Math.min(count, stack.stackSize));
        if (stack.stackSize <= 0) this.inventory[slot] = null;
        if (slot == HAMMER_SLOT || slot == INPUT_SLOT && this.inventory[slot] == null) this.resetAutomaticProgress();
        this.onInventoryChanged();
        return taken;
    }
    @Override public ItemStack getStackInSlotOnClosing(int slot) {
        return this.decrStackSize(slot, Integer.MAX_VALUE);
    }
    @Override public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == HAMMER_SLOT || slot == INPUT_SLOT && !sameItem(this.inventory[slot], stack)) this.resetAutomaticProgress();
        this.inventory[slot] = stack;
        if (stack != null) stack.stackSize = Math.min(stack.stackSize, Math.min(stack.getMaxStackSize(), slot == HAMMER_SLOT ? 1 : 64));
        this.onInventoryChanged();
    }
    @Override public String getInvName() { return "container.ifhyAutoAnvil"; }
    @Override public boolean isInvNameLocalized() { return false; }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public boolean isUseableByPlayer(EntityPlayer player) {
        return this.isAutomatic() && this.worldObj != null && !this.isInvalid()
                && this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64;
    }
    @Override public void openChest() {}
    @Override public void closeChest() {}
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (!this.isAutomatic() || stack == null || stack.stackSize <= 0) return false;
        if (slot == HAMMER_SLOT) return this.getUpgrade().needsHammer() && stack.getItem() instanceof ItemHammer;
        if (slot != INPUT_SLOT) return false;
        ItemStack probe = stack.copy();
        probe.stackSize = probe.getMaxStackSize();
        return HammerCraftingManager.instance.getRecipe(probe) != null;
    }
    @Override public int[] getSlotsForFace(int side) {
        return !this.isAutomatic() ? new int[0] : side == 0 ? new int[]{OUTPUT_SLOT}
                : this.getUpgrade().needsHammer() ? new int[]{HAMMER_SLOT, INPUT_SLOT} : new int[]{INPUT_SLOT};
    }
    @Override public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return side != 0 && this.isItemValidForSlot(slot, stack);
    }
    @Override public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return this.isAutomatic() && slot == OUTPUT_SLOT && side == 0;
    }

    public int insertFromHopper(ItemStack offered, int limit) {
        for (int slot = HAMMER_SLOT; slot <= INPUT_SLOT; ++slot) {
            if (!this.isItemValidForSlot(slot, offered)) continue;
            ItemStack current = this.inventory[slot];
            if (current != null && !sameItem(current, offered)) continue;
            int room = Math.min(offered.getMaxStackSize(), slot == HAMMER_SLOT ? 1 : 64) - (current == null ? 0 : current.stackSize);
            int count = Math.min(room, Math.min(limit, offered.stackSize));
            if (count <= 0) continue;
            ItemStack replacement = offered.copy();
            replacement.stackSize = count + (current == null ? 0 : current.stackSize);
            this.setInventorySlotContents(slot, replacement);
            return count;
        }
        return 0;
    }
}
