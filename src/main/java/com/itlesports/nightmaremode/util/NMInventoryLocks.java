package com.itlesports.nightmaremode.util;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Slot;

public final class NMInventoryLocks {
    private static final int[] HOTBAR_SLOT_LEVELS = {0, 3, 6, 9, 12, 15, 18, 21, 24};
    private static final int[] BACKPACK_ROW_LEVELS = {0, 10, 20};

    private NMInventoryLocks() {
    }

    public static int getUnlockedHotbarSlots(EntityPlayer player) {
        if (player == null || player.capabilities == null || player.capabilities.isCreativeMode) {
            return 9;
        }

        int unlocked = 1;
        for (int i = 1; i < HOTBAR_SLOT_LEVELS.length; i++) {
            if (player.experienceLevel >= HOTBAR_SLOT_LEVELS[i]) {
                unlocked = i + 1;
            }
        }
        return unlocked;
    }

    public static int getUnlockedBackpackSlots(EntityPlayer player) {
        if (player == null || player.capabilities == null || player.capabilities.isCreativeMode) {
            return 27;
        }

        int unlockedRows = 1;
        for (int i = 1; i < BACKPACK_ROW_LEVELS.length; i++) {
            if (player.experienceLevel >= BACKPACK_ROW_LEVELS[i]) {
                unlockedRows = i + 1;
            }
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

    public static boolean isPlayerInventorySlotLocked(Slot slot, EntityPlayer player) {
        return slot != null
                && slot.inventory instanceof InventoryPlayer
                && !isMainInventorySlotUnlocked(player, slot.getSlotIndex());
    }
}
