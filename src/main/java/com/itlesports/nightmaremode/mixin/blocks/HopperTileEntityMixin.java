package com.itlesports.nightmaremode.mixin.blocks;

import api.item.tag.Tag;
import btw.block.tileentity.HopperTileEntity;
import btw.item.Filtering;
import com.itlesports.nightmaremode.block.tileEntities.HellforgeTileEntity;
import net.minecraft.src.Block;
import net.minecraft.src.FurnaceRecipes;
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
    @Shadow
    public abstract ItemStack decrStackSize(int iSlot, int iAmount);

    @Shadow public abstract ItemStack getStackInSlot(int iSlot);

    @ModifyConstant(method = "attemptToEjectXPIntoHopper", constant = @Constant(intValue = 100), remap = false)
    private int increaseExperienceCapacity(int xp) {
        return 1000;
    }

    @ModifyConstant(method = "attemptToSwallowXPOrb", constant = @Constant(intValue = 100), remap = false)
    private int increaseExperienceCapacity1(int xp) {
        return 1000;
    }

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

    @ModifyArg(method = "updateEntity", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/EntityCreature;attemptToPossessCreaturesAroundBlock(Lnet/minecraft/src/World;IIIIILapi/entity/mob/possession/PossessionSource;)I"), index = 5)
    private int lowerPossessionHopperRadius0(int i) {
        return i - 8;
    }

    @ModifyArg(method = "hopperSoulOverload", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/EntityCreature;attemptToPossessCreaturesAroundBlock(Lnet/minecraft/src/World;IIIIILapi/entity/mob/possession/PossessionSource;)I"), index = 5)
    private int lowerPossessionHopperRadius1(int i) {
        return i - 8;
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
