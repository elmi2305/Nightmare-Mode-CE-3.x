package com.itlesports.nightmaremode.skill;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;

import java.util.HashSet;
import java.util.Set;

public class WorldSkillData {
    public boolean woodBlocksIgnoreSkybaseGravity;
    public boolean netherAccessUnlocked;
    public boolean fireSpreadsSlower;
    private final Set<String> unlockedWorldNodes = new HashSet<>();

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
        NBTTagList unlocked = tag.getTagList("UnlockedWorldNodes");
        for (int i = 0; i < unlocked.tagCount(); ++i) {
            data.unlockedWorldNodes.add(((NBTTagString)unlocked.tagAt(i)).data);
        }
        return data;
    }

    public static void writeToNBT(NBTTagCompound tag, WorldSkillData data) {
        tag.setBoolean("WoodBlocksIgnoreSkybaseGravity", data.woodBlocksIgnoreSkybaseGravity);
        tag.setBoolean("NetherAccessUnlocked", data.netherAccessUnlocked);
        tag.setBoolean("FireSpreadsSlower", data.fireSpreadsSlower);
        NBTTagList unlocked = new NBTTagList("UnlockedWorldNodes");
        for (String id : data.unlockedWorldNodes) {
            unlocked.appendTag(new NBTTagString("", id));
        }
        tag.setTag("UnlockedWorldNodes", unlocked);
    }
}
