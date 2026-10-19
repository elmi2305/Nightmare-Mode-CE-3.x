package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public final class NMFishFood {
    private NMFishFood() {}

    public static boolean isRawFish(ItemStack stack) {
        if (stack == null) return false;
        if (stack.getItem() == Item.fishRaw || stack.getItem() == NMItems.debonedRawFish || stack.getItem() == NMItems.lavafish) return true;
        for (Item fish : NMItems.getRawFish()) if (stack.getItem() == fish) return true;
        return false;
    }

    public static int ocelotFoodID(ItemStack stack) {
        return isRawFish(stack) ? Item.fishRaw.itemID : stack.itemID;
    }
}
