package com.itlesports.nightmaremode.underworld.biomes;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.EntityShadowZombie;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.EntityEnderman;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.SpawnListEntry;

public class BiomeGenUnderHell extends BiomeGenUnderworld {
    public BiomeGenUnderHell(int par1) {
        super(par1);

        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();

        this.spawnableMonsterList.add(new SpawnListEntry(EntityShadowZombie.class, 15, 2, 6));
        // add squids
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 1, 1, 1));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 5, 1, 4));


        this.topBlock = (short) NMBlocks.underStones.blockID;
        this.fillerBlock = (short)NMBlocks.underStones.blockID;

        this.topBlockMetadata = (short) NMBlocks.META_VOID_STONE;
        this.fillerBlockMetadata = (short)NMBlocks.META_VOID_STONE;


    }
}
