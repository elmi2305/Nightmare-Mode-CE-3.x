package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(WorldClient.class)
public abstract class WorldClientMixin extends World {

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
