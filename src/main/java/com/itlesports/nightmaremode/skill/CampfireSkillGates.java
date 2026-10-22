package com.itlesports.nightmaremode.skill;

import api.crafting.IdentifiableRecipe;
import btw.crafting.manager.CampfireCraftingManager;
import emi.shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.World;

import java.util.HashMap;
import java.util.Map;

public final class CampfireSkillGates {
    private static final Map<Integer, Recipe> RECIPES = new HashMap<>();

    private CampfireSkillGates() {
    }

    public static void require(int inputId, SkillNode... skills) {
        ItemStack output = CampfireCraftingManager.instance.getRecipeResult(inputId);
        if (output == null) throw new IllegalArgumentException("Missing campfire recipe: " + inputId);
        Recipe recipe = RECIPES.computeIfAbsent(inputId, id -> new Recipe(new ResourceLocation(
                "campfire:/" + SyntheticIdentifier.describe(new ItemStack(id, 1, 0))
                        + "/" + SyntheticIdentifier.describe(output))));
        SkillLockedCrafting.requireWorldSkills(recipe, skills);
    }

    public static boolean isLocked(World world, int inputId) {
        Recipe recipe = RECIPES.get(inputId);
        return recipe != null && SkillLockedCrafting.isWorldLocked(world, recipe);
    }

    private record Recipe(ResourceLocation id) implements IdentifiableRecipe<Recipe> {
        @Override
        public ResourceLocation getId() {
            return id;
        }
    }
}
