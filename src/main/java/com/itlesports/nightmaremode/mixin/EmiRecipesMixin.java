package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.api.RecipeIndexExporter;
import emi.dev.emi.emi.api.recipe.EmiRecipeManager;
import emi.dev.emi.emi.registry.EmiRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EmiRecipes.class, remap = false)
public abstract class EmiRecipesMixin {
    @Shadow(remap = false) public static EmiRecipeManager manager;

    @Inject(method = "bake", at = @At("TAIL"), remap = false)
    private static void exportRecipeIndex(CallbackInfo ci) {
        RecipeIndexExporter.exportDevelopmentIndex(manager);
    }
}