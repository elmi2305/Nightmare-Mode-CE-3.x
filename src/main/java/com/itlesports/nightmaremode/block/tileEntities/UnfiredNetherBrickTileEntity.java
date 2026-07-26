package com.itlesports.nightmaremode.block.tileEntities;

import api.block.TileEntityDataPacketHandler;
import btw.block.BTWBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet132TileEntityData;
import net.minecraft.src.TileEntity;

public class UnfiredNetherBrickTileEntity extends TileEntity implements TileEntityDataPacketHandler {
    private static final int COOK_TIME = 14000;

    private int cookTime;
    private boolean cooking;

    @Override
    public void updateEntity() {
        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (metadata != 7 && metadata != 8) {
            return;
        }

        if (this.worldObj.isRemote) {
            if (this.cooking && this.worldObj.rand.nextInt(20) == 0) {
                this.worldObj.spawnParticle("fcwhitesmoke",
                        this.xCoord + 0.25D + this.worldObj.rand.nextDouble() * 0.5D,
                        this.yCoord + 0.25D + this.worldObj.rand.nextDouble() * 0.35D,
                        this.zCoord + 0.25D + this.worldObj.rand.nextDouble() * 0.5D,
                        0.0D, 0.0D, 0.0D);
            }
            return;
        }

        boolean nextCooking = this.hasHorizontalLavaNeighbor();
        if (nextCooking != this.cooking) {
            this.cooking = nextCooking;
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }

        if (!this.cooking) {
            if (this.cookTime != 0) {
                this.cookTime = 0;
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
            return;
        }

        int previousLevel = this.getCookLevel();
        if (++this.cookTime >= COOK_TIME) {
            BTWBlocks.unfiredPottery.onCookedByKiLn(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            return;
        }
        if (previousLevel != this.getCookLevel()) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    private boolean hasHorizontalLavaNeighbor() {
        return this.isLava(this.xCoord - 1, this.yCoord, this.zCoord)
                || this.isLava(this.xCoord + 1, this.yCoord, this.zCoord)
                || this.isLava(this.xCoord, this.yCoord, this.zCoord - 1)
                || this.isLava(this.xCoord, this.yCoord, this.zCoord + 1);
    }

    private boolean isLava(int x, int y, int z) {
        int blockID = this.worldObj.getBlockId(x, y, z);
        return blockID == Block.lavaStill.blockID || blockID == Block.lavaMoving.blockID;
    }

    public boolean isCooking() {
        return this.cooking;
    }

    public int getCookLevel() {
        if (this.cookTime <= 0) {
            return 0;
        }
        return MathHelper.clamp_int((int)((float)this.cookTime / COOK_TIME * 7.0F) + 1, 1, 7);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.cookTime = tag.getInteger("CookTime");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("CookTime", this.cookTime);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("CookTime", this.cookTime);
        tag.setBoolean("Cooking", this.cooking);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override
    public void readNBTFromPacket(NBTTagCompound tag) {
        this.cookTime = tag.getInteger("CookTime");
        this.cooking = tag.getBoolean("Cooking");
        this.worldObj.markBlockRangeForRenderUpdate(
                this.xCoord, this.yCoord, this.zCoord,
                this.xCoord, this.yCoord, this.zCoord);
    }
}
