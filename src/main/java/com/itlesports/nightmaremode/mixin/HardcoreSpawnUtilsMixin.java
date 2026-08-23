package com.itlesports.nightmaremode.mixin;

import btw.BTWMod;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(HardcoreSpawnUtils.class)
public abstract class HardcoreSpawnUtilsMixin{
    @Unique private static final long FIRST_FIVE_DAYS_TICKS = 120000L;

    @Redirect(method = "assignNewHardcoreSpawnLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldServer;setWorldTime(J)V"))
    private static void resetSingleplayerGracePeriodOnDeath(WorldServer world, long ignoredWorldTime) {
        if (BTWMod.isSinglePlayerNonLan() && world.getWorldTime() < FIRST_FIVE_DAYS_TICKS) {
            world.setWorldTime(0L);
        }
    }



    @ModifyConstant(method = "handleHardcoreSpawn", constant = @Constant(longValue = 10800L))
    private static long lowerCooldownForRandomSpawning(long constant){
        return 67676767;
    }

}
