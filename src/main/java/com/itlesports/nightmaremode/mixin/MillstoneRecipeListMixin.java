package com.itlesports.nightmaremode.mixin;

import btw.crafting.recipe.MillstoneRecipeList;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MillstoneRecipeList.class)
public class MillstoneRecipeListMixin {
    @Inject(method = "addRecipes", at = @At("TAIL"),remap = false)
    private static void addAdditionalRecipes(CallbackInfo ci){
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.carrotSeeds), new ItemStack(BTWItems.hempSeeds));
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.wheatSeeds), new ItemStack(BTWItems.carrotSeeds));
        RecipeManager.addMillStoneRecipe(new ItemStack[]{new ItemStack(NMItems.rifle), new ItemStack(NMItems.rpg)}, new ItemStack[]{new ItemStack(Block.dragonEgg)});
    }
}
