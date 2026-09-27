package com.itlesports.nightmaremode.mixin;

import btw.util.hardcorespawn.HardcoreSpawnUtils;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.interfaces.FoodStatsExt;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HardcoreSpawnUtils.class)
public abstract class HardcoreSpawnUtilsMixin{

    @ModifyConstant(method = "handleHardcoreSpawn", constant = @Constant(longValue = 10800L))
    private static long lowerCooldownForRandomSpawning(long constant){
        return 1200L;
    }

    @Inject(method = "getPlayerSpawnRadius", at = @At("RETURN"), cancellable = true)
    private static void scaleHardcoreSpawnRadius(World world, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(NMUtils.getWorldProgress() > NMFields.PREHARDMODE
                ? cir.getReturnValue() * 1.25D : 500.0D);
    }

    @Inject(method = "getPlayerSpawnExclusionRadius", at = @At("RETURN"), cancellable = true)
    private static void scaleHardcoreSpawnExclusionRadius(World world, CallbackInfoReturnable<Double> cir) {
        if (NMUtils.getWorldProgress() == NMFields.PREHARDMODE) {
            cir.setReturnValue(200.0D);
        }
    }

    @Inject(method = "onSoftRespawn", at = @At("TAIL"))
    private static void restoreFullStatsOnSoftRespawn(EntityPlayerMP oldPlayer, EntityPlayerMP newPlayer, CallbackInfo ci) {
        newPlayer.setHealth(newPlayer.getMaxHealth());
        int maxFood = NightmareMode.nite
                ? NMUtils.getFoodShanksFromLevel(newPlayer)
                : ((FoodStatsExt)newPlayer.foodStats).nightmareMode$getMaxFoodLevel();
        newPlayer.foodStats.setFoodLevel(maxFood);
    }

}
