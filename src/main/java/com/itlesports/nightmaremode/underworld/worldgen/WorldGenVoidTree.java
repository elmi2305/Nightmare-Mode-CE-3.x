package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenVoidTree extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, int x, int y, int z) {
        int trunkHeight = 8 + rand.nextInt(4);
        if (y < 1 || y + trunkHeight + 2 >= 256) return false;
        int below = world.getBlockId(x, y - 1, z);
        if (below != Block.grass.blockID && below != Block.dirt.blockID && below != NMBlocks.underGrass.blockID && below != NMBlocks.underStones.blockID) return false;
        this.setBlockAndMetadata(world, x, y - 1, z, NMBlocks.underStones.blockID, NMBlocks.META_VOID_STONE);
        for (int i = 0; i < trunkHeight; i++)
            this.setBlockAndMetadata(world, x, y + i, z, NMBlocks.customLog.blockID, 0);
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{1,-1},{-1,1},{-1,-1}};
        boolean[] used = new boolean[8];
        int branches = 3 + rand.nextInt(2);
        for (int b = 0; b < branches; b++) {
            int di;
            do { di = rand.nextInt(8); } while (used[di]);
            used[di] = true;
            int[] d = dirs[di];
            int by = y + 3 + rand.nextInt(trunkHeight - 4);
            int len = 2 + rand.nextInt(2);
            int bx = x, bz = z, tipY = by;
            for (int s = 1; s <= len; s++) {
                bx = x + d[0] * s;
                bz = z + d[1] * s;
                tipY = by + (s == len ? 1 : 0);
                this.setBlockAndMetadata(world, bx, tipY, bz, NMBlocks.customLog.blockID, 0);
            }
            for (int lx = -1; lx <= 1; lx++)
                for (int ly = 0; ly <= 1; ly++)
                    for (int lz = -1; lz <= 1; lz++)
                        if (rand.nextInt(2) == 0 && world.isAirBlock(bx + lx, tipY + 1 + ly, bz + lz))
                            this.setBlockAndMetadata(world, bx + lx, tipY + 1 + ly, bz + lz, NMBlocks.cryingObsidian.blockID, 0);
        }
        for (int dx = -1; dx <= 1; dx++)
            for (int dz = -1; dz <= 1; dz++)
                if (world.isAirBlock(x + dx, y + trunkHeight, z + dz))
                    this.setBlockAndMetadata(world, x + dx, y + trunkHeight, z + dz, NMBlocks.cryingObsidian.blockID, 0);
        return true;
    }
}
