package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.entity.EntityCustomSkeleton;
import com.itlesports.nightmaremode.entity.EntityFireCreeper;
import com.itlesports.nightmaremode.entity.EntityShadowZombie;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenHell.class)
public abstract class BiomeGenHellMixin implements BiomeGenBaseAccessor{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCreepersToNetherSpawn(int par1, CallbackInfo ci){
        this.getSpawnableMonsterList().add(new SpawnListEntry(EntityCreeper.class, 15,1,1));
        this.getSpawnableMonsterList().add(new SpawnListEntry(EntityFireCreeper.class, 40,1,1));
        this.getSpawnableMonsterList().add(new SpawnListEntry(EntityCustomSkeleton.class, 30,1,3));
        this.getSpawnableMonsterList().add(new SpawnListEntry(EntityShadowZombie.class, 20,1,1));
        if(NightmareMode.magicMonsters){
            this.getSpawnableMonsterList().clear();
            this.getSpawnableMonsterList().add(new SpawnListEntry(EntityWitch.class, 40, 1, 2));
        }
    }
}
