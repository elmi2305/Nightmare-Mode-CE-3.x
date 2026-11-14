package com.itlesports.nightmaremode.fishing;

public final class FishingTuning {
    // radians per tick while cruising; hooked turns allow one and a half times this rate.
    public static float GIANT_SQUID_TURN_RATE = 0.035F;
    // applied after each species' own pull value and before rod resistance.
    public static float FISH_PULL_MULTIPLIER = 2.25F;
    public static float GIANT_SQUID_PULL_MULTIPLIER = 1.3F;
    // surge adds to the 0.12 windup fraction; recovery is a direct fraction.
    public static float GIANT_SQUID_SURGE_FORCE = 0.85F;
    public static float GIANT_SQUID_RECOVERY_FORCE = 0.08F;
    // one is normal; large values can make every eligible wandering arrival rare.
    public static float RARE_SPAWN_MULTIPLIER = 1.0F;
    // zero disables selection forcing; -2 selects the giant squid, -1 the blue squid.
    // item ids select other rare fish. habitat, depth and supply checks still apply.
    public static int FORCED_RARE_CREATURE_ID = 0;
    public static float WINCH_METRES_PER_NOTCH = 7.0F;
    public static float OCEAN_FULL_DEPTH_OFFSHORE_BLOCKS = 96.0F;
    public static float GIANT_SQUID_MIN_OFFSHORE_BLOCKS = 96.0F;
    public static float WIDE_HOOK_RADIUS_MULTIPLIER = 1.8F;

    private FishingTuning() {}
}
