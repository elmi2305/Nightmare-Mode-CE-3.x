package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.MinerDrillTileEntity;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerMinerDrill extends Container {
    public static final int ID = 51;
    private final MinerDrillTileEntity drill;

    public ContainerMinerDrill(InventoryPlayer playerInventory, MinerDrillTileEntity drill) {
        this.drill = drill;
        this.addSlotToContainer(new Slot(drill, 0, 80, 35) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack != null && stack.itemID == Item.coal.itemID;
            }
        });
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9,
                        8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; ++column) {
            this.addSlotToContainer(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.drill.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= this.inventorySlots.size()) {
            return null;
        }

        Slot slot = (Slot)this.inventorySlots.get(slotIndex);
        if (slot == null || !slot.getHasStack()) {
            return null;
        }

        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        if (slotIndex == 0) {
            if (!this.mergeItemStack(stack, 1, 37, true)) {
                return null;
            }
        } else if (stack.itemID != Item.coal.itemID || !this.mergeItemStack(stack, 0, 1, false)) {
            return null;
        }

        if (stack.stackSize <= 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }
        if (stack.stackSize == original.stackSize) {
            return null;
        }
        slot.onPickupFromSlot(player, stack);
        return original;
    }
}
