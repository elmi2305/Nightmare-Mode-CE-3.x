package com.itlesports.nightmaremode.util;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public final class NMItemStackUtils {
    private NMItemStackUtils() {}

    public static boolean isValid(ItemStack stack) {
        return stack != null && stack.stackSize > 0 && stack.itemID >= 0
                && stack.itemID < Item.itemsList.length && Item.itemsList[stack.itemID] != null;
    }

    public static boolean shouldBreakOnUse(ItemStack stack, int damage) {
        return damage > 0 && isValid(stack) && stack.isItemStackDamageable()
                && stack.getItemDamage() >= stack.getMaxDamage() - damage;
    }
}
