package com.itlesports.nightmaremode.mixin.biomegen;

import com.itlesports.nightmaremode.worldgen.OverworldOuterBiomes;
import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.GenLayer;
import net.minecraft.src.World;
import net.minecraft.src.WorldChunkManager;
import net.minecraft.src.WorldType;
import com.itlesports.nightmaremode.worldgen.IFHYOverworldGenLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunkManager.class)
public class WorldChunkManagerMixin {
    @Unique private World nightmareMode$world;
    @Shadow private GenLayer genBiomes;
    @Shadow private GenLayer biomeIndexLayer;

    @Inject(method = "<init>(JLnet/minecraft/src/WorldType;)V", at = @At("TAIL"))
    private void installIfhyBiomePipeline(long seed, WorldType worldType, CallbackInfo ci) {
        GenLayer[] layers = IFHYOverworldGenLayer.initializeAllBiomeGenerators(seed, worldType);
        this.genBiomes = layers[0];
        this.biomeIndexLayer = layers[1];
    }

    @Inject(method = "<init>(Lnet/minecraft/src/World;)V", at = @At("TAIL"))
    private void rememberOwningWorld(World world, CallbackInfo ci) {
        this.nightmareMode$world = world;
    }

    @Inject(method = "getBiomeGenAt(II)Lnet/minecraft/src/BiomeGenBase;", at = @At("HEAD"), cancellable = true)
    private void useRadialBiome(int x, int z, CallbackInfoReturnable<BiomeGenBase> cir) {
        BiomeGenBase biome = getOuterBiome(x, z);
        if (biome != null) cir.setReturnValue(biome);
    }

    @Inject(method = "getBiomesForGeneration", at = @At("RETURN"))
    private void replaceGenerationBiomes(BiomeGenBase[] biomes, int startX, int startZ, int width, int height,
                                         CallbackInfoReturnable<BiomeGenBase[]> cir) {
        BiomeGenBase[] result = cir.getReturnValue();
        for (int z = 0; z < height; ++z) {
            for (int x = 0; x < width; ++x) {
                BiomeGenBase biome = getOuterBiome((startX + x) * 4, (startZ + z) * 4);
                if (biome != null) result[x + z * width] = biome;
            }
        }
    }

    @Inject(method = "getBiomeGenAt([Lnet/minecraft/src/BiomeGenBase;IIIIZ)[Lnet/minecraft/src/BiomeGenBase;", at = @At("RETURN"))
    private void replaceBlockBiomes(BiomeGenBase[] biomes, int startX, int startZ, int width, int height, boolean useCache,
                                    CallbackInfoReturnable<BiomeGenBase[]> cir) {
        BiomeGenBase[] result = cir.getReturnValue();
        for (int z = 0; z < height; ++z) {
            for (int x = 0; x < width; ++x) {
                BiomeGenBase biome = getOuterBiome(startX + x, startZ + z);
                if (biome != null) result[x + z * width] = biome;
            }
        }
    }

    @Unique
    private BiomeGenBase getOuterBiome(int x, int z) {
        if (this.nightmareMode$world == null || this.nightmareMode$world.provider.dimensionId != 0) return null;
        return OverworldOuterBiomes.forRegion(OverworldTierHelper.getRegion(this.nightmareMode$world, x, z));
    }
}
