package com.itlesports.nightmaremode.scary;

import api.achievement.AchievementTab;
import api.achievement.AchievementTabList;
import net.minecraft.src.Achievement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class ScaryAchievements {
    private ScaryAchievements() {}

    public static Achievement pick(Random random) {
        Set<Achievement> registered = new LinkedHashSet<>();
        for (AchievementTab tab : AchievementTabList.tabList) registered.addAll(tab.achievementList);
        List<Achievement> choices = new ArrayList<>(registered);
        return choices.isEmpty() ? null : choices.get(random.nextInt(choices.size()));
    }
}
