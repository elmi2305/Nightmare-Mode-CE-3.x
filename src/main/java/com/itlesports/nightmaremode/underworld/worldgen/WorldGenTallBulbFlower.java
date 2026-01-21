package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenTallBulbFlower extends WorldGenerator {

    private int leafMeta = 0;   // Metadata for bulb leaves
    private int metaWood = 0;   // Metadata for stem

    @Override
    public boolean generate(World world, Random rand, int baseX, int baseY, int baseZ) {
        return generateFlowerAt(world, rand, baseX, baseY, baseZ);
    }

    /**
     * Generates a single tall bulb flower at the given base position.
     * Tall stem with slight XZ curve + large bulbous spherical head.
     */
    private boolean generateFlowerAt(World world, Random rand, int baseX, int baseY, int baseZ) {
        // Taller stem: 8-14 blocks
        int stemHeight = rand.nextInt(9) + 9;

        if (baseY < 1 || baseY + stemHeight + 4 > 255) {
            return false;
        }

        // Slight curve: X or Z axis
        int curveDX = 0, curveDZ = 0;
        if (rand.nextBoolean()) {
            curveDX = rand.nextBoolean() ? 1 : -1;
        } else {
            curveDZ = rand.nextBoolean() ? 1 : -1;
        }

        // Minimal shift: 0-2 blocks total, upper stem only
        int totalShift = rand.nextInt(3);
        int startCurve = Math.max(1, stemHeight * 2 / 3);
        int steps = stemHeight - startCurve;
        int spacing = (totalShift == 0 || steps == 0) ? Integer.MAX_VALUE : Math.max(1, (int) Math.ceil((double) steps / totalShift));

        // Precompute & validate stem path
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

        // Bulb head: centered 1 block above tip, radius 2.5 (filled sphere)
        int tipX = pathX[stemHeight - 1];
        int tipY = baseY + stemHeight;
        int tipZ = pathZ[stemHeight - 1];
        float bulbRadius = (rand.nextBoolean() ? 3.5f : 2.5f);
        float bulbRadiusSq = bulbRadius * bulbRadius;
        int bulbCenterY = tipY + 1;

        // Validate bulb space (loop over bounding box)
        int rInt = MathHelper.ceiling_float_int(bulbRadius);
        for (int dy = -rInt; dy <= rInt; ++dy) {
            int by = bulbCenterY + dy;
            if (by < 0 || by >= 256) return false;
            for (int dx = -rInt; dx <= rInt; ++dx) {
                for (int dz = -rInt; dz <= rInt; ++dz) {
                    if (dx * dx + dy * dy + dz * dz <= bulbRadiusSq) {
                        if (!isReplaceable(world, tipX + dx, by, tipZ + dz)) {
                            return false;
                        }
                    }
                }
            }
        }

        // Validate ground
        int groundId = world.getBlockId(baseX, baseY - 1, baseZ);
        if (groundId != Block.grass.blockID && groundId != Block.dirt.blockID && groundId != NMBlocks.flowerGrass.blockID) {
            return false;
        }

        // Place dirt base
        setBlock(world, baseX, baseY - 1, baseZ, Block.dirt.blockID);

        // Place stem
        for (int i = 0; i < stemHeight; ++i) {
            setBlockAndMetadata(world, pathX[i], pathY[i], pathZ[i], NMBlocks.plantMatter.blockID, this.metaWood);
        }

        // Place bulbous sphere head
        for (int dy = -rInt; dy <= rInt; ++dy) {
            int by = bulbCenterY + dy;
            for (int dx = -rInt; dx <= rInt; ++dx) {
                for (int dz = -rInt; dz <= rInt; ++dz) {
                    float distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq <= bulbRadiusSq) {
                        setBlockAndMetadata(world, tipX + dx, by, tipZ + dz, Block.leaves.blockID, this.leafMeta);
                    }
                }
            }
        }

        return true;
    }

    /**
     * Checks if block at (x,y,z) is replaceable.
     */
    private boolean isReplaceable(World world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        return id == 0 || id == Block.leaves.blockID || id == Block.grass.blockID ||
                id == Block.dirt.blockID || id == NMBlocks.plantMatter.blockID || id == NMBlocks.flowerGrass.blockID;
    }
}