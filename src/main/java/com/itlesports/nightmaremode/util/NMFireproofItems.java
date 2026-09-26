package com.itlesports.nightmaremode.util;

import btw.item.items.ArcaneScrollItem;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public final class NMFireproofItems {
    private NMFireproofItems() {
    }

    public static boolean isFireproof(ItemStack stack) {
        return isIndestructible(stack) || stack != null && stack.itemID == BTWItems.creeperOysters.itemID;
    }

    public static boolean isIndestructible(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        int id = stack.itemID;
        return id == NMItems.bloodOrb.itemID
                || id == Item.netherStar.itemID
                || id == NMItems.starOfTheBloodGod.itemID
                || id == Item.blazeRod.itemID
                || id == Item.blazePowder.itemID
                || id == Block.obsidian.blockID
                || id == NMItems.obsidianShard.itemID
                || item instanceof ArcaneScrollItem
                || item instanceof INetherItem
                || item instanceof NMItem && ((NMItem)item).isIndestructible();
    }
}
