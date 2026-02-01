package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.AestheticOpaqueEarthBlock;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(AestheticOpaqueEarthBlock.class)
public class AestheticOpaqueEarthBlockMixin {

    @Redirect(method = "checkForBlightEvolution", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockMaterial(III)Lnet/minecraft/src/Material;",ordinal = 0))
    private Material blightRandomGrowthWater(World w, int par1, int par2, int par3){
        if((w.getWorldTime() & 1023) == 0 && w.rand.nextInt(10) == 0){
            return Material.water;
        }
        return Material.air;
        // on average every 500 seconds, or 8 minutes and 20 seconds. this makes the blight no longer need water to grow to the second stage
    }

    @Redirect(method = "checkForBlightEvolution", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockMaterial(III)Lnet/minecraft/src/Material;",ordinal = 1))
    private Material blightRandomGrowthLava(World w, int par1, int par2, int par3){
        if((w.getWorldTime() & 1023) == 0 && w.rand.nextInt(10) == 0){
            return Material.lava;
        }
        return Material.air;
        // on average every 500 seconds, or 8 minutes and 20 seconds. this makes the blight no longer need water to grow to the second stage
    }
    @Inject(method = "checkForBlightEvolution", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockId(III)I"))
    private void blightRandomGrowth(World w, int i, int j, int k, Random rand, CallbackInfo ci){
        if((w.getWorldTime() & 1023) == 0 && w.rand.nextInt(10) == 0){
            w.setBlockMetadataWithNotify(i, j, k, 4);
        }
    }
}
