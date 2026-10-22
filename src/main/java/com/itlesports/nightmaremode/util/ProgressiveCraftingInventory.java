package com.itlesports.nightmaremode.util;

import net.minecraft.src.*;

public final class ProgressiveCraftingInventory {
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
            if (candidate.stackSize <= 0) inventory.mainInventory[slot] = null;
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
            inventory.mainInventory[selected] = null;
        }
        inventory.onInventoryChanged();
        return refill != null ? refill : output;
    }
}
