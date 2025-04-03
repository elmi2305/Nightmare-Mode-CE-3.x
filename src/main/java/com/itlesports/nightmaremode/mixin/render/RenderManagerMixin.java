package com.itlesports.nightmaremode.mixin.render;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.Entity;
import net.minecraft.src.RenderManager;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(RenderManager.class)
public class RenderManagerMixin {
    @Unique
    private static final Random random = new Random();
    @Unique
    private static long lastChangeTime = 0;
    @Unique
    private static int activeEffect = -1; // -1 means no effect
    @Unique
    private static int threshold = 100; // -1 means no effect

    @Inject(method = "renderEntity", at = @At("TAIL"))
    private void destroyRenderingTemporarily(Entity par1Entity, float par2, CallbackInfo ci) {
        if (NightmareMode.isAprilFools && NightmareMode.aprilFoolsRendering) {
            long currentTime = par1Entity.worldObj.getWorldTime();
            if(threshold == 100){
                threshold = 100 + random.nextInt(200);
            }

            if (currentTime - lastChangeTime > threshold) {
                activeEffect = random.nextInt(30);
                lastChangeTime = currentTime;
                threshold = 100;
            }
            switch (activeEffect) {
                case 0: // Fisheye
                    GL11.glScalef(1.2f, 1.2f, 1);
                    GL11.glRotatef(10, 1, 0, 0);
                    break;
                case 1: // Jittering
                    GL11.glScalef(1 + (float) (Math.random() * 0.5 - 0.25),
                            1 + (float) (Math.random() * 0.5 - 0.25),
                            1);
                    break;
                case 2: // Spinning
                    GL11.glRotatef((currentTime % 180) / 2.0f, 0, 0, 1); // Adjusted for tick-based time
                    break;
                case 3: // Flip Y-axis
                    GL11.glScalef(1, -1, 1);
                    break;
                case 4: // Extreme Distortion
                    GL11.glScalef(-0.5f, -1, 0.5f);
                    break;
                case 5: // Wobble
                    GL11.glScalef(1 + 0.1f * (float) Math.sin(currentTime * 0.1),
                            1 + 0.1f * (float) Math.cos(currentTime * 0.1),
                            1);
                    break;
                case 6: // Barrel Roll
                    GL11.glRotatef(currentTime % 360, 0, 0, 1);
                    break;
                case 7: // Wave Warp
                    GL11.glScalef(1 + 0.2f * (float) Math.sin(currentTime * 0.05),
                            1 + 0.2f * (float) Math.cos(currentTime * 0.05),
                            1);
                    break;
                case 8: // Offset Chaos
                    GL11.glTranslatef((float) (Math.random() * 0.2 - 0.1),
                            (float) (Math.random() * 0.2 - 0.1),
                            0);
                    break;
                case 9: // Zoom Pulse
                    float pulse = 1 + 0.3f * (float) Math.sin(currentTime * 0.1);
                    GL11.glScalef(pulse, pulse, 1);
                    break;
                case 10: // Inverted Depth
                    GL11.glScalef(1, 1, -1);
                    break;
                case 11: // Screen Splitting
                    GL11.glTranslatef(0, (currentTime % 20 < 10) ? 0.05f : -0.05f, 0);
                    break;
                case 12: // Drunk Mode
                    GL11.glRotatef(5 * (float) Math.sin(currentTime * 0.1), 0, 0, 1);
                    break;
                case 13: // Tornado Spin
                    float scaleTornado = 1 + 0.2f * (float) Math.sin(currentTime * 0.2);
                    GL11.glScalef(scaleTornado, scaleTornado, 1);
                    GL11.glRotatef(currentTime % 360, 0, 0, 1);
                    break;
                case 14: // Vortex Stretch
                    GL11.glScalef(1 + 0.5f * (float) Math.sin(currentTime * 0.1),
                            1 - 0.5f * (float) Math.sin(currentTime * 0.1),
                            1);
                    break;
                case 15: // Chromatic Aberration (Fake)
                    GL11.glTranslatef((float) Math.sin(currentTime * 0.1) * 0.01f,
                            (float) Math.cos(currentTime * 0.1) * 0.01f,
                            0);
                    break;
                case 16: // Screen Meltdown
                    GL11.glScalef(1, Math.max(0.1f, 1 - (currentTime % 200) / 200.0f), 1);
                    break;
                case 17: // Hyperspeed Zoom
                    float zoomSpeed = 1 + 0.5f * (float) Math.sin(currentTime * 0.5);
                    GL11.glScalef(zoomSpeed, zoomSpeed, 1);
                    break;
                default:
                    // No effect (normal rendering)
                    break;
            }
        }
    }
}
