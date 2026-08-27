package com.itlesports.nightmaremode.util.interfaces;

import net.minecraft.src.ItemStack;

/** Supplies a second, non-durability status bar for equipment with a stored resource. */
public interface IArmorStatus {
    float getStatusFraction(ItemStack stack);

    int getStatusColor(ItemStack stack);

    int getStatusBackgroundColor();
}
