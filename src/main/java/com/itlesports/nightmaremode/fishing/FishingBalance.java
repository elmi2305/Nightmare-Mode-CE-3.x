package com.itlesports.nightmaremode.fishing;

/** combat and progression settings affect separate parts of fishing. */
public final class FishingBalance {
    private FishingBalance() {}
    public static float flee(boolean combatEasy) { return combatEasy ? 0.6F : 1; }
    public static float pull(boolean combatEasy) { return combatEasy ? 0.65F : 1; }
    public static float bossDamage(boolean combatEasy) { return combatEasy ? 3 : 6; }
    public static float abundance(boolean lowProgression) { return lowProgression ? 1.6F : 1; }
    public static float rarity(boolean lowProgression) { return lowProgression ? 2 : 1; }
    public static int schoolCap(boolean lowProgression) { return lowProgression ? 24 : 18; }
}
