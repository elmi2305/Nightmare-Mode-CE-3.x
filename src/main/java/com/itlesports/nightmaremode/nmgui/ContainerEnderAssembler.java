package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.block.tileEntities.EnderAssemblerTileEntity;
import net.minecraft.src.*;

public class ContainerEnderAssembler extends Container {
    public static final int ID = 53;
    private final EnderAssemblerTileEntity assembler;
    private int lastProcess = -1;
    private int lastTotal = -1;

    public ContainerEnderAssembler(InventoryPlayer playerInventory, EnderAssemblerTileEntity assembler) {
        this.assembler = assembler;
        for (int i = 0; i < 6; ++i) this.addSlotToContainer(new Slot(assembler, i, 8 + i * 18, 35));
        this.addSlotToContainer(new Slot(assembler, 6, 143, 35) { @Override public boolean isItemValid(ItemStack stack) { return false; } });
        for (int row = 0; row < 3; ++row) for (int column = 0; column < 9; ++column)
            this.addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
        for (int column = 0; column < 9; ++column) this.addSlotToContainer(new Slot(playerInventory, column, 8 + column * 18, 142));
    }
    @Override public void onCraftGuiOpened(ICrafting crafting) {
        super.onCraftGuiOpened(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.assembler.getProcessTicks());
        crafting.sendProgressBarUpdate(this, 1, this.assembler.getProcessTotal());
    }
    @Override public void detectAndSendChanges() {
        super.detectAndSendChanges();
        int process = this.assembler.getProcessTicks(), total = this.assembler.getProcessTotal();
        for (Object object : this.crafters) {
            ICrafting crafting = (ICrafting)object;
            if (process != this.lastProcess) crafting.sendProgressBarUpdate(this, 0, process);
            if (total != this.lastTotal) crafting.sendProgressBarUpdate(this, 1, total);
        }
        this.lastProcess = process; this.lastTotal = total;
    }
    @Override public void updateProgressBar(int id, int value) {
        if (id == 0) this.assembler.setProcessTicks(value);
        if (id == 1) this.assembler.setProcessTotal(value);
    }
    @Override public boolean canInteractWith(EntityPlayer player) { return this.assembler.isUseableByPlayer(player); }
    @Override public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (index < 0 || index >= this.inventorySlots.size()) return null;
        Slot slot = (Slot)this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) return null;
        ItemStack stack = slot.getStack(), original = stack.copy();
        if (index < 7) { if (!this.mergeItemStack(stack, 7, 43, true)) return null; }
        else if (!this.mergeItemStack(stack, 0, 6, false)) return null;
        if (stack.stackSize <= 0) slot.putStack(null); else slot.onSlotChanged();
        if (stack.stackSize == original.stackSize) return null;
        slot.onPickupFromSlot(player, stack);
        return original;
    }
}
