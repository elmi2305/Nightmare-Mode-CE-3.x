package com.itlesports.nightmaremode.crafting.recipe.types;

import net.minecraft.src.*;

public class QuestToolRepairRecipe implements IRecipe {
    private final Item brokenItem;
    private final Item repairMaterial;
    private final int repairMetadata;
    private final Item completedItem;
    private final ResourceLocation id;

    public QuestToolRepairRecipe(String name, Item brokenItem, Item repairMaterial, int repairMetadata, Item completedItem) {
        this.brokenItem = brokenItem;
        this.repairMaterial = repairMaterial;
        this.repairMetadata = repairMetadata;
        this.completedItem = completedItem;
        this.id = new ResourceLocation("nightmare", name);
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return this.findBroken(inventory) != null && this.hasOneMaterial(inventory);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack broken = this.findBroken(inventory);
        if (broken == null || !this.hasOneMaterial(inventory)) return null;
        if (broken.getItemDamage() <= 1) return new ItemStack(this.completedItem);
        ItemStack result = broken.copy();
        result.stackSize = 1;
        result.setItemDamage(broken.getItemDamage() - 1);
        return result;
    }

    private ItemStack findBroken(InventoryCrafting inventory) {
        ItemStack found = null;
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            if (stack.itemID == this.brokenItem.itemID) {
                if (found != null) return null;
                found = stack;
            } else if (stack.itemID != this.repairMaterial.itemID
                    || this.repairMetadata >= 0 && stack.getItemDamage() != this.repairMetadata) return null;
        }
        return found;
    }

    private boolean hasOneMaterial(InventoryCrafting inventory) {
        int materials = 0;
        int occupied = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            ++occupied;
            if (stack.itemID == this.repairMaterial.itemID
                    && (this.repairMetadata < 0 || stack.getItemDamage() == this.repairMetadata)) ++materials;
        }
        return occupied == 2 && materials == 1;
    }

    @Override public int getRecipeSize() { return 2; }
    @Override public ItemStack getRecipeOutput() { return new ItemStack(this.completedItem); }
    @Override public boolean matches(IRecipe recipe) { return recipe == this; }
    @Override public boolean hasSecondaryOutput() { return false; }
    @Override public ItemStack[] getSecondaryOutput(IInventory inventory) { return null; }
    @Override public ResourceLocation getId() { return this.id; }
}
