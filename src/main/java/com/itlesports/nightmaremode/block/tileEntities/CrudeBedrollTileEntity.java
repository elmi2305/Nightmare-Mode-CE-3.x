package com.itlesports.nightmaremode.block.tileEntities;

import com.itlesports.nightmaremode.block.blocks.BlockCrudeBedroll;
import net.minecraft.src.*;

public class CrudeBedrollTileEntity extends TileEntity {
    private int uses;
    private boolean occupiedLastTick;

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = Math.max(0, Math.min(uses, 3));
        this.onInventoryChanged();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.uses = tag.getInteger("Uses");
        this.occupiedLastTick = tag.getBoolean("OccupiedLastTick");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Uses", this.uses);
        tag.setBoolean("OccupiedLastTick", this.occupiedLastTick);
    }

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote
                || !(Block.blocksList[worldObj.getBlockId(xCoord, yCoord, zCoord)] instanceof BlockCrudeBedroll)) return;
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if (BlockBed.isBlockHeadOfBed(meta)) return;
        int facing = BlockBed.getDirection(meta);
        int headX = xCoord + BlockBed.footBlockToHeadBlockMap[facing][0];
        int headZ = zCoord + BlockBed.footBlockToHeadBlockMap[facing][1];
        boolean occupied = false;
        for (Object candidate : worldObj.playerEntities) {
            EntityPlayer player = (EntityPlayer) candidate;
            if (player.isPlayerSleeping() && player.playerLocation != null
                    && player.playerLocation.posX == headX && player.playerLocation.posY == yCoord
                    && player.playerLocation.posZ == headZ) {
                occupied = true;
                break;
            }
        }
        if (occupiedLastTick && !occupied) {
            setUses(uses + 1);
            if (uses >= 3) {
                worldObj.setBlockToAir(xCoord, yCoord, zCoord);
                worldObj.setBlockToAir(headX, yCoord, headZ);
            }
        }
        occupiedLastTick = occupied;
    }
}
