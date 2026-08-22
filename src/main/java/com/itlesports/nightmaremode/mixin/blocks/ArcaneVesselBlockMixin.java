package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.ArcaneVesselBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ArcaneVesselBlock.class)
public class ArcaneVesselBlockMixin {
    @ModifyConstant(method = "getComparatorInputOverride", constant = @Constant(floatValue = 1000.0f), remap = false)
    private float increaseExperienceCapacity(float capacity) {
        return 100000.0f;
    }
}
