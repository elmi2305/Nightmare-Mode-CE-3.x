package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.HempCropBlockRoots;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(HempCropBlockRoots.class)
public class HempCropBlockRootsMixin {
    @Inject(method = "attemptToGrowTop", at = @At("HEAD"), cancellable = true)
    private void preventTopGrowthWithoutResources(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        if (ChunkPollutionManager.isAtLeast(world, x, z, ChunkPollutionManager.BIOLOGICAL_DAMAGE)) {
            world.setBlockToAir(x, y, z);
            ci.cancel();
            return;
        }
        if (!ChunkAttributeManager.canPlantGrow(world, x, z, (Block)(Object)this)) {
            ci.cancel();
        }
    }

    @Redirect(method = "attemptToGrowTop", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/World;setBlockAndMetadataWithNotify(IIIII)Z"))
    private boolean consumeWhenGrowingTop(World world, int x, int y, int z, int blockId, int metadata) {
        boolean changed = world.setBlockAndMetadataWithNotify(x, y, z, blockId, metadata);
        if (changed) {
            ChunkAttributeManager.consumeForHempTopGrowth(world, x, z, (Block)(Object)this);
        }
        return changed;
    }

    @Redirect(method = "attemptToGrowTop", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/Block;getPlantGrowthOnMultiplier(Lnet/minecraft/src/World;IIILnet/minecraft/src/Block;)F"))
    private float onlyUseMatchingFertilizer(Block farmland, World world, int x, int y, int z, Block crop) {
        float multiplier = farmland.getPlantGrowthOnMultiplier(world, x, y, z, crop);
        return multiplier > 1.0F && !ChunkAttributeManager.hasEffectiveFertilizer(world, x, y, z, crop)
                ? 1.0F : multiplier;
    }
}
