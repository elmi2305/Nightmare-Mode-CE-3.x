package com.itlesports.nightmaremode.mixin.render;

import net.minecraft.src.ResourceLocation;
import net.minecraft.src.TileEntitySpecialRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TileEntitySpecialRenderer.class)
public interface TileEntitySpecialRendererAccessor {
    @Invoker("bindTexture")
    void nightmareMode$bindTexture(ResourceLocation texture);
}
