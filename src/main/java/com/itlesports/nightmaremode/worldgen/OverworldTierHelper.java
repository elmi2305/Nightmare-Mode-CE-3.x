package com.itlesports.nightmaremode.worldgen;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.ChunkCoordIntPair;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class OverworldTierHelper {
    public static final double MOB_SCALING_START = 5000.0D;
    public static final double DEADZONE_START = 20000.0D;
    public static final double CRUEL_DESERT_START = 30000.0D;
    public static final double GREAT_VOID_START = 35000.0D;
    public static final double LOST_OCEAN_START = 40000.0D;
    public static final double FROZEN_WASTES_START = 45000.0D;
    public static final double STRONGHOLD_RADIUS = 50120.0D;
    public static final double SURFACE_BLEND_LENGTH = 160.0D;
    public static final double VOID_SLOPE_LENGTH = 20.0D;
    public static final double OCEAN_SLOPE_LENGTH = 256.0D;
    public static final double FROZEN_SLOPE_LENGTH = 192.0D;

    private OverworldTierHelper() {}

    public enum Region {
        INNER,
        DEADZONE,
        CRUEL_DESERT,
        GREAT_VOID,
        LOST_OCEAN,
        FROZEN_WASTES,
        BEYOND
    }

    public static double getDistanceFromSpawn(World world, double x, double z) {
        if (world == null) return 0.0D;
        ChunkCoordinates spawn = world.getSpawnPoint();
        double dx = x - spawn.posX;
        double dz = z - spawn.posZ;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static Region getRegion(World world, double x, double z) {
        if (world == null || world.provider.dimensionId != 0) return Region.INNER;
        double distance = getDistanceFromSpawn(world, x, z);
        if (distance < DEADZONE_START) return Region.INNER;
        if (distance < CRUEL_DESERT_START) return Region.DEADZONE;
        if (distance < GREAT_VOID_START) return Region.CRUEL_DESERT;
        if (distance < LOST_OCEAN_START) return Region.GREAT_VOID;
        if (distance < FROZEN_WASTES_START) return Region.LOST_OCEAN;
        if (distance < STRONGHOLD_RADIUS) return Region.FROZEN_WASTES;
        return Region.BEYOND;
    }

    public static boolean isOuterOverworld(World world, double x, double z) {
        return world != null && world.provider.dimensionId == 0
                && getDistanceFromSpawn(world, x, z) >= DEADZONE_START;
    }

    public static boolean hasNoVanillaCaves(World world, double x, double z) {
        double distance = getDistanceFromSpawn(world, x, z);
        return world != null && world.provider.dimensionId == 0
                && distance >= CRUEL_DESERT_START && distance < STRONGHOLD_RADIUS;
    }

    public static boolean isPortalBlocked(World world, double x, double z) {
        if (world == null) return false;
        if (world.provider.dimensionId == 0) {
            return getDistanceFromSpawn(world, x, z) >= DEADZONE_START;
        }
        if (world.provider.dimensionId == -1) {
            return getDistanceFromSpawn(world, x, z) >= 2500.0D;
        }
        return false;
    }

    public static double getMobScalingProgress(World world, double x, double z) {
        if (world == null || world.provider.dimensionId != 0) return 0.0D;
        double distance = getDistanceFromSpawn(world, x, z);
        return clamp((distance - MOB_SCALING_START) / (STRONGHOLD_RADIUS - MOB_SCALING_START), 0.0D, 1.0D);
    }

    public static double getDeadzoneWarpProgress(World world, double x, double z) {
        double distance = getDistanceFromSpawn(world, x, z);
        return clamp((distance - DEADZONE_START) / (CRUEL_DESERT_START - DEADZONE_START), 0.0D, 1.0D);
    }

    public static List<ChunkPosition> getStrongholdPositions(World world) {
        ChunkCoordinates spawn = world.getSpawnPoint();
        Random random = new Random(world.getSeed() ^ 0x5354524F4E47484FL);
        double initialAngle = random.nextDouble() * Math.PI * 2.0D;
        List<ChunkPosition> positions = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            double angle = initialAngle + i * Math.PI * 2.0D / 3.0D;
            int blockX = spawn.posX + (int)Math.round(Math.cos(angle) * STRONGHOLD_RADIUS);
            int blockZ = spawn.posZ + (int)Math.round(Math.sin(angle) * STRONGHOLD_RADIUS);
            positions.add(new ChunkCoordIntPair(blockX >> 4, blockZ >> 4).getChunkPosition(64));
        }
        return positions;
    }

    public static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    public static double smoothstep(double value) {
        double clamped = clamp(value, 0.0D, 1.0D);
        return clamped * clamped * (3.0D - 2.0D * clamped);
    }
}
