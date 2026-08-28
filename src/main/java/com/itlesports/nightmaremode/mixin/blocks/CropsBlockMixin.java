package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.CropsBlock;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropsBlock.class)
public class CropsBlockMixin {
    @Inject(method = "incrementGrowthLevel", at = @At("HEAD"), cancellable = true)
    private void requireChunkResources(World world, int x, int y, int z, CallbackInfo ci) {
        if (ChunkPollutionManager.isAtLeast(world, x, z, ChunkPollutionManager.BIOLOGICAL_DAMAGE)) {
            world.setBlockToAir(x, y, z);
            ci.cancel();
            return;
        }
        if (!ChunkAttributeManager.canGrow(world, x, z, (Block)(Object)this)) {
            ci.cancel();
        }
    }

    @Inject(method = "incrementGrowthLevel", at = @At("TAIL"))
    private void consumeChunkResources(World world, int x, int y, int z, CallbackInfo ci) {
        ChunkAttributeManager.consumeForGrowth(world, x, z, (Block)(Object)this);
    }
}
