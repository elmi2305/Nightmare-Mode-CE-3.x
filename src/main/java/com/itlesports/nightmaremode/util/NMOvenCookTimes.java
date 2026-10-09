package com.itlesports.nightmaremode.util;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.FurnaceRecipes;
import net.minecraft.src.Item;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;

public final class NMOvenCookTimes {
    private NMOvenCookTimes() {}

    public static boolean canOvercookToMeat(ItemStack stack) {
        if (!NMItemStackUtils.isValid(stack) || !(stack.getItem() instanceof ItemFood)) return false;
        Item item = stack.getItem();
        return item == Item.beefCooked || item == Item.porkCooked || item == Item.chickenCooked
                || item == Item.fishCooked || item == BTWItems.cookedMutton || item == BTWItems.cookedWolfChop
                || item == BTWItems.cookedMysteryMeat || item == BTWItems.cookedLiver || item == BTWItems.cookedCheval
                || item == BTWItems.cookedKebab || item == BTWItems.friedEgg || item == BTWItems.cookedMushroomOmelet
                || item == BTWItems.cookedScrambledEggs || item == NMItems.calamariRoast || item == NMItems.friedCalamari;
    }

    public static int getCookTime(ItemStack stack) {
        if (!NMItemStackUtils.isValid(stack)) return 1600;
        Item item = stack.getItem();
        if (item == BTWItems.unfiredCrudeBrick) return 24000;
        if (item == BTWItems.unfiredNetherBrick) return 2400;
        if (item == NMItems.unbakedChocolateCake) return 2400;

        if (item == Item.fishRaw || item == NMItems.debonedRawFish
                || item == NMItems.calamari || item == BTWItems.unbakedCookies) return 2000;
        if (item == Item.chickenRaw || item == BTWItems.rawMutton || item == BTWItems.rawWolfChop
                || item == BTWItems.rawMysteryMeat || item == BTWItems.rawLiver
                || item == BTWItems.breadDough) return 2400;
        if (item == Item.beefRaw || item == Item.porkRaw || item == BTWItems.rawCheval
                || item == BTWItems.rawKebab) return 3200;
        if (item == BTWItems.unbakedCake || item == BTWItems.unbakedPumpkinPie) return 3600;

        if (item == NMItems.wetFusedPlantSheet || item == NMItems.washedHemp) return 2400;
        if (item == NMItems.azureSlag || item == NMItems.lapisPrecipitate) return 3200;
        if (item == NMItems.tungstenConcentrate || item == NMItems.obsidianPaste) return 4800;
        if (item == NMItems.pureTungstenChunk) return 6400;

        // preserve the established long batches for alloys, glass, nickel and diamonds.
        return (400 << FurnaceRecipes.smelting().getCookTimeBinaryShift(stack.itemID)) * 4;
    }
}
