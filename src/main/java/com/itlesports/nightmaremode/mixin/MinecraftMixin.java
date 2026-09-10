package com.itlesports.nightmaremode.mixin;

import api.AddonHandler;
import btw.BTWMod;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.integration.emi.RecipeIndexExporter;
import com.itlesports.nightmaremode.util.NightmareKeyBindings;
import com.itlesports.nightmaremode.util.interfaces.ZoomStateAccessor;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public GameSettings gameSettings;
    @Shadow public GuiScreen currentScreen;
    @Shadow public EntityRenderer entityRenderer;

    @Unique private boolean wasZooming = false;
    @Unique private float originalFov = 0.0f;

    @Inject(method = "startGame", at = @At("TAIL"))
    private void nightmareMode$startAutomatedRecipeExport(CallbackInfo ci) {
        if (NightmareMode.devMode) {
            RecipeIndexExporter.startAutomatedExport((Minecraft) (Object) this);
        } else{
            System.out.println("Recipe export was skipped because devmode is disabled");
        }
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

    @ModifyArg(method = "startGame", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"))
    private String changeWindowText(String newTitle){
        return newTitle + " | Nightmare Mode v"+ AddonHandler.getModByID("nightmare").getVersionString();
    }
    @Redirect(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Minecraft;readImage(Ljava/io/File;)Ljava/nio/ByteBuffer;"))
    private ByteBuffer nm$readWindowIcon(Minecraft minecraft, File originalFile) throws IOException {
        return this.nm$readWindowIconResource(originalFile.getName().contains("16x16")
                ? "/assets/nightmare/window_icon_16x16.png"
                : "/assets/nightmare/window_icon_32x32.png");
    }

    @Unique
    private ByteBuffer nm$readWindowIconResource(String path) throws IOException {
        try (InputStream stream = MinecraftMixin.class.getResourceAsStream(path)) {
            if (stream == null) {
                throw new IOException("missing Nightmare Mode window icon: " + path);
            }

            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new IOException("invalid Nightmare Mode window icon: " + path);
            }

            int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
            ByteBuffer buffer = ByteBuffer.allocate(4 * pixels.length);
            for (int pixel : pixels) {
                buffer.putInt(pixel << 8 | pixel >>> 24 & 0xFF);
            }
            buffer.flip();
            return buffer;
        }
    }

    @Inject(method = "runTick", at = @At("TAIL"))
    private void nightmareMode$stopAfterRecipeExport(CallbackInfo ci) {
        if (RecipeIndexExporter.consumeDevelopmentStopRequest()
                || RecipeIndexExporter.consumeAutomatedStopRequest()) {
            ((Minecraft) (Object) this).shutdown();
        }
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
    }
}
