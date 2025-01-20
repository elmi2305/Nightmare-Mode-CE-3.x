package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityHorse.class)
public class EntityHorseMixin {
    @ModifyConstant(method = "applyEntityAttributes", constant = @Constant(doubleValue = 20.0d))
    private double increaseHP(double constant){
        return 24.0;
    }
    @ModifyArg(method = "applyEntityAttributes",at = @At(value = "INVOKE", target = "Lnet/minecraft/src/AttributeInstance;setAttribute(D)V",ordinal = 1))
    private double increaseSpeed(double var1){
        return NightmareUtils.getIsEclipse() ? 0.4d : var1;
    }
}
