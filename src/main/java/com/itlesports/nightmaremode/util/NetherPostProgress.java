package com.itlesports.nightmaremode.util;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillTreeData;
import com.itlesports.nightmaremode.skill.WorldSkillData;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraft.server.MinecraftServer;

public final class NetherPostProgress {
    private NetherPostProgress() {}

    public static World savedWorld(World world) {
        MinecraftServer server = MinecraftServer.getServer();
        return !world.isRemote && server != null && server.worldServerForDimension(0) != null
                ? server.worldServerForDimension(0) : world;
    }

    public static WorldSkillData worldData(World world) {
        World saved = savedWorld(world);
        WorldSkillData data = SkillHandler.getWorldData(saved);
        if (saved != world) data.mergeNetherPosts(SkillHandler.getWorldData(world));
        if (!world.isRemote) saved.setData(NightmareMode.WORLD_SKILL_TREE, data);
        return data;
    }

    public static int completedTiers(EntityPlayer player) {
        SkillTreeData data = SkillHandler.getPlayerData(player);
        WorldSkillData legacy = worldData(player.worldObj);
        int completed = data.netherPostCompletedTiers
                | (legacy.netherVillagerTier1Complete ? 1 : 0)
                | (legacy.netherVillagerTier2Complete ? 2 : 0)
                | (legacy.netherVillagerTier3Complete ? 4 : 0);
        if (!player.worldObj.isRemote && completed != data.netherPostCompletedTiers) {
            data.netherPostCompletedTiers = completed;
            player.setData(NightmareMode.SKILL_TREE, data);
        }
        return completed;
    }
}
