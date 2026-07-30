package com.itlesports.nightmaremode.structure;

import net.minecraft.src.ChunkPosition;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.MapGenStructure;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.StructureStart;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapGenNetherDesertTemple extends MapGenStructure {
    private static final int MAX_DISTANCE = 32;
    private static final int MIN_DISTANCE = 8;
    private final List spawnList = new ArrayList();

    public MapGenNetherDesertTemple() {

        this.spawnList.add(new SpawnListEntry(EntitySkeleton.class, 100, 8, 12));
    }

    @Override
    public String func_143025_a() {
        return "NMNetherTemple";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int originalX = chunkX;
        int originalZ = chunkZ;
        if (chunkX < 0) chunkX -= MAX_DISTANCE - 1;
        if (chunkZ < 0) chunkZ -= MAX_DISTANCE - 1;
        int regionX = chunkX / MAX_DISTANCE;
        int regionZ = chunkZ / MAX_DISTANCE;
        Random random = worldObj.setRandomSeed(regionX, regionZ, 14357617);
        regionX *= MAX_DISTANCE;
        regionZ *= MAX_DISTANCE;
        regionX += random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
        regionZ += random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
        return originalX == regionX && originalZ == regionZ;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        return new StructureNetherDesertTempleStart(worldObj, rand, chunkX, chunkZ);
    }

    @Override
    protected ChunkPosition getSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return null;
    }

    @Override
    public int getCheckRange() {
        return MAX_DISTANCE;
    }

    public boolean hasTempleAt(int x, int y, int z) {
        if(this.worldObj == null){
            return false;
        }
        return this.hasStructureAt(x, y, z);
    }

    public List getSpawnList() {
        return spawnList;
    }
}
