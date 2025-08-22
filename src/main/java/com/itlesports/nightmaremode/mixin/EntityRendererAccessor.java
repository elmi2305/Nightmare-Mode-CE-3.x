package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Provides access to EntityRenderer's private fields and methods
 */
@Mixin(EntityRenderer.class)
public interface EntityRendererAccessor {
    @Invoker("updateLightmap")
    void nightmaremode$updateLightmap(float partialTicks);
}