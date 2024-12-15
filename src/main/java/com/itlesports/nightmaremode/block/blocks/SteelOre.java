package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.OreBlockStaged;
import btw.world.util.difficulty.Difficulty;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Item;

import java.util.Random;

public class SteelOre extends OreBlockStaged {
    public SteelOre(int iBlockID) {
        super(iBlockID);
        this.setCreativeTab(CreativeTabs.tabBlock);

    }

    public int idDropped(int iMetadata, Random rand, int iFortuneModifier) {
        return Item.diamond.itemID;
    }

    public int idDroppedOnConversion(Difficulty difficulty, int iMetadata) {
        return Item.diamond.itemID;
    }

    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k) {
        return 2;
    }
}
