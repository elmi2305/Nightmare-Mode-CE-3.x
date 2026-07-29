package com.itlesports.nightmaremode.worldgen;

import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

/** Places clay-sized aquamarine deposits in submerged dirt or clay on the ocean floor. */
public class WorldGenAquamarineOre extends WorldGenerator {
    private final int oreBlockId;
    private final int maxRadius;

    public WorldGenAquamarineOre(int oreBlockId, int maxRadius) {
        this.oreBlockId = oreBlockId;
        this.maxRadius = maxRadius;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        if (world.getBlockMaterial(x, y, z) != Material.water) {
            return false;
        }

        int radius = random.nextInt(this.maxRadius - 1) + 1;
        for (int candidateX = x - radius; candidateX <= x + radius; ++candidateX) {
            for (int candidateZ = z - radius; candidateZ <= z + radius; ++candidateZ) {
                int offsetX = candidateX - x;
                int offsetZ = candidateZ - z;
                if (offsetX * offsetX + offsetZ * offsetZ > radius * radius) {
                    continue;
                }
                for (int candidateY = y - 1; candidateY <= y + 1; ++candidateY) {
                    int blockId = world.getBlockId(candidateX, candidateY, candidateZ);
                    if (blockId == Block.dirt.blockID || blockId == Block.blockClay.blockID) {
                        world.setBlock(candidateX, candidateY, candidateZ, this.oreBlockId, 0, 2);
                    }
                }
            }
        }
        return true;
    }
}
