package com.itlesports.nightmaremode.util;

import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;

public final class VineGrowthRules {
    public static final int MAX_LENGTH = 20;

    private VineGrowthRules() {}

    public static boolean canGrowAt(IBlockAccess world, int x, int y, int z) {
        int length = 1;
        for (int above = y + 1; length <= MAX_LENGTH && world.getBlockId(x, above, z) == Block.vine.blockID; ++above) ++length;
        for (int below = y - 1; length <= MAX_LENGTH && world.getBlockId(x, below, z) == Block.vine.blockID; --below) ++length;
        return length <= MAX_LENGTH;
    }
}
