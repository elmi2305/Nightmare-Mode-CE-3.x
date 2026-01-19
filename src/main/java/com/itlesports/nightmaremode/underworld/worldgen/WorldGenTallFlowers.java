package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.blocks.BlockTallFlower;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenTallFlowers extends WorldGenerator {
    private int plantBlockId;
    private int types;

    public WorldGenTallFlowers(int par1, int types) {
        this.plantBlockId = par1;
        this.types = types;
    }

    @Override
    public boolean generate(World w, Random rand, int x, int y, int z) {
        for (int var6 = 0; var6 < 128; ++var6) {
            int nz;
            int ny;
            int nx = x + rand.nextInt(8) - rand.nextInt(8);
            if (!w.isAirBlock(nx, ny = y + rand.nextInt(3) - rand.nextInt(3), nz = z + rand.nextInt(8) - rand.nextInt(8)) || w.provider.hasNoSky && ny >= 127 || !Block.blocksList[this.plantBlockId].canBlockStayDuringGenerate(w, nx, ny, nz)) continue;
            int type = rand.nextInt(types);
            w.setBlock(nx, ny, nz, this.plantBlockId, type,2);
            if (type == BlockTallFlower.SUNFLOWER || rand.nextInt(6) != 0) {
                w.setBlock(nx, ny + 1, nz, this.plantBlockId, type | 0x8,2);
            }
        }
        return true;
    }
}
