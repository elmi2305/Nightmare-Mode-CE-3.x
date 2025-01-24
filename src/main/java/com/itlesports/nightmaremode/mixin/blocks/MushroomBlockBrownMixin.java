package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.MushroomBlockBrown;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MushroomBlockBrown.class)
public class MushroomBlockBrownMixin {
    @ModifyArg(method = "checkForSpread", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I",ordinal = 0))
    private int increaseChanceOfMushroomSpreading(int bound){
        return 6;
    }
    @ModifyConstant(method = "checkForSpread", constant = @Constant(intValue = 4,ordinal = 0))
    private int decreaseMushroomDetectionRange(int constant){
        return 2;
    }
    @ModifyConstant(method = "checkForSpread", constant = @Constant(intValue = 4,ordinal = 1))
    private int increaseAttemptsToSpread(int constant){
        return 7;
    }
}
