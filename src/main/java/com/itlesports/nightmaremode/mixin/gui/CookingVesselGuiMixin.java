package com.itlesports.nightmaremode.mixin.gui;

import btw.client.gui.CookingVesselGui;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CookingVesselGui.class)
public class CookingVesselGuiMixin {
    @Unique private static final ResourceLocation COOKING_VESSEL_TEXTURE =
            new ResourceLocation("nightmare:textures/gui/cooking_vessel_30.png");

    @ModifyArg(method = "drawGuiContainerBackgroundLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation useLegacyVesselTexture(ResourceLocation texture) {
        return COOKING_VESSEL_TEXTURE;
    }
}
