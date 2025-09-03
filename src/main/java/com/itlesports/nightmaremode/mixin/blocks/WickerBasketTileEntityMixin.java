package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.BasketBlock;
import btw.block.tileentity.BasketTileEntity;
import btw.block.tileentity.WickerBasketTileEntity;
import btw.item.util.ItemUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WickerBasketTileEntity.class)
public abstract class WickerBasketTileEntityMixin extends BasketTileEntity {
    @Mutable @Shadow @Final public static int MAX_STORAGE_STACKS;

    @Unique
    private static int LIMIT = 2;

    @Mutable @Shadow private ItemStack[] storageStacks;

    public WickerBasketTileEntityMixin(BasketBlock blockBasket) {
        super(blockBasket);
    }


    @Inject(method = "<init>", at = @At("TAIL"))
    private void setMaxItems(CallbackInfo ci){
        MAX_STORAGE_STACKS = 2;
    }





    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("fcStorageStack")) {
            NBTTagCompound storageTag = tag.getCompoundTag("fcStorageStack");
            if (storageTag != null) {
                this.storageStacks[0] = ItemStack.loadItemStackFromNBT(storageTag);
            }
        } else {
            NBTTagList tagList = tag.getTagList("Items");
            for (int i = 0; i < Math.min(tagList.tagCount(), LIMIT); ++i) {
                NBTTagCompound tagCompound = (NBTTagCompound)tagList.tagAt(i);
                byte slot = tagCompound.getByte("Slot");
                if (slot < 0 || slot >= LIMIT) continue;
                this.storageStacks[slot] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < LIMIT; ++i) {
            if (this.storageStacks[i] == null) continue;
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setByte("Slot", (byte)i);
            this.storageStacks[i].writeToNBT(tagCompound);
            tagList.appendTag(tagCompound);
        }
        tag.setTag("Items", tagList);
    }

    @Override
    public void ejectContents() {
        for (int i = 0; i < LIMIT; ++i) {
            if (this.storageStacks[i] != null) {
                ItemUtils.ejectStackWithRandomOffset(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.storageStacks[i]);
            }
            this.storageStacks[i] = null;
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList tagList = new NBTTagList();
        for (int var3 = 0; var3 < LIMIT; ++var3) {
            if(this.storageStacks.length == 0) continue;

            if (this.storageStacks[var3] == null) continue;
            NBTTagCompound var4 = new NBTTagCompound();
            var4.setByte("s", (byte)var3);
            this.storageStacks[var3].writeToNBT(var4);
            tagList.appendTag(var4);
        }
        tag.setTag("i", tagList);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    /**
     * @author e
     * @reason a
     */
    @Overwrite
    public void readNBTFromPacket(NBTTagCompound tag) {
        NBTTagList storageTagList = tag.getTagList("i");
        for (int var3 = 0; var3 < Math.min(storageTagList.tagCount(), LIMIT); ++var3) {
            NBTTagCompound var4 = (NBTTagCompound)storageTagList.tagAt(var3);
            int var5 = var4.getByte("s") & 0xFF;
            if (var5 >= LIMIT) continue;
            if(this.storageStacks.length == 0) return;

            this.storageStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
        }
    }


}
