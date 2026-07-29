package com.itlesports.nightmaremode.block.blocks;

import api.block.blocks.StairsBlock;
import net.minecraft.src.Block;

/** Basic stairs which inherit their appearance and material properties from prismarine. */
public class BlockPrismarineStairs extends StairsBlock {
    public BlockPrismarineStairs(int blockID, Block prismarine) {
        super(blockID, prismarine, 0);
        this.setPicksEffectiveOn();
        this.setUnlocalizedName("nmPrismarineStairs");
    }
}
