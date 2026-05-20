package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.MushroomCapBlock;
import btw.item.BTWItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(MushroomCapBlock.class)
public class MushroomCapBlockMixin {
    @Shadow @Final protected int mushroomType;

    @Inject(method = "idDropped", at = @At("HEAD"),cancellable = true)
    private void changeIDDropped(int iMetadata, Random rand, int iFortuneModifier, CallbackInfoReturnable<Integer> cir){
        if (this.mushroomType != 0) {
            cir.setReturnValue(BTWItems.redMushroom.itemID);
            return;
        }
        cir.setReturnValue(0);
    }
}
