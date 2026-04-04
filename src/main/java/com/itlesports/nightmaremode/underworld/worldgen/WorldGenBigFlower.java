package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenBigFlower extends WorldGenerator {

    private int leafMeta = 0;
    private int metaWood = 0;

    @Override
    public boolean generate(World world, Random rand, int baseX, int baseY, int baseZ) {
        int offsetX = baseX + rand.nextInt(8) - 6;
        int offsetZ = baseZ + rand.nextInt(8) - 6;
        return generateFlowerAt(world, rand, offsetX, baseY, offsetZ);
    }

    private boolean generateFlowerAt(World world, Random rand, int baseX, int baseY, int baseZ) {
        int stemHeight = rand.nextInt(4) + 6;

        if (baseY < 1 || baseY + stemHeight + 3 > 255) {
            return false;
        }

        // return early if no proper ground
        int groundId = world.getBlockId(baseX, baseY - 1, baseZ);
        if (groundId != Block.grass.blockID && groundId != Block.dirt.blockID
                && groundId != NMBlocks.flowerGrass.blockID) {
            return false;
        }

        int curveDX = 0, curveDZ = 0;
        if (rand.nextBoolean()) {
            curveDX = rand.nextBoolean() ? 1 : -1;
        } else {
            curveDZ = rand.nextBoolean() ? 1 : -1;
        }

        int totalShift = rand.nextInt(2) + 1;
        int startCurve = Math.max(1, stemHeight * 2 / 3);
        int steps = stemHeight - startCurve;
        int spacing = steps == 0 ? Integer.MAX_VALUE : Math.max(1, (int) Math.ceil((double) steps / totalShift));

        // validate stem
        int curX = baseX, curZ = baseZ;
        int accumulated = 0, shifted = 0;
        for (int i = 0; i < stemHeight; ++i) {
            if (i >= startCurve && shifted < totalShift && (i - startCurve) % spacing == 0) {
                accumulated++;
                shifted++;
            }
            curX = baseX + accumulated * curveDX;
            curZ = baseZ + accumulated * curveDZ;
            int curY = baseY + i;
            if (curY < 0 || curY >= 256 || !isReplaceable(world, curX, curY, curZ)) {
                return false;
            }
        }

        // after the loop, curX/curZ hold the tip's XZ position.
        int tipX = curX;
        int tipY = baseY + stemHeight;
        int tipZ = curZ;

        // validate tulip
        int topRadius = 3;
        int headHeight = 3;
        for (int layer = 0; layer < headHeight; ++layer) {
            int layerY = tipY + layer;
            if (layerY < 0 || layerY >= 256) return false;
            int layerRadius = Math.max(1, topRadius - (headHeight - 1 - layer));
            for (int dx = -layerRadius; dx <= layerRadius; ++dx) {
                for (int dz = -layerRadius; dz <= layerRadius; ++dz) {
                    if (dx * dx + dz * dz <= layerRadius * layerRadius
                            && !isReplaceable(world, tipX + dx, layerY, tipZ + dz)) {
                        return false;
                    }
                }
            }
        }

        int lipY = tipY - 1;
        if (lipY >= 0 && lipY < 256) {
            int lipRadius = 2;
            for (int dx = -lipRadius; dx <= lipRadius; ++dx) {
                for (int dz = -lipRadius; dz <= lipRadius; ++dz) {
                    if (dx * dx + dz * dz <= lipRadius * lipRadius
                            && !isReplaceable(world, tipX + dx, lipY, tipZ + dz)) {
                        return false;
                    }
                }
            }
        }


        // place the blocks
        setBlock(world, baseX, baseY - 1, baseZ, Block.dirt.blockID);

        // recompute stem path identically
        curX = baseX; curZ = baseZ; accumulated = 0; shifted = 0;
        int leafCap = Math.min(3, stemHeight / 3);
        for (int i = 0; i < stemHeight; ++i) {
            if (i >= startCurve && shifted < totalShift && (i - startCurve) % spacing == 0) {
                accumulated++;
                shifted++;
            }
            curX = baseX + accumulated * curveDX;
            curZ = baseZ + accumulated * curveDZ;
            int curY = baseY + i;

            setBlockAndMetadata(world, curX, curY, curZ, NMBlocks.plantMatter.blockID, this.metaWood);

            if (i > 0 && i <= leafCap) {
                if (curveDX != 0) {
                    placeLeaf(world, curX, curY, curZ + 1);
                    placeLeaf(world, curX, curY, curZ - 1);
                } else {
                    placeLeaf(world, curX + 1, curY, curZ);
                    placeLeaf(world, curX - 1, curY, curZ);
                }
                if (rand.nextInt(3) == 0) {
                    int dx = (curveDX != 0) ? curveDX : (rand.nextBoolean() ? 1 : -1);
                    int dz = (curveDZ != 0) ? curveDZ : (rand.nextBoolean() ? 1 : -1);
                    placeLeaf(world, curX + dx, curY, curZ + dz);
                }
            }

            if (i > 2 && rand.nextInt(4) == 0) {
                generateMiniBranch(world, rand, curX, curY, curZ);
            }
        }

        placeInvertedPyramid(world, tipX, tipY, tipZ, topRadius, headHeight);

        int topCenterY = tipY + headHeight;
        if (topCenterY >= 0 && topCenterY < 256) {
            placeLeaf(world, tipX, topCenterY, tipZ);
        }

        return true;
    }

    private boolean isReplaceable(World world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        return id == 0 || id == Block.leaves.blockID || id == Block.grass.blockID
                || id == Block.dirt.blockID || id == NMBlocks.plantMatter.blockID
                || id == NMBlocks.flowerGrass.blockID;
    }

    private void placeLeaf(World world, int x, int y, int z) {
        if (y < 0 || y >= 256) return;
        if (isReplaceable(world, x, y, z)) {
            this.setBlockAndMetadata(world, x, y, z, Block.leaves.blockID, this.leafMeta);
        }
    }

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
                    if (distSq <= outerSq && distSq > innerSq) {
                        placeLeaf(world, centerX + dx, layerY, centerZ + dz);
                    }
                }
            }
        }

        int bottomLipY = baseY - 1;
        if (bottomLipY >= 0 && bottomLipY < 256) {
            int lipRadius = 2;
            int outerSq = lipRadius * lipRadius;
            int innerSq = (lipRadius - 1) * (lipRadius - 1);
            for (int dx = -lipRadius; dx <= lipRadius; ++dx) {
                for (int dz = -lipRadius; dz <= lipRadius; ++dz) {
                    int distSq = dx * dx + dz * dz;
                    if (distSq <= outerSq && distSq > innerSq) {
                        placeLeaf(world, centerX + dx, bottomLipY, centerZ + dz);
                    }
                }
            }
        }
    }

    private void generateMiniBranch(World world, Random rand, int x, int y, int z) {
        int dx = rand.nextInt(3) - 1;
        int dz = rand.nextInt(3) - 1;
        if (dx == 0 && dz == 0) return;
        int length = 1 + rand.nextInt(2);
        for (int i = 1; i <= length; i++) {
            placeLeaf(world, x + dx * i, y, z + dz * i);
        }
    }
}