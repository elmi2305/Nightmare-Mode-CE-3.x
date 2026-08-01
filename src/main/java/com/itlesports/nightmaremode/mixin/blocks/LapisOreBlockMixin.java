package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.LapisOreBlock;
import com.itlesports.nightmaremode.item.NMItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(LapisOreBlock.class)
public class LapisOreBlockMixin {
    @Inject(method = "idDropped", at = @At("HEAD"), cancellable = true)
    private void dropRawAzureStone(int metadata, Random random, int fortune, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(NMItems.rawAzureStone.itemID);
    }

    @Inject(method = "quantityDropped", at = @At("HEAD"), cancellable = true)
    private void dropOneRawAzureStone(Random random, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }

    @Inject(method = "damageDropped", at = @At("HEAD"), cancellable = true)
    private void useRawAzureMetadata(int metadata, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }

    @Inject(method = "idDroppedOnConversion", at = @At("HEAD"), cancellable = true, remap = false)
    private void convertToRawAzureStone(boolean dropPiles, int metadata, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(NMItems.rawAzureStone.itemID);
    }

    @Inject(method = "quantityDroppedOnConversion", at = @At("HEAD"), cancellable = true, remap = false)
    private void convertOneRawAzureStone(Random random, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }

    @Inject(method = "damageDroppedOnConversion", at = @At("HEAD"), cancellable = true, remap = false)
    private void useConvertedRawAzureMetadata(int metadata, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}
