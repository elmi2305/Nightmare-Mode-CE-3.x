package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.BiomeGenBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BiomeGenBase.class)
public interface BiomeGenBaseAccessor {
    @Accessor("spawnableMonsterList")
    List getSpawnableMonsterList();
    @Accessor("spawnableWaterCreatureList")
    List getSpawnableWaterCreatureList();
}
