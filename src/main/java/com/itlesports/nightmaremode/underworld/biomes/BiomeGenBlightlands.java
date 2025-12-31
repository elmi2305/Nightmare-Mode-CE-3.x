package com.itlesports.nightmaremode.underworld.biomes;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.EntityHorse;
import net.minecraft.src.SpawnListEntry;

public class BiomeGenBlightlands extends BiomeGenUnderworld {

    public BiomeGenBlightlands(int par1) {
        super(par1);
        this.theBiomeDecorator.generateLakes = true;
        this.theBiomeDecorator.treesPerChunk = 1; // doesn't generate due to undergrass not supporting trees
        this.theBiomeDecorator.flowersPerChunk = 2;
        this.theBiomeDecorator.grassPerChunk = 3;
        this.topBlock = (short) NMBlocks.underGrass.blockID;
        this.fillerBlock = (short) NMBlocks.underDirt.blockID;

        this.spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 5, 2, 6));

    }


    @Override
    public int getBiomeGrassColor() {
        return 0x383332;
    }
}
