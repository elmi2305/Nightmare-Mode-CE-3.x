package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.LoadingScreenRenderer;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin {
    @ModifyArg(method = "setLoadingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation setCustomBackground(ResourceLocation resource){
        if(NightmareMode.bloodmare){
            return new ResourceLocation("nightmare:textures/gui/bloodNightmare.png");
        }
        return resource;
    }
}
