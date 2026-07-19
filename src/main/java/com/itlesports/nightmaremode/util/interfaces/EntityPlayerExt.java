package com.itlesports.nightmaremode.util.interfaces;

public interface EntityPlayerExt {
    void nightmareMode$setBlinkLength(int length);
    int nightmareMode$getBlinkLength();

    void nightmareMode$setFear(float targetFear);
    float nightmareMode$getFear();

    void nightmareMode$setFoodMax(int targetFood);

    void nightmareMode$incrementHealth(int amount);

    void nightmareMode$setHeartCrack(int length);
    boolean nightmareMode$getHeartCrack();

    float nightmareMode$getSkillBlockBreakSpeedBonus();
    float nightmareMode$getSkillMobLootChanceBonus();
    boolean nightmareMode$canSkillHarvestDiamondOre();
    boolean nightmareMode$canSkillCureVillagers();
    float nightmareMode$getSkillFoodSpoilageRateMultiplier();
    boolean nightmareMode$doesGrassBreakInstantly();
}
