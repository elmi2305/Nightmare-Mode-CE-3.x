package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.EntityFireCreeper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenBase.class)
public abstract class BiomeGenBaseMixin implements BiomeGenBaseAccessor {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addMobBiomeSpawn(int par1, CallbackInfo ci) {
        this.getSpawnableMonsterList().add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        this.getSpawnableMonsterList().add(new SpawnListEntry(EntityGhast.class, 1, 1, 1));
        if (NightmareMode.magicMonsters) {
            this.getSpawnableMonsterList().clear();
            this.getSpawnableMonsterList().add(new SpawnListEntry(EntityWitch.class, 10, 1, 2));
            this.getSpawnableWaterCreatureList().clear();
        }
    }
}
