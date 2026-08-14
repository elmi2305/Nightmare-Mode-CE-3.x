package com.itlesports.nightmaremode.crafting.manager;

import com.itlesports.nightmaremode.crafting.recipe.types.EnderAssemblerRecipe;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class EnderAssemblerRecipeManager {
    public static final EnderAssemblerRecipeManager instance = new EnderAssemblerRecipeManager();
    private final List<EnderAssemblerRecipe> recipes = new ArrayList<>();

    private EnderAssemblerRecipeManager() {}

    public EnderAssemblerRecipe addRecipe(ItemStack output, int duration, ItemStack... ingredients) {
        EnderAssemblerRecipe recipe = new EnderAssemblerRecipe(output, duration, ingredients);
        this.recipes.add(recipe);
        return recipe;
    }

    public EnderAssemblerRecipe find(IInventory inventory) {
        for (EnderAssemblerRecipe recipe : this.recipes) if (recipe.matches(inventory)) return recipe;
        return null;
    }

    public List<EnderAssemblerRecipe> getRecipes() {
        return List.copyOf(this.recipes);
    }
}
