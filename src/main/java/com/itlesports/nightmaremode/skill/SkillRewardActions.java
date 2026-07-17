package com.itlesports.nightmaremode.skill;

import btw.community.nightmaremode.NightmareMode;

public class SkillRewardActions {
    public static SkillUnlockAction addBlockBreakSpeed(float amount) {
        return (player, world) -> {
            SkillTreeData data = player.getData(NightmareMode.SKILL_TREE);
            data.blockBreakSpeedBonus += amount;
            player.setData(NightmareMode.SKILL_TREE, data);
        };
    }

    public static SkillUnlockAction addMobLootChance(float amount) {
        return (player, world) -> {
            SkillTreeData data = player.getData(NightmareMode.SKILL_TREE);
            data.mobLootChanceBonus += amount;
            player.setData(NightmareMode.SKILL_TREE, data);
        };
    }

    public static SkillUnlockAction unlockDiamondHarvest() {
        return (player, world) -> {
            SkillTreeData data = player.getData(NightmareMode.SKILL_TREE);
            data.canHarvestDiamondOre = true;
            player.setData(NightmareMode.SKILL_TREE, data);
        };
    }

    public static SkillUnlockAction unlockVillagerCuring() {
        return (player, world) -> {
            SkillTreeData data = player.getData(NightmareMode.SKILL_TREE);
            data.canCureVillagers = true;
            player.setData(NightmareMode.SKILL_TREE, data);
        };
    }

    public static SkillUnlockAction slowFoodSpoilage() {
        return (player, world) -> {
            SkillTreeData data = player.getData(NightmareMode.SKILL_TREE);
            data.foodSpoilsSlower = true;
            player.setData(NightmareMode.SKILL_TREE, data);
        };
    }

    public static SkillUnlockAction unlockNetherAccess() {
        return (player, world) -> {
            WorldSkillData data = world.getData(NightmareMode.WORLD_SKILL_TREE);
            data.netherAccessUnlocked = true;
            world.setData(NightmareMode.WORLD_SKILL_TREE, data);
        };
    }

    public static SkillUnlockAction disableWoodGravity() {
        return (player, world) -> {
            WorldSkillData data = world.getData(NightmareMode.WORLD_SKILL_TREE);
            data.woodBlocksIgnoreSkybaseGravity = true;
            world.setData(NightmareMode.WORLD_SKILL_TREE, data);
        };
    }
}
