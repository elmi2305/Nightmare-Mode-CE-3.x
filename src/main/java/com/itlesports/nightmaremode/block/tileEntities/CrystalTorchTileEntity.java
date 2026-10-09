package com.itlesports.nightmaremode.block.tileEntities;

import api.block.TileEntityDataPacketHandler;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;

public class CrystalTorchTileEntity extends TileEntity implements TileEntityDataPacketHandler {
    public static final int MAX_BURN_TICKS = 48000;
    private int orientation = 5;
    private int remainingBurnTicks = MAX_BURN_TICKS;

    public int getRemainingBurnTicks() { return this.remainingBurnTicks; }

    public void setRemainingBurnTicks(int ticks) {
        this.remainingBurnTicks = MathHelper.clamp_int(ticks, 0, MAX_BURN_TICKS);
        this.onInventoryChanged();
    }

    public ItemStack createTorchDrop() {
        ItemStack stack = new ItemStack(com.itlesports.nightmaremode.item.NMItems.crystalTorch);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("CrystalBurnTicks", this.remainingBurnTicks);
        return stack;
    }

    @Override public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote
                || this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) != NMBlocks.crystalTorch.blockID) return;
        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if ((metadata & 8) != 0) return;
        if (this.remainingBurnTicks > 0) --this.remainingBurnTicks;
        this.onInventoryChanged();
        if (this.remainingBurnTicks == 0) {
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, metadata | 8, 3);
            this.worldObj.updateAllLightTypes(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    public int getOrientation() { return this.orientation; }

    public void setOrientation(int orientation) {
        this.orientation = MathHelper.clamp_int(orientation, 1, 5);
        this.onInventoryChanged();
        if (this.worldObj != null) this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    @Override public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Orientation", this.orientation);
        tag.setInteger("CrystalBurnTicks", this.remainingBurnTicks);
    }

    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.orientation = tag.hasKey("Orientation") ? MathHelper.clamp_int(tag.getInteger("Orientation"), 1, 5) : 5;
        this.remainingBurnTicks = tag.hasKey("CrystalBurnTicks")
                ? MathHelper.clamp_int(tag.getInteger("CrystalBurnTicks"), 0, MAX_BURN_TICKS) : MAX_BURN_TICKS;
    }

    @Override public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Orientation", this.orientation);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    @Override public void readNBTFromPacket(NBTTagCompound tag) {
        this.orientation = MathHelper.clamp_int(tag.getInteger("Orientation"), 1, 5);
        if (this.worldObj != null) this.worldObj.markBlockRangeForRenderUpdate(
                this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord, this.zCoord);
    }
}
