package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.HempCropBlock;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HempCropBlock.class)
public class HempCropBlockMixin {
    @Inject(method = "getBaseGrowthChance", at = @At("HEAD"),cancellable = true)
    private void useChunkResourcesAndTypedFertilizer(
            World world,
            int x,
            int y,
            int z,
            CallbackInfoReturnable<Float> cir
    ) {
        if (ChunkPollutionManager.isAtLeast(world, x, z, ChunkPollutionManager.BIOLOGICAL_DAMAGE)) {
            world.setBlockToAir(x, y, z);
            cir.setReturnValue(0.0F);
            return;
        }
        cir.setReturnValue(ChunkAttributeManager.adjustGrowthChance(
                0.01F,
                world,
                x,
                y,
                z,
                (Block)(Object)this
        ));
    }

    @Redirect(
            method = "attemptToGrow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/World;setBlockAndMetadataWithNotify(IIIII)Z"
            )
    )
    private boolean consumeWhenGrowingTop(
            World world,
            int x,
            int y,
            int z,
            int blockId,
            int metadata
    ) {
        if (!ChunkAttributeManager.canGrow(world, x, z, (Block)(Object)this)) {
            return false;
        }
        boolean changed = world.setBlockAndMetadataWithNotify(x, y, z, blockId, metadata);
        if (changed) {
            ChunkAttributeManager.consumeForGrowth(world, x, z, (Block)(Object)this);
        }
        return changed;
    }

    @Redirect(
            method = "attemptToGrow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/Block;getPlantGrowthOnMultiplier(Lnet/minecraft/src/World;IIILnet/minecraft/src/Block;)F"
            )
    )
    private float onlyUseMatchingFertilizer(
            Block farmland,
            World world,
            int x,
            int y,
            int z,
            Block crop
    ) {
        float multiplier = farmland.getPlantGrowthOnMultiplier(world, x, y, z, crop);
        if (multiplier > 1.0F
                && !ChunkAttributeManager.hasEffectiveFertilizer(world, x, y, z, crop)) {
            return 1.0F;
        }
        return multiplier;
    }
}
