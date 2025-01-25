package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.EntityFireCreeper;
import net.minecraft.src.BiomeGenHell;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.EntityWitch;
import net.minecraft.src.SpawnListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenHell.class)
public class BiomeGenHellMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCreepersToNetherSpawn(int par1, CallbackInfo ci){
        BiomeGenHell thisObj =((BiomeGenHell)(Object)this);
        thisObj.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 15,1,1));
        thisObj.spawnableMonsterList.add(new SpawnListEntry(EntityFireCreeper.class, 40,1,1));
        if(NightmareMode.magicMonsters){
            thisObj.spawnableMonsterList.clear();
            thisObj.spawnableMonsterList.add(new SpawnListEntry(EntityWitch.class, 40, 1, 2));
        }
    }
}
