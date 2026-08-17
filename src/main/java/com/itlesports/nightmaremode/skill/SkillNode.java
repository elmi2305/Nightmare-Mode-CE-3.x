package com.itlesports.nightmaremode.skill;

import com.itlesports.nightmaremode.skill.reward.SkillReward;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SkillNode {
    public final ResourceLocation id;
    public final String name;
    public final String requirementText;
    public final ItemStack icon;
    public final int displayColumn;
    public final int displayRow;
    public SkillNode[] parents;
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

    /**
     * Adds prerequisite nodes after every skill node has been initialized.
     * This is the safe API for cross-tab and forward-reference parent changes.
     */
    public SkillNode addParents(SkillNode... additionalParents) {
        for (SkillNode parent : additionalParents) {
            if (parent == null) {
                throw new IllegalArgumentException("Skill parents cannot be null");
            }
            if (parent == this || hasAncestor(parent, this, new HashSet<SkillNode>())) {
                throw new IllegalArgumentException("Adding " + parent.id + " as a parent of " + id + " would create a cycle");
            }
            if (hasDirectParent(parent)) {
                continue;
            }

            int previousLength = parents.length;
            parents = Arrays.copyOf(parents, previousLength + 1);
            parents[previousLength] = parent;
        }
        return this;
    }

    private boolean hasDirectParent(SkillNode candidate) {
        for (SkillNode parent : parents) {
            if (parent == candidate) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasAncestor(SkillNode node, SkillNode candidate, Set<SkillNode> visited) {
        if (node == candidate) {
            return true;
        }
        if (!visited.add(node)) {
            return false;
        }
        for (SkillNode parent : node.parents) {
            if (hasAncestor(parent, candidate, visited)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack stack(Item item) {
        return new ItemStack(item);
    }

    public static ItemStack stack(Block block) {
        return new ItemStack(block);
    }
}
