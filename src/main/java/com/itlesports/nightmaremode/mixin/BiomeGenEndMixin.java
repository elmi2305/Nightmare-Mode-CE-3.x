package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.BiomeGenEnd;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.SpawnListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenEnd.class)
public class BiomeGenEndMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCreepersToEndSpawnTable(int par1, CallbackInfo ci){
        ((BiomeGenEnd)(Object)this).spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 1, 1, 1));
    }
}
