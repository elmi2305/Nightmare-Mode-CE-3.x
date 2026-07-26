package com.itlesports.nightmaremode.block.tileEntities;

import api.block.TileEntityDataPacketHandler;
import api.item.util.ItemUtils;
import net.minecraft.src.*;

public class HellforgeTileEntity extends TileEntityFurnace implements TileEntityDataPacketHandler {

    public static final int FUEL_LIMIT = 14200;
    private ItemStack cookStack = null;
    private int unlitFuelBurnTime = 0;
    private int visualFuelLevel = 0;
    private boolean lightOnNextUpdate = false;


    public void updateEntity() {
        boolean bWasBurning = this.furnaceBurnTime > 0;
        boolean bInventoryChanged = false;
        if (this.furnaceBurnTime > 0) {
            --this.furnaceBurnTime;
        }

        if (!this.worldObj.isRemote) {
            boolean hasLavaAccess = this.hasHorizontalFlowingLavaNeighbor();
            if (!bWasBurning && this.unlitFuelBurnTime > 0 && hasLavaAccess) {
                this.lightOnNextUpdate = true;
            }
            if (bWasBurning || this.lightOnNextUpdate) {
                this.furnaceBurnTime += this.unlitFuelBurnTime;
                this.unlitFuelBurnTime = 0;
                this.lightOnNextUpdate = false;
            }
            if (this.isBurning() && hasLavaAccess && this.canSmelt()) {
                ++this.furnaceCookTime;
                if (this.furnaceCookTime >= this.getCookTimeForCurrentItem()) {
                    this.furnaceCookTime = 0;
                    this.smeltItem();
                    bInventoryChanged = true;
                }
            } else {
                this.furnaceCookTime = 0;
            }

            BlockFurnace furnaceBlock = (BlockFurnace) Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord)];
            if (bWasBurning != this.isBurning()) {
                bInventoryChanged = true;
                furnaceBlock.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord, false);
            }

            this.updateCookStack();
            this.updateVisualFuelLevel();
        }

        if (bInventoryChanged) {
            this.onInventoryChanged();
        }

    }

    private boolean hasHorizontalFlowingLavaNeighbor() {
        return this.isFlowingLava(this.xCoord - 1, this.yCoord, this.zCoord)
                || this.isFlowingLava(this.xCoord + 1, this.yCoord, this.zCoord)
                || this.isFlowingLava(this.xCoord, this.yCoord, this.zCoord - 1)
                || this.isFlowingLava(this.xCoord, this.yCoord, this.zCoord + 1);
    }

    private boolean isFlowingLava(int x, int y, int z) {
        int blockId = this.worldObj.getBlockId(x, y, z);
        return blockId == Block.lavaMoving.blockID || blockId == Block.lavaStill.blockID;
    }

    public String getInvName() {
        return "container.nmHellForge";
    }

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("fcUnlitFuel")) {
            this.unlitFuelBurnTime = tag.getInteger("fcUnlitFuel");
        }

        if (tag.hasKey("fcVisualFuel")) {
            this.visualFuelLevel = tag.getByte("fcVisualFuel");
        }

    }

    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("fcUnlitFuel", this.unlitFuelBurnTime);
        tag.setByte("fcVisualFuel", (byte)this.visualFuelLevel);
    }
    public int getItemBurnTime(ItemStack stack) {
        return super.getItemBurnTime(stack) * 4;
    }


    protected int getCookTimeForCurrentItem() {
        return super.getCookTimeForCurrentItem() * 4;
    }

    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        if (this.cookStack != null) {
            NBTTagCompound cookTag = new NBTTagCompound();
            this.cookStack.writeToNBT(cookTag);
            tag.setCompoundTag("x", cookTag);
        }

        tag.setByte("y", (byte)this.visualFuelLevel);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    public void readNBTFromPacket(NBTTagCompound tag) {
        NBTTagCompound cookTag = tag.getCompoundTag("x");
        if (cookTag != null) {
            this.cookStack = ItemStack.loadItemStackFromNBT(cookTag);
        }

        this.visualFuelLevel = tag.getByte("y");
        this.worldObj.markBlockRangeForRenderUpdate(this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord, this.zCoord);
    }

    public boolean attemptToLight() {
        if (this.unlitFuelBurnTime > 0) {
            this.lightOnNextUpdate = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean hasValidFuel() {
        return this.unlitFuelBurnTime > 0;
    }

    private void updateCookStack() {
        ItemStack newCookStack = this.furnaceItemStacks[0];
        if (newCookStack == null) {
            newCookStack = this.furnaceItemStacks[2];
            if (newCookStack == null) {
                newCookStack = this.furnaceItemStacks[1];
            }
        }

        if (!ItemStack.areItemStacksEqual(newCookStack, this.cookStack)) {
            this.setCookStack(newCookStack);
        }

    }

    public void setCookStack(ItemStack stack) {
        if (stack != null) {
            this.cookStack = stack.copy();
        } else {
            this.cookStack = null;
        }

        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public ItemStack getCookStack() {
        return this.cookStack;
    }

    public void givePlayerCookStack(EntityPlayer player, int iFacing) {
        if (!this.worldObj.isRemote) {
            this.ejectAllNotCookStacksToFacing(player, iFacing);
        }

        ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, this.cookStack, this.xCoord, this.yCoord, this.zCoord, iFacing);
        this.furnaceItemStacks[0] = null;
        this.furnaceItemStacks[1] = null;
        this.furnaceItemStacks[2] = null;
        this.setCookStack(null);
        this.onInventoryChanged();
    }

    private void ejectAllNotCookStacksToFacing(EntityPlayer player, int iFacing) {
        if (this.furnaceItemStacks[0] != null && !ItemStack.areItemStacksEqual(this.furnaceItemStacks[0], this.cookStack)) {
            ItemUtils.ejectStackFromBlockTowardsFacing(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.furnaceItemStacks[0], iFacing);
            this.furnaceItemStacks[0] = null;
        }

        if (this.furnaceItemStacks[1] != null && !ItemStack.areItemStacksEqual(this.furnaceItemStacks[1], this.cookStack)) {
            ItemUtils.ejectStackFromBlockTowardsFacing(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.furnaceItemStacks[1], iFacing);
            this.furnaceItemStacks[1] = null;
        }

        if (this.furnaceItemStacks[2] != null && !ItemStack.areItemStacksEqual(this.furnaceItemStacks[2], this.cookStack)) {
            ItemUtils.ejectStackFromBlockTowardsFacing(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.furnaceItemStacks[2], iFacing);
            this.furnaceItemStacks[2] = null;
        }

        this.onInventoryChanged();
    }

    public void addCookStack(ItemStack stack) {
        this.furnaceItemStacks[0] = stack;
        this.onInventoryChanged();
    }

    public int attemptToAddFuel(ItemStack stack) {
        int iTotalBurnTime = this.unlitFuelBurnTime + this.furnaceBurnTime;
        int iDeltaBurnTime = FUEL_LIMIT - iTotalBurnTime;
        int iNumItemsBurned = 0;
        if (iDeltaBurnTime > 0) {
            iNumItemsBurned = iDeltaBurnTime / this.getItemBurnTime(stack);
            if (iNumItemsBurned == 0 && this.getVisualFuelLevel() <= 2) {
                iNumItemsBurned = 1;
            }

            if (iNumItemsBurned > 0) {
                if (iNumItemsBurned > stack.stackSize) {
                    iNumItemsBurned = stack.stackSize;
                }

                this.unlitFuelBurnTime += this.getItemBurnTime(stack) * iNumItemsBurned;
                this.onInventoryChanged();
            }
        }

        return iNumItemsBurned;
    }

    private void updateVisualFuelLevel() {
        int iTotalBurnTime = this.unlitFuelBurnTime + this.furnaceBurnTime;
        int iNewFuelLevel = 0;
        if (iTotalBurnTime > 0) {
            if (iTotalBurnTime < 400) {
                iNewFuelLevel = 1;
            } else {
                iNewFuelLevel = iTotalBurnTime / 1600 + 2;
            }
        }

        this.setVisualFuelLevel(iNewFuelLevel);
    }

    public int getVisualFuelLevel() {
        return this.visualFuelLevel;
    }

    public void setVisualFuelLevel(int iLevel) {
        if (this.visualFuelLevel != iLevel) {
            this.visualFuelLevel = iLevel;
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }

    }

    public static int calcRedstoneFromOven(HellforgeTileEntity tileEntity) {
        boolean hasInventory = tileEntity.getCookStack() != null || tileEntity.furnaceItemStacks[0] != null;
        return hasInventory ? 15 : 0;
    }

    @Override
    public int[] getSlotsForFace(int i) {
        return new int[0];
    }
}
