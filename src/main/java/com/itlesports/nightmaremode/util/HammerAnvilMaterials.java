package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.ItemStack;

public final class HammerAnvilMaterials {
    private HammerAnvilMaterials() {
    }

    public static int getHitsRequired(int anvilID, HammerRecipe recipe) {
        int base = recipe.getHitsRequired();
        ItemStack input = recipe.getInput();
        if (input == null || input.getItemDamage() != 0) return base;
        int itemID = input.itemID;
        int hits = base;
        if (NMBlocks.stoneAnvil != null && anvilID == NMBlocks.stoneAnvil.blockID) {
            if (itemID == NMItems.roughStoneBrick.itemID) hits = 2;
        } else if (NMBlocks.lithiumAnvil != null && anvilID == NMBlocks.lithiumAnvil.blockID) {
            if (itemID == NMItems.lithiumRaw.itemID || itemID == NMItems.lithiumHammered.itemID) hits = 1;
        } else if (NMBlocks.ironAnvil != null && anvilID == NMBlocks.ironAnvil.blockID) {
            if (itemID == NMItems.ironBloom.itemID) hits = 2;
            else if (itemID == NMItems.carburizedIronBloom.itemID
                    || itemID == NMItems.carbonIronIngot.itemID || itemID == NMItems.reinforcedIronIngot.itemID) hits = 1;
        } else if (NMBlocks.diamondAnvil != null && anvilID == NMBlocks.diamondAnvil.blockID) {
            if (itemID == NMItems.diamondBearingRock.itemID) hits = 2;
        } else if (NMBlocks.netherrackAnvil != null && anvilID == NMBlocks.netherrackAnvil.blockID) {
            if (itemID == NMItems.coresteelIngot.itemID) hits = 5;
            else if (itemID == NMItems.deadzoneAlloyIngot.itemID) hits = 12;
        } else if (anvilID == Block.anvil.blockID) {
            if (itemID == NMItems.deadzoneAlloyIngot.itemID) hits = 6;
            else if (itemID == NMItems.phaseSteelIngot.itemID) hits = 2;
        }
        return Math.max(1, Math.min(base, hits));
    }
}
