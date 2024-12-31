package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.GuiSlot;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiSlot.class)
public class GuiSlotMixin {
    @ModifyArg(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation drawCustomBackground(ResourceLocation par1ResourceLocation){
        if(NightmareMode.bloodmare){
            return new ResourceLocation("textures/gui/bloodNightmare.png");
        }
        return new ResourceLocation("textures/gui/dirtBackground.png");
    }
    @ModifyArg(method = "overlayBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation drawCustomBackground1(ResourceLocation par1ResourceLocation){
        if(NightmareMode.bloodmare){
            return new ResourceLocation("textures/gui/bloodNightmare.png");
        }
        return new ResourceLocation("textures/gui/dirtBackground.png");
    }
}
