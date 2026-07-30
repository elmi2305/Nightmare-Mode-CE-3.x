package com.itlesports.nightmaremode.block.tileEntities;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class OreNodeTileEntity extends TileEntity {
    private int remainingCapacity;

    public void initializeCapacity() {
        if (this.remainingCapacity <= 0 && this.worldObj != null) {
            this.remainingCapacity = 200 + this.worldObj.rand.nextInt(801);
            this.onInventoryChanged();
        }
    }

    public int consumeOne() {
        this.initializeCapacity();
        if (this.remainingCapacity > 0) {
            --this.remainingCapacity;
            this.onInventoryChanged();
        }
        return this.remainingCapacity;
    }

    public int getRemainingCapacity() {
        return this.remainingCapacity;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.remainingCapacity = tag.getInteger("capacity");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("capacity", this.remainingCapacity);
    }
}
