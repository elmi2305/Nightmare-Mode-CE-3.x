package com.itlesports.nightmaremode.mixin;

import btw.util.hardcorespawn.HardcoreSpawnUtils;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.FoodStatsExt;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HardcoreSpawnUtils.class)
public abstract class HardcoreSpawnUtilsMixin{

    @ModifyConstant(method = "handleHardcoreSpawn", constant = @Constant(longValue = 10800L))
    private static long lowerCooldownForRandomSpawning(long constant){
        return 67676767;
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
