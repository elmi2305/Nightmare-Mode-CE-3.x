package com.itlesports.nightmaremode.mixin;

import btw.item.items.BucketItemWater;
import btw.util.MiscUtils;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BucketItemWater.class)
public class BucketItemWaterMixin {
    @Redirect(method = "attemptPlaceContentsAtLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;setBlockWithNotify(IIII)Z",ordinal = 2))
    private boolean placeFlowingWaterInTheEnd(World world, int i, int j, int k, int iBlockID){
        world.setBlockAndMetadataWithNotify(i, j, k, Block.waterMoving.blockID, 1);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i + 1, j, k, 1);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i - 1, j, k, 1);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i, j, k + 1, 1);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i, j, k - 1, 1);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i + 1, j, k + 1, 1);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i - 1, j, k + 1, 1);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i + 1, j, k - 1, 1);
        MiscUtils.flowWaterIntoBlockIfPossible(world, i - 1, j, k - 1, 1);

        return true;
    }
}
