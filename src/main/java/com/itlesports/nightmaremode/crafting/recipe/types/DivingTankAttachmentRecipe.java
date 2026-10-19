package com.itlesports.nightmaremode.crafting.recipe.types;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.DivingTankAttachment;
import net.minecraft.src.*;

public final class DivingTankAttachmentRecipe implements IRecipe {
    private final boolean detach;

    public DivingTankAttachmentRecipe(boolean detach) { this.detach = detach; }

    private ItemStack findChest(IInventory inventory) {
        ItemStack chest = null;
        int tanks = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            if (DivingTankAttachment.isChestplate(stack)) {
                if (chest != null || DivingTankAttachment.hasAttachment(stack) != this.detach) return null;
                chest = stack;
            } else if (!this.detach && stack.getItem() == NMItems.divingTank) ++tanks;
            else return null;
        }
        return tanks == (this.detach ? 0 : 1) ? chest : null;
    }

    @Override public boolean matches(InventoryCrafting inventory, World world) { return findChest(inventory) != null; }

    @Override public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack chest = findChest(inventory);
        if (chest == null) return null;
        ItemStack result = chest.copy();
        result.stackSize = 1;
        if (this.detach) DivingTankAttachment.setTank(result, null);
        else {
            for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack != null && stack.getItem() == NMItems.divingTank) {
                    ItemStack tank = stack.copy();
                    tank.stackSize = 1;
                    DivingTankAttachment.setTank(result, tank);
                }
            }
        }
        return result;
    }

    @Override public ItemStack[] getSecondaryOutput(IInventory inventory) {
        ItemStack chest = findChest(inventory);
        ItemStack tank = this.detach ? DivingTankAttachment.getTank(chest) : null;
        return tank == null ? null : new ItemStack[]{tank.copy()};
    }

    @Override public boolean hasSecondaryOutput() { return this.detach; }
    @Override public int getRecipeSize() { return this.detach ? 1 : 2; }
    @Override public ItemStack getRecipeOutput() { return null; }
    @Override public boolean matches(IRecipe recipe) { return this == recipe; }
    @Override public ResourceLocation getId() {
        return new ResourceLocation("nightmare", this.detach ? "detach_diving_tank" : "attach_diving_tank");
    }
}
