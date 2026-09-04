package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.BlockUnderworldOre;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenerator;
import net.minecraft.src.Chunk;

import java.util.Random;

public class WorldGenUnderworldOre extends WorldGenerator {
    private final int oreId;
    private final int metadata;
    private final int veinSize;
    private final int hostId;

    public WorldGenUnderworldOre(int oreId, int metadata, int veinSize, int hostId) {
        this.oreId = oreId;
        this.metadata = metadata;
        this.veinSize = veinSize;
        this.hostId = hostId;
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        int placed = 0;
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        int cx = x;
        int cy = y;
        int cz = z;
        for (int i = 0; i < veinSize; i++) {
            // decoration must never load a neighbouring chunk. doing so re-enters
            // the biome decorator and produces its "already decorating" crash.
            if ((cx >> 4) != chunkX || (cz >> 4) != chunkZ) {
                cx += random.nextInt(3) - 1;
                cy += random.nextInt(3) - 1;
                cz += random.nextInt(3) - 1;
                continue;
            }

            int localX = cx & 15;
            int localZ = cz & 15;
            if (cy > 1 && cy < 255 && chunk.getBlockID(localX, cy, localZ) == hostId) {
                int placedMetadata = metadata;
                if (hostId == NMBlocks.underrock.blockID) {
                    placedMetadata = BlockUnderworldOre.withHostStrata(metadata, chunk.getBlockMetadata(localX, cy, localZ));
                }
                chunk.setBlockIDWithMetadata(localX, cy, localZ, oreId, placedMetadata);
                placed++;
            }
            cx += random.nextInt(3) - 1;
            cy += random.nextInt(3) - 1;
            cz += random.nextInt(3) - 1;
        }
        return placed > 0;
    }
}
