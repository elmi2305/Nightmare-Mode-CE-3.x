package com.itlesports.nightmaremode.crafting.recipe;

import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public abstract class HammerRecipeList {
    public static void addRecipes() {
        HammerCraftingManager.instance.addRecipe(new ItemStack(Block.cobblestone, 1), Block.stone, new int[]{0,1,2});
    }
}
