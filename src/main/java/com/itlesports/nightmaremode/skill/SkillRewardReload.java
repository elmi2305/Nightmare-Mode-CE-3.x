package com.itlesports.nightmaremode.skill;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import net.fabricmc.loader.api.FabricLoader;
import java.util.*;

/** Rebuild derived rewards without replaying unlock costs or changing progress. */
public final class SkillRewardReload {
    static final String LEGACY_VERSION = "legacy";
    private static final int SCHEMA = 7;
    private enum ReplayScope { WORLD, PLAYER }
    private static final ThreadLocal<ReplayScope> REPLAYING = new ThreadLocal<>();

    private SkillRewardReload() {}

    public static boolean isReplaying() {
        return REPLAYING.get() != null;
    }

    static boolean isReplayingPlayer() {
        return REPLAYING.get() == ReplayScope.PLAYER;
    }

    /** Missing versions must remain distinguishable from a completed migration. */
    static String savedVersion(String version) {
        return version == null || version.trim().isEmpty() ? LEGACY_VERSION : version;
    }

    private static String version() {
        // The convenience addon singleton is not necessarily the loader-initialized instance.
        return FabricLoader.getInstance().getModContainer("nightmare")
                .map(mod -> mod.getMetadata().getVersion().getFriendlyString())
                .filter(value -> !value.trim().isEmpty())
                .orElse("unavailable");
    }

    public static WorldSkillData validateWorld(World world, boolean force) {
        WorldSkillData old = world.getData(NightmareMode.WORLD_SKILL_TREE);
        if (world.isRemote || isReplaying()
                || !force && old.rewardsSchema == SCHEMA && version().equals(old.rewardsVersion)) return old;
        NBTTagCompound saved = new NBTTagCompound();
        WorldSkillData.writeToNBT(saved, old);
        WorldSkillData rebuilt = WorldSkillData.readFromNBT(saved);
        rebuilt.resetRewards();
        REPLAYING.set(ReplayScope.WORLD);
        try {
            world.setData(NightmareMode.WORLD_SKILL_TREE, rebuilt);
            for (SkillNode node : orderedNodes()) {
                if (node.worldReward && rebuilt.isUnlocked(node)) {
                    node.reward.getAction().apply(null, world);
                }
            }
            rebuilt.rewardsVersion = version();
            rebuilt.rewardsSchema = SCHEMA;
            rebuilt.rewardsRevision = old.rewardsRevision + 1;
            world.setData(NightmareMode.WORLD_SKILL_TREE, rebuilt);
            return rebuilt;
        } catch (RuntimeException exception) {
            world.setData(NightmareMode.WORLD_SKILL_TREE, old);
            throw exception;
        } finally {
            REPLAYING.remove();
        }
    }

    public static SkillTreeData validatePlayer(EntityPlayer player) {
        SkillTreeData old = player.getData(NightmareMode.SKILL_TREE);
        if (player.worldObj == null || player.worldObj.isRemote || isReplaying()) return old;
        WorldSkillData world = validateWorld(player.worldObj, false);
        if (old.rewardsSchema == SCHEMA && version().equals(old.rewardsVersion)
                && old.rewardsRevision == world.rewardsRevision) return old;
        NBTTagCompound saved = new NBTTagCompound();
        SkillTreeData.writeToNBT(saved, old);
        SkillTreeData rebuilt = SkillTreeData.readFromNBT(saved);
        rebuilt.resetRewards();
        REPLAYING.set(ReplayScope.PLAYER);
        try {
            player.setData(NightmareMode.SKILL_TREE, rebuilt);
            for (SkillNode node : orderedNodes()) {
                // world nodes can also grant personal bonuses to the player who unlocked them.
                if (rebuilt.isUnlocked(node)) {
                    node.reward.getAction().apply(player, player.worldObj);
                }
            }
            rebuilt.rewardsVersion = version();
            rebuilt.rewardsSchema = SCHEMA;
            rebuilt.rewardsRevision = world.rewardsRevision;
            player.setData(NightmareMode.SKILL_TREE, rebuilt);
            if (player instanceof EntityPlayerMP serverPlayer) SkillHandler.sync(serverPlayer);
            return rebuilt;
        } catch (RuntimeException exception) {
            player.setData(NightmareMode.SKILL_TREE, old);
            throw exception;
        } finally {
            REPLAYING.remove();
        }
    }

    private static List<SkillNode> orderedNodes() {
        List<SkillNode> ordered = new ArrayList<>();
        Set<SkillNode> visited = new HashSet<>();
        Set<SkillNode> active = new HashSet<>();
        for (SkillNode node : SkillRegistry.getNodes()) visit(node, visited, active, ordered);
        return ordered;
    }

    private static void visit(SkillNode node, Set<SkillNode> visited, Set<SkillNode> active,
                              List<SkillNode> ordered) {
        if (visited.contains(node)) return;
        if (!active.add(node)) throw new IllegalStateException("Skill parent cycle: " + node.id);
        for (SkillNode parent : node.parents) visit(parent, visited, active, ordered);
        active.remove(node);
        visited.add(node);
        ordered.add(node);
    }
}
