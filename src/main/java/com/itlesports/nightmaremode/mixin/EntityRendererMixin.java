package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Arrays;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin implements EntityAccessor {
    @Shadow private Minecraft mc;
    @Mutable
    @Shadow float fogColorRed;
    @Shadow float fogColorBlue;
    @Shadow float fogColorGreen;

    // MEA CODE. credit to Pot_tx
    @ModifyArgs(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityClientPlayerMP;setAngles(FF)V", ordinal = 0))
    private void slowSmoothCameraInWeb(Args args) {
        if (((EntityAccessor)this.mc.thePlayer).getIsInWeb()) {
            args.set(0, (float) args.get(0) * 0.25F);
            args.set(1, (float) args.get(1) * 0.25F);
        }
    }

    @ModifyArgs(method = "updateCameraAndRender(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityClientPlayerMP;setAngles(FF)V", ordinal = 1))
    private void slowCameraInWeb(Args args) {
        if (((EntityAccessor)this.mc.thePlayer).getIsInWeb()) {
            args.set(0, (float) args.get(0) * 0.25F);
            args.set(1, (float) args.get(1) * 0.25F);
        }
    }
    @ModifyArg(method = "modUpdateLightmapOverworld", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureUtil;uploadTexture(I[III)V"),index = 1)
    private int[] manageNightvisionColor(int[] par1ArrayOfInteger){
        if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
            if(this.mc.thePlayer.dimension == 1){
                return getArray();
            } else {
                if(NightmareUtils.getIsBloodMoon()) {
                    return getArrayBloodMoon();
                }
                return getArray1();
            }
        }

        return par1ArrayOfInteger;
    }

//    @Inject(method = "modUpdateLightmapOverworld", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/TextureUtil;uploadTexture(I[III)V"))
//    private void removeNightVisionRed(WorldClient world, float fPartialTicks, CallbackInfo ci){
//        for (int iTempMapIndex = 0; iTempMapIndex < 256; ++iTempMapIndex) {
//            if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
//                if(this.mc.thePlayer.dimension == 1){
//                    this.lightmapColors[iTempMapIndex] = -10197916;
//                } else {
//                    this.lightmapColors[iTempMapIndex] = -1;
//                }
//            }
//            if(NightmareUtils.getIsBloodMoon()) {
//                this.lightmapColors[iTempMapIndex] = -14145496;
//            }
//        }
//    }

    @Redirect(method = "getNightVisionBrightness", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PotionEffect;getDuration()I"))
    private int noCrashGetDuration(PotionEffect potion){
        if(potion == null){
            return 200;
        }
        return potion.getDuration();
    }
    @Redirect(method = "getNightVisionBrightness", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/PotionEffect;getIsAmbient()Z"))
    private boolean noCrashGetIsAmbient(PotionEffect potion){
        if(potion == null){
            return true;
        }
        return potion.getIsAmbient();
    }


    @Inject(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glClearColor(FFFF)V"))
    private void manageEndFogWithNightVision(float par1, CallbackInfo ci){
        if (this.mc.thePlayer.dimension == 1) {
            this.fogColorRed = 0;
            this.fogColorBlue = 0;
            this.fogColorGreen= 0;
        }
        if(NightmareUtils.getIsBloodMoon()){
            this.fogColorRed = 0.50196075f;
            this.fogColorBlue = 0.06359476f;
            this.fogColorGreen= 0.03591233f;
        }
        // if anaglyph:
        // red = 0.1787719233f
        // grn = 0.1787719233f
        // blu = 0.195104557f
    }

//    @Inject(method = "modUpdateLightmap",
//            at = @At(value = "INVOKE",
//                    target = "Lnet/minecraft/src/EntityRenderer;modUpdateLightmapOverworld(Lnet/minecraft/src/WorldClient;F)V",
//                    ordinal = 1,
//                    shift = At.Shift.AFTER),
//            locals = LocalCapture.CAPTURE_FAILHARD)
//    private void manageEndGloom(float fPartialTicks, CallbackInfo ci, WorldClient world){
//        if(world.provider.dimensionId == 1){
//            this.updateLightmap(fPartialTicks);
//        }
//    }
//    REMOVES END GLOOM

    @Unique
    private static int[] getArray(){
        int[] numbers = new int[256];
        Arrays.fill(numbers,-10197916);
        return numbers;
    }

    @Unique
    private static int[] getArray1(){
        int[] numbers = new int[256];
        Arrays.fill(numbers,-1);
        return numbers;
    }
    @Unique
    private static int[] getArrayBloodMoon(){
        int[] numbers = new int[256];
        Arrays.fill(numbers,-12829636); // slightly brighter bloodmoon
        // 255 << 24 | 60 << 16 | 60 << 8 | 60

//        Arrays.fill(numbers,-14145496);
        // 255 << 24 | 40 << 16 | 40 << 8 | 40
        return numbers;
    }
}
