package com.itlesports.nightmaremode.mixin.biomegen;

import net.minecraft.src.BiomeDecorator;
import net.minecraft.src.BiomeGenOcean;
import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenFlowers;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.worldgen.WorldGenAquamarineOre;
import net.minecraft.src.WorldGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BiomeDecorator.class)
public class BiomeDecoratorMixin {
    @Shadow protected WorldGenerator mushroomBrownGen;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        // removes brown mushrooms from the overworld
        this.mushroomBrownGen = new WorldGenFlowers(Block.mushroomRed.blockID);
    }

    @Inject(method = "decorate*", at = @At("TAIL"))
    private void generateDeepOceanAquamarine(World world, Random random, int chunkX, int chunkZ, CallbackInfo ci) {
        int x = chunkX + random.nextInt(16) + 8;
        int z = chunkZ + random.nextInt(16) + 8;
        if (!(world.getBiomeGenForCoords(x, z) instanceof BiomeGenOcean)) {
            return;
        }

        int waterY = this.findDeepOceanFloorWaterY(world, x, z);
        if (waterY > 0) {
            new WorldGenAquamarineOre(NMBlocks.aquamarineOre.blockID, 2).generate(world, random, x, waterY, z);
        }
    }

    @Unique private int findDeepOceanFloorWaterY(World world, int x, int z) {
        for (int y = 49; y > 1; --y) {
            if (world.getBlockMaterial(x, y, z) == Material.water
                    && world.getBlockMaterial(x, y - 1, z) != Material.water) {
                return y;
            }
        }
        return -1;
    }
}
