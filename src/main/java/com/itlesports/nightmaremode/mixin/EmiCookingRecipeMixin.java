package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.NMOvenCookTimes;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.recipe.EmiCookingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = EmiCookingRecipe.class, remap = false)
public class EmiCookingRecipeMixin {
    @Shadow @Final private EmiIngredient input;

    @ModifyVariable(method = "addWidgets", at = @At("STORE"), ordinal = 0)
    private int showJourneyCookTime(int original) {
        return NMOvenCookTimes.getCookTime(this.input.getEmiStacks().get(0).getItemStack());
    }
}
