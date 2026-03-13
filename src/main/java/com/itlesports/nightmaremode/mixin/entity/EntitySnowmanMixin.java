package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.EntityGolem;
import net.minecraft.src.EntitySnowman;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntitySnowman.class)
public class EntitySnowmanMixin extends EntityGolem {
    public EntitySnowmanMixin(World world) {
        super(world);
    }

    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(floatValue = 1.0f))
    private float increaseTemperatureCondition(float constant){
        return constant * 2;
    }
}
