package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class SlotInstantConsume extends Slot {
    public SlotInstantConsume(IInventory inv, int index, int x, int y) {
        super(inv, index, x, y);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        // never allow taking out (it's consumed instantly anyway)
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return this.inventory.isItemValidForSlot(this.slotNumber, stack);
    }
}
