package com.itlesports.nightmaremode.underworld;

import com.itlesports.nightmaremode.underworld.biomes.BiomeGenBlightlands;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenFlowerFields;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenHighlands;
import net.minecraft.src.*;

public class BiomeGenUnderworld extends BiomeGenBase {

    public static final BiomeGenUnderworld blightlands = (BiomeGenUnderworld) new BiomeGenBlightlands(24).setBiomeName("UnderworldPlains").setMinMaxHeight(1.1F, 1.4F);
    public static final BiomeGenUnderworld highlands = (BiomeGenUnderworld) new BiomeGenHighlands(25).setBiomeName("UnderworldDesert").setMinMaxHeight(1.9F, 2.1F).setDisableRain();
    public static final BiomeGenUnderworld flowerFields = (BiomeGenUnderworld) new BiomeGenFlowerFields(26).setBiomeName("UnderworldFlowerFields").setMinMaxHeight(-0.1F, 0.1F).setDisableRain();


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

//        System.out.println("USING OLD BIOMEDECORATOR FOR DEBUG");
//        this.theBiomeDecorator = new BiomeDecorator(this);
        this.theBiomeDecorator = new BiomeUnderworldDecorator(this);
    }

}
