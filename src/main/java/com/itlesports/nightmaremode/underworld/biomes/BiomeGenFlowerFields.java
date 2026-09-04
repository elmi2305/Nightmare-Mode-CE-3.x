package com.itlesports.nightmaremode.underworld.biomes;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.EntityMushWorm;
import com.itlesports.nightmaremode.entity.underworld.FlowerCreeper;
import com.itlesports.nightmaremode.entity.underworld.FlowerSkeleton;
import com.itlesports.nightmaremode.entity.underworld.FlowerZombie;
import com.itlesports.nightmaremode.mixin.interfaces.BiomeDecoratorAccess;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.SpawnListEntry;

public class BiomeGenFlowerFields extends BiomeGenUnderworld {
    public BiomeGenFlowerFields(int par1) {
        super(par1);
        this.theBiomeDecorator.generateLakes = false;
        ((BiomeDecoratorAccess)this.theBiomeDecorator).setFlowersPerChunk(8);
        ((BiomeDecoratorAccess)this.theBiomeDecorator).setGrassPerChunk(4);
        ((BiomeDecoratorAccess)this.theBiomeDecorator).setTreesPerChunk(1);
        this.topBlock = (short) NMBlocks.underFlowerDirts.blockID;
        this.fillerBlock = (short) NMBlocks.underFlowerDirts.blockID;

        this.topBlockMetadata = (short) NMBlocks.META_FLOWER_GRASS;
        this.fillerBlockMetadata = (short) NMBlocks.META_FLOWER_DIRT;

        this.spawnableMonsterList.add(new SpawnListEntry(EntityMushWorm.class, 16, 3, 6));
        this.spawnableMonsterList.add(new SpawnListEntry(FlowerZombie.class, 12, 2, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(FlowerSkeleton.class, 10, 2, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(FlowerCreeper.class, 8, 1, 3));
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
