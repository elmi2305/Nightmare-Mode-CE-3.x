package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.entity.*;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BiomeGenBase.class)
public class BiomeGenBaseMixin implements BiomeGenBaseAccessor {
    @Shadow @Final public static BiomeGenBase river;
    @Shadow protected List spawnableMonsterList;
    @Shadow protected List spawnableWaterCreatureList;

    static{
        WorldGenReed.addBiomeToGenerator(river);
    }

    @Override
    public List nightmareMode$getSpawnableMonsterList() {
        return this.spawnableMonsterList;
    }

    @Override
    public List nightmareMode$getSpawnableWaterCreatureList() {
        return this.spawnableWaterCreatureList;
    }
}
