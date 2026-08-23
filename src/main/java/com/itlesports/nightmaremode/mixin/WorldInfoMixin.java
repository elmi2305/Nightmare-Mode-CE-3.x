package com.itlesports.nightmaremode.mixin;

import api.world.difficulty.Difficulty;
import btw.community.nightmaremode.NightmareMode;
import btw.world.BTWDifficulties;
import com.itlesports.nightmaremode.mixin.interfaces.WorldInfoAccessor;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(WorldInfo.class)
public abstract class WorldInfoMixin implements WorldInfoAccessor {
    @Shadow public abstract Difficulty getDifficulty();
    @Shadow private GameRules theGameRules;
    @Shadow private boolean allowCommands;
    @Shadow private long totalTime;

    @Inject(method = "<init>(Lnet/minecraft/src/WorldInfo;)V", at = @At("TAIL"))
    private void doDevStuff(WorldInfo worldInfo, CallbackInfo ci){
        if(NightmareMode.devMode && this.totalTime == 0L){
            this.theGameRules.addGameRule("extendedDebugAccess", "creativeOnly");
            this.theGameRules.addGameRule("keepInventory", "true");
            this.allowCommands = true;
        }
    }


    @ModifyArg(method = "updateTagCompound", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/NBTTagCompound;setBoolean(Ljava/lang/String;Z)V",ordinal = 4),index = 0)
    private String javaCompatibility(String string){
        return "jvmArgsOverride";
    }
    @Inject(method = "<init>(Lnet/minecraft/src/NBTTagCompound;)V", at = @At("TAIL"))
    private void addCompatibility(NBTTagCompound par1NBTTagCompound, CallbackInfo ci){
        if (par1NBTTagCompound.hasKey("jvmArgsOverride")) {
            this.setJavaCompatibilityLevel(par1NBTTagCompound.getBoolean("jvmArgsOverride"));
        }
    }
    
    @ModifyArg(method = "updateTagCompound", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/NBTTagCompound;setInteger(Ljava/lang/String;I)V",ordinal = 1),index = 0)
    private String implementDeathCounter(String string){
        return "DeathCount";
    }
    @Inject(method = "<init>(Lnet/minecraft/src/NBTTagCompound;)V", at = @At("TAIL"))
    private void countDeaths(NBTTagCompound par1NBTTagCompound, CallbackInfo ci){
        this.setDeathCounter(EnumGameType.getByID(par1NBTTagCompound.getInteger("DeathCount")));
    }

}
