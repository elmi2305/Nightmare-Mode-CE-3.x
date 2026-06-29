package com.itlesports.nightmaremode.underworld;

import com.itlesports.nightmaremode.mixin.biomegen.BiomeGenBaseAccessor;
import com.itlesports.nightmaremode.underworld.biomes.*;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BiomeGenUnderworld extends BiomeGenBase {
    // biome specific information
    /** Default: 1.0f
     * <br>
     * Higher than 1.0 means the player loses sanity in this biome
     * <br>
     * Less than 1.0 means the player becomes enlightened in this biome
     **/
    private float drainMultiplier = 1.0f;

    // the min/max
    private static final float BLIGHT_MIN = 0.5f;
    private static final float BLIGHT_MAX = 0.6f;

    private static final float HIGH_MIN = 1.0f;
    private static final float HIGH_MAX = 1.5f;

    private static final float FLOWER_MIN = 0.15f;
    private static final float FLOWER_MAX = 0.1501f;

    private static final float VOID_MIN = 0.25f;
    private static final float VOID_MAX = 0.2501f;

    private static final float DEFAULT_MIN = 0.5f;
    private static final float DEFAULT_MAX = 0.6f;

//    private static final float BLIGHT_MIN = 0.1f;
//    private static final float BLIGHT_MAX = 0.3f;

//    private static final float HIGH_MIN = 0.5f;
//    private static final float HIGH_MAX = 1.2f;

//    private static final float FLOWER_MIN = 0.05f;
//    private static final float FLOWER_MAX = 0.06f;
//
//    private static final float VOID_MIN = 0.25f;
//    private static final float VOID_MAX = 0.2501f;


    public static final BiomeGenUnderworld blightlands = (BiomeGenUnderworld) ((BiomeGenBaseAccessor)                           new BiomeGenBlightlands(24) .setDrainMultiplier(1.0f).setBiomeName("UnderworldPlains"))         .invokeSetMinMaxHeight(BLIGHT_MIN, BLIGHT_MAX);
    public static final BiomeGenUnderworld highlands = (BiomeGenUnderworld) ((BiomeGenBaseAccessor)((BiomeGenBaseAccessor)      new BiomeGenHighlands(25)   .setDrainMultiplier(1.1f).setBiomeName("UnderworldDesert"))         .invokeSetMinMaxHeight(HIGH_MIN, HIGH_MAX)).invokeSetDisableRain();
    public static final BiomeGenUnderworld flowerFields = (BiomeGenUnderworld) ((BiomeGenBaseAccessor)                          new BiomeGenFlowerFields(26).setDrainMultiplier(0.3f).setBiomeName("UnderworldFlowerFields"))   .invokeSetMinMaxHeight(FLOWER_MIN, FLOWER_MAX);
    public static final BiomeGenUnderworld shadowRealm = (BiomeGenUnderworld) ((BiomeGenBaseAccessor)((BiomeGenBaseAccessor)    new BiomeGenShadowRealm(27) .setDrainMultiplier(2.3f).setBiomeName("UnderworldVoid"))           .invokeSetMinMaxHeight(VOID_MIN, VOID_MAX)).invokeSetDisableRain();
    public static final BiomeGenUnderworld underHell = (BiomeGenUnderworld) ((BiomeGenBaseAccessor)                             new BiomeGenUnderHell(28)   .setDrainMultiplier(1.5f).setBiomeName("UnderworldHell"))           .invokeSetMinMaxHeight(DEFAULT_MIN, DEFAULT_MAX);

    public static List biomelist = new ArrayList<>();
    static{
        biomelist.add(blightlands);
        biomelist.add(highlands);
        biomelist.add(flowerFields);
        biomelist.add(shadowRealm);
        biomelist.add(underHell);
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

    /**
     * Higher than 1.0 means the player loses sanity in this biome
     * <br>
     * Less than 1.0 means the player becomes enlightened in this biome
     **/
    public BiomeGenUnderworld setDrainMultiplier(float drainMultiplier) {
        this.drainMultiplier = drainMultiplier;
        return this;
    }
}
