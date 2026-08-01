package com.itlesports.nightmaremode.structure;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import com.itlesports.nightmaremode.worldgen.StructureSpacingHelper;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.MapGenStructure;
import net.minecraft.src.StructureStart;

public class MapGenNetherVillagerPost extends MapGenStructure {
    @Override
    public String func_143025_a() {
        return "NMNetherVillagerPost";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int originalX = chunkX;
        int originalZ = chunkZ;
        double centerX = originalX * 16 + 8;
        double centerZ = originalZ * 16 + 8;
        int tier = NetherTierHelper.getTier(this.worldObj, centerX, centerZ);
        if (tier == 0) {
            return false;
        }

        int maximum = getMaximumSpacing(tier);
        int minimum = getMinimumSpacing(tier);
        if (!StructureSpacingHelper.isCandidateChunk(
                this.worldObj, originalX, originalZ, minimum, maximum)) {
            return false;
        }

        double radius = tier == 1 ? 13.0D : tier == 2 ? 21.0D : tier == 3 ? 35.0D : 0.0D;
        return NetherTierHelper.isAreaEntirelyInTier(this.worldObj, centerX, centerZ, radius, tier);
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new StructureNetherVillagerPostStart(this.worldObj, this.rand, chunkX, chunkZ);
    }

    @Override
    protected ChunkPosition getSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return null;
    }

    @Override
    public int getCheckRange() {
        return Math.max(Tier1VillagerPost.MAX_CHUNKS_APART,
                Math.max(Tier2VillagerPost.MAX_CHUNKS_APART, Tier3VillagerPost.MAX_CHUNKS_APART));
    }

    private static int getMinimumSpacing(int tier) {
        return tier == 1 ? Tier1VillagerPost.MIN_CHUNKS_APART
                : tier == 2 ? Tier2VillagerPost.MIN_CHUNKS_APART
                : Tier3VillagerPost.MIN_CHUNKS_APART;
    }

    private static int getMaximumSpacing(int tier) {
        return tier == 1 ? Tier1VillagerPost.MAX_CHUNKS_APART
                : tier == 2 ? Tier2VillagerPost.MAX_CHUNKS_APART
                : Tier3VillagerPost.MAX_CHUNKS_APART;
    }
}
