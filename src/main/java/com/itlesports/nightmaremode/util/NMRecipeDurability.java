package com.itlesports.nightmaremode.util;

import api.item.tag.TagOrStack;
import btw.crafting.manager.SoulforgeCraftingManager;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import net.minecraft.src.*;

import java.util.Arrays;
import java.util.List;

public final class NMRecipeDurability {
    private NMRecipeDurability() {}

    public static void allowWornIngredients() {
        normalize(CraftingManager.getInstance().getRecipeList());
        normalize(SoulforgeCraftingManager.getInstance().getRecipeList());
    }

    private static void normalize(List recipes) {
        for (Object entry : recipes) {
            IRecipe recipe = (IRecipe)entry;
            List<TagOrStack> ingredients;
            if (recipe instanceof ShapedRecipes shaped) ingredients = Arrays.asList(shaped.getRecipeItems());
            else if (recipe instanceof ShapelessRecipes shapeless) ingredients = shapeless.getRecipeItems();
            else continue;
            ItemStack output = recipe.getRecipeOutput();
            boolean armorUpgrade = output != null && isArmor(output.getItem());
            for (TagOrStack ingredient : ingredients) {
                if (ingredient instanceof ItemStack stack
                        && (stack.getItem() == NMItems.soulFlint
                            || armorUpgrade && isArmor(stack.getItem()))) {
                    stack.setItemDamage(Short.MAX_VALUE);
                }
            }
        }
    }

    private static boolean isArmor(Item item) {
        return item instanceof ItemArmor || item instanceof ItemAdvancedHorseArmor;
    }
}
