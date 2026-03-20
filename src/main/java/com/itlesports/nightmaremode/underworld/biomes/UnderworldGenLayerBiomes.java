package com.itlesports.nightmaremode.underworld.biomes;

import net.minecraft.src.*;

import java.util.Random;

public class UnderworldGenLayerBiomes extends GenLayerBiome {
    private static final BiomeGenBase[] underworldBiomes = new BiomeGenBase[] {
            BiomeGenBase.biomeList[24], // Underworld Plains
            BiomeGenBase.biomeList[25], // Underworld Desert
            BiomeGenBase.biomeList[26], // Flower garden
    };

    public UnderworldGenLayerBiomes(long par1, GenLayer par3GenLayer, WorldType par4WorldType) {
        super(par1, par3GenLayer, par4WorldType);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        // Call super, but it uses the vanilla list—override to use your array
        int[] ints = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] intCache = IntCache.getIntCache(areaWidth * areaHeight);
        for (int x = 0; x < areaHeight; ++x) {
            for (int y = 0; y < areaWidth; ++y) {
                this.initChunkSeed(y + areaX, x + areaY);
                int var9 = ints[y + x * areaWidth];
                if (var9 == 0) {
                    intCache[y + x * areaWidth] = 0;
                }
                else if (var9 == 1) {
                    if (this.nextInt(8) == 0) { // Control rarity (1/20 chance for flower fields)
                        intCache[y + x * areaWidth] = underworldBiomes[2].biomeID; // Flower Fields
                    }
                    else {
                        intCache[y + x * areaWidth] = underworldBiomes[this.nextInt(underworldBiomes.length - 1)].biomeID;
                    }
                }
                else {
                    // Add logic for other var9 values (e.g., 2=desert, etc.) to map to your biomes
                    intCache[y + x * areaWidth] = underworldBiomes[this.nextInt(underworldBiomes.length)].biomeID;
                }
            }
        }
        return intCache;
    }
}