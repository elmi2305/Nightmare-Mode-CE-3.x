package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.WorldUtils;
import com.itlesports.nightmaremode.client.NightmareKeyBindings;
import com.itlesports.nightmaremode.client.ZoomStateAccessor;
import com.itlesports.nightmaremode.network.HandshakeClient;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public GameSettings gameSettings;
    @Shadow public GuiScreen currentScreen;
    @Shadow public EntityRenderer entityRenderer;

    @Shadow public EntityClientPlayerMP thePlayer;
    @Unique
    private boolean wasZooming = false;
    @Unique
    private float originalFov = 0.0f;

    @Inject(method = "runTick", at = @At("TAIL"))
    private void nmOnClientTick(CallbackInfo ci) {
        HandshakeClient.onClientTick();
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I", remap = false))
    private int nmBlockHotbarScrollWhenZoom() {
        if (entityRenderer instanceof ZoomStateAccessor accessor) {
            if (accessor.nightmareMode$isToggleZoomActive() && accessor.nightmareMode$isToggleZoomKeyHeld()) {
                return 0;
            }
        }
        return Mouse.getEventDWheel();
    }

    @Inject(method = "screenshotListener", at = @At(value = "HEAD"))
    private void manageKeybinds(CallbackInfo ci) {
        if (Keyboard.isKeyDown(NightmareKeyBindings.nmZoomHold.keyCode) && this.currentScreen == null) {
            if (!this.wasZooming) {
                this.originalFov = this.gameSettings.fovSetting;
                this.wasZooming = true;
            }
            this.gameSettings.fovSetting = -1.2f;
        } else if (this.wasZooming) {
            this.gameSettings.fovSetting = originalFov;
            this.wasZooming = false;
        }


        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) && Keyboard.isKeyDown(Keyboard.KEY_F4) && NightmareMode.getInstance() != null && !NightmareMode.getInstance().getCanLeaveGame()) {
            if (NightmareMode.worldState == 0) {
                ChatMessageComponent text2 = new ChatMessageComponent();
                text2.addText("<???> " + ("nightmare.hardmode_begun"));
                text2.setColor(EnumChatFormatting.DARK_RED);
                this.thePlayer.sendChatToPlayer(text2);
                this.thePlayer.playSound("mob.wither.death",1.0f,0.905f);
                WorldUtils.gameProgressSetNetherBeenAccessedServerOnly();
                NightmareMode.worldState = 1;
            }
        }
    }
}