package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import btw.item.items.ChiselItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiselItem.class)
public abstract class ChiselItemMixin {
    @Inject(method = "getCanBePlacedAsBlock", at = @At("RETURN"), cancellable = true, remap = false)
    private void allowSharpStonePlacement(CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this == BTWItems.sharpStone) cir.setReturnValue(true);
    }
}
