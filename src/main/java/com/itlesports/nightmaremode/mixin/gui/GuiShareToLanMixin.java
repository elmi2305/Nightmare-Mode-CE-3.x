package com.itlesports.nightmaremode.mixin.gui;

import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiShareToLan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiShareToLan.class)
public class GuiShareToLanMixin extends GuiScreen {
    @Shadow private GuiButton buttonAllowCommandsToggle;
    @Shadow private GuiButton buttonGameMode;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void removeCheatButtons(CallbackInfo ci){
        this.buttonList.remove(this.buttonAllowCommandsToggle);
        this.buttonList.remove(this.buttonGameMode);
    }
    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiShareToLan;drawCenteredString(Lnet/minecraft/src/FontRenderer;Ljava/lang/String;III)V",ordinal = 1))
    private void avoidDrawingUnnecessaryText(GuiShareToLan instance, FontRenderer fontRenderer, String s, int i, int j, int k){}
}
