package com.itlesports.nightmaremode.mixin;

import btw.crafting.recipe.types.HopperFilterRecipe;
import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperFilterRecipe.class)
public abstract class EasyGravelFilteringMixin {
    @Inject(method = {"getHopperOutput", "getFilteredOutput"}, at = @At("RETURN"), cancellable = true)
    private void improveGravelFiltering(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack output = cir.getReturnValue();
        HopperFilterRecipe recipe = (HopperFilterRecipe)(Object)this;
        if (BalanceProfile.isEasy() && output != null && output.itemID == Item.flint.itemID
                && recipe.getInput().matches(new ItemStack(Block.gravel), false)) {
            ItemStack result = output.copy();
            result.stackSize *= 3;
            cir.setReturnValue(result);
        }
    }
}
