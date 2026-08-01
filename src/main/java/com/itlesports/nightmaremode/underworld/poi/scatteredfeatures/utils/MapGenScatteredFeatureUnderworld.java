package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.BigMushroom;
import com.itlesports.nightmaremode.worldgen.StructureSpacingHelper;
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
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(var3 * 16 + 8, var4 * 16 + 8);
        boolean isBigMushroom = biome instanceof BiomeGenFlowerFields;
        if (isBigMushroom) {
            return StructureSpacingHelper.isCandidateChunk(this.worldObj, var3, var4,
                    BigMushroom.MIN_CHUNKS_APART, BigMushroom.MAX_CHUNKS_APART);
        }

        int maximum = maxDistanceBetweenScatteredFeatures;
        int minimum = minDistanceBetweenScatteredFeatures;
        if (par1 < 0) par1 -= maximum - 1;
        if (par2 < 0) par2 -= maximum - 1;
        int var5 = par1 / maximum;
        int var6 = par2 / maximum;
        Random var7 = this.worldObj.setRandomSeed(var5, var6, 14357617);
        var5 = var5 * maximum + var7.nextInt(maximum - minimum);
        var6 = var6 * maximum + var7.nextInt(maximum - minimum);

        if (var3 == var5 && var4 == var6) {
            if (biome instanceof BiomeGenHighlands || biome instanceof BiomeGenBlightlands) {
                return var7.nextInt(5) == 0;
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
        ChunkPosition mushroom = StructureSpacingHelper.getCandidateForCell(this.worldObj, par1, par2,
                BigMushroom.MIN_CHUNKS_APART, BigMushroom.MAX_CHUNKS_APART);
        if (this.worldObj.getBiomeGenForCoords(mushroom.x * 16 + 8, mushroom.z * 16 + 8)
                instanceof BiomeGenFlowerFields) {
            return mushroom;
        }

        ChunkPosition other = getCandidate(par1, par2, this.maxDistanceBetweenScatteredFeatures,
                this.minDistanceBetweenScatteredFeatures, 14357617);
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(other.x * 16 + 8, other.z * 16 + 8);
        for (Object allowedBiome : BiomeGenUnderworld.biomelist) {
            if (biome == allowedBiome) {
                return other;
            }
        }
        return null;
    }

    private ChunkPosition getCandidate(int chunkX, int chunkZ, int maximum, int minimum, int salt) {
        if (chunkX < 0) chunkX -= maximum - 1;
        if (chunkZ < 0) chunkZ -= maximum - 1;
        int regionX = chunkX / maximum;
        int regionZ = chunkZ / maximum;
        Random random = this.worldObj.setRandomSeed(regionX, regionZ, salt);
        regionX = regionX * maximum + random.nextInt(maximum - minimum);
        regionZ = regionZ * maximum + random.nextInt(maximum - minimum);
        return new ChunkPosition(regionX, 0, regionZ);
    }
    @Override
    public int getCheckRange() {
        return Math.max(this.maxDistanceBetweenScatteredFeatures, BigMushroom.MAX_CHUNKS_APART);
    }
}
