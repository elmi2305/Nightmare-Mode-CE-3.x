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
    @Accessor("spawnableCreatureList")
    List nightmareMode$getSpawnableCreatureList();
    @Accessor("spawnableWaterCreatureList")
    List nightmareMode$getSpawnableWaterCreatureList();
    @Accessor("spawnableCaveCreatureList")
    List nightmareMode$getSpawnableCaveCreatureList();
    @Invoker("setMinMaxHeight")
    BiomeGenBase invokeSetMinMaxHeight(float par1, float par2);
    @Invoker("setDisableRain")
    BiomeGenBase invokeSetDisableRain();
    @Invoker("setTemperatureRainfall")
    BiomeGenBase invokeSetTemperatureRainfall(float temperature, float rainfall);
    @Invoker("setColor")
    BiomeGenBase invokeSetColor(int color);
    @Invoker("setEnableSnow")
    BiomeGenBase invokeSetEnableSnow();
}
