package com.itlesports.nightmaremode.mixin.blocks;

import api.item.tag.Tag;
import api.inventory.InventoryUtils;
import btw.block.BTWBlocks;
import btw.block.blocks.HopperBlock;
import btw.block.tileentity.HopperTileEntity;
import btw.item.Filtering;
import com.itlesports.nightmaremode.block.tileEntities.HellforgeTileEntity;
import net.minecraft.src.Block;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(HopperTileEntity.class)
public abstract class HopperTileEntityMixin extends TileEntity {
    @Shadow public abstract ItemStack decrStackSize(int iSlot, int iAmount);
    @Shadow public abstract ItemStack getStackInSlot(int iSlot);
    @Shadow public abstract boolean canCurrentFilterProcessItem(ItemStack stack);


    @Inject(method = "attemptToEjectStackFromInv", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockTileEntity(III)Lnet/minecraft/src/TileEntity;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void governHellforge(CallbackInfo ci, int iStackIndex, ItemStack invStack, int iEjectStackSize, ItemStack ejectStack, int iTargetI, int iTargetJ, int iTargetK, boolean bEjectIntoWorld, int iTargetBlockID, Block targetBlock) {
        TileEntity targetTileEntity = this.worldObj.getBlockTileEntity(iTargetI, iTargetJ, iTargetK);
        if (targetTileEntity instanceof HellforgeTileEntity hellforge) {
            ItemStack input = hellforge.getStackInSlot(0);
            ejectStack.stackSize = 1; // caps it to 1 so it transfers 1 item at a time
            if (input == null && this.isItemValidForSmelting(ejectStack.copy())) {
                hellforge.setInventorySlotContents(0, ejectStack.copy());
                this.decrStackSize(iStackIndex, 1);
                this.worldObj.playAuxSFX(2231, this.xCoord, this.yCoord, this.zCoord, 0);
                hellforge.onInventoryChanged();
            }
            ci.cancel();
        }
    }

    @Inject(method = "updateEntity", at = @At("TAIL"))
    private void extractFromHellforgeWhenPowered(CallbackInfo ci) {
        if (this.worldObj.isRemote
                || this.worldObj.getTotalWorldTime() % 3L != 0L
                || !((HopperBlock)BTWBlocks.hopper).isBlockOn(
                        this.worldObj, this.xCoord, this.yCoord, this.zCoord)) {
            return;
        }

        TileEntity tileAbove = this.worldObj.getBlockTileEntity(
                this.xCoord, this.yCoord + 1, this.zCoord);
        if (!(tileAbove instanceof HellforgeTileEntity hellforge)) {
            return;
        }

        ItemStack output = hellforge.getStackInSlot(2);
        if (output == null || !this.canCurrentFilterProcessItem(output)) {
            return;
        }

        int transferLimit = Math.min(8, output.stackSize);
        ItemStack transfer = output.copy();
        transfer.stackSize = transferLimit;
        boolean fullyInserted = InventoryUtils.addItemStackToInventoryInSlotRange(
                (IInventory)(Object)this, transfer, 0, 17);
        int transferred = fullyInserted ? transferLimit : transferLimit - transfer.stackSize;
        if (transferred > 0) {
            hellforge.decrStackSize(2, transferred);
            this.worldObj.playAuxSFX(2231, this.xCoord, this.yCoord, this.zCoord, 0);
        }
    }


    @Unique
    protected boolean isItemValidForSmelting(ItemStack stack) {
        if (stack == null) {
            return false;
        } else {
            ItemStack var1 = FurnaceRecipes.smelting().getSmeltingResult(stack.getItem().itemID);
            return var1 != null;
        }
    }
}
