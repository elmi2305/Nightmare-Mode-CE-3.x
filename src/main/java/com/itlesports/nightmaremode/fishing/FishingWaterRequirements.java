package com.itlesports.nightmaremode.fishing;

public final class FishingWaterRequirements {
    @FunctionalInterface public interface Sources { boolean source(int x, int y, int z); }
    private FishingWaterRequirements() {}

    public static boolean smallPool(Sources fluid, float rodMultiplier, int x, int y, int z) {
        int depth = rodMultiplier <= 0.5F ? 1 : 2;
        int needed = rodMultiplier <= 0.65F ? 3 : 5;
        if (!fluid.source(x, y, z)) y--;
        for (int d = 0; d < depth; d++) if (!fluid.source(x, y - d, z)) return false;
        int columns = 0;
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            if (Math.abs(dx) + Math.abs(dz) > 1) continue;
            boolean valid = true;
            for (int d = 0; d < depth; d++) if (!fluid.source(x + dx, y - d, z + dz)) valid = false;
            if (valid) columns++;
        }
        return columns >= needed;
    }
}
