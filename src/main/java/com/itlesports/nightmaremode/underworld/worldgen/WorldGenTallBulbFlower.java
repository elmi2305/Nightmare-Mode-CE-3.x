package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenTallBulbFlower extends WorldGenerator {

    private int leafMeta = 0;
    private int metaWood = 0;

    @Override
    public boolean generate(World world, Random rand, int baseX, int baseY, int baseZ) {
        byte stemHeight = (byte) (rand.nextInt(9) + 9);

        if (baseY < 1 || baseY + stemHeight + 4 > 255) {
            return false;
        }

        // validate ground before continuing
        int groundId = world.getBlockId(baseX, baseY - 1, baseZ);
        if (groundId != Block.grass.blockID && groundId != Block.dirt.blockID
                && groundId != NMBlocks.flowerGrass.blockID) {
            return false;
        }

        byte curveDX = 0, curveDZ = 0;
        if (rand.nextBoolean()) {
            curveDX = (byte) (rand.nextBoolean() ? 1 : -1);
        } else {
            curveDZ = (byte) (rand.nextBoolean() ? 1 : -1);
        }

        byte totalShift = (byte) rand.nextInt(3);
        byte startCurve = (byte) Math.max(1, stemHeight * 2 / 3);
        byte steps = (byte) (stemHeight - startCurve);
        int spacing = (totalShift == 0 || steps == 0) ? Integer.MAX_VALUE
                : Math.max(1, (int) Math.ceil((double) steps / totalShift));

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
        int tipX = curX;
        int tipY = baseY + stemHeight;
        int tipZ = curZ;

        // bulb
        float bulbRadius = rand.nextBoolean() ? 3.5f : 2.5f;
        int bulbRadiusSqInt = (int)(bulbRadius * bulbRadius); // 6 for r=2.5, 12 for r=3.5
        byte rByte = (byte) MathHelper.ceiling_float_int(bulbRadius);   // 3 or 4
        int bulbCenterY = tipY + 1;

        for (int dy = -rByte; dy <= rByte; ++dy) {
            int by = bulbCenterY + dy;
            if (by < 0 || by >= 256) return false;
            for (int dx = -rByte; dx <= rByte; ++dx) {
                for (int dz = -rByte; dz <= rByte; ++dz) {
                    if (dx * dx + dy * dy + dz * dz <= bulbRadiusSqInt
                            && !isReplaceable(world, tipX + dx, by, tipZ + dz)) {
                        return false;
                    }
                }
            }
        }


        // place the dirt below
        setBlock(world, baseX, baseY - 1, baseZ, NMBlocks.flowerDirt.blockID);

        // recompute stem path identically
        curX = baseX; curZ = baseZ; accumulated = 0; shifted = 0;
        for (int i = 0; i < stemHeight; ++i) {
            if (i >= startCurve && shifted < totalShift && (i - startCurve) % spacing == 0) {
                accumulated++;
                shifted++;
            }
            curX = baseX + accumulated * curveDX;
            curZ = baseZ + accumulated * curveDZ;
            setBlockAndMetadata(world, curX, baseY + i, curZ, NMBlocks.plantMatter.blockID, this.metaWood);
        }

        // place bulb sphere with same integer distance threshold.
        for (int dy = -rByte; dy <= rByte; ++dy) {
            int by = bulbCenterY + dy;
            for (int dx = -rByte; dx <= rByte; ++dx) {
                for (int dz = -rByte; dz <= rByte; ++dz) {
                    if (dx * dx + dy * dy + dz * dz <= bulbRadiusSqInt) {
                        setBlockAndMetadata(world, tipX + dx, by, tipZ + dz,
                                Block.leaves.blockID, this.leafMeta);
                    }
                }
            }
        }

        return true;
    }

    private boolean isReplaceable(World world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        return id == 0 || id == Block.leaves.blockID || id == Block.grass.blockID
                || id == Block.dirt.blockID || id == NMBlocks.plantMatter.blockID
                || id == NMBlocks.flowerGrass.blockID;
    }
}