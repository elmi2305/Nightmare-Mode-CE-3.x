package com.itlesports.nightmaremode.skill;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;

import java.util.HashSet;
import java.util.Set;

public class SkillTreeData {
    public int blocksMined;
    public int coalOreMined;
    public int ironOreMined;
    public int diamondOreMined;
    public int animalsTamed;
    public int animalsBred;
    public int mobsKilled;
    public int zombiesKilled;
    public int skeletonsKilled;
    public int fishCaught;
    public int rareItemsCaught;
    public int foodCooked;
    public int booksCrafted;
    public int potionsBrewed;
    public float blockBreakSpeedBonus;
    public float mobLootChanceBonus;
    public boolean canHarvestDiamondOre;
    public boolean canCureVillagers;
    public boolean foodSpoilsSlower;
    private final Set<String> unlockedNodes = new HashSet<>();

    public boolean isUnlocked(SkillNode node) {
        return node != null && this.unlockedNodes.contains(node.id.toString());
    }

    public boolean isUnlocked(String nodeId) {
        return this.unlockedNodes.contains(nodeId);
    }

    public void unlock(SkillNode node) {
        if (node != null) {
            this.unlockedNodes.add(node.id.toString());
        }
    }

    public Set<String> getUnlockedNodes() {
        return this.unlockedNodes;
    }

    public static SkillTreeData readFromNBT(NBTTagCompound tag) {
        SkillTreeData data = new SkillTreeData();
        data.blocksMined = tag.getInteger("BlocksMined");
        data.coalOreMined = tag.getInteger("CoalOreMined");
        data.ironOreMined = tag.getInteger("IronOreMined");
        data.diamondOreMined = tag.getInteger("DiamondOreMined");
        data.animalsTamed = tag.getInteger("AnimalsTamed");
        data.animalsBred = tag.getInteger("AnimalsBred");
        data.mobsKilled = tag.getInteger("MobsKilled");
        data.zombiesKilled = tag.getInteger("ZombiesKilled");
        data.skeletonsKilled = tag.getInteger("SkeletonsKilled");
        data.fishCaught = tag.getInteger("FishCaught");
        data.rareItemsCaught = tag.getInteger("RareItemsCaught");
        data.foodCooked = tag.getInteger("FoodCooked");
        data.booksCrafted = tag.getInteger("BooksCrafted");
        data.potionsBrewed = tag.getInteger("PotionsBrewed");
        data.blockBreakSpeedBonus = tag.getFloat("BlockBreakSpeedBonus");
        data.mobLootChanceBonus = tag.getFloat("MobLootChanceBonus");
        data.canHarvestDiamondOre = tag.getBoolean("CanHarvestDiamondOre");
        data.canCureVillagers = tag.getBoolean("CanCureVillagers");
        data.foodSpoilsSlower = tag.getBoolean("FoodSpoilsSlower");
        NBTTagList unlocked = tag.getTagList("UnlockedNodes");
        for (int i = 0; i < unlocked.tagCount(); ++i) {
            data.unlockedNodes.add(((NBTTagString)unlocked.tagAt(i)).data);
        }
        return data;
    }

    public static void writeToNBT(NBTTagCompound tag, SkillTreeData data) {
        tag.setInteger("BlocksMined", data.blocksMined);
        tag.setInteger("CoalOreMined", data.coalOreMined);
        tag.setInteger("IronOreMined", data.ironOreMined);
        tag.setInteger("DiamondOreMined", data.diamondOreMined);
        tag.setInteger("AnimalsTamed", data.animalsTamed);
        tag.setInteger("AnimalsBred", data.animalsBred);
        tag.setInteger("MobsKilled", data.mobsKilled);
        tag.setInteger("ZombiesKilled", data.zombiesKilled);
        tag.setInteger("SkeletonsKilled", data.skeletonsKilled);
        tag.setInteger("FishCaught", data.fishCaught);
        tag.setInteger("RareItemsCaught", data.rareItemsCaught);
        tag.setInteger("FoodCooked", data.foodCooked);
        tag.setInteger("BooksCrafted", data.booksCrafted);
        tag.setInteger("PotionsBrewed", data.potionsBrewed);
        tag.setFloat("BlockBreakSpeedBonus", data.blockBreakSpeedBonus);
        tag.setFloat("MobLootChanceBonus", data.mobLootChanceBonus);
        tag.setBoolean("CanHarvestDiamondOre", data.canHarvestDiamondOre);
        tag.setBoolean("CanCureVillagers", data.canCureVillagers);
        tag.setBoolean("FoodSpoilsSlower", data.foodSpoilsSlower);
        NBTTagList unlocked = new NBTTagList("UnlockedNodes");
        for (String id : data.unlockedNodes) {
            unlocked.appendTag(new NBTTagString("", id));
        }
        tag.setTag("UnlockedNodes", unlocked);
    }
}
