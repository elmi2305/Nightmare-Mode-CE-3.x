package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.Direction;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenSimpleTree extends WorldGenerator {
    private final int minTreeHeight;
    private final boolean vinesGrow;
    private final int metaWood;
    private final int metaLeaves;

    public WorldGenSimpleTree(boolean par1) {
        this(par1, 4, 0, 0, false);
    }

    public WorldGenSimpleTree(boolean notifyBlocks, int minTreeHeight, int woodMeta, int leafMeta, boolean vineGrow) {
        super(notifyBlocks);
        this.minTreeHeight = minTreeHeight;
        this.metaWood = woodMeta;
        this.metaLeaves = leafMeta;
        this.vinesGrow = vineGrow;
    }

    public boolean generate(World world, Random rand, int x, int y, int z) {
        int treeHeight = rand.nextInt(3) + this.minTreeHeight;

        if (!isWithinWorldBounds(y, treeHeight)) {
            return false;
        }
        if (!canTreeFit(world, x, y, z, treeHeight)) {
            return false;
        }

        int blockBelowBase = world.getBlockId(x, y - 1, z);
        boolean isOnFertileGround = blockBelowBase == Block.grass.blockID
                || blockBelowBase == Block.dirt.blockID
                || blockBelowBase == NMBlocks.underFlowerDirts.blockID
                || blockBelowBase == NMBlocks.underGrass.blockID;
        if (!isOnFertileGround || y >= 256 - treeHeight - 1) {
            return false;
        }

        this.setBlock(world, x, y - 1, z, Block.dirt.blockID);

        placeLeafCanopy(world, rand, x, y, z, treeHeight);
        placeTrunk(world, rand, x, y, z, treeHeight);

        if (this.vinesGrow) {
            growVinesFromLeaves(world, rand, x, y, z, treeHeight);
            placeCocoaPods(world, rand, x, y, z, treeHeight);
        }

        setTrunkBaseConnectionMetadata(world, x, y, z, treeHeight);
        return true;
    }

    private boolean isWithinWorldBounds(int baseY, int treeHeight) {
        return baseY >= 1 && baseY + treeHeight + 1 <= 256;
    }

    private boolean canTreeFit(World world, int x, int y, int z, int treeHeight) {
        for (int checkY = y; checkY <= y + 1 + treeHeight; checkY++) {
            int radius = getSpaceCheckRadius(checkY, y, treeHeight);
            for (int checkX = x - radius; checkX <= x + radius; checkX++) {
                for (int checkZ = z - radius; checkZ <= z + radius; checkZ++) {
                    if (checkY < 0 || checkY >= 256) {
                        return false;
                    }
                    if (!isReplaceableByTree(world.getBlockId(checkX, checkY, checkZ))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int getSpaceCheckRadius(int checkY, int baseY, int treeHeight) {
        if (checkY == baseY)              return 0;
        if (checkY >= baseY + treeHeight - 1) return 2;
        return 1;
    }

    private boolean isReplaceableByTree(int blockId) {
        return blockId == 0
                || blockId == Block.leaves.blockID
                || blockId == Block.grass.blockID
                || blockId == Block.dirt.blockID
                || blockId == Block.wood.blockID;
    }

    private void placeLeafCanopy(World world, Random rand, int x, int y, int z, int treeHeight) {
        final int LEAF_DEPTH = 3;
        int topY = y + treeHeight;

        for (int leafY = topY - LEAF_DEPTH; leafY <= topY; leafY++) {
            int distanceFromTop = leafY - topY;
            int leafRadius = 1 - distanceFromTop / 2;

            for (int leafX = x - leafRadius; leafX <= x + leafRadius; leafX++) {
                int dx = leafX - x;
                for (int leafZ = z - leafRadius; leafZ <= z + leafRadius; leafZ++) {
                    int dz = leafZ - z;

                    boolean isCorner = Math.abs(dx) == leafRadius && Math.abs(dz) == leafRadius;
                    boolean skipCorner = isCorner && (distanceFromTop == 0 || rand.nextInt(2) == 0);
                    if (skipCorner) {
                        continue;
                    }

                    int existingBlock = world.getBlockId(leafX, leafY, leafZ);
                    boolean canReplace = existingBlock == 0 || existingBlock == Block.leaves.blockID;
                    if (!canReplace) {
                        continue;
                    }

                    this.setBlockAndMetadata(world, leafX, leafY, leafZ, Block.leaves.blockID, this.metaLeaves);
                }
            }
        }
    }

    private void placeTrunk(World world, Random rand, int x, int y, int z, int treeHeight) {
        for (int i = 0; i < treeHeight; i++) {
            int existingBlock = world.getBlockId(x, y + i, z);
            boolean canPlace = existingBlock == 0 || existingBlock == Block.leaves.blockID;
            if (!canPlace) {
                continue;
            }
            this.setBlockAndMetadata(world, x, y + i, z, Block.wood.blockID, this.metaWood);

            if (this.vinesGrow && i > 0) {
                placeTrunkVines(world, rand, x, y + i, z);
            }
        }
    }

    private void placeTrunkVines(World world, Random rand, int x, int y, int z) {
        if (rand.nextInt(3) > 0 && world.isAirBlock(x - 1, y, z)) {
            this.setBlockAndMetadata(world, x - 1, y, z, Block.vine.blockID, 8);
        }
        if (rand.nextInt(3) > 0 && world.isAirBlock(x + 1, y, z)) {
            this.setBlockAndMetadata(world, x + 1, y, z, Block.vine.blockID, 2);
        }
        if (rand.nextInt(3) > 0 && world.isAirBlock(x, y, z - 1)) {
            this.setBlockAndMetadata(world, x, y, z - 1, Block.vine.blockID, 1);
        }
        if (rand.nextInt(3) > 0 && world.isAirBlock(x, y, z + 1)) {
            this.setBlockAndMetadata(world, x, y, z + 1, Block.vine.blockID, 4);
        }
    }

    private void growVinesFromLeaves(World world, Random rand, int x, int y, int z, int treeHeight) {
        final int LEAF_DEPTH = 3;
        int topY = y + treeHeight;

        for (int leafY = topY - LEAF_DEPTH; leafY <= topY; leafY++) {
            int distanceFromTop = leafY - topY;

            int scanRadius = 2 - distanceFromTop / 2;

            for (int leafX = x - scanRadius; leafX <= x + scanRadius; leafX++) {
                for (int leafZ = z - scanRadius; leafZ <= z + scanRadius; leafZ++) {
                    if (world.getBlockId(leafX, leafY, leafZ) != Block.leaves.blockID) {
                        continue;
                    }
                    if (rand.nextInt(4) == 0 && world.getBlockId(leafX - 1, leafY, leafZ) == 0) {
                        this.extendVineDownward(world, leafX - 1, leafY, leafZ, 8);
                    }
                    if (rand.nextInt(4) == 0 && world.getBlockId(leafX + 1, leafY, leafZ) == 0) {
                        this.extendVineDownward(world, leafX + 1, leafY, leafZ, 2);
                    }
                    if (rand.nextInt(4) == 0 && world.getBlockId(leafX, leafY, leafZ - 1) == 0) {
                        this.extendVineDownward(world, leafX, leafY, leafZ - 1, 1);
                    }
                    if (rand.nextInt(4) == 0 && world.getBlockId(leafX, leafY, leafZ + 1) == 0) {
                        this.extendVineDownward(world, leafX, leafY, leafZ + 1, 4);
                    }
                }
            }
        }
    }

    private void placeCocoaPods(World world, Random rand, int x, int y, int z, int treeHeight) {
        if (treeHeight <= 5 || rand.nextInt(5) != 0) {
            return;
        }
        for (int row = 0; row < 2; row++) {
            for (int side = 0; side < 4; side++) {
                if (rand.nextInt(4 - row) != 0) {
                    continue;
                }
                int cocoaAge  = rand.nextInt(3);
                int podX      = x + Direction.offsetX[Direction.rotateOpposite[side]];
                int podY      = y + treeHeight - 5 + row;
                int podZ      = z + Direction.offsetZ[Direction.rotateOpposite[side]];

                this.setBlockAndMetadata(world, podX, podY, podZ,
                        Block.cocoaPlant.blockID, cocoaAge << 2 | side);
            }
        }
    }

    private void setTrunkBaseConnectionMetadata(World world, int x, int y, int z, int treeHeight) {
        if (treeHeight <= 2) {
            return;
        }
        int trunkBlockId  = world.getBlockId(x, y, z);
        int trunkMetadata = world.getBlockMetadata(x, y, z);
        if (trunkBlockId == Block.wood.blockID && trunkMetadata == this.metaWood) {
            world.setBlockMetadataWithClient(x, y, z, trunkMetadata | 0xC);
        }
    }

    private void extendVineDownward(World world, int x, int y, int z, int vineFaceMeta) {
        this.setBlockAndMetadata(world, x, y, z, Block.vine.blockID, vineFaceMeta);
        int remainingLength = 4;
        while (world.getBlockId(x, --y, z) == 0 && remainingLength > 0) {
            this.setBlockAndMetadata(world, x, y, z, Block.vine.blockID, vineFaceMeta);
            --remainingLength;
        }
    }
}
