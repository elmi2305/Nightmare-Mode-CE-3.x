package com.itlesports.nightmaremode.underworld.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.Block;
import net.minecraft.src.MapGenBase;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

import java.util.Random;

/** full-height cave carver for the Underworld's 256-block chunk arrays. */
public class MapGenCavesUnderworld extends MapGenBase {
    @Override
    protected void recursiveGenerate(World world, int sourceChunkX, int sourceChunkZ,
                                     int targetChunkX, int targetChunkZ, short[] blocks, byte[] metadata) {
        if (blocks == null || this.rand.nextInt(15) != 0) return;

        int tunnels = 1 + this.rand.nextInt(3);
        for (int tunnel = 0; tunnel < tunnels; tunnel++) {
            double x = sourceChunkX * 16 + this.rand.nextInt(16);
            double y = 12 + this.rand.nextInt(205);
            double z = sourceChunkZ * 16 + this.rand.nextInt(16);
            float yaw = this.rand.nextFloat() * (float)Math.PI * 2.0F;
            float pitch = (this.rand.nextFloat() - 0.5F) * 0.24F;
            float radius = 1.25F + this.rand.nextFloat() * 1.8F;
            carveTunnel(new Random(this.rand.nextLong()), targetChunkX, targetChunkZ, blocks, metadata,
                    x, y, z, yaw, pitch, radius, 48 + this.rand.nextInt(56), 0.72D);
        }
    }

    protected void carveTunnel(Random random, int targetChunkX, int targetChunkZ,
                               short[] blocks, byte[] metadata, double x, double y, double z,
                               float yaw, float pitch, float baseRadius, int length, double verticalScale) {
        float yawVelocity = 0.0F;
        float pitchVelocity = 0.0F;

        for (int step = 0; step < length; step++) {
            double taper = Math.sin((double)step * Math.PI / (double)length);
            double horizontalRadius = 1.2D + taper * baseRadius;
            double verticalRadius = horizontalRadius * verticalScale;
            x += MathHelper.cos(yaw) * MathHelper.cos(pitch);
            y += MathHelper.sin(pitch);
            z += MathHelper.sin(yaw) * MathHelper.cos(pitch);

            pitch *= 0.72F;
            pitch += pitchVelocity * 0.08F;
            yaw += yawVelocity * 0.08F;
            pitchVelocity = pitchVelocity * 0.75F + (random.nextFloat() - random.nextFloat()) * 0.05F;
            yawVelocity = yawVelocity * 0.9F + (random.nextFloat() - random.nextFloat()) * 0.1F;

            if (random.nextInt(4) != 0) {
                carveEllipsoid(targetChunkX, targetChunkZ, blocks, metadata, x, y, z,
                        horizontalRadius, verticalRadius);
            }
        }
    }

    protected void carveEllipsoid(int targetChunkX, int targetChunkZ, short[] blocks, byte[] metadata,
                                  double centerX, double centerY, double centerZ,
                                  double horizontalRadius, double verticalRadius) {
        int chunkMinX = targetChunkX * 16;
        int chunkMinZ = targetChunkZ * 16;
        int minX = Math.max(0, MathHelper.floor_double(centerX - horizontalRadius) - chunkMinX);
        int maxX = Math.min(15, MathHelper.floor_double(centerX + horizontalRadius) - chunkMinX);
        int minY = Math.max(2, MathHelper.floor_double(centerY - verticalRadius));
        int maxY = Math.min(253, MathHelper.floor_double(centerY + verticalRadius));
        int minZ = Math.max(0, MathHelper.floor_double(centerZ - horizontalRadius) - chunkMinZ);
        int maxZ = Math.min(15, MathHelper.floor_double(centerZ + horizontalRadius) - chunkMinZ);

        if (minX > maxX || minY > maxY || minZ > maxZ) return;
        for (int localX = minX; localX <= maxX; localX++) {
            double nx = (chunkMinX + localX + 0.5D - centerX) / horizontalRadius;
            for (int localZ = minZ; localZ <= maxZ; localZ++) {
                double nz = (chunkMinZ + localZ + 0.5D - centerZ) / horizontalRadius;
                if (nx * nx + nz * nz >= 1.0D) continue;
                for (int blockY = maxY; blockY >= minY; blockY--) {
                    double ny = (blockY + 0.5D - centerY) / verticalRadius;
                    if (nx * nx + ny * ny + nz * nz >= 1.0D) continue;
                    int index = (localX * 16 + localZ) * 256 + blockY;
                    if (!isCarvable(blocks[index])) continue;
                    blocks[index] = (short)(blockY < 8 ? Block.lavaMoving.blockID : 0);
                    metadata[index] = 0;
                }
            }
        }
    }

    private boolean isCarvable(int blockId) {
        return blockId == NMBlocks.underCobble.blockID
                || blockId == NMBlocks.underrock.blockID
                || blockId == NMBlocks.understoneSmooth.blockID
                || blockId == NMBlocks.underFlowerDirts.blockID
                || blockId == NMBlocks.underGrass.blockID
                || blockId == NMBlocks.underStones.blockID
                || blockId == NMBlocks.hellStones.blockID
                || blockId == Block.stone.blockID
                || blockId == Block.dirt.blockID
                || blockId == Block.grass.blockID;
    }
}
