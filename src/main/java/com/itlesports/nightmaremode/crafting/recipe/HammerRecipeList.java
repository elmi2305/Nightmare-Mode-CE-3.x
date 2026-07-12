package com.itlesports.nightmaremode.crafting.recipe;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.crafting.manager.HammerCraftingManager;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public abstract class HammerRecipeList {
    public static void addRecipes() {

        HammerCraftingManager.instance.addRecipe(new ItemStack(BTWItems.ironNugget, 1), Block.stone, new int[]{0,1,2});
    }
}
