package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Enchantment;
import net.minecraft.src.EnchantmentProtection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentProtection.class)
public class EnchantmentProtectionMixin {
    @Inject(method = "getMaxLevel", at = @At("HEAD"),cancellable = true)
    private void limitProtectionEnchantmentsToLevelThree(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(3);
    }

    @Inject(method = "canApplyTogether", at = @At("HEAD"),cancellable = true)
    private void cannotApplyProtectionsTogether(Enchantment par1, CallbackInfoReturnable<Boolean> cir){
        if(par1 instanceof EnchantmentProtection){
            cir.setReturnValue(false);
        }
    }
}
