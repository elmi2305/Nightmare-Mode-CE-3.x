package com.itlesports.nightmaremode.underworld;

import api.AddonHandler;
import api.util.ForkableRandom;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.BlockTallFlower;
import com.itlesports.nightmaremode.block.blocks.BlockUnderworldOre;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenBlightlands;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenFlowerFields;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenShadowRealm;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenUnderHell;
import com.itlesports.nightmaremode.underworld.worldgen.*;
import net.minecraft.src.*;

public class BiomeUnderworldDecorator extends BiomeDecorator {
    protected WorldGenerator tallPlantGen;
    protected WorldGenerator lavaPlantGen;
    protected WorldGenerator voidPlantGen;
    protected WorldGenerator lucidBloomGen;
    protected WorldGenerator tallFlowerTulipGen;
    protected WorldGenerator tallFlowerBulbGen;
    protected WorldGenerator tallFlowerDroopingGen;
    protected WorldGenerator simpleTreeGen;
    protected WorldGenerator hellTreeGen;
    protected WorldGenerator voidTreeGen;

    private final WorldGenerator deadBushGen;
    private final WorldGenerator waterLiquidGen;
    private final WorldGenerator lavaLiquidGen;
    private final WorldGenerator pumpkinGen;

    public BiomeUnderworldDecorator(BiomeGenBase par1BiomeGenBase) {
        super(par1BiomeGenBase);
        this.tallPlantGen       = new WorldGenTallFlowers(NMBlocks.yellowFlowerRoots.blockID, 5, false);
        this.lavaPlantGen       = new WorldGenTallFlowers(NMBlocks.yellowFlowerRoots.blockID, BlockTallFlower.LAVA_FLOWER, true);
        this.voidPlantGen       = new WorldGenTallFlowers(NMBlocks.yellowFlowerRoots.blockID, BlockTallFlower.VOID_SHRUB, true);
        this.lucidBloomGen      = new WorldGenTallFlowers(NMBlocks.yellowFlowerRoots.blockID, BlockTallFlower.LUCID_BLOOM, true);
        this.tallFlowerTulipGen = new WorldGenTulip(false);
        this.tallFlowerBulbGen  = new WorldGenTallBulbFlower();
        this.simpleTreeGen = new WorldGenSimpleTree(false);
        this.hellTreeGen = new WorldGenHellTree();
        this.voidTreeGen = new WorldGenVoidTree();

//        this.tallFlowerDroopingGen = new WorldGenDroopingFlower();

        this.deadBushGen    = new WorldGenDeadBush(Block.deadBush.blockID);
        this.waterLiquidGen = new WorldGenLiquids(Block.waterMoving.blockID);
        this.lavaLiquidGen  = new WorldGenLiquids(Block.lavaMoving.blockID);
        this.pumpkinGen     = new WorldGenPumpkin();
    }


    @Override
    protected void decorate() {
        int var4;
        int var3;
        int var2;
        int numPerChunk;
        this.generateOres();
        this.generateProgressionOres();
        for (numPerChunk = 0; numPerChunk < this.sandPerChunk2; ++numPerChunk) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.sandGen.generate(this.currentWorld, this.randomGenerator, var2, this.currentWorld.getTopSolidOrLiquidBlock(var2, var3), var3);
        }
        for (numPerChunk = 0; numPerChunk < this.clayPerChunk; ++numPerChunk) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.clayGen.generate(this.currentWorld, this.randomGenerator, var2, this.currentWorld.getTopSolidOrLiquidBlock(var2, var3), var3);
        }
        for (numPerChunk = 0; numPerChunk < this.sandPerChunk; ++numPerChunk) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.sandGen.generate(this.currentWorld, this.randomGenerator, var2, this.currentWorld.getTopSolidOrLiquidBlock(var2, var3), var3);
        }

        // toggle if I don't want
        if (true) {
            numPerChunk = this.treesPerChunk + (this.randomGenerator.nextInt(3) == 0 ? 1 : 0);
            WorldGenerator treeGen = this.getTreeGenForBiome();
//            treeGen = this.hellTreeGen;

            if (treeGen != null) {
                for (var2 = 0; var2 < numPerChunk; ++var2) {
                    var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                    var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                    treeGen.setScale(1.0, 1.0, 1.0);
                    treeGen.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getHeightValue(var3, var4), var4);
                }
            }
        }

        for (var2 = 0; var2 < this.bigMushroomsPerChunk; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getHeightValue(var3, var4), var4);
        }

        boolean isFlowerFields = this.biome instanceof BiomeGenFlowerFields;
        if (isFlowerFields && this.randomGenerator.nextInt(24) == 0) {
            int bloomX = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            int bloomZ = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.lucidBloomGen.generate(this.currentWorld, this.randomGenerator, bloomX,
                    this.currentWorld.getPrecipitationHeight(bloomX, bloomZ), bloomZ);
        }
        for (var2 = 0; var2 < this.flowersPerChunk; ++var2) {
//            if(true) continue;
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(128) + 40;
            int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.plantYellowGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);

            if (!isFlowerFields || this.randomGenerator.nextInt(4) != 0) continue;
            // do flower generation twice for flower fields. might split this into its own method

            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
//            var4 = this.randomGenerator.nextInt(40) + 40;
            var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;

            var4 = this.currentWorld.getPrecipitationHeight(var3,var7);

            this.tallPlantGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
        }

        boolean isHellWorld = this.biome instanceof BiomeGenUnderHell;

        if (isHellWorld) {
            for (var2 = 0; var2 < this.flowersPerChunk; ++var2) {
                var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                var4 = this.randomGenerator.nextInt(128) + 40;
                int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                this.lavaPlantGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
            }
        }
        boolean isVoidWorld = this.biome instanceof BiomeGenShadowRealm || this.biome instanceof BiomeGenBlightlands;

        if (isVoidWorld) {
            for (var2 = 0; var2 < this.flowersPerChunk; ++var2) {
                var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                var4 = this.currentWorld.getPrecipitationHeight(var3,var7);

                this.voidPlantGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
            }
        }

        for (var2 = 0; var2 < this.grassPerChunk; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(128);
            int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            WorldGenerator var6 = this.biome.getRandomWorldGenForGrass(this.randomGenerator);
            var6.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
        }
        for (var2 = 0; var2 < this.deadBushPerChunk; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(128);
            int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.deadBushGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
        }
        for (var2 = 0; var2 < this.waterlilyPerChunk; ++var2) {
            int var7;
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            for (var7 = this.randomGenerator.nextInt(128); var7 > 0 && this.currentWorld.getBlockId(var3, var7 - 1, var4) == 0; --var7) {
            }
            this.waterlilyGen.generate(this.currentWorld, this.randomGenerator, var3, var7, var4);
        }
        for (var2 = 0; var2 < this.mushroomsPerChunk; ++var2) {
            int var7;
            if (this.randomGenerator.nextInt(4) == 0) {
                var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                var7 = this.currentWorld.getHeightValue(var3, var4);
                this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, var3, var7, var4);
            }
            if (this.randomGenerator.nextInt(8) != 0) continue;
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            var7 = this.randomGenerator.nextInt(128);
            this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, var3, var7, var4);
        }
        if (this.randomGenerator.nextInt(4) == 0) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.randomGenerator.nextInt(128);
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, var2, var3, var4);
        }
        if (this.randomGenerator.nextInt(8) == 0) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.randomGenerator.nextInt(128);
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, var2, var3, var4);
        }
        for (var2 = 0; var2 < this.reedsPerChunk; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            int var7 = this.randomGenerator.nextInt(128);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, var3, var7, var4);
        }
        for (var2 = 0; var2 < 10; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(128);
            int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.reedGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
        }
        if (this.randomGenerator.nextInt(32) == 0) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.randomGenerator.nextInt(128);
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.pumpkinGen.generate(this.currentWorld, this.randomGenerator, var2, var3, var4);
        }
        for (var2 = 0; var2 < this.cactiPerChunk; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(128);
            int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.cactusGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
        }
        if (this.generateLakes) {
            for (var2 = 0; var2 < 50; ++var2) {
                var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                var4 = this.randomGenerator.nextInt(this.randomGenerator.nextInt(120) + 8);
                int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                this.waterLiquidGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
            }
            for (var2 = 0; var2 < 20; ++var2) {
                var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                var4 = this.randomGenerator.nextInt(this.randomGenerator.nextInt(this.randomGenerator.nextInt(112) + 8) + 8);
                int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                this.lavaLiquidGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
            }
        }

        ForkableRandom forkedRand = ForkableRandom.forkRandom(this.randomGenerator);
        AddonHandler.decorateWorld(this, this.currentWorld, forkedRand, this.chunk_X, this.chunk_Z, this.biome);
    }

    private void generateProgressionOres() {
        int titaniumVeins = 0;
        int tungstenVeins = 0;
        boolean titaniumBiome = biome instanceof BiomeGenBlightlands || biome instanceof BiomeGenFlowerFields
                || biome instanceof com.itlesports.nightmaremode.underworld.biomes.BiomeGenHighlands;
        if (titaniumBiome) {
            for (int i = 0; i < 5; i++) {
                int x = chunk_X + randomGenerator.nextInt(16);
                int y = 48 + randomGenerator.nextInt(65);
                int z = chunk_Z + randomGenerator.nextInt(16);
                if (new WorldGenUnderworldOre(NMBlocks.underworldOres.blockID, BlockUnderworldOre.TITANIUM,
                        3 + randomGenerator.nextInt(4), NMBlocks.underCobble.blockID).generate(currentWorld, randomGenerator, x, y, z)) {
                    titaniumVeins++;
                }
            }
        }
        boolean tungstenBiome = biome instanceof BiomeGenBlightlands
                || biome instanceof com.itlesports.nightmaremode.underworld.biomes.BiomeGenHighlands;
        if (tungstenBiome) {
            for (int i = 0; i < 2; i++) {
                int x = chunk_X + randomGenerator.nextInt(16);
                int y = 8 + randomGenerator.nextInt(33);
                int z = chunk_Z + randomGenerator.nextInt(16);
                if (new WorldGenUnderworldOre(NMBlocks.underworldOres.blockID, BlockUnderworldOre.TUNGSTEN,
                        2 + randomGenerator.nextInt(3), NMBlocks.underrock.blockID).generate(currentWorld, randomGenerator, x, y, z)) {
                    tungstenVeins++;
                }
            }
        }
        if (NightmareMode.devMode && (titaniumBiome || tungstenBiome)) {
            System.out.println("[Underworld/Ores] chunk=" + (chunk_X >> 4) + "," + (chunk_Z >> 4)
                    + " biome=" + biome.biomeName + " titaniumVeins=" + titaniumVeins
                    + " tungstenVeins=" + tungstenVeins);
        }
    }

    private WorldGenerator getTreeGenForBiome(){
        if(this.biome instanceof BiomeGenFlowerFields){
            if(this.randomGenerator.nextFloat() < 0.75){
                return tallFlowerTulipGen;
            }
            return tallFlowerBulbGen;
        }

        if(this.biome instanceof BiomeGenUnderHell){
            return this.hellTreeGen;
        }
        if(this.biome instanceof BiomeGenShadowRealm){
            return this.voidTreeGen;
        }
        return null;
    }
}
