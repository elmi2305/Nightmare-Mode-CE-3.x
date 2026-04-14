package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.ArrayList;
import java.util.Random;

public class WorldGenTallBulbFlower extends WorldGenerator {

    private int leafMeta = 0;
    private int metaWood = 0;

    private static final int[][] BULB_SMALL;
    private static final int[][] BULB_LARGE;
    static {
        ArrayList<int[]> s = new ArrayList<>(), l = new ArrayList<>();
        for (int dy = -4; dy <= 4; dy++)
            for (int dx = -4; dx <= 4; dx++)
                for (int dz = -4; dz <= 4; dz++) {
                    int d = dx * dx + dy * dy + dz * dz;
                    if (d <= 6)  s.add(new int[]{dx, dy, dz});
                    if (d <= 12) l.add(new int[]{dx, dy, dz});
                }
        BULB_SMALL = s.toArray(new int[0][]);
        BULB_LARGE = l.toArray(new int[0][]);
    }

    @Override
    public boolean generate(World world, Random rand, int baseX, int baseY, int baseZ) {
        int stemHeight = 12 + rand.nextInt(3);
        if (baseY < 1 || baseY + stemHeight + 5 > 255) return false;

        int groundId = world.getBlockId(baseX, baseY - 1, baseZ);
        if (groundId != Block.grass.blockID && groundId != Block.dirt.blockID
                && (groundId != NMBlocks.underFlowerDirts.blockID && world.getBlockMetadata(baseX,baseY - 1, baseZ) != NMBlocks.META_FLOWER_GRASS)) return false;

        for (int i = 0; i < stemHeight; i++) {
            if (!isReplaceable(world, baseX, baseY + i, baseZ)) return false;
        }

        int tipY = baseY + stemHeight;

        int[][] bulb = rand.nextBoolean() ? BULB_LARGE : BULB_SMALL;
        int bulbCY = tipY + 1;
        for (int[] o : bulb) {
            int by = bulbCY + o[1];
            if (by < 0 || by >= 256) return false;
            if (!isReplaceable(world, baseX + o[0], by, baseZ + o[2])) return false;
        }

        setBlockAndMetadata(world, baseX, baseY - 1, baseZ, NMBlocks.underFlowerDirts.blockID, NMBlocks.META_FLOWER_DIRT);

        for (int i = 0; i < stemHeight; i++) {
            setBlockAndMetadata(world, baseX, baseY + i, baseZ, NMBlocks.plantMatter.blockID, metaWood);
        }

        for (int[] o : bulb) {
            setBlockAndMetadata(world, baseX + o[0], bulbCY + o[1], baseZ + o[2], Block.leaves.blockID, leafMeta);
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