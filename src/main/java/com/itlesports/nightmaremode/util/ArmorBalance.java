package com.itlesports.nightmaremode.util;

import net.minecraft.src.Item;
import net.minecraft.src.ItemArmor;

public final class ArmorBalance {
    private ArmorBalance() {
    }

    public static void setProtection(Item item, int protection) {
        if (!(item instanceof ItemArmor armor)) {
            throw new IllegalArgumentException("Expected an armor item");
        }
        if (protection < 0) {
            throw new IllegalArgumentException("Armor protection cannot be negative");
        }
        armor.damageReduceAmount = protection;
    }
}
