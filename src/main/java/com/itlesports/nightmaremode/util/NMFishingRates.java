package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.fishing.FishingTuning;

public final class NMFishingRates {
    private NMFishingRates() {}

    public static float abundance(long time, int moon, boolean blood, boolean rain, boolean thunder, boolean lava) {
        if (lava) return 1;
        int phaseBonus = fishWeightMultiplier(time, moon, blood);
        float quantity = phaseBonus >= 8 ? 2.2F : phaseBonus >= 4 ? 1.8F : phaseBonus >= 2 ? 1.15F : 0.75F;
        return Math.min(3.5F, quantity * (rain ? thunder ? 2.5F : 1.9F : 1));
    }

    public static float rareAppearanceChance(float depth, float skillBonus, boolean rain) {
        float chance = Math.min(0.16F, (0.008F + Math.max(0, depth) * 0.000175F)
                * (1 + Math.max(0, skillBonus) * 2) * (rain ? 1.2F : 1));
        return Math.min(1, chance * Math.max(0, FishingTuning.RARE_SPAWN_MULTIPLIER));
    }

    public static int activationTicks(float arrivals, float rodMultiplier, float abundance) {
        return Math.round(Math.max(40, Math.min(600, (40 + arrivals * 2 * rodMultiplier) / Math.max(0.1F, abundance))));
    }

    public static float captureMultiplier(float rodMultiplier, boolean trackingReel) {
        return (float)Math.pow(1 / Math.max(0.1F, rodMultiplier), 1.25) * (trackingReel ? 1.4F : 1);
    }

    public static float pullResistance(float rodMultiplier) {
        return (float)Math.pow(1 / Math.max(0.1F, rodMultiplier), 0.95);
    }

    public static float arrivalTicks(int biteOdds) {
        // compress the old waiting times while preserving every equipment and weather bonus.
        return 18 + 40 * (float)Math.sqrt(Math.max(1, biteOdds) / 150.0F);
    }

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
