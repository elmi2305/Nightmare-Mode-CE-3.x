package com.itlesports.nightmaremode.block.tileEntities;

import btw.block.tileentity.HopperTileEntity;
import com.itlesports.nightmaremode.util.interfaces.IChuteTransferFilter;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;

import java.util.List;

public class ChuteHopperTileEntity extends HopperTileEntity {
    private static IChuteTransferFilter transferFilter = IChuteTransferFilter.ALLOW_ALL;

    public static void setTransferFilter(IChuteTransferFilter filter) {
        transferFilter = filter == null ? IChuteTransferFilter.ALLOW_ALL : filter;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        boolean active = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
        this.mechanicalPowerIndicator = active ? 1 : 0;
        if (!active) {
            return;
        }

        IInventory target = this.findInventoryAt(this.yCoord - 1);
        if (target != null && this.moveFirstAvailableItem(this, target)) {
            return;
        }

        IInventory source = this.findInventoryAt(this.yCoord + 1);
        if (source != null) {
            this.moveFirstAvailableItem(source, this);
        }
    }

    private IInventory findInventoryAt(int y) {
        TileEntity tileEntity = this.worldObj.getBlockTileEntity(this.xCoord, y, this.zCoord);
        if (tileEntity instanceof IInventory inventory) {
            return inventory;
        }

        AxisAlignedBB bounds = AxisAlignedBB.getAABBPool().getAABB(
                this.xCoord, y, this.zCoord,
                this.xCoord + 1, y + 1, this.zCoord + 1);
        List carts = this.worldObj.getEntitiesWithinAABB(EntityMinecart.class, bounds);
        for (Object object : carts) {
            if (object instanceof Entity entity
                    && entity.boundingBox.intersectsWith(bounds)
                    && object instanceof IInventory inventory) {
                return inventory;
            }
        }
        return null;
    }

    private boolean moveFirstAvailableItem(IInventory source, IInventory target) {
        int sourceLimit = source == this ? 18 : source.getSizeInventory();
        int targetLimit = target == this ? 18 : target.getSizeInventory();

        for (int sourceSlot = 0; sourceSlot < sourceLimit; ++sourceSlot) {
            ItemStack sourceStack = source.getStackInSlot(sourceSlot);
            if (sourceStack == null || sourceStack.stackSize <= 0
                    || !transferFilter.canTransfer(sourceStack, source, target)) {
                continue;
            }

            ItemStack transferStack = sourceStack.copy();
            transferStack.stackSize = 1;
            if (!this.insertSingleItem(target, transferStack, targetLimit)) {
                continue;
            }

            source.decrStackSize(sourceSlot, 1);
            source.onInventoryChanged();
            target.onInventoryChanged();
            this.worldObj.playAuxSFX(2231, this.xCoord, this.yCoord, this.zCoord, 0);
            return true;
        }
        return false;
    }

    private boolean insertSingleItem(IInventory target, ItemStack transferStack, int targetLimit) {
        for (int targetSlot = 0; targetSlot < targetLimit; ++targetSlot) {
            ItemStack targetStack = target.getStackInSlot(targetSlot);
            if (targetStack != null
                    && targetStack.isItemEqual(transferStack)
                    && ItemStack.areItemStackTagsEqual(targetStack, transferStack)
                    && targetStack.stackSize < Math.min(targetStack.getMaxStackSize(), target.getInventoryStackLimit())
                    && target.isItemValidForSlot(targetSlot, transferStack)) {
                ++targetStack.stackSize;
                return true;
            }
        }

        for (int targetSlot = 0; targetSlot < targetLimit; ++targetSlot) {
            if (target.getStackInSlot(targetSlot) == null
                    && target.isItemValidForSlot(targetSlot, transferStack)) {
                target.setInventorySlotContents(targetSlot, transferStack);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canCurrentFilterProcessItem(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot != 18;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (slot == 18 && stack != null) {
            return;
        }
        super.setInventorySlotContents(slot, stack);
    }
}
