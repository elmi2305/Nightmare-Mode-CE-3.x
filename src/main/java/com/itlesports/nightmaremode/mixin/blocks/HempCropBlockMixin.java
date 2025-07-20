package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.HempCropBlock;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HempCropBlock.class)
public class HempCropBlockMixin {
    @Inject(method = "getBaseGrowthChance", at = @At("HEAD"),cancellable = true)
    private void makeHempGrowFaster(World world, int i, int j, int k, CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(0.2f);
    }
}
