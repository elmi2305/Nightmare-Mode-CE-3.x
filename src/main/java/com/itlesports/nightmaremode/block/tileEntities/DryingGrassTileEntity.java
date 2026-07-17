package com.itlesports.nightmaremode.block.tileEntities;

import com.itlesports.nightmaremode.block.blocks.BlockDryingGrass;
import net.minecraft.src.Block;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class DryingGrassTileEntity extends TileEntity {
    private static final int TIME_TO_DRY = 2 * 20 * 60;

    private int dryCounter;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.dryCounter = tag.getInteger("DryCounter");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("DryCounter", this.dryCounter);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (this.worldObj.isRemote || this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) == BlockDryingGrass.META_DRIED) {
            return;
        }

        if (this.canDry()) {
            ++this.dryCounter;
            if (this.dryCounter >= TIME_TO_DRY && Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord)] instanceof BlockDryingGrass dryingGrass) {
                dryingGrass.onFinishedDrying(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }

    private boolean canDry() {
        int maxNaturalLight = this.worldObj.getBlockNaturalLightValueMaximum(this.xCoord, this.yCoord, this.zCoord);
        int currentNaturalLight = maxNaturalLight - this.worldObj.skylightSubtracted;
        if (currentNaturalLight < 15) {
            return false;
        }

        int blockAboveID = this.worldObj.getBlockId(this.xCoord, this.yCoord + 1, this.zCoord);
        Block blockAbove = Block.blocksList[blockAboveID];
        return blockAbove == null || !blockAbove.isGroundCover();
    }
}
