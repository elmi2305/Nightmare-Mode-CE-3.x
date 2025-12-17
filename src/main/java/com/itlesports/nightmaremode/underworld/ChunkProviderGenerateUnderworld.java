package com.itlesports.nightmaremode.underworld;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.Arrays;
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
    private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
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

    public void generateTerrain(int chunkX, int chunkZ, short[] blockIDs, byte[] metadata) {
        int noiseScaleXZ = 4;
        int noiseScaleY = 16;
        int seaLevel = 80;
        int noiseSizeX = noiseScaleXZ + 1; // 5
        int noiseSizeY = 17;
        int noiseSizeZ = noiseScaleXZ + 1; // 5

        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, noiseSizeX + 5, noiseSizeZ + 5);

        this.noiseArray = this.initializeNoiseField(this.noiseArray, chunkX * noiseScaleXZ, 0, chunkZ * noiseScaleXZ, noiseSizeX, noiseSizeY, noiseSizeZ);

        for (int noiseX = 0; noiseX < noiseScaleXZ; ++noiseX) {
            for (int noiseZ = 0; noiseZ < noiseScaleXZ; ++noiseZ) {
                for (int noiseY = 0; noiseY < noiseScaleY; ++noiseY) {
                    double verticalInterpStep = 0.125;

                    double noise000 = this.noiseArray[((noiseX + 0) * noiseSizeZ + noiseZ + 0) * noiseSizeY + noiseY + 0];
                    double noise001 = this.noiseArray[((noiseX + 0) * noiseSizeZ + noiseZ + 1) * noiseSizeY + noiseY + 0];
                    double noise100 = this.noiseArray[((noiseX + 1) * noiseSizeZ + noiseZ + 0) * noiseSizeY + noiseY + 0];
                    double noise101 = this.noiseArray[((noiseX + 1) * noiseSizeZ + noiseZ + 1) * noiseSizeY + noiseY + 0];

                    double noise000Step = (this.noiseArray[((noiseX + 0) * noiseSizeZ + noiseZ + 0) * noiseSizeY + noiseY + 1] - noise000) * verticalInterpStep;
                    double noise001Step = (this.noiseArray[((noiseX + 0) * noiseSizeZ + noiseZ + 1) * noiseSizeY + noiseY + 1] - noise001) * verticalInterpStep;
                    double noise100Step = (this.noiseArray[((noiseX + 1) * noiseSizeZ + noiseZ + 0) * noiseSizeY + noiseY + 1] - noise100) * verticalInterpStep;
                    double noise101Step = (this.noiseArray[((noiseX + 1) * noiseSizeZ + noiseZ + 1) * noiseSizeY + noiseY + 1] - noise101) * verticalInterpStep;

                    for (int subY = 0; subY < 8; ++subY) {
                        double horizontalInterpStep = 0.25;

                        double noiseX0 = noise000;
                        double noiseX1 = noise001;

                        double noiseXStep0 = (noise100 - noise000) * horizontalInterpStep;
                        double noiseXStep1 = (noise101 - noise001) * horizontalInterpStep;

                        for (int subX = 0; subX < 4; ++subX) {
                            int blockIndex = subX + noiseX * 4 << 11 | 0 + noiseZ * 4 << 7 | noiseY * 8 + subY;
                            int blockStride = 128;
                            blockIndex -= blockStride;

                            double depthInterpStep = 0.25;
                            double noiseZStep = (noiseX1 - noiseX0) * depthInterpStep;
                            double noiseZValue = noiseX0 - noiseZStep;

                            for (int subZ = 0; subZ < 4; ++subZ) {
                                if ((noiseZValue += noiseZStep) > 0.0D) {
                                    blockIDs[blockIndex += blockStride] = (byte)Block.stone.blockID;
                                }
//                                else if (noiseY * 8 + subY < seaLevel) {
//                                    blockIDs[blockIndex += blockStride] = (byte)Block.waterStill.blockID;
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
        int seaLevel = 80;  // Sync with generateTerrain
        double stoneNoiseScale = 0.03125;
        this.stoneNoise = this.noiseGen4.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, stoneNoiseScale * 2.0, stoneNoiseScale * 2.0, stoneNoiseScale * 2.0);
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                BiomeGenBase biome = biomes[localZ + localX * 16];
                float biomeTemperature = biome.getFloatTemperature();
                int surfaceDepth = (int)(this.stoneNoise[localX + localZ * 16] / 3.0 + 3.0 + this.rand.nextDouble() * 0.25);
                int remainingDepth = -1;
                short topBlock = biome.topBlock;
                byte topBlockMetadata = biome.topBlockMetadata;
                short fillerBlock = biome.fillerBlock;
                byte fillerBlockMetadata = biome.fillerBlockMetadata;
                for (int y = 127; y >= 0; --y) {
                    int blockIndex = (localZ * 16 + localX) * 128 + y;
                    if (y <= 0 + this.rand.nextInt(5)) {
                        blockIDs[blockIndex] = (short)Block.bedrock.blockID;
                        continue;
                    }
                    short currentBlock = blockIDs[blockIndex];
                    if (currentBlock == 0) {
                        remainingDepth = -1;
                        continue;
                    }
                    if (currentBlock != Block.stone.blockID) continue;
                    if (remainingDepth == -1) {
                        if (surfaceDepth <= 0) {
                            topBlock = 0;
                            fillerBlock = (short)Block.stone.blockID;
                        }
                        else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
                            topBlock = biome.topBlock;
                            fillerBlock = biome.fillerBlock;
                        }


                        if (y < seaLevel && topBlock == 0) {

                            // edit
//                            if (biomeTemperature < 0.15f) {
//                                topBlock = (short)Block.ice.blockID;
//                                topBlockMetadata = 1;
//                            } else {
//                                topBlock = (short)Block.waterStill.blockID;
//                            }
                            // edit
                        }
                        remainingDepth = surfaceDepth;
                        // edit
                        if (true) {
//                        if (y >= seaLevel - 1) {
                            blockIDs[blockIndex] = topBlock;
                            metadata[blockIndex] = topBlockMetadata;
                            continue;
                        }
                        // edit
                        blockIDs[blockIndex] = fillerBlock;
                        metadata[blockIndex] = fillerBlockMetadata;
                        continue;
                    }
                    if (remainingDepth <= 0) continue;
                    blockIDs[blockIndex] = fillerBlock;
                    metadata[blockIndex] = fillerBlockMetadata;
                    if (--remainingDepth != 0 || fillerBlock != Block.sand.blockID || fillerBlockMetadata != 0) continue;
                    remainingDepth = this.rand.nextInt(4);
                    fillerBlock = (short)Block.sandStone.blockID;
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
        short[] blockIDs = new short[32768];
        byte[] metadata = new byte[32768];
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

    private static int parabolicRadius = 5;
    private static int biomesForGenerationMagicNumber = 10;
    private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2x, int par3, int par4z, int par5, int par6, int par7) {
        if (par1ArrayOfDouble == null) {
            par1ArrayOfDouble = new double[par5 * par6 * par7];
        }
        if (this.parabolicField == null) {
            int size = 2 * parabolicRadius + 1; // e.g., 3 for radius=1
            this.parabolicField = new float[size * size];
            for (int dx = -parabolicRadius; dx <= parabolicRadius; ++dx) {
                for (int dy = -parabolicRadius; dy <= parabolicRadius; ++dy) {
                    float dist = MathHelper.sqrt_float((float)(dx * dx + dy * dy)) + 0.2F;
                    // Optional tweaks: Adjust 10.0F for stronger/weaker center (higher=more local bias)
                    this.parabolicField[dx + parabolicRadius + (dy + parabolicRadius) * size] = 10.0F / dist;
                }
            }
        }

        double var44 = 684.412;
        double var45 = 684.412;

        // edit
        var44 *= 0.7d; // xz
        var45 *= 1.5d; // y
        // edit
        this.noise5 = this.noiseGen5.generateNoiseOctaves(this.noise5, par2x, par4z, par5, par7, 1.121, 1.121, 0.5);
        this.noise6 = this.noiseGen6.generateNoiseOctaves(this.noise6, par2x, par4z, par5, par7, 200.0, 200.0, 0.5);
        this.noise3 = this.noiseGen3.generateNoiseOctaves(this.noise3, par2x, par3, par4z, par5, par6, par7, var44 / 80.0, var45 / 160.0, var44 / 80.0);
        this.noise1 = this.noiseGen1.generateNoiseOctaves(this.noise1, par2x, par3, par4z, par5, par6, par7, var44, var45, var44);
        this.noise2 = this.noiseGen2.generateNoiseOctaves(this.noise2, par2x, par3, par4z, par5, par6, par7, var44, var45, var44);
        int var12 = 0;
        int var13 = 0;
        for (int var14 = 0; var14 < par5; ++var14) {
            for (int var15 = 0; var15 < par7; ++var15) {
                float var16 = 0.0f;
                float var17 = 0.0f;
                float var18 = 0.0f;

                int var19 = 2;
                BiomeGenBase var20 = this.biomesForGeneration[var14 + 2 + (var15 + 2) * (par5 + 5)];
                for (int var21 = -var19; var21 <= var19; ++var21) {
                    for (int var22 = -var19; var22 <= var19; ++var22) {
                        BiomeGenBase var23 = this.biomesForGeneration[var14 + var21 + 2 + (var15 + var22 + 2) * (par5 + 5)];
                        float minH = var23.minHeight;
                        float maxH = var23.maxHeight;
                        if (minH > 0.0F) {
                            minH = 1.0F + minH * 3.0F;
                            maxH = 1.0F + maxH * 2.0F;

//                            minH = Math.max(minH, 0.2f);
                        }
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
                double var47 = this.noise6[var13] / 8000.0;
                if (var47 < 0.0) {
                    var47 = -var47 * 0.3;
                }
                if ((var47 = var47 * 3.0 - 2.0) < 0.0) {
                    if ((var47 /= 2.0) < -1.0) {
                        var47 = -1.0;
                    }
                    var47 /= 1.4;
                    var47 /= 2.0;
                } else {
                    if (var47 > 1.0) {
                        var47 = 1.0;
                    }
                    var47 /= 8.0;
                }
                ++var13;
                for (int var46 = 0; var46 < par6; ++var46) {
                    double var48 = var17;
                    double var26 = var16;
                    var48 += var47 * 0.2;
                    var48 = var48 * (double)par6 / 16.0;
                    double var28 = (double)par6 / 2.0 + var48 * 4.0;
                    double var30 = 0;
                    double var32 = ((double)var46 - var28) * 12.0 * 128.0 / 128.0 / var26;
                    if (var32 < 0.0) {
                        var32 *= 4.0;
                    }
                    double var34 = this.noise1[var12] / 512.0;
                    double var36 = this.noise2[var12] / 512.0;
                    double var38 = (this.noise3[var12] / 10.0 + 1.0) / 2.0;
                    var30 = var38 < 0.0 ? var34 : (var38 > 1.0 ? var36 : var34 + (var36 - var34) * var38);
                    var30 -= var32;
                    if (var46 > par6 - 4) {
                        double var40 = (float)(var46 - (par6 - 4)) / 3.0f;
                        var30 = var30 * (1.0 - var40) + -10.0 * var40;
                    }
                    par1ArrayOfDouble[var12] = var30;
                    ++var12;
                }
            }
        }
        return par1ArrayOfDouble;
    }

    @Override
    public boolean chunkExists(int par1, int par2) {
        return true;
    }

    @Override
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {
        int var14;
        int var13;
        int var12;
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
        if (true) {
            this.mineshaftGenerator.generateStructuresInChunk(this.worldObj, this.rand, par2, par3);
            this.structureRand.setSeed((long)par2 * lStructureSeedX + (long)par3 * lStructureSeedZ ^ this.worldObj.getSeed());
            var11 = this.villageGenerator.generateStructuresInChunk(this.worldObj, this.structureRand, par2, par3);
            this.strongholdGenerator.generateStructuresInChunk(this.worldObj, this.structureRand, par2, par3);
            this.scatteredFeatureGenerator.generateStructuresInChunk(this.worldObj, this.structureRand, par2, par3);
        }
        if (var6 != BiomeGenBase.desert && var6 != BiomeGenBase.desertHills && !var11 && this.rand.nextInt(4) == 0) {
            var12 = var4 + this.rand.nextInt(16) + 8;
            // edit
            var13 = this.rand.nextInt(220);
            // edit
//            var13 = this.rand.nextInt(128);
            var14 = var5 + this.rand.nextInt(16) + 8;
            new WorldGenLakes(Block.waterStill.blockID).generate(this.worldObj, this.rand, var12, var13, var14);
        }
        if (!var11 && this.rand.nextInt(8) == 0) {
            var12 = var4 + this.rand.nextInt(16) + 8;
            var13 = this.rand.nextInt(this.rand.nextInt(120) + 8);
            var14 = var5 + this.rand.nextInt(16) + 8;
            if (var13 < 63 || this.rand.nextInt(10) == 0) {
                new WorldGenLakes(Block.lavaStill.blockID).generate(this.worldObj, this.rand, var12, var13, var14);
            }
        }
        for (var12 = 0; var12 < 8; ++var12) {
            var13 = var4 + this.rand.nextInt(16) + 8;
            // edit
            var14 = this.rand.nextInt(220);
            // edit
//            var14 = this.rand.nextInt(128);
            int var15 = var5 + this.rand.nextInt(16) + 8;
            new WorldGenDungeons().generate(this.worldObj, this.rand, var13, var14, var15);
        }
        var6.decorate(this.worldObj, this.rand, var4, var5);
        SpawnerAnimals.performWorldGenSpawning(this.worldObj, var6, var4 + 8, var5 + 8, 16, 16, this.rand);
        var4 += 8;
        var5 += 8;
        for (var12 = 0; var12 < 16; ++var12) {
            for (var13 = 0; var13 < 16; ++var13) {
                var14 = this.worldObj.getPrecipitationHeight(var4 + var12, var5 + var13);
                if (this.worldObj.isBlockFreezable(var12 + var4, var14 - 1, var13 + var5)) {
                    this.worldObj.setBlock(var12 + var4, var14 - 1, var13 + var5, Block.ice.blockID, 1, 2);
                }
                if (this.worldObj.canSnowAt(var12 + var4, var14, var13 + var5)) {
                    this.worldObj.setBlock(var12 + var4, var14, var13 + var5, Block.snow.blockID, 0, 2);
                    continue;
                }
                if (!this.worldObj.canSnowAt(var12 + var4, var14 + 1, var13 + var5)) continue;
                this.worldObj.setBlock(var12 + var4, var14 + 1, var13 + var5, Block.snow.blockID, 0, 2);
            }
        }
        this.btwPostProcessChunk(this.worldObj, var4 - 8, var5 - 8);
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
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4) {
        BiomeGenBase var5 = this.worldObj.getBiomeGenForCoords(par2, par4);
        return var5 == null ? null : (par1EnumCreatureType == EnumCreatureType.monster && this.scatteredFeatureGenerator.func_143030_a(par2, par3, par4) ? this.scatteredFeatureGenerator.getScatteredFeatureSpawnList() : var5.getSpawnableList(par1EnumCreatureType));
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
        if (worldObj.provider.dimensionId == 0) {
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
            }
        }
    }

    public MapGenStronghold getStrongholdGenerator() {
        return this.strongholdGenerator;
    }

    public MapGenVillage getVillageGenerator() {
        return this.villageGenerator;
    }

    public MapGenMineshaft getMineshaftGenerator() {
        return this.mineshaftGenerator;
    }

    public MapGenScatteredFeature getScatteredFeatureGenerator() {
        return this.scatteredFeatureGenerator;
    }
}




