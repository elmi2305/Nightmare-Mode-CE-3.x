package com.itlesports.nightmaremode.mixin.biomegen;

import net.minecraft.src.*;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.worldgen.WorldGenAquamarineOre;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BiomeDecorator.class)
public class BiomeDecoratorMixin {
    @Shadow protected WorldGenerator mushroomBrownGen;
    @Shadow protected int treesPerChunk;

    @Shadow
    protected BiomeGenBase biome;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(CallbackInfo ci) {
        // removes brown mushrooms from the overworld
        this.mushroomBrownGen = new WorldGenFlowers(Block.mushroomRed.blockID);
    }

    @Redirect(method = "decorate()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldGenerator;generate(Lnet/minecraft/src/World;Ljava/util/Random;III)Z", ordinal = 3))
    private boolean addMushroomsBelowTrees(WorldGenerator generator, World world, Random random, int x, int y, int z) {
        boolean generated = generator.generate(world, random, x, y, z);
        int mushroomChance = this.treesPerChunk > 2 ? this.treesPerChunk * this.treesPerChunk : 4;
        if (generated && world.provider.dimensionId == 0 && random.nextInt(mushroomChance) == 0 && this.biome != BiomeGenBase.swampland) {
            for (int attempt = 0; attempt < 4; ++attempt) {
                int mushroomX = x + random.nextInt(5) - 2;
                int mushroomZ = z + random.nextInt(5) - 2;
                for (int mushroomY = y + 2; mushroomY >= y - 2; --mushroomY) {
                    int ground = world.getBlockId(mushroomX, mushroomY - 1, mushroomZ);
                    if (ground != Block.grass.blockID && ground != Block.dirt.blockID) continue;
                    if (world.getBlockId(mushroomX, mushroomY, mushroomZ) != 0) break;
                    boolean shaded = false;
                    for (int canopyY = mushroomY + 2; canopyY <= mushroomY + 7; ++canopyY) {
                        if (world.getBlockId(mushroomX, canopyY, mushroomZ) == Block.leaves.blockID) {
                            shaded = true;
                            break;
                        }
                    }
                    if (shaded && Block.mushroomRed.canPlaceBlockAt(world, mushroomX, mushroomY, mushroomZ)) {
                        world.setBlock(mushroomX, mushroomY, mushroomZ, Block.mushroomRed.blockID);
                        return true;
                    }
                    break;
                }
            }
        }
        return generated;
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
