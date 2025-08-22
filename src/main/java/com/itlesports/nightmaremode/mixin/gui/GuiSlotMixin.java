package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiSlot.class)
public class GuiSlotMixin {

    @Shadow private int height;

    @Shadow private int width;

    @ModifyArg(method = "overlayBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation drawCustomBackground1(ResourceLocation par1ResourceLocation){
        if(NightmareMode.bloodmare){
            return new ResourceLocation("textures/gui/bloodNightmare.png");
        }
        return new ResourceLocation("textures/gui/dirtBackground.png");
    }
}
