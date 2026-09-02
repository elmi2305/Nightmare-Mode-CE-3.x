package com.itlesports.nightmaremode.structure;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.MapGenStructure;
import net.minecraft.src.SpawnListEntry;
import net.minecraft.src.StructureStart;
import net.minecraft.src.World;

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
        if (!NetherTierHelper.isChunkEntirelyTierZero(this.worldObj, chunkX, chunkZ)) {
            return false;
        }
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

    /**
     * Finds the nearest possible temple without loading or generating its chunk.
     * Nether temples occupy only the finite inner-Nether band, so all eligible
     * regions can be checked deterministically.
     */
    public static ChunkPosition findNearestTemple(World world, int x, int z) {
        int spawnX = world.getSpawnPoint().posX;
        int spawnZ = world.getSpawnPoint().posZ;
        int minChunkX = (int) Math.floor((spawnX - 1024.0D) / 16.0D);
        int maxChunkX = (int) Math.floor((spawnX + 1024.0D) / 16.0D);
        int minChunkZ = (int) Math.floor((spawnZ - 1024.0D) / 16.0D);
        int maxChunkZ = (int) Math.floor((spawnZ + 1024.0D) / 16.0D);
        ChunkPosition nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int regionX = regionForChunk(minChunkX); regionX <= regionForChunk(maxChunkX); ++regionX) {
            for (int regionZ = regionForChunk(minChunkZ); regionZ <= regionForChunk(maxChunkZ); ++regionZ) {
                Random random = new Random((long) regionX * 341873128712L
                        + (long) regionZ * 132897987541L + world.getSeed() + 14357617L);
                int chunkX = regionX * MAX_DISTANCE + random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
                int chunkZ = regionZ * MAX_DISTANCE + random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
                if (!NetherTierHelper.isChunkEntirelyTierZero(world, chunkX, chunkZ)) {
                    continue;
                }
                int templeX = chunkX * 16 + 8;
                int templeZ = chunkZ * 16 + 8;
                double distance = (double) (templeX - x) * (templeX - x)
                        + (double) (templeZ - z) * (templeZ - z);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = new ChunkPosition(templeX, 64, templeZ);
                }
            }
        }
        return nearest;
    }

    private static int regionForChunk(int chunk) {
        return chunk < 0 ? (chunk - (MAX_DISTANCE - 1)) / MAX_DISTANCE : chunk / MAX_DISTANCE;
    }

    public List getSpawnList() {
        return spawnList;
    }
}
