package com.itlesports.nightmaremode.underworld;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenBlightlands;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenFlowerFields;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenHighlands;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.MapGenScatteredFeatureUnderworld;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class ChunkProviderGenerateUnderworld implements IChunkProvider {
    private Random rand;
    private NoiseGeneratorOctaves noiseGen1;
    private NoiseGeneratorOctaves noiseGen2;
    private NoiseGeneratorOctaves noiseGen3;
    private NoiseGeneratorOctaves noiseGen4;
    public NoiseGeneratorOctaves noiseGen5;
    public NoiseGeneratorOctaves noiseGen6;
    public NoiseGeneratorOctaves mobSpawnerNoise;
    private World worldObj;
    private double[] noiseArray;
    private double[] stoneNoise = new double[256];
    private MapGenBase caveGenerator = new MapGenCaves();
    private MapGenStronghold strongholdGenerator = new MapGenStronghold();
    private MapGenVillage villageGenerator = new MapGenVillage();
    private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private MapGenScatteredFeatureUnderworld scatteredFeatureGenerator = new MapGenScatteredFeatureUnderworld();
    private MapGenBase ravineGenerator = new MapGenRavine();
    private BiomeGenBase[] biomesForGeneration;
    double[] noise3;
    double[] noise1;
    double[] noise2;
    double[] noise5;
    double[] noise6;
    float[] parabolicField;
    int[][] field_73219_j = new int[32][32];
    private Random structureRand;
    private boolean mineshaftsEnabled = false;
    private boolean villagesEnabled = false;
    private boolean strongholdsEnabled = false;
    private boolean mapFeaturesEnabled = true;
    private boolean dungeonsEnabled = false;

    public ChunkProviderGenerateUnderworld(World par1World, long par2) {
        this.worldObj = par1World;
        this.rand = new Random(par2);
        this.structureRand = new Random(par2);
        this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseGen4 = new NoiseGeneratorOctaves(this.rand, 4);
        this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
    }

    private int seaLevel = 10; // unused

    public void generateTerrain(int chunkX, int chunkZ, short[] blockIDs, byte[] metadata) {
        byte noiseScaleXZ = 4;
        byte noiseScaleY = 32;
        byte noiseSizeX = (byte)(noiseScaleXZ + 1); // 5
        byte noiseSizeY = 33;
        byte noiseSizeZ = (byte)(noiseScaleXZ + 1); // 5

        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, noiseSizeX + 5, noiseSizeZ + 5);

        this.noiseArray = this.initializeNoiseFieldInLayers(this.noiseArray, chunkX * noiseScaleXZ, 0, chunkZ * noiseScaleXZ, noiseSizeX, noiseSizeY, noiseSizeZ);

        int blockStride = 256; // CHANGED: was 128
        double horizontalInterpStep = 0.25;
        double depthInterpStep = 0.25;

        for (byte noiseX = 0; noiseX < noiseScaleXZ; ++noiseX) {
            for (byte noiseZ = 0; noiseZ < noiseScaleXZ; ++noiseZ) {
                short xNoiseStride = (short)(noiseSizeZ * noiseSizeY);
                short zNoiseStride = noiseSizeY;
                short noiseBase = (short)((noiseX * noiseSizeZ + noiseZ) * noiseSizeY);
                int xBaseShift = noiseX * 4 << 12;
                int zShift = noiseZ * 4 << 8;
                for (byte noiseY = 0; noiseY < noiseScaleY; ++noiseY) {
                    double verticalInterpStep = 0.125;

                    double noise000 = this.noiseArray[noiseBase + noiseY];
                    double noise001 = this.noiseArray[noiseBase + zNoiseStride + noiseY];
                    double noise100 = this.noiseArray[noiseBase + xNoiseStride + noiseY];
                    double noise101 = this.noiseArray[noiseBase + xNoiseStride + zNoiseStride + noiseY];

                    double noise000Step = (this.noiseArray[noiseBase + noiseY + 1] - noise000) * verticalInterpStep;
                    double noise001Step = (this.noiseArray[noiseBase + zNoiseStride + noiseY + 1] - noise001) * verticalInterpStep;
                    double noise100Step = (this.noiseArray[noiseBase + xNoiseStride + noiseY + 1] - noise100) * verticalInterpStep;
                    double noise101Step = (this.noiseArray[noiseBase + xNoiseStride + zNoiseStride + noiseY + 1] - noise101) * verticalInterpStep;

                    for (byte subY = 0; subY < 8; ++subY) {  // unchanged (still 8 sub-steps per noise slice)

                        double noiseX0 = noise000;
                        double noiseX1 = noise001;

                        double noiseXStep0 = (noise100 - noise000) * horizontalInterpStep;
                        double noiseXStep1 = (noise101 - noise001) * horizontalInterpStep;

                        short yPart = (short)(noiseY * 8 + subY);
                        int baseBlockIndex = xBaseShift + zShift + yPart;
                        for (byte subX = 0; subX < 4; ++subX) {
                            // CHANGED indexing + stride for 256 height
//                            int xPart = subX + noiseX * 4;                    // world x 0-15
//                            int zPartBase = noiseZ * 4;                       // base z before subZ
//                            int yPart = noiseY * 8 + subY;                    // world y 0-255
//                            int blockIndex = (xPart << 12) | (zPartBase << 8) | yPart;
                            int blockIndex = baseBlockIndex + (subX << 12); // Fixed bit shifts for 256 height


                            blockIndex -= blockStride;

                            double noiseZStep = (noiseX1 - noiseX0) * depthInterpStep;
                            double noiseZValue = noiseX0 - noiseZStep;

                            for (byte subZ = 0; subZ < 4; ++subZ) {
                                if ((noiseZValue += noiseZStep) > 0.0D) {
                                    blockIDs[blockIndex += blockStride] = (short) NMBlocks.underCobble.blockID;
                                }
//                                else if(noiseY * 8 + subX < seaLevel){
//                                    blockIDs[blockIndex += blockStride] = (short) Block.waterStill.blockID;
//
//                                }

                                else {
                                    blockIDs[blockIndex += blockStride] = 0;
                                }
                            }

                            noiseX0 += noiseXStep0;
                            noiseX1 += noiseXStep1;
                        }

                        noise000 += noise000Step;
                        noise001 += noise001Step;
                        noise100 += noise100Step;
                        noise101 += noise101Step;
                    }
                }
            }
        }
    }

    public void replaceBlocksForBiome(int chunkX, int chunkZ, short[] blockIDs, byte[] metadata, BiomeGenBase[] biomes) {
        double stoneNoiseScale = 0.03125;
        this.stoneNoise = this.noiseGen4.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, stoneNoiseScale * 2.0, stoneNoiseScale * 2.0, stoneNoiseScale * 2.0);

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                BiomeGenBase biome = biomes[localZ + localX * 16];
                float biomeTemperature = biome.getFloatTemperature();
                int surfaceDepth = (int)(this.stoneNoise[localX + localZ * 16] / 3.0 + 5.0 + this.rand.nextDouble() * 0.5); // slightly thicker dirt for nicer look

                short topBlock = biome.topBlock;
                byte topBlockMetadata = biome.topBlockMetadata;
                short fillerBlock = biome.fillerBlock;
                byte fillerBlockMetadata = biome.fillerBlockMetadata;

                if (biome instanceof BiomeGenHighlands) {
                    fillerBlock = (short) NMBlocks.underCobble.blockID;
                    topBlock = (short) NMBlocks.underCobble.blockID;
                } else if (biome instanceof BiomeGenFlowerFields) {
                    fillerBlock = (short) NMBlocks.flowerDirt.blockID;
                    topBlock = (short) NMBlocks.flowerGrass.blockID;
                } else if (biome instanceof BiomeGenBlightlands) {
                    fillerBlock = (short) NMBlocks.underDirt.blockID;
                    topBlock = (short) NMBlocks.underGrass.blockID;
                }

                int remainingDepth = -1;

                for (int y = 255; y >= 0; --y) {
                    int blockIndex = (localZ * 16 + localX) * 256 + y;

                    if (y <= this.rand.nextInt(5)) {
                        blockIDs[blockIndex] = (short) Block.bedrock.blockID;
                        continue;
                    }

                    short currentBlock = blockIDs[blockIndex];
                    if (currentBlock == 0) {
                        remainingDepth = -1;
                        continue;
                    }

                    if (currentBlock != NMBlocks.underCobble.blockID) continue;

                    if (remainingDepth == -1) {
                        if (surfaceDepth <= 0) {
                            topBlock = 0;
                            fillerBlock = (short) Block.stone.blockID;
                        }

                        remainingDepth = surfaceDepth;

                        blockIDs[blockIndex] = topBlock;
                        metadata[blockIndex] = topBlockMetadata;
                        continue;
                    }

                    // place filler blocks below the surface
                    if (remainingDepth > 0) {
                        --remainingDepth;
                        blockIDs[blockIndex] = fillerBlock;
                        metadata[blockIndex] = fillerBlockMetadata;

                        // sandstone edge case
                        if (remainingDepth == 0 && fillerBlock == Block.sand.blockID && fillerBlockMetadata == 0) {
                            remainingDepth = this.rand.nextInt(4);
                            fillerBlock = (short) Block.sandStone.blockID;
                        }
                    }
                }
            }
        }
    }

    @Override
    public Chunk loadChunk(int par1, int par2) {
        return this.provideChunk(par1, par2);
    }
    
    @Override
    public Chunk provideChunk(int chunkX, int chunkZ) {
        this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
        short[] blockIDs = new short[65536];
        byte[] metadata = new byte[65536];
        this.generateTerrain(chunkX, chunkZ, blockIDs, metadata);
        // For radius 4: sample 4 (chunk scale) + 2*radius + 1 extra padding → 13 minimum, use 14
    //        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 6, chunkZ * 4 - 6, 16,16);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
        this.replaceBlocksForBiome(chunkX, chunkZ, blockIDs, metadata, this.biomesForGeneration);
        this.caveGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
        this.ravineGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
        if (true) {
            this.mineshaftGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
            this.villageGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
            this.strongholdGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
            this.scatteredFeatureGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
        }
        Chunk var4 = new Chunk(this.worldObj, blockIDs, metadata, chunkX, chunkZ);
        byte[] var5 = var4.getBiomeArray();
        for (int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = (byte)this.biomesForGeneration[var6].biomeID;
        }
        var4.generateSkylightMap();
        return var4;
    }

    private static int parabolicRadius = 10;
/*
    private double[] initializeNoiseFieldOriginal(double[] theNoiseArray, int par2x, int zero, int par4z, int noiseSizeX, int noiseSizeY, int noiseSizeZ) {
        if (theNoiseArray == null) {
            theNoiseArray = new double[noiseSizeX * noiseSizeY * noiseSizeZ];
        }
        if (this.parabolicField == null) {
            int size = 2 * parabolicRadius + 1;
            this.parabolicField = new float[size * size];
            for (int dx = -parabolicRadius; dx <= parabolicRadius; ++dx) {
                for (int dy = -parabolicRadius; dy <= parabolicRadius; ++dy) {
                    float dist = MathHelper.sqrt_float((float)(dx * dx + dy * dy)) + 0.2F;
                    this.parabolicField[dx + parabolicRadius + (dy + parabolicRadius) * size] = 10.0F / dist;
                }
            }
        }

        double var44 = 684.412;
        double var45 = 684.412;


//        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(par2x * 4, par4z * 4);
//        System.out.println("biome: "+ biome + " at " +"x"+(par2x * 4) + ", z" +(par4z * 4));
        // edit
        var44 *= 0.40d; // xz
//        var45 *= 0.1d; // y
//        if (biome == BiomeGenUnderworld.flowerFields) {
        // can not do this because it causes lots of issues with noise abruptly changing
//        }
        // edit
        this.noise6 = this.noiseGen6.generateNoiseOctaves(this.noise6, par2x, par4z, noiseSizeX, noiseSizeZ, 200.0, 200.0, 0.5);
        this.noise5 = this.noiseGen5.generateNoiseOctaves(this.noise5, par2x, par4z, noiseSizeX, noiseSizeZ, 1.121, 1.121, 0.5);
        this.noise3 = this.noiseGen3.generateNoiseOctaves(this.noise3, par2x, zero, par4z, noiseSizeX, noiseSizeY, noiseSizeZ, var44 / 80.0, var45 / 160.0, var44 / 80.0);
        this.noise2 = this.noiseGen2.generateNoiseOctaves(this.noise2, par2x, zero, par4z, noiseSizeX, noiseSizeY, noiseSizeZ, var44, var45, var44);
        this.noise1 = this.noiseGen1.generateNoiseOctaves(this.noise1, par2x, zero, par4z, noiseSizeX, noiseSizeY, noiseSizeZ, var44, var45, var44);
        short var12 = 0;
        short var13 = 0;
        int biomeStride = noiseSizeX + 5;
        double noiseYDiv32 = (double)noiseSizeY / 32.0;
        double noiseYDiv4 = (double)noiseSizeY / 4.0D;
        byte yTopLimit = (byte)(noiseSizeY - 4);

        for (byte var14 = 0; var14 < noiseSizeX; ++var14) {
            for (byte var15 = 0; var15 < noiseSizeZ; ++var15) {
                float var16 = 0.0f;
                float var17 = 0.0f;
                float var18 = 0.0f;

                int biomeBase = var14 + 2 + (var15 + 2) * biomeStride;

                BiomeGenBase var20 = this.biomesForGeneration[biomeBase];

                for (byte var21 = (byte) -2; var21 <= (byte) 2; ++var21) {
                    for (byte var22 = (byte) -2; var22 <= (byte) 2; ++var22) {
                        BiomeGenBase var23 = this.biomesForGeneration[biomeBase + var21 + var22 * biomeStride];

                        float minH = var23.minHeight;
                        float maxH = var23.maxHeight;
//                        if (minH > 0.0F) {
//                            minH = 1.0F + minH * 3.0F;
//                            maxH = 1.0F + maxH * 2.0F;
//                        }
                        float var24 = this.parabolicField[var21 + 2 + (var22 + 2) * 5] / (minH + 2.0F);
                        if (var23.minHeight > var20.minHeight) {
                            var24 /= 2.0F;
                        }
                        var16 += maxH * var24;
                        var17 += minH * var24;
                        var18 += var24;
                    }
                }
                var16 /= var18;
                var17 /= var18;
                var16 = var16 * 0.9f + 0.1f;
                var17 = (var17 * 4.0f - 1.0f) / 8.0f;

                double var47 = this.noise6[var13] / 8000.0D;

                if (var47 < 0.0D) {
                    var47 = -var47 * 0.3D;
                }

                var47 = var47 * 3.0D - 2.0D;

                if (var47 < 0.0D) {
                    var47 /= 2.0D;

                    if (var47 < -1.0D) {
                        var47 = -1.0D;
                    }

                    var47 /= 1.4D;
                    var47 /= 2.0D;
                } else {
                    if (var47 > 1.0) {
                        var47 = 1.0;
                    }
                    var47 /= 8.0;
                }
                ++var13;
                for (byte var46 = 0; var46 < noiseSizeY; ++var46) {
                    double var48 = var17;
                    double var26 = var16;
                    var48 += var47 * 0.2;
                    var48 = var48 * noiseYDiv32;   // keep this (we want variation to scale with new height)

                    double var28 = noiseYDiv4 + var48 * 4.0D; // Changed from par6/2.0D to par6/4.0D to lower base height

                    double var30 = 0;
                    double var32 = ((double)var46 - var28) * 12.0 * 128 / 128.0 / var26;
                    if (var32 < 0.0) {
                        var32 *= 4.0;
                    }
                    double var34 = this.noise1[var12] / 512.0;
                    double var36 = this.noise2[var12] / 512.0;
                    double var38 = (this.noise3[var12] / 10.0 + 1.0) / 2.0;
                    var30 = var38 < 0.0 ? var34 : (var38 > 1.0 ? var36 : var34 + (var36 - var34) * var38);
                    var30 -= var32;
                    if (var46 > yTopLimit) {
                        double var40 = (float)(var46 - yTopLimit) / 3.0f;
                        var30 = var30 * (1.0 - var40) + -10.0 * var40;
                    }
                    theNoiseArray[var12] = var30;
                    ++var12;
                }
            }
        }
        return theNoiseArray;
    }
*/

    private double[] initializeNoiseFieldInLayers(double[] theNoiseArray, int par2x, int zero, int par4z, int noiseSizeX, int noiseSizeY, int noiseSizeZ) {
        if (theNoiseArray == null) {
            theNoiseArray = new double[noiseSizeX * noiseSizeY * noiseSizeZ];
        }
        if (this.parabolicField == null) {
            int size = 2 * parabolicRadius + 1;
            this.parabolicField = new float[size * size];
            for (int dx = -parabolicRadius; dx <= parabolicRadius; ++dx) {
                for (int dy = -parabolicRadius; dy <= parabolicRadius; ++dy) {
                    float dist = MathHelper.sqrt_float((float)(dx * dx + dy * dy)) + 0.2F;
                    this.parabolicField[dx + parabolicRadius + (dy + parabolicRadius) * size] = 10.0F / dist;
                }
            }
        }

        double var44 = 684.412;  // XZ scale
        double var45 = 684.412;  // Y scale

        // tune both of them
        var44 *= 0.55D;   // xz
        var45 *= 1.8D;    // y

        this.noise6 = this.noiseGen6.generateNoiseOctaves(this.noise6, par2x, par4z, noiseSizeX, noiseSizeZ, 200.0, 200.0, 0.5);
        // noise5 is generated but unused in vanilla. can remove it for performance
//        this.noise5 = this.noiseGen5.generateNoiseOctaves(this.noise5, par2x, par4z, noiseSizeX, noiseSizeZ, 1.121, 1.121, 0.5);
        this.noise3 = this.noiseGen3.generateNoiseOctaves(this.noise3, par2x, zero, par4z, noiseSizeX, noiseSizeY, noiseSizeZ, var44 / 160.0, var45 / 320.0, var44 / 160.0); // SLOWER selector = huge smooth ranges
        this.noise2 = this.noiseGen2.generateNoiseOctaves(this.noise2, par2x, zero, par4z, noiseSizeX, noiseSizeY, noiseSizeZ, var44, var45, var44);
        this.noise1 = this.noiseGen1.generateNoiseOctaves(this.noise1, par2x, zero, par4z, noiseSizeX, noiseSizeY, noiseSizeZ, var44, var45, var44);

        short var12 = 0;
        short var13 = 0;
        int biomeStride = noiseSizeX + 5;
        double noiseYDiv32 = (double)noiseSizeY / 32.0;
        double noiseYDiv4 = (double)noiseSizeY / 4.0D;
        byte yTopLimit = (byte)(noiseSizeY - 4);

        for (byte var14 = 0; var14 < noiseSizeX; ++var14) {
            for (byte var15 = 0; var15 < noiseSizeZ; ++var15) {
                float var16 = 0.0f;
                float var17 = 0.0f;
                float var18 = 0.0f;

                int biomeBase = var14 + 2 + (var15 + 2) * biomeStride;
                BiomeGenBase var20 = this.biomesForGeneration[biomeBase];

                for (byte var21 = -2; var21 <= 2; ++var21) {
                    for (byte var22 = -2; var22 <= 2; ++var22) {
                        BiomeGenBase var23 = this.biomesForGeneration[biomeBase + var21 + var22 * biomeStride];
                        float minH = var23.minHeight;
                        float maxH = var23.maxHeight;

                        // amplifying mountains, though this applies for everything. CAN TUNE
                        if (minH > 0.0F) {
                            minH = 1.0F + minH * 4.0F;   // stronger mountains
                            maxH = 1.0F + maxH * 3.0F;
                        }

                        float var24 = this.parabolicField[var21 + 2 + (var22 + 2) * 5] / (minH + 2.0F);
                        if (var23.minHeight > var20.minHeight) var24 /= 2.0F;

                        var16 += maxH * var24;
                        var17 += minH * var24;
                        var18 += var24;
                    }
                }
                var16 /= var18;
                var17 /= var18;
                var16 = var16 * 0.9f + 0.1f;
                var17 = (var17 * 4.0f - 1.0f) / 8.0f;


                double var47 = this.noise6[var13] / 8000.0D;
                var47 = var47 * 5.0D - 2.0D;          // simplified
                var47 = Math.max(-1.0D, Math.min(1.0D, var47)); // clean clamp

                ++var13;

                for (byte var46 = 0; var46 < noiseSizeY; ++var46) {
                    double var48 = var17;
                    double var26 = var16;

                    var48 += var47 * 1.0D; // much stronger low-freq for huge ranges
                    var48 = var48 * noiseYDiv32;

                    double var28 = noiseYDiv4 + var48 * 5.0D; // higher multiplier = taller base mountains

                    double var30;
                    if (false) {
//                    if (var20.biomeID == BiomeGenUnderworld.flowerFields.biomeID) {
//                        double flatY = 35.0D;
//                        var30 = (flatY - (double)var46) ;
//                        double var32 = ((double) var46 - var28) * 12.0 * 128 / 128.0 / var26;
//                        if (var32 < 0.0) var32 *= 4.0;
//                        var30 -= var32;


                    } else {
                        double var32 = ((double) var46 - var28) * 12.0 * 128 / 128.0 / var26;
                        if (var32 < 0.0) var32 *= 4.0;

                        double var34 = this.noise1[var12] / 512.0;
                        double var36 = this.noise2[var12] / 512.0;

                        // smooth selector (no more abrupt spikes/islands)
                        double var38 = (this.noise3[var12] / 25.0 + 1.0) / 2.0; // div by 25 instead of by 10 || slower changes
                        var38 = MathHelper.clamp_float((float) var38, 0.0F, 1.0F);     // force clean blend
                        var30 = var34 + (var36 - var34) * var38;                // always smooth blend, no hard if-snap

                        var30 -= var32;


                    }
                    // stronger roof pull-down (kills any remaining floaters)
                    if (var46 > yTopLimit) {
                        double var40 = (float) (var46 - yTopLimit) / 3.0f;
                        var30 = var30 * (1.0 - var40) + -15.0 * var40;   // -15 instead of -10
                    }

                    theNoiseArray[var12] = var30;
                    ++var12;
                }
            }
        }
        return theNoiseArray;
    }

    @Override
    public boolean chunkExists(int par1, int par2) {
        return true;
    }
    @Override
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
    {
        int var4 = par2 * 16;
        int var5 = par3 * 16;
        BiomeGenBase var6 = this.worldObj.getBiomeGenForCoords(var4 + 16, var5 + 16);
        this.rand.setSeed(this.worldObj.getSeed());
        long var7 = this.rand.nextLong() / 2L * 2L + 1L;
        long var9 = this.rand.nextLong() / 2L * 2L + 1L;


        long lStructureSeedX = var7;
        long lStructureSeedZ = var9;

        this.rand.setSeed((long)par2 * var7 + (long)par3 * var9 ^ this.worldObj.getSeed());
        boolean var11 = false;

        if (this.mineshaftsEnabled)
        {
            this.mineshaftGenerator.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);
        }

        structureRand.setSeed((long)par2 * lStructureSeedX +
                (long)par3 * lStructureSeedZ ^ this.worldObj.getSeed());

        if (this.villagesEnabled)
        {
            var11 = villageGenerator.generateStructuresInChunk(worldObj, structureRand, par2, par3);
        }
        if (this.strongholdsEnabled)
        {
            strongholdGenerator.generateStructuresInChunk(worldObj, structureRand, par2, par3);
        }
        if (this.mapFeaturesEnabled)
        {
            scatteredFeatureGenerator.generateStructuresInChunk(worldObj, structureRand, par2, par3);
        }

        int var12;
        int var13;
        int var14;

        if (var6 != BiomeGenBase.desert && var6 != BiomeGenBase.desertHills && !var11 && this.rand.nextInt(4) == 0)
        {
            var12 = var4 + this.rand.nextInt(16) + 8;
            var13 = this.rand.nextInt(256); // Changed from 128 to 256 for full height range
            var14 = var5 + this.rand.nextInt(16) + 8;
            (new WorldGenLakes(Block.waterStill.blockID)).generate(this.worldObj, this.rand, var12, var13, var14);
        }

        if (!var11 && this.rand.nextInt(8) == 0)
        {
            var12 = var4 + this.rand.nextInt(16) + 8;
            var13 = this.rand.nextInt(this.rand.nextInt(248) + 8); // Changed from 120 to 248 to support higher generation
            var14 = var5 + this.rand.nextInt(16) + 8;

            if (var13 < 63 || this.rand.nextInt(10) == 0)
            {
                (new WorldGenLakes(Block.lavaStill.blockID)).generate(this.worldObj, this.rand, var12, var13, var14);
            }
        }

        if (this.dungeonsEnabled) {
            for (var12 = 0; var12 < 8; ++var12) {
                var13 = var4 + this.rand.nextInt(16) + 8;
                var14 = this.rand.nextInt(256); // Changed from 128 to 256 for full height range
                int var15 = var5 + this.rand.nextInt(16) + 8;
                (new WorldGenDungeons()).generate(this.worldObj, this.rand, var13, var14, var15);
            }
        }

        var6.decorate(this.worldObj, this.rand, var4, var5);
        SpawnerAnimals.performWorldGenSpawning(this.worldObj, var6, var4 + 8, var5 + 8, 16, 16, this.rand);
        var4 += 8;
        var5 += 8;

        for (var12 = 0; var12 < 16; ++var12)
        {
            for (var13 = 0; var13 < 16; ++var13)
            {
                var14 = this.worldObj.getPrecipitationHeight(var4 + var12, var5 + var13);

                if (this.worldObj.isBlockFreezable(var12 + var4, var14 - 1, var13 + var5))
                {
                    this.worldObj.setBlock(var12 + var4, var14 - 1, var13 + var5, Block.ice.blockID, 0, 2);
                }

                if (this.worldObj.canSnowAt(var12 + var4, var14, var13 + var5))
                {
                    this.worldObj.setBlock(var12 + var4, var14, var13 + var5, Block.snow.blockID, 0, 2);
                }
                else if (this.worldObj.canSnowAt(var12 + var4, var14 + 1, var13 + var5))
                {
                    this.worldObj.setBlock(var12 + var4, var14 + 1, var13 + var5, Block.snow.blockID, 0, 2);
                }
            }
        }

        btwPostProcessChunk(worldObj, var4 - 8, var5 - 8); // -8 because of += 8 offset applied above
    }

    @Override
    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
        return true;
    }

    @Override
    public void saveExtraData() {
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "RandomLevelSource";
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType mobType, int x, int y, int z) {
        BiomeGenBase biomeGenBase = this.worldObj.getBiomeGenForCoords(x, z);
        return biomeGenBase == null ? null : (mobType == EnumCreatureType.monster &&
                this.scatteredFeatureGenerator.shouldUseStructureSpawnTable(x, y, z)
                ? this.scatteredFeatureGenerator.getScatteredFeatureSpawnList()
                : biomeGenBase.getSpawnableList(mobType));
        // whether I should use the structure's spawn table or the biome's spawn table. shouldUseStructureSpawnTable() has the structure checking logic. currently it just checks for a witch hut
    }

    @Override
    public ChunkPosition findClosestStructure(World par1World, String par2Str, int par3, int par4, int par5) {
        return "Stronghold".equals(par2Str) && this.strongholdGenerator != null ? this.strongholdGenerator.getNearestInstance(par1World, par3, par4, par5) : null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int par1, int par2) {
        if (true) {
            this.mineshaftGenerator.generate(this, this.worldObj, par1, par2, null, null);
            this.villageGenerator.generate(this, this.worldObj, par1, par2, null, null);
            this.strongholdGenerator.generate(this, this.worldObj, par1, par2, null, null);
            this.scatteredFeatureGenerator.generate(this, this.worldObj, par1, par2, null, null);
        }
    }

    private void btwPostProcessChunk(World worldObj, int iChunkX, int iChunkZ) {
        if (worldObj.provider.dimensionId == 0 || worldObj.provider.dimensionId == NMFields.UNDERWORLD_DIMENSION) {
            this.generateStrata(worldObj, iChunkX, iChunkZ);
            this.generateAdditionalBrownMushrooms(worldObj, iChunkX, iChunkZ);
        }
    }

    private void generateAdditionalBrownMushrooms(World worldObj, int iChunkX, int iChunkZ) {
        if (worldObj.rand.nextInt(4) == 0) {
            WorldGenFlowers mushroomBrownGen = new WorldGenFlowers(Block.mushroomBrown.blockID);
            int iMushroomX = iChunkX + worldObj.rand.nextInt(16) + 8;
            int iMushroomY = worldObj.rand.nextInt(25);
            int iMushroomZ = iChunkZ + worldObj.rand.nextInt(16) + 8;
            ((WorldGenerator)mushroomBrownGen).generate(worldObj, worldObj.rand, iMushroomX, iMushroomY, iMushroomZ);
        }
    }

    private void generateStrata(World world, int iChunkX, int iChunkZ) {
        Chunk chunk = world.getChunkFromChunkCoords(iChunkX >> 4, iChunkZ >> 4);
        for (int iTempI = 0; iTempI < 16; ++iTempI) {
            for (int iTempK = 0; iTempK < 16; ++iTempK) {
                int iTempBlockID;
                int iTempJ;
                int iStrataHeight = 24 + world.rand.nextInt(2);
                for (iTempJ = 0; iTempJ <= iStrataHeight; ++iTempJ) {
                    iTempBlockID = chunk.getBlockID(iTempI, iTempJ, iTempK);
                    if (iTempBlockID != Block.stone.blockID) continue;
                    chunk.setBlockMetadata(iTempI, iTempJ, iTempK, 2);
                }
                iStrataHeight = 48 + world.rand.nextInt(2);
                while (iTempJ <= iStrataHeight) {
                    iTempBlockID = chunk.getBlockID(iTempI, iTempJ, iTempK);
                    if (iTempBlockID == Block.stone.blockID) {
                        chunk.setBlockMetadata(iTempI, iTempJ, iTempK, 1);
                    }
                    ++iTempJ;
                }

                // simply makes every block understone above y48
                iStrataHeight = 255;
                while (iTempJ <= iStrataHeight) {
                    iTempBlockID = chunk.getBlockID(iTempI, iTempJ, iTempK);
                    if (iTempBlockID == Block.stone.blockID) {
                        chunk.setBlockIDWithMetadata(iTempI, iTempJ, iTempK, NMBlocks.underCobble.blockID, 0);
                    }
                    ++iTempJ;
                }

            }
        }
    }

//    public MapGenStronghold getStrongholdGenerator() {
//        return this.strongholdGenerator;
//    }
//
//    public MapGenVillage getVillageGenerator() {
//        return this.villageGenerator;
//    }
//
//    public MapGenMineshaft getMineshaftGenerator() {
//        return this.mineshaftGenerator;
//    }
//
//    public CustomMapGenSF getScatteredFeatureGenerator() {
//        return this.scatteredFeatureGenerator;
//    }
}




