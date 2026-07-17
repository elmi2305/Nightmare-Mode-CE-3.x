package com.itlesports.nightmaremode.crafting.manager;

import com.itlesports.nightmaremode.crafting.recipe.types.MiscRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public class MiscRecipeManager {
    public static final MiscRecipeManager instance = new MiscRecipeManager();

    private final List<MiscRecipe> recipes = new ArrayList<>();

    private MiscRecipeManager() {
    }

    public MiscRecipe addRecipe(ItemStack output, ItemStack input, String description) {
        MiscRecipe recipe = new MiscRecipe(output, input, description);
        this.recipes.add(recipe);
        return recipe;
    }

    public MiscRecipe addBlockRecipe(Block output, Block input, String description) {
        return this.addBlockRecipe(output, 0, input, Short.MAX_VALUE, description);
    }

    public MiscRecipe addBlockRecipe(Block output, int outputMetadata, Block input, int inputMetadata,
                                     String description) {
        MiscRecipe recipe = new MiscRecipe(output, outputMetadata, input, inputMetadata, description);
        this.recipes.add(recipe);
        return recipe;
    }

    public MiscRecipe getBlockRecipe(Block input, int metadata) {
        for (MiscRecipe recipe : this.recipes) {
            if (recipe.matchesBlockInput(input, metadata)) {
                return recipe;
            }
        }
        return null;
    }

    public MiscRecipe getRecipe(ItemStack input) {
        for (MiscRecipe recipe : this.recipes) {
            if (recipe.matchesInput(input)) {
                return recipe;
            }
        }
        return null;
    }

    public List<MiscRecipe> getRecipes() {
        return this.recipes;
    }
}
