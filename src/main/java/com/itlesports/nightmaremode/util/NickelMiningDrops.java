package com.itlesports.nightmaremode.util;

import java.util.Random;

public final class NickelMiningDrops {
    private NickelMiningDrops() {}

    public static int count(int base, float secondRockChance, boolean diamondPickaxe, Random random) {
        int count = base;
        if (random.nextFloat() < Math.min(1.0F, secondRockChance)) count *= 2;
        if (diamondPickaxe) {
            int extra = count / 2;
            if (count % 2 != 0 && random.nextBoolean()) ++extra;
            count += extra;
        }
        return count;
    }
}
