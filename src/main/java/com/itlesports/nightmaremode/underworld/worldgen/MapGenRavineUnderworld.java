package com.itlesports.nightmaremode.underworld.worldgen;

import net.minecraft.src.World;
import net.minecraft.src.MathHelper;

import java.util.Random;

/** sparse, full-height ravines sharing the safe Underworld carver. */
public class MapGenRavineUnderworld extends MapGenCavesUnderworld {
    @Override
    protected void recursiveGenerate(World world, int sourceChunkX, int sourceChunkZ,
                                     int targetChunkX, int targetChunkZ, short[] blocks, byte[] metadata) {
        if (blocks == null || this.rand.nextInt(55) != 0) return;
        double x = sourceChunkX * 16 + this.rand.nextInt(16);
        double z = sourceChunkZ * 16 + this.rand.nextInt(16);
        int surfaceY = getSurfaceY(world, MathHelper.floor_double(x), MathHelper.floor_double(z));
        int maxY = Math.min(world.getHeight() - 20, surfaceY - 14);
        if (maxY < 20) return;
        double y = this.rand.nextInt(3) == 0 ? maxY - this.rand.nextInt(7)
                : 20 + this.rand.nextInt(maxY - 19);
        float yaw = this.rand.nextFloat() * (float)Math.PI * 2.0F;
        float pitch = (this.rand.nextFloat() - 0.5F) * 0.08F;
        carveTunnel(new Random(this.rand.nextLong()), targetChunkX, targetChunkZ, blocks, metadata,
                x, y, z, yaw, pitch, 4.5F + this.rand.nextFloat() * 2.5F,
                72 + this.rand.nextInt(56), 3.2D);
    }

    @Override
    protected double getCarvingCenterY(double x, double y, double z, double verticalRadius) {
        // keep the body below local terrain when a ravine crosses into a lower biome.
        int surface = getSurfaceY(this.worldObj, MathHelper.floor_double(x), MathHelper.floor_double(z));
        return Math.min(y, surface - verticalRadius * 0.65);
    }
}
