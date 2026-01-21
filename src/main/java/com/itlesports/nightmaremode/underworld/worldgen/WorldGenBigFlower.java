package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenBigFlower extends WorldGenerator {

    private int leafMeta = 0;   // Metadata for leaves (petals)
    private int metaWood = 0;   // Metadata for stem (plant matter)

    @Override
    public boolean generate(World world, Random rand, int baseX, int baseY, int baseZ) {
        int flowerCount = 1; // Can increase for clusters

        for (int i = 0; i < flowerCount; i++) {
            int offsetX = baseX + rand.nextInt(8) - 6;
            int offsetZ = baseZ + rand.nextInt(8) - 6;

            if (!generateFlowerAt(world, rand, offsetX, baseY, offsetZ)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Generates a single big flower at the given base position.
     * Returns true if successful, false if space is insufficient.
     */
    private boolean generateFlowerAt(World world, Random rand, int baseX, int baseY, int baseZ) {
        // Stem height: 6 to 9 blocks
        int stemHeight = rand.nextInt(4) + 6;

        if (baseY < 1 || baseY + stemHeight + 3 > 255) {
            return false;
        }

        // Choose curve direction: either X or Z axis (no diagonal)
        int curveDX = 0, curveDZ = 0;
        if (rand.nextBoolean()) {
            curveDX = rand.nextBoolean() ? 1 : -1;
        } else {
            curveDZ = rand.nextBoolean() ? 1 : -1;
        }

        // Total horizontal shift: 0 to 2 blocks
        int totalShift = rand.nextInt(2) + 1;

        // Start curving in upper 2/3 of stem
        int startCurve = Math.max(1, stemHeight * 2 / 3);
        int steps = stemHeight - startCurve;
        int spacing = steps == 0 ? Integer.MAX_VALUE : Math.max(1, (int) Math.ceil((double) steps / totalShift));

        // Precompute path and check space
        int[] pathX = new int[stemHeight];
        int[] pathY = new int[stemHeight];
        int[] pathZ = new int[stemHeight];

        int accumulatedShift = 0;
        int shiftsDone = 0;
        for (int i = 0; i < stemHeight; ++i) {
            if (i >= startCurve && shiftsDone < totalShift && ((i - startCurve) % spacing == 0)) {
                accumulatedShift++;
                shiftsDone++;
            }
            pathX[i] = baseX + accumulatedShift * curveDX;
            pathY[i] = baseY + i;
            pathZ[i] = baseZ + accumulatedShift * curveDZ;

            if (pathY[i] < 0 || pathY[i] >= 256 || !isReplaceable(world, pathX[i], pathY[i], pathZ[i])) {
                return false;
            }
        }

        // Head parameters
        int tipX = pathX[stemHeight - 1];
        int tipY = baseY + stemHeight;
        int tipZ = pathZ[stemHeight - 1];
        int topRadius = 3;
        int headHeight = 3;

        // Check head space (inverted pyramid + lip)
        for (int layer = 0; layer < headHeight; ++layer) {
            int layerY = tipY + layer;
            if (layerY < 0 || layerY >= 256) {
                return false;
            }
            int layerRadius = Math.max(1, topRadius - (headHeight - 1 - layer));
            for (int dx = -layerRadius; dx <= layerRadius; ++dx) {
                for (int dz = -layerRadius; dz <= layerRadius; ++dz) {
                    int px = tipX + dx;
                    int pz = tipZ + dz;
                    if (dx * dx + dz * dz <= layerRadius * layerRadius && !isReplaceable(world, px, layerY, pz)) {
                        return false;
                    }
                }
            }
        }

        // Check lip space
        int lipY = tipY - 1;
        if (lipY >= 0 && lipY < 256) {
            int lipRadius = 2;
            for (int dx = -lipRadius; dx <= lipRadius; ++dx) {
                for (int dz = -lipRadius; dz <= lipRadius; ++dz) {
                    int px = tipX + dx;
                    int pz = tipZ + dz;
                    if (dx * dx + dz * dz <= lipRadius * lipRadius && !isReplaceable(world, px, lipY, pz)) {
                        return false;
                    }
                }
            }
        }

        // Check ground
        int groundId = world.getBlockId(baseX, baseY - 1, baseZ);
        if (groundId != Block.grass.blockID && groundId != Block.dirt.blockID && groundId != NMBlocks.flowerGrass.blockID) {
            return false;
        }

        // Place dirt base
        setBlock(world, baseX, baseY - 1, baseZ, Block.dirt.blockID);

        // Place stem and mini branches/leaves
        for (int i = 0; i < stemHeight; ++i) {
            int sx = pathX[i];
            int sy = pathY[i];
            int sz = pathZ[i];

            setBlockAndMetadata(world, sx, sy, sz, NMBlocks.plantMatter.blockID, this.metaWood);

            // Small leaves low on stem
            if (i > 0 && i <= Math.min(3, stemHeight / 3)) {
                if (curveDX != 0) {
                    placeLeaf(world, sx, sy, sz + 1);
                    placeLeaf(world, sx, sy, sz - 1);
                } else {
                    placeLeaf(world, sx + 1, sy, sz);
                    placeLeaf(world, sx - 1, sy, sz);
                }
                if (rand.nextInt(3) == 0) {
                    int dx = (curveDX != 0) ? curveDX : (rand.nextBoolean() ? 1 : -1);
                    int dz = (curveDZ != 0) ? curveDZ : (rand.nextBoolean() ? 1 : -1);
                    placeLeaf(world, sx + dx, sy, sz + dz);
                }
            }

            // Mini branches occasionally
            if (i > 2 && rand.nextInt(4) == 0) {
                generateMiniBranch(world, rand, sx, sy, sz);
            }
        }

        // Place inverted pyramid (cup) head
        placeInvertedPyramid(world, tipX, tipY, tipZ, topRadius, headHeight);

        // Small filled top center for bulk
        int topCenterY = tipY + headHeight;
        if (topCenterY >= 0 && topCenterY < 256) {
            placeLeaf(world, tipX, topCenterY, tipZ);
        }

        return true;
    }

    /**
     * Checks if the block at (x,y,z) can be replaced (air, leaves, grass, etc.).
     */
    private boolean isReplaceable(World world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        return id == 0 || id == Block.leaves.blockID || id == Block.grass.blockID || id == Block.dirt.blockID ||
                id == NMBlocks.plantMatter.blockID || id == NMBlocks.flowerGrass.blockID;
    }

    /**
     * Places a leaf block if the position is air or replaceable.
     */
    private void placeLeaf(World world, int x, int y, int z) {
        if (y < 0 || y >= 256) return;
        if (isReplaceable(world, x, y, z)) {
            this.setBlockAndMetadata(world, x, y, z, Block.leaves.blockID, this.leafMeta);
        }
    }

    /**
     * Places an inverted pyramid "cup" head using leaf blocks.
     * Hollow rings with increasing radius upward, plus base lip.
     */
    private void placeInvertedPyramid(World world, int centerX, int baseY, int centerZ, int topRadius, int height) {
        for (int layer = 0; layer < height; ++layer) {
            int layerY = baseY + layer;
            if (layerY < 0 || layerY >= 256) continue;

            int radius = Math.max(1, topRadius - (height - 1 - layer));
            int outerSq = radius * radius;
            int innerSq = (radius - 1) * (radius - 1);

            for (int dx = -radius; dx <= radius; ++dx) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    int distSq = dx * dx + dz * dz;
                    if (distSq <= outerSq && distSq > innerSq) { // Perimeter only for hollow cup
                        int px = centerX + dx;
                        int pz = centerZ + dz;
                        placeLeaf(world, px, layerY, pz);
                    }
                }
            }
        }

        // Bottom lip (outward extension at baseY - 1)
        int bottomLipY = baseY - 1;
        if (bottomLipY >= 0 && bottomLipY < 256) {
            int lipRadius = 2;
            int outerSq = lipRadius * lipRadius;
            int innerSq = (lipRadius - 1) * (lipRadius - 1);
            for (int dx = -lipRadius; dx <= lipRadius; ++dx) {
                for (int dz = -lipRadius; dz <= lipRadius; ++dz) {
                    int distSq = dx * dx + dz * dz;
                    if (distSq <= outerSq && distSq > innerSq) {
                        int px = centerX + dx;
                        int pz = centerZ + dz;
                        placeLeaf(world, px, bottomLipY, pz);
                    }
                }
            }
        }
    }

    /**
     * Generates a small branch from the stem.
     */
    private void generateMiniBranch(World world, Random rand, int x, int y, int z) {
        int dx = rand.nextInt(3) - 1;
        int dz = rand.nextInt(3) - 1;
        if (dx == 0 && dz == 0) return;

        int length = 1 + rand.nextInt(2); // 1-2 blocks

        for (int i = 1; i <= length; i++) {
            placeLeaf(world, x + dx * i, y, z + dz * i);
        }
    }
}