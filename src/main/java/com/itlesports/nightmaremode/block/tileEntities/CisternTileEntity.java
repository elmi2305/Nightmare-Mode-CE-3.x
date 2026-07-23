package com.itlesports.nightmaremode.block.tileEntities;

import btw.block.BTWBlocks;
import btw.block.blocks.CampfireBlock;
import api.block.TileEntityDataPacketHandler;
import com.itlesports.nightmaremode.crafting.manager.CisternRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.*;

import java.util.List;

public class CisternTileEntity extends TileEntity implements IInventory, TileEntityDataPacketHandler {
    public static final int FLUID_ANY = -1;
    public static final int FLUID_EMPTY = 0;
    public static final int FLUID_WATER = 1;
    public static final int FLUID_BRINE = 2;
    public static final int FLUID_SLURRY = 3;
    public static final int FLUID_ACIDIC_WASH = 4;
    public static final int FLUID_LAVA = 5;

    public static final int FIRST_INPUT_SLOT = 0;
    public static final int LAST_INPUT_SLOT = 1;
    public static final int FIRST_OUTPUT_SLOT = 2;
    public static final int LAST_OUTPUT_SLOT = 5;
    private static final int SLOT_COUNT = 6;

    private final ItemStack[] contents = new ItemStack[SLOT_COUNT];
    private int fluid = FLUID_EMPTY;
    private int heatLevel;
    private int processingTime;
    private int stirProgress;
    private int ticksExisted;

    private CisternRecipe currentRecipe;

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        this.ticksExisted++;
        this.absorbItemEntitiesAbove();
        if(this.stirProgress > 0 && this.ticksExisted % 64 == 0){
            this.stirProgress--;
        }
        this.heatLevel = this.calculateHeatLevel();
        CisternRecipe recipe = CisternRecipeManager.instance.getMatchingRecipe(this.contents, this.fluid, this.heatLevel, this.stirProgress);
        if (recipe == null || !this.canAcceptOutputs(recipe)) {
            this.currentRecipe = null;
            this.processingTime = 0;
            this.syncState();
            return;
        }

        if (this.currentRecipe != recipe) {
            this.currentRecipe = recipe;
            this.processingTime = 0;
        }

        ++this.processingTime;
        EntityPlayer nearbyPlayer = this.worldObj.getClosestPlayer(
                this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D, 8.0D);
        if (nearbyPlayer != null && this.worldObj.rand.nextFloat() < SkillHandler.getPlayerData(nearbyPlayer).cisternSpeedBonus) {
            ++this.processingTime;
        }

        if (this.processingTime >= recipe.getDuration()) {
            this.finishRecipe(recipe);
        }

        if (this.worldObj.getTotalWorldTime() % 20L == 0L) {
            this.syncState();
        }
    }

    public boolean addFluid(int fluid) {
        if (this.fluid != FLUID_EMPTY && this.fluid != fluid) {
            return false;
        }
        this.fluid = fluid;
        this.processingTime = 0;
        this.syncState();
        return true;
    }

    public int drainFluid() {
        int drained = this.fluid;
        this.fluid = FLUID_EMPTY;
        this.processingTime = 0;
        this.syncState();
        return drained;
    }

    public void stir(EntityPlayer p) {
        this.stirProgress = Math.min(this.stirProgress + 1, 64);
        p.addExhaustion(0.3f);
        this.syncState();
    }

    public boolean insertInput(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        ItemStack one = stack.copy();
        one.stackSize = 1;

        return this.insertInputStack(one) > 0;
    }

    /**
     * Inserts as much of the supplied stack as the two input slots can hold.
     * The source stack is never mutated; callers can use the returned amount to
     * preserve a partially accepted EntityItem stack.
     */
    private int insertInputStack(ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return 0;
        }

        ItemStack remaining = stack.copy();
        int inserted = 0;

        for (int i = FIRST_INPUT_SLOT; i <= LAST_INPUT_SLOT; ++i) {
            ItemStack input = this.contents[i];
            if (input != null && input.isItemEqual(remaining) && ItemStack.areItemStackTagsEqual(input, remaining)) {
                int maxStackSize = Math.min(this.getInventoryStackLimit(), input.getMaxStackSize());
                int amount = Math.min(remaining.stackSize, maxStackSize - input.stackSize);
                if (amount > 0) {
                    input.stackSize += amount;
                    remaining.stackSize -= amount;
                    inserted += amount;
                }
            }
        }

        for (int i = FIRST_INPUT_SLOT; i <= LAST_INPUT_SLOT && remaining.stackSize > 0; ++i) {
            if (this.contents[i] == null) {
                int amount = Math.min(remaining.stackSize, Math.min(this.getInventoryStackLimit(), remaining.getMaxStackSize()));
                ItemStack input = remaining.copy();
                input.stackSize = amount;
                this.contents[i] = input;
                remaining.stackSize -= amount;
                inserted += amount;
            }
        }

        if (inserted > 0) {
            this.processingTime = 0;
            this.onInventoryChanged();
        }
        return inserted;
    }

    /**
     * Matches a hopper's collection zone: items crossing the cistern opening
     * are accepted into input slots only. Full or incompatible stacks remain
     * in the world, allowing dispensers to retry once space becomes available.
     */
    private void absorbItemEntitiesAbove() {
        AxisAlignedBB collectionZone = AxisAlignedBB.getAABBPool().getAABB(
                this.xCoord, this.yCoord + 1.0D, this.zCoord,
                this.xCoord + 1.0D, this.yCoord + 1.05D, this.zCoord + 1.0D);
        List entities = this.worldObj.getEntitiesWithinAABB(EntityItem.class, collectionZone);

        for (Object object : entities) {
            EntityItem itemEntity = (EntityItem) object;
            if (itemEntity.isDead) {
                continue;
            }

            ItemStack entityStack = itemEntity.getEntityItem();
            int inserted = this.insertInputStack(entityStack);
            if (inserted <= 0) {
                continue;
            }

            entityStack.stackSize -= inserted;
            if (entityStack.stackSize <= 0) {
                itemEntity.setDead();
            } else {
                itemEntity.setEntityItemStack(entityStack);
            }
            this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
                    "random.pop", 0.2F, 1.0F);
        }
    }

    public ItemStack removeFirstOutput() {
        for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; ++i) {
            if (this.contents[i] != null) {
                ItemStack stack = this.contents[i];
                this.contents[i] = null;
                this.onInventoryChanged();
                return stack;
            }
        }
        return null;
    }

    public int getFluid() {
        return this.fluid;
    }

    public int getHeatLevel() {
        return this.heatLevel;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    public int getStirProgress() {
        return this.stirProgress;
    }

    public int getCurrentRecipeDuration() {
        return this.currentRecipe == null ? 0 : this.currentRecipe.getDuration();
    }

    public boolean hasOutputs() {
        for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT; ++i) {
            if (this.contents[i] != null) {
                return true;
            }
        }
        return false;
    }

    public static String getFluidDisplayName(int fluid) {
        switch (fluid) {
            case FLUID_WATER:
                return "Water";
            case FLUID_BRINE:
                return "Brine";
            case FLUID_SLURRY:
                return "Slurry";
            case FLUID_ACIDIC_WASH:
                return "Acidic Wash";
            case FLUID_LAVA:
                return "Lava";
            case FLUID_EMPTY:
            default:
                return "Empty";
        }
    }

    public static int getFluidTint(int fluid) {
        switch (fluid) {
            case FLUID_BRINE:
                return 0x9DBD9A;
            case FLUID_SLURRY:
                return 0x7D7564;
            case FLUID_ACIDIC_WASH:
                return 0x96D66B;
            case FLUID_LAVA:
                return 0xFF6600;
            case FLUID_WATER:
            default:
                return 0x3F76E4;
        }
    }

    private void finishRecipe(CisternRecipe recipe) {
        recipe.consumeInputs(this.contents);
        this.insertOutputs(recipe.getOutputs(this.worldObj.rand));
        this.fluid = recipe.consumesFluid() ? FLUID_EMPTY : recipe.getResultingFluid(this.fluid);
        this.processingTime = 0;
        if (this.stirProgress > 0) {
            this.stirProgress /= 4;
        }
        this.currentRecipe = null;
        this.onInventoryChanged();
    }

    private boolean canAcceptOutputs(CisternRecipe recipe) {
        ItemStack[] simulated = new ItemStack[SLOT_COUNT];
        for (int i = 0; i < SLOT_COUNT; ++i) {
            simulated[i] = this.contents[i] == null ? null : this.contents[i].copy();
        }
        return this.insertOutputsInto(simulated, recipe.getPotentialOutputs(), false);
    }

    private void insertOutputs(ItemStack[] outputs) {
        this.insertOutputsInto(this.contents, outputs, true);
    }

    private boolean insertOutputsInto(ItemStack[] target, ItemStack[] outputs, boolean mutate) {
        for (ItemStack output : outputs) {
            ItemStack remaining = output.copy();
            for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT && remaining.stackSize > 0; ++i) {
                ItemStack stack = target[i];
                if (stack != null && stack.isItemEqual(remaining) && ItemStack.areItemStackTagsEqual(stack, remaining)) {
                    int max = Math.min(this.getInventoryStackLimit(), stack.getMaxStackSize());
                    int move = Math.min(remaining.stackSize, max - stack.stackSize);
                    if (move > 0) {
                        if (mutate) {
                            stack.stackSize += move;
                        } else {
                            target[i].stackSize += move;
                        }
                        remaining.stackSize -= move;
                    }
                }
            }
            for (int i = FIRST_OUTPUT_SLOT; i <= LAST_OUTPUT_SLOT && remaining.stackSize > 0; ++i) {
                if (target[i] == null) {
                    target[i] = remaining.copy();
                    remaining.stackSize = 0;
                }
            }
            if (remaining.stackSize > 0) {
                return false;
            }
        }
        return true;
    }

    private int calculateHeatLevel() {
        int heat = 0;
        for (int x = this.xCoord - 1; x <= this.xCoord + 1; ++x) {
            for (int y = this.yCoord - 1; y <= this.yCoord; ++y) {
                for (int z = this.zCoord - 1; z <= this.zCoord + 1; ++z) {
                    if (x == this.xCoord && y == this.yCoord && z == this.zCoord) {
                        continue;
                    }
                    heat = Math.max(heat, this.getHeatForBlock(x, y, z));
                }
            }
        }
        return heat;
    }

    private int getHeatForBlock(int x, int y, int z) {
        int blockID = this.worldObj.getBlockId(x, y, z);
        if (blockID == BTWBlocks.stokedFire.blockID) {
            return 3;
        }
        if (blockID == Block.lavaStill.blockID || blockID == Block.lavaMoving.blockID) {
            return 3;
        }
        if (blockID == Block.fire.blockID) {
            return 2;
        }
        Block block = Block.blocksList[blockID];
        if (block instanceof CampfireBlock) {
            return ((CampfireBlock) block).fireLevel;
        }
        return 0;
    }

    private void syncState() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, Math.min(this.fluid, 3), 2);
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public int getSizeInventory() {
        return SLOT_COUNT;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.contents[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = this.contents[slot];
        if (stack == null) {
            return null;
        }
        if (stack.stackSize <= count) {
            this.contents[slot] = null;
            this.onInventoryChanged();
            return stack;
        }
        ItemStack split = stack.splitStack(count);
        if (stack.stackSize <= 0) {
            this.contents[slot] = null;
        }
        this.onInventoryChanged();
        return split;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = this.contents[slot];
        this.contents[slot] = null;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.contents[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }
        this.onInventoryChanged();
    }

    @Override
    public String getInvName() {
        return "container.cistern";
    }

    @Override
    public boolean isInvNameLocalized() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot >= FIRST_INPUT_SLOT && slot <= LAST_INPUT_SLOT;
    }

    @Override
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.fluid = tag.getInteger("Fluid");
        this.heatLevel = tag.getInteger("Heat");
        this.processingTime = tag.getInteger("Process");
        this.stirProgress = tag.getInteger("Stir");
        this.currentRecipe = null;
        for (int i = 0; i < this.contents.length; ++i) {
            this.contents[i] = null;
        }
        NBTTagList list = tag.getTagList("Items");
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound itemTag = (NBTTagCompound) list.tagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot < this.contents.length) {
                this.contents[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Fluid", this.fluid);
        tag.setInteger("Heat", this.heatLevel);
        tag.setInteger("Process", this.processingTime);
        tag.setInteger("Stir", this.stirProgress);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.contents.length; ++i) {
            if (this.contents[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                this.contents[i].writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        tag.setTag("Items", list);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writePacketNBT(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override
    public void onInventoryChanged() {
        super.onInventoryChanged();
        this.syncState();
    }

    @Override
    public void readNBTFromPacket(NBTTagCompound tag) {
        this.fluid = tag.getInteger("Fluid");
        this.heatLevel = tag.getInteger("Heat");
        this.processingTime = tag.getInteger("Process");
        this.stirProgress = tag.getInteger("Stir");
    }

    private void writePacketNBT(NBTTagCompound tag) {
        tag.setInteger("Fluid", this.fluid);
        tag.setInteger("Heat", this.heatLevel);
        tag.setInteger("Process", this.processingTime);
        tag.setInteger("Stir", this.stirProgress);
    }
}
