/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.itlesports.nightmaremode.underworld;

import net.minecraft.src.*;

public abstract class UnderworldGenLayer {
    private long worldGenSeed;
    protected GenLayer parent;
    private long chunkSeed;
    private long baseSeed;

    public static GenLayer[] initializeAllBiomeGenerators(long l, WorldType worldType) {
        GenLayer genLayer = new GenLayerIsland(1L);
        genLayer = new GenLayerFuzzyZoom(2000L, genLayer);
        genLayer = new GenLayerAddIsland(1L, genLayer);
        genLayer = new GenLayerZoom(2001L, genLayer);
        genLayer = new GenLayerAddIsland(2L, genLayer);
        genLayer = new GenLayerAddSnow(2L, genLayer);
        genLayer = new GenLayerZoom(2002L, genLayer);
        genLayer = new GenLayerAddIsland(3L, genLayer);
        genLayer = new GenLayerZoom(2003L, genLayer);
        genLayer = new GenLayerAddIsland(4L, genLayer);
        genLayer = new GenLayerAddMushroomIsland(5L, genLayer);
        int n = 4;
        if (worldType == WorldType.LARGE_BIOMES) {
            n = 6;
        }
        GenLayer genLayer2 = genLayer;
        genLayer2 = GenLayerZoom.magnify(1000L, genLayer2, 0);
        genLayer2 = new GenLayerRiverInit(100L, genLayer2);
        genLayer2 = GenLayerZoom.magnify(1000L, genLayer2, n + 2);
        genLayer2 = new GenLayerRiver(1L, genLayer2);
        genLayer2 = new GenLayerSmooth(1000L, genLayer2);
        GenLayer genLayer3 = genLayer;
        genLayer3 = GenLayerZoom.magnify(1000L, genLayer3, 0);
        genLayer3 = new GenLayerBiome(200L, genLayer3, worldType);
        genLayer3 = GenLayerZoom.magnify(1000L, genLayer3, 2);
        genLayer3 = new GenLayerHills(1000L, genLayer3);
        for (int i = 0; i < n; ++i) {
            genLayer3 = new GenLayerZoom(1000 + i, genLayer3);
            if (i == 0) {
                genLayer3 = new GenLayerAddIsland(3L, genLayer3);
            }
            if (i == 1) {
                genLayer3 = new GenLayerShore(1000L, genLayer3);
            }
            if (i != 1) continue;
            genLayer3 = new GenLayerSwampRivers(1000L, genLayer3);
        }
        genLayer3 = new GenLayerSmooth(1000L, genLayer3);
        GenLayer genLayer4 = genLayer3 = new GenLayerRiverMix(100L, genLayer3, genLayer2);
        GenLayerVoronoiZoom genLayerVoronoiZoom = new GenLayerVoronoiZoom(10L, genLayer3);
        genLayer3.initWorldGenSeed(l);
        genLayerVoronoiZoom.initWorldGenSeed(l);
        return new GenLayer[]{genLayer3, genLayerVoronoiZoom, genLayer4};
    }

    public UnderworldGenLayer(long l) {
        this.baseSeed = l;
        this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
        this.baseSeed += l;
        this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
        this.baseSeed += l;
        this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
        this.baseSeed += l;


        this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
        this.baseSeed += l;
    }

    public void initWorldGenSeed(long l) {
        this.worldGenSeed = l;
        if (this.parent != null) {
            this.parent.initWorldGenSeed(l);
        }
        this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldGenSeed += this.baseSeed;
        this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldGenSeed += this.baseSeed;
        this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldGenSeed += this.baseSeed;

        this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
        this.worldGenSeed += this.baseSeed;
    }

    public void initChunkSeed(long l, long m) {
        this.chunkSeed = this.worldGenSeed;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += l;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += m;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += l;
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += m;


        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += m;
    }

    protected int nextInt(int i) {
        int n = (int)((this.chunkSeed >> 24) % (long)i);
        if (n < 0) {
            n += i;
        }
        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += this.worldGenSeed;

        this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
        this.chunkSeed += this.worldGenSeed;
        return n;
    }

    public abstract int[] getInts(int var1, int var2, int var3, int var4);
}

