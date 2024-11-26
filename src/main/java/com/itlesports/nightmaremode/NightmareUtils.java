package com.itlesports.nightmaremode;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.WorldUtils;
import net.minecraft.src.World;

import java.util.Objects;

public class NightmareUtils {
    public static int getWorldProgress(World world) {
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
    public static boolean getIsBloodMoon(){
        if(NightmareMode.getInstance() == null){return false;}
        return Objects.requireNonNullElse(NightmareMode.getInstance().isBloodMoon, false);
    }
}
