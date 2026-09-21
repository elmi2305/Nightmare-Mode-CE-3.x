package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import api.block.TileEntityDataPacketHandler;
import com.itlesports.nightmaremode.util.StorageColor;
import com.itlesports.nightmaremode.util.interfaces.IDyeableStorage;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityChest.class)
public abstract class TileEntityChestMixin extends TileEntity implements IInventory, IDyeableStorage, TileEntityDataPacketHandler {
    @Unique private int nightmareMode$storageColor = StorageColor.BROWN;

    @Override
    public int nm$getStorageColor() {
        return this.nightmareMode$storageColor;
    }

    @Override
    public void nm$setStorageColor(int color) {
        this.nightmareMode$storageColor = color;
        if (this.worldObj != null) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void readStorageColor(NBTTagCompound tag, CallbackInfo ci) {
        this.nightmareMode$storageColor = tag.hasKey("nmStorageColor")
                ? tag.getByte("nmStorageColor") & 15 : StorageColor.BROWN;
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void writeStorageColor(NBTTagCompound tag, CallbackInfo ci) {
        if (this.nightmareMode$storageColor != StorageColor.BROWN) {
            tag.setByte("nmStorageColor", (byte) this.nightmareMode$storageColor);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("nmStorageColor", (byte) this.nightmareMode$storageColor);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    public void readNBTFromPacket(NBTTagCompound tag) {
        this.nightmareMode$storageColor = tag.getByte("nmStorageColor") & 15;
        this.worldObj.markBlockRangeForRenderUpdate(this.xCoord, this.yCoord, this.zCoord,
                this.xCoord, this.yCoord, this.zCoord);
    }

    @Override
    public void onInventoryChanged() {
        World w = this.worldObj;
        if (NightmareMode.getInstance().isGriefLogging() && !w.isRemote && NightmareMode.getInstance().getLogSettings().logItemRemoval) {

            int x = this.xCoord;
            int y = this.yCoord;
            int z = this.zCoord;
            EntityPlayer p = w.getClosestPlayer(x, y, z, 12);

            String text = "Player " + (p == null ? "NULL" : p.username) + " edited Chest at " + x + " " + y + " " + z;
            NightmareMode.appendLogLine(text);
        }

        super.onInventoryChanged();
    }
}
