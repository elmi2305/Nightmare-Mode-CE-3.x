package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.crafting.recipe.CauldronRecipeList;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CauldronRecipeList.class)
public class CauldronRecipeListMixin {
    @Inject(method = "addRecipes", at = @At("TAIL"),remap = false)
    private static void addMoreRecipes(CallbackInfo ci){
        RecipeManager.addCauldronRecipe(new ItemStack(Block.cobblestoneMossy, 1), new ItemStack[]{new ItemStack(BTWBlocks.looseCobblestone, 1), new ItemStack(Block.vine, 4)});
    }
}
