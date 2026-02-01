package com.itlesports.nightmaremode.mixin.biomegen;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BiomeGenBase.class)
public class BiomeGenBaseMixin implements BiomeGenBaseAccessor {
    @Shadow protected List spawnableMonsterList;
    @Shadow protected List spawnableWaterCreatureList;

    @Override
    public List nightmareMode$getSpawnableMonsterList() {
        return this.spawnableMonsterList;
    }

    @Override
    public List nightmareMode$getSpawnableWaterCreatureList() {
        return this.spawnableWaterCreatureList;
    }
}
