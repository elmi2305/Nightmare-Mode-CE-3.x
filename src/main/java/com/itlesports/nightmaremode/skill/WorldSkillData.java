package com.itlesports.nightmaremode.skill;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorldSkillData {
    public String rewardsVersion = SkillRewardReload.LEGACY_VERSION;
    public int rewardsSchema;
    public long rewardsRevision;
    public boolean woodBlocksIgnoreSkybaseGravity;
    public boolean netherAccessUnlocked;
    public boolean fireSpreadsSlower;
    public boolean witherSummoningUnlocked;
    public boolean endAccessUnlocked;
    public boolean diamondExtractionUnlocked;
    public boolean netherVillagerTier1Complete;
    public boolean netherVillagerTier2Complete;
    public boolean netherVillagerTier3Complete;
    public int woodGravityUnlockProgress;
    public int netherAccessUnlockProgress;
    public int witherSummonUnlockProgress;
    public int endAccessUnlockProgress;
    public float globalIronPileChanceBonus;
    public float globalFoodSpoilageRateMultiplier = 1.0F;
    public float globalVillagerHungerDrainRateMultiplier = 1.0F;
    public float globalMobLootChanceBonus;
    public float globalXpGainBonus;
    public float globalPollutionReduction;
    private final Set<String> unlockedWorldNodes = new HashSet<>();
    private final Map<String, Integer> netherPostCompletionMasks = new HashMap<>();

    public int markNetherPostVillagerComplete(int tier, String postGroup, int villagerSlot) {
        if (postGroup == null || postGroup.length() == 0 || villagerSlot < 0 || villagerSlot > 3) {
            return 0;
        }
        String key = tier + "@" + postGroup;
        int mask = this.netherPostCompletionMasks.getOrDefault(key, 0) | 1 << villagerSlot;
        this.netherPostCompletionMasks.put(key, mask);
        return mask;
    }

    public void mergeNetherPosts(WorldSkillData other) {
        this.netherVillagerTier1Complete |= other.netherVillagerTier1Complete;
        this.netherVillagerTier2Complete |= other.netherVillagerTier2Complete;
        this.netherVillagerTier3Complete |= other.netherVillagerTier3Complete;
        for (Map.Entry<String, Integer> entry : other.netherPostCompletionMasks.entrySet()) {
            this.netherPostCompletionMasks.merge(entry.getKey(), entry.getValue(), (a, b) -> a | b);
        }
        this.restoreCompletedPostFlags();
    }

    private void restoreCompletedPostFlags() {
        for (Map.Entry<String, Integer> entry : this.netherPostCompletionMasks.entrySet()) {
            if ((entry.getValue() & 15) != 15) continue;
            if (entry.getKey().startsWith("1@")) this.netherVillagerTier1Complete = true;
            if (entry.getKey().startsWith("2@")) this.netherVillagerTier2Complete = true;
            if (entry.getKey().startsWith("3@")) this.netherVillagerTier3Complete = true;
        }
    }

    public boolean isUnlocked(SkillNode node) {
        return node != null && this.unlockedWorldNodes.contains(node.id.toString());
    }

    public boolean isUnlocked(String nodeId) {
        return this.unlockedWorldNodes.contains(nodeId);
    }

    public void unlock(SkillNode node) {
        if (node != null) {
            this.unlockedWorldNodes.add(node.id.toString());
        }
    }

    /** Reset derived rewards only; retain unlocks, counters and quest progress. */
    public void resetRewards() {
        WorldSkillData defaults = new WorldSkillData();
        this.woodBlocksIgnoreSkybaseGravity = defaults.woodBlocksIgnoreSkybaseGravity;
        this.netherAccessUnlocked = defaults.netherAccessUnlocked;
        this.fireSpreadsSlower = defaults.fireSpreadsSlower;
        this.witherSummoningUnlocked = defaults.witherSummoningUnlocked;
        this.endAccessUnlocked = defaults.endAccessUnlocked;
        this.woodGravityUnlockProgress = defaults.woodGravityUnlockProgress;
        this.netherAccessUnlockProgress = defaults.netherAccessUnlockProgress;
        this.witherSummonUnlockProgress = defaults.witherSummonUnlockProgress;
        this.endAccessUnlockProgress = defaults.endAccessUnlockProgress;
        this.globalIronPileChanceBonus = defaults.globalIronPileChanceBonus;
        this.globalFoodSpoilageRateMultiplier = defaults.globalFoodSpoilageRateMultiplier;
        this.globalVillagerHungerDrainRateMultiplier = defaults.globalVillagerHungerDrainRateMultiplier;
        this.globalMobLootChanceBonus = defaults.globalMobLootChanceBonus;
        this.globalXpGainBonus = defaults.globalXpGainBonus;
        this.globalPollutionReduction = defaults.globalPollutionReduction;
    }

    public static WorldSkillData readFromNBT(NBTTagCompound tag) {
        WorldSkillData data = new WorldSkillData();
        data.rewardsVersion = SkillRewardReload.savedVersion(tag.getString("RewardsVersion"));
        data.rewardsSchema = tag.getInteger("RewardsSchema");
        data.rewardsRevision = tag.getLong("RewardsRevision");
        data.woodBlocksIgnoreSkybaseGravity = tag.getBoolean("WoodBlocksIgnoreSkybaseGravity");
        data.netherAccessUnlocked = tag.getBoolean("NetherAccessUnlocked");
        data.fireSpreadsSlower = tag.getBoolean("FireSpreadsSlower");
        data.witherSummoningUnlocked = tag.getBoolean("WitherSummoningUnlocked");
        data.endAccessUnlocked = tag.getBoolean("EndAccessUnlocked");
        data.diamondExtractionUnlocked = tag.getBoolean("DiamondExtractionUnlocked");
        data.netherVillagerTier1Complete = tag.getBoolean("NetherVillagerTier1Complete");
        data.netherVillagerTier2Complete = tag.getBoolean("NetherVillagerTier2Complete");
        data.netherVillagerTier3Complete = tag.getBoolean("NetherVillagerTier3Complete");
        data.woodGravityUnlockProgress = tag.getInteger("WoodGravityUnlockProgress");
        data.netherAccessUnlockProgress = tag.getInteger("NetherAccessUnlockProgress");
        data.witherSummonUnlockProgress = tag.getInteger("WitherSummonUnlockProgress");
        data.endAccessUnlockProgress = tag.getInteger("EndAccessUnlockProgress");
        data.globalIronPileChanceBonus = tag.getFloat("GlobalIronPileChanceBonus");
        data.globalFoodSpoilageRateMultiplier = tag.hasKey("GlobalFoodSpoilageRateMultiplier") ? tag.getFloat("GlobalFoodSpoilageRateMultiplier") : 1.0F;
        data.globalVillagerHungerDrainRateMultiplier = tag.hasKey("GlobalVillagerHungerDrainRateMultiplier") ? tag.getFloat("GlobalVillagerHungerDrainRateMultiplier") : 1.0F;
        data.globalMobLootChanceBonus = tag.getFloat("GlobalMobLootChanceBonus");
        data.globalXpGainBonus = tag.getFloat("GlobalXpGainBonus");
        data.globalPollutionReduction = tag.getFloat("GlobalPollutionReduction");
        NBTTagList unlocked = tag.getTagList("UnlockedWorldNodes");
        for (int i = 0; i < unlocked.tagCount(); ++i) {
            data.unlockedWorldNodes.add(((NBTTagString)unlocked.tagAt(i)).data);
        }
        if (tag.getInteger("NetherContributionVersion") < 1) {
            // move the legacy hammer contribution to iron sample without replaying other rewards.
            boolean ironSample = data.isUnlocked("nightmare:skill/iron_sample");
            boolean diamondHammer = data.isUnlocked("nightmare:skill/nether_diamond_hammer");
            data.netherAccessUnlockProgress = Math.max(0, data.netherAccessUnlockProgress
                    + (ironSample ? 1 : 0) - (diamondHammer ? 1 : 0));
            if (diamondHammer) {
                data.globalIronPileChanceBonus += 0.05F;
            }
            // never revoke access from a world that already opened the nether.
            data.netherAccessUnlocked |= data.netherAccessUnlockProgress >= SkillRewardActions.NETHER_ACCESS_PROGRESS_REQUIRED;
        }
        NBTTagList postCompletions = tag.getTagList("NetherPostCompletions");
        for (int i = 0; i < postCompletions.tagCount(); ++i) {
            NBTTagCompound completion = (NBTTagCompound)postCompletions.tagAt(i);
            data.netherPostCompletionMasks.put(completion.getString("Key"), completion.getInteger("Mask"));
        }
        data.restoreCompletedPostFlags();
        return data;
    }

    public static void writeToNBT(NBTTagCompound tag, WorldSkillData data) {
        tag.setString("RewardsVersion", SkillRewardReload.savedVersion(data.rewardsVersion));
        tag.setInteger("RewardsSchema", data.rewardsSchema);
        tag.setLong("RewardsRevision", data.rewardsRevision);
        tag.setInteger("NetherContributionVersion", 1);
        tag.setBoolean("WoodBlocksIgnoreSkybaseGravity", data.woodBlocksIgnoreSkybaseGravity);
        tag.setBoolean("NetherAccessUnlocked", data.netherAccessUnlocked);
        tag.setBoolean("FireSpreadsSlower", data.fireSpreadsSlower);
        tag.setBoolean("WitherSummoningUnlocked", data.witherSummoningUnlocked);
        tag.setBoolean("EndAccessUnlocked", data.endAccessUnlocked);
        tag.setBoolean("DiamondExtractionUnlocked", data.diamondExtractionUnlocked);
        tag.setBoolean("NetherVillagerTier1Complete", data.netherVillagerTier1Complete);
        tag.setBoolean("NetherVillagerTier2Complete", data.netherVillagerTier2Complete);
        tag.setBoolean("NetherVillagerTier3Complete", data.netherVillagerTier3Complete);
        tag.setInteger("WoodGravityUnlockProgress", data.woodGravityUnlockProgress);
        tag.setInteger("NetherAccessUnlockProgress", data.netherAccessUnlockProgress);
        tag.setInteger("WitherSummonUnlockProgress", data.witherSummonUnlockProgress);
        tag.setInteger("EndAccessUnlockProgress", data.endAccessUnlockProgress);
        tag.setFloat("GlobalIronPileChanceBonus", data.globalIronPileChanceBonus);
        tag.setFloat("GlobalFoodSpoilageRateMultiplier", data.globalFoodSpoilageRateMultiplier);
        tag.setFloat("GlobalVillagerHungerDrainRateMultiplier", data.globalVillagerHungerDrainRateMultiplier);
        tag.setFloat("GlobalMobLootChanceBonus", data.globalMobLootChanceBonus);
        tag.setFloat("GlobalXpGainBonus", data.globalXpGainBonus);
        tag.setFloat("GlobalPollutionReduction", data.globalPollutionReduction);
        NBTTagList unlocked = new NBTTagList("UnlockedWorldNodes");
        for (String id : data.unlockedWorldNodes) {
            unlocked.appendTag(new NBTTagString("", id));
        }
        tag.setTag("UnlockedWorldNodes", unlocked);
        NBTTagList postCompletions = new NBTTagList("NetherPostCompletions");
        for (Map.Entry<String, Integer> entry : data.netherPostCompletionMasks.entrySet()) {
            NBTTagCompound completion = new NBTTagCompound();
            completion.setString("Key", entry.getKey());
            completion.setInteger("Mask", entry.getValue());
            postCompletions.appendTag(completion);
        }
        tag.setTag("NetherPostCompletions", postCompletions);
    }
}
