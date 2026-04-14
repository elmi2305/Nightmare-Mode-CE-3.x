package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.blocks.BlockTallFlower;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;

import java.util.Random;

public class WorldGenTallFlowers extends WorldGenerator {
    private final int plantBlockId;
    private final int types;
    private final Block plantBlock;
    private final boolean exactType; // if true, we only want the metadata provided by this.types. otherwise, roll a random number between 0 and this.types

    public WorldGenTallFlowers(int id, int types, boolean exactType) {
        this.plantBlockId = id;
        this.types = types;
        this.exactType = exactType;
        this.plantBlock = Block.blocksList[id];
    }

    @Override
    public boolean generate(World w, Random rand, int x, int y, int z) {
        for (int i = 0; i < 64; ++i) {
            int nx = x + rand.nextInt(8) - rand.nextInt(8);
            int ny = y + rand.nextInt(3) - rand.nextInt(3);
            int nz = z + rand.nextInt(8) - rand.nextInt(8);

            if (!w.isAirBlock(nx, ny, nz)) continue;
            // roof guard relevant for nether-style dim. never passes in the underworld. still here for future reference
            if (w.provider.hasNoSky && ny >= 127) continue;
            // ground adjacency and block-specific placement rules (checks the block below)
            if (!this.plantBlock.canBlockStayDuringGenerate(w, nx, ny, nz)) continue;

            int type = this.exactType ? this.types : rand.nextInt(this.types);
            w.setBlock(nx, ny, nz, this.plantBlockId, type, 2);
            // sunflower often needs top half
            if (type == BlockTallFlower.SUNFLOWER || rand.nextInt(6) != 0) {
                w.setBlock(nx, ny + 1, nz, this.plantBlockId, type | 0x8, 2);
            }
        }
        return true;
    }
}