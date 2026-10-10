package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.HempCropBlock;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemShears.class)
public class ItemShearsMixin {
    @Inject(method = "isEfficientVsBlock", at = @At("HEAD"), cancellable = true)
    private void stopTreatingHempAsIdeal(ItemStack stack, World world, Block block, int x, int y, int z,
                                        CallbackInfoReturnable<Boolean> cir) {
        if (block instanceof HempCropBlock) cir.setReturnValue(false);
    }

    @Inject(method = "getStrVsBlock", at = @At("HEAD"), cancellable = true)
    private void slowHempBreaking(ItemStack stack, World world, Block block, int x, int y, int z,
                                  CallbackInfoReturnable<Float> cir) {
        if (block instanceof HempCropBlock) cir.setReturnValue(0.25F);
    }
}
