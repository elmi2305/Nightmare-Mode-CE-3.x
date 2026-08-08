package com.itlesports.nightmaremode.skill;

import api.crafting.IdentifiableRecipe;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IRecipe;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class SkillLockedCrafting {
    private static final Map<Object, List<SkillNode>> REQUIRED_SKILLS = new IdentityHashMap<>();
    private static final Map<ResourceLocation, List<SkillNode>> REQUIRED_SKILLS_BY_ID = new HashMap<>();

    private SkillLockedCrafting() {
    }

    public static <T> T requireSkill(T recipe, SkillNode skill) {
        return requireSkills(recipe, skill);
    }

    public static <T> T requireSkills(T recipe, SkillNode... skills) {
        if (recipe != null) {
            List<SkillNode> requiredSkills = new ArrayList<>(REQUIRED_SKILLS.getOrDefault(recipe, List.of()));
            for (SkillNode skill : skills) {
                if (skill != null && !requiredSkills.contains(skill)) {
                    requiredSkills.add(skill);
                }
            }
            if (!requiredSkills.isEmpty()) {
                REQUIRED_SKILLS.put(recipe, requiredSkills);
                if (recipe instanceof IdentifiableRecipe<?> identifiableRecipe) {
                    REQUIRED_SKILLS_BY_ID.put(identifiableRecipe.getId(), requiredSkills);
                }
            }
        }
        return recipe;
    }

    /**
     * Autonomous machine recipes have no crafting player.  They may therefore
     * only depend on world-reward nodes, which are deterministic for every
     * hopper, vessel, and player in the dimension.
     */
    public static <T> T requireWorldSkills(T recipe, SkillNode... skills) {
        for (SkillNode skill : skills) {
            if (skill != null && !skill.worldReward) {
                throw new IllegalArgumentException(
                        "Autonomous recipe skill must be a world reward: " + skill.id);
            }
        }
        return requireSkills(recipe, skills);
    }

    public static boolean isLocked(EntityPlayer player, IRecipe recipe) {
        for (SkillNode skill : getRequiredSkills(recipe)) {
            if (!SkillHandler.isUnlocked(player, skill)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWorldLocked(World world, Object recipe) {
        for (SkillNode skill : getRequiredSkills(recipe)) {
            if (!skill.worldReward || !SkillHandler.isWorldUnlocked(world, skill)) {
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

    public static List<SkillNode> getRequiredSkills(Object recipe) {
        return REQUIRED_SKILLS.getOrDefault(recipe, List.of());
    }

    public static List<SkillNode> getRequiredSkills(ResourceLocation recipeId) {
        return REQUIRED_SKILLS_BY_ID.getOrDefault(recipeId, List.of());
    }
}
