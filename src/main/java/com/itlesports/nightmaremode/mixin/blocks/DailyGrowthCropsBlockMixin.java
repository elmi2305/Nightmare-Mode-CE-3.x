package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.CropsBlock;
import api.block.blocks.DailyGrowthCropsBlock;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(DailyGrowthCropsBlock.class)
public abstract class DailyGrowthCropsBlockMixin extends CropsBlock {
    protected DailyGrowthCropsBlockMixin(int iBlockID) {
        super(iBlockID);
    }

    @Inject(method = "getBaseGrowthChance", at = @At("RETURN"), cancellable = true)
    private void preserveDailyGrowthRoll(
            World world,
            int x,
            int y,
            int z,
            CallbackInfoReturnable<Float> cir
    ) {
        // soil scales daily allowances; scaling this roll too would apply the slowdown twice.
        if (BalanceProfile.isEasy()) cir.setReturnValue(Math.min(1.0F, cir.getReturnValue() * 2.0F));
    }

    @Inject(method = "attemptToGrow", at = @At("HEAD"), cancellable = true)
    private void scaleDailyGrowthAllowance(World world, int x, int y, int z, Random random, CallbackInfo ci) {
        int time = (int)Math.floorMod(world.getWorldTime(), 24000L);
        // always let the original night path reset the grown-today flag.
        if (!(time > 14000 && time < 22000)
                && !ChunkAttributeManager.isCropGrowthDay(world, x, z, (Block)(Object)this)) {
            ci.cancel();
        }
    }

    @Inject(method = "updateFlagForGrownToday", at = @At("TAIL"))
    private void allowMoreGrowthInGoodSoil(World world, int x, int y, int z, CallbackInfo ci) {
        ChunkAttributeManager.allowExtraDailyGrowth(world, x, y, z, (Block)(Object)this);
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
