package com.itlesports.nightmaremode.block.tileEntities;

import btw.block.tileentity.OvenTileEntity;
import net.minecraft.src.Block;

public class NetherOvenTileEntity extends OvenTileEntity {
    @Override
    public void updateEntity() {
        if (this.worldObj != null && !this.worldObj.isRemote && this.hasAdjacentLava() && this.hasValidFuel()) {
            this.attemptToLight();
        }
        super.updateEntity();
    }

    private boolean hasAdjacentLava() {
        return this.isLava(this.xCoord - 1, this.yCoord, this.zCoord)
                || this.isLava(this.xCoord + 1, this.yCoord, this.zCoord)
                || this.isLava(this.xCoord, this.yCoord, this.zCoord - 1)
                || this.isLava(this.xCoord, this.yCoord, this.zCoord + 1);
    }

    private boolean isLava(int x, int y, int z) {
        int id = this.worldObj.getBlockId(x, y, z);
        return id == Block.lavaStill.blockID || id == Block.lavaMoving.blockID;
    }
}
