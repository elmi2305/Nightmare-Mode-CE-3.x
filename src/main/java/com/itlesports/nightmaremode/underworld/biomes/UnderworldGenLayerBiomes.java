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
    public int[] getInts(int par1, int par2, int par3, int par4) {
        // Call super, but it uses the vanilla list—override to use your array
        int[] var5 = this.parent.getInts(par1, par2, par3, par4);
        int[] var6 = IntCache.getIntCache(par3 * par4);
        for (int var7 = 0; var7 < par4; ++var7) {
            for (int var8 = 0; var8 < par3; ++var8) {
                this.initChunkSeed(var8 + par1, var7 + par2);
                int var9 = var5[var8 + var7 * par3];
                if (var9 == 0) {
                    var6[var8 + var7 * par3] = 0;
                }
                else if (var9 == BiomeGenBase.mushroomIsland.biomeID) {
                    var6[var8 + var7 * par3] = var9; // Keep if needed
                }
                else if (var9 == 1) {
                    // Plains-like: Pick from your list (e.g., flower fields rare)
//                    if (this.nextInt(20) == 0) { // Control rarity (1/20 chance for flower fields)
//                        var6[var8 + var7 * par3] = BiomeGenBase.biomeList[42].biomeID; // Flower Fields
//                    }
//                    else {
                        var6[var8 + var7 * par3] = underworldBiomes[this.nextInt(underworldBiomes.length)].biomeID;
//                    }
                }
                else {
                    // Add logic for other var9 values (e.g., 2=desert, etc.) to map to your biomes
                    var6[var8 + var7 * par3] = underworldBiomes[this.nextInt(underworldBiomes.length)].biomeID;
                }
            }
        }
        return var6;
    }
}