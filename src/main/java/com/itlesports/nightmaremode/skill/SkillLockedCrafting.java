package com.itlesports.nightmaremode.skill;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IRecipe;
import net.minecraft.src.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class SkillLockedCrafting {
    private static final Map<IRecipe, List<SkillNode>> REQUIRED_SKILLS = new IdentityHashMap<>();
    private static final Map<ResourceLocation, List<SkillNode>> REQUIRED_SKILLS_BY_ID = new HashMap<>();

    private SkillLockedCrafting() {
    }

    public static <T extends IRecipe> T requireSkill(T recipe, SkillNode skill) {
        return requireSkills(recipe, skill);
    }

    public static <T extends IRecipe> T requireSkills(T recipe, SkillNode... skills) {
        if (recipe != null) {
            List<SkillNode> requiredSkills = new ArrayList<>();
            for (SkillNode skill : skills) {
                if (skill != null) {
                    requiredSkills.add(skill);
                }
            }
            if (!requiredSkills.isEmpty()) {
                REQUIRED_SKILLS.put(recipe, requiredSkills);
                REQUIRED_SKILLS_BY_ID.put(recipe.getId(), requiredSkills);
            }
        }
        return recipe;
    }

    public static boolean isLocked(EntityPlayer player, IRecipe recipe) {
        for (SkillNode skill : getRequiredSkills(recipe)) {
            if (!SkillHandler.isUnlocked(player, skill)) {
                return true;
            }
        }
        return false;
    }

    public static void notifyLocked(EntityPlayer player, IRecipe recipe) {
        for (SkillNode skill : getRequiredSkills(recipe)) {
            if (!SkillHandler.isUnlocked(player, skill)) {
                SkillHandler.sendStatus(player, "Requires skill unlock: " + skill.name);
                return;
            }
        }
    }

    public static SkillNode getRequiredSkill(IRecipe recipe) {
        List<SkillNode> skills = getRequiredSkills(recipe);
        return skills.isEmpty() ? null : skills.get(0);
    }

    public static SkillNode getRequiredSkill(ResourceLocation recipeId) {
        List<SkillNode> skills = getRequiredSkills(recipeId);
        return skills.isEmpty() ? null : skills.get(0);
    }

    public static List<SkillNode> getRequiredSkills(IRecipe recipe) {
        return REQUIRED_SKILLS.getOrDefault(recipe, List.of());
    }

    public static List<SkillNode> getRequiredSkills(ResourceLocation recipeId) {
        return REQUIRED_SKILLS_BY_ID.getOrDefault(recipeId, List.of());
    }
}
