package com.itlesports.nightmaremode.mixin.biomegen;

import net.minecraft.src.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(BiomeGenBase.class)
public interface BiomeGenBaseAccessor {
    @Accessor("spawnableMonsterList")
    List nightmareMode$getSpawnableMonsterList();
    @Accessor("spawnableWaterCreatureList")
    List nightmareMode$getSpawnableWaterCreatureList();
    @Invoker("setMinMaxHeight")
    BiomeGenBase invokeSetMinMaxHeight(float par1, float par2);
    @Invoker("setDisableRain")
    BiomeGenBase invokeSetDisableRain();
}
