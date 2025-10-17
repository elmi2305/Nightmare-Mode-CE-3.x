package com.itlesports.nightmaremode.nmgui;

import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import net.minecraft.src.*;

public class ContainerHorseArmor extends Container {
    private final IInventory inv;

    public ContainerHorseArmor(InventoryPlayer playerInv, IInventory inv) {
        this.inv = inv;
        this.addSlotToContainer(new SlotInstantConsume(inv, 0, 80, 35));

        // player inventory slots
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; ++col)
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
    }
    @Override
    public ItemStack slotClick(int slotId, int mouseButton, int modifier, EntityPlayer player) {
        Slot slot = slotId >= 0 && slotId < inventorySlots.size() ? (Slot) inventorySlots.get(slotId) : null;
        if (slot != null && slot.getHasStack() && slot.getStack().getItem() instanceof ItemAdvancedHorseArmor) {
            // block any interaction with horse armor itself
            return null;
        }
        return super.slotClick(slotId, mouseButton, modifier, player);
    }


    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return inv.isUseableByPlayer(player);
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        inv.closeChest();

        if (!player.worldObj.isRemote && inv instanceof InventoryHorseArmor) {
            ((InventoryHorseArmor) inv).transferAllToArmor();
        }
    }

}
