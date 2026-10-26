package com.itlesports.nightmaremode.util;

public final class NMFishingRates {
    private NMFishingRates() {}

    public static int netherBiteOdds(float rodMultiplier, boolean lure) {
        return Math.max(1, Math.round(150.0F * rodMultiplier * (lure ? 0.5F : 1.0F)));
    }

    public static int biteOdds(long worldTime, int moonPhase, boolean bloodMoon, boolean raining,
                               float availability, float rodMultiplier, boolean lure) {
        int time = (int)Math.floorMod(worldTime, 24000L);
        int odds;
        if (isFishingNight(worldTime)) {
            if (bloodMoon) odds = 30;
            else switch (Math.floorMod(moonPhase, 8)) {
                case 0: odds = 120; break;
                case 1: case 7: odds = 600; break;
                case 2: case 6: odds = 1200; break;
                case 3: case 5: odds = 1800; break;
                default: odds = 2400;
            }
        } else {
            odds = time < 2000 || time > 22000 || time > 10000 && time < 14000 ? 600 : 1200;
        }
        // sparse populations slow bites by at most twice; empty waters remain blocked by the hook.
        float populationPenalty = 2.0F - Math.max(0.0F, Math.min(1.0F, availability));
        return Math.max(1, Math.round(odds * populationPenalty * rodMultiplier
                * (raining ? 0.5F : 1.0F) * (lure ? 0.5F : 1.0F)));
    }

    public static int fishWeightMultiplier(long worldTime, int moonPhase, boolean bloodMoon) {
        if (!isFishingNight(worldTime)) return 1;
        if (bloodMoon) return 8;
        int phase = Math.floorMod(moonPhase, 8);
        if (phase == 0) return 4;
        return phase == 1 || phase == 7 ? 2 : 1;
    }

    private static boolean isFishingNight(long worldTime) {
        int time = (int)Math.floorMod(worldTime, 24000L);
        return time > 14000 && time < 22000;
    }
}
