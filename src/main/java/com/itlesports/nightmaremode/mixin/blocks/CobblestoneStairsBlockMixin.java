package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.CobblestoneStairsBlock;
import btw.item.BTWItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(CobblestoneStairsBlock.class)
public class CobblestoneStairsBlockMixin {
    @Shadow(remap = false) private int strata;

    @Inject(method = "idDropped", at = @At("TAIL"),cancellable = true, remap = false)
    private void dropOnlyOneRock(int iMetaData, Random rand, int iFortuneModifier, CallbackInfoReturnable<Integer> cir){
        if(this.strata == 0){
            cir.setReturnValue(BTWItems.stone.itemID);
        }
    }
}
