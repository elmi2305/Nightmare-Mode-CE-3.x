package com.itlesports.nightmaremode.block.tileEntities;

import api.block.TileEntityDataPacketHandler;
import api.block.util.MechPowerUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Facing;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.TileEntity;

/** Mechanically enabled, signal-configured automatic cistern stirring. */
public class CisternStirrerTileEntity extends TileEntity implements TileEntityDataPacketHandler {
    private static final int WORK_INTERVAL = 20;
    private static final int MAX_TARGET = 255;

    private int targetStir;
    private boolean mechanicallyPowered;
    private int ticksExisted;

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        ++this.ticksExisted;
        if (this.ticksExisted == 1 || this.ticksExisted % WORK_INTERVAL == 0) {
            this.refreshConfiguration();
        }
        if (this.ticksExisted % WORK_INTERVAL != 0 || !this.mechanicallyPowered || this.targetStir <= 0) {
            return;
        }

        CisternTileEntity cistern = this.getTargetCistern();
        if (cistern != null && cistern.getStirProgress() < this.targetStir) {
            cistern.stirAutomatically();
        }
    }

    public void refreshConfiguration() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }
        int updatedTarget = this.calculateTargetStir();
        boolean updatedPower = MechPowerUtils.isBlockPoweredByAxleToSide(
                this.worldObj, this.xCoord, this.yCoord, this.zCoord, 1);
        if (updatedTarget != this.targetStir || updatedPower != this.mechanicallyPowered) {
            this.targetStir = updatedTarget;
            this.mechanicallyPowered = updatedPower;
            this.onInventoryChanged();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    private int calculateTargetStir() {
        int target = 0;
        for (int side = 2; side <= 5; ++side) {
            int x = this.xCoord + Facing.offsetsXForSide[side];
            int z = this.zCoord + Facing.offsetsZForSide[side];
            if (this.worldObj.getBlockId(x, this.yCoord, z) == NMBlocks.netherProgressionGems.blockID) {
                int metadata = this.worldObj.getBlockMetadata(x, this.yCoord, z);
                if (metadata == NMBlocks.META_RED_GEM) target += 25;
                else if (metadata == NMBlocks.META_PURPLE_GEM) target += 50;
                else if (metadata == NMBlocks.META_BLACK_GEM) target += 100;
            } else {
                target += this.worldObj.getIndirectPowerLevelTo(x, this.yCoord, z, side);
            }
        }
        return Math.min(target, MAX_TARGET);
    }

    public CisternTileEntity getTargetCistern() {
        if (this.worldObj == null) {
            return null;
        }
        TileEntity target = this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
        if (target instanceof CisternTileEntity cistern) {
            return cistern;
        }
        if (target instanceof CisternInterfaceTileEntity cisternInterface) {
            return cisternInterface.getCistern();
        }
        return null;
    }

    public int getTargetStir() {
        return this.targetStir;
    }

    public boolean isMechanicallyPowered() {
        return this.mechanicallyPowered;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.targetStir = Math.min(MAX_TARGET, Math.max(0, tag.getInteger("TargetStir")));
        this.mechanicallyPowered = tag.getBoolean("Powered");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.writeState(tag);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeState(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override
    public void readNBTFromPacket(NBTTagCompound tag) {
        this.targetStir = tag.getInteger("TargetStir");
        this.mechanicallyPowered = tag.getBoolean("Powered");
        this.worldObj.markBlockRangeForRenderUpdate(
                this.xCoord, this.yCoord, this.zCoord,
                this.xCoord, this.yCoord, this.zCoord);
    }

    private void writeState(NBTTagCompound tag) {
        tag.setInteger("TargetStir", this.targetStir);
        tag.setBoolean("Powered", this.mechanicallyPowered);
    }
}
