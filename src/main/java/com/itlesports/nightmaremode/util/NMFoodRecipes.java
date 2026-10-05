package com.itlesports.nightmaremode.util;

import api.item.tag.Tag;
import api.item.tag.TagInstance;
import api.item.tag.TagOrStack;
import btw.crafting.manager.BulkCraftingManager;
import btw.crafting.manager.CauldronCraftingManager;
import btw.crafting.manager.CauldronStokedCraftingManager;
import btw.crafting.recipe.types.BulkRecipe;
import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import com.itlesports.nightmaremode.skill.SkillNode;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NMFoodRecipes {
    private NMFoodRecipes() {}

    public static void replaceFreshnessSensitiveRecipes() {
        List recipes = CraftingManager.getInstance().getRecipeList();
        Map<Tag, Tag> foodTags = new HashMap<>();
        for (int index = 0; index < recipes.size(); ++index) {
            IRecipe original = (IRecipe)recipes.get(index);
            TagOrStack[] inputs;
            if (original.getClass() == ShapedRecipes.class) {
                inputs = ((ShapedRecipes)original).getRecipeItems().clone();
            } else if (original.getClass() == ShapelessRecipes.class) {
                inputs = ((ShapelessRecipes)original).getRecipeItems().toArray(new TagOrStack[0]);
            } else continue;

            boolean changed = false;
            for (int slot = 0; slot < inputs.length; ++slot) {
                TagOrStack replacement = allowAnyFreshness(inputs[slot], foodTags);
                changed |= replacement != inputs[slot];
                inputs[slot] = replacement;
            }
            if (!changed) continue;

            IRecipe replacement;
            if (original instanceof ShapedRecipes shaped) {
                ShapedRecipes copy = new ShapedRecipes(shaped.getRecipeWidth(), shaped.getRecipeHeight(),
                        inputs, shaped.getRecipeOutput().copy());
                copy.setSecondaryOutput(shaped.getSecondaryOutput(null));
                replacement = copy;
            } else {
                ShapelessRecipes copy = new ShapelessRecipes(original.getRecipeOutput().copy(),
                        new ArrayList<>(java.util.Arrays.asList(inputs)));
                copy.setSecondaryOutput(original.getSecondaryOutput(null));
                replacement = copy;
            }
            SkillLockedCrafting.requireSkills(replacement,
                    SkillLockedCrafting.getRequiredSkills(original).toArray(new SkillNode[0]));
            recipes.set(index, replacement);
        }
        allowBulkFoodFreshness(CauldronCraftingManager.getInstance(), foodTags);
        allowBulkFoodFreshness(CauldronStokedCraftingManager.getInstance(), foodTags);
    }

    private static void allowBulkFoodFreshness(BulkCraftingManager manager, Map<Tag, Tag> foodTags) {
        for (BulkRecipe recipe : manager.getRecipeList()) {
            List<SkillNode> skills = new ArrayList<>(SkillLockedCrafting.getRequiredSkills(recipe));
            List<TagOrStack> inputs = recipe.getCraftingIngrediantList();
            for (int slot = 0; slot < inputs.size(); ++slot) {
                inputs.set(slot, allowAnyFreshness(inputs.get(slot), foodTags));
            }
            SkillLockedCrafting.requireWorldSkills(recipe, skills.toArray(new SkillNode[0]));
        }
    }

    private static TagOrStack allowAnyFreshness(TagOrStack ingredient, Map<Tag, Tag> foodTags) {
        if (ingredient instanceof ItemStack stack && NMFoodSpoilage.isPerishable(stack)
                && stack.getItemDamage() != Short.MAX_VALUE) {
            ItemStack copy = stack.copy();
            copy.setItemDamage(Short.MAX_VALUE);
            return copy;
        }
        if (ingredient instanceof TagInstance instance && instance.tag().getItems().stream()
                .anyMatch(stack -> NMFoodSpoilage.isPerishable(stack) && stack.getItemDamage() != Short.MAX_VALUE)) {
            Tag tag = foodTags.computeIfAbsent(instance.tag(), source -> {
                Tag copy = Tag.of(new ResourceLocation(NMFields.modID,
                        "food_freshness/" + source.id.getResourceDomain() + "/" + source.id.getResourcePath()));
                for (ItemStack stack : source.getItems()) {
                    ItemStack value = stack.copy();
                    if (NMFoodSpoilage.isPerishable(value)) value.setItemDamage(Short.MAX_VALUE);
                    copy.add(value);
                }
                return copy;
            });
            return TagInstance.of(tag, instance.stackSize());
        }
        return ingredient;
    }
}
