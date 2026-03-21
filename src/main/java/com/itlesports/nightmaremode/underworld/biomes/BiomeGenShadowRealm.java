package com.itlesports.nightmaremode.underworld.biomes;

import btw.entity.mob.BTWCaveSpiderEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.EntityShadowZombie;
import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import net.minecraft.src.*;

public class BiomeGenShadowRealm extends BiomeGenUnderworld {
    public BiomeGenShadowRealm(int par1) {
        super(par1);
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();

        this.spawnableMonsterList.add(new SpawnListEntry(EntityShadowZombie.class, 15, 2, 6));

        this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 1, 1, 1));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 5, 1, 4));


        this.topBlock = (short) NMBlocks.blockAsphalt.blockID;
        this.fillerBlock = (short)NMBlocks.blockRoad.blockID;

    }
}
