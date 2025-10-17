package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.*;

public class SlotInsertOnly extends Slot {
    public SlotInsertOnly(IInventory inv, int index, int x, int y) { super(inv, index, x, y); }

    @Override public boolean canTakeStack(EntityPlayer player) { return false; }
    @Override public boolean isItemValid(ItemStack stack) {
        return this.inventory.isItemValidForSlot(this.slotNumber, stack);
    }

}

