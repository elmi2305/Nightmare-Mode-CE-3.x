package com.itlesports.nightmaremode.underworld;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenBlightlands;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenFlowerFields;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenHighlands;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.MapGenScatteredFeatureUnderworld;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld.TerrainProfile;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class ChunkProviderGenerateUnderworld implements IChunkProvider {
    private final Random rand;
    private final NoiseGeneratorOctaves noiseGen4;
    public NoiseGeneratorOctaves noiseGen6;
    public NoiseGeneratorOctaves mobSpawnerNoise;
    private final World worldObj;
    private final TerrainGenerator terrainGenerator;
    private double[] stoneNoise = new double[256];
    private final MapGenBase caveGenerator = new com.itlesports.nightmaremode.underworld.worldgen.MapGenCavesUnderworld();
    private final MapGenStronghold strongholdGenerator = new MapGenStronghold();
    private final MapGenVillage villageGenerator = new MapGenVillage();
    private final MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
    private final MapGenScatteredFeatureUnderworld scatteredFeatureGenerator = new MapGenScatteredFeatureUnderworld();
    private final WorldGenFlowers mushroomBrownGen = new WorldGenFlowers(Block.mushroomBrown.blockID);
    private final MapGenBase ravineGenerator = new com.itlesports.nightmaremode.underworld.worldgen.MapGenRavineUnderworld();
    private BiomeGenBase[] biomesForGeneration;
    private final Random structureRand;
    private final boolean mineshaftsEnabled = false;
    private final boolean villagesEnabled = false;
    private final boolean strongholdsEnabled = false;
    private final boolean mapFeaturesEnabled = true;
    private final boolean dungeonsEnabled = false;

    public ChunkProviderGenerateUnderworld(World par1World, long par2) {
        this.worldObj = par1World;
        this.rand = new Random(par2);
        this.structureRand = new Random(par2);
        NoiseGeneratorOctaves lowerNoise = new NoiseGeneratorOctaves(this.rand, 16);
        NoiseGeneratorOctaves upperNoise = new NoiseGeneratorOctaves(this.rand, 16);
        NoiseGeneratorOctaves selectorNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseGen4 = new NoiseGeneratorOctaves(this.rand, 4);
        this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 6);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.terrainGenerator = new TerrainGenerator(lowerNoise, upperNoise, selectorNoise, this.noiseGen6);
    }

    public void generateTerrain(int chunkX, int chunkZ, short[] blockIDs, byte[] metadata) {
        int sampleCount = TerrainGenerator.HORIZONTAL_SAMPLES;
        int radius = TerrainGenerator.BIOME_BLEND_RADIUS;
        int biomeSamples = TerrainGenerator.BIOME_SAMPLES;
        int sampleX = chunkX * (sampleCount - 1);
        int sampleZ = chunkZ * (sampleCount - 1);
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(
                this.biomesForGeneration, sampleX - radius, sampleZ - radius, biomeSamples, biomeSamples);
        TerrainProfile[] profiles = new TerrainProfile[biomeSamples * biomeSamples];
        for (int i = 0; i < profiles.length; i++) {
            BiomeGenBase biome = this.biomesForGeneration[i];
            profiles[i] = biome instanceof BiomeGenUnderworld
                    ? ((BiomeGenUnderworld)biome).getTerrainProfile() : TerrainProfile.BLIGHTLANDS;
        }
        this.terrainGenerator.generate(chunkX, chunkZ, profiles, blockIDs, (short)NMBlocks.underCobble.blockID);
    }

    public void replaceBlocksForBiome(int chunkX, int chunkZ, short[] blockIDs, byte[] metadata, BiomeGenBase[] biomes) {
        double stoneNoiseScale = 0.03125;
        this.stoneNoise = this.noiseGen4.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, stoneNoiseScale * 2.0, stoneNoiseScale * 2.0, stoneNoiseScale * 2.0);

        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {

                BiomeGenBase biome = biomes[localX + localZ * 16];
                int surfaceDepth = (int)(this.stoneNoise[localX + localZ * 16] / 3.0 + 5.0 + this.rand.nextDouble() * 0.5); // slightly thicker dirt for nicer look

                short topBlock = biome.topBlock;
                byte topBlockMetadata = biome.topBlockMetadata;
                short fillerBlock = biome.fillerBlock;
                byte fillerBlockMetadata = biome.fillerBlockMetadata;

                if (biome instanceof BiomeGenHighlands) {
                    fillerBlock = (short) NMBlocks.underCobble.blockID;
                    topBlock = (short) NMBlocks.underCobble.blockID;
                } else if (biome instanceof BiomeGenFlowerFields) {
                    fillerBlock = (short) NMBlocks.underFlowerDirts.blockID; // flower dirt
                    topBlock = (short) NMBlocks.underFlowerDirts.blockID; // flower grass

                    topBlockMetadata = (short) NMBlocks.META_FLOWER_GRASS;
                    fillerBlockMetadata = (short) NMBlocks.META_FLOWER_DIRT;
                } else if (biome instanceof BiomeGenBlightlands) {
                    fillerBlock = (short) NMBlocks.underFlowerDirts.blockID;
                    topBlock = (short) NMBlocks.underGrass.blockID;

                    fillerBlockMetadata = (short) NMBlocks.META_UNDER_DIRT;
                }

                int remainingDepth = -1;
                int bedrockCutoff = this.rand.nextInt(5);

                for (int y = 255; y >= 0; --y) {
                    int blockIndex = (localX * 16 + localZ) * TerrainGenerator.WORLD_HEIGHT + y;
                    if (y <= bedrockCutoff) {
                        blockIDs[blockIndex] = (short) Block.bedrock.blockID;
                        continue;
                    }

                    short currentBlock = blockIDs[blockIndex];
                    if (currentBlock == 0) {
                        remainingDepth = -1;
                        continue;
                    }

                    if (currentBlock != NMBlocks.underCobble.blockID) continue;
                    if (y < 48) {
                        blockIDs[blockIndex] = (short)NMBlocks.underrock.blockID;
                        metadata[blockIndex] = (byte)(y <= 24 ? 2 : 1);
                        continue;
                    }

                    if (remainingDepth == -1) {
                        if (surfaceDepth <= 0) {
                            topBlock = 0;
                            fillerBlock = (short) NMBlocks.underCobble.blockID;
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
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
        this.replaceBlocksForBiome(chunkX, chunkZ, blockIDs, metadata, this.biomesForGeneration);
        this.caveGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
        this.ravineGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
        if (this.mapFeaturesEnabled) {
            if (this.mineshaftsEnabled) {
            this.mineshaftGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
            }
            if (this.villagesEnabled) {
            this.villageGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
            }
            if (this.strongholdsEnabled) {
            this.strongholdGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockIDs, metadata);
            }
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
        long lStructureSeedX = this.rand.nextLong() / 2L * 2L + 1L;
        long lStructureSeedZ = this.rand.nextLong() / 2L * 2L + 1L;


        this.rand.setSeed((long)par2 * lStructureSeedX + (long)par3 * lStructureSeedZ ^ this.worldObj.getSeed());
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

        // EDIT: removed snow generation
//        for (var12 = 0; var12 < 16; ++var12)
//        {
//            for (var13 = 0; var13 < 16; ++var13)
//            {
//                var14 = this.worldObj.getPrecipitationHeight(var4 + var12, var5 + var13);
//
//                if (this.worldObj.isBlockFreezable(var12 + var4, var14 - 1, var13 + var5))
//                {
//                    this.worldObj.setBlock(var12 + var4, var14 - 1, var13 + var5, Block.ice.blockID, 0, 2);
//                }
//
//                if (this.worldObj.canSnowAt(var12 + var4, var14, var13 + var5))
//                {
//                    this.worldObj.setBlock(var12 + var4, var14, var13 + var5, Block.snow.blockID, 0, 2);
//                }
//                else if (this.worldObj.canSnowAt(var12 + var4, var14 + 1, var13 + var5))
//                {
//                    this.worldObj.setBlock(var12 + var4, var14 + 1, var13 + var5, Block.snow.blockID, 0, 2);
//                }
//            }
//        }
        // EDIT

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
        if (this.mapFeaturesEnabled) {
            if (this.mineshaftsEnabled) {
            this.mineshaftGenerator.generate(this, this.worldObj, par1, par2, null, null);
            }
            if (this.villagesEnabled) {
            this.villageGenerator.generate(this, this.worldObj, par1, par2, null, null);
            }
            if (this.strongholdsEnabled) {
            this.strongholdGenerator.generate(this, this.worldObj, par1, par2, null, null);
            }
            this.scatteredFeatureGenerator.generate(this, this.worldObj, par1, par2, null, null);
        }
    }

    private void btwPostProcessChunk(World worldObj, int iChunkX, int iChunkZ) {
        if (worldObj.provider.dimensionId == 0 || worldObj.provider.dimensionId == NMFields.UNDERWORLD_DIMENSION) {
            this.generateAdditionalBrownMushrooms(worldObj, iChunkX, iChunkZ);
        }
    }

    private void generateAdditionalBrownMushrooms(World worldObj, int chunkBlockX, int chunkBlockZ) {
        if (this.rand.nextInt(4) == 0) {
            int x = chunkBlockX + this.rand.nextInt(16) + 8;
            int y = this.rand.nextInt(25);
            int z = chunkBlockZ + this.rand.nextInt(16) + 8;
            mushroomBrownGen.generate(worldObj, this.rand, x, y, z);
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
    // instance-owned noise and buffers shared by terrain generation and structure-height sampling.
    public static final class TerrainGenerator {
        public static final int WORLD_HEIGHT = 256;
        public static final int CHUNK_SIZE = 16;
        public static final int HORIZONTAL_STEP = 4;
        public static final int VERTICAL_STEP = 8;
        public static final int HORIZONTAL_SAMPLES = CHUNK_SIZE / HORIZONTAL_STEP + 1;
        public static final int VERTICAL_SAMPLES = WORLD_HEIGHT / VERTICAL_STEP + 1;
        public static final int BIOME_BLEND_RADIUS = 2;
        public static final int BIOME_SAMPLES = HORIZONTAL_SAMPLES + BIOME_BLEND_RADIUS * 2;

        private final NoiseGeneratorOctaves lowerNoise;
        private final NoiseGeneratorOctaves upperNoise;
        private final NoiseGeneratorOctaves selectorNoise;
        private final NoiseGeneratorOctaves reliefNoise;
        private final double[] blendWeights = new double[25];
        private double[] lowerValues;
        private double[] upperValues;
        private double[] selectorValues;
        private double[] reliefValues;
        private final double[] density = new double[HORIZONTAL_SAMPLES * HORIZONTAL_SAMPLES * VERTICAL_SAMPLES];

        public static TerrainGenerator forSeed(long seed) {
            Random random = new Random(seed);
            NoiseGeneratorOctaves lower = new NoiseGeneratorOctaves(random, 16);
            NoiseGeneratorOctaves upper = new NoiseGeneratorOctaves(random, 16);
            NoiseGeneratorOctaves selector = new NoiseGeneratorOctaves(random, 8);
            // match the chunk provider's surface-noise allocation before its relief generator.
            new NoiseGeneratorOctaves(random, 4);
            return new TerrainGenerator(lower, upper, selector, new NoiseGeneratorOctaves(random, 6));
        }

        public TerrainGenerator(NoiseGeneratorOctaves lowerNoise, NoiseGeneratorOctaves upperNoise,
                                         NoiseGeneratorOctaves selectorNoise, NoiseGeneratorOctaves reliefNoise) {
            this.lowerNoise = lowerNoise;
            this.upperNoise = upperNoise;
            this.selectorNoise = selectorNoise;
            this.reliefNoise = reliefNoise;
            for (int z = -BIOME_BLEND_RADIUS; z <= BIOME_BLEND_RADIUS; z++) {
                for (int x = -BIOME_BLEND_RADIUS; x <= BIOME_BLEND_RADIUS; x++) {
                    blendWeights[x + BIOME_BLEND_RADIUS + (z + BIOME_BLEND_RADIUS) * 5]
                            = 1.0 / Math.sqrt(x * x + z * z + 0.2);
                }
            }
        }

        public void generate(int chunkX, int chunkZ, TerrainProfile[] profiles,
                             short[] blocks, short terrainBlock) {
            sampleDensity(chunkX, chunkZ, profiles);
            int cells = HORIZONTAL_SAMPLES - 1;
            for (int x = 0; x < cells; x++) {
                for (int z = 0; z < cells; z++) {
                    int base = (x * HORIZONTAL_SAMPLES + z) * VERTICAL_SAMPLES;
                    int nextX = base + HORIZONTAL_SAMPLES * VERTICAL_SAMPLES;
                    for (int y = 0; y < VERTICAL_SAMPLES - 1; y++) {
                        for (int dy = 0; dy < VERTICAL_STEP; dy++) {
                            double fy = (double)dy / VERTICAL_STEP;
                            double d00 = lerp(density[base + y], density[base + y + 1], fy);
                            double d01 = lerp(density[base + VERTICAL_SAMPLES + y], density[base + VERTICAL_SAMPLES + y + 1], fy);
                            double d10 = lerp(density[nextX + y], density[nextX + y + 1], fy);
                            double d11 = lerp(density[nextX + VERTICAL_SAMPLES + y], density[nextX + VERTICAL_SAMPLES + y + 1], fy);
                            for (int dx = 0; dx < HORIZONTAL_STEP; dx++) {
                                double fx = (double)dx / HORIZONTAL_STEP;
                                double near = lerp(d00, d10, fx);
                                double far = lerp(d01, d11, fx);
                                int localX = x * HORIZONTAL_STEP + dx;
                                int blockY = y * VERTICAL_STEP + dy;
                                for (int dz = 0; dz < HORIZONTAL_STEP; dz++) {
                                    int localZ = z * HORIZONTAL_STEP + dz;
                                    int index = (localX * CHUNK_SIZE + localZ) * WORLD_HEIGHT + blockY;
                                    blocks[index] = lerp(near, far, (double)dz / HORIZONTAL_STEP) > 0.0
                                            ? terrainBlock : 0;
                                }
                            }
                        }
                    }
                }
            }
        }

        // the returned buffer is reused by the next sample or generation call.
        public double[] sampleDensity(int chunkX, int chunkZ, TerrainProfile[] profiles) {
            if (profiles.length != BIOME_SAMPLES * BIOME_SAMPLES) {
                throw new IllegalArgumentException("expected a padded 9x9 Underworld terrain profile grid");
            }
            int sampleX = chunkX * (HORIZONTAL_SAMPLES - 1);
            int sampleZ = chunkZ * (HORIZONTAL_SAMPLES - 1);
            double scale = 684.412;
            lowerValues = lowerNoise.generateNoiseOctaves(lowerValues, sampleX, 0, sampleZ,
                    HORIZONTAL_SAMPLES, VERTICAL_SAMPLES, HORIZONTAL_SAMPLES, scale, scale, scale);
            upperValues = upperNoise.generateNoiseOctaves(upperValues, sampleX, 0, sampleZ,
                    HORIZONTAL_SAMPLES, VERTICAL_SAMPLES, HORIZONTAL_SAMPLES, scale, scale, scale);
            selectorValues = selectorNoise.generateNoiseOctaves(selectorValues, sampleX, 0, sampleZ,
                    HORIZONTAL_SAMPLES, VERTICAL_SAMPLES, HORIZONTAL_SAMPLES,
                    scale / 80.0, scale / 160.0, scale / 80.0);
            // six octaves have a combined amplitude of 63; use block-scale relief rather than a 128-height offset.
            reliefValues = reliefNoise.generateNoiseOctaves(reliefValues, sampleX, sampleZ,
                    HORIZONTAL_SAMPLES, HORIZONTAL_SAMPLES, 1.0, 1.0, 0.5);

            for (int x = 0; x < HORIZONTAL_SAMPLES; x++) {
                for (int z = 0; z < HORIZONTAL_SAMPLES; z++) {
                    double baseHeight = 0.0;
                    double relief = 0.0;
                    double roughness = 0.0;
                    double totalWeight = 0.0;
                    for (int dz = -BIOME_BLEND_RADIUS; dz <= BIOME_BLEND_RADIUS; dz++) {
                        for (int dx = -BIOME_BLEND_RADIUS; dx <= BIOME_BLEND_RADIUS; dx++) {
                            int profileIndex = x + BIOME_BLEND_RADIUS + dx
                                    + (z + BIOME_BLEND_RADIUS + dz) * BIOME_SAMPLES;
                            TerrainProfile profile = profiles[profileIndex];
                            double weight = blendWeights[dx + BIOME_BLEND_RADIUS + (dz + BIOME_BLEND_RADIUS) * 5];
                            baseHeight += profile.baseHeight * weight;
                            relief += profile.relief * weight;
                            roughness += profile.roughness * weight;
                            totalWeight += weight;
                        }
                    }
                    baseHeight /= totalWeight;
                    relief /= totalWeight;
                    roughness /= totalWeight;
                    double height = baseHeight + clamp(reliefValues[x * HORIZONTAL_SAMPLES + z] / 63.0, -1.0, 1.0) * relief;
                    int column = (x * HORIZONTAL_SAMPLES + z) * VERTICAL_SAMPLES;
                    for (int y = 0; y < VERTICAL_SAMPLES; y++) {
                        int index = column + y;
                        double selector = clamp(selectorValues[index] / 128.0 * 0.5 + 0.5, 0.0, 1.0);
                        double detail = clamp(lerp(lowerValues[index], upperValues[index], selector) / 32768.0, -1.0, 1.0);
                        int blockY = y * VERTICAL_STEP;
                        double value = height - blockY + detail * roughness;
                        double topFade = clamp((blockY - (WORLD_HEIGHT - 24.0)) / 24.0, 0.0, 1.0);
                        density[index] = lerp(value, -64.0, topFade);
                    }
                }
            }
            return density;
        }

        private static double lerp(double from, double to, double fraction) {
            return from + (to - from) * fraction;
        }

        // sampleDensity must be called for the containing chunk before reading a local column.
        public int getSurfaceHeight(int localX, int localZ) {
            if (localX < 0 || localX >= CHUNK_SIZE || localZ < 0 || localZ >= CHUNK_SIZE) {
                throw new IllegalArgumentException("surface column must be inside its sampled chunk");
            }
            int column = (localX / HORIZONTAL_STEP * HORIZONTAL_SAMPLES + localZ / HORIZONTAL_STEP) * VERTICAL_SAMPLES;
            int nextX = column + HORIZONTAL_SAMPLES * VERTICAL_SAMPLES;
            double fx = (double)(localX % HORIZONTAL_STEP) / HORIZONTAL_STEP;
            double fz = (double)(localZ % HORIZONTAL_STEP) / HORIZONTAL_STEP;
            for (int y = WORLD_HEIGHT - 1; y >= 0; y--) {
                int sampleY = y / VERTICAL_STEP;
                double fy = (double)(y % VERTICAL_STEP) / VERTICAL_STEP;
                double near = lerp(lerp(density[column + sampleY], density[column + sampleY + 1], fy),
                        lerp(density[nextX + sampleY], density[nextX + sampleY + 1], fy), fx);
                double far = lerp(lerp(density[column + VERTICAL_SAMPLES + sampleY], density[column + VERTICAL_SAMPLES + sampleY + 1], fy),
                        lerp(density[nextX + VERTICAL_SAMPLES + sampleY], density[nextX + VERTICAL_SAMPLES + sampleY + 1], fy), fx);
                if (lerp(near, far, fz) > 0.0) return y;
            }
            return 0;
        }

        private static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }
    }
}
