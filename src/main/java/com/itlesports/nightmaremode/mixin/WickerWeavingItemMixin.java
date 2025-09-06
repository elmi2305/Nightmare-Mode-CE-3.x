package com.itlesports.nightmaremode.mixin;

import btw.item.items.WickerWeavingItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WickerWeavingItem.class)
public class WickerWeavingItemMixin {
    @Mutable
    @Shadow(remap = false) @Final public static int WICKER_WEAVING_MAX_DAMAGE;

    @Inject(method = "getProgressiveCraftingMaxDamage", at = @At("HEAD"), remap = false)
    private void makeWickerWeaveFaster(CallbackInfoReturnable<Integer> cir){
        WICKER_WEAVING_MAX_DAMAGE = 150;
    }
}
