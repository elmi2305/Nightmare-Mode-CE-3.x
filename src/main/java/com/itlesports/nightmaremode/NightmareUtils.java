package com.itlesports.nightmaremode;

import btw.world.util.WorldUtils;
import net.minecraft.src.World;

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
//        return false;

        if(btw.community.nightmaremode.NightmareMode.getInstance().isBloodMoon != null) {
            return btw.community.nightmaremode.NightmareMode.getInstance().isBloodMoon || btw.community.nightmaremode.NightmareMode.bloodNightmare;
        }
        return false;

//        if(world == null){return false;}
//        if(NightmareMode.bloodNightmare){
//            return !world.isDaytime();
//        }
//        return world.getMoonPhase() == 0 && world.getWorldTime() % 72000 > 60540 && world.getWorldTime() % 72000 < 71459;
    }
}
