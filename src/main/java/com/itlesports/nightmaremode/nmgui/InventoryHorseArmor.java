package com.itlesports.nightmaremode.nmgui;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import net.minecraft.src.*;

public class InventoryHorseArmor implements IInventory {
    private final EntityPlayer owner;     // server-side player who opened GUI
    private final int ownerSlot;          // hotbar slot index where the armor is (pass player.inventory.currentItem)
    private final ItemStack armorStack;   // the ItemStack being modified (the real instance in player's inventory)
    private final ItemStack[] contents = new ItemStack[1]; // a single input slot

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
        // nothing special here; we keep conversion for onContainerClosed on server
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) return false;
        return stack.itemID == BTWItems.wheat.itemID || stack.itemID == BTWItems.straw.itemID;
    }

    /** Call this on the server when the GUI/container closes to actually apply fuel to the armor. */
    public void transferAllToArmor() {
        if (owner == null || owner.worldObj == null || owner.worldObj.isRemote) return; // server only

        ItemStack input = contents[0];
        if (input == null) return;

        // only handle wheat/straw; preserve item metadata if returning remainder
        int totalUnits = 0;
        if (input.itemID == BTWItems.wheat.itemID) {
            totalUnits = input.stackSize;
        } else if (input.itemID == BTWItems.straw.itemID) {
            totalUnits = input.stackSize; // convert 1 straw -> 1 wheat equivalence; change as needed
        } else {
            // Not a fuel: try to return it to player inventory
            owner.inventory.addItemStackToInventory(input);
            contents[0] = null;
            owner.openContainer.detectAndSendChanges();
            owner.inventory.onInventoryChanged();
            return;
        }

        contents[0] = null; // we will consume (or return remainder) below

        if (!(armorStack.getItem() instanceof ItemAdvancedHorseArmor)) {
            // armor not the right type — return all to player
            owner.inventory.addItemStackToInventory(new ItemStack(input.itemID, totalUnits, input.getItemDamage()));
            owner.openContainer.detectAndSendChanges();
            owner.inventory.onInventoryChanged();
            return;
        }

        ItemAdvancedHorseArmor armorItem = (ItemAdvancedHorseArmor) armorStack.getItem();
        int added = armorItem.addWheat(armorStack, totalUnits); // returns number actually added (clamped)
        int remainder = totalUnits - added;

        // return remainder to player inventory if any
        if (remainder > 0) {
            owner.inventory.addItemStackToInventory(new ItemStack(input.itemID, remainder, input.getItemDamage()));
        }

        // Replace the player's hotbar slot with the updated armorStack instance to ensure server->client sync.
        if (owner.inventory != null) {
            if (ownerSlot >= 0 && ownerSlot < owner.inventory.mainInventory.length) {
                owner.inventory.mainInventory[ownerSlot] = armorStack;
            } else {
                // fallback: find and replace reference with armorStack
                for (int i = 0; i < owner.inventory.mainInventory.length; i++) {
                    if (owner.inventory.mainInventory[i] == armorStack) {
                        owner.inventory.mainInventory[i] = armorStack;
                        break;
                    }
                }
            }
            owner.inventory.onInventoryChanged();
        }

        // Force container -> client slot sync so client sees cleared input slot and updated armor stack.
        if (owner.openContainer != null) owner.openContainer.detectAndSendChanges();
    }
}

