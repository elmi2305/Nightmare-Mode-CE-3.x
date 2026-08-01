package com.itlesports.nightmaremode.worldgen;

import net.minecraft.src.ChunkPosition;
import net.minecraft.src.World;

import java.util.Random;

public final class StructureSpacingHelper {
    private StructureSpacingHelper() {}

    public static boolean isCandidateChunk(World world, int chunkX, int chunkZ, int minimum, int maximum) {
        ChunkPosition candidate = getCandidateForCell(world, chunkX, chunkZ, minimum, maximum);
        return candidate.x == chunkX && candidate.z == chunkZ;
    }

    public static ChunkPosition getCandidateForCell(World world, int chunkX, int chunkZ,
                                                     int minimum, int maximum) {
        validateSpacing(minimum, maximum);
        int cellSize = (minimum + maximum + 1) / 2;
        int jitter = maximum - cellSize;
        int cellX = Math.floorDiv(chunkX, cellSize);
        int cellZ = Math.floorDiv(chunkZ, cellSize);
        Random random = world.setRandomSeed(cellX, cellZ, 0);
        int candidateX = cellX * cellSize + (jitter == 0 ? 0 : random.nextInt(jitter + 1));
        int candidateZ = cellZ * cellSize + (jitter == 0 ? 0 : random.nextInt(jitter + 1));
        return new ChunkPosition(candidateX, 0, candidateZ);
    }

    private static void validateSpacing(int minimum, int maximum) {
        if (minimum < 1 || maximum < minimum) {
            throw new IllegalArgumentException("structure spacing must satisfy 1 <= minimum <= maximum");
        }
    }
}
