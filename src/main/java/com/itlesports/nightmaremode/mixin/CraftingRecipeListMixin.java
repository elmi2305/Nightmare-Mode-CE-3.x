package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.crafting.recipe.CraftingRecipeList;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import btw.item.items.BandageItem;
import com.itlesports.nightmaremode.ItemBandage;
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
    private static void addAdditionalBlocks(CallbackInfo ci){
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.planter, 1), new Object[]{"# #", "# #", "###", Character.valueOf('#'), Item.brick});
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.planter, 1), new Object[]{"# #", "# #", "###", Character.valueOf('#'), BTWItems.netherBrick});
        RecipeManager.addRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XXX", "###", Character.valueOf('#'), new ItemStack(BTWItems.woodSidingStubID, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.book,1,Short.MAX_VALUE)});
    }
    @Inject(method = "addLooseBrickRecipes",at = @At("TAIL"),remap = false)
    private static void addClayRecipes(CallbackInfo ci){
        RecipeManager.addShapelessRecipe(new ItemStack(Item.clay), new Object[]{new ItemStack(BTWItems.unfiredNetherBrick)});
    }
    @Inject(method = "addItemRecipes", at = @At("TAIL"),remap = false)
    private static void addGappleAndBookRecipes(CallbackInfo ci){
//        Item bandage = new ItemBandage(2305, 0, 0, false);
//        RecipeManager.addShapelessRecipe(new ItemStack(bandage), new Object[]{BTWItems.wool, new ItemStack(BTWItems.wool, 1, Short.MAX_VALUE), new ItemStack(BTWItems.wool, 1, Short.MAX_VALUE), Item.silk});
        ItemStack EnchantedApple = new ItemStack(Item.appleGold);
        EnchantedApple.setItemDamage(1);
        RecipeManager.addRecipe(EnchantedApple, new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(Item.ingotGold, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(Item.appleGold), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(Item.goldNugget, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(Item.book), new Object[]{"###", "XXX", Character.valueOf('#'), BTWItems.cutScouredLeather, Character.valueOf('X'), new ItemStack(Item.paper, 1, Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(Item.book), new Object[]{"###", "XXX", Character.valueOf('#'), BTWItems.cutTannedLeather, Character.valueOf('X'), new ItemStack(Item.paper, 1, Short.MAX_VALUE)});
    }
}
