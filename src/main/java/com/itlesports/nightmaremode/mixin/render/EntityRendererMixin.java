package com.itlesports.nightmaremode.mixin.render;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMSanityUtils;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.NightmareKeyBindings;
import com.itlesports.nightmaremode.util.interfaces.ZoomStateAccessor;
import com.itlesports.nightmaremode.mixin.entity.EntityAccessor;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static btw.community.nightmaremode.NightmareMode.SANITY;
import static com.itlesports.nightmaremode.util.NMSanityUtils.CRITICAL_SANITY;
import static com.itlesports.nightmaremode.util.NMSanityUtils.MAX_SANITY;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin implements EntityAccessor, ZoomStateAccessor {
    @Shadow private Minecraft mc;
    @Shadow private double cameraZoom;
    @Shadow private float farPlaneDistance;
    @Shadow public ItemRenderer itemRenderer;
    @Unique private boolean nmToggleZoomActive = false;
    @Unique private boolean nmToggleToggleZoomKeyWasDown = false;
    @Unique private double targetZoom = 1.0D;
    //    @Unique private double lastZoomLevel = 4.0D;
    @Mutable
    @Shadow float fogColorRed;
    @Shadow float fogColorBlue;
    @Shadow float fogColorGreen;

    @Shadow protected abstract void hurtCameraEffect(float partialTicks);
    @Shadow protected abstract void setupViewBobbing(float partialTicks);
    @Shadow protected abstract void enableLightmap(double partialTicks);
    @Shadow protected abstract void disableLightmap(double partialTicks);
    @Shadow protected abstract float getFOVModifier(float partialTicks, boolean useFOVSetting);


    @Shadow @Final private int[] lightmapColors;

    @Shadow protected abstract FloatBuffer setFogColorBuffer(float par1, float par2, float par3, float par4);

    @Shadow private boolean cloudFog;
    private static final ResourceLocation BLOOD_RAIN = new ResourceLocation("nightmare:textures/entity/nmBloodRain.png");

    @ModifyArg(method = "renderRainSnow", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureManager;bindTexture(Lnet/minecraft/src/ResourceLocation;)V",ordinal = 1))
    private ResourceLocation bloodMoonCustomRain(ResourceLocation par1ResourceLocation){
        if(NMUtils.getIsBloodMoon()){
            return BLOOD_RAIN;
        }
        return par1ResourceLocation;
    }
    @Override
    public boolean nightmareMode$isToggleZoomActive() {
        return nmToggleZoomActive;
    }

    @Override
    public boolean nightmareMode$isToggleZoomKeyHeld() {
        return Keyboard.isKeyDown(NightmareKeyBindings.nmZoomToggle.keyCode);
    }
    private float underworldFogAlpha = 0.0f;
    @Inject(method = "setupFog", at = @At("HEAD"),cancellable = true)
    private void doUnderworldFog(int par1, float par2, CallbackInfo ci){
        EntityLivingBase entity = this.mc.renderViewEntity;

        if (entity.dimension == NightmareMode.UNDERWORLD_DIMENSION && entity instanceof EntityPlayer p) {
            long worldTime = this.mc.theWorld.getWorldTime();
            long timeOfDay = worldTime % 24000L;

            float targetAlpha = Math.max(getTargetAlpha(timeOfDay), getPlayerSanityFogModifier(p));

            // Smooth interpolation over 120 ticks (6 seconds at 20 TPS)
            float fadeSpeed = 1.0f / 240.0f;
            if (this.underworldFogAlpha < targetAlpha) {
                this.underworldFogAlpha = Math.min(targetAlpha, this.underworldFogAlpha + fadeSpeed);
            } else if (this.underworldFogAlpha > targetAlpha) {
                this.underworldFogAlpha = Math.max(targetAlpha, this.underworldFogAlpha - fadeSpeed);
            }

            // Skip fog rendering entirely if alpha is effectively zero
            if (this.underworldFogAlpha < 0.001f) {
                // No fog during day - let vanilla fog handle it
                return;
            }

            // === FOG CALCULATIONS ===
            float timeFactor = (float) timeOfDay / 24000.0f;

            // Slower, gentler pulse
            float pulse = 0.008f * (float) Math.sin(worldTime * 0.015f);
            float warp = 0.004f * (float) Math.cos(entity.posY * 0.08f + worldTime * 0.012f);

            // Fog color with alpha blending
            float red   = 0.07f + 0.015f * (float) Math.sin(worldTime * 0.022f + par2 * 0.8f) + warp;
            float green = 0.015f + 0.008f * (float) Math.cos(worldTime * 0.018f + par2);
            float blue  = 0.04f  + 0.012f * (float) Math.sin(worldTime * 0.025f + par2) - warp;

            red   = MathHelper.clamp_float(red,   0.04f, 0.14f);
            green = MathHelper.clamp_float(green, 0.005f, 0.06f);
            blue  = MathHelper.clamp_float(blue,  0.025f, 0.10f);

            // Base density calculations
            float baseDensity = 0.048f + 0.012f * timeFactor;
            float density = baseDensity + pulse;
            density = MathHelper.clamp_float(density, 0.038f, 0.072f);

            // Apply alpha fade to density
            density *= this.underworldFogAlpha;

            // === GL STATE SETUP - Order matters! ===
            // 1. Set fog parameters BEFORE enabling fog
            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP2);
            GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(red, green, blue, 1.0f));
            GL11.glFogf(GL11.GL_FOG_DENSITY, density);

            int blockId = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, entity, par2);

            if (entity.isPotionActive(Potion.blindness)) {
                GL11.glFogf(GL11.GL_FOG_DENSITY, (0.14f + pulse * 1.5f) * this.underworldFogAlpha);
            }
            else if (blockId > 0 && Block.blocksList[blockId].blockMaterial == Material.water) {
                GL11.glFogf(GL11.GL_FOG_DENSITY, (0.09f + pulse * 0.8f) * this.underworldFogAlpha);
                GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(0.08f, 0.025f, 0.07f, 1.0f));
            }
            else if (blockId > 0 && Block.blocksList[blockId].blockMaterial == Material.lava) {
                GL11.glFogf(GL11.GL_FOG_DENSITY, (0.45f + pulse * 0.6f) * this.underworldFogAlpha);
                GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(0.18f, 0.06f, 0.03f, 1.0f));
            }
            else if (this.cloudFog) {
                GL11.glFogf(GL11.GL_FOG_DENSITY, (0.065f + pulse * 1.2f) * this.underworldFogAlpha);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                GL11.glFogi(0x855A, 0x855B);
            }

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glNormal3f(0.0f, -1.0f, 0.0f);

            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);

            GL11.glEnable(GL11.GL_FOG);

            ci.cancel();
        }
    }

    @Unique private float getPlayerSanityFogModifier(EntityPlayer p){
        double sanity = p.getData(SANITY);

        if (sanity >= MAX_SANITY){
            // player is fully insane
            return (float) Math.min((sanity - MAX_SANITY + 30) / 30, 16.0f);
        } else if(sanity > CRITICAL_SANITY) {
            // player is very insane, intense fog
            return (float) Math.min((sanity - CRITICAL_SANITY) / 30, 1.0);
        }

        return 0f;
    }

    @Unique
    private float getTargetAlpha(long timeOfDay) {
        float targetAlpha = 0.0f;

        if (timeOfDay >= 12542 && timeOfDay <= 23458) {
            // Nighttime - fog should be visible
            targetAlpha = 1.0f;
        } else if (timeOfDay >= 11542 && timeOfDay < 12542) {
            // Sunset fade-in (1000 ticks before night = ~50 seconds)
            // But we want 120 tick fade, so calculate accordingly
            float sunsetProgress = (timeOfDay - 11542) / 1000.0f;
            targetAlpha = Math.min(1.0f, sunsetProgress * (1000.0f / 240.0f));
        } else if (timeOfDay > 23458 && timeOfDay <= 24458) {
            // Sunrise fade-out (1000 ticks after night)
            float sunriseProgress = (timeOfDay - 23458) / 1000.0f;
            targetAlpha = Math.max(0.0f, 1.0f - sunriseProgress * (1000.0f / 240.0f));
        }
        return targetAlpha;
    }

    @Inject(method = "updateCameraAndRender", at = @At("HEAD"))
    private void nightmaremode$injectZoomCamera(float partialTicks, CallbackInfo ci) {
        if (mc == null || mc.thePlayer == null) return;

        boolean zoomKeyDown = Keyboard.isKeyDown(NightmareKeyBindings.nmZoomToggle.keyCode);
        // toggle
        if (zoomKeyDown && !nmToggleToggleZoomKeyWasDown && mc.currentScreen == null) {
            nmToggleZoomActive = !nmToggleZoomActive;
            targetZoom = nmToggleZoomActive ? 4.0D : 1.0D;
        }
        nmToggleToggleZoomKeyWasDown = zoomKeyDown;

        // Allow scroll wheel to set zoom only in the frame just activated
        if (nmToggleZoomActive && zoomKeyDown) {
            int wheel = Mouse.getDWheel();
            if (wheel != 0 && mc.currentScreen == null) {
                double step;
                if (targetZoom >= 12.0D) {
                    step = 2.0D;
                } else if (targetZoom >= 8.0D) {
                    step = 1.5D;
                } else if (targetZoom >= 4.0D) {
                    step = 1.0D;
                } else if (targetZoom >= 1.5D) {
                    step = 0.25D;
                } else {
                    step = 0.1D;
                }
                if (wheel > 0) {
                    targetZoom += step;
                } else {
                    targetZoom -= step;
                }
                if (targetZoom < 1.0D) targetZoom = 1.0D;
                if (targetZoom > 32.0D) targetZoom = 32.0D;
//                lastZoomLevel = targetZoom;
            }
        }
        double absDelta = Math.abs(targetZoom - cameraZoom);
        if (targetZoom > 1.0D) {
            double lerpSpeed = Math.min(0.04, Math.max(0.008, absDelta * 0.02));
            if (absDelta > 0.01D) {
                cameraZoom += (targetZoom - cameraZoom) * lerpSpeed;
            } else {
                cameraZoom = targetZoom;
            }
        } else {
            double lerpSpeed = Math.min(0.14, Math.max(0.03, absDelta * 0.10));
            if (absDelta > 0.001D) {
                cameraZoom += (targetZoom - cameraZoom) * lerpSpeed;
            } else {
                cameraZoom = targetZoom;
            }
        }
    }

    @Inject(method = "renderHand(FI)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V", shift = At.Shift.AFTER, remap = false))
    private void nightmaremode$scaleViewmodel(float partialTicks, int pass, CallbackInfo ci) {
        float zoom = (float) cameraZoom;
        if (zoom != 1.0F) {
            GL11.glScalef(zoom, zoom, zoom);
        }
    }

    @Inject(method = "getFOVModifier(FZ)F", at = @At("RETURN"), cancellable = true)
    private void nightmaremode$setZoomFOV(float par1, boolean par2, CallbackInfoReturnable<Float> cir) {
        float fov = cir.getReturnValue();
        float zoom = (float) cameraZoom;
        if (zoom != 1.0F) {
            cir.setReturnValue(fov / zoom);
        }
    }

    /**
     * Force render hand when zoomed by injecting after the zoom check
     */
    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glClear(I)V", shift = At.Shift.AFTER, remap = false))
    private void nightmaremode$forceRenderHandWhenZoomed(float partialTicks, long timeSlice, CallbackInfo ci) {
        // Check if we're in a zoom state and the normal hand rendering was skipped
        if (this.cameraZoom != 1.0D) {
            // Render hand manually when zoomed
            this.nightmaremode$renderHandForZoom(partialTicks, 0);
        }
    }

    /**
     * Custom hand rendering method for zoom
     */
    @Unique
    private void nightmaremode$renderHandForZoom(float partialTicks, int pass) {
        if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping() && !this.mc.gameSettings.hideGUI && !this.mc.playerController.enableEverythingIsScrewedUpMode()) {
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            float var3 = 0.07F;
            if (this.mc.gameSettings.anaglyph) {
                GL11.glTranslatef((float)(-(pass * 2 - 1)) * var3, 0.0F, 0.0F);
            }

            if (this.cameraZoom != 1.0D) {
                GL11.glTranslatef((float)0, (float)0, 0.0F);
                GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
            }

            Project.gluPerspective(this.getFOVModifier(partialTicks, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);

            if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                float var4 = 0.6666667F;
                GL11.glScalef(1.0F, var4, 1.0F);
            }

            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            if (this.mc.gameSettings.anaglyph) {
                GL11.glTranslatef((float)(pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            GL11.glPushMatrix();
            this.hurtCameraEffect(partialTicks);
            if (this.mc.gameSettings.viewBobbing) {
                this.setupViewBobbing(partialTicks);
            }

            this.enableLightmap(partialTicks);
            this.itemRenderer.renderItemInFirstPerson(partialTicks);
            this.disableLightmap(partialTicks);

            GL11.glPopMatrix();

            if (!this.mc.renderViewEntity.isPlayerSleeping()) {
                this.itemRenderer.renderOverlays(partialTicks);
                this.hurtCameraEffect(partialTicks);
            }

            if (this.mc.gameSettings.viewBobbing) {
                this.setupViewBobbing(partialTicks);
            }
        }
    }

    @ModifyArgs(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityClientPlayerMP;setAngles(FF)V", ordinal = 0))
    private void slowSmoothCameraInWeb(Args args) {
        Entity player = this.mc.thePlayer;
        boolean inWeb = false;
        try {
            inWeb = ((EntityAccessor) player).getIsInWeb();
        } catch (Throwable t) {
            // Use reflection as a fallback
            try {
                Field f = player.getClass().getDeclaredField("isInWeb");
                f.setAccessible(true);
                inWeb = f.getBoolean(player);
            } catch (Throwable tt) {
                // Ignore, don't crash if field missing
            }
        }
        if (inWeb) {
            for (int i = 0; i < args.size(); i++) {
                Object arg = args.get(i);
                if (arg instanceof Float) {
                    args.set(i, (Float) arg * 0.25F);
                } else if (arg instanceof Number) {
                    args.set(i, ((Number) arg).floatValue() * 0.25F);
                }
            }
        }
    }

    @ModifyArgs(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityClientPlayerMP;setAngles(FF)V", ordinal = 1))
    private void slowCameraInWeb(Args args) {
        Entity player = this.mc.thePlayer;
        boolean inWeb = false;
        try {
            inWeb = ((EntityAccessor) player).getIsInWeb();
        } catch (Throwable t) {
            // Use reflection as a fallback
            try {
                Field f = player.getClass().getDeclaredField("isInWeb");
                f.setAccessible(true);
                inWeb = f.getBoolean(player);
            } catch (Throwable tt) {
                // Ignore, don't crash if field missing
            }
        }
        if (inWeb) {
            for (int i = 0; i < args.size(); i++) {
                Object arg = args.get(i);
                if (arg instanceof Float) {
                    args.set(i, (Float) arg * 0.25F);
                } else if (arg instanceof Number) {
                    args.set(i, ((Number) arg).floatValue() * 0.25F);
                }
            }
        }
    }

    @Unique private int bloodMoonFadeTracker = 1;
    @ModifyArg(method = "modUpdateLightmapOverworld", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureUtil;uploadTexture(I[III)V"),index = 1)
    private int[] manageCustomColorEvents(int[] par1ArrayOfInteger){
        if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
            if (this.mc.thePlayer.dimension == 1) {
                return nightvisionEnd();
            } else {
                return nightvisionFullbright();
            }
        }


        if(NMUtils.getIsBloodMoon()){
            if(NightmareMode.bloodmoonColors){
                if(bloodMoonFadeTracker < 800){
                    bloodMoonFadeTracker++;
                }

                float t = Math.min(1f, bloodMoonFadeTracker / 800f);

                final float target = 80f;

                for (int i = 0; i < 256; ++i) {
                    int color = par1ArrayOfInteger[i];

                    int a = (color >> 24) & 0xFF;
                    int r = (color >> 16) & 0xFF;
                    int g = (color >> 8) & 0xFF;
                    int b = color & 0xFF;



                    int newR = r;
                    int newG = g;
                    int newB = b;

                    if(r > 80){
                        newR = Math.round(r * (1f - t) + target * t);

                    }
                    if(g > 80){
                        newG = Math.round(g * (1f - t) + target * t);

                    }
                    if(b > 80){
                        newB = Math.round(b * (1f - t) + target * t);
                    }
                    // clamp to [0,255]
                    newR = Math.max(0, Math.min(255, newR));
                    newG = Math.max(0, Math.min(255, newG));
                    newB = Math.max(0, Math.min(255, newB));

                    par1ArrayOfInteger[i] = (a << 24) | (newR << 16) | (newG << 8) | newB;
                }

                return par1ArrayOfInteger;
            }
        } else{
            bloodMoonFadeTracker = 1;
        }

        return par1ArrayOfInteger;
    }

    @Inject(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glClearColor(FFFF)V", remap = false))
    private void manageEndFogWithNightVision(float par1, CallbackInfo ci){
        if (this.mc.thePlayer.dimension == 1) {
            this.fogColorRed = 0;
            this.fogColorBlue = 0;
            this.fogColorGreen= 0;
        }
        // if anaglyph:
        // r = 0.178771923f
        // g = 0.178771923f
        // b = 0.195104557f
    }

    @Redirect(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;isPotionActive(Lnet/minecraft/src/Potion;)Z"))
    private boolean noNightvisionRedFog(EntityLivingBase instance, Potion par1Potion){
        return false;
    }

    @Redirect(method = "updateLightmap", at = @At(value = "FIELD", target = "Lnet/minecraft/src/GameSettings;gammaSetting:F"))
    private float activateFullbright(GameSettings instance){
        return NightmareMode.fullBright ? 16f : instance.gammaSetting;
    }

    @Redirect(method = "modUpdateLightmapOverworld", at = @At(value = "FIELD", target = "Lnet/minecraft/src/GameSettings;gammaSetting:F"))
    private float activateFullbright0(GameSettings instance){
        return NightmareMode.fullBright ? 16f : instance.gammaSetting;
    }

    @Unique
    private static int[] nightvisionEnd(){
        int[] numbers = new int[256];
        Arrays.fill(numbers,-10197916);
        return numbers;
    }

    @Unique
    private static int[] nightvisionFullbright(){
        int[] numbers = new int[256];
        Arrays.fill(numbers,-1);
        return numbers;
    }
}