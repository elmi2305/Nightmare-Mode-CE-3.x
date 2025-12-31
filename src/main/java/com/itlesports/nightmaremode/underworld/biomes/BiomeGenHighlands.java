package com.itlesports.nightmaremode.underworld.biomes;

import btw.entity.mob.BTWCaveSpiderEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.*;

import java.util.Random;

public class BiomeGenHighlands extends BiomeGenUnderworld {
    public BiomeGenHighlands(int par1) {
        super(par1);
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();

        this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 4, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityPigZombie.class, 2, 4, 4));

        this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(BTWCaveSpiderEntity.class, 6, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 2, 1, 4));


        this.topBlock = (short) NMBlocks.understoneSmooth.blockID;
        this.fillerBlock = (short)NMBlocks.underCobble.blockID;
        this.theBiomeDecorator.treesPerChunk = 1;
        this.theBiomeDecorator.deadBushPerChunk = 2;
        this.theBiomeDecorator.reedsPerChunk = 50;
        this.theBiomeDecorator.cactiPerChunk = 10;
    }

    public void decorate(World par1World, Random par2Random, int par3, int par4) {
        super.decorate(par1World, par2Random, par3, par4);
        if (par2Random.nextInt(1000) == 0) {
            int var5 = par3 + par2Random.nextInt(16) + 8;
            int var6 = par4 + par2Random.nextInt(16) + 8;
            WorldGenDesertWells var7 = new WorldGenDesertWells();
            var7.generate(par1World, par2Random, var5, par1World.getHeightValue(var5, var6) + 1, var6);
        }

    }

    public boolean canLightningStrikeInBiome() {
        return true;
    }
}
