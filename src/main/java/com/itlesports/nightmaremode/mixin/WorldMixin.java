package com.itlesports.nightmaremode.mixin;

import api.world.data.DataEntry;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.BTWSquidEntity;
import com.itlesports.nightmaremode.entity.underworld.EntityVoidSquid;
import com.itlesports.nightmaremode.util.elements.LogSettings;
import com.itlesports.nightmaremode.util.NMConfUtils;
import com.itlesports.nightmaremode.util.NMUtils;
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
import java.util.Random;

import static com.itlesports.nightmaremode.util.NMFields.POSTWITHER;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow public Random rand;
    @Shadow public WorldInfo worldInfo;
    @Shadow public abstract long getWorldTime();
    @Shadow public boolean isRemote;
    @Shadow public abstract void setRainStrength(float par1);
    @Shadow protected float thunderingStrength;
    @Shadow public abstract boolean isThundering();


    @Shadow public abstract <T> void setData(DataEntry.WorldDataEntry<T> worldDataEntry, T t);

    @Shadow
    public abstract TileEntity getBlockTileEntity(int par1, int par2, int par3);

    @Shadow
    public abstract EntityPlayer getClosestPlayer(double par1, double par3, double par5, double par7);

    @Inject(method = "isRaining", at = @At("HEAD"),cancellable = true)
    private void bloodMoonRain(CallbackInfoReturnable<Boolean> cir) {
        if (NMUtils.getIsBloodMoon() && (!this.isThundering())) {
            this.setRainStrength(1.0f);
            this.thunderingStrength = 1.0f;
            cir.setReturnValue(true);
        }
    }

    @Unique private static Vec3 getRainbowFogColorFromWorldTime(long worldTime) {
        float fogHue = (worldTime % 24000) / 24000.0f;

        // convert HSB to RGB
        Color color = Color.getHSBColor(fogHue, 1.0f, 1.0f);

        return Vec3.createVectorHelper(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }

    @Unique private static Vec3 getRainbowSkyColorFromWorldTime(long worldTime) {
        float fogHue = (worldTime % 24000) / 24000.0f;

        // convert HSB to RGB
        Color color = Color.getHSBColor(fogHue, 1.0f, 1.0f);

        // normalize RGB values (0-255) to 0-1 range
        return Vec3.createVectorHelper(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void setDataOnInit(WorldSettings par1WorldSettings, CallbackInfo ci){
        this.setData(NightmareMode.CONFIGS_CREATED, NMConfUtils.getClientConfigData());
    }

    @Inject(method = "getFogColor", at = @At("RETURN"), cancellable = true)
    private void changeFogColor(CallbackInfoReturnable<Vec3> cir){
        Vec3 color = cir.getReturnValue();
        if(NightmareMode.isAprilFools){
            cir.setReturnValue(getRainbowFogColorFromWorldTime(this.getWorldTime()));
        } else {
            if (NMUtils.getIsEclipse()) {
                color.scale(0);
                cir.setReturnValue(color);
            }
            if (NMUtils.getIsBloodMoon()) {
                double x = color.xCoord;
                double y = color.yCoord;
                double z = color.zCoord;

                // target ratio
                double rx = 0.50196;
                double ry = 0.06359;
                double rz = 0.03591;

                // normalize the ratio
                double ratioMagnitude = Math.sqrt(rx * rx + ry * ry + rz * rz);
                double normX = rx / ratioMagnitude;
                double normY = ry / ratioMagnitude;
                double normZ = rz / ratioMagnitude;

                // compute the magnitude of the original vector
                double vMagnitude = Math.sqrt(x * x + y * y + z * z);

                // scale the normalized ratio to match the original vector's magnitude
                double newX = normX * vMagnitude;
                double newY = normY * vMagnitude;
                double newZ = normZ * vMagnitude;

                color.setComponents(newX, newY, newZ);
                cir.setReturnValue(color);
            }
        }
    }




    @Inject(method = "isBoundingBoxBurning", at = @At("RETURN"),cancellable = true)
    private void manageBurningItemImmunity(Entity entity, CallbackInfoReturnable<Boolean> cir){
        if(entity instanceof EntityItem item) {
            int itemID = item.getEntityItem().itemID;
            if (itemID == Item.magmaCream.itemID || itemID == Item.blazeRod.itemID || itemID == NMItems.bloodOrb.itemID) {
                cir.setReturnValue(false);
            }
        }
    }


    @Inject(method = "handleMaterialAcceleration", at = @At("HEAD"),cancellable = true)
    private void manageSquidNoGravityWater(AxisAlignedBB par1AxisAlignedBB, Material par2Material, Entity entity, CallbackInfoReturnable<Boolean> cir){
        if((entity instanceof BTWSquidEntity && (NMUtils.getIsMobEclipsed((BTWSquidEntity) entity) || NightmareMode.buffedSquids) || entity instanceof EntityVoidSquid) && par2Material == Material.water){
            entity.inWater = true;
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "computeOverworldSunBrightnessWithMoonPhases", at = @At("RETURN"),remap = false, cancellable = true)
    private void manageGloomPostWither(CallbackInfoReturnable<Float> cir){
        World thisObj = (World)(Object)this;
        if(NMUtils.getWorldProgress() == POSTWITHER && !thisObj.isDaytime() && !NMUtils.getIsBloodMoon()){cir.setReturnValue(0f);}
    }

    @Inject(method = "computeOverworldSunBrightnessWithMoonPhases", at = @At("TAIL"),cancellable = true)
    private void setEclipseTargetLightLevel(CallbackInfoReturnable<Float> cir){
        if(NMUtils.getIsEclipse()){
            cir.setReturnValue(cir.getReturnValueF() * 0.4f);
        }
    }

    @Inject(method = "updateWeather", at = @At("TAIL"))
    private void manageRainAndBloodMoon(CallbackInfo ci){
        if (!NightmareMode.darkStormyNightmare && !NightmareMode.devMode && !this.worldInfo.areCommandsAllowed()) {
            if(this.getWorldTime() < 140000){
                this.worldInfo.setRaining(false);
            }
        }
        if(this.getWorldTime() % 300 == 0 && NightmareMode.nite){
            NightmareMode.setNiteMultiplier(this.calculateNiteMultiplier());
        }
    }

    @Unique
    private double calculateNiteMultiplier(){
        int progress = NMUtils.getWorldProgress();
        double baseInterval = 14000 + 6000 * Math.log(Math.max(4 - progress, 1)) / Math.log(4); // from 20000 to 14000

        return 1 + ((double) this.getWorldTime() / baseInterval) * 0.01;
    }


    @Inject(method = "isDaytime", at = @At("HEAD"),cancellable = true)
    private void eclipseNightTime(CallbackInfoReturnable<Boolean> cir){
        if(NMUtils.getIsEclipse()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getSkyColor", at = @At("HEAD"),cancellable = true)
    private void manageEclipseSkyColor(Entity par1Entity, float par2, CallbackInfoReturnable<Vec3> cir){
        World thisObj = (World)(Object)this;
        if(NightmareMode.isAprilFools){
            cir.setReturnValue(getRainbowSkyColorFromWorldTime(this.getWorldTime()));
        } else if (NMUtils.getIsEclipse()) {
            cir.setReturnValue(thisObj.getWorldVec3Pool().getVecFromPool(0.0, 0.0, 0.0));
        }
    }

    @Unique
    private static String simpleClassName(String name) {
        int index = name.lastIndexOf('.');
        return index >= 0 ? name.substring(index + 1) : name;
    }
    @Inject(method = "removeBlockTileEntity", at = @At("HEAD"))
    private void logTileEntityDestruction(int x, int y, int z, CallbackInfo ci){
        if(NightmareMode.getInstance().isGriefLogging() && !this.isRemote){
            TileEntity te = this.getBlockTileEntity(x, y, z);
            LogSettings ls = NightmareMode.getInstance().getLogSettings();
            if(!ls.logIndirectBreaks) return;

            String text = simpleClassName(te.getClass().getName()) + " destroyed at " + x + " " + y + " " + z + ". Nearest Player: " + this.getClosestPlayer(x,y,z, 32).username;

            if(ls.logAllTileEntities){
                NightmareMode.appendLogLine(text);
                return;
            }
            if(ls.logChests && !ls.logContainers && te instanceof TileEntityChest){
                NightmareMode.appendLogLine(text);
                return;
            }
        }
    }
}
