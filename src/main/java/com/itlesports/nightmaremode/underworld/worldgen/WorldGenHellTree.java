package com.itlesports.nightmaremode.underworld.worldgen;

import btw.block.BTWBlocks;
import btw.block.blocks.BloodWoodLogBlock;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenHellTree extends WorldGenerator {

    private static final int BASE_TRUNK_HEIGHT = 3;
    private static final int TOTAL_TREE_HEIGHT = BASE_TRUNK_HEIGHT + 1;  // = 4
    private static final int META_TRUNK = 0;
    private static final int META_APEX = 1;
    private static final int NATURAL_MATURITY_PASSES = 2;


    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {

        if (!isWithinWorldBounds(y)) {
            return false;
        }
        if (!isRootedInCorrectBlock(world, x, y, z)) return false;
        if (!hasClearColumnAbove(world, x, y, z)) return false;

        BloodWoodLogBlock bloodWoodBlock = (BloodWoodLogBlock) BTWBlocks.bloodWoodLog;

        placeBaseTrunk(world, x, y, z);

        int apexY = placeApexBlock(world, bloodWoodBlock, rand, x, y, z);

        // Mirror the sapling's second pass: grow branches that the apex just placed
        growNeighboringBranches(world, bloodWoodBlock, rand, x, apexY, z);

        // Extra passes to produce a mature, naturally-established tree
        matureTree(world, bloodWoodBlock, rand, x, y, apexY, z);

        world.playAuxSFX(2228, x, y, z, 0);
        return true;
    }

    private boolean isWithinWorldBounds(int baseY) {
        return baseY >= 1 && baseY + TOTAL_TREE_HEIGHT + 1 < 256;
    }
    private boolean isRootedInCorrectBlock(World world, int x, int y, int z) {
        return world.getBlockId(x, y - 1, z) == NMBlocks.hellStones.blockID;
    }

    private boolean hasClearColumnAbove(World world, int x, int y, int z) {
        for (int checkY = y; checkY < y + TOTAL_TREE_HEIGHT; checkY++) {
            if (checkY >= 256 || !world.isAirBlock(x, checkY, z)) {
                return false;
            }
        }
        return true;
    }



    private void placeBaseTrunk(World world, int x, int y, int z) {
        for (int trunkY = y; trunkY < y + BASE_TRUNK_HEIGHT; trunkY++) {
            world.setBlockAndMetadataWithNotify(x, trunkY, z,
                    BTWBlocks.bloodWoodLog.blockID, META_TRUNK);
        }
    }

    private int placeApexBlock(World world, BloodWoodLogBlock bloodWoodBlock,
                               Random rand, int x, int y, int z) {
        int apexY = y + BASE_TRUNK_HEIGHT;
        world.setBlockAndMetadataWithNotify(x, apexY, z,
                BTWBlocks.bloodWoodLog.blockID, META_APEX);
        bloodWoodBlock.growLeaves(world, x, apexY, z);
        bloodWoodBlock.grow(world, x, apexY, z, rand);
        return apexY;
    }

    private void growNeighboringBranches(World world, BloodWoodLogBlock bloodWoodBlock,
                                         Random rand, int apexX, int apexY, int apexZ) {
        for (int scanX = apexX - 1; scanX <= apexX + 1; scanX++) {
            for (int scanY = apexY; scanY <= apexY + 1; scanY++) {
                for (int scanZ = apexZ - 1; scanZ <= apexZ + 1; scanZ++) {
                    boolean isApex = scanX == apexX && scanY == apexY && scanZ == apexZ;
                    if (isApex) continue;

                    if (world.getBlockId(scanX, scanY, scanZ) != BTWBlocks.bloodWoodLog.blockID) continue;

                    int growthDirection = bloodWoodBlock.getFacing(world, scanX, scanY, scanZ);
                    if (growthDirection == 0) continue;

                    bloodWoodBlock.grow(world, scanX, scanY, scanZ, rand);
                }
            }
        }
    }


    private void matureTree(World world, BloodWoodLogBlock bloodWoodBlock,
                            Random rand, int x, int baseY, int apexY, int z) {
        for (int pass = 0; pass < NATURAL_MATURITY_PASSES; pass++) {
            // Widen horizontally and upward each pass to catch new growth from the prior pass
            int horizontalRange = 2 + pass;
            int topY            = apexY + 2 + pass;

            for (int scanX = x - horizontalRange; scanX <= x + horizontalRange; scanX++) {
                for (int scanY = baseY; scanY <= topY; scanY++) {
                    for (int scanZ = z - horizontalRange; scanZ <= z + horizontalRange; scanZ++) {
                        if (world.getBlockId(scanX, scanY, scanZ) != BTWBlocks.bloodWoodLog.blockID) continue;

                        int growthDirection = bloodWoodBlock.getFacing(world, scanX, scanY, scanZ);
                        if (growthDirection == 0) continue;

                        bloodWoodBlock.grow(world, scanX, scanY, scanZ, rand);
                    }
                }
            }
        }
    }
}
