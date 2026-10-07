package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.FiredBrickBlock;
import btw.item.BTWItems;
import net.minecraft.src.CreativeTabs;
import java.util.Random;

public class BlockPlacedNetherBrick extends FiredBrickBlock {
    public BlockPlacedNetherBrick(int id) {
        super(id);
        this.setUnlocalizedName("ifhyPlacedNetherBrick");
        this.setTextureName("nightmare:ifhyPlacedNetherBrick");
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public int idDropped(int metadata, Random random, int fortune) {
        return BTWItems.netherBrick.itemID;
    }
}
