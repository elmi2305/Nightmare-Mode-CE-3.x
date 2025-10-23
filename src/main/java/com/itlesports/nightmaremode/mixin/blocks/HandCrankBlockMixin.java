package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.HandCrankBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HandCrankBlock.class)
public class HandCrankBlockMixin {
    @ModifyConstant(method = "onBlockActivated", constant = @Constant(floatValue = 1.0f, ordinal = 0))
    private float increaseHungerDrain(float constant){
        return constant * 2;
    }
}
