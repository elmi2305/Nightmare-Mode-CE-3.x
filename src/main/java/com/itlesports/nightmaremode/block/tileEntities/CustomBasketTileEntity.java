package com.itlesports.nightmaremode.block.tileEntities;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import api.block.TileEntityDataPacketHandler;
import api.item.util.ItemUtils;
import btw.block.tileentity.BasketTileEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet132TileEntityData;

public class CustomBasketTileEntity extends BasketTileEntity implements TileEntityDataPacketHandler {
    private ItemStack storageStack = null;

    public CustomBasketTileEntity() {
        super(NMBlocks.customWickerBasket);
    }

    public void updateEntity() {
        super.updateEntity();
        this.updateVisualContentsState();
    }

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagCompound storageTag = tag.getCompoundTag("fcStorageStack");
        if (storageTag != null) {
            this.storageStack = ItemStack.loadItemStackFromNBT(storageTag);
        }

    }

    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (this.storageStack != null) {
            NBTTagCompound storageTag = new NBTTagCompound();
            this.storageStack.writeToNBT(storageTag);
            tag.setCompoundTag("fcStorageStack", storageTag);
        }

    }

    public void ejectContents() {
        if (this.storageStack != null) {
            ItemUtils.ejectStackWithRandomOffset(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.storageStack);
            this.storageStack = null;
        }

    }

    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        if (this.storageStack != null) {
            NBTTagCompound storageTag = new NBTTagCompound();
            this.storageStack.writeToNBT(storageTag);
            tag.setCompoundTag("x", storageTag);
        }

        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    public void readNBTFromPacket(NBTTagCompound tag) {
        NBTTagCompound storageTag = tag.getCompoundTag("x");
        if (storageTag != null) {
            this.storageStack = ItemStack.loadItemStackFromNBT(storageTag);
        }

    }

    public boolean shouldStartClosingServerSide() {
        return !this.worldObj.isRemote && this.worldObj.getClosestPlayer((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)8.0F) == null;
    }

    public void setStorageStack(ItemStack stack) {
        if (stack != null) {
            this.storageStack = stack.copy();
        } else {
            this.storageStack = null;
        }

        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }


    public ItemStack getStorageStack() {
        return this.storageStack;
    }

    private void updateVisualContentsState() {
        if (!this.worldObj.isRemote) {
            boolean bHasContents = NMBlocks.customWickerBasket.getHasContents(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            if (bHasContents == (this.storageStack == null)) {
                NMBlocks.customWickerBasket.setHasContents(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.storageStack != null);
            }
        }

    }

    public static int calculateComparatorPower(CustomBasketTileEntity tileEntity) {
        ItemStack stack = tileEntity.storageStack;
        if (stack != null) {
            float fullness = (float)stack.stackSize / (float)stack.getMaxStackSize();
            return 1 + (int)(14.0F * fullness);
        } else {
            return 0;
        }
    }
}
