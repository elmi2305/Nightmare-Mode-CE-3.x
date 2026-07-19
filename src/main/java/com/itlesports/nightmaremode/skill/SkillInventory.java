package com.itlesports.nightmaremode.skill;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class SkillInventory {
    public static boolean has(EntityPlayer player, Item item, int count) {
        return count(player, item.itemID, 0, false) >= count;
    }

    public static boolean has(EntityPlayer player, int itemId, int damage, boolean matchDamage, int count) {
        return count(player, itemId, damage, matchDamage) >= count;
    }

    public static void consume(EntityPlayer player, Item item, int count) {
        consume(player, item.itemID, 0, false, count);
    }

    public static void consume(EntityPlayer player, int itemId, int damage, boolean matchDamage, int count) {
        for (int i = 0; i < player.inventory.mainInventory.length && count > 0; ++i) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack == null || stack.itemID != itemId || matchDamage && stack.getItemDamage() != damage) {
                continue;
            }
            int removed = Math.min(stack.stackSize, count);
            stack.stackSize -= removed;
            count -= removed;
            if (stack.stackSize <= 0) {
                player.inventory.mainInventory[i] = null;
            }
        }
        player.inventory.onInventoryChanged();
    }

    public static boolean hasAny(EntityPlayer player, int count, Item... items) {
        int total = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack == null) {
                continue;
            }
            for (Item item : items) {
                if (item != null && stack.itemID == item.itemID) {
                    total += stack.stackSize;
                    break;
                }
            }
        }
        return total >= count;
    }

    public static void consumeAny(EntityPlayer player, int count, Item... items) {
        for (int slot = 0; slot < player.inventory.mainInventory.length && count > 0; ++slot) {
            ItemStack stack = player.inventory.mainInventory[slot];
            if (stack == null) {
                continue;
            }
            boolean matches = false;
            for (Item item : items) {
                if (item != null && stack.itemID == item.itemID) {
                    matches = true;
                    break;
                }
            }
            if (!matches) {
                continue;
            }
            int removed = Math.min(stack.stackSize, count);
            stack.stackSize -= removed;
            count -= removed;
            if (stack.stackSize <= 0) {
                player.inventory.mainInventory[slot] = null;
            }
        }
        player.inventory.onInventoryChanged();
    }

    private static int count(EntityPlayer player, int itemId, int damage, boolean matchDamage) {
        int count = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.itemID == itemId && (!matchDamage || stack.getItemDamage() == damage)) {
                count += stack.stackSize;
            }
        }
        return count;
    }
}
