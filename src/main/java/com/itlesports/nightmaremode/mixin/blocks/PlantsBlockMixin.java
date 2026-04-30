package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.PlantsBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PlantsBlock.class)
public class PlantsBlockMixin {
    @ModifyConstant(method = "isInBrightLight", constant = @Constant(intValue = 15))
    private int lowerLightRequirementToGrow(int constant){
        return constant - 4;
    }
}
