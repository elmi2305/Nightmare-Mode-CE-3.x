package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class)
public interface EntityRendererAccessor {
    @Accessor("lightmapColors")
    void setLightMapColors(int[] par1);
}
