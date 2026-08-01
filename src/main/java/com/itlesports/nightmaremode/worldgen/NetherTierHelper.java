package com.itlesports.nightmaremode.worldgen;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.World;

public final class NetherTierHelper {
    public static int NETHERRACK_BLEND_WIDTH = 5;

    private NetherTierHelper() {}

    public static double getDistanceFromSpawn(World world, double x, double z) {
        ChunkCoordinates spawn = world.getSpawnPoint();
        double deltaX = x - spawn.posX;
        double deltaZ = z - spawn.posZ;
        return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
    }

    public static int getTier(World world, double x, double z) {
        double distance = getDistanceFromSpawn(world, x, z);
        return distance >= 3000.0D ? 3 : distance >= 2000.0D ? 2 : distance >= 1000.0D ? 1 : 0;
    }

    public static int getNetherrackMetadata(World world, int x, int z) {
        double distance = getDistanceFromSpawn(world, x, z);
        int tier = distance >= 3000.0D ? 3 : distance >= 2000.0D ? 2 : distance >= 1000.0D ? 1 : 0;
        int width = Math.max(0, NETHERRACK_BLEND_WIDTH);
        if (width == 0 || tier == 3) {
            return tier == 0 ? 0 : tier + 1;
        }

        double nextBorder = (tier + 1) * 1000.0D;
        double blendProgress = (distance - (nextBorder - width)) / width;
        if (blendProgress > 0.0D && deterministicChance(world, x, z) < blendProgress) {
            ++tier;
        }
        return tier == 0 ? 0 : tier + 1;
    }

    public static boolean isAreaEntirelyInTier(World world, double x, double z, double radius, int tier) {
        double distance = getDistanceFromSpawn(world, x, z);
        double minimum = tier == 0 ? 0.0D : tier * 1000.0D;
        double maximum = tier == 3 ? Double.POSITIVE_INFINITY : (tier + 1) * 1000.0D;
        return distance - radius >= minimum && distance + radius < maximum;
    }

    public static boolean isChunkEntirelyTierZero(World world, int chunkX, int chunkZ) {
        double centerX = chunkX * 16 + 8;
        double centerZ = chunkZ * 16 + 8;
        double chunkRadius = Math.sqrt(128.0D) + Math.max(0, NETHERRACK_BLEND_WIDTH);
        return isAreaEntirelyInTier(world, centerX, centerZ, chunkRadius, 0);
    }

    private static double deterministicChance(World world, int x, int z) {
        long value = world.getSeed();
        value ^= (long) x * 341873128712L;
        value ^= (long) z * 132897987541L;
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdL;
        value ^= value >>> 33;
        return (value & 0x1fffffffffffffL) / (double) 0x20000000000000L;
    }
}
