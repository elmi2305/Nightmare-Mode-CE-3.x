package com.itlesports.nightmaremode.mixin;

import btw.crafting.manager.CrucibleStokedCraftingManager;
import btw.crafting.recipe.CrucibleRecipeList;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrucibleRecipeList.class)
public class CrucibleRecipeListMixin {
    @Inject(method = "addRecipes", at = @At("TAIL"),remap = false)
    private static void addBloodCrucibleRecipes(CallbackInfo ci){
        // remove vanilla helmet recipe because it returns 6 ingots instead of 5
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 6), new ItemStack[]{new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 5), new ItemStack[]{new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE)});

        // add blood armor and tool recipes
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 5), new ItemStack[]{new ItemStack(NMItems.bloodHelmet, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 8), new ItemStack[]{new ItemStack(NMItems.bloodChestplate, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 7), new ItemStack[]{new ItemStack(NMItems.bloodLeggings, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 4), new ItemStack[]{new ItemStack(NMItems.bloodBoots, 1, Short.MAX_VALUE)});

        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 3), new ItemStack[]{new ItemStack(NMItems.bloodPickaxe, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 2), new ItemStack[]{new ItemStack(NMItems.bloodSword, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 2), new ItemStack[]{new ItemStack(NMItems.bloodAxe, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 1), new ItemStack[]{new ItemStack(NMItems.bloodShovel, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 1), new ItemStack[]{new ItemStack(NMItems.bloodHoe, 1, Short.MAX_VALUE)});
    }
}
