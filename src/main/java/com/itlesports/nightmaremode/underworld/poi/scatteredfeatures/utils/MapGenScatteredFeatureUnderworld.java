package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenBlightlands;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenFlowerFields;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenHighlands;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapGenScatteredFeatureUnderworld extends MapGenStructure {
    private final int maxDistanceBetweenScatteredFeatures = 16;  // max gap (vanilla 32)
    private final int minDistanceBetweenScatteredFeatures = 8;  // min gap (vanilla 8)
    private final List scatteredFeatureSpawnList = new ArrayList();


    public MapGenScatteredFeatureUnderworld() {
        // No-arg constructor for instantiation in chunk provider
    }


    public boolean shouldUseStructureSpawnTable(int x, int y, int z) {
        StructureStart structStart = this.func_143028_c(x, y, z);
        if (structStart != null && structStart instanceof StructureScatteredFeatureStart && !structStart.getComponents().isEmpty()) {
            StructureComponent component = (StructureComponent)structStart.getComponents().getFirst();
            return component instanceof ComponentScatteredFeatureSwampHut; // what?
        }
        return false;
    }

    public List getScatteredFeatureSpawnList() {
        return this.scatteredFeatureSpawnList;
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


    @Override
    protected ChunkPosition getSpawnStructureAtCoords(int par1, int par2) {
        if (par1 < 0) {
            par1 -= this.maxDistanceBetweenScatteredFeatures - 1;
        }
        if (par2 < 0) {
            par2 -= this.maxDistanceBetweenScatteredFeatures - 1;
        }
        int var5 = par1 / this.maxDistanceBetweenScatteredFeatures;
        int var6 = par2 / this.maxDistanceBetweenScatteredFeatures;
        Random var7 = this.worldObj.setRandomSeed(var5, var6, 14357617);
        var5 *= this.maxDistanceBetweenScatteredFeatures;
        var6 *= this.maxDistanceBetweenScatteredFeatures;
        BiomeGenBase var8 = this.worldObj.getWorldChunkManager().getBiomeGenAt((var5 += var7.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures)) * 16 + 8, (var6 += var7.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures)) * 16 + 8);
        for (Object var10 : BiomeGenUnderworld.biomelist) {
            if (var8 != var10) continue;
            return new ChunkPosition(var5, 0, var6);
        }
        return null;
    }
    @Override
    public int getCheckRange() {
        return this.maxDistanceBetweenScatteredFeatures;
    }
}