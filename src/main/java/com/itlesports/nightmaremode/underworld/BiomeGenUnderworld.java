package com.itlesports.nightmaremode.underworld;

import com.itlesports.nightmaremode.underworld.biomes.BiomeGenBlightlands;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenFlowerFields;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenHighlands;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenShadowRealm;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BiomeGenUnderworld extends BiomeGenBase {
    // biome specific information
    private float drainMultiplier = 1.0f; // 1.0f means no biome penalty. higher means you lose sanity and lower means you gain it

    // the min/max
    private static final float BLIGHT_MIN = 0.5f;
    private static final float BLIGHT_MAX = 0.6f;

    private static final float HIGH_MIN = 1.0f;
    private static final float HIGH_MAX = 1.5f;

    private static final float FLOWER_MIN = 0.15f;
    private static final float FLOWER_MAX = 0.1501f;

    private static final float VOID_MIN = 0.25f;
    private static final float VOID_MAX = 0.2501f;

//    private static final float BLIGHT_MIN = 0.1f;
//    private static final float BLIGHT_MAX = 0.3f;

//    private static final float HIGH_MIN = 0.5f;
//    private static final float HIGH_MAX = 1.2f;

//    private static final float FLOWER_MIN = 0.05f;
//    private static final float FLOWER_MAX = 0.06f;
//
//    private static final float VOID_MIN = 0.25f;
//    private static final float VOID_MAX = 0.2501f;


    public static final BiomeGenUnderworld blightlands = (BiomeGenUnderworld) new BiomeGenBlightlands(24).setDrainMultiplier(1.0f).setBiomeName("UnderworldPlains").setMinMaxHeight(BLIGHT_MIN, BLIGHT_MAX);
    public static final BiomeGenUnderworld highlands = (BiomeGenUnderworld) new BiomeGenHighlands(25).setDrainMultiplier(0.9f).setBiomeName("UnderworldDesert").setMinMaxHeight(HIGH_MIN, HIGH_MAX).setDisableRain();
    public static final BiomeGenUnderworld flowerFields = (BiomeGenUnderworld) new BiomeGenFlowerFields(26).setDrainMultiplier(0.3f).setBiomeName("UnderworldFlowerFields").setMinMaxHeight(FLOWER_MIN, FLOWER_MAX);
    public static final BiomeGenUnderworld shadowRealm = (BiomeGenUnderworld) new BiomeGenShadowRealm(27).setDrainMultiplier(1.8f).setBiomeName("UnderworldVoid").setMinMaxHeight(VOID_MIN, VOID_MAX).setDisableRain();

    public static List biomelist = new ArrayList<>();
    static{
        biomelist.add(blightlands);
        biomelist.add(highlands);
        biomelist.add(flowerFields);
        biomelist.add(shadowRealm);
    }
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

//        System.out.println("USING OLD BIOME DECORATOR FOR DEBUG");
//        this.theBiomeDecorator = new BiomeDecorator(this);
        this.theBiomeDecorator = new BiomeUnderworldDecorator(this);
    }



    public float getDrainMultiplier() {
        return drainMultiplier;
    }

    public BiomeGenUnderworld setDrainMultiplier(float drainMultiplier) {
        this.drainMultiplier = drainMultiplier;
        return this;
    }
}
