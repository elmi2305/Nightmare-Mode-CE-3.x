package com.itlesports.nightmaremode.mixin.render;

import btw.client.render.entity.CanvasRenderer;
import net.minecraft.src.Render;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(CanvasRenderer.class)
public abstract class CanvasRendererMixin extends Render {
    @Unique
    private ResourceLocation nmCanvas = new ResourceLocation("nightmare:textures/entity/nmCanvas.png");

    @ModifyArg(method = "func_158_a", at = @At(value = "INVOKE", target = "Lbtw/client/render/entity/CanvasRenderer;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation customCanvasPaintings(ResourceLocation par1){
        return nmCanvas;
    }
}
