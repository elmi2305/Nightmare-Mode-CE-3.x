package com.itlesports.nightmaremode.mixin.render;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.client.NightmareKeyBindings;
import com.itlesports.nightmaremode.client.ZoomStateAccessor;
import com.itlesports.nightmaremode.mixin.EntityAccessor;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Arrays;

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


    private static final ResourceLocation BLOOD_RAIN = new ResourceLocation("textures/entity/nmBloodRain.png");

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

            org.lwjgl.util.glu.Project.gluPerspective(this.getFOVModifier(partialTicks, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);

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

    // MEA CODE. credit to Pot_tx
    @ModifyArgs(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityClientPlayerMP;setAngles(FF)V", ordinal = 0))
    private void slowSmoothCameraInWeb(Args args) {
        Entity player = this.mc.thePlayer;
        boolean inWeb = false;
        try {
            inWeb = ((EntityAccessor) player).getIsInWeb();
        } catch (Throwable t) {
            // Use reflection as a fallback
            try {
                java.lang.reflect.Field f = player.getClass().getDeclaredField("isInWeb");
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
                java.lang.reflect.Field f = player.getClass().getDeclaredField("isInWeb");
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

    @ModifyArg(method = "modUpdateLightmapOverworld", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureUtil;uploadTexture(I[III)V"),index = 1)
    private int[] manageNightvisionColor(int[] par1ArrayOfInteger){
        if(NMUtils.getIsBloodMoon() && NightmareMode.bloodmoonColors) {
            return bloodmoonForcedBrightness();
        } else if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
            if(this.mc.thePlayer.dimension == 1){
                return nightvisionEnd();
            } else {
                return nightvisionFullbright();
            }
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
    @Unique
    private static int[] bloodmoonForcedBrightness(){
        int[] numbers = new int[256];
        Arrays.fill(numbers,-12829636); // slightly brighter bloodmoon
        // 255 << 24 | 60 << 16 | 60 << 8 | 60

//        Arrays.fill(numbers,-14145496);
        // 255 << 24 | 40 << 16 | 40 << 8 | 40
        return numbers;
    }
}