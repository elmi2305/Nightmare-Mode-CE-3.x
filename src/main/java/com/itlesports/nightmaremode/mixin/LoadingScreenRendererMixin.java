package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.LoadingScreenRenderer;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin {
    @ModifyArg(method = "setLoadingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation setCustomBackground(ResourceLocation par1ResourceLocation){
        if(NightmareMode.bloodNightmare){
            return new ResourceLocation("textures/gui/bloodNightmare.png");
        }
        return new ResourceLocation("textures/gui/dirtBackground.png");
    }
}
