package com.itlesports.nightmaremode.skill;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IRecipe;

import java.util.IdentityHashMap;
import java.util.Map;

public final class SkillLockedCrafting {
    private static final Map<IRecipe, SkillNode> REQUIRED_SKILLS = new IdentityHashMap<>();

    private SkillLockedCrafting() {
    }

    public static <T extends IRecipe> T requireSkill(T recipe, SkillNode skill) {
        if (recipe != null && skill != null) {
            REQUIRED_SKILLS.put(recipe, skill);
        }
        return recipe;
    }

    public static boolean isLocked(EntityPlayer player, IRecipe recipe) {
        SkillNode skill = REQUIRED_SKILLS.get(recipe);
        return skill != null && !SkillHandler.isUnlocked(player, skill);
    }

    public static void notifyLocked(EntityPlayer player, IRecipe recipe) {
        SkillNode skill = REQUIRED_SKILLS.get(recipe);
        if (skill != null) {
            SkillHandler.sendStatus(player, "Requires skill unlock: " + skill.name);
        }
    }
}
