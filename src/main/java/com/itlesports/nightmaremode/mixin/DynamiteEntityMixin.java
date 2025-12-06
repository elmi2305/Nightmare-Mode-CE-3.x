package com.itlesports.nightmaremode.mixin;

import btw.entity.DynamiteEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DynamiteEntity.class)
public class DynamiteEntityMixin {
    @ModifyConstant(method = "spawnRedneckFish", constant = @Constant(floatValue = 0.25f),remap = false)
    private float increaseChanceOfRegularFish(float constant){
        return 0.75f;
    }
}
