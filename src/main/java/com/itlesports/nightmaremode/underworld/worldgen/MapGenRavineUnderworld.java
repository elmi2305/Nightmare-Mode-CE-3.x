package com.itlesports.nightmaremode.underworld.worldgen;

import net.minecraft.src.World;

import java.util.Random;

/** sparse, full-height ravines sharing the safe Underworld carver. */
public class MapGenRavineUnderworld extends MapGenCavesUnderworld {
    @Override
    protected void recursiveGenerate(World world, int sourceChunkX, int sourceChunkZ,
                                     int targetChunkX, int targetChunkZ, short[] blocks, byte[] metadata) {
        if (blocks == null || this.rand.nextInt(55) != 0) return;
        double x = sourceChunkX * 16 + this.rand.nextInt(16);
        double y = 20 + this.rand.nextInt(150);
        double z = sourceChunkZ * 16 + this.rand.nextInt(16);
        float yaw = this.rand.nextFloat() * (float)Math.PI * 2.0F;
        float pitch = (this.rand.nextFloat() - 0.5F) * 0.08F;
        carveTunnel(new Random(this.rand.nextLong()), targetChunkX, targetChunkZ, blocks, metadata,
                x, y, z, yaw, pitch, 4.5F + this.rand.nextFloat() * 2.5F,
                72 + this.rand.nextInt(56), 1.75D);
    }
}
