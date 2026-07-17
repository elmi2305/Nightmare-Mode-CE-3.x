package com.itlesports.nightmaremode.skill;

import api.world.data.DataSyncManager;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

public class SkillHandler {
    public static SkillTreeData getPlayerData(EntityPlayer player) {
        return player.getData(NightmareMode.SKILL_TREE);
    }

    public static WorldSkillData getWorldData(World world) {
        return world.getData(NightmareMode.WORLD_SKILL_TREE);
    }

    public static boolean isUnlocked(EntityPlayer player, SkillNode node) {
        if (node == null || player == null) {
            return false;
        }
        if (node.worldReward && isWorldUnlocked(player.worldObj, node)) {
            return true;
        }
        return getPlayerData(player).isUnlocked(node);
    }

    public static boolean isUnlocked(EntityPlayer player, String nodeId) {
        return isUnlocked(player, SkillRegistry.getNode(nodeId));
    }

    public static boolean isWorldUnlocked(World world, SkillNode node) {
        return world != null && getWorldData(world).isUnlocked(node);
    }

    public static boolean hasUnlockedAllParents(EntityPlayer player, SkillNode node) {
        for (SkillNode parent : node.parents) {
            if (parent != null && !isUnlocked(player, parent)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEligible(EntityPlayer player, SkillNode node) {
        return node != null
                && !isUnlocked(player, node)
                && hasUnlockedAllParents(player, node)
                && node.triggerCondition.test(player, player.worldObj);
    }

    public static boolean tryUnlock(EntityPlayerMP player, String nodeId) {
        SkillNode node = SkillRegistry.getNode(nodeId);
        if (!isEligible(player, node) && !NightmareMode.devMode) {
            sendStatus(player, "Skill is not ready to unlock.");
            return false;
        }

        SkillTreeData playerData = getPlayerData(player);
        WorldSkillData worldData = getWorldData(player.worldObj);
        if (node.onUnlockConsume != null) {
            node.onUnlockConsume.apply(player, player.worldObj);
        }
        playerData.unlock(node);
        if (node.worldReward) {
            worldData.unlock(node);
        }
        node.reward.getAction().apply(player, player.worldObj);
        player.setData(NightmareMode.SKILL_TREE, playerData);
        player.worldObj.setData(NightmareMode.WORLD_SKILL_TREE, worldData);
        sync(player);
        sendStatus(player, "Unlocked skill: " + node.name);
        return true;
    }

    public static void incrementBlocksMined(EntityPlayer player, int blockId) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        SkillTreeData data = getPlayerData(player);
        data.blocksMined++;
        if (blockId == Block.oreCoal.blockID) {
            data.coalOreMined++;
        } else if (blockId == Block.oreIron.blockID) {
            data.ironOreMined++;
        } else if (blockId == Block.oreDiamond.blockID) {
            data.diamondOreMined++;
        } else if (blockId == Block.tallGrass.blockID) {
            data.tallGrassMined++;
        }

        player.setData(NightmareMode.SKILL_TREE, data);
    }

    public static void incrementMobKill(EntityPlayer player, net.minecraft.src.EntityLivingBase killed) {
        if (player == null || killed == null || player.worldObj.isRemote) {
            return;
        }
        SkillTreeData data = getPlayerData(player);
        data.mobsKilled++;
        if (killed instanceof net.minecraft.src.EntityZombie) {
            data.zombiesKilled++;
        } else if (killed instanceof net.minecraft.src.EntitySkeleton) {
            data.skeletonsKilled++;
        }
        player.setData(NightmareMode.SKILL_TREE, data);
    }

    public static void incrementFishCaught(EntityPlayer player, boolean rare) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        SkillTreeData data = getPlayerData(player);
        data.fishCaught++;
        if (rare) {
            data.rareItemsCaught++;
        }
        player.setData(NightmareMode.SKILL_TREE, data);
    }

    public static void incrementAnimalsTamed(EntityPlayer player) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        SkillTreeData data = getPlayerData(player);
        data.animalsTamed++;
        player.setData(NightmareMode.SKILL_TREE, data);
    }

    public static void incrementAnimalsBred(EntityPlayer player) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) {
            return;
        }
        SkillTreeData data = getPlayerData(player);
        data.animalsBred++;
        player.setData(NightmareMode.SKILL_TREE, data);
    }

    public static void incrementPotionsBrewed(EntityPlayer player, int count) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote || count <= 0) {
            return;
        }
        SkillTreeData data = getPlayerData(player);
        data.potionsBrewed += count;
        player.setData(NightmareMode.SKILL_TREE, data);
    }

    public static void incrementFoodCooked(EntityPlayer player, int count) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote || count <= 0) {
            return;
        }
        SkillTreeData data = getPlayerData(player);
        data.foodCooked += count;
        player.setData(NightmareMode.SKILL_TREE, data);
    }

    public static void incrementBooksCrafted(EntityPlayer player, int count) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote || count <= 0) {
            return;
        }
        SkillTreeData data = getPlayerData(player);
        data.booksCrafted += count;
        player.setData(NightmareMode.SKILL_TREE, data);
    }

    public static boolean canHarvestDiamondOre(EntityPlayer player) {
        return player != null && getPlayerData(player).canHarvestDiamondOre;
    }

    public static boolean hasNetherAccess(EntityPlayer player) {
        return player != null && getWorldData(player.worldObj).netherAccessUnlocked;
    }

    public static boolean woodBlocksIgnoreSkybaseGravity(World world) {
        return world != null && getWorldData(world).woodBlocksIgnoreSkybaseGravity;
    }

    public static void sync(EntityPlayerMP player) {
        if (player.worldObj instanceof WorldServer worldServer) {
            DataSyncManager.syncPlayerDataToAllPlayers(worldServer, NightmareMode.SKILL_TREE);
            DataSyncManager.syncWorldDataToAllPlayers(worldServer, NightmareMode.WORLD_SKILL_TREE);
        }
    }

    public static void sendStatus(EntityPlayer player, String message) {
        if (!(player instanceof EntityPlayerMP playerMP)) {
            return;
        }
        ChatMessageComponent text = new ChatMessageComponent();
        text.addText(message);
        text.setColor(EnumChatFormatting.AQUA);
        playerMP.sendChatToPlayer(text);
    }
}
