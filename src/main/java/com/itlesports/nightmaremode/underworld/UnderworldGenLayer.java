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
        int zooms = worldType == WorldType.LARGE_BIOMES ? 8 : 6;
        GenLayer biomes = new UnderworldGenLayerBiomes(200L);
        biomes = GenLayerZoom.magnify(1000L, biomes, zooms);
        biomes = new GenLayerSmooth(1000L, biomes);
        GenLayer blockBiomes = new GenLayerVoronoiZoom(10L, biomes);
        biomes.initWorldGenSeed(seed);
        blockBiomes.initWorldGenSeed(seed);
        return new GenLayer[]{biomes, blockBiomes, biomes};
    }


    public static GenLayer[] initializeAllBiomeGeneratorsStupid(long seed, WorldType worldType) {
        return initializeAllBiomeGenerators(seed, worldType);
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

