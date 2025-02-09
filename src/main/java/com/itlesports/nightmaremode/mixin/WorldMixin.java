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

import java.util.Objects;
import java.util.Random;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow public abstract long getTotalWorldTime();
    @Shadow public Random rand;
    @Shadow public WorldInfo worldInfo;

    @Shadow public abstract long getWorldTime();

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
        if(this.getWorldTime() % 200 == 0 && NightmareMode.nite){
            NightmareMode.setNiteMultiplier(this.calculateNiteMultiplier());
            if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.isSneaking()) {
                ChatMessageComponent text2 = new ChatMessageComponent();
                text2.addText("[debug] current nite multiplier: " + NightmareUtils.getNiteMultiplier());
                text2.setColor(EnumChatFormatting.WHITE);
                Minecraft.getMinecraft().thePlayer.sendChatToPlayer(text2);
            }
        }
    }
    @ModifyConstant(method = "updateWeather", constant = @Constant(intValue = 12000))
    private int increaseDurationBetweenRain(int constant){
        return 24000;
    }

    @Unique
    private double calculateNiteMultiplier(){
        return 1 + ((double) this.getWorldTime() / 20000) * 0.01;
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
        if (NightmareUtils.getIsEclipse()) {
            cir.setReturnValue(thisObj.getWorldVec3Pool().getVecFromPool(0.0, 0.0, 0.0));
        }
    }
}
