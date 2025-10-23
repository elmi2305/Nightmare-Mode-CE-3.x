package com.itlesports.nightmaremode;

import btw.world.util.difficulty.DifficultyParam;
import btw.world.util.difficulty.DifficultyProvider;

import static btw.world.util.difficulty.Difficulties.HOSTILE;
import static btw.world.util.difficulty.Difficulties.STANDARD;

public abstract class NMDifficultyParam<T> extends DifficultyParam<T>{

    public static void init() {}

    static {
        DifficultyProvider.setDefaultForParameter(ShouldMobsBeBuffed.class, false);
        HOSTILE.addParam(NMDifficultyParam.ShouldMobsBeBuffed.class, true);
        STANDARD.addParam(NMDifficultyParam.ShouldMobsBeBuffed.class, false);
    }






    public static class ShouldMobsBeBuffed
            extends NMDifficultyParam<Boolean> {
    }
    // time for difficulty configs
    public static class AreBloodMoonsEnabled
            extends NMDifficultyParam<Boolean> {
    }

    public static class AreMobVariantsEnabled
            extends NMDifficultyParam<Boolean> {
    }

    public static class DoCreepersLunge
            extends NMDifficultyParam<Boolean> {
    }

    public static class ShouldMobStatsScale
            extends NMDifficultyParam<Boolean> {
    }

    public static class ShouldSquidsTeleport
            extends NMDifficultyParam<Boolean> {
    }

    public static class ShouldBloodZombiesSpawn
            extends NMDifficultyParam<Boolean> {
    }




    public static class CreeperExplosionMultiplier
            extends NMDifficultyParam<Float> {
    }

    public static class ZombieReachMultiplier
            extends NMDifficultyParam<Float> {
    }

    public static class SkeletonShotSpeedMultiplier
            extends NMDifficultyParam<Float> {
    }

    public static class MobHealthMultiplier
            extends NMDifficultyParam<Float> {
    }

    public static class MobSpeedMultiplier
            extends NMDifficultyParam<Float> {
    }

    public static class VariantChanceMultiplier
            extends NMDifficultyParam<Float> {
    }

    public static class GracePeriodWorldMultiplier
            extends NMDifficultyParam<Float> {
    }

    public static class VillagerTradeResetMultiplier
            extends NMDifficultyParam<Float> {
    }



    public static class DaysUntilHardmode
            extends NMDifficultyParam<Integer> {
    }


}
