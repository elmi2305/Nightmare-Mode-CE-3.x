package com.itlesports.nightmaremode.mixin.component;

import com.itlesports.nightmaremode.underworld.BiomeGenUnderworld;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.BigMushroom;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(StructureScatteredFeatureStart.class)
public class StructureScatteredFeatureStartMixin extends StructureStart {
    @Inject(method = "<init>(Lnet/minecraft/src/World;Ljava/util/Random;II)V", at = @At("TAIL"))
    private void addCustomFeatures(World world, Random rand, int chunkX, int chunkZ, CallbackInfo ci) {
        BiomeGenBase biome = world.getBiomeGenForCoords(chunkX * 16 + 8, chunkZ * 16 + 8);
        if (biome == BiomeGenUnderworld.flowerFields) {
            this.components.add(new BigMushroom(rand, chunkX * 16, chunkZ * 16));
        }
    }
}
