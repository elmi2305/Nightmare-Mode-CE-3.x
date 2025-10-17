package com.itlesports.nightmaremode.nmgui;

import net.minecraft.src.*;

public class ContainerAdvancedHorseArmorSingle extends Container {
    private final IInventory armorInv;

    public ContainerAdvancedHorseArmorSingle(InventoryPlayer playerInv, IInventory armorInv) {
        this.armorInv = armorInv;

        // single insert slot
        this.addSlotToContainer(new SlotInsertOnly(armorInv, 0, 80, 20));

        // player inventory (standard layout)
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));

        for (int col = 0; col < 9; ++col)
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 109));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) { return armorInv.isUseableByPlayer(player); }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        armorInv.closeChest();
        // Make sure final conversion occurs server-side (defensive)
        if (!player.worldObj.isRemote && armorInv instanceof InventoryHorseArmor) {
            ((InventoryHorseArmor) armorInv).onInventoryChanged();
        }
    }

    // implement transferStackInSlot if you want shift-click behavior, otherwise leave null
}
