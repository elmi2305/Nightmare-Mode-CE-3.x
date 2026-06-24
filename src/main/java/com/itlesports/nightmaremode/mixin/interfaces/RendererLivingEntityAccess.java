package com.itlesports.nightmaremode.mixin.interfaces;

import net.minecraft.src.RendererLivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RendererLivingEntity.class)
public interface RendererLivingEntityAccess {
    @Invoker("interpolateRotation")
    float invokeInterpolateRotation(float par1, float par2, float par3);
}
