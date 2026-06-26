package com.itlesports.nightmaremode.util.elements;

import btw.crafting.recipe.types.SawRecipe;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

import java.util.ArrayList;

public class BloodSawCraftingManager {
    private BloodSawCraftingManager() {
    }

    public static BloodSawCraftingManager instance = new BloodSawCraftingManager();
    private ArrayList<SawRecipe> recipes = new ArrayList();



    public boolean removeRecipe(ItemStack[] output, Block block, int[] metadatas) {
        SawRecipe recipeToRemove = new SawRecipe(output, block, metadatas);

        for(SawRecipe recipe : this.recipes) {
            if (recipe.matchesRecipe(recipeToRemove)) {
                this.recipes.remove(recipe);
                return true;
            }
        }

        return false;
    }

    public SawRecipe getRecipe(Block block, int metadata) {
        for(SawRecipe recipe : this.recipes) {
            if (recipe.matchesInputs(block, metadata)) {
                return recipe;
            }
        }

        return null;
    }

    public ItemStack[] getRecipeResult(Block block, int metadata) {
        for(SawRecipe recipe : this.recipes) {
            if (recipe.matchesInputs(block, metadata)) {
                return recipe.getOutput();
            }
        }

        return null;
    }

    public SawRecipe addRecipe(ItemStack[] output, Block block, int[] metadatas) {
        SawRecipe recipe = new SawRecipe(output, block, metadatas);
        this.recipes.add(recipe);
        return recipe;
    }

    public void addSawSubBlockRecipes(Block fullBlock, int metadata, Block sidingAndCornerBlock, Block mouldingBlock, ItemStack slabStack) {

        this.addSawSubBlockRecipes(fullBlock, metadata, metadata, sidingAndCornerBlock, mouldingBlock, slabStack);
    }

    public void addSawSubBlockRecipes(Block fullBlock, int inputMetadata, int outputMetadata, Block sidingAndCornerBlock, Block mouldingBlock, ItemStack slabStack) {

        // Full Block - 2 Sidings
        this.addRecipe(
                new ItemStack[]{ new ItemStack(sidingAndCornerBlock, 2, 0) },
                fullBlock,
                new int[]{inputMetadata}
        );

        // Siding (meta 0) - 2 Mouldings
        this.addRecipe(
                new ItemStack[]{ new ItemStack(mouldingBlock, 2, 0) },
                sidingAndCornerBlock,
                new int[]{0}
        );

        // Moulding (meta 0) - 2 Corners (meta 1)
        this.addRecipe(
                new ItemStack[]{ new ItemStack(sidingAndCornerBlock, 2, 0) },
                mouldingBlock,
                new int[]{0}
        );

        // Corner (meta 1) - drops itself
//        this.addRecipe(
//                new ItemStack[]{ new ItemStack(sidingAndCornerBlock, 1, 1) },
//                sidingAndCornerBlock,
//                new int[]{1}
//        );
    }

    public ArrayList<SawRecipe> getRecipes() {
        return this.recipes;
    }
}
