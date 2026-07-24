package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.WeightedRandomChestContent;

import java.util.Random;

/** Shared helpers for inserting one-metadata knowledge books into structure loot. */
public final class KnowledgeBookLoot {
    private KnowledgeBookLoot() {
    }

    public static WeightedRandomChestContent[] addWeightedBooks(WeightedRandomChestContent[] loot, int[] allowedMetadata, int weight) {
        WeightedRandomChestContent[] additions = new WeightedRandomChestContent[allowedMetadata.length];
        for (int i = 0; i < allowedMetadata.length; ++i) {
            additions[i] = new WeightedRandomChestContent(NMItems.knowledgeBook.itemID, allowedMetadata[i], 1, 1, weight);
        }
        return WeightedRandomChestContent.func_92080_a(loot, additions);
    }

    public static void addBookIfRolled(IInventory inventory, Random random, int[] allowedMetadata, int chance) {
        if (allowedMetadata.length == 0 || random.nextInt(chance) != 0) {
            return;
        }
        int startSlot = random.nextInt(inventory.getSizeInventory());
        for (int offset = 0; offset < inventory.getSizeInventory(); ++offset) {
            int slot = (startSlot + offset) % inventory.getSizeInventory();
            if (inventory.getStackInSlot(slot) == null) {
                int metadata = allowedMetadata[random.nextInt(allowedMetadata.length)];
                inventory.setInventorySlotContents(slot, new ItemStack(NMItems.knowledgeBook, 1, metadata));
                return;
            }
        }
    }
}
