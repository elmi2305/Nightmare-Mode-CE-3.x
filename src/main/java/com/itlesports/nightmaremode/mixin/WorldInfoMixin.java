package com.itlesports.nightmaremode.mixin;

import api.world.difficulty.Difficulty;
import btw.community.nightmaremode.NightmareMode;
import btw.world.BTWDifficulties;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.GameRules;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(WorldInfo.class)
public abstract class WorldInfoMixin implements WorldInfoAccessor{
    @Shadow private long worldTime;
    @Shadow private GameRules theGameRules;
    @Shadow private long totalTime;
    @Shadow public abstract Difficulty getDifficulty();
    @Unique private boolean shouldCheck = true;

    @Inject(method = "getWorldTime()J", at = @At("HEAD"))
    private void setTimeToNightAndManageGracePeriod(CallbackInfoReturnable<Long> cir) {
        if (this.shouldCheck) {
            long initialTime = NightmareMode.perfectStart || NightmareMode.darkStormyNightmare ? 24000L : 18000L;
            long gracePeriodEnd = initialTime + (NightmareMode.bloodmare ? 2400: 2100) + (this.getDifficulty() != BTWDifficulties.HOSTILE ? 2000 : 0); // 1:45 grace period, 3:25 on bad dream
            if (this.totalTime == 0L) {
                this.worldTime = initialTime;
                this.theGameRules.addGameRule("doMobSpawning", "false");
            } else if (this.worldTime >= gracePeriodEnd && !this.theGameRules.getGameRuleBooleanValue("doMobSpawning")) {
                this.theGameRules.addGameRule("doMobSpawning", "true");
                this.shouldCheck = false;
            }
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
