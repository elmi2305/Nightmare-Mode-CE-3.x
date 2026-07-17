package com.itlesports.nightmaremode.skill;

import com.itlesports.nightmaremode.skill.reward.SkillReward;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public class SkillNode {
    public final ResourceLocation id;
    public final String name;
    public final String requirementText;
    public final ItemStack icon;
    public final int displayColumn;
    public final int displayRow;
    public final SkillNode[] parents;
    public final SkillCondition triggerCondition;
    public final SkillUnlockAction onUnlockConsume;
    public final SkillReward reward;
    public final boolean worldReward;
    public SkillBranch branch;

    SkillNode(ResourceLocation id, String name, String requirementText, ItemStack icon, int displayColumn, int displayRow,
              SkillNode[] parents, SkillCondition triggerCondition, SkillUnlockAction onUnlockConsume, SkillReward reward, boolean worldReward) {
        this.id = id;
        this.name = name;
        this.requirementText = requirementText;
        this.icon = icon;
        this.displayColumn = displayColumn;
        this.displayRow = displayRow;
        this.parents = parents;
        this.triggerCondition = triggerCondition;
        this.onUnlockConsume = onUnlockConsume;
        this.reward = reward;
        this.worldReward = worldReward;
    }

    public SkillNode register(SkillBranch branch) {
        this.branch = branch;
        branch.add(this);
        SkillRegistry.registerNode(this);
        return this;
    }

    public static ItemStack stack(Item item) {
        return new ItemStack(item);
    }

    public static ItemStack stack(Block block) {
        return new ItemStack(block);
    }
}
