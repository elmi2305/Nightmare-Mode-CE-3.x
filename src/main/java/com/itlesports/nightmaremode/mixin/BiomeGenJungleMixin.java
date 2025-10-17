package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.BiomeGenJungle;
import net.minecraft.src.EntityWitch;
import net.minecraft.src.SpawnListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenJungle.class)
public abstract class BiomeGenJungleMixin implements BiomeGenBaseAccessor{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void magicMonsters(int par1, CallbackInfo ci){
        if(NightmareMode.magicMonsters){
            this.nightmareMode$getSpawnableMonsterList().clear();
            this.nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(EntityWitch.class, 2, 1, 1));
        }
    }
}
