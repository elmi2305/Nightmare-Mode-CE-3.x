package com.itlesports.nightmaremode.underworld.biomes;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.EntityHorse;
import net.minecraft.src.SpawnListEntry;

public class BiomeGenFlowerFields extends BiomeGenUnderworld {
    public BiomeGenFlowerFields(int par1) {
        super(par1);
        this.theBiomeDecorator.generateLakes = false;
        this.theBiomeDecorator.flowersPerChunk = 8;
        this.theBiomeDecorator.grassPerChunk = 2;
        this.theBiomeDecorator.treesPerChunk = 0; // flower gen handled in BiomeUnderworldDecorator
        this.topBlock = (short) NMBlocks.underFlowerDirts.blockID;
        this.fillerBlock = (short) NMBlocks.underFlowerDirts.blockID;

        this.topBlockMetadata = (short) NMBlocks.META_FLOWER_GRASS;
        this.fillerBlockMetadata = (short) NMBlocks.META_FLOWER_DIRT;

        this.spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 5, 2, 6));

    }

    @Override
    public int getBiomeGrassColor() {
        return 0xffeb14;
    }

    @Override
    public int getBiomeFoliageColor() {
        return 0xffe900;
    }

}
