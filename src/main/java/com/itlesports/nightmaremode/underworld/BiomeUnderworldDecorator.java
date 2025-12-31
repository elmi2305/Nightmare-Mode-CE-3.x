package com.itlesports.nightmaremode.underworld;

import api.AddonHandler;
import api.util.ForkableRandom;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenFlowerFields;
import com.itlesports.nightmaremode.underworld.worldgen.WorldGenBigFlower;
import com.itlesports.nightmaremode.underworld.worldgen.WorldGenTallFlowers;
import net.minecraft.src.*;

public class BiomeUnderworldDecorator extends BiomeDecorator {
    protected WorldGenerator tallPlantGen;
    protected WorldGenerator tallFlowerTreeGen;

    public BiomeUnderworldDecorator(BiomeGenBase par1BiomeGenBase) {
        super(par1BiomeGenBase);
        this.tallPlantGen = new WorldGenTallFlowers(NMBlocks.yellowFlowerRoots.blockID, 5);
        this.tallFlowerTreeGen = new WorldGenBigFlower();
    }


    @Override
    protected void decorate() {
        int var4;
        int var3;
        int var2;
        int var1;
        this.generateOres();
        for (var1 = 0; var1 < this.sandPerChunk2; ++var1) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.sandGen.generate(this.currentWorld, this.randomGenerator, var2, this.currentWorld.getTopSolidOrLiquidBlock(var2, var3), var3);
        }
        for (var1 = 0; var1 < this.clayPerChunk; ++var1) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.clayGen.generate(this.currentWorld, this.randomGenerator, var2, this.currentWorld.getTopSolidOrLiquidBlock(var2, var3), var3);
        }
        for (var1 = 0; var1 < this.sandPerChunk; ++var1) {
            var2 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var3 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.sandGen.generate(this.currentWorld, this.randomGenerator, var2, this.currentWorld.getTopSolidOrLiquidBlock(var2, var3), var3);
        }

        var1 = this.treesPerChunk;
        if (this.randomGenerator.nextInt(10) == 0) {
            ++var1;
        }

        for (var2 = 0; var2 < var1; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            WorldGenerator var5 = tallFlowerTreeGen;
            var5.setScale(1.0, 1.0, 1.0);
            var5.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getHeightValue(var3, var4), var4);
        }

        for (var2 = 0; var2 < this.bigMushroomsPerChunk; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator, var3, this.currentWorld.getHeightValue(var3, var4), var4);
        }



        for (var2 = 0; var2 < this.flowersPerChunk; ++var2) {
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(128);
            int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.plantYellowGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
            if (!(this.biome instanceof BiomeGenFlowerFields)) continue;
            if (this.randomGenerator.nextInt(4) != 0 || !(this.biome instanceof BiomeGenFlowerFields)) continue;
            var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
            var4 = this.randomGenerator.nextInt(10) + 60;
            var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
            this.tallPlantGen.generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
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
            new WorldGenDeadBush(Block.deadBush.blockID).generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
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
            new WorldGenPumpkin().generate(this.currentWorld, this.randomGenerator, var2, var3, var4);
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
                new WorldGenLiquids(Block.waterMoving.blockID).generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
            }
            for (var2 = 0; var2 < 20; ++var2) {
                var3 = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                var4 = this.randomGenerator.nextInt(this.randomGenerator.nextInt(this.randomGenerator.nextInt(112) + 8) + 8);
                int var7 = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                new WorldGenLiquids(Block.lavaMoving.blockID).generate(this.currentWorld, this.randomGenerator, var3, var4, var7);
            }
        }



        ForkableRandom forkedRand = ForkableRandom.forkRandom(this.randomGenerator);
        AddonHandler.decorateWorld(this, this.currentWorld, forkedRand, this.chunk_X, this.chunk_Z, this.biome);
    }
}
