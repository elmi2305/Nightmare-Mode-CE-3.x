package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.MapGenStructure;
import net.minecraft.src.StructureStart;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * one structure map owns every Underworld scattered feature. Feature identity,
 * progression role and placement are explicit instead of inferred from a biome.
 */
public class MapGenScatteredFeatureUnderworld extends MapGenStructure {
    public enum Feature {
        BIG_MUSHROOM(true, true, 28, 20, 0x5B17A91L),
        RIBCAGE_CLOSED(false, true, 12, 8, 0x19C4D31L),
        RIBCAGE_OPEN(false, true, 12, 8, 0x19C4D31L),
        OBSIDIAN_SPIKE(false, false, 12, 8, 0x4F08BC9L);

        public final boolean dungeon;
        public final boolean enabled;
        public final int spacing;
        public final int separation;
        public final long salt;

        Feature(boolean dungeon, boolean enabled, int spacing, int separation, long salt) {
            this.dungeon = dungeon;
            this.enabled = enabled;
            this.spacing = spacing;
            this.separation = separation;
            this.salt = salt;
        }

        boolean accepts(BiomeGenBase biome) {
            switch (this) {
                case BIG_MUSHROOM:
                    return biome == BiomeGenUnderworld.flowerFields;
                case RIBCAGE_CLOSED:
                case RIBCAGE_OPEN:
                    return biome == BiomeGenUnderworld.highlands || biome == BiomeGenUnderworld.blightlands;
                case OBSIDIAN_SPIKE:
                    return biome == BiomeGenUnderworld.underHell || biome == BiomeGenUnderworld.shadowRealm;
                default:
                    return false;
            }
        }
    }

    @Override
    public String func_143025_a() {
        return "nmUnderworldFeature";
    }

    public boolean shouldUseStructureSpawnTable(int x, int y, int z) {
        return false;
    }

    public List getScatteredFeatureSpawnList() {
        return Collections.emptyList();
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        return selectFeature(chunkX, chunkZ) != null;
    }

    Feature selectFeature(int chunkX, int chunkZ) {
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8);
        Feature dungeon = Feature.BIG_MUSHROOM;
        if (dungeon.enabled && dungeon.accepts(biome) && isCandidate(dungeon, chunkX, chunkZ)) {
            return dungeon;
        }

        Feature closed = Feature.RIBCAGE_CLOSED;
        Feature open = Feature.RIBCAGE_OPEN;
        if ((closed.enabled || open.enabled) && closed.accepts(biome) && isCandidate(closed, chunkX, chunkZ)) {
            if (!closed.enabled) return open;
            if (!open.enabled) return closed;
            long variantSeed = this.worldObj.getSeed() ^ (long)chunkX * 73428767L ^ (long)chunkZ * 912931L;
            return new Random(variantSeed).nextBoolean() ? closed : open;
        }
        return null;
    }

    private boolean isCandidate(Feature feature, int chunkX, int chunkZ) {
        int regionX = floorDiv(chunkX, feature.spacing);
        int regionZ = floorDiv(chunkZ, feature.spacing);
        long seed = this.worldObj.getSeed()
                + (long) regionX * 341873128712L
                + (long) regionZ * 132897987541L
                + feature.salt;
        Random random = new Random(seed);
        int spread = feature.spacing - feature.separation;
        int candidateX = regionX * feature.spacing + random.nextInt(spread);
        int candidateZ = regionZ * feature.spacing + random.nextInt(spread);
        return chunkX == candidateX && chunkZ == candidateZ;
    }

    private static int floorDiv(int value, int divisor) {
        return value < 0 ? (value - divisor + 1) / divisor : value / divisor;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        Feature feature = selectFeature(chunkX, chunkZ);
        if (NightmareMode.devMode && feature != null) {
            System.out.println("[Underworld/Features] " + feature.name() + " at chunk " + chunkX + "," + chunkZ
                    + " dungeon=" + feature.dungeon);
        }
        return new StructureScatteredFeatureStartUnderworld(this.rand, chunkX, chunkZ, feature);
    }

    @Override
    protected ChunkPosition getSpawnStructureAtCoords(int x, int z) {
        int centerChunkX = x >> 4;
        int centerChunkZ = z >> 4;
        for (int radius = 0; radius <= 64; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    int chunkX = centerChunkX + dx;
                    int chunkZ = centerChunkZ + dz;
                    if (selectFeature(chunkX, chunkZ) != null) {
                        return new ChunkPosition(chunkX * 16 + 8, 0, chunkZ * 16 + 8);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int getCheckRange() {
        return 28;
    }
}
