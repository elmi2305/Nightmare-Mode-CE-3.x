package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulties;
import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// SETS THE TIME TO NIGHT UPON WORLD CREATION

@Mixin(WorldInfo.class)
public abstract class WorldInfoMixin {
    @Shadow private long worldTime;
    @Shadow private GameRules theGameRules;
    @Shadow private long totalTime;
    @Shadow public abstract Difficulty getDifficulty();

    @Unique private boolean botherChecking = true;

    @Inject(method = "getWorldTime()J", at = @At("HEAD"))
    private void nightSetter(CallbackInfoReturnable<Long> cir) {
        if (botherChecking && this.getDifficulty() == Difficulties.HOSTILE) {
            if (this.totalTime == 0L) {
                worldTime = 18000L;
                theGameRules.addGameRule("doMobSpawning", "false");
            } else if(worldTime >= 19800 && !theGameRules.getGameRuleBooleanValue("doMobSpawning")){
                theGameRules.addGameRule("doMobSpawning", "true");
                botherChecking = false;
            } // 1:30 grace period
        }
    }
}
