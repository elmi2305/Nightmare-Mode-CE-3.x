package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityHorse.class)
public class EntityHorseMixin {
    @ModifyConstant(method = "applyEntityAttributes", constant = @Constant(doubleValue = 20.0f))
    private double increaseHP(double constant){
        return 24.0;
    }
}
