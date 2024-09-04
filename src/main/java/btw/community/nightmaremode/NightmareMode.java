package btw.community.nightmaremode;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.world.biome.BiomeDecoratorBase;
import btw.world.util.WorldUtils;
import com.itlesports.nightmaremode.EntityFireCreeper;
import net.minecraft.src.*;

import java.util.Random;

public class NightmareMode extends BTWAddon {
    public WorldGenerator lavaPillowGenThirdStrata;
    public WorldGenerator silverfishGenFirstStrata;
    public WorldGenerator silverfishGenSecondStrata;
    public WorldGenerator silverfishGenThirdStrata;

    public NightmareMode(){
        super();
    }
    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        addMobBiomeSpawn();
        this.lavaPillowGenThirdStrata = new WorldGenMinable(BTWBlocks.lavaPillow.blockID, 10);
        this.silverfishGenFirstStrata = new WorldGenMinable(BTWBlocks.infestedStone.blockID, 8);
        this.silverfishGenSecondStrata = new WorldGenMinable(BTWBlocks.infestedMidStrataStone.blockID, 8);
        this.silverfishGenThirdStrata = new WorldGenMinable(BTWBlocks.infestedDeepStrataStone.blockID, 16);
    }
    @Override
    public void decorateWorld(BiomeDecoratorBase decorator, World world, Random rand, int x, int z, BiomeGenBase biome) {
        for(int var5 = 0; var5 < 24; ++var5) {
            int var6 = x + rand.nextInt(16);
            int var7 = rand.nextInt(20)+5;
            int var8 = z + rand.nextInt(16);
            this.lavaPillowGenThirdStrata.generate(world, rand, var6, var7, var8);
        }
        for(int var5 = 0; var5 < 8; ++var5) {
            int var6 = x + rand.nextInt(16);
            int var7 = rand.nextInt(30)+50;
            int var8 = z + rand.nextInt(16);
            this.silverfishGenFirstStrata.generate(world, rand, var6, var7, var8);
        }
        for(int var5 = 0; var5 < 8; ++var5) {
            int var6 = x + rand.nextInt(16);
            int var7 = rand.nextInt(26)+24;
            int var8 = z + rand.nextInt(16);
            this.silverfishGenSecondStrata.generate(world, rand, var6, var7, var8);
        }
        for(int var5 = 0; var5 < 8; ++var5) {
            int var6 = x + rand.nextInt(16);
            int var7 = rand.nextInt(23)+1;
            int var8 = z + rand.nextInt(16);
            this.silverfishGenThirdStrata.generate(world, rand, var6, var7, var8);
        }
    }

    private void addMobBiomeSpawn() {
        // adds fire creepers
        BiomeGenBase.desert.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 6, 1, 2));
        BiomeGenBase.extremeHills.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        BiomeGenBase.forest.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        BiomeGenBase.desertHills.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 6, 1, 2));
        BiomeGenBase.extremeHillsEdge.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        BiomeGenBase.forestHills.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        BiomeGenBase.hell.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 40, 1, 1));
        BiomeGenBase.plains.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        BiomeGenBase.jungle.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        BiomeGenBase.swampland.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        BiomeGenBase.beach.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        BiomeGenBase.icePlains.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 1, 1, 2));
        BiomeGenBase.taiga.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 2, 1, 2));
        BiomeGenBase.taigaHills.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityFireCreeper.class, 2, 1, 2));

        // adds creepers to the end and nether
        BiomeGenEnd.sky.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityCreeper.class,1,1,1));
        BiomeGenHell.hell.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityCreeper.class,20,1,2));

        if(WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()){
            // adds witches post hardmode
            BiomeGenBase.plains.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.forest.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.forestHills.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.extremeHills.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.desert.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.desertHills.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.icePlains.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.iceMountains.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.jungle.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.beach.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.taiga.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
            BiomeGenBase.taigaHills.getSpawnableList(EnumCreatureType.monster).add(new SpawnListEntry(EntityWitch.class, 1, 1, 1));
        }
    }
}
