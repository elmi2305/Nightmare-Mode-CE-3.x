package com.itlesports.nightmaremode.mixin.biomegen;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(BiomeGenBase.class)
public class BiomeGenBaseMixin {
    // do not mixin into getSpawnableList or any method like that. it runs once per chunk load. you will create bugs that are basically undetectable :D
}
