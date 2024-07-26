package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.EntityFireCreeper;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeGenHell.class)
public class BiomeGenHellMixin extends BiomeGenBase {
    protected BiomeGenHellMixin(int par1) {
        super(par1);
    }

    @Inject(method = "<init>",at = @At("TAIL"))
    private void addCustomEntitiesToNetherSpawn(int par1, CallbackInfo ci){
        BiomeGenHell thisObj = (BiomeGenHell)(Object)this;
        thisObj.spawnableMonsterList.add(new SpawnListEntry(EntityFireCreeper.class, 40, 1, 1));
        thisObj.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 20, 1, 2));
    }
}
