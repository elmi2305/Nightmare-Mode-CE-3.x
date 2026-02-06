package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TileEntitySteelLocker;
import net.minecraft.src.*;

public class ContainerSteelLocker extends Container {

    private final IInventory chest;

    private static final int CHEST_SLOT_X = 8;
    private static final int CHEST_SLOT_Y = 18;
    private static final int PLAYER_INV_X = 98;
    private static final int PLAYER_INV_Y = 158;
    private static final int HOTBAR_GAP = 4;

    public ContainerSteelLocker(InventoryPlayer playerInv, IInventory chest) {
        this.chest = chest;

        int chestSlots = chest.getSizeInventory();
        int cols = (chestSlots == TileEntitySteelLocker.SLOT_TOTAL)
                ? TileEntitySteelLocker.SLOT_COLS
                : 9;
        int rows = (chestSlots == TileEntitySteelLocker.SLOT_TOTAL)
                ? TileEntitySteelLocker.SLOT_ROWS
                : (chestSlots + cols - 1) / cols;

        int idx = 0;
        for (int r = 0; r < rows && idx < chestSlots; r++) {
            for (int c = 0; c < cols && idx < chestSlots; c++) {
                addSlotToContainer(new Slot(chest, idx++,
                        CHEST_SLOT_X + c * 18,
                        CHEST_SLOT_Y + r * 18));
            }
        }

        // Inventory
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                addSlotToContainer(new Slot(playerInv, c + r * 9 + 9,
                        PLAYER_INV_X + c * 18,
                        PLAYER_INV_Y + r * 18));
            }
        }
        int hotbarY = PLAYER_INV_Y + 3 * 18 + HOTBAR_GAP;
        for (int c = 0; c < 9; c++) {
            addSlotToContainer(new Slot(playerInv, c,
                    PLAYER_INV_X + c * 18,
                    hotbarY));
        }
    }

    public IInventory getChestInventory() {
        return chest;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return chest.isUseableByPlayer(player);
    }

    @Override
    public void onContainerClosed(EntityPlayer player){
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote && chest instanceof TileEntitySteelLocker te){
            te.closeChest();
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player,int slotIdx){
        ItemStack result = null;
        Slot slot = (Slot)inventorySlots.get(slotIdx);
        if (slot != null && slot.getHasStack()){
            ItemStack stack = slot.getStack();
            result = stack.copy();

            int chestSlots = chest.getSizeInventory();
            int hotbarStart = chestSlots + 27;
            int end = hotbarStart + 9;

            if (slotIdx < chestSlots){
                if (!mergeItemStack(stack, chestSlots, end, true)) return null;
            } else {
                if (!mergeItemStack(stack, 0, chestSlots, false)) return null;
            }

            if (stack.stackSize == 0) slot.putStack(null);
            else slot.onSlotChanged();
            if (stack.stackSize == result.stackSize) return null;
            slot.onPickupFromSlot(player, stack);
        }
        return result;
    }

    public static void copyMissingStacks(IInventory src, IInventory dst){
        if (src == null || dst == null) return;
        int limit = Math.min(src.getSizeInventory(), dst.getSizeInventory());
        for (int i=0;i<limit;i++){
            ItemStack s = src.getStackInSlot(i);
            if (s != null && dst.getStackInSlot(i) == null){
                dst.setInventorySlotContents(i, s.copy());
            }
        }
    }
}