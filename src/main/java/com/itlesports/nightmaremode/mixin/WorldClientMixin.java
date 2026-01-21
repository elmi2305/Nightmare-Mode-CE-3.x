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


    @Unique private int subTick = 0;
    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldClient;setWorldTime(J)V"))
    private long realTime(long par1){
        if(NightmareMode.realTime){
            this.subTick++;
            if(this.subTick % 36 != 0){
                return par1 - 1L;
            } else{
                this.subTick = 0;
                return par1;
            }
        }

        return par1;
    }
    public WorldClientMixin(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent) {
        super(par1ISaveHandler, par2Str, par3WorldProvider, par4WorldSettings, par5Profiler, par6ILogAgent);
    }


    @ModifyArgs(method = "playSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/SoundManager;playSound(Ljava/lang/String;FFFFF)V"))
    private void modifySoundOfLightning(Args args){
        if(NightmareMode.darkStormyNightmare && args.get(0).equals("ambient.weather.thunder")){
            float vol = args.get(4);
            args.set(4,vol * 0.0005f);
        }
    }
}
