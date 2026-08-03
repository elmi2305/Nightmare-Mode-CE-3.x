package com.itlesports.nightmaremode.block.tileEntities;

import net.minecraft.src.IInventory;
import net.minecraft.src.ISidedInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;

/**
 * Side-mounted automation access for a cistern. The first four virtual slots
 * expose only finished outputs; the remaining nine expose the cistern inputs.
 * The interface deliberately owns no inventory, so breaking it can never
 * duplicate or destroy the cistern's contents.
 */
public class CisternInterfaceTileEntity extends TileEntity implements ISidedInventory {
    public static final int OUTPUT_SLOTS = 4;
    private static final int[] ALL_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

    public CisternTileEntity getCistern() {
        int metadata = this.worldObj == null ? 0 : this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        int x = this.xCoord;
        int z = this.zCoord;
        if (metadata == 2) --z;
        else if (metadata == 3) ++z;
        else if (metadata == 4) --x;
        else if (metadata == 5) ++x;
        TileEntity tile = this.worldObj == null ? null : this.worldObj.getBlockTileEntity(x, this.yCoord, z);
        return tile instanceof CisternTileEntity ? (CisternTileEntity)tile : null;
    }

    private int mapSlot(int slot) {
        return slot < OUTPUT_SLOTS
                ? CisternTileEntity.FIRST_OUTPUT_SLOT + slot
                : CisternTileEntity.FIRST_INPUT_SLOT + slot - OUTPUT_SLOTS;
    }

    @Override public int getSizeInventory() { return ALL_SLOTS.length; }

    @Override
    public ItemStack getStackInSlot(int slot) {
        CisternTileEntity cistern = this.getCistern();
        return cistern == null || slot < 0 || slot >= this.getSizeInventory() ? null : cistern.getStackInSlot(this.mapSlot(slot));
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        CisternTileEntity cistern = this.getCistern();
        return cistern == null || slot < 0 || slot >= this.getSizeInventory() ? null : cistern.decrStackSize(this.mapSlot(slot), count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        CisternTileEntity cistern = this.getCistern();
        return cistern == null || slot < 0 || slot >= this.getSizeInventory() ? null : cistern.getStackInSlotOnClosing(this.mapSlot(slot));
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        CisternTileEntity cistern = this.getCistern();
        if (cistern != null && slot >= OUTPUT_SLOTS && slot < this.getSizeInventory()) {
            cistern.setInventorySlotContents(this.mapSlot(slot), stack);
        }
    }

    @Override public String getInvName() { return "container.ifhyCisternInterface"; }
    @Override public boolean isInvNameLocalized() { return true; }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public boolean isUseableByPlayer(net.minecraft.src.EntityPlayer player) { return false; }
    @Override public void openChest() {}
    @Override public void closeChest() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        CisternTileEntity cistern = this.getCistern();
        return cistern != null && slot >= OUTPUT_SLOTS && slot < this.getSizeInventory()
                && cistern.isItemValidForSlot(this.mapSlot(slot), stack);
    }

    @Override public int[] getSlotsForFace(int side) { return ALL_SLOTS; }
    @Override public boolean canInsertItem(int slot, ItemStack stack, int side) { return this.isItemValidForSlot(slot, stack); }
    @Override public boolean canExtractItem(int slot, ItemStack stack, int side) { return slot >= 0 && slot < OUTPUT_SLOTS; }
}
