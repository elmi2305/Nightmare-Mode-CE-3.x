package com.itlesports.nightmaremode.nmgui;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import net.minecraft.src.*;

public class InventoryHorseArmor implements IInventory {
    private final EntityPlayer owner;
    private final int ownerSlot;
    private final ItemStack armorStack;
    private final ItemStack[] contents = new ItemStack[1];

    public InventoryHorseArmor(EntityPlayer owner, ItemStack armorStack, int ownerSlot) {
        this.owner = owner;
        this.armorStack = armorStack;
        this.ownerSlot = ownerSlot;
    }

    @Override public int getSizeInventory() { return contents.length; }
    @Override public ItemStack getStackInSlot(int i) { return contents[i]; }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        contents[slot] = stack;
        onInventoryChanged();
    }

    @Override public ItemStack decrStackSize(int slot, int count) {
        if (contents[slot] == null) return null;
        ItemStack ret;
        if (contents[slot].stackSize <= count) {
            ret = contents[slot];
            contents[slot] = null;
        } else {
            ret = contents[slot].splitStack(count);
            if (contents[slot].stackSize == 0) contents[slot] = null;
        }
        onInventoryChanged();
        return ret;
    }

    @Override public ItemStack getStackInSlotOnClosing(int slot) {
        if (contents[slot] == null) return null;
        ItemStack s = contents[slot];
        contents[slot] = null;
        return s;
    }

    @Override public String getInvName() { return I18n.getString("gui.nm.horseMenu"); }
    @Override public boolean isInvNameLocalized() { return false; }

    @Override public int getInventoryStackLimit() { return Integer.MAX_VALUE; }

    @Override public boolean isUseableByPlayer(EntityPlayer player) { return true; }
    @Override public void openChest() {}
    @Override public void closeChest() {}

    @Override
    public void onInventoryChanged() {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) return false;
        return stack.itemID == BTWItems.wheat.itemID || stack.itemID == BTWItems.straw.itemID;
    }

    public void transferAllToArmor() {
        if (owner == null || owner.worldObj == null || owner.worldObj.isRemote) return;

        ItemStack input = contents[0];
        if (input == null) return;

        int totalUnits = 0;
        if (input.itemID == BTWItems.wheat.itemID) {
            totalUnits = input.stackSize;
        } else if (input.itemID == BTWItems.straw.itemID) {
            totalUnits = input.stackSize;
        } else {

            owner.inventory.addItemStackToInventory(input);
            contents[0] = null;
            owner.openContainer.detectAndSendChanges();
            owner.inventory.onInventoryChanged();
            return;
        }

        contents[0] = null;

        if (!(armorStack.getItem() instanceof ItemAdvancedHorseArmor)) {

            owner.inventory.addItemStackToInventory(new ItemStack(input.itemID, totalUnits, input.getItemDamage()));
            owner.openContainer.detectAndSendChanges();
            owner.inventory.onInventoryChanged();
            return;
        }

        ItemAdvancedHorseArmor armorItem = (ItemAdvancedHorseArmor) armorStack.getItem();
        int added = armorItem.addWheat(armorStack, totalUnits);
        int remainder = totalUnits - added;

        if (remainder > 0) {
            owner.inventory.addItemStackToInventory(new ItemStack(input.itemID, remainder, input.getItemDamage()));
        }

        if (owner.inventory != null) {
            if (ownerSlot >= 0 && ownerSlot < owner.inventory.mainInventory.length) {
                owner.inventory.mainInventory[ownerSlot] = armorStack;
            } else {

                for (int i = 0; i < owner.inventory.mainInventory.length; i++) {
                    if (owner.inventory.mainInventory[i] == armorStack) {
                        owner.inventory.mainInventory[i] = armorStack;
                        break;
                    }
                }
            }
            owner.inventory.onInventoryChanged();
        }

        if (owner.openContainer != null) owner.openContainer.detectAndSendChanges();
    }
}

