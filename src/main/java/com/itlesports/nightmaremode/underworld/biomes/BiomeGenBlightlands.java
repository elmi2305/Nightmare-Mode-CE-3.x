package com.itlesports.nightmaremode.underworld.biomes;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.mixin.interfaces.BiomeDecoratorAccess;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.EntityHorse;
import net.minecraft.src.SpawnListEntry;

public class BiomeGenBlightlands extends BiomeGenUnderworld {

    public BiomeGenBlightlands(int par1) {
        super(par1);
        this.theBiomeDecorator.generateLakes = true;
        ((BiomeDecoratorAccess)this.theBiomeDecorator).setTreesPerChunk(1); // doesn't generate due to undergrass not supporting regular trees TODO
        ((BiomeDecoratorAccess)this.theBiomeDecorator).setFlowersPerChunk(2);
        ((BiomeDecoratorAccess)this.theBiomeDecorator).setGrassPerChunk(3);
        this.topBlock = (short) NMBlocks.underGrass.blockID;
        this.fillerBlock = (short) NMBlocks.underFlowerDirts.blockID;
        this.topBlockMetadata = (short) NMBlocks.META_FLOWER_GRASS;
        this.fillerBlockMetadata = (short) NMBlocks.META_FLOWER_DIRT;

        this.spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 5, 2, 6));

    }


    @Override
    public int getBiomeGrassColor() {
        return 0x383332;
    }
}
