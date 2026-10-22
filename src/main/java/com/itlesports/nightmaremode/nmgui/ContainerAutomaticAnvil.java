package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.util.AutoAnvilUpgrade;
import net.minecraft.src.*;
import java.util.Arrays;

public abstract class ContainerAutomaticAnvil extends Container {
    private static final int PROPERTY_COUNT = 8;
    private final TileEntityHammerAnvil anvil;
    private final int[] previous = new int[PROPERTY_COUNT];
    private final boolean phase;
    private final int machineSlots;

    protected ContainerAutomaticAnvil(InventoryPlayer inventory, TileEntityHammerAnvil anvil, boolean phase) {
        this.phase = phase;
        this.machineSlots = phase ? 2 : 3;
        this.anvil = anvil;
        Arrays.fill(this.previous, Integer.MIN_VALUE);
        int[] positions = phase ? new int[]{0, 48, 112} : new int[]{27, 76, 134};
        for (int index = phase ? 1 : 0; index < 3; ++index) {
            final int slot = index;
            this.addSlotToContainer(new Slot(anvil, slot, positions[slot], 38) {
                @Override public boolean isItemValid(ItemStack stack) { return anvil.isItemValidForSlot(slot, stack); }
                @Override public int getSlotStackLimit() { return slot == 0 ? 1 : 64; }
            });
        }
        for (int row = 0; row < 3; ++row) for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 96 + row * 18));
        }
        for (int col = 0; col < 9; ++col) this.addSlotToContainer(new Slot(inventory, col, 8 + col * 18, 154));
    }

    @Override public void onCraftGuiOpened(ICrafting crafter) {
        super.onCraftGuiOpened(crafter);
        for (int i = 0; i < PROPERTY_COUNT; ++i) this.sendProperty(crafter, i);
    }
    private void sendProperty(ICrafting crafter, int property) {
        int value = this.anvil.getGuiProperty(property);
        // window properties are signed shorts; diamond durability needs both halves.
        crafter.sendProgressBarUpdate(this, property * 2, value & 65535);
        crafter.sendProgressBarUpdate(this, property * 2 + 1, value >>> 16);
    }
    @Override public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < PROPERTY_COUNT; ++i) {
            int value = this.anvil.getGuiProperty(i);
            if (value == this.previous[i]) continue;
            for (Object listener : this.crafters) this.sendProperty((ICrafting)listener, i);
            this.previous[i] = value;
        }
    }
    @Override public void updateProgressBar(int id, int value) {
        if (id < 0 || id >= PROPERTY_COUNT * 2) return;
        int property = id / 2;
        int previousValue = this.anvil.getGuiProperty(property);
        this.anvil.setGuiProperty(property, (id & 1) == 0
                ? (previousValue & 0xFFFF0000) | (value & 65535)
                : (previousValue & 65535) | (value & 65535) << 16);
    }
    @Override public boolean canInteractWith(EntityPlayer player) {
        return this.anvil.isUseableByPlayer(player)
                && (this.anvil.getUpgrade() == AutoAnvilUpgrade.PHASE) == this.phase;
    }

    @Override public boolean enchantItem(EntityPlayer player, int button) {
        return this.canInteractWith(player) && this.anvil.togglePresentation(button);
    }

    @Override public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index < 0 || index >= this.inventorySlots.size()) return null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (!slot.getHasStack()) return null;
        ItemStack stack = slot.getStack(), original = stack.copy();
        if (index < this.machineSlots) {
            if (!this.mergeItemStack(stack, this.machineSlots, this.machineSlots + 36, true)) return null;
        } else if (!this.phase && this.anvil.isItemValidForSlot(0, stack)) {
            if (!this.mergeItemStack(stack, 0, 1, false)) return null;
        } else if (this.anvil.isItemValidForSlot(1, stack)) {
            if (!this.mergeItemStack(stack, this.phase ? 0 : 1, this.phase ? 1 : 2, false)) return null;
        } else if (index < this.machineSlots + 27) {
            if (!this.mergeItemStack(stack, this.machineSlots + 27, this.machineSlots + 36, false)) return null;
        } else if (!this.mergeItemStack(stack, this.machineSlots, this.machineSlots + 27, false)) return null;
        if (stack.stackSize == original.stackSize) return null;
        if (stack.stackSize <= 0) slot.putStack(null); else slot.onSlotChanged();
        slot.onPickupFromSlot(player, stack);
        return original;
    }
}
