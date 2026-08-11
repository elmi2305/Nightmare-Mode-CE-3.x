package com.itlesports.nightmaremode.structure;

import com.itlesports.nightmaremode.worldgen.StructureSpacingHelper;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.MapGenStructure;
import net.minecraft.src.StructureStart;

public class MapGenSkyZiggurath extends MapGenStructure {
    @Override
    public String func_143025_a() {
        return "NMSkyZiggurath";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        if (this.worldObj.provider.dimensionId != 0 || !StructureSpacingHelper.isCandidateChunk(
                this.worldObj, chunkX, chunkZ,
                SkyZiggurath.MIN_CHUNKS_APART, SkyZiggurath.MAX_CHUNKS_APART)) {
            return false;
        }

        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8);
        return biome != null && biome != BiomeGenBase.ocean && biome != BiomeGenBase.frozenOcean;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new StructureSkyZiggurathStart(this.worldObj, this.rand, chunkX, chunkZ);
    }

    @Override
    protected ChunkPosition getSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return null;
    }

    @Override
    public int getCheckRange() {
        return SkyZiggurath.MAX_CHUNKS_APART;
    }
}
