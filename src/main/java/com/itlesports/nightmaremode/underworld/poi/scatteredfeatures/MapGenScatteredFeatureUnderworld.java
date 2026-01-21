package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import com.itlesports.nightmaremode.underworld.biomes.BiomeGenBlightlands;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenFlowerFields;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenHighlands;
import net.minecraft.src.*;

import java.util.Random;

public class MapGenScatteredFeatureUnderworld extends MapGenScatteredFeature {
    private int maxDistanceBetweenScatteredFeatures = 8;  // Debug: dense (vanilla 32)
    private int minDistanceBetweenScatteredFeatures = 4;  // Debug: min gap (vanilla 8)

    public MapGenScatteredFeatureUnderworld() {
        // No-arg constructor for instantiation in chunk provider
    }

    @Override
    public String func_143025_a() {
        return "nmTemple";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int par1, int par2) {
        int var3 = par1;
        int var4 = par2;
        if (par1 < 0) par1 -= maxDistanceBetweenScatteredFeatures - 1;
        if (par2 < 0) par2 -= maxDistanceBetweenScatteredFeatures - 1;
        int var5 = par1 / maxDistanceBetweenScatteredFeatures;
        int var6 = par2 / maxDistanceBetweenScatteredFeatures;
        Random var7 = this.worldObj.setRandomSeed(var5, var6, 14357617);
        var5 *= maxDistanceBetweenScatteredFeatures;
        var6 *= maxDistanceBetweenScatteredFeatures;
        var5 += var7.nextInt(maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);
        var6 += var7.nextInt(maxDistanceBetweenScatteredFeatures - minDistanceBetweenScatteredFeatures);

        if (var3 == var5 && var4 == var6) {
            BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(var3 * 16 + 8, var4 * 16 + 8);

            // Check for your custom biomes (add all where mushrooms spawn)
            if (biome instanceof BiomeGenFlowerFields ||
                    biome instanceof BiomeGenHighlands ||
                    biome instanceof BiomeGenBlightlands) {  // Etc.

                // For debugging: Make common but not every chunk (1/5 chance)
                return this.rand.nextInt(5) == 0;  // Tune: 5=common, 20=rare (vanilla-like)
            }
        }
        return false;
    }

    @Override
    protected StructureStart getStructureStart(int par1, int par2) {
        return new StructureScatteredFeatureStartUnderworld(this.worldObj, this.rand, par1, par2);
    }
}