package com.itlesports.nightmaremode.skill.reward;

import com.itlesports.nightmaremode.skill.SkillUnlockAction;

public class SkillReward {
    private final String text;
    private final SkillUnlockAction action;

    public SkillReward(String text, SkillUnlockAction action) {
        this.text = text;
        this.action = action;
    }

    public String getText() {
        return this.text;
    }

    public SkillUnlockAction getAction() {
        return this.action;
    }
}
