package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(WorldClient.class)
public abstract class WorldClientMixin extends World {

    @Unique private int earlyDayTick;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldClient;setWorldTime(J)V"))
    private long extendFirstTwoDays(long worldTime) {
        long currentTime = this.getWorldTime();
        if (this.provider.dimensionId == 0 && currentTime < 48000L
                && currentTime % 24000L < 12000L
                && ++this.earlyDayTick % 2 != 0) {
            return worldTime - 1L;
        }
        return worldTime;
    }

    public WorldClientMixin(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent) {
        super(par1ISaveHandler, par2Str, par3WorldProvider, par4WorldSettings, par5Profiler, par6ILogAgent);
    }


    @ModifyArgs(method = "playSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/SoundManager;playSound(Ljava/lang/String;FFFFF)V"))
    private void modifySoundOfLightning(Args args){
        if(args.get(0).equals("ambient.weather.thunder")){
            float vol = args.get(4);
            if((NightmareMode.darkStormyNightmare)) {
                args.set(4, vol * 0.0005f);
            }
            if(NightmareMode.devMode){
                args.set(4, vol * 0.0001f);
            }
        }
    }
}
