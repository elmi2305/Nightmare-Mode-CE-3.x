package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.ArrayList;
import java.util.Random;

public class WorldGenDroopingFlower extends WorldGenerator {

    private int leafMeta = 0;
    private int metaWood = 0;

    private static final int[][] BULB_OFFSETS;

    static {
        ArrayList<int[]> list = new ArrayList<>();
        for (int dy = -3; dy <= 3; dy++)
            for (int dx = -3; dx <= 3; dx++)
                for (int dz = -3; dz <= 3; dz++) {
                    float dxn = dx / 2.3f, dyn = dy / 3.0f, dzn = dz / 2.3f;
                    if (dxn * dxn + dyn * dyn + dzn * dzn <= 1.0f)
                        list.add(new int[]{dx, dy, dz});
                }
        BULB_OFFSETS = list.toArray(new int[0][]);
    }

    @Override
    public boolean generate(World world, Random rand, int baseX, int baseY, int baseZ) {
        int stemHeight  = rand.nextInt(7) + 9;
        int totalDroop  = rand.nextInt(3) + 2;
        int hShift      = rand.nextInt(2) + 1;
        if (baseY < 1 || baseY + stemHeight + 4 > 255) return false;

        int groundId = world.getBlockId(baseX, baseY - 1, baseZ);
        if (groundId != Block.grass.blockID && groundId != Block.dirt.blockID
                && (groundId != NMBlocks.underFlowerDirts.blockID && world.getBlockMetadata(baseX,baseY - 1, baseZ) != NMBlocks.META_FLOWER_GRASS)) return false;

        int ldx = 0, ldz = 0;
        if (rand.nextBoolean()) ldx = rand.nextBoolean() ? 1 : -1;
        else                    ldz = rand.nextBoolean() ? 1 : -1;

        int curveStart = stemHeight / 2;
        int curveLen   = stemHeight - curveStart;
        int droopEvery = Math.max(1, curveLen / totalDroop);
        int hEvery     = Math.max(1, curveLen / hShift);

        int[] pathX = new int[stemHeight], pathY = new int[stemHeight], pathZ = new int[stemHeight];
        int v = 0, h = 0;
        for (int i = 0; i < stemHeight; i++) {
            if (i >= curveStart) {
                int rel = i - curveStart;
                if (v < totalDroop && rel % droopEvery == 0) v++;
                if (h < hShift    && rel % hEvery     == 0) h++;
            }
            pathX[i] = baseX + h * ldx;
            pathY[i] = baseY + i - v;
            pathZ[i] = baseZ + h * ldz;
        }

        int tipX = pathX[stemHeight - 1], tipY = pathY[stemHeight - 1], tipZ = pathZ[stemHeight - 1];
        int bulbCY = tipY - 1;

        for (int i = 0; i < stemHeight; i++) {
            if (pathY[i] < 0 || pathY[i] >= 256 || !isReplaceable(world, pathX[i], pathY[i], pathZ[i]))
                return false;
        }

        for (int[] o : BULB_OFFSETS) {
            int by = bulbCY + o[1];
            if (by < 0 || by >= 256) return false;
            if (!isReplaceable(world, tipX + o[0], by, tipZ + o[2])) return false;
        }

        setBlock(world, baseX, baseY - 1, baseZ, Block.dirt.blockID);

        for (int i = 0; i < stemHeight; i++) {
            setBlockAndMetadata(world, pathX[i], pathY[i], pathZ[i], NMBlocks.plantMatter.blockID, metaWood);
        }

        for (int[] o : BULB_OFFSETS) {
            setBlockAndMetadata(world, tipX + o[0], bulbCY + o[1], tipZ + o[2], Block.leaves.blockID, leafMeta);
        }

        return true;
    }

    private boolean isReplaceable(World world, int x, int y, int z) {
        int id = world.getBlockId(x, y, z);
        return id == 0 || id == Block.leaves.blockID || id == Block.grass.blockID
                || id == Block.dirt.blockID || id == NMBlocks.plantMatter.blockID
                || (id == NMBlocks.underFlowerDirts.blockID && world.getBlockMetadata(x,y,z) == NMBlocks.META_FLOWER_GRASS);
    }
}