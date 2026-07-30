package com.itlesports.nightmaremode.util.interfaces;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

public interface IChuteTransferFilter {
    IChuteTransferFilter ALLOW_ALL = (stack, source, target) -> true;

    boolean canTransfer(ItemStack stack, IInventory source, IInventory target);
}
