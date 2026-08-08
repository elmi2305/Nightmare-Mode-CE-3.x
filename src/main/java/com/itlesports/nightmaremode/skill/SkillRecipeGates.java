package com.itlesports.nightmaremode.skill;

import btw.crafting.manager.BulkCraftingManager;
import btw.crafting.manager.SoulforgeCraftingManager;
import btw.crafting.recipe.types.BulkRecipe;
import com.itlesports.nightmaremode.crafting.manager.CisternRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import net.minecraft.src.CraftingManager;
import net.minecraft.src.IRecipe;
import net.minecraft.src.ItemStack;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class SkillRecipeGates {
    private static final Set<IRecipe> TAXED_CRAFTING_RECIPES = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Set<BulkRecipe> TAXED_BULK_RECIPES = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Set<CisternRecipe> TAXED_CISTERN_RECIPES = Collections.newSetFromMap(new IdentityHashMap<>());

    private SkillRecipeGates() {
    }

    public static void crafting(int outputId, SkillNode... skills) {
        crafting(outputId, -1, skills);
    }

    public static void crafting(int outputId, int metadata, SkillNode... skills) {
        for (Object object : CraftingManager.getInstance().getRecipeList()) {
            IRecipe recipe = (IRecipe)object;
            ItemStack output = recipe.getRecipeOutput();
            if (!matches(output, outputId, metadata)) {
                continue;
            }
            SkillLockedCrafting.requireSkills(recipe, skills);
            reduceMultiOutputYield(recipe);
        }
    }

    public static void soulforge(int outputId, SkillNode... skills) {
        soulforge(outputId, -1, skills);
    }

    public static void soulforge(int outputId, int metadata, SkillNode... skills) {
        for (IRecipe recipe : SoulforgeCraftingManager.getInstance().getRecipeList()) {
            if (matches(recipe.getRecipeOutput(), outputId, metadata)) {
                SkillLockedCrafting.requireSkills(recipe, skills);
            }
        }
    }

    public static void bulk(BulkCraftingManager manager, int outputId, SkillNode... skills) {
        bulk(manager, outputId, -1, skills);
    }

    public static void bulk(BulkCraftingManager manager, int outputId, int metadata, SkillNode... skills) {
        for (BulkRecipe recipe : manager.getRecipeList()) {
            boolean matches = recipe.getCraftingOutputList().stream()
                    .anyMatch(output -> matches(output, outputId, metadata));
            if (!matches) {
                continue;
            }
            SkillLockedCrafting.requireWorldSkills(recipe, skills);
            strengthenBulkInputs(manager, recipe);
        }
    }

    public static void bulkByInput(BulkCraftingManager manager, int inputId, SkillNode... skills) {
        for (BulkRecipe recipe : manager.getRecipeList()) {
            boolean matches = recipe.getCraftingIngrediantList().stream()
                    .anyMatch(input -> input instanceof ItemStack stack && stack.itemID == inputId);
            if (!matches) {
                continue;
            }
            SkillLockedCrafting.requireWorldSkills(recipe, skills);
            strengthenBulkInputs(manager, recipe);
        }
    }

    public static void cistern(int outputId, SkillNode... skills) {
        for (CisternRecipe recipe : CisternRecipeManager.instance.getRecipes()) {
            boolean matches = false;
            for (ItemStack output : recipe.getPotentialOutputs()) {
                if (output.itemID == outputId) {
                    matches = true;
                    break;
                }
            }
            if (matches) {
                SkillLockedCrafting.requireWorldSkills(recipe, skills);
                if (TAXED_CISTERN_RECIPES.add(recipe)) {
                    recipe.strengthenFirstInput();
                }
            }
        }
    }

    private static boolean matches(ItemStack output, int outputId, int metadata) {
        return output != null
                && output.itemID == outputId
                && (metadata < 0 || output.getItemDamage() == metadata);
    }

    private static void reduceMultiOutputYield(IRecipe recipe) {
        if (!TAXED_CRAFTING_RECIPES.add(recipe)) {
            return;
        }
        ItemStack output = recipe.getRecipeOutput();
        if (output != null && output.stackSize > 1) {
            output.stackSize = Math.max(1, (output.stackSize + 1) / 2);
        }
    }

    private static void strengthenBulkInputs(BulkCraftingManager manager, BulkRecipe recipe) {
        if (!TAXED_BULK_RECIPES.add(recipe)
                || manager.getClass().getSimpleName().contains("MillStone")) {
            return;
        }
        for (Object ingredient : recipe.getCraftingIngrediantList()) {
            if (ingredient instanceof ItemStack stack) {
                stack.stackSize = Math.min(stack.getMaxStackSize(), Math.max(stack.stackSize + 1, stack.stackSize * 2));
                return;
            }
        }
    }
}
