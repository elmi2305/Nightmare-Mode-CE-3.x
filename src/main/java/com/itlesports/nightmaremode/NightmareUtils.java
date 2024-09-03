package com.itlesports.nightmaremode;

import btw.world.util.WorldUtils;
import com.itlesports.nightmaremode.mixin.WorldInfoMixin;
import net.minecraft.src.World;

public class NightmareUtils {

    public static int getGameProgressMobsLevel(World world) {
        if (!world.worldInfo.getDifficulty().shouldHCSRangeIncrease()) {
            return 0;
        }
        else if (WorldUtils.gameProgressHasEndDimensionBeenAccessedServerOnly()) {
            return 3;
        }
        else if (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
            return 2;
        }
        else if (WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()) {
            return 1;
        }
        return 0;
    }
}
