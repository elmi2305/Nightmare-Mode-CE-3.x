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
        if (stack == null || stack.getItem() == Item.pickaxeDiamond) return false;
        return isIndestructible(stack) || stack != null
                && (stack.itemID == BTWItems.creeperOysters.itemID || stack.itemID == Item.magmaCream.itemID
                    || stack.itemID == Block.netherrack.blockID || stack.getItem() == BTWItems.netherSludge
                    || stack.getItem() == BTWItems.diamondPile
                    || isNetherEquipment(stack.getItem()));
    }

    private static boolean isNetherEquipment(Item item) {
        return item == NMItems.netherrackSword
                || item == NMItems.tungstenSword || item == NMItems.tungstenShovel
                || item == NMItems.blackglassSword || item == NMItems.blackglassScythe || item == NMItems.blackglassKnife
                || item == NMItems.coresteelSword || item == NMItems.coresteelPickaxe
                || item == NMItems.coresteelAxe || item == NMItems.coresteelShovel
                || item == NMItems.deadzoneSword || item == NMItems.deadzoneScythe
                || item == BTWItems.steelSword || item == BTWItems.steelPickaxe || item == BTWItems.steelAxe
                || item == BTWItems.steelShovel || item == BTWItems.steelHoe
                || item == BTWItems.battleaxe || item == BTWItems.mattock || item == NMItems.steelHammer
                || item == NMItems.steelFishingPole || item == NMItems.steelFishingPoleBaited;
    }

    public static boolean isIndestructible(ItemStack stack) {
        if (stack == null || stack.getItem() == Item.pickaxeDiamond) return false;
        Item item = stack.getItem();
        int id = stack.itemID;
        return id == NMItems.bloodOrb.itemID
                || id == Item.netherStar.itemID
                || id == NMItems.starOfTheBloodGod.itemID
                || id == Item.blazeRod.itemID
                || id == Item.blazePowder.itemID
                || item instanceof ArcaneScrollItem
                || item instanceof INetherItem
                || item instanceof NMItem && ((NMItem)item).isIndestructible();
    }
}
