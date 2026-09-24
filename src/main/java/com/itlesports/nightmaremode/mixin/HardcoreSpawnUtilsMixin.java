package com.itlesports.nightmaremode.mixin;

import btw.util.hardcorespawn.HardcoreSpawnUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(HardcoreSpawnUtils.class)
public abstract class HardcoreSpawnUtilsMixin{

    @ModifyConstant(method = "handleHardcoreSpawn", constant = @Constant(longValue = 10800L))
    private static long lowerCooldownForRandomSpawning(long constant){
        return 67676767;
    }

}
