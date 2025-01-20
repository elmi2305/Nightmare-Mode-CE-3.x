package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.EntityFireCreeper;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.SpawnListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenBase.class)
public class BiomeGenBaseMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addMobBiomeSpawn(int par1, CallbackInfo ci) {
        ((BiomeGenBase)(Object)this).spawnableMonsterList.add(new SpawnListEntry(EntityFireCreeper.class, 4, 1, 2));
        ((BiomeGenBase)(Object)this).spawnableMonsterList.add(new SpawnListEntry(EntityGhast.class, 1, 1, 1));
    }
}
