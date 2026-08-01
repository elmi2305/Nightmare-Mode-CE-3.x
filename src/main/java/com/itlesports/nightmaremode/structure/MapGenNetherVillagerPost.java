package com.itlesports.nightmaremode.structure;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.MapGenStructure;
import net.minecraft.src.StructureStart;

import java.util.Random;

public class MapGenNetherVillagerPost extends MapGenStructure {
    private static final int MAX_DISTANCE = 32;
    private static final int MIN_DISTANCE = 8;

    @Override
    public String func_143025_a() {
        return "NMNetherVillagerPost";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int originalX = chunkX;
        int originalZ = chunkZ;
        if (chunkX < 0) chunkX -= MAX_DISTANCE - 1;
        if (chunkZ < 0) chunkZ -= MAX_DISTANCE - 1;
        int regionX = chunkX / MAX_DISTANCE;
        int regionZ = chunkZ / MAX_DISTANCE;
        Random random = this.worldObj.setRandomSeed(regionX, regionZ, 0x4e4d5650);
        regionX = regionX * MAX_DISTANCE + random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
        regionZ = regionZ * MAX_DISTANCE + random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
        if (originalX != regionX || originalZ != regionZ) {
            return false;
        }

        double centerX = originalX * 16 + 8;
        double centerZ = originalZ * 16 + 8;
        int tier = NetherTierHelper.getTier(this.worldObj, centerX, centerZ);
        double radius = tier == 1 ? 13.0D : tier == 2 ? 21.0D : tier == 3 ? 35.0D : 0.0D;
        return tier > 0 && NetherTierHelper.isAreaEntirelyInTier(this.worldObj, centerX, centerZ, radius, tier);
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
        return MAX_DISTANCE;
    }
}
