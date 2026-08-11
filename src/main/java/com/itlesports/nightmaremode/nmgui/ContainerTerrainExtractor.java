package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.TerrainExtractorTileEntity;
import net.minecraft.src.*;

public class ContainerTerrainExtractor extends Container {
    public static final int ID = 52;
    private final TerrainExtractorTileEntity extractor;
    private int lastFuel = -1;
    private int lastProcess = -1;
    private int lastField = -1;

    public ContainerTerrainExtractor(InventoryPlayer playerInventory, TerrainExtractorTileEntity extractor) {
        this.extractor = extractor;
        this.addSlotToContainer(new Slot(extractor, 0, 44, 35));
        this.addSlotToContainer(new Slot(extractor, 1, 80, 35));
        this.addSlotToContainer(new Slot(extractor, 2, 116, 35) {
            @Override public boolean isItemValid(ItemStack stack) { return false; }
        });
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; ++column) {
            this.addSlotToContainer(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public void onCraftGuiOpened(ICrafting crafting) {
        super.onCraftGuiOpened(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.extractor.getFuelTicks());
        crafting.sendProgressBarUpdate(this, 1, this.extractor.getProcessTicks());
        crafting.sendProgressBarUpdate(this, 2, this.extractor.getFieldMilli() & 65535);
        crafting.sendProgressBarUpdate(this, 3, this.extractor.getFieldMilli() >>> 16);
        crafting.sendProgressBarUpdate(this, 4, this.extractor.getFieldType());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int fuel = this.extractor.getFuelTicks();
        int process = this.extractor.getProcessTicks();
        int field = this.extractor.getFieldMilli();
        for (Object listener : this.crafters) {
            ICrafting crafting = (ICrafting)listener;
            if (fuel != this.lastFuel) crafting.sendProgressBarUpdate(this, 0, fuel);
            if (process != this.lastProcess) crafting.sendProgressBarUpdate(this, 1, process);
            if (field != this.lastField) {
                crafting.sendProgressBarUpdate(this, 2, field & 65535);
                crafting.sendProgressBarUpdate(this, 3, field >>> 16);
            }
        }
        this.lastFuel = fuel;
        this.lastProcess = process;
        this.lastField = field;
    }

    @Override
    public void updateProgressBar(int id, int value) {
        if (id == 0) this.extractor.setFuelTicks(value);
        if (id == 1) this.extractor.setProcessTicks(value);
        if (id == 2) this.extractor.setFieldMilli((this.extractor.getFieldMilli() & -65536) | value);
        if (id == 3) this.extractor.setFieldMilli((this.extractor.getFieldMilli() & 65535) | value << 16);
        if (id == 4) this.extractor.setFieldType(value);
    }

    @Override public boolean canInteractWith(EntityPlayer player) { return this.extractor.isUseableByPlayer(player); }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index < 0 || index >= this.inventorySlots.size()) return null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return null;
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        if (index < 3) {
            if (!this.mergeItemStack(stack, 3, 39, true)) return null;
        } else if (stack.itemID == Item.coal.itemID) {
            if (!this.mergeItemStack(stack, 0, 1, false)) return null;
        } else if (stack.itemID == Item.bucketEmpty.itemID) {
            if (!this.mergeItemStack(stack, 1, 2, false)) return null;
        } else return null;
        if (stack.stackSize <= 0) slot.putStack(null); else slot.onSlotChanged();
        if (stack.stackSize == original.stackSize) return null;
        slot.onPickupFromSlot(player, stack);
        return original;
    }
}
