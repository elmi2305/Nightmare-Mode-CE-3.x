package com.itlesports.nightmaremode.util;

import net.minecraft.src.Block;
import net.minecraft.src.WorldServer;

public final class NetherArrivalSearch {
    private NetherArrivalSearch() {}

    public static int[] find(WorldServer world, int centerX, int centerY, int centerZ) {
        int[] open = null;
        int[] fallback = null;
        double bestOpen = -Double.MAX_VALUE;
        double bestFallback = Double.MAX_VALUE;
        int ceiling = Math.min(120, world.getActualHeight() - 5);
        for (int x = centerX - 32; x <= centerX + 32; x++) {
            for (int z = centerZ - 32; z <= centerZ + 32; z++) {
                for (int y = ceiling; y >= 5; y--) {
                    if (!hasGround(world, x, y, z)) continue;
                    double distance = (x - centerX) * (x - centerX) + (z - centerZ) * (z - centerZ)
                            + Math.abs(y + 1 - centerY) * 4.0D;
                    if (distance < bestFallback && supportedFootprint(world, x, y, z)
                            && dryArrivalArea(world, x, y, z)) {
                        fallback = new int[]{x, y, z};
                        bestFallback = distance;
                    }
                    if (!world.isAirBlock(x, y + 1, z)) continue;
                    int clearance = 0;
                    while (clearance < 32 && y + clearance + 1 < ceiling
                            && world.isAirBlock(x, y + clearance + 1, z)) clearance++;
                    double score = clearance * 12.0D - distance / 32.0D;
                    if (clearance < 3 || score <= bestOpen || !supportedFootprint(world, x, y, z)
                            || !clearFootprint(world, x, y, z) || !dryArrivalArea(world, x, y, z)) continue;
                    open = new int[]{x, y, z};
                    bestOpen = score;
                }
            }
        }
        return open != null ? open : fallback;
    }

    private static boolean hasGround(WorldServer world, int x, int y, int z) {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        return block != null && block.blockMaterial.blocksMovement() && !block.blockMaterial.isLiquid()
                && block != Block.bedrock;
    }

    private static boolean supportedFootprint(WorldServer world, int x, int y, int z) {
        for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) {
            if (!hasGround(world, x + dx, y, z + dz)) return false;
        }
        return true;
    }

    private static boolean clearFootprint(WorldServer world, int x, int y, int z) {
        for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) {
            for (int dy = 1; dy <= 3; dy++) {
                if (!world.isAirBlock(x + dx, y + dy, z + dz)) return false;
            }
        }
        return true;
    }

    private static boolean dryArrivalArea(WorldServer world, int x, int y, int z) {
        for (int dx = -3; dx <= 3; dx++) for (int dz = -3; dz <= 3; dz++) {
            for (int dy = 1; dy <= 4; dy++) {
                Block block = Block.blocksList[world.getBlockId(x + dx, y + dy, z + dz)];
                if (block != null && block.blockMaterial.isLiquid()) return false;
            }
        }
        return true;
    }
}
