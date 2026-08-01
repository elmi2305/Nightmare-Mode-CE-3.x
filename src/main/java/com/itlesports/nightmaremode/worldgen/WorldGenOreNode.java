package com.itlesports.nightmaremode.worldgen;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenOreNode extends WorldGenerator {
    private final int nodeBlockId;
    private final int replaceBlockId;
    private final int minVeinSize;
    private final int maxVeinSize;

    public WorldGenOreNode(int nodeBlockId, int replaceBlockId, int minVeinSize, int maxVeinSize) {
        this.nodeBlockId = nodeBlockId;
        this.replaceBlockId = replaceBlockId;
        this.minVeinSize = minVeinSize;
        this.maxVeinSize = maxVeinSize;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        int firstY = this.findReplaceableY(world, x, y, z);
        if (firstY < 0) {
            return false;
        }

        int targetSize = this.minVeinSize + random.nextInt(this.maxVeinSize - this.minVeinSize + 1);
        int placed = 0;
        List<int[]> vein = new ArrayList<>();
        vein.add(new int[]{x, firstY, z});

        for (int attempt = 0; attempt < targetSize * 8 && placed < targetSize; ++attempt) {
            int[] current = vein.get(random.nextInt(vein.size()));
            int currentX = current[0];
            int currentY = current[1];
            int currentZ = current[2];
            if (world.getBlockId(currentX, currentY, currentZ) == this.replaceBlockId) {
                this.setBlock(world, currentX, currentY, currentZ, this.nodeBlockId);
                ++placed;
            }

            int direction = random.nextInt(6);
            int nextX = currentX + (direction == 0 ? 1 : direction == 1 ? -1 : 0);
            int nextY = currentY + (direction == 2 ? 1 : direction == 3 ? -1 : 0);
            int nextZ = currentZ + (direction == 4 ? 1 : direction == 5 ? -1 : 0);
            if (nextY >= 4 && nextY < 60 && !this.contains(vein, nextX, nextY, nextZ)) {
                vein.add(new int[]{nextX, nextY, nextZ});
            }
        }
        return placed > 0;
    }

    private int findReplaceableY(World world, int x, int preferredY, int z) {
        for (int distance = 0; distance < 56; ++distance) {
            int above = preferredY + distance;
            if (above < 60 && world.getBlockId(x, above, z) == this.replaceBlockId) {
                return above;
            }
            int below = preferredY - distance;
            if (below >= 4 && world.getBlockId(x, below, z) == this.replaceBlockId) {
                return below;
            }
        }
        return -1;
    }

    private boolean contains(List<int[]> positions, int x, int y, int z) {
        for (int[] position : positions) {
            if (position[0] == x && position[1] == y && position[2] == z) {
                return true;
            }
        }
        return false;
    }
}
