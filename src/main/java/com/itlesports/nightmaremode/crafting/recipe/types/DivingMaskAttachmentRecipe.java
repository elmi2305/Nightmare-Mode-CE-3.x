package com.itlesports.nightmaremode.crafting.recipe.types;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.DivingMaskAttachment;
import net.minecraft.src.*;

public final class DivingMaskAttachmentRecipe implements IRecipe {
    private final boolean detach;

    public DivingMaskAttachmentRecipe(boolean detach) { this.detach = detach; }

    private ItemStack findHelmet(IInventory inventory) {
        ItemStack helmet = null;
        int masks = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            if (stack.stackSize != 1) return null;
            if (DivingMaskAttachment.isHelmet(stack)) {
                if (helmet != null || DivingMaskAttachment.hasAttachment(stack) != this.detach) return null;
                if (this.detach && DivingMaskAttachment.getMask(stack) == null) return null;
                helmet = stack;
            } else if (!this.detach && stack.getItem() == NMItems.divingMask) ++masks;
            else return null;
        }
        return masks == (this.detach ? 0 : 1) ? helmet : null;
    }

    @Override public boolean matches(InventoryCrafting inventory, World world) { return findHelmet(inventory) != null; }

    @Override public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack helmet = findHelmet(inventory);
        if (helmet == null) return null;
        ItemStack result = helmet.copy();
        result.stackSize = 1;
        if (this.detach) DivingMaskAttachment.setMask(result, null);
        else {
            for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack != null && stack.getItem() == NMItems.divingMask) {
                    DivingMaskAttachment.setMask(result, stack.copy());
                }
            }
        }
        return result;
    }

    @Override public ItemStack[] getSecondaryOutput(IInventory inventory) {
        ItemStack helmet = findHelmet(inventory);
        ItemStack mask = this.detach ? DivingMaskAttachment.getMask(helmet) : null;
        return mask == null ? null : new ItemStack[]{mask.copy()};
    }

    @Override public boolean hasSecondaryOutput() { return this.detach; }
    @Override public int getRecipeSize() { return this.detach ? 1 : 2; }
    @Override public ItemStack getRecipeOutput() { return null; }
    @Override public boolean matches(IRecipe recipe) { return this == recipe; }
    @Override public ResourceLocation getId() {
        return new ResourceLocation("nightmare", this.detach ? "detach_diving_mask" : "attach_diving_mask");
    }
}
