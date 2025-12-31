/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.itlesports.nightmaremode.underworld;

import com.itlesports.nightmaremode.underworld.biomes.UnderworldGenLayerBiomes;
import net.minecraft.src.*;

public abstract class UnderworldGenLayer {
    private long worldGenSeed;
    protected GenLayer parent;
    private long chunkSeed;
    private long baseSeed;
    public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType) {
        GenLayerIsland var1 = new GenLayerIsland(1L);
        GenLayerFuzzyZoom var9 = new GenLayerFuzzyZoom(2000L, var1);
        GenLayerAddIsland var10 = new GenLayerAddIsland(1L, var9);
        GenLayerZoom var11 = new GenLayerZoom(2001L, var10);
        var10 = new GenLayerAddIsland(2L, var11);
        GenLayerAddSnow var12 = new GenLayerAddSnow(2L, var10);
        var11 = new GenLayerZoom(2002L, var12);
        var10 = new GenLayerAddIsland(3L, var11);
        var11 = new GenLayerZoom(2003L, var10);
        var10 = new GenLayerAddIsland(4L, var11);
        GenLayerAddMushroomIsland var15 = new GenLayerAddMushroomIsland(5L, var10);
        GenLayer var17 = GenLayerZoom.magnify(1000L, var15, 0);
        byte var4 = 4;
        if (worldType == WorldType.LARGE_BIOMES) {
            var4 = 6;
        }
        GenLayer var5 = GenLayerZoom.magnify(1000L, var17, 0);
//        GenLayerRiverInit var19 = new GenLayerRiverInit(100L, var5);
        var5 = GenLayerZoom.magnify(1000L, var5, var4 + 2);
//        GenLayerRiver var20 = new GenLayerRiver(1L, var5);
        GenLayerSmooth var21 = new GenLayerSmooth(1000L, var5);
        GenLayer var6 = GenLayerZoom.magnify(1000L, var17, 0);
        GenLayerBiome var23 = new UnderworldGenLayerBiomes(200L, var6, worldType); // Use your custom biome layer here
        var6 = GenLayerZoom.magnify(1000L, var23, 2);
        Object var22 = new GenLayerHills(1000L, var6);
        for (int var7 = 0; var7 < var4; ++var7) {
            var22 = new GenLayerZoom(1000L + (long)var7, (GenLayer)var22);
            if (var7 == 0) {
                var22 = new GenLayerAddIsland(3L, (GenLayer)var22);
            }
            if (var7 == 1) {
                var22 = new GenLayerShore(1000L, (GenLayer)var22);
            }
            if (var7 == 1) {
                var22 = new GenLayerSwampRivers(1000L, (GenLayer)var22);
            }
        }
        GenLayerSmooth var24 = new GenLayerSmooth(1000L, (GenLayer)var22);
//        GenLayerRiverMix var25 = new GenLayerRiverMix(100L, var24, var21);
        GenLayerVoronoiZoom var8 = new GenLayerVoronoiZoom(10L, var24);
        var24.initWorldGenSeed(seed);
        var8.initWorldGenSeed(seed);
        return new GenLayer[]{var24, var8, var24};
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

