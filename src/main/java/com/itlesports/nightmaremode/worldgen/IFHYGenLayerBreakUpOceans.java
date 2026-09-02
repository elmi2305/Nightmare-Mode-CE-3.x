package com.itlesports.nightmaremode.worldgen;

import net.minecraft.src.GenLayer;
import net.minecraft.src.IntCache;

/** Turns only deep ocean interiors into land; coastlines can never be eroded. */
public class IFHYGenLayerBreakUpOceans extends GenLayer {
    public IFHYGenLayerBreakUpOceans(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int x, int z, int width, int height) {
        int parentWidth = width + 2;
        int[] source = this.parent.getInts(x - 1, z - 1, parentWidth, height + 2);
        int[] output = IntCache.getIntCache(width * height);

        for (int localZ = 0; localZ < height; ++localZ) {
            for (int localX = 0; localX < width; ++localX) {
                int north = source[localX + 1 + localZ * parentWidth];
                int east = source[localX + 2 + (localZ + 1) * parentWidth];
                int west = source[localX + (localZ + 1) * parentWidth];
                int south = source[localX + 1 + (localZ + 2) * parentWidth];
                int center = source[localX + 1 + (localZ + 1) * parentWidth];
                this.initChunkSeed(localX + x, localZ + z);
                output[localX + localZ * width] = center == 0 && north == 0 && east == 0 && west == 0 && south == 0
                        && this.nextInt(2) == 0 ? 1 : center;
            }
        }
        return output;
    }
}
