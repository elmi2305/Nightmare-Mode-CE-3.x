package com.itlesports.nightmaremode.mixin.biomegen;

import net.minecraft.src.BiomeDecorator;
import net.minecraft.src.Block;
import net.minecraft.src.WorldGenFlowers;
import net.minecraft.src.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeDecorator.class)
public class BiomeDecoratorMixin {
    @Shadow protected WorldGenerator mushroomBrownGen;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        // removes brown mushrooms from the overworld
        this.mushroomBrownGen = new WorldGenFlowers(Block.mushroomRed.blockID);
    }
}
