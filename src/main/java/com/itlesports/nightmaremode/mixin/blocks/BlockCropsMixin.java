package com.itlesports.nightmaremode.mixin.blocks;

import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import net.minecraft.src.Block;
import net.minecraft.src.BlockCrops;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockCrops.class)
public class BlockCropsMixin {
    @Inject(method = "incrementGrowthLevel", at = @At("HEAD"), cancellable = true)
    private void requireChunkResources(World world, int x, int y, int z, CallbackInfo ci) {
        Block crop = (Block)(Object)this;
        if (ChunkAttributeManager.isSupportedPlant(crop)
                && !ChunkAttributeManager.canGrow(world, x, z, crop)) {
            ci.cancel();
        }
    }

    @Inject(method = "incrementGrowthLevel", at = @At("TAIL"))
    private void consumeChunkResources(World world, int x, int y, int z, CallbackInfo ci) {
        Block crop = (Block)(Object)this;
        if (ChunkAttributeManager.isSupportedPlant(crop)) {
            ChunkAttributeManager.consumeForGrowth(world, x, z, crop);
        }
    }
}
