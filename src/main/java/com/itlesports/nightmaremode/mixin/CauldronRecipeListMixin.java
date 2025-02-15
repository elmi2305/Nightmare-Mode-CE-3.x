package com.itlesports.nightmaremode.mixin;

import btw.crafting.recipe.CauldronRecipeList;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronRecipeList.class)
public class CauldronRecipeListMixin {
    @Inject(method = "addRecipes", at = @At("TAIL"),remap = false)
    private static void addMoreRecipes(CallbackInfo ci){
        RecipeManager.addCauldronRecipe(new ItemStack(Item.potato, 1), new ItemStack[]{new ItemStack(BTWItems.straw, 8)});
        RecipeManager.addCauldronRecipe(new ItemStack(Item.clay, 8), new ItemStack[]{new ItemStack(BTWItems.netherSludge, 8)});
        RecipeManager.addCauldronRecipe(new ItemStack(NMItems.friedCalamari), new ItemStack[]{new ItemStack(NMItems.calamariRoast), new ItemStack(Item.bowlEmpty)});
        RecipeManager.addStokedCauldronRecipe(new ItemStack(BTWItems.netherSludge, 4), new ItemStack[]{new ItemStack(BTWItems.netherBrick, 8)});
    }
}
