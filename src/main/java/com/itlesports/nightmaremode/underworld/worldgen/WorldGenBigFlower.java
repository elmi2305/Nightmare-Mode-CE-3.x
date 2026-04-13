package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.ArrayList;
import java.util.Random;

public class WorldGenBigFlower extends WorldGenerator {

    private int leafMeta = 0;
    private int metaWood = 0;

    private static final int[][] HEAD_OFFSETS;
    private static final int[][] LIP_OFFSETS;

    static {
        ArrayList<int[]> head = new ArrayList<>(), lip = new ArrayList<>();
        int topRadius = 3, headHeight = 3;
        for (int layer = 0; layer < headHeight; layer++) {
            int r     = Math.max(1, topRadius - (headHeight - 1 - layer));
            int outer = r * r, inner = (r - 1) * (r - 1);
            for (int dx = -r; dx <= r; dx++)
                for (int dz = -r; dz <= r; dz++) {
                    int d = dx * dx + dz * dz;
                    if (d <= outer && d > inner)
                        head.add(new int[]{dx, layer, dz});
                }
        }
        int lr = 2, lo = lr * lr, li = (lr - 1) * (lr - 1);
        for (int dx = -lr; dx <= lr; dx++)
            for (int dz = -lr; dz <= lr; dz++) {
                int d = dx * dx + dz * dz;
                if (d <= lo && d > li)
                    lip.add(new int[]{dx, -1, dz});
            }
        HEAD_OFFSETS = head.toArray(new int[0][]);
        LIP_OFFSETS  = lip.toArray(new int[0][]);
    }

    @Override
    public boolean generate(World world, Random rand, int baseX, int baseY, int baseZ) {
        int offsetX = baseX + rand.nextInt(8) - 6;
        int offsetZ = baseZ + rand.nextInt(8) - 6;
        return generateFlowerAt(world, rand, offsetX, baseY, offsetZ);
    }

    private boolean generateFlowerAt(World world, Random rand, int baseX, int baseY, int baseZ) {
        int stemHeight = rand.nextInt(4) + 6;
        if (baseY < 1 || baseY + stemHeight + 4 > 255) return false;

        int groundId = world.getBlockId(baseX, baseY - 1, baseZ);
        if (groundId != Block.grass.blockID && groundId != Block.dirt.blockID
                && groundId != NMBlocks.flowerGrass.blockID) return false;

        int ldx = 0, ldz = 0;
        if (rand.nextBoolean()) ldx = rand.nextBoolean() ? 1 : -1;
        else                    ldz = rand.nextBoolean() ? 1 : -1;

        int totalShift = rand.nextInt(2) + 1;
        int leanAt     = stemHeight * 2 / 3;
        int steps      = stemHeight - leanAt;
        int spacing    = steps == 0 ? Integer.MAX_VALUE : Math.max(1, (int) Math.ceil((double) steps / totalShift));

        int acc = 0, shifted = 0;
        for (int i = 0; i < stemHeight; i++) {
            if (i >= leanAt && shifted < totalShift && (i - leanAt) % spacing == 0) { acc++; shifted++; }
            if (!isReplaceable(world, baseX + acc * ldx, baseY + i, baseZ + acc * ldz)) return false;
        }
        int tipX = baseX + acc * ldx;
        int tipY = baseY + stemHeight;
        int tipZ = baseZ + acc * ldz;

        for (int[] o : HEAD_OFFSETS) {
            int hy = tipY + o[1];
            if (hy < 0 || hy >= 256 || !isReplaceable(world, tipX + o[0], hy, tipZ + o[2])) return false;
        }
        for (int[] o : LIP_OFFSETS) {
            int ly = tipY + o[1];
            if (ly >= 0 && ly < 256 && !isReplaceable(world, tipX + o[0], ly, tipZ + o[2])) return false;
        }

        setBlock(world, baseX, baseY - 1, baseZ, Block.dirt.blockID);

        int leafCap = Math.min(3, stemHeight / 3);
        acc = 0; shifted = 0;
        for (int i = 0; i < stemHeight; i++) {
            if (i >= leanAt && shifted < totalShift && (i - leanAt) % spacing == 0) { acc++; shifted++; }
            int cx = baseX + acc * ldx, cz = baseZ + acc * ldz, cy = baseY + i;
            setBlockAndMetadata(world, cx, cy, cz, NMBlocks.plantMatter.blockID, metaWood);

            if (i > 0 && i <= leafCap) {
                if (ldx != 0) { placeLeaf(world, cx, cy, cz + 1); placeLeaf(world, cx, cy, cz - 1); }
                else          { placeLeaf(world, cx + 1, cy, cz); placeLeaf(world, cx - 1, cy, cz); }
                if (rand.nextInt(3) == 0) {
                    int bx = ldx != 0 ? ldx : (rand.nextBoolean() ? 1 : -1);
                    int bz = ldz != 0 ? ldz : (rand.nextBoolean() ? 1 : -1);
                    placeLeaf(world, cx + bx, cy, cz + bz);
                }
            }
            if (i > 2 && rand.nextInt(4) == 0) generateMiniBranch(world, rand, cx, cy, cz);
        }

        for (int[] o : HEAD_OFFSETS) placeLeaf(world, tipX + o[0], tipY + o[1], tipZ + o[2]);
        for (int[] o : LIP_OFFSETS)  placeLeaf(world, tipX + o[0], tipY + o[1], tipZ + o[2]);
        placeLeaf(world, tipX, tipY + 3, tipZ);

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
        if (isReplaceable(world, x, y, z))
            setBlockAndMetadata(world, x, y, z, Block.leaves.blockID, leafMeta);
    }

    private void generateMiniBranch(World world, Random rand, int x, int y, int z) {
        int dx = rand.nextInt(3) - 1, dz = rand.nextInt(3) - 1;
        if (dx == 0 && dz == 0) return;
        int length = 1 + rand.nextInt(2);
        for (int i = 1; i <= length; i++) placeLeaf(world, x + dx * i, y, z + dz * i);
    }
}