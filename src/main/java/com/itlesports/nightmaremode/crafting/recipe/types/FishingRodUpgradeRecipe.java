package com.itlesports.nightmaremode.crafting.recipe.types;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.ItemFishingRod;
import net.minecraft.src.IInventory;
import net.minecraft.src.IRecipe;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.World;

/** Applies one fisherman upgrade while preserving rod material, bait state, and durability. */
public class FishingRodUpgradeRecipe implements IRecipe {
    private final Item upgrade;
    private final String upgradeKey;
    private final ResourceLocation id;

    public FishingRodUpgradeRecipe(String name, Item upgrade, String upgradeKey) {
        this.upgrade = upgrade;
        this.upgradeKey = upgradeKey;
        this.id = new ResourceLocation("nightmare", name);
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        ItemStack rod = null;
        int upgrades = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack == null) continue;
            if (stack.getItem() instanceof ItemFishingRod) {
                if (rod != null) return false;
                rod = stack;
            } else if (stack.itemID == this.upgrade.itemID) {
                ++upgrades;
            } else return false;
        }
        return rod != null && upgrades == 1 && !hasUpgrade(rod, this.upgradeKey);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack rod = null;
        for (int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack != null && stack.getItem() instanceof ItemFishingRod) rod = stack;
        }
        return applyUpgrade(rod, this.upgradeKey);
    }

    public static String upgradeKey(Item attachment) {
        if (attachment == null) return null;
        if (attachment == NMItems.fishingBellUpgrade) return "IfhyFishingBell";
        if (attachment == NMItems.fishingLureUpgrade) return "IfhyFishingLure";
        if (attachment == NMItems.fishingAutoReelUpgrade) return "IfhyFishingAutoReel";
        if (attachment == NMItems.rareFishLureUpgrade) return "IfhyRareFishLure";
        if (attachment == NMItems.twinHookUpgrade) return "IfhyTwinHook";
        if (attachment == NMItems.depthWinchUpgrade) return "IfhyDepthWinch";
        if (attachment == NMItems.wideHookUpgrade) return "IfhyWideHook";
        return null;
    }

    public static ItemStack applyUpgrade(ItemStack rod, String upgradeKey) {
        if (rod == null || !(rod.getItem() instanceof ItemFishingRod) || upgradeKey == null || hasUpgrade(rod, upgradeKey)) return null;
        ItemStack result = rod.copy();
        result.stackSize = 1;
        NBTTagCompound tag = result.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            result.setTagCompound(tag);
        }
        tag.setBoolean(upgradeKey, true);
        return result;
    }

    public static boolean hasUpgrade(ItemStack stack, String key) {
        return stack != null && stack.hasTagCompound() && stack.getTagCompound().getBoolean(key);
    }

    @Override public int getRecipeSize() { return 2; }
    @Override public ItemStack getRecipeOutput() { return null; }
    @Override public boolean matches(IRecipe recipe) { return recipe == this; }
    @Override public boolean hasSecondaryOutput() { return false; }
    @Override public ItemStack[] getSecondaryOutput(IInventory inventory) { return null; }
    @Override public ResourceLocation getId() { return this.id; }
}
