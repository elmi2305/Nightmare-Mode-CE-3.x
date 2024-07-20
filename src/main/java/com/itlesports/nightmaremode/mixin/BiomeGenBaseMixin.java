package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.EntityFireCreeper;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.EntityEnderman;
import net.minecraft.src.SpawnListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(BiomeGenBase.class)
public class BiomeGenBaseMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addSpecialCreaturesToOverworld(int par1, CallbackInfo ci){
        BiomeGenBase thisObj = (BiomeGenBase)(Object)this;
        thisObj.spawnableMonsterList.add(new SpawnListEntry(EntityFireCreeper.class, 15, 1, 4));
    }
}
