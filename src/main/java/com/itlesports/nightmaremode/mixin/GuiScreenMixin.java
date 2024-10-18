package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.GuiScreen;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiScreen.class)
public class GuiScreenMixin {
    @ModifyArg(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation changeBackground(ResourceLocation par1ResourceLocation){
        return new ResourceLocation("textures/gui/dirtBackground.png");
    }
}
