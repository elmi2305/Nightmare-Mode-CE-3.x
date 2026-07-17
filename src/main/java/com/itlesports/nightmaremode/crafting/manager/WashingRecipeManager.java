package com.itlesports.nightmaremode.crafting.manager;

import com.itlesports.nightmaremode.crafting.recipe.types.WashingRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public class WashingRecipeManager {
    public static final WashingRecipeManager instance = new WashingRecipeManager();

    private final List<WashingRecipe> recipes = new ArrayList<>();

    private WashingRecipeManager() {
    }

    public WashingRecipe addWaterRecipe(ItemStack output, ItemStack input, int duration) {
        WashingRecipe recipe = new WashingRecipe(output, input, duration);
        this.recipes.add(recipe);
        return recipe;
    }

    public WashingRecipe addRainRecipe(Block output, Block input, int duration, int chanceDivisor) {
        return this.addRainRecipe(output, 0, input, Short.MAX_VALUE, duration, chanceDivisor);
    }

    public WashingRecipe addRainRecipe(Block output, int outputMetadata, Block input, int inputMetadata,
                                       int duration, int chanceDivisor) {
        WashingRecipe recipe = new WashingRecipe(
                output, outputMetadata, input, inputMetadata, duration, chanceDivisor);
        this.recipes.add(recipe);
        return recipe;
    }

    public WashingRecipe getWaterRecipe(ItemStack input) {
        for (WashingRecipe recipe : this.recipes) {
            if (recipe.matchesWaterInput(input)) {
                return recipe;
            }
        }
        return null;
    }

    public WashingRecipe getRainRecipe(Block input, int metadata) {
        for (WashingRecipe recipe : this.recipes) {
            if (recipe.matchesRainInput(input, metadata)) {
                return recipe;
            }
        }
        return null;
    }

    public List<WashingRecipe> getRecipes() {
        return this.recipes;
    }
}
