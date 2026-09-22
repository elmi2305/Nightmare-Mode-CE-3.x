package com.itlesports.nightmaremode.item.itemblock;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.util.StorageColor;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.StatCollector;

/** The NBT carried by a chest stack keeps differently colored chests separate. */
public class ItemBlockColoredChest extends ItemBlock {
    public ItemBlockColoredChest(int itemId) {
        super(itemId);
    }

    @Override
    public String getModId() {
        return "nightmare";
    }

    @Override
    public String getItemDisplayName(ItemStack stack) {
        if (StorageColor.isChestItem(stack) && StorageColor.hasChestItemColor(stack)) {
            return StatCollector.translateToLocal("item.nmColoredChest.name");
        }
        if (stack.itemID == NMBlocks.bloodChest.blockID && StorageColor.hasChestItemColor(stack)) {
            return StatCollector.translateToLocal("item.nmColoredBloodChest.name");
        }
        if (stack.itemID == NMBlocks.steelLocker.blockID && StorageColor.hasChestItemColor(stack)) {
            return StatCollector.translateToLocal("item.nmColoredSteelLocker.name");
        }
        if (!StorageColor.hasStorageItemColor(stack)) return super.getItemDisplayName(stack);
        if (stack.itemID == NMBlocks.bloodChest.blockID) return StatCollector.translateToLocal("item.nmColoredBloodChest.name");
        if (stack.itemID == NMBlocks.steelLocker.blockID) return StatCollector.translateToLocal("item.nmColoredSteelLocker.name");
        return super.getItemDisplayName(stack);
    }
}
