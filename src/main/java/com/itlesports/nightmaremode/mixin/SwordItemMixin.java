package com.itlesports.nightmaremode.mixin;

import api.item.items.SwordItem;
import net.minecraft.src.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwordItem.class)
public class SwordItemMixin {
    @Inject(method = "isEnchantmentApplicable", at = @At("HEAD"), cancellable = true)
    private void preventLootingOnSwords(Enchantment enchantment, CallbackInfoReturnable<Boolean> cir) {
        if (enchantment == Enchantment.looting) {
            cir.setReturnValue(false);
        }
    }
}
