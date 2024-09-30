package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.crafting.recipe.RecipeManager;
import btw.crafting.recipe.SoulforgeRecipeList;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoulforgeRecipeList.class)
public class SoulforgeRecipeListMixin {
    @Inject(method = "addRecipes", at = @At("TAIL"),remap = false)
    private static void addAdditionalRecipes(CallbackInfo ci){
        RecipeManager.addSoulforgeRecipe(new ItemStack(Block.cobblestoneMossy, 4),new Object[]{"####", "#XX#", "#XX#", "####", Character.valueOf('#'), Block.vine, Character.valueOf('X'), BTWBlocks.looseCobblestone});
    }
}
