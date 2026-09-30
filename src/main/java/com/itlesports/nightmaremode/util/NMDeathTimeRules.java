package com.itlesports.nightmaremode.util;

public final class NMDeathTimeRules {
    private NMDeathTimeRules() {}

    public static long getRespawnTime(long deathTime, long currentTime, int totalDeaths) {
        if (deathTime < 120000L && totalDeaths < 100) return 0L;
        return ((currentTime / 24000L) + (currentTime % 24000L == 0L ? 0L : 1L)) * 24000L;
    }
}
