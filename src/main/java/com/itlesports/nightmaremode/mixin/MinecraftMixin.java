package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public GameSettings gameSettings;
    @Shadow public GuiScreen currentScreen;

    @Shadow public EntityClientPlayerMP thePlayer;
    @Unique boolean testBoolean = true;
    @Unique boolean testBoolean1 = false;
    @Unique float fov = -1;

    @Inject(method = "startGame", at = @At("TAIL"))
    private void addNightmareSpecificKeybinds(CallbackInfo ci){
        NightmareMode.getInstance().initKeybind();
    }

    @Inject(method = "screenshotListener", at = @At(value = "HEAD"))
    private void manageKeybinds(CallbackInfo ci) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) && Keyboard.isKeyDown(Keyboard.KEY_F4) && NightmareMode.getInstance() != null && !NightmareMode.getInstance().getCanLeaveGame()) {
            if (NightmareMode.worldState == 0) {
                ChatMessageComponent text2 = new ChatMessageComponent();
                text2.addText("<???> Hardmode has begun.");
                text2.setColor(EnumChatFormatting.DARK_RED);
                this.thePlayer.sendChatToPlayer(text2);
                this.thePlayer.playSound("mob.wither.death",1.0f,0.905f);
                WorldUtils.gameProgressSetNetherBeenAccessedServerOnly();
                NightmareMode.worldState = 1;
            }
        }


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
