package com.itlesports.nightmaremode.underworld.crafting;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public final class UnderforgeRecipe {
    private final ItemStack base;
    private final ItemStack metal;
    private final ItemStack flux;
    private final ItemStack fuel;
    private final ItemStack output;
    private final boolean transferToolData;

    public UnderforgeRecipe(ItemStack base, ItemStack metal, ItemStack flux, ItemStack fuel, ItemStack output, boolean transferToolData) {
        this.base = base;
        this.metal = metal;
        this.flux = flux;
        this.fuel = fuel;
        this.output = output;
        this.transferToolData = transferToolData;
    }

    public boolean matches(ItemStack[] inventory) {
        return matches(inventory[0], base) && matches(inventory[1], metal)
                && matches(inventory[2], flux) && matches(inventory[3], fuel);
    }

    private boolean matches(ItemStack actual, ItemStack expected) {
        if (expected == null) return actual == null;
        return actual != null && actual.itemID == expected.itemID
                && (expected.getItemDamage() == Short.MAX_VALUE || actual.getItemDamage() == expected.getItemDamage())
                && actual.stackSize >= expected.stackSize;
    }

    public ItemStack createOutput(ItemStack input) {
        ItemStack result = output.copy();
        if (transferToolData && input != null) {
            if (input.hasTagCompound()) result.setTagCompound((NBTTagCompound)input.getTagCompound().copy());
            if (input.isItemStackDamageable() && result.isItemStackDamageable()) {
                double remaining = 1.0 - (double)input.getItemDamage() / Math.max(1, input.getMaxDamage());
                result.setItemDamage((int)Math.round(result.getMaxDamage() * (1.0 - remaining)));
            }
        }
        return result;
    }

    public void consume(ItemStack[] inventory) {
        consume(inventory, 0, base);
        consume(inventory, 1, metal);
        consume(inventory, 2, flux);
        consume(inventory, 3, fuel);
    }

    private void consume(ItemStack[] inventory, int slot, ItemStack expected) {
        if (expected == null) return;
        inventory[slot].stackSize -= expected.stackSize;
        if (inventory[slot].stackSize <= 0) inventory[slot] = null;
    }

    public ItemStack getOutputTemplate() { return output; }
    public ItemStack getBase() { return base; }
    public ItemStack getMetal() { return metal; }
    public ItemStack getFlux() { return flux; }
    public ItemStack getFuel() { return fuel; }
}
