package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.BiomeGenEnd;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.EntityWitch;
import net.minecraft.src.SpawnListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenEnd.class)
public abstract class BiomeGenEndMixin implements BiomeGenBaseAccessor{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCreepersToEndSpawnTable(int par1, CallbackInfo ci){
        this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityCreeper.class, 1, 1, 3));
        if(NightmareMode.magicMonsters){
            this.nightmareMode$getSpawnableMonsterList().clear();
            this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityWitch.class, 4, 1, 2));
        }
    }
}
