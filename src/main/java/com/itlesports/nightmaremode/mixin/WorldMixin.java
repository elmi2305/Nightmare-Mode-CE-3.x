package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
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

    @Shadow public abstract long getWorldTime();

    @Unique private boolean test = false;

    @Inject(method = "isBoundingBoxBurning", at = @At("RETURN"),cancellable = true)
    private void manageBurningItemImmunity(Entity entity, CallbackInfoReturnable<Boolean> cir){
        if(entity instanceof EntityItem item && ((Objects.equals(item.getEntityName(), "item.item.magmaCream")) || Objects.equals(item.getEntityName(), "item.item.blazeRod"))){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "computeOverworldSunBrightnessWithMoonPhases", at = @At("RETURN"),remap = false, cancellable = true)
    private void manageGloomPostWither(CallbackInfoReturnable<Float> cir){
        World thisObj = (World)(Object)this;
        if(NightmareUtils.getWorldProgress(thisObj) == 2 && !thisObj.isDaytime()){cir.setReturnValue(0f);}
    }
    @Inject(method = "updateWeather", at = @At("TAIL"))
    private void manageRainAndBloodMoon(CallbackInfo ci){
        if(this.getTotalWorldTime() < 120000){
            this.worldInfo.setRaining(false);
        }
        if (!MinecraftServer.getIsServer()) {
            World thisObj = (World)(Object)this;
            if(thisObj.getWorldTime() != thisObj.getTotalWorldTime() && !thisObj.worldInfo.areCommandsAllowed()){
                thisObj.setWorldTime(thisObj.getTotalWorldTime());
            }
            int dawnOffset = this.isDawnOrDusk(thisObj.getWorldTime());

            if (this.getIsBloodMoon(thisObj,((int)Math.ceil((double) thisObj.getWorldTime() / 24000)) + dawnOffset) && !this.test && !thisObj.isDaytime()) {
                NightmareMode.setBloodMoonTrue();
                this.test = true;
            }
            if (thisObj.isDaytime()) {
                NightmareMode.setBloodMoonFalse();
            }
            if(!NightmareMode.bloodNightmare){
                int dayCount = ((int)Math.ceil((double) thisObj.getWorldTime() / 24000)) + dawnOffset;

                if(!this.getIsBloodMoon(thisObj,dayCount)){
                    NightmareMode.setBloodMoonFalse();
                }
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
        if(NightmareUtils.getWorldProgress(world) == 0){return NightmareMode.bloodNightmare;}
        return world.getMoonPhase() == 0 && (dayCount % 16 == 9) || NightmareMode.bloodNightmare;
    }
}
