package com.itlesports.nightmaremode.crafting.recipe.types;

import btw.crafting.recipe.types.customcrafting.FishingRodBaitingRecipe;
import net.minecraft.src.*;

public final class CustomFishingRodBaitingRecipe implements IRecipe {
    private final Item rod;
    private final Item baitedRod;
    private final ResourceLocation id;

    public CustomFishingRodBaitingRecipe(String name, Item rod, Item baitedRod) {
        this.rod = rod;
        this.baitedRod = baitedRod;
        this.id = new ResourceLocation("nightmare", "baiting/" + name);
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return findRod(inventory) != null;
    }

    private ItemStack findRod(InventoryCrafting inventory) {
        ItemStack foundRod = null;
        boolean foundBait = false;
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            if (stack.itemID == this.rod.itemID && foundRod == null) foundRod = stack;
            else if (FishingRodBaitingRecipe.isFishingBait(stack) && !foundBait) foundBait = true;
            else return null;
        }
        return foundBait ? foundRod : null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack rodStack = findRod(inventory);
        if (rodStack == null) return null;
        ItemStack result = rodStack.copy();
        result.stackSize = 1;
        result.itemID = this.baitedRod.itemID;
        return result;
    }

    @Override public int getRecipeSize() { return 2; }
    @Override public ItemStack getRecipeOutput() { return new ItemStack(this.baitedRod); }
    @Override public boolean matches(IRecipe recipe) { return false; }
    @Override public boolean hasSecondaryOutput() { return false; }
    @Override public ItemStack[] getSecondaryOutput(IInventory inventory) { return null; }
    @Override public ResourceLocation getId() { return this.id; }
}
