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
        FurnaceRecipes.smelting().addSmelting(NMItems.calamari.itemID, new ItemStack(NMItems.calamariRoast), 0.0f);
    }
    @Inject(method = "addCampfireRecipes", at = @At("TAIL"),remap = false)
    private static void addCalamariRecipeCampfire(CallbackInfo ci){
        RecipeManager.addCampfireRecipe(NMItems.calamari.itemID, new ItemStack(NMItems.calamariRoast));
    }
}
