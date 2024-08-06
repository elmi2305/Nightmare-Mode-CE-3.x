package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.crafting.recipe.CraftingRecipeList;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingRecipeList.class)
public class CraftingRecipeListMixin {
    @Inject(method = "addBlockRecipes",at = @At("TAIL"),remap = false)
    private static void addPlanterRecipe(CallbackInfo ci){
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.planter, 1), new Object[]{"# #", "# #", "###", Character.valueOf('#'), Item.brick});
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.planter, 1), new Object[]{"# #", "# #", "###", Character.valueOf('#'), BTWItems.netherBrick});
    }
    @Inject(method = "addLooseBrickRecipes",at = @At("TAIL"),remap = false)
    private static void addClayRecipes(CallbackInfo ci){
        RecipeManager.addShapelessRecipe(new ItemStack(Item.clay), new Object[]{new ItemStack(BTWItems.unfiredNetherBrick)});
    }
    @Inject(method = "addItemRecipes", at = @At("TAIL"),remap = false)
    private static void addGappleRecipes(CallbackInfo ci){
        ItemStack EnchantedApple = new ItemStack(Item.appleGold);
        EnchantedApple.setItemDamage(1);
        RecipeManager.addRecipe(EnchantedApple, new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(Item.ingotGold, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(Item.appleGold), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(Item.goldNugget, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
    }
}
