package com.itlesports.nightmaremode.underworld;

import api.AddonHandler;
import api.util.ForkableRandom;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenFlowerFields;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenUnderHell;
import com.itlesports.nightmaremode.underworld.worldgen.WorldGenTulip;
import com.itlesports.nightmaremode.underworld.worldgen.WorldGenTallBulbFlower;
import com.itlesports.nightmaremode.underworld.worldgen.WorldGenTallFlowers;
import net.minecraft.src.*;

public class BiomeUnderworldDecorator extends BiomeDecorator {
    protected WorldGenerator tallPlantGen;
    protected WorldGenerator lavaPlantGen;
    protected WorldGenerator tallFlowerTulipGen;
    protected WorldGenerator tallFlowerBulbGen;
    protected WorldGenerator tallFlowerDroopingGen;

    private final WorldGenerator deadBushGen;
    private final WorldGenerator waterLiquidGen;
    private final WorldGenerator lavaLiquidGen;
    private final WorldGenerator pumpkinGen;

    public BiomeUnderworldDecorator(BiomeGenBase par1BiomeGenBase) {
        super(par1BiomeGenBase);
        this.tallPlantGen       = new WorldGenTallFlowers(NMBlocks.yellowFlowerRoots.blockID, 5, false);
        this.lavaPlantGen       = new WorldGenTallFlowers(NMBlocks.yellowFlowerRoots.blockID, 5, true);
        this.tallFlowerTulipGen = new WorldGenTulip();
        this.tallFlowerBulbGen  = new WorldGenTallBulbFlower();
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
//            numPerChunk = this.treesPerChunk + (this.randomGenerator.nextInt(3) == 0 ? 1 : 0);
            numPerChunk = this.randomGenerator.nextInt(3) == 0 ? 0 : 1;
            WorldGenerator treeGen = this.getTreeGenForBiome();

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
        for (var2 = 0; var2 < this.flowersPerChunk; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(128) + 40;
            int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.plantYellowGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);

            if (!isFlowerFields || this.randomGenerator.nextInt(4) != 0) continue;
            // do flower generation twice for flower fields. might split this into its own method

            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(40) + 40;
            var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.tallPlantGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
        }

        boolean isHellWorld = this.biome instanceof BiomeGenUnderHell;

        if (isHellWorld) {
            for (var2 = 0; var2 < this.flowersPerChunk; ++var2) {
                var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                var4 = this.randomGenerator.nextInt(128);
                int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                this.tallPlantGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
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

    private WorldGenerator getTreeGenForBiome(){
        if(this.biome instanceof BiomeGenFlowerFields){
            if(this.randomGenerator.nextFloat() < 0.25){
                return tallFlowerTulipGen;
            }
            return tallFlowerBulbGen;
        }
        return null;
    }
}