package com.itlesports.nightmaremode.achievements;

import net.minecraft.src.Achievement;
import net.minecraft.src.ItemStack;

import java.util.function.Predicate;

public interface AchievementExt {
    void nightmareMode$appendParent(Achievement achievementToAdd);
    void nightmareMode$setDisplay(int row, int column);
    Achievement[] nightmareMode$removeParent(Achievement[] original, Achievement toRemove);
    void nightmareMode$setIcon(ItemStack stack);
    void nightmareMode$setPredicate(Predicate predicate);
}
