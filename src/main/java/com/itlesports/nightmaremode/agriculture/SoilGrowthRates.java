package com.itlesports.nightmaremode.agriculture;

public final class SoilGrowthRates {
    public static final float MIN_PREFERRED_ATTRIBUTE = 5.0F;

    private SoilGrowthRates() {}

    public static boolean meetsPreferredMinimum(float value) {
        return value >= MIN_PREFERRED_ATTRIBUTE;
    }

    public static float multiplier(float preferredAverage, float otherAverage) {
        float quality = preferredAverage * 0.75F + otherAverage * 0.25F;
        return 0.5F + Math.max(0.0F, Math.min(100.0F, quality)) / 100.0F;
    }

    public static boolean permitsDay(float multiplier, long seed, long day, int x, int z) {
        if (multiplier >= 1.0F) return true;
        // keep a poor-soil day's allowance stable across random ticks and reloads.
        long roll = seed ^ day * 42317861L ^ (long)x * 341873128712L ^ (long)z * 132897987541L;
        roll ^= roll >>> 33;
        roll *= 0xff51afd7ed558ccdL;
        roll ^= roll >>> 33;
        roll *= 0xc4ceb9fe1a85ec53L;
        roll ^= roll >>> 33;
        return (roll >>> 40) / 16777216.0F < multiplier;
    }

    public static long growthDay(long worldTime) {
        // the growing window crosses tick zero, so start its day when the night reset ends.
        return Math.floorDiv(worldTime - 22000L, 24000L);
    }

    public static float continuationChance(float multiplier) {
        // continuing after each daily allowance gives an expected multiplier of allowances.
        return multiplier > 1.0F ? 1.0F - 1.0F / multiplier : 0.0F;
    }
}
