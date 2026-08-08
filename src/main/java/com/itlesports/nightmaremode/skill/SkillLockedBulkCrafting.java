package com.itlesports.nightmaremode.skill;

import api.item.tag.TagOrStack;
import btw.crafting.manager.BulkCraftingManager;
import btw.crafting.recipe.types.BulkRecipe;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import java.util.List;

public final class SkillLockedBulkCrafting {
    private SkillLockedBulkCrafting() {
    }

    public static List<ItemStack> getCraftingResult(BulkCraftingManager manager, IInventory inventory) {
        BulkRecipe recipe = findInventoryRecipe(manager, inventory);
        return recipe == null ? null : recipe.getCraftingOutputList();
    }

    public static List<TagOrStack> getValidIngredients(BulkCraftingManager manager, IInventory inventory) {
        BulkRecipe recipe = findInventoryRecipe(manager, inventory);
        return recipe == null ? null : recipe.getCraftingIngrediantList();
    }

    public static List<ItemStack> consumeIngredientsAndReturnResult(BulkCraftingManager manager, IInventory inventory) {
        BulkRecipe recipe = findInventoryRecipe(manager, inventory);
        if (recipe == null) {
            return null;
        }
        recipe.consumeInventoryIngredients(inventory);
        return recipe.getCraftingOutputList();
    }

    public static boolean hasSingleIngredientRecipe(BulkCraftingManager manager, ItemStack input, World world) {
        return findSingleIngredientRecipe(manager, input, world) != null;
    }

    public static List<ItemStack> getSingleIngredientResult(BulkCraftingManager manager, ItemStack input, World world) {
        BulkRecipe recipe = findSingleIngredientRecipe(manager, input, world);
        return recipe == null ? null : recipe.getCraftingOutputList();
    }

    private static BulkRecipe findInventoryRecipe(BulkCraftingManager manager, IInventory inventory) {
        World world = inventory instanceof TileEntity tileEntity ? tileEntity.worldObj : null;
        for (BulkRecipe recipe : manager.getRecipeList()) {
            if (SkillLockedCrafting.isWorldLocked(world, recipe)) {
                continue;
            }
            if (recipe.doesInventoryContainIngredients(inventory)) {
                return recipe;
            }
        }
        return null;
    }

    private static BulkRecipe findSingleIngredientRecipe(BulkCraftingManager manager, ItemStack input, World world) {
        for (BulkRecipe recipe : manager.getRecipeList()) {
            if (SkillLockedCrafting.isWorldLocked(world, recipe)) {
                continue;
            }
            if (recipe.doesStackSatisfyIngredients(input)) {
                return recipe;
            }
        }
        return null;
    }
}
