package com.itlesports.nightmaremode.block.tileEntities;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.ItemStack;

public class TungstenForgeTileEntity extends HellforgeTileEntity {
    @Override public String getInvName() { return "container.ifhyTungstenForge"; }

    @Override protected int getCookTimeForCurrentItem() {
        int ticks = super.getCookTimeForCurrentItem();
        return Math.max(1, (ticks + 2) / 3);
    }

    private boolean isRawTungsten() {
        return this.furnaceItemStacks[0] != null && this.furnaceItemStacks[0].getItem() == NMItems.tungstenChunk;
    }

    @Override protected boolean canSmelt() {
        if (isRawTungsten()) return this.furnaceItemStacks[2] == null;
        return super.canSmelt();
    }

    @Override public void smeltItem() {
        if (!isRawTungsten()) {
            super.smeltItem();
            return;
        }
        if (!this.canSmelt()) return;
        this.furnaceItemStacks[2] = new ItemStack(NMItems.tungstenNugget);
        if (--this.furnaceItemStacks[0].stackSize <= 0) this.furnaceItemStacks[0] = null;
    }

    @Override public void updateEntity() {
        if (this.worldObj != null && !this.worldObj.isRemote && this.furnaceItemStacks[1] != null) {
            ItemStack fuel = this.furnaceItemStacks[1];
            if (fuel.getItem().getCanBeFedDirectlyIntoBrickOven(fuel.getItemDamage()) && this.getItemBurnTime(fuel) > 0) {
                int used = this.attemptToAddFuel(fuel);
                if (used > 0) {
                    fuel.stackSize -= used;
                    if (fuel.stackSize <= 0) this.furnaceItemStacks[1] = null;
                    this.onInventoryChanged();
                }
            }
        }
        super.updateEntity();
    }

    @Override public int[] getSlotsForFace(int side) { return side == 0 ? new int[]{2} : side == 1 ? new int[]{0} : new int[]{1}; }
    @Override public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return stack != null && (slot == 0 && (stack.getItem() == NMItems.tungstenChunk
                || net.minecraft.src.FurnaceRecipes.smelting().getSmeltingResult(stack.itemID) != null)
                || slot == 1 && stack.getItem().getCanBeFedDirectlyIntoBrickOven(stack.getItemDamage()) && this.getItemBurnTime(stack) > 0);
    }
    @Override public boolean canExtractItem(int slot, ItemStack stack, int side) { return slot == 2; }
}
