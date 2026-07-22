package com.itlesports.nightmaremode.util;

import btw.item.BTWItems;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public final class NetherItemHelper {
    private NetherItemHelper() {
    }

    public static boolean survivesNetherEntry(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof INetherItem) {
            return true;
        }
        int id = stack.itemID;
        return id == Block.netherrack.blockID
                || id == Block.slowSand.blockID
                || id == Block.glowStone.blockID
                || id == Block.oreNetherQuartz.blockID
                || id == Block.netherBrick.blockID
                || id == Block.netherFence.blockID
                || id == Block.stairsNetherBrick.blockID
                || id == Block.blockNetherQuartz.blockID
                || id == Block.netherStalk.blockID
                || id == BTWBlocks.netherGroth.blockID
                || id == BTWBlocks.netherSludge.blockID
                || id == BTWBlocks.looseNetherBrick.blockID
                || id == BTWBlocks.looseNetherBrickSlab.blockID
                || id == BTWBlocks.looseNetherBrickStairs.blockID
                || id == BTWBlocks.nethercoalBlock.blockID
                || id == Item.glowstone.itemID
                || id == Item.blazeRod.itemID
                || id == Item.ghastTear.itemID
                || id == Item.netherStalkSeeds.itemID
                || id == Item.blazePowder.itemID
                || id == Item.magmaCream.itemID
                || id == Item.netherStar.itemID
                || id == Item.netherQuartz.itemID
                || id == Item.netherrackBrick.itemID
                || id == BTWItems.nethercoal.itemID
                || id == BTWItems.hellfireDust.itemID
                || id == BTWItems.concentratedHellfire.itemID
                || id == BTWItems.soulUrn.itemID
                || id == BTWItems.soulDust.itemID
                || id == BTWItems.netherBrick.itemID
                || id == BTWItems.netherGrothSpores.itemID
                || id == BTWItems.brimstone.itemID
                || id == BTWItems.groundNetherrack.itemID
                || id == BTWItems.soulSandPile.itemID
                || id == BTWItems.soulFlux.itemID
                || id == BTWItems.netherSludge.itemID
                || id == BTWItems.unfiredNetherBrick.itemID;
    }
}
