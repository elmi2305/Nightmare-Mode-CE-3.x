package com.itlesports.nightmaremode.mixin;

import btw.crafting.recipe.MillstoneRecipeList;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import net.minecraft.src.BlockVine;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(MillstoneRecipeList.class)
public class MillstoneRecipeListMixin {
    @Inject(method = "addRecipes", at = @At("TAIL"),remap = false)
    private static void addAdditionalRecipes(CallbackInfo ci){
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.carrotSeeds,2), new ItemStack(BTWItems.hempSeeds));
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.wheatSeeds), new ItemStack(BTWItems.carrotSeeds));
        RecipeManager.addMillStoneRecipe(new ItemStack(Item.potato), new ItemStack(BTWItems.straw));
        RecipeManager.addMillStoneRecipe(new ItemStack(Item.melonSeeds), new ItemStack(Item.pumpkinSeeds));
        RecipeManager.addMillStoneRecipe(new ItemStack(Item.pumpkinSeeds), new ItemStack(Item.melonSeeds));
    }
}
