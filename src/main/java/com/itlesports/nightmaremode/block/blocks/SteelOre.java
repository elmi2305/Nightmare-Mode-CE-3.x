package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.OreBlockStaged;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class SteelOre extends OreBlockStaged {
    public SteelOre(int iBlockID) {
        super(iBlockID);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public int idDropped(int iMetadata, Random rand, int iFortuneModifier) {
        return BTWItems.steelNugget.itemID;
    }

    public int idDroppedOnConversion(Difficulty difficulty, int iMetadata) {
        return BTWItems.steelNugget.itemID;
    }
    @Override
    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k) {
        return 4;
    }
    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(blockID, 1, 0));
    }
    @Override
    public void dropBlockAsItemWithChance(World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier) {
        if (!world.isRemote) {
            this.dropItemsIndividually(world, i, j, k, BTWItems.steelNugget.itemID, world.rand.nextInt(4)+2, 0, 1.0F);
            this.dropItemsIndividually(world, i, j, k, BTWItems.stone.itemID, 6, 2, 1.0F);
        }
    }
}
