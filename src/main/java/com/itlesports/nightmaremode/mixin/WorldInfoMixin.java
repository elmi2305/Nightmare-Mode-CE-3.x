package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.difficulty.Difficulties;
import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// SETS THE TIME TO NIGHT UPON WORLD CREATION

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
            long initialTime = NightmareMode.perfectStart ? 24000L : 18000L;
            long gracePeriodEnd = initialTime + (NightmareMode.bloodmare ? 2400: 2100) + (this.getDifficulty() != Difficulties.HOSTILE ? 2000 : 0); // 1:45 grace period, 3:25 on bad dream
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

    @Inject(method = "<init>(Lnet/minecraft/src/NBTTagCompound;)V", at = @At(value = "TAIL"))
    private void addCustomNBT(NBTTagCompound par1NBTTagCompound, CallbackInfo ci){
        NightmareMode.getInstance().portalTime = par1NBTTagCompound.getLong("PortalTime");
        NightmareMode.getInstance().shouldStackSizesIncrease = par1NBTTagCompound.getBoolean("HasDragonBeenDefeated");
    }
    @Inject(method = "updateTagCompound", at = @At("TAIL"))
    private void manageCustomNBT(NBTTagCompound par1NBTTagCompound, NBTTagCompound par2NBTTagCompound, CallbackInfo ci){
        par1NBTTagCompound.setLong("PortalTime", NightmareMode.getInstance().portalTime);
        par1NBTTagCompound.setBoolean("HasDragonBeenDefeated", NightmareMode.getInstance().shouldStackSizesIncrease);
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
