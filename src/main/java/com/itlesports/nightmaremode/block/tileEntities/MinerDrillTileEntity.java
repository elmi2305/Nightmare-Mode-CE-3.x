package com.itlesports.nightmaremode.block.tileEntities;

import api.item.util.ItemUtils;
import com.itlesports.nightmaremode.block.blocks.BlockOreNode;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;

public class MinerDrillTileEntity extends TileEntity implements IInventory {
    private ItemStack fuelStack;
    private int fuelTicks;
    private int processingTicks;
    private int speedUpgradeLevel;
    private int fuelUpgradeLevel;

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        int[] target = this.findNodeTarget();
        if (target == null) {
            this.processingTicks = 0;
            this.setActive(false);
            return;
        }

        if (this.fuelTicks <= 0 && !this.consumeCoal()) {
            this.processingTicks = 0;
            this.setActive(false);
            return;
        }

        this.setActive(true);
        --this.fuelTicks;
        if (++this.processingTicks < this.getProcessingTicksPerItem()) {
            return;
        }

        this.processingTicks = 0;
        BlockOreNode node = (BlockOreNode)net.minecraft.src.Block.blocksList[this.worldObj.getBlockId(target[0], target[1], target[2])];
        ItemStack output = node.mineNodeByMachine(this.worldObj, target[0], target[1], target[2]);
        if (output != null && !this.insertIntoAdjacentInventory(output)) {
            ItemUtils.ejectStackFromBlockTowardsFacing(this.worldObj, this.xCoord, this.yCoord, this.zCoord, output, 1);
        }
        this.onInventoryChanged();
    }

    protected int getBaseProcessingTicks() {
        return 200;
    }

    protected int getBaseFuelTicksPerCoal() {
        return 1600;
    }

    public int getProcessingTicksPerItem() {
        return Math.max(20, this.getBaseProcessingTicks() * 4 / (4 + this.speedUpgradeLevel));
    }

    public int getFuelTicksPerCoal() {
        return this.getBaseFuelTicksPerCoal() + this.fuelUpgradeLevel * 400;
    }

    public void setUpgradeLevels(int speedLevel, int fuelLevel) {
        this.speedUpgradeLevel = Math.max(0, speedLevel);
        this.fuelUpgradeLevel = Math.max(0, fuelLevel);
        this.onInventoryChanged();
    }

    public int getProcessingProgress() {
        return this.processingTicks;
    }

    public int getFuelTicks() {
        return this.fuelTicks;
    }

    private boolean consumeCoal() {
        if (this.fuelStack == null || this.fuelStack.itemID != Item.coal.itemID || this.fuelStack.stackSize <= 0) {
            return false;
        }
        --this.fuelStack.stackSize;
        if (this.fuelStack.stackSize <= 0) {
            this.fuelStack = null;
        }
        this.fuelTicks = this.getFuelTicksPerCoal();
        this.onInventoryChanged();
        return true;
    }

    private int[] findNodeTarget() {
        int facing = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) & 7;
        int targetX = this.xCoord;
        int targetZ = this.zCoord;
        if (facing == 2) {
            --targetZ;
        } else if (facing == 3) {
            ++targetZ;
        } else if (facing == 4) {
            --targetX;
        } else if (facing == 5) {
            ++targetX;
        }
        if (this.worldObj.getBlockId(targetX, this.yCoord, targetZ) > 0
                && net.minecraft.src.Block.blocksList[this.worldObj.getBlockId(targetX, this.yCoord, targetZ)] instanceof BlockOreNode) {
            return new int[]{targetX, this.yCoord, targetZ};
        }
        if (this.worldObj.getBlockId(this.xCoord, this.yCoord - 1, this.zCoord) > 0
                && net.minecraft.src.Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord - 1, this.zCoord)] instanceof BlockOreNode) {
            return new int[]{this.xCoord, this.yCoord - 1, this.zCoord};
        }
        return null;
    }

    private void setActive(boolean active) {
        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        int updated = active ? metadata | 8 : metadata & 7;
        if (updated != metadata) {
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, updated, 3);
        }
    }

    private boolean insertIntoAdjacentInventory(ItemStack stack) {
        int[][] offsets = {{0, 1, 0}, {0, -1, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}};
        for (int[] offset : offsets) {
            TileEntity tile = this.worldObj.getBlockTileEntity(
                    this.xCoord + offset[0], this.yCoord + offset[1], this.zCoord + offset[2]);
            if (tile instanceof IInventory inventory && tile != this && this.insertIntoInventory(inventory, stack)) {
                return true;
            }
        }
        return false;
    }

    private boolean insertIntoInventory(IInventory inventory, ItemStack stack) {
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack existing = inventory.getStackInSlot(slot);
            if (existing != null && existing.isItemEqual(stack)
                    && ItemStack.areItemStackTagsEqual(existing, stack)
                    && existing.stackSize < Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit())) {
                ++existing.stackSize;
                inventory.onInventoryChanged();
                return true;
            }
        }
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            if (inventory.getStackInSlot(slot) == null && inventory.isItemValidForSlot(slot, stack)) {
                inventory.setInventorySlotContents(slot, stack.copy());
                inventory.onInventoryChanged();
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("FuelTicks", this.fuelTicks);
        tag.setInteger("ProcessingTicks", this.processingTicks);
        tag.setInteger("SpeedUpgrade", this.speedUpgradeLevel);
        tag.setInteger("FuelUpgrade", this.fuelUpgradeLevel);
        if (this.fuelStack != null) {
            NBTTagCompound itemTag = new NBTTagCompound();
            this.fuelStack.writeToNBT(itemTag);
            tag.setCompoundTag("FuelStack", itemTag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.fuelTicks = tag.getInteger("FuelTicks");
        this.processingTicks = tag.getInteger("ProcessingTicks");
        this.speedUpgradeLevel = tag.getInteger("SpeedUpgrade");
        this.fuelUpgradeLevel = tag.getInteger("FuelUpgrade");
        if (tag.hasKey("FuelStack")) {
            this.fuelStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("FuelStack"));
        }
    }

    @Override public int getSizeInventory() { return 1; }
    @Override public ItemStack getStackInSlot(int slot) { return this.fuelStack; }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (this.fuelStack == null) return null;
        if (this.fuelStack.stackSize <= count) {
            ItemStack result = this.fuelStack;
            this.fuelStack = null;
            return result;
        }
        ItemStack result = this.fuelStack.splitStack(count);
        if (this.fuelStack.stackSize <= 0) this.fuelStack = null;
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack result = this.fuelStack;
        this.fuelStack = null;
        return result;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        this.fuelStack = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) stack.stackSize = this.getInventoryStackLimit();
        this.onInventoryChanged();
    }

    @Override public String getInvName() { return "container.ifhyMinerDrill"; }
    @Override public boolean isInvNameLocalized() { return true; }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public boolean isUseableByPlayer(net.minecraft.src.EntityPlayer player) {
        return this.worldObj == null || this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }
    @Override public void openChest() {}
    @Override public void closeChest() {}
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) { return stack != null && stack.itemID == Item.coal.itemID; }
}
