package com.itlesports.nightmaremode.world;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.skill.SkillNode;
import com.itlesports.nightmaremode.skill.SkillRegistry;
import com.itlesports.nightmaremode.skill.SkillTreeData;
import com.itlesports.nightmaremode.skill.WorldSkillData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public final class SandboxRules {
    private SandboxRules() { }

    public static boolean isSandbox(World world) {
        return NightmareMode.lockDownCreative && world != null
                && world.getWorldInfo().getGameType() == EnumGameType.CREATIVE;
    }

    public static boolean isPrivateSingleplayer() {
        MinecraftServer server = MinecraftServer.getServer();
        return server instanceof IntegratedServer && !((IntegratedServer)server).getPublic();
    }

    public static boolean mayCreate(World world, ItemStack stack) {
        if (!NightmareMode.lockDownCreative || world == null) return true;
        if (stack == null) return true;
        JourneyProfile profile = world.getData(NightmareMode.JOURNEY_PROFILE);
        return profile != null && profile.hasItem(stack);
    }

    public static void recordAcquisition(EntityPlayer player, ItemStack stack) {
        if (!NightmareMode.lockDownCreative || player == null || stack == null
                || player.worldObj == null || player.worldObj.isRemote
                || player.capabilities.isCreativeMode || isSandbox(player.worldObj)
                || !isPrivateSingleplayer()) return;
        JourneyProfile profile = JourneyProfile.getOrCreate(player.worldObj);
        if (profile.recordItem(stack)) player.worldObj.setData(NightmareMode.JOURNEY_PROFILE, profile);
    }

    public static long uniqueSeed(ISaveFormat saves, long proposed) {
        if (!NightmareMode.lockDownCreative) return proposed;
        try {
            List worlds = saves.getSaveList();
            Random random = new Random();
            long seed = proposed;
            boolean duplicate;
            do {
                duplicate = false;
                for (Object entry : worlds) {
                    SaveFormatComparator save = (SaveFormatComparator)entry;
                    WorldInfo info = saves.getWorldInfo(save.getFileName());
                    if (info != null && info.getSeed() == seed) {
                        seed = random.nextLong();
                        duplicate = true;
                        break;
                    }
                }
            } while (duplicate);
            return seed;
        } catch (AnvilConverterException ignored) {
            return proposed;
        }
    }

    public static void inheritSkills(EntityPlayerMP player) {
        if (!isSandbox(player.worldObj) || !isPrivateSingleplayer()) return;
        MinecraftServer server = MinecraftServer.getServer();
        JourneyProfile best = null;
        JourneyProfile sandboxProfile = JourneyProfile.getOrCreate(player.worldObj);
        boolean catalogChanged = false;
        try {
            ISaveFormat saves = server.getActiveAnvilConverter();
            for (Object entry : saves.getSaveList()) {
                SaveFormatComparator save = (SaveFormatComparator)entry;
                WorldInfo info = saves.getWorldInfo(save.getFileName());
                if (info == null || info.getGameType() == EnumGameType.CREATIVE) continue;
                JourneyProfile candidate = info.getData(NightmareMode.JOURNEY_PROFILE);
                if (candidate != null && candidate.valid) catalogChanged |= sandboxProfile.addItemsFrom(candidate);
                if (candidate != null && candidate.valid && (best == null
                        || candidate.getCompletedSkillCount() > best.getCompletedSkillCount())) {
                    best = candidate;
                }
            }
        } catch (AnvilConverterException ignored) {
            return;
        }
        if (catalogChanged) player.worldObj.setData(NightmareMode.JOURNEY_PROFILE, sandboxProfile);
        if (best == null) return;
        SkillTreeData playerData = player.getData(NightmareMode.SKILL_TREE);
        WorldSkillData worldData = player.worldObj.getData(NightmareMode.WORLD_SKILL_TREE);
        for (SkillNode node : SkillRegistry.getNodes()) {
            if (!best.hasCompletedSkill(node.id.toString())) continue;
            if (node.worldReward) {
                if (worldData.isUnlocked(node)) continue;
                worldData.unlock(node);
            } else {
                if (playerData.isUnlocked(node)) continue;
                playerData.unlock(node);
            }
            node.reward.getAction().apply(player, player.worldObj);
        }
        player.setData(NightmareMode.SKILL_TREE, playerData);
        player.worldObj.setData(NightmareMode.WORLD_SKILL_TREE, worldData);
    }
}
