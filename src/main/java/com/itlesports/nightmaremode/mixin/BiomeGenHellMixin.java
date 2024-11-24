package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.EntityFireCreeper;
import net.minecraft.src.BiomeGenHell;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.SpawnListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenHell.class)
public class BiomeGenHellMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCreepersToNetherSpawn(int par1, CallbackInfo ci){
        ((BiomeGenHell)(Object)this).spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 15,1,1));
        ((BiomeGenHell)(Object)this).spawnableMonsterList.add(new SpawnListEntry(EntityFireCreeper.class, 40,1,1));
    }
}
