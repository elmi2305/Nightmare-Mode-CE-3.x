package com.itlesports.nightmaremode.crafting.manager;

import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CisternRecipeManager {
    public static final CisternRecipeManager instance = new CisternRecipeManager();

    private final List<CisternRecipe> recipes = new ArrayList<>();

    private CisternRecipeManager() {
    }

    public CisternRecipe addRecipe(CisternRecipe recipe) {
        this.recipes.add(recipe);
        return recipe;
    }

    public CisternRecipe addRecipe(ItemStack[] inputs, int requiredFluid, int requiredHeat, int requiredStir, int duration, ItemStack[] outputs) {
        CisternRecipe recipe = new CisternRecipe(inputs, requiredFluid, requiredHeat, requiredStir, duration, outputs);
        return this.addRecipe(recipe);
    }

    public CisternRecipe getMatchingRecipe(ItemStack[] inventory, int fluid, int heat, int stir) {
        for (CisternRecipe recipe : this.recipes) {
            if (recipe.matches(inventory, fluid, heat, stir)) {
                return recipe;
            }
        }
        return null;
    }

    public ItemStack[] getRecipeResult(CisternRecipe recipe, Random random) {
        return recipe == null ? null : recipe.getOutputs(random);
    }

    public List<CisternRecipe> getRecipes() {
        return this.recipes;
    }
}
