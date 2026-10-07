package com.itlesports.nightmaremode.util;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import com.itlesports.nightmaremode.skill.SkillHandler;

public final class NMInventoryLocks {
    private static final int[] HOTBAR_SLOT_LEVELS = {0, 3, 6, 9, 12, 15};
    private static final int SECOND_BACKPACK_ROW_LEVEL = 10;

    private static int getInventoryLevel(EntityPlayer player) {
        if (!com.itlesports.nightmaremode.world.BalanceProfile.isEasy()) return player.experienceLevel;
        com.itlesports.nightmaremode.skill.SkillTreeData data = SkillHandler.getPlayerData(player);
        data.easyInventoryLevel = Math.max(data.easyInventoryLevel, player.experienceLevel);
        return data.easyInventoryLevel;
    }

    private NMInventoryLocks() {
    }

    public static int getUnlockedHotbarSlots(EntityPlayer player) {
        if (player == null || player.capabilities == null || player.capabilities.isCreativeMode
                || NightmareMode.devMode || NightmareMode.fullInventoryCapacity) {
            return 9;
        }

        int unlocked = 1;
        for (int i = 1; i < HOTBAR_SLOT_LEVELS.length; i++) {
            if (getInventoryLevel(player) >= HOTBAR_SLOT_LEVELS[i]) {
                unlocked = i + 1;
            }
        }
        return Math.min(9, unlocked + SkillHandler.getPlayerData(player).extraHotbarSlots);
    }

    public static int getUnlockedBackpackSlots(EntityPlayer player) {
        if (player == null || player.capabilities == null || player.capabilities.isCreativeMode
                || NightmareMode.devMode || NightmareMode.fullInventoryCapacity) {
            return 27;
        }

        int unlockedRows = getInventoryLevel(player) >= SECOND_BACKPACK_ROW_LEVEL ? 2 : 1;
        if (SkillHandler.getPlayerData(player).thirdInventoryRowUnlocked) {
            unlockedRows = 3;
        }
        return unlockedRows * 9;
    }

    public static boolean isMainInventorySlotUnlocked(EntityPlayer player, int slotIndex) {
        if (slotIndex < 0) {
            return false;
        }
        if (slotIndex < 9) {
            return slotIndex < getUnlockedHotbarSlots(player);
        }
        if (slotIndex < 36) {
            return slotIndex - 9 < getUnlockedBackpackSlots(player);
        }
        return true;
    }

    public static boolean isMainInventorySlotUnlockedAfterDeath(EntityPlayer player, int slotIndex) {
        if (player == null || player.capabilities == null || player.capabilities.isCreativeMode
                || NightmareMode.devMode || NightmareMode.fullInventoryCapacity) return true;
        if (com.itlesports.nightmaremode.world.BalanceProfile.isEasy()) return isMainInventorySlotUnlocked(player, slotIndex);
        if (slotIndex < 0) return false;
        if (slotIndex < 9) return slotIndex < Math.min(9, 1 + SkillHandler.getPlayerData(player).extraHotbarSlots);
        if (slotIndex < 36) return slotIndex - 9 < (SkillHandler.getPlayerData(player).thirdInventoryRowUnlocked ? 27 : 9);
        return true;
    }

    public static void dropLockedItems(EntityPlayer player, boolean afterDeath) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote || player.inventory == null) return;

        boolean changed = false;
        ItemStack[] inventory = player.inventory.mainInventory;
        for (int slot = 0; slot < inventory.length; slot++) {
            ItemStack stack = inventory[slot];
            if (stack == null || (afterDeath
                    ? isMainInventorySlotUnlockedAfterDeath(player, slot)
                    : isMainInventorySlotUnlocked(player, slot))) continue;

            if (afterDeath && stack.itemID == com.itlesports.nightmaremode.item.NMItems.skillBook.itemID) continue;

            inventory[slot] = null;
            player.dropPlayerItemWithRandomChoice(stack, true);
            changed = true;
        }
        if (changed) player.inventory.onInventoryChanged();
    }

    public static boolean isPlayerInventorySlotLocked(Slot slot, EntityPlayer player) {
        return slot != null
                && slot.inventory instanceof InventoryPlayer
                && !isMainInventorySlotUnlocked(player, slot.getSlotIndex());
    }

    public static void relocateSkillBooks(EntityPlayer player) {
        ItemStack[] inventory = player.inventory.mainInventory;
        for (int slot = 0; slot < inventory.length; slot++) {
            ItemStack stack = inventory[slot];
            if (stack == null || stack.itemID != com.itlesports.nightmaremode.item.NMItems.skillBook.itemID
                    || isMainInventorySlotUnlocked(player, slot)) continue;
            inventory[slot] = null;
            if (!player.inventory.addItemStackToInventory(stack)) player.dropPlayerItem(stack);
        }
        player.inventory.onInventoryChanged();
    }
}
