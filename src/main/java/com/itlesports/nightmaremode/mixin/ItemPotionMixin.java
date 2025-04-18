package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Item;
import net.minecraft.src.ItemPotion;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemPotion.class)
public class ItemPotionMixin extends Item {
    public ItemPotionMixin(int par1) {
        super(par1);
    }

    @Inject(method = "getMaxItemUseDuration", at = @At("HEAD"),cancellable = true)
    private void makePotionsDrinkQuicker(ItemStack par1ItemStack, CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(20);
    }
}
