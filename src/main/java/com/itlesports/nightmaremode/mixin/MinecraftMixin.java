package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.GameSettings;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Minecraft;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public GameSettings gameSettings;
    @Shadow public GuiScreen currentScreen;

    @Unique boolean testBoolean = true;
    @Unique boolean testBoolean1 = false;
    @Unique float fov = -1;

    @Inject(method = "startGame", at = @At("TAIL"))
    private void addNightmareSpecificKeybinds(CallbackInfo ci){
        NightmareMode.getInstance().initKeybind();
    }

    @Inject(method = "screenshotListener", at = @At(value = "HEAD"))
    private void manageKeybinds(CallbackInfo ci) {
        if (Keyboard.isKeyDown(NightmareMode.nightmareZoom.keyCode) && this.currentScreen == null) {
            if (testBoolean) {
                this.fov = gameSettings.fovSetting;
                testBoolean = false;
            }
            testBoolean1 = true;
            gameSettings.fovSetting = -1.2f;
        } else if (testBoolean1 && gameSettings.fovSetting == -1.2f) {
            gameSettings.fovSetting = this.fov;
            testBoolean1 = false;
        }
    }
}
