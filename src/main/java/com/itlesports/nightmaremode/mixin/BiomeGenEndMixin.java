package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BiomeGenEnd.class)
public class BiomeGenEndMixin extends BiomeGenBase {

    protected BiomeGenEndMixin(int par1) {
        super(par1);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addChargedCreepers(CallbackInfo ci){
        BiomeGenEnd thisObj = (BiomeGenEnd)(Object)this;
        thisObj.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 1, 1, 1));
    }
}
