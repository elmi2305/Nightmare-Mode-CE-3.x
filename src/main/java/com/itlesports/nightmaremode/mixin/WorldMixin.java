package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
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
        World thisObj = (World)(Object)this;

        if(NightmareMode.getInstance().isBloodMoon != null && NightmareMode.getInstance().isBloodMoon){
            NightmareMode.setBloodMoonTrue();
        } else {
            if (this.getIsBloodMoon(thisObj) && !this.test && !thisObj.isDaytime()) {
                NightmareMode.setBloodMoonTrue();
                this.test = true;
            }
            if (thisObj.isDaytime() && this.test) {
                NightmareMode.setBloodMoonFalse();
                this.test = false;
            }
        }
    }

    @Unique private boolean getIsBloodMoon(World world){
        long time = world.getWorldTime() % 72000;
//        return (time % 384000) >= 204000 && (time % 384000) < 248000;
        return world.getMoonPhase() == 0 && time > 60540 && time < 71459;
    }
}
