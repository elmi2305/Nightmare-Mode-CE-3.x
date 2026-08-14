package com.itlesports.nightmaremode.crafting.recipe.types;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

public final class EnderAssemblerRecipe {
    private final ItemStack[] ingredients;
    private final ItemStack output;
    private final int duration;

    public EnderAssemblerRecipe(ItemStack output, int duration, ItemStack... ingredients) {
        this.output = output.copy();
        this.duration = duration;
        this.ingredients = new ItemStack[ingredients.length];
        for (int i = 0; i < ingredients.length; ++i) this.ingredients[i] = ingredients[i].copy();
    }

    public boolean matches(IInventory inventory) {
        for (ItemStack ingredient : this.ingredients) {
            int found = 0;
            for (int slot = 0; slot < 6; ++slot) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack != null && stack.isItemEqual(ingredient)) found += stack.stackSize;
            }
            if (found < ingredient.stackSize) return false;
        }
        for (int slot = 0; slot < 6; ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            boolean used = false;
            for (ItemStack ingredient : this.ingredients) {
                if (stack.isItemEqual(ingredient)) { used = true; break; }
            }
            if (!used) return false;
        }
        return true;
    }

    public void consume(IInventory inventory) {
        for (ItemStack ingredient : this.ingredients) {
            int remaining = ingredient.stackSize;
            for (int slot = 0; slot < 6 && remaining > 0; ++slot) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (stack == null || !stack.isItemEqual(ingredient)) continue;
                int taken = Math.min(remaining, stack.stackSize);
                inventory.decrStackSize(slot, taken);
                remaining -= taken;
            }
        }
    }

    public ItemStack getOutput() { return this.output.copy(); }
    public ItemStack[] getIngredients() {
        ItemStack[] copy = new ItemStack[this.ingredients.length];
        for (int i = 0; i < this.ingredients.length; ++i) copy[i] = this.ingredients[i].copy();
        return copy;
    }
    public int getDuration() { return this.duration; }
}
