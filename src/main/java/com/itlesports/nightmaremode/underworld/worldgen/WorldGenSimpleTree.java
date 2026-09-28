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

    // Generates a single tree in the world. Handles oaks and jungle trees (vinesGrow = true).
// Call generate() as the entry point; all other methods below are private helpers.

    /**
     * Attempts to grow a tree at (x, y, z).
     * Returns true if the tree was placed, false if the location is unsuitable.
     */
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

// ---------------------------------------------------------------------------
//  Validation helpers
// ---------------------------------------------------------------------------

    /**
     * Returns true if the tree's full height fits within valid world coordinates.
     */
    private boolean isWithinWorldBounds(int baseY, int treeHeight) {
        return baseY >= 1 && baseY + treeHeight + 1 <= 256;
    }

    /**
     * Returns true if every block the tree would occupy can be safely replaced.
     * The checked volume widens near the top to account for the leaf canopy.
     */
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

    /**
     * Returns the horizontal clearance radius to check at a given height.
     *   - At the very base (y):          0  (only the trunk column)
     *   - Top two leaf layers:           2  (wide canopy check)
     *   - All middle trunk levels:       1  (narrow trunk check)
     */
    private int getSpaceCheckRadius(int checkY, int baseY, int treeHeight) {
        if (checkY == baseY)              return 0;
        if (checkY >= baseY + treeHeight - 1) return 2;
        return 1;
    }

    /**
     * Returns true for block types a growing tree is allowed to displace.
     */
    private boolean isReplaceableByTree(int blockId) {
        return blockId == 0                        // air
                || blockId == Block.leaves.blockID
                || blockId == Block.grass.blockID
                || blockId == Block.dirt.blockID
                || blockId == Block.wood.blockID;
    }

// ---------------------------------------------------------------------------
//  Placement: leaves
// ---------------------------------------------------------------------------

    /**
     * Places the leaf canopy across the top four y-levels of the tree.
     *
     * Leaf radius at each layer (0 = top of tree):
     *   offset  0  →  radius 1
     *   offset -1  →  radius 1
     *   offset -2  →  radius 2
     *   offset -3  →  radius 2
     *
     * Corner blocks of each layer are always skipped at the very top, and
     * skipped with 50% probability on lower layers, to round off the canopy.
     */
    private void placeLeafCanopy(World world, Random rand, int x, int y, int z, int treeHeight) {
        final int LEAF_DEPTH = 3; // number of layers below the top that receive leaves
        int topY = y + treeHeight;

        for (int leafY = topY - LEAF_DEPTH; leafY <= topY; leafY++) {
            int distanceFromTop = leafY - topY;            // 0 at top, -1, -2, -3 below
            int leafRadius = 1 - distanceFromTop / 2;      // 1 for top two layers, 2 for bottom two

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

// ---------------------------------------------------------------------------
//  Placement: trunk
// ---------------------------------------------------------------------------

    /**
     * Places the wood trunk from the base (y) up to the top (y + treeHeight - 1).
     * Skips any position that already holds a non-replaceable block.
     * On jungle trees (vinesGrow = true), vines are also placed on trunk sides.
     */
    private void placeTrunk(World world, Random rand, int x, int y, int z, int treeHeight) {
        for (int i = 0; i < treeHeight; i++) {
            int existingBlock = world.getBlockId(x, y + i, z);
            boolean canPlace = existingBlock == 0 || existingBlock == Block.leaves.blockID;
            if (!canPlace) {
                continue;
            }
            this.setBlockAndMetadata(world, x, y + i, z, Block.wood.blockID, this.metaWood);

            // Skip vine placement at the base block (i == 0) so vines don't start underground
            if (this.vinesGrow && i > 0) {
                placeTrunkVines(world, rand, x, y + i, z);
            }
        }
    }

    /**
     * Randomly places a vine on each of the four sides of a trunk block.
     * Each side independently has a 2/3 chance of receiving a vine.
     *
     * Vine metadata encodes the face of the trunk the vine clings to:
     *   8 = east face   (vine placed one block to the west,  clinging east)
     *   2 = west face   (vine placed one block to the east,  clinging west)
     *   1 = south face  (vine placed one block to the north, clinging south)
     *   4 = north face  (vine placed one block to the south, clinging north)
     */
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

// ---------------------------------------------------------------------------
//  Placement: vines from leaves (jungle trees only)
// ---------------------------------------------------------------------------

    /**
     * Grows hanging vines downward from exposed sides of leaf blocks.
     * Scans the same y-range and a slightly wider radius than the canopy to
     * capture all outer leaf edges. Each exposed side has a 1/4 chance of
     * spawning a vine that drapes up to 4 blocks downward.
     */
    private void growVinesFromLeaves(World world, Random rand, int x, int y, int z, int treeHeight) {
        final int LEAF_DEPTH = 3;
        int topY = y + treeHeight;

        for (int leafY = topY - LEAF_DEPTH; leafY <= topY; leafY++) {
            int distanceFromTop = leafY - topY;
            // One block wider than the canopy radius so we find all exposed outer faces
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

// ---------------------------------------------------------------------------
//  Placement: cocoa pods (jungle trees only)
// ---------------------------------------------------------------------------

    /**
     * Randomly places cocoa pods on the lower trunk of tall jungle trees.
     *
     * Conditions: tree height > 5, and only a 1-in-5 chance of triggering at all.
     * Pods spawn across 2 rows near the base of the canopy, one on each of the
     * 4 trunk faces, with decreasing probability on the upper row.
     * The cocoa pod's age (0–2) is randomised independently per pod.
     */
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
                // Metadata: upper 2 bits = age (0–2), lower 2 bits = trunk face (0–3)
                this.setBlockAndMetadata(world, podX, podY, podZ,
                        Block.cocoaPlant.blockID, cocoaAge << 2 | side);
            }
        }
    }

// ---------------------------------------------------------------------------
//  Trunk base metadata
// ---------------------------------------------------------------------------

    /**
     * Sets bits 2–3 of the base trunk block's metadata, which signals to the
     * renderer that this log connects to a block below it (correct Y-axis
     * orientation). Only applied when the tree is taller than 2 blocks and the
     * base block is still the expected wood type.
     */
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

// ---------------------------------------------------------------------------
//  Vine utility
// ---------------------------------------------------------------------------

    /**
     * Places a vine at (x, y, z) and then continues placing vines downward
     * through consecutive air blocks, up to a maximum length of 4.
     *
     * @param vineFaceMeta  Metadata indicating which face of the adjacent block
     *                      the vine clings to (see placeTrunkVines for values).
     */
    private void extendVineDownward(World world, int x, int y, int z, int vineFaceMeta) {
        this.setBlockAndMetadata(world, x, y, z, Block.vine.blockID, vineFaceMeta);
        int remainingLength = 4;
        while (world.getBlockId(x, --y, z) == 0 && remainingLength > 0) {
            this.setBlockAndMetadata(world, x, y, z, Block.vine.blockID, vineFaceMeta);
            --remainingLength;
        }
    }
}
