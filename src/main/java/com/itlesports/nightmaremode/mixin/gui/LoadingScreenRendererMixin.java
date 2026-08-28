package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.nmgui.JourneyTitleTheme;
import net.minecraft.src.LoadingScreenRenderer;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LoadingScreenRenderer.class)
public class LoadingScreenRendererMixin {
    @Shadow private Minecraft mc;

    /** The loading renderer binds the vanilla dirt texture directly instead of using GuiScreen. */
    @ModifyArg(method = "setLoadingProgress", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation journeyMode$replaceLoadingDirt(ResourceLocation vanillaTexture) {
        return JourneyTitleTheme.getActive(this.mc).background;
    }

    @ModifyArg(method = "setLoadingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setColorOpaque_I(I)V", ordinal = 1))
    private int journeyMode$tintProgressTrack(int vanillaColor) {
        return JourneyTitleTheme.getActive(this.mc).edge & 0x00FFFFFF;
    }

    @ModifyArg(method = "setLoadingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Tessellator;setColorOpaque_I(I)V", ordinal = 2))
    private int journeyMode$tintProgressFill(int vanillaColor) {
        return JourneyTitleTheme.getActive(this.mc).textHighlight & 0x00FFFFFF;
    }

    @ModifyArg(method = "setLoadingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I", ordinal = 0), index = 3)
    private int journeyMode$tintPrimaryLoadingText(int vanillaColor) {
        return JourneyTitleTheme.getActive(this.mc).textHighlight;
    }

    @ModifyArg(method = "setLoadingProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I", ordinal = 1), index = 3)
    private int journeyMode$tintSecondaryLoadingText(int vanillaColor) {
        return JourneyTitleTheme.getActive(this.mc).text;
    }
}
