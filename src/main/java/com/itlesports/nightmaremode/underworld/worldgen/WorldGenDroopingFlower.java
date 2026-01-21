package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenDroopingFlower extends WorldGenerator {

    private int leafMeta = 0;   // Metadata for drooping petals
    private int metaWood = 0;   // Metadata for stem

    @Override
    public boolean generate(World world, Random rand, int baseX, int baseY, int baseZ) {
        return generateDroopingFlowerAt(world, rand, baseX, baseY, baseZ);
    }

    /**
     * Generates a single drooping flower.
     * Tall curved stem that bends downward at the top, ending in a large hanging bulb of petals.
     */
    private boolean generateDroopingFlowerAt(World world, Random rand, int baseX, int baseY, int baseZ) {
        // Tall stem: 9-15 blocks
        int stemHeight = rand.nextInt(7) + 9;

        if (baseY < 1 || baseY + stemHeight + 4 > 255) {
            return false;
        }

        // Curve starts earlier and is stronger: always bends downward
        int curveDY = -1; // Always curves down
        int totalVerticalShift = 2 + rand.nextInt(3); // 2-4 blocks downward droop

        // Horizontal curve for natural sway
        int curveDX = 0, curveDZ = 0;
        if (rand.nextBoolean()) {
            curveDX = rand.nextBoolean() ? 1 : -1;
        } else {
            curveDZ = rand.nextBoolean() ? 1 : -1;
        }
        int totalHorizontalShift = rand.nextInt(2) + 1; // 0-2 blocks sideways

        // Curve begins in upper half of stem
        int startCurve = Math.max(1, stemHeight / 2);
        int steps = stemHeight - startCurve;
        int verticalSpacing = steps > 0 ? Math.max(1, steps / totalVerticalShift) : Integer.MAX_VALUE;
        int horizontalSpacing = steps > 0 ? Math.max(1, steps / totalHorizontalShift) : Integer.MAX_VALUE;

        // Precompute path and validate
        int[] pathX = new int[stemHeight];
        int[] pathY = new int[stemHeight];
        int[] pathZ = new int[stemHeight];

        int accumulatedVertical = 0;
        int accumulatedHorizontal = 0;
        int vShiftsDone = 0;
        int hShiftsDone = 0;

        for (int i = 0; i < stemHeight; ++i) {
            if (i >= startCurve) {
                if (vShiftsDone < totalVerticalShift && ((i - startCurve) % verticalSpacing == 0)) {
                    accumulatedVertical++;
                    vShiftsDone++;
                }
                if (hShiftsDone < totalHorizontalShift && ((i - startCurve) % horizontalSpacing == 0)) {
                    accumulatedHorizontal++;
                    hShiftsDone++;
                }
            }

            pathX[i] = baseX + accumulatedHorizontal * curveDX;
            pathY[i] = baseY + i - accumulatedVertical; // Downward droop
            pathZ[i] = baseZ + accumulatedHorizontal * curveDZ;

            if (pathY[i] < 0 || pathY[i] >= 256 || !isReplaceable(world, pathX[i], pathY[i], pathZ[i])) {
                return false;
            }
        }

        // Drooping bulb: hangs below the stem tip
        int tipX = pathX[stemHeight - 1];
        int tipY = pathY[stemHeight - 1];
        int tipZ = pathZ[stemHeight - 1];

        // Bulb is a downward-elongated sphere (ovoid) centered 1 block below tip
        float bulbRadiusX = 2.3F;
        float bulbRadiusY = 3.0F; // Taller vertically for droop
        float bulbRadiusZ = 2.3F;
        int bulbCenterY = tipY - 1;

        float rxSq = bulbRadiusX * bulbRadiusX;
        float rySq = bulbRadiusY * bulbRadiusY;
        float rzSq = bulbRadiusZ * bulbRadiusZ;

        int rInt = MathHelper.ceiling_float_int(Math.max(bulbRadiusX, Math.max(bulbRadiusY, bulbRadiusZ)));

        // Validate bulb space
        for (int dy = -rInt; dy <= rInt; ++dy) {
            int by = bulbCenterY + dy;
            if (by < 0 || by >= 256) return false;
            for (int dx = -rInt; dx <= rInt; ++dx) {
                for (int dz = -rInt; dz <= rInt; ++dz) {
                    float dxn = dx / bulbRadiusX;
                    float dyn = dy / bulbRadiusY;
                    float dzn = dz / bulbRadiusZ;
                    if (dxn * dxn + dyn * dyn + dzn * dzn <= 1.0F) {
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

        // Place drooping ovoid bulb
        for (int dy = -rInt; dy <= rInt; ++dy) {
            int by = bulbCenterY + dy;
            for (int dx = -rInt; dx <= rInt; ++dx) {
                for (int dz = -rInt; dz <= rInt; ++dz) {
                    float dxn = dx / bulbRadiusX;
                    float dyn = dy / bulbRadiusY;
                    float dzn = dz / bulbRadiusZ;
                    if (dxn * dxn + dyn * dyn + dzn * dzn <= 1.0F) {
                        setBlockAndMetadata(world, tipX + dx, by, tipZ + dz, Block.leaves.blockID, this.leafMeta);
                    }
                }
            }
        }

        return true;
    }

    private boolean isReplaceable(World world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        return id == 0 || id == Block.leaves.blockID || id == Block.grass.blockID ||
                id == Block.dirt.blockID || id == NMBlocks.plantMatter.blockID || id == NMBlocks.flowerGrass.blockID;
    }
}