package com.itlesports.nightmaremode.worldgen;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.GenLayer;
import net.minecraft.src.IntCache;
import net.minecraft.src.WorldType;

public final class IFHYGenLayerBiome extends GenLayer {
    private final BiomeGenBase[] allowedBiomes;

    public IFHYGenLayerBiome(long seed, GenLayer parent, WorldType worldType) {
        super(seed);
        this.parent = parent;
        this.allowedBiomes = worldType == WorldType.DEFAULT_1_1
                ? new BiomeGenBase[]{BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills,
                        BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga}
                : new BiomeGenBase[]{BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills,
                        BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga, BiomeGenBase.jungle};
    }

    @Override
    public int[] getInts(int x, int z, int width, int height) {
        int[] source = this.parent.getInts(x, z, width, height);
        int[] result = IntCache.getIntCache(width * height);

        for (int row = 0; row < height; ++row) {
            for (int column = 0; column < width; ++column) {
                this.initChunkSeed(x + column, z + row);
                int biome = source[column + row * width];
                if (biome == 0 || biome == BiomeGenBase.mushroomIsland.biomeID) {
                    result[column + row * width] = biome;
                } else if (biome == 1) {
                    result[column + row * width] = this.allowedBiomes[this.nextInt(this.allowedBiomes.length)].biomeID;
                } else {
                    int choice = this.nextInt(5);
                    result[column + row * width] = switch (choice) {
                        case 0, 1 -> BiomeGenBase.icePlains.biomeID;
                        case 2 -> BiomeGenBase.taiga.biomeID;
                        case 3 -> BiomeGenBase.forest.biomeID;
                        default -> BiomeGenBase.plains.biomeID;
                    };
                }
            }
        }

        return result;
    }
}
