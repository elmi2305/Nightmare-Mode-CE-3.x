package com.itlesports.nightmaremode.underworld;

import net.minecraft.src.*;

public class BiomeGenUnderworld extends BiomeGenBase {


    protected BiomeGenUnderworld(int par1) {
        super(par1);
        this.spawnableMonsterList.clear();
        this.spawnableCreatureList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 4, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityPigZombie.class, 2, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityMagmaCube.class, 1, 4, 4));

        this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 6, 4, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 8, 4, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 4, 4, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 6, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 10, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 10, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 10, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 8, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 4, 4, 4));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 2, 1, 4));


        this.theBiomeDecorator = new BiomeUnderworldDecorator(this);
    }

}
