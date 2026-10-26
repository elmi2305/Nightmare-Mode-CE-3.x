package com.itlesports.nightmaremode.util;

import net.minecraft.src.*;

public final class ProgressiveCraftingInventory {
    private static final java.util.Map<EntityPlayer, Long> emptiedRefillSlots = new java.util.WeakHashMap<>();
    private ProgressiveCraftingInventory() {}

    public static ItemStack finish(EntityPlayer player, ItemStack input, ItemStack output) {
        if (player.worldObj.isRemote || output == null) return output;
        com.itlesports.nightmaremode.world.SandboxRules.recordAcquisition(player, output);
        InventoryPlayer inventory = player.inventory;
        int selected = inventory.currentItem;
        if (inventory.mainInventory[selected] != input) return output;
        inventory.mainInventory[selected] = null;
        ItemStack refill = null;
        for (int slot = 0; slot < inventory.mainInventory.length; ++slot) {
            ItemStack candidate = inventory.mainInventory[slot];
            if (slot == selected || !NMInventoryLocks.isMainInventorySlotUnlocked(player, slot)
                    || candidate == null || candidate.stackSize <= 0 || candidate.itemID != input.itemID
                    || !ItemStack.areItemStackTagsEqual(candidate, input)) continue;
            refill = candidate.splitStack(1);
            if (candidate.stackSize <= 0) {
                inventory.mainInventory[slot] = null;
                emptiedRefillSlots.put(player, emptiedRefillSlots.getOrDefault(player, 0L) | 1L << slot);
            }
            break;
        }
        if (output.isStackable()) {
            for (int slot = 0; slot < inventory.mainInventory.length && output.stackSize > 0; ++slot) {
                ItemStack existing = inventory.mainInventory[slot];
                if (!NMInventoryLocks.isMainInventorySlotUnlocked(player, slot) || existing == null
                        || !existing.isItemEqual(output) || !ItemStack.areItemStackTagsEqual(existing, output)) continue;
                int moved = Math.min(output.stackSize,
                        Math.min(existing.getMaxStackSize(), inventory.getInventoryStackLimit()) - existing.stackSize);
                if (moved <= 0) continue;
                existing.stackSize += moved;
                output.stackSize -= moved;
            }
        }
        if (refill != null && output.stackSize > 0) {
            inventory.mainInventory[selected] = refill;
            inventory.addItemStackToInventory(output);
            if (output.stackSize > 0) player.dropPlayerItem(output);
        }
        inventory.mainInventory[selected] = refill != null ? refill : output.stackSize > 0 ? output : null;
        inventory.onInventoryChanged();
        return refill != null ? refill : output;
    }

    public static void syncRefilledSlots(EntityPlayerMP player) {
        Long pending = emptiedRefillSlots.get(player);
        if (pending == null) return;
        long remaining = pending;
        for (int slot = 0; slot < player.inventory.mainInventory.length; ++slot) {
            ItemStack stack = player.inventory.mainInventory[slot];
            if ((pending & 1L << slot) != 0 && stack != null && stack.stackSize > 0) {
                remaining &= ~(1L << slot);
            }
        }
        if (remaining == pending) return;
        player.sendContainerToPlayer(player.inventoryContainer);
        if (player.openContainer != player.inventoryContainer) player.sendContainerToPlayer(player.openContainer);
        if (remaining == 0L) emptiedRefillSlots.remove(player);
        else emptiedRefillSlots.put(player, remaining);
    }
}
