package com.itlesports.nightmaremode.skill;

import com.itlesports.nightmaremode.skill.reward.SkillReward;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public class SkillNodeProvider {
    public static NameStep getBuilder() {
        return new SkillNodeBuilder();
    }

    public interface NameStep {
        IconStep id(ResourceLocation id);
    }

    public interface IconStep {
        LocationStep name(String name);
    }

    public interface LocationStep {
        RequirementTextStep icon(Item item);
        RequirementTextStep icon(Block block);
        RequirementTextStep icon(ItemStack stack);
    }

    public interface RequirementTextStep {
        ConditionStep displayLocation(int x, int y);
    }

    public interface ConditionStep {
        RewardStep requirementText(String text);
    }

    public interface RewardStep {
        BuildStep triggerCondition(SkillCondition condition);
        BuildStep alwaysEligible();
    }

    public interface BuildStep {
        BuildStep parents(SkillNode... parents);
        BuildStep onUnlockConsume(SkillUnlockAction action);
        BuildStep reward(String text, SkillUnlockAction action);
        BuildStep worldReward();
        SkillNode build();
    }

    private static class SkillNodeBuilder implements NameStep, IconStep, LocationStep, RequirementTextStep, ConditionStep, RewardStep, BuildStep {
        private ResourceLocation id;
        private String name;
        private ItemStack icon;
        private int displayColumn;
        private int displayRow;
        private String requirementText;
        private SkillCondition condition;
        private SkillNode[] parents = new SkillNode[0];
        private SkillUnlockAction consume;
        private SkillReward reward = new SkillReward("No reward configured.", (player, world) -> {});
        private boolean worldReward;

        @Override
        public IconStep id(ResourceLocation id) {
            this.id = id;
            return this;
        }

        @Override
        public LocationStep name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public RequirementTextStep icon(Item item) {
            return this.icon(new ItemStack(item));
        }

        @Override
        public RequirementTextStep icon(Block block) {
            return this.icon(new ItemStack(block));
        }

        @Override
        public RequirementTextStep icon(ItemStack stack) {
            this.icon = stack;
            return this;
        }

        @Override
        public ConditionStep displayLocation(int x, int y) {
            this.displayColumn = x;
            this.displayRow = y;
            return this;
        }

        @Override
        public RewardStep requirementText(String text) {
            this.requirementText = text;
            return this;
        }

        @Override
        public BuildStep triggerCondition(SkillCondition condition) {
            this.condition = condition;
            return this;
        }

        @Override
        public BuildStep alwaysEligible() {
            this.condition = (player, world) -> true;
            return this;
        }

        @Override
        public BuildStep parents(SkillNode... parents) {
            this.parents = parents;
            return this;
        }

        @Override
        public BuildStep onUnlockConsume(SkillUnlockAction action) {
            this.consume = action;
            return this;
        }

        @Override
        public BuildStep reward(String text, SkillUnlockAction action) {
            this.reward = new SkillReward(text, action);
            return this;
        }

        @Override
        public BuildStep worldReward() {
            this.worldReward = true;
            return this;
        }

        @Override
        public SkillNode build() {
            return new SkillNode(this.id, this.name, this.requirementText, this.icon, this.displayColumn, this.displayRow,
                    this.parents, this.condition, this.consume, this.reward, this.worldReward);
        }
    }
}
