package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.crafting.manager.BrewingStandRecipeManager;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.src.SlotBrewingStandIngredient")
public class SlotBrewingStandIngredientMixin {
    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true)
    private void allowCustomIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (BrewingStandRecipeManager.instance.isIngredient(stack)) {
            cir.setReturnValue(true);
        }
    }
}
