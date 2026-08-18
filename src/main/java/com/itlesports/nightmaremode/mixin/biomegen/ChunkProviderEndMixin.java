package com.itlesports.nightmaremode.mixin.biomegen;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.worldgen.WorldGenEnderNest;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ChunkProviderEnd.class)
public class ChunkProviderEndMixin {
    @Shadow private World endWorld;
    @Shadow double[] noiseData1;
    @Shadow double[] noiseData2;
    @Shadow double[] noiseData3;

    @Unique private NoiseGeneratorOctaves outerIslandShapeNoise;
    @Unique private NoiseGeneratorOctaves outerIslandDetailNoise;
    @Unique private NoiseGeneratorOctaves outerIslandHeightNoise;
    @Unique private double[] outerIslandShapeData;
    @Unique private double[] outerIslandDetailData;
    @Unique private double[] outerIslandHeightData;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initializeOuterIslandNoise(World world, long seed, CallbackInfo ci) {
        Random noiseRandom = new Random(seed ^ 0x45434C495053454CL);
        this.outerIslandShapeNoise = new NoiseGeneratorOctaves(noiseRandom, 4);
        this.outerIslandDetailNoise = new NoiseGeneratorOctaves(noiseRandom, 3);
        this.outerIslandHeightNoise = new NoiseGeneratorOctaves(noiseRandom, 3);
    }

    /**
     * Adds outer islands to the End's coarse density field. ChunkProviderEnd performs
     * the actual trilinear interpolation and block placement, just as it does for the
     * central island, so every edge sample is shared by both neighbouring chunks.
     */
    @Inject(method = "initializeNoiseField", at = @At("RETURN"))
    private void addOuterIslandDensity(double[] densities, int startX, int startY, int startZ,
                                       int sizeX, int sizeY, int sizeZ,
                                       CallbackInfoReturnable<double[]> cir) {
        double[] result = cir.getReturnValue();
        this.outerIslandShapeData = this.outerIslandShapeNoise.generateNoiseOctaves(
                this.outerIslandShapeData, startX, startZ, sizeX, sizeZ, 0.045D, 0.045D, 0.5D);
        this.outerIslandDetailData = this.outerIslandDetailNoise.generateNoiseOctaves(
                this.outerIslandDetailData, startX, startZ, sizeX, sizeZ, 0.13D, 0.13D, 0.5D);
        this.outerIslandHeightData = this.outerIslandHeightNoise.generateNoiseOctaves(
                this.outerIslandHeightData, startX, startZ, sizeX, sizeZ, 0.012D, 0.012D, 0.5D);

        int densityIndex = 0;
        int columnIndex = 0;
        for (int localNoiseX = 0; localNoiseX < sizeX; ++localNoiseX) {
            double worldX = (startX + localNoiseX) * 8.0D;
            for (int localNoiseZ = 0; localNoiseZ < sizeZ; ++localNoiseZ) {
                double worldZ = (startZ + localNoiseZ) * 8.0D;
                double radius = Math.sqrt(worldX * worldX + worldZ * worldZ);

                double broadNoise = this.outerIslandShapeData[columnIndex] / 15.0D;
                double detailNoise = this.outerIslandDetailData[columnIndex] / 7.0D;
                double heightNoise = this.outerIslandHeightData[columnIndex] / 7.0D;
                double islandShape = broadNoise * 0.75D + detailNoise * 0.25D;

                // Fade the first outer land in instead of cutting a circular wall at 500 blocks.
                double distanceFade = clamp((radius - 500.0D) / 160.0D, 0.0D, 1.0D);
                double shapeThreshold = 0.02D + (1.0D - distanceFade) * 0.32D;
                double islandStrength = (islandShape - shapeThreshold) * 105.0D * distanceFade;
                double centerY = 15.5D + heightNoise * 5.5D + broadNoise * 2.0D;

                for (int localNoiseY = 0; localNoiseY < sizeY; ++localNoiseY) {
                    if (radius >= 500.0D && islandStrength > 0.0D) {
                        double lowNoise = this.noiseData2[densityIndex] / 512.0D;
                        double highNoise = this.noiseData3[densityIndex] / 512.0D;
                        double blend = (this.noiseData1[densityIndex] / 10.0D + 1.0D) / 2.0D;
                        double terrainNoise = blend < 0.0D ? lowNoise
                                : blend > 1.0D ? highNoise
                                : lowNoise + (highNoise - lowNoise) * blend;
                        terrainNoise = clamp(terrainNoise - 8.0D, -18.0D, 18.0D);

                        double verticalFalloff = Math.abs(localNoiseY - centerY) * 5.0D;
                        double outerDensity = islandStrength - verticalFalloff + terrainNoise * 0.65D;
                        if (outerDensity > result[densityIndex]) result[densityIndex] = outerDensity;
                    }
                    ++densityIndex;
                }
                ++columnIndex;
            }
        }
    }

    @Inject(method = "generateTerrain", at = @At("TAIL"))
    private void finishOuterIslandTerrain(int chunkX, int chunkZ, short[] blocks, byte[] metadata, BiomeGenBase[] biomes, CallbackInfo ci) {
        long seed = this.endWorld.getSeed();
        for (int localX = 0; localX < 16; ++localX) {
            int worldX = chunkX * 16 + localX;
            for (int localZ = 0; localZ < 16; ++localZ) {
                int worldZ = chunkZ * 16 + localZ;
                long radiusSq = (long)worldX * worldX + (long)worldZ * worldZ;
                if (radiusSq < 500L * 500L) continue;
                boolean deep = radiusSq >= 1000L * 1000L;
                for (int y = 0; y < 128; ++y) {
                    int index = (localX * 16 + localZ) * 128 + y;
                    if (blocks[index] != Block.whiteStone.blockID) continue;
                    if (deep) metadata[index] = 1;

                    long oreHash = mix(seed ^ ((long)worldX * 341873128712L) ^ ((long)y * 42317861L) ^ ((long)worldZ * 132897987541L));
                    int chance = deep ? 95 : 220;
                    if (Math.floorMod(oreHash, chance) == 0) {
                        blocks[index] = (short)NMBlocks.mercuryOre.blockID;
                    }
                }
            }
        }
    }

    @Inject(method = "populate", at = @At("TAIL"))
    private void populateOuterIslands(IChunkProvider provider, int chunkX, int chunkZ, CallbackInfo ci) {
        int centerX = chunkX * 16 + 8, centerZ = chunkZ * 16 + 8;
        long radiusSq = (long)centerX * centerX + (long)centerZ * centerZ;
        if (radiusSq < 500L * 500L) return;
        Random random = new Random(mix(this.endWorld.getSeed() ^ ((long)chunkX * 341873128712L) ^ ((long)chunkZ * 132897987541L)));

        if (radiusSq >= 1000L * 1000L && random.nextInt(7) == 0) {
            int x = chunkX * 16 + 3 + random.nextInt(10), z = chunkZ * 16 + 3 + random.nextInt(10);
            int top = this.findSurface(x, z);
            if (top > 10) this.endWorld.setBlock(x, Math.max(8, top - 3), z, NMBlocks.mercuryOreNode.blockID, 0, 2);
        }
        if (random.nextInt(38) == 0) {
            int top = this.findSurface(centerX, centerZ);
            if (top > 10) new WorldGenEnderNest().generate(this.endWorld, random, centerX, top + 1, centerZ);
        }
        int cropChance = radiusSq >= 1000L * 1000L ? 3 : 7;
        if (random.nextInt(cropChance) == 0) {
            for (int attempt = 0; attempt < 6; ++attempt) {
                int x = chunkX * 16 + 2 + random.nextInt(12), z = chunkZ * 16 + 2 + random.nextInt(12);
                int top = this.findSurface(x, z);
                if (top <= 10 || this.endWorld.getBlockId(x, top, z) != Block.whiteStone.blockID) continue;
                this.endWorld.setBlockAndMetadataWithNotify(x, top, z, NMBlocks.endFarmland.blockID, 7);
                this.endWorld.setBlockAndMetadataWithNotify(x, top + 1, z, NMBlocks.paleRootCrop.blockID, 7);
            }
        }
    }

    private int findSurface(int x, int z) {
        for (int y = 120; y >= 8; --y) if (this.endWorld.getBlockId(x, y, z) != 0) return y;
        return -1;
    }

    @Unique
    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static long mix(long value) {
        value ^= value >>> 33; value *= 0xff51afd7ed558ccdL;
        value ^= value >>> 33; value *= 0xc4ceb9fe1a85ec53L;
        return value ^ value >>> 33;
    }

}
