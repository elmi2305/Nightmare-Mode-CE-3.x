package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.entity.*;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BiomeGenBase.class)
public class BiomeGenBaseMixin implements BiomeGenBaseAccessor {
    @Shadow @Final public static BiomeGenBase river;

    @Shadow protected List spawnableMonsterList;

    @Shadow protected List spawnableWaterCreatureList;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addMobBiomeSpawn(int par1, CallbackInfo ci) {
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityGhast.class, 1, 1, 1));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityFireSpider.class, 2, 1, 2));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityStoneZombie.class, 1, 1, 2));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityObsidianCreeper.class, 8, 1, 2));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntitySuperchargedCreeper.class, 2, 1, 2));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityBlackWidowSpider.class, 2, 1, 2));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityRadioactiveEnderman.class, 1, 1, 1));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityDungCreeper.class, 10, 1, 1));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityLightningCreeper.class, 1, 1, 1));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityBloodZombie.class, 2, 1, 1));
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityFauxVillager.class, 1, 1, 1));
    }

    static{
        WorldGenReed.addBiomeToGenerator(river);
    }

    @Override
    public List nightmareMode$getSpawnableMonsterList() {
        return this.spawnableMonsterList;
    }

    @Override
    public List nightmareMode$getSpawnableWaterCreatureList() {
        return this.spawnableWaterCreatureList;
    }
}
