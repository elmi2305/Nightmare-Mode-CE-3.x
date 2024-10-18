package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.CobblestoneBlock;
import btw.item.BTWItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CobblestoneBlock.class)
public class CobblestoneBlockMixin {
    @Inject(method = "idDropped", at = @At("RETURN"), cancellable = true)
    private void dropStoneOnBreak(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(BTWItems.stone.itemID);
        // makes mortared cobblestone drop 1 loose rock instead of the full cobblestone block. this is done to nerf day 1 village strategies
    }
}
