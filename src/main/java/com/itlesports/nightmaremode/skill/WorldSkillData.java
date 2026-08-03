package com.itlesports.nightmaremode.skill;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorldSkillData {
    public boolean woodBlocksIgnoreSkybaseGravity;
    public boolean netherAccessUnlocked;
    public boolean fireSpreadsSlower;
    public boolean witherSummoningUnlocked;
    public boolean endAccessUnlocked;
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

    public static WorldSkillData readFromNBT(NBTTagCompound tag) {
        WorldSkillData data = new WorldSkillData();
        data.woodBlocksIgnoreSkybaseGravity = tag.getBoolean("WoodBlocksIgnoreSkybaseGravity");
        data.netherAccessUnlocked = tag.getBoolean("NetherAccessUnlocked");
        data.fireSpreadsSlower = tag.getBoolean("FireSpreadsSlower");
        data.witherSummoningUnlocked = tag.getBoolean("WitherSummoningUnlocked");
        data.endAccessUnlocked = tag.getBoolean("EndAccessUnlocked");
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
        NBTTagList unlocked = tag.getTagList("UnlockedWorldNodes");
        for (int i = 0; i < unlocked.tagCount(); ++i) {
            data.unlockedWorldNodes.add(((NBTTagString)unlocked.tagAt(i)).data);
        }
        NBTTagList postCompletions = tag.getTagList("NetherPostCompletions");
        for (int i = 0; i < postCompletions.tagCount(); ++i) {
            NBTTagCompound completion = (NBTTagCompound)postCompletions.tagAt(i);
            data.netherPostCompletionMasks.put(completion.getString("Key"), completion.getInteger("Mask"));
        }
        return data;
    }

    public static void writeToNBT(NBTTagCompound tag, WorldSkillData data) {
        tag.setBoolean("WoodBlocksIgnoreSkybaseGravity", data.woodBlocksIgnoreSkybaseGravity);
        tag.setBoolean("NetherAccessUnlocked", data.netherAccessUnlocked);
        tag.setBoolean("FireSpreadsSlower", data.fireSpreadsSlower);
        tag.setBoolean("WitherSummoningUnlocked", data.witherSummoningUnlocked);
        tag.setBoolean("EndAccessUnlocked", data.endAccessUnlocked);
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
