package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.DiamondOreBlock;
import com.itlesports.nightmaremode.item.NMItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(DiamondOreBlock.class)
public abstract class DiamondOreBlockMixin extends api.block.blocks.OreBlockStaged {
    protected DiamondOreBlockMixin(int id) { super(id); }
    @Override public int quantityDropped(Random random) {
        return com.itlesports.nightmaremode.world.BalanceProfile.isEasy() ? 2 + random.nextInt(2) : super.quantityDropped(random);
    }
    @Inject(method = "idDropped", at = @At("HEAD"), cancellable = true)
    private void dropDiamondBearingRock(int metadata, Random random, int fortune,
                                        CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(NMItems.diamondBearingRock.itemID);
    }

    @Inject(method = "idDroppedOnConversion", at = @At("HEAD"), cancellable = true, remap = false)
    private void convertToDiamondBearingRock(boolean dropPiles, int metadata,
                                             CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(NMItems.diamondBearingRock.itemID);
    }
}
