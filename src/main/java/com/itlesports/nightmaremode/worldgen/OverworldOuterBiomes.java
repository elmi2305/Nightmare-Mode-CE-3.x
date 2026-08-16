package com.itlesports.nightmaremode.worldgen;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.outer.*;
import com.itlesports.nightmaremode.entity.creepers.EntityVoidCreeper;
import com.itlesports.nightmaremode.entity.variants.EntityBlackWidowSpider;
import com.itlesports.nightmaremode.entity.variants.EntityFireSpider;
import com.itlesports.nightmaremode.entity.variants.EntityShadowZombie;
import com.itlesports.nightmaremode.mixin.biomegen.BiomeGenBaseAccessor;
import com.itlesports.nightmaremode.mixin.interfaces.BiomeDecoratorAccess;
import net.minecraft.src.*;

public final class OverworldOuterBiomes {
    public static final BiomeGenBase DEADZONE = new DeadzoneBiome(29);
    public static final BiomeGenBase CRUEL_DESERT = new CruelDesertBiome(30);
    public static final BiomeGenBase GREAT_VOID = new EmptyOuterBiome(31, "Great Void", 0x11131A, 0x15151C);
    public static final BiomeGenBase LOST_OCEAN = new LostOceanBiome(32);
    public static final BiomeGenBase FROZEN_WASTES = new FrozenWastesBiome(33);

    private OverworldOuterBiomes() {}

    public static BiomeGenBase forRegion(OverworldTierHelper.Region region) {
        return switch (region) {
            case DEADZONE -> DEADZONE;
            case CRUEL_DESERT -> CRUEL_DESERT;
            case GREAT_VOID -> GREAT_VOID;
            case LOST_OCEAN -> LOST_OCEAN;
            case FROZEN_WASTES -> FROZEN_WASTES;
            default -> null;
        };
    }

    private static void clearSpawns(BiomeGenBase biome) {
        BiomeGenBaseAccessor access = (BiomeGenBaseAccessor) biome;
        access.nightmareMode$getSpawnableMonsterList().clear();
        access.nightmareMode$getSpawnableCreatureList().clear();
        access.nightmareMode$getSpawnableWaterCreatureList().clear();
        access.nightmareMode$getSpawnableCaveCreatureList().clear();
    }

    private static class DeadzoneBiome extends BiomeGenPlains {
        DeadzoneBiome(int id) {
            super(id);
            this.setBiomeName("Deadzone");
            ((BiomeGenBaseAccessor)this).invokeSetTemperatureRainfall(0.55F, 0.3F);
            ((BiomeDecoratorAccess)this.theBiomeDecorator).setGrassPerChunk(0);
            this.topBlock = (short) NMBlocks.underGrass.blockID;
            this.fillerBlock = (short) NMBlocks.underFlowerDirts.blockID;
            this.fillerBlockMetadata = NMBlocks.META_UNDER_DIRT;
            clearSpawns(this);
            this.spawnableMonsterList.add(new SpawnListEntry(EntityShadowZombie.class, 18, 1, 3));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderSkeleton.class, 12, 1, 3));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityWitherSkeletonOuter.class, 8, 1, 2));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityBlackWidowSpider.class, 12, 1, 3));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityVoidCreeper.class, 8, 1, 2));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityEnderman.class, 10, 1, 3));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 4, 1, 1));
        }
    }

    private static class CruelDesertBiome extends BiomeGenDesert {
        CruelDesertBiome(int id) {
            super(id);
            this.setBiomeName("Cruel Desert");
            BiomeGenBaseAccessor biomeAccess = (BiomeGenBaseAccessor)this;
            biomeAccess.invokeSetTemperatureRainfall(2.0F, 0.0F);
            biomeAccess.invokeSetDisableRain();
            BiomeDecoratorAccess decoratorAccess = (BiomeDecoratorAccess)this.theBiomeDecorator;
            decoratorAccess.setTreesPerChunk(-999);
            decoratorAccess.setDeadBushPerChunk(0);
            decoratorAccess.setReedsPerChunk(0);
            decoratorAccess.setCactiPerChunk(0);
            this.theBiomeDecorator.generateLakes = false;
            clearSpawns(this);
            this.spawnableMonsterList.add(new SpawnListEntry(EntityFireSkeletonOuter.class, 14, 1, 3));
            this.spawnableMonsterList.add(new SpawnListEntry(com.itlesports.nightmaremode.entity.EntityFauxVillager.class, 5, 1, 1));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityFireSpider.class, 12, 1, 3));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityInfernoSkeleton.class, 10, 1, 2));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityMummyZombie.class, 18, 1, 4));
        }

        @Override
        public void decorate(World world, java.util.Random random, int x, int z) {
            // no trees, lakes, plants, or ore-carved cavities in the cruel desert
        }
    }

    private static class EmptyOuterBiome extends BiomeGenBase {
        EmptyOuterBiome(int id, String name, int color, int waterColor) {
            super(id);
            this.setBiomeName(name);
            BiomeGenBaseAccessor biomeAccess = (BiomeGenBaseAccessor)this;
            biomeAccess.invokeSetColor(color);
            biomeAccess.invokeSetDisableRain();
            this.waterColorMultiplier = waterColor;
            clearSpawns(this);
            this.theBiomeDecorator.generateLakes = false;
        }

        @Override
        public void decorate(World world, java.util.Random random, int x, int z) {}
    }

    private static class LostOceanBiome extends BiomeGenOcean {
        LostOceanBiome(int id) {
            super(id);
            this.setBiomeName("Lost Ocean");
            ((BiomeGenBaseAccessor)this).invokeSetTemperatureRainfall(0.75F, 1.0F);
            this.waterColorMultiplier = 0x62FF28;
            this.topBlock = (short) Block.gravel.blockID;
            this.fillerBlock = (short) Block.stone.blockID;
            clearSpawns(this);
            this.spawnableMonsterList.add(new SpawnListEntry(EntityAcidGhast.class, 2, 1, 1));
            this.spawnableWaterCreatureList.add(new SpawnListEntry(EntityAcidSquid.class, 12, 1, 3));
            this.spawnableWaterCreatureList.add(new SpawnListEntry(btw.entity.mob.BTWSquidEntity.class, 12, 1, 4));
            this.theBiomeDecorator.generateLakes = false;
        }

        @Override
        public void decorate(World world, java.util.Random random, int x, int z) {}
    }

    private static class FrozenWastesBiome extends BiomeGenTaiga {
        FrozenWastesBiome(int id) {
            super(id);
            this.setBiomeName("Frozen Wastes");
            BiomeGenBaseAccessor biomeAccess = (BiomeGenBaseAccessor)this;
            biomeAccess.invokeSetEnableSnow();
            biomeAccess.invokeSetTemperatureRainfall(0.0F, 0.7F);
            clearSpawns(this);
            this.spawnableMonsterList.add(new SpawnListEntry(EntityIceSkeletonOuter.class, 20, 2, 5));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityIceZombie.class, 16, 1, 4));
            this.spawnableMonsterList.add(new SpawnListEntry(EntityIceGolem.class, 6, 1, 1));
            this.theBiomeDecorator.generateLakes = false;
        }

        @Override
        public void decorate(World world, java.util.Random random, int x, int z) {}
    }
}
