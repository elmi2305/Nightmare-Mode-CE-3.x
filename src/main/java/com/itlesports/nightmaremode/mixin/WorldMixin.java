package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.BTWSquidEntity;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow public abstract long getTotalWorldTime();
    @Shadow public Random rand;
    @Shadow public WorldInfo worldInfo;
    @Shadow public abstract long getWorldTime();


    @Unique private static float fogHue = 0.0f; // Start hue at 0

    @Unique private static Vec3 getRainbowFogColor() {
        fogHue += 0.001f; // Adjust speed for smoother/faster transitions
        if (fogHue > 1.0f) fogHue -= 1.0f; // Loop hue back to 0 when it exceeds 1

        // Convert HSB to RGB
        Color color = Color.getHSBColor(fogHue, 1.0f, 1.0f);

        // Normalize RGB values (0-255) to Minecraft's 0-1 range
        return Vec3.createVectorHelper(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }
    @Unique private static Vec3 getRainbowFogColorFromWorldTime(long worldTime) {
        float fogHue = (worldTime % 24000) / 24000.0f; // Normalize worldTime to range [0, 1] over a full Minecraft day

        // Convert HSB to RGB
        Color color = Color.getHSBColor(fogHue, 1.0f, 1.0f);

        // Normalize RGB values (0-255) to Minecraft's 0-1 range
        return Vec3.createVectorHelper(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }

    @Unique private static Vec3 getRainbowSkyColor() {
        fogHue += 0.0005f; // Adjust speed for smoother/faster transitions
        if (fogHue > 1.0f) fogHue -= 1.0f; // Loop hue back to 0 when it exceeds 1

        // Convert HSB to RGB
        Color color = Color.getHSBColor(fogHue, 1.0f, 0.5f);

        // Normalize RGB values (0-255) to Minecraft's 0-1 range
        return Vec3.createVectorHelper(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }

    @Unique private static Vec3 getRainbowSkyColorFromWorldTime(long worldTime) {
        float fogHue = (worldTime % 24000) / 24000.0f; // Normalize worldTime to range [0, 1] over a full Minecraft day

        // Convert HSB to RGB
        Color color = Color.getHSBColor(fogHue, 1.0f, 1.0f);

        // Normalize RGB values (0-255) to Minecraft's 0-1 range
        return Vec3.createVectorHelper(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }


    @Inject(method = "getFogColor", at = @At("RETURN"), cancellable = true)
    private void changeFogColor(CallbackInfoReturnable<Vec3> cir){
        Vec3 color = cir.getReturnValue();
        if(NightmareMode.isAprilFools){
            cir.setReturnValue(getRainbowFogColorFromWorldTime(this.getWorldTime()));
        } else {
            if (NightmareUtils.getIsEclipse()) {
                color.scale(0);
                cir.setReturnValue(color);
            }
            if (NightmareUtils.getIsBloodMoon()) {
                double x = color.xCoord;
                double y = color.yCoord;
                double z = color.zCoord;

                // Target ratio
                double rx = 0.50196;
                double ry = 0.06359;
                double rz = 0.03591;

                // Normalize the ratio
                double ratioMagnitude = Math.sqrt(rx * rx + ry * ry + rz * rz);
                double normX = rx / ratioMagnitude;
                double normY = ry / ratioMagnitude;
                double normZ = rz / ratioMagnitude;

                // Compute the magnitude of the original vector
                double vMagnitude = Math.sqrt(x * x + y * y + z * z);

                // Scale the normalized ratio to match the original vector's magnitude
                double newX = normX * vMagnitude;
                double newY = normY * vMagnitude;
                double newZ = normZ * vMagnitude;

                color.setComponents(newX, newY, newZ);
            }
        }
    }




    @Inject(method = "isBoundingBoxBurning", at = @At("RETURN"),cancellable = true)
    private void manageBurningItemImmunity(Entity entity, CallbackInfoReturnable<Boolean> cir){
        if(entity instanceof EntityItem item
                && (
                Objects.equals(item.getEntityItem().itemID, Item.magmaCream.itemID)
                        || Objects.equals(item.getEntityItem().itemID, Item.blazeRod.itemID)
                        || Objects.equals(item.getEntityItem().itemID, NMItems.bloodOrb.itemID)
        )
        ){
            cir.setReturnValue(false);
        }
    }


    @Inject(method = "handleMaterialAcceleration", at = @At("HEAD"),cancellable = true)
    private void manageSquidNoGravityWater(AxisAlignedBB par1AxisAlignedBB, Material par2Material, Entity par3Entity, CallbackInfoReturnable<Boolean> cir){
        if(par3Entity instanceof BTWSquidEntity && (NightmareUtils.getIsMobEclipsed((BTWSquidEntity) par3Entity) || NightmareMode.buffedSquids) && par2Material == Material.water){
            par3Entity.inWater = true;
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "computeOverworldSunBrightnessWithMoonPhases", at = @At("RETURN"),remap = false, cancellable = true)
    private void manageGloomPostWither(CallbackInfoReturnable<Float> cir){
        World thisObj = (World)(Object)this;
        if(NightmareUtils.getWorldProgress(thisObj) == 2 && !thisObj.isDaytime() && !NightmareUtils.getIsBloodMoon()){cir.setReturnValue(0f);}
    }

    @Inject(method = "computeOverworldSunBrightnessWithMoonPhases", at = @At("TAIL"),cancellable = true)
    private void setEclipseTargetLightLevel(CallbackInfoReturnable<Float> cir){
        if(NightmareUtils.getIsEclipse()){
            cir.setReturnValue(cir.getReturnValueF() * 0.4f);
        }
    }
    @Inject(method = "updateWeather", at = @At("TAIL"))
    private void manageRainAndBloodMoon(CallbackInfo ci){
        if(this.getTotalWorldTime() < 140000){
            this.worldInfo.setRaining(false);
        }
        if(this.getWorldTime() % 300 == 0 && NightmareMode.nite){
            NightmareMode.setNiteMultiplier(this.calculateNiteMultiplier());
        }
    }
    @ModifyConstant(method = "updateWeather", constant = @Constant(intValue = 12000))
    private int increaseDurationBetweenRain(int constant){
        return 24000;
    }

    @Unique
    private double calculateNiteMultiplier(){
        int progress = NightmareUtils.getWorldProgress((World)(Object)this);
//        double baseInterval = 6000 * Math.log((4 - progress) * 10); // 22133 -> 20407 -> 17974
        double baseInterval = 14000 + 6000 * Math.log(Math.max(4 - progress, 1)) / Math.log(4); // from 20000 to 14000

        return 1 + ((double) this.getWorldTime() / baseInterval) * 0.01;
    }

    @Inject(method = "isDaytime", at = @At("HEAD"),cancellable = true)
    private void eclipseNightTime(CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsEclipse()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getSkyColor", at = @At("HEAD"),cancellable = true)
    private void manageEclipseSkyColor(Entity par1Entity, float par2, CallbackInfoReturnable<Vec3> cir){
        World thisObj = (World)(Object)this;
        if(NightmareMode.isAprilFools){
            cir.setReturnValue(getRainbowSkyColorFromWorldTime(this.getWorldTime()));
        } else if (NightmareUtils.getIsEclipse()) {
            cir.setReturnValue(thisObj.getWorldVec3Pool().getVecFromPool(0.0, 0.0, 0.0));
        }
    }
}
