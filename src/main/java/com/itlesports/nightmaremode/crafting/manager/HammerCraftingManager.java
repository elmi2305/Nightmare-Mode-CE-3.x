package com.itlesports.nightmaremode.crafting.manager;

import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;

public class HammerCraftingManager {
    public static final HammerCraftingManager instance = new HammerCraftingManager();

    private final ArrayList<HammerRecipe> recipes = new ArrayList<>();

    private HammerCraftingManager() {
    }

    public HammerRecipe addRecipe(ItemStack output, Block block) {
        return this.addRecipe(new ItemStack[]{output}, block, new int[]{Short.MAX_VALUE});
    }

    public HammerRecipe addRecipe(ItemStack output, Block block, int metadata) {
        return this.addRecipe(new ItemStack[]{output}, block, new int[]{metadata});
    }

    public HammerRecipe addRecipe(ItemStack output, Block block, int[] metadatas) {
        return this.addRecipe(new ItemStack[]{output}, block, metadatas);
    }

    public HammerRecipe addRecipe(ItemStack[] output, Block block) {
        return this.addRecipe(output, block, new int[]{Short.MAX_VALUE});
    }

    public HammerRecipe addRecipe(ItemStack[] output, Block block, int[] metadatas) {
        HammerRecipe recipe = new HammerRecipe(output, block, metadatas);
        this.recipes.add(recipe);
        return recipe;
    }

    public boolean removeRecipe(ItemStack[] output, Block block, int[] metadatas) {
        HammerRecipe recipeToRemove = new HammerRecipe(output, block, metadatas);
        for (HammerRecipe recipe : this.recipes) {
            if (recipe.matchesRecipe(recipeToRemove)) {
                this.recipes.remove(recipe);
                return true;
            }
        }
        return false;
    }

    public HammerRecipe getRecipe(Block block, int metadata) {
        if (block == null) {
            return null;
        }

        for (HammerRecipe recipe : this.recipes) {
            if (recipe.matchesInputs(block, metadata)) {
                return recipe;
            }
        }
        return null;
    }

    public ItemStack[] getRecipeResult(Block block, int metadata) {
        HammerRecipe recipe = this.getRecipe(block, metadata);
        return recipe == null ? null : recipe.getOutput();
    }

    public ArrayList<HammerRecipe> getRecipes() {
        return this.recipes;
    }
}
