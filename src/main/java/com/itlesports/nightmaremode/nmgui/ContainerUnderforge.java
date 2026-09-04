package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityUnderforge;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerUnderforge extends Container {
    public static final int ID = 51;
    private static final int FORGE_SLOTS = 5;
    private final TileEntityUnderforge underforge;

    public ContainerUnderforge(IInventory playerInventory, TileEntityUnderforge underforge) {
        this.underforge = underforge;
        int[] xPositions = {26, 53, 80, 107, 134};
        for (int slot = 0; slot < FORGE_SLOTS - 1; slot++) {
            this.addSlotToContainer(new Slot(underforge, slot, xPositions[slot], 35));
        }
        this.addSlotToContainer(new OutputSlot(underforge, TileEntityUnderforge.SLOT_OUTPUT, xPositions[4], 35));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9,
                        8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlotToContainer(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.underforge.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack original = null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return null;

        ItemStack moving = slot.getStack();
        original = moving.copy();
        if (index < FORGE_SLOTS) {
            if (!this.mergeItemStack(moving, FORGE_SLOTS, FORGE_SLOTS + 36, true)) return null;
        } else if (!this.mergeItemStack(moving, 0, FORGE_SLOTS - 1, false)) {
            return null;
        }

        if (moving.stackSize == 0) slot.putStack(null);
        else slot.onSlotChanged();
        if (moving.stackSize == original.stackSize) return null;
        slot.onPickupFromSlot(player, moving);
        return original;
    }

    private static class OutputSlot extends Slot {
        OutputSlot(IInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }
}
