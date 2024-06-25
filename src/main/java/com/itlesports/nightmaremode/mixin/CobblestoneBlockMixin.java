package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.CobblestoneBlock;
import btw.item.BTWItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(CobblestoneBlock.class)
public class CobblestoneBlockMixin {
    /**
     * @author elmi
     * @reason changing cobblestone blocks to drop a loose stone instead of a full block.
     */
    @Overwrite
    public int idDropped(int iMetadata, Random rand, int iFortuneModifier) {
        return BTWItems.stone.itemID;
    }
}
