package com.itlesports.nightmaremode.fishing;

/** shared timing keeps the visible windup and the server's pull in step. */
public final class FishingStruggle {
    public static final int WINDUP = 0, SURGE = 1, RECOVERY = 2;
    public static final int CYCLE_TICKS = 100, WINDUP_END = 20, SURGE_END = 48;
    public static final float SURFACE_Y = 36, GATE_HEIGHT = 40;
    public static final float NORMAL_GATE_HALF_WIDTH = 42, GIANT_GATE_HALF_WIDTH = 74;
    public static final float GIANT_HOLD_TICKS = 50;

    private FishingStruggle() {}

    public static float cycle(float ticks) { return Math.max(0, ticks) % CYCLE_TICKS; }
    public static int phase(float ticks) {
        float t = cycle(ticks);
        return t < WINDUP_END ? WINDUP : t < SURGE_END ? SURGE : RECOVERY;
    }

    public static float surge(float ticks) {
        float t = cycle(ticks);
        if (t < WINDUP_END || t >= SURGE_END) return 0;
        float wave = (float)Math.sin((t - WINDUP_END) / (SURGE_END - WINDUP_END) * Math.PI);
        return wave * wave;
    }

    public static float coil(float ticks) {
        float t = cycle(ticks);
        if (t < WINDUP_END) return 0.22F + 0.78F * smooth(t / WINDUP_END);
        if (t < SURGE_END) return 1 - 0.88F * smooth((t - WINDUP_END) / (SURGE_END - WINDUP_END));
        return 0.12F + 0.1F * smooth((t - SURGE_END) / (CYCLE_TICKS - SURGE_END));
    }

    public static float pullScale(float ticks) {
        return phase(ticks) == RECOVERY ? FishingTuning.GIANT_SQUID_RECOVERY_FORCE
                : 0.12F + FishingTuning.GIANT_SQUID_SURGE_FORCE * surge(ticks);
    }

    public static float gateHalfWidth(boolean giant) { return giant ? GIANT_GATE_HALF_WIDTH : NORMAL_GATE_HALF_WIDTH; }
    public static float smooth(float t) { t = FishingGame.clamp(t, 0, 1); return t * t * (3 - 2 * t); }
}
