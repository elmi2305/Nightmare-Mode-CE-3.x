package com.itlesports.nightmaremode.crafting.manager;

import com.itlesports.nightmaremode.crafting.recipe.types.BrewingStandRecipe;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** registry for non-potion recipes processed by a brewing stand. */
public class BrewingStandRecipeManager {
    public static final BrewingStandRecipeManager instance = new BrewingStandRecipeManager();

    private final List<BrewingStandRecipe> recipes = new ArrayList<BrewingStandRecipe>();

    private BrewingStandRecipeManager() {
    }

    public BrewingStandRecipe addRecipe(BrewingStandRecipe recipe) {
        this.recipes.add(recipe);
        return recipe;
    }

    public BrewingStandRecipe addRecipe(ItemStack output, ItemStack ingredient, ItemStack bottleInput) {
        return this.addRecipe(new BrewingStandRecipe(output, ingredient, bottleInput));
    }

    public BrewingStandRecipe addRecipe(ItemStack output, ItemStack ingredient, ItemStack bottleInput, float brewingTimeMultiplier) {
        return this.addRecipe(new BrewingStandRecipe(output, ingredient, bottleInput, brewingTimeMultiplier));
    }

    public BrewingStandRecipe getMatchingRecipe(ItemStack ingredient, ItemStack bottleInput) {
        for (BrewingStandRecipe recipe : this.recipes) {
            if (recipe.matches(ingredient, bottleInput)) {
                return recipe;
            }
        }
        return null;
    }

    public boolean hasMatchingRecipe(ItemStack ingredient, ItemStack[] bottleSlots) {
        if (ingredient == null || bottleSlots == null) {
            return false;
        }
        for (ItemStack bottleSlot : bottleSlots) {
            if (this.getMatchingRecipe(ingredient, bottleSlot) != null) {
                return true;
            }
        }
        return false;
    }

    public float getBatchTimeMultiplier(ItemStack ingredient, ItemStack[] bottleSlots) {
        float multiplier = 0.0F;
        if (bottleSlots == null) {
            return multiplier;
        }
        for (ItemStack bottleSlot : bottleSlots) {
            BrewingStandRecipe recipe = this.getMatchingRecipe(ingredient, bottleSlot);
            if (recipe != null) {
                multiplier = Math.max(multiplier, recipe.getBrewingTimeMultiplier());
            }
        }
        return multiplier;
    }

    public boolean isIngredient(ItemStack stack) {
        for (BrewingStandRecipe recipe : this.recipes) {
            if (recipe.matchesIngredient(stack)) {
                return true;
            }
        }
        return false;
    }

    public boolean isBottleInput(ItemStack stack) {
        for (BrewingStandRecipe recipe : this.recipes) {
            if (recipe.matchesBottleInput(stack)) {
                return true;
            }
        }
        return false;
    }

    public List<BrewingStandRecipe> getRecipes() {
        return this.recipes;
    }
}
