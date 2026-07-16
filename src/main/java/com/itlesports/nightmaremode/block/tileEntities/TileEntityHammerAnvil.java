package com.itlesports.nightmaremode.block.tileEntities;

import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import com.itlesports.nightmaremode.item.items.ItemHammer;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class TileEntityHammerAnvil extends TileEntity {
    private static final int SOUND_INTERVAL_TICKS = 2;
    private static final int MIN_FOOD_LEVEL = 6;
    private static final float EXHAUSTION_PER_HIT = 0.2F;

    protected int maxUses = -1;
    protected int usesRemaining = -1;
    private int queuedHammerSounds;
    private int soundDelay;
    private boolean breakWhenSoundsFinish;

    public TileEntityHammerAnvil() {
    }

    protected TileEntityHammerAnvil(int maxUses) {
        this.maxUses = maxUses;
        this.usesRemaining = maxUses;
    }

    public boolean tryStartHammerOperation(EntityPlayer player) {
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

        this.consumeInputAndDamageHammer(player, input, recipe.getInput(), hammerSlot, hits);
        recipe.chargePlayerExperience(player);
        player.addExhaustion(EXHAUSTION_PER_HIT * hits);
        this.deliverOutputs(player, recipe);
        this.spendHits(hits);
        player.inventoryContainer.detectAndSendChanges();
        player.inventory.onInventoryChanged();
        return true;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
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
        return !this.isBusy() && (this.maxUses < 0 || (!this.breakWhenSoundsFinish && this.usesRemaining >= hits));
    }

    public void spendHits(int hits) {
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
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("MaxUses", this.maxUses);
        tag.setInteger("UsesRemaining", this.usesRemaining);
        tag.setInteger("QueuedHammerSounds", this.queuedHammerSounds);
        tag.setInteger("SoundDelay", this.soundDelay);
        tag.setBoolean("BreakWhenSoundsFinish", this.breakWhenSoundsFinish);
    }
}
