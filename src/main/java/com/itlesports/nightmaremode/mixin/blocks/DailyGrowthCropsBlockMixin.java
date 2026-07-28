package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.CropsBlock;
import api.block.blocks.DailyGrowthCropsBlock;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DailyGrowthCropsBlock.class)
public abstract class DailyGrowthCropsBlockMixin extends CropsBlock {
    protected DailyGrowthCropsBlockMixin(int iBlockID) {
        super(iBlockID);
    }

    @Inject(method = "getBaseGrowthChance", at = @At("RETURN"), cancellable = true)
    private void useTypedFarmlandFertilizer(
            World world,
            int x,
            int y,
            int z,
            CallbackInfoReturnable<Float> cir
    ) {
        cir.setReturnValue(ChunkAttributeManager.adjustGrowthChance(
                cir.getReturnValue(),
                world,
                x,
                y,
                z,
                (Block)(Object)this
        ));
    }

    @Redirect(
            method = {"attemptToGrow", "updateFlagForGrownToday"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/src/Block;getIsFertilizedForPlantGrowth(Lnet/minecraft/src/World;III)Z"
            )
    )
    private boolean onlyUseMatchingFertilizer(
            Block farmland,
            World world,
            int x,
            int y,
            int z
    ) {
        return farmland.getIsFertilizedForPlantGrowth(world, x, y, z)
                && ChunkAttributeManager.hasEffectiveFertilizer(
                        world,
                        x,
                        y,
                        z,
                        (Block)(Object)this
                );
    }
}
