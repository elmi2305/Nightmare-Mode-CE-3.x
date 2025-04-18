package com.itlesports.nightmaremode.mixin;

import btw.crafting.recipe.RecipeManager;
import btw.crafting.recipe.SmeltingRecipeList;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmeltingRecipeList.class)
public class SmeltingRecipeListMixin {
    @Inject(method = "addSmeltingRecipes", at = @At("TAIL"),remap = false)
    private static void addCalamariRecipe(CallbackInfo ci){
        // add calamari
        FurnaceRecipes.smelting().addSmelting(NMItems.calamari.itemID, new ItemStack(NMItems.calamariRoast), 0.0f);

        // add food overcooking - doesn't work since the items don't get detected when going from raw->cooked
//        FurnaceRecipes.smelting().addSmelting(FoodItem.porkCooked.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(FoodItem.fishCooked.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(FoodItem.chickenCooked.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(FoodItem.beefCooked.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(BTWItems.cookedMutton.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(BTWItems.cookedLiver.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(BTWItems.cookedMysteryMeat.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(BTWItems.cookedKebab.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(BTWItems.cookedCheval.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(BTWItems.cookedWolfChop.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(NMItems.calamariRoast.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);
//        FurnaceRecipes.smelting().addSmelting(NMItems.calamariRoast.itemID, new ItemStack(BTWItems.burnedMeat), 0.0f);


    }
    @Inject(method = "addCampfireRecipes", at = @At("TAIL"),remap = false)
    private static void addCalamariRecipeCampfire(CallbackInfo ci){
        RecipeManager.addCampfireRecipe(NMItems.calamari.itemID, new ItemStack(NMItems.calamariRoast));
    }
}
