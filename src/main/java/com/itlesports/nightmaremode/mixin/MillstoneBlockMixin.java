package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.MillstoneBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MillstoneBlock.class)
public class MillstoneBlockMixin {
    @ModifyConstant(method = "randomDisplayTick", constant = @Constant(floatValue = 1.0f))
    private float lowerVolume(float constant){
        return 0.09f;
    }
    @ModifyConstant(method = "randomDisplayTick", constant = @Constant(floatValue = 1.5f))
    private float lowerVolume1(float constant){
        return 0.09f;
    }
    @ModifyConstant(method = "randomDisplayTick", constant = @Constant(floatValue = 0.75f,ordinal = 1))
    private float lowerVolume2(float constant){
        return 0.065f;
    }
}
