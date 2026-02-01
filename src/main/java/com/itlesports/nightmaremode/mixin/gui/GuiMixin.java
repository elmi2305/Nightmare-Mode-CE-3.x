package com.itlesports.nightmaremode.mixin.gui;

import net.minecraft.src.Gui;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Mutable
    @Shadow @Final public static ResourceLocation optionsBackground;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void redeclareBackgroundField(CallbackInfo ci){
        optionsBackground = new ResourceLocation("nightmare:textures/gui/dirtBackground.png");
    }
}
