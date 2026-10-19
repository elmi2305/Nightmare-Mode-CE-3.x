package com.itlesports.nightmaremode.agriculture;

public final class PotatoHarvestRates {
    public static final float GROWTH_COST_MULTIPLIER = 0.25F;

    private PotatoHarvestRates() {}

    public static float bonusChance(float potassium) {
        return 0.2F + Math.max(0.0F, Math.min(100.0F, potassium)) * 0.004F;
    }

    public static float thirdPotatoChance(float potassium) {
        return Math.max(0.0F, Math.min(1.0F, (potassium - 60.0F) / 40.0F)) * 0.5F;
    }

    public static int extraPotatoes(float potassium, java.util.Random random) {
        if (random.nextFloat() >= bonusChance(potassium)) return 0;
        return 1 + (random.nextFloat() < thirdPotatoChance(potassium) ? 1 : 0);
    }
}
