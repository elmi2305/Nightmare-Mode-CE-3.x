package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Random;

@Mixin(World.class)
public abstract class WorldMixin {
    @Shadow public abstract long getTotalWorldTime();

    @Shadow public Random rand;

    @Shadow public WorldInfo worldInfo;

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

    @Inject(method = "computeOverworldSunBrightnessWithMoonPhases", at = @At("RETURN"),remap = false, cancellable = true)
    private void manageGloomPostWither(CallbackInfoReturnable<Float> cir){
        World thisObj = (World)(Object)this;
        if(NightmareUtils.getWorldProgress(thisObj) == 2 && !thisObj.isDaytime() && !NightmareUtils.getIsBloodMoon()){cir.setReturnValue(0f);}
    }
    @Inject(method = "updateWeather", at = @At("TAIL"))
    private void manageRainAndBloodMoon(CallbackInfo ci){
        if(this.getTotalWorldTime() < 120000){
            this.worldInfo.setRaining(false);
        }
        if (!MinecraftServer.getIsServer()) {
            World thisObj = (World)(Object)this;
            int dawnOffset = this.isDawnOrDusk(thisObj.getWorldTime());

            if(!NightmareMode.bloodmare){
                NightmareMode.setBloodmoon(this.getIsBloodMoon(thisObj, ((int) Math.ceil((double) thisObj.getWorldTime() / 24000)) + dawnOffset));
            } else{
                NightmareMode.setBloodmoon(this.getIsNight(thisObj));
            }

            if(!NightmareMode.eclipsed){
                NightmareMode.setEclipse(this.getIsEclipse(thisObj, ((int) Math.ceil((double) thisObj.getWorldTime() / 24000)) + dawnOffset));
            } else{
                NightmareMode.setEclipse(!this.getIsNight(thisObj));
            }
        }
    }

    @Unique
    private int isDawnOrDusk(long time){
        if(time % 24000 >= 23459) {
            return 1;
        }
        return 0;
    }

    @Unique private boolean getIsBloodMoon(World world, int dayCount){
//        if(NightmareUtils.getWorldProgress(world) == 0){return false;}
        // TODO don't forget to remove this debug after testing
        return this.getIsNight(world) && (world.getMoonPhase() == 0  && (dayCount % 16 == 9)) || NightmareMode.bloodmare;
    }

    @Unique private boolean getIsEclipse(World world, int dayCount){
//        if(NightmareUtils.getWorldProgress(world) <= 2){return false;}
        return !this.getIsNight(world);
//        return !this.getIsNight(world) && (dayCount % 12 == 8) || NightmareMode.eclipsed;
    }

    @Unique private boolean getIsNight(World world){
        return world.getWorldTime() % 24000 >= 12541 && world.getWorldTime() % 24000 <= 23459;
    }
    @Inject(method = "isDaytime", at = @At("HEAD"),cancellable = true)
    private void eclipseNightTime(CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsEclipse()){
            cir.setReturnValue(false);
        }
    }

//    @ModifyArgs(method = "getSkyColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Vec3Pool;getVecFromPool(DDD)Lnet/minecraft/src/Vec3;"))
//    private void manageBackgroundColorInEclipse(Args args){
//        if (NightmareUtils.getIsEclipse()) {
//            args.set(0,0.0f);
//            args.set(1,0.0f);
//            args.set(2,0.0f);
//        System.out.println(args.get(0) + ", " + args.get(1) + ", " + args.get(2));
//        }
//    }
    @Inject(method = "getSkyColor", at = @At("HEAD"),cancellable = true)
    private void manageEclipseSkyColor(Entity par1Entity, float par2, CallbackInfoReturnable<Vec3> cir){
        World thisObj = (World)(Object)this;
        if (NightmareUtils.getIsEclipse()) {
            cir.setReturnValue(thisObj.getWorldVec3Pool().getVecFromPool(0.0, 0.0, 0.0));
        }
    }
}
