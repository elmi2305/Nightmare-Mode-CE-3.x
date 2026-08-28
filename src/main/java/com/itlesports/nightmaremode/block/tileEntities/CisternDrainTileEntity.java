package com.itlesports.nightmaremode.block.tileEntities;

import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import net.minecraft.src.Block;
import net.minecraft.src.TileEntity;

/** Executes the drain's downward ejection and upward flowing-fluid intake. */
public class CisternDrainTileEntity extends TileEntity {
    private int ticksExisted;

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote || ++this.ticksExisted % 10 != 0) {
            return;
        }

        CisternTileEntity cisternAbove = this.resolveCisternAt(this.yCoord + 1);
        if (cisternAbove != null && this.worldObj.isBlockGettingPowered(this.xCoord, this.yCoord, this.zCoord)) {
            int drainedFluid = cisternAbove.getFluid();
            if (cisternAbove.drainAndEjectContents(this.xCoord, this.yCoord, this.zCoord)) {
                ChunkPollutionManager.pollute(this.worldObj, this.xCoord, this.yCoord, this.zCoord,
                        this.pollutionForFluid(drainedFluid));
                this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.25D, this.zCoord + 0.5D,
                        "random.pop", 0.4F, 0.7F);
            }
        }

        CisternTileEntity cisternBelow = this.resolveCisternAt(this.yCoord - 1);
        if (cisternBelow == null || cisternBelow.getFluid() != CisternTileEntity.FLUID_EMPTY) {
            return;
        }
        int sourceId = this.worldObj.getBlockId(this.xCoord, this.yCoord + 1, this.zCoord);
        int fluid = sourceId == Block.waterMoving.blockID
                ? CisternTileEntity.FLUID_WATER
                : sourceId == Block.lavaMoving.blockID ? CisternTileEntity.FLUID_LAVA : CisternTileEntity.FLUID_EMPTY;
        if (fluid != CisternTileEntity.FLUID_EMPTY && cisternBelow.addFluid(fluid)) {
            this.worldObj.setBlockToAir(this.xCoord, this.yCoord + 1, this.zCoord);
            this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.75D, this.zCoord + 0.5D,
                    fluid == CisternTileEntity.FLUID_LAVA ? "liquid.lavapop" : "random.splash", 0.35F, 1.0F);
        }
    }

    private CisternTileEntity resolveCisternAt(int y) {
        TileEntity tile = this.worldObj.getBlockTileEntity(this.xCoord, y, this.zCoord);
        if (tile instanceof CisternTileEntity cistern) {
            return cistern;
        }
        if (tile instanceof CisternInterfaceTileEntity cisternInterface) {
            return cisternInterface.getCistern();
        }
        return null;
    }

    private float pollutionForFluid(int fluid) {
        return switch (fluid) {
            case CisternTileEntity.FLUID_WATER -> 2.0F;
            case CisternTileEntity.FLUID_BRINE -> 12.0F;
            case CisternTileEntity.FLUID_SLURRY -> 35.0F;
            case CisternTileEntity.FLUID_ACIDIC_WASH -> 50.0F;
            case CisternTileEntity.FLUID_LAVA -> 20.0F;
            default -> 0.0F;
        };
    }
}
