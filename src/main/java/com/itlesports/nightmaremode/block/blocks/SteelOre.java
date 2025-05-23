package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.OreBlockStaged;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import btw.world.util.WorldUtils;
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
        this.setChiselsEffectiveOn(false);
        this.setChiselsCanHarvest(false);
    }

    public int idDropped(int iMetadata, Random rand, int iFortuneModifier) {
        return BTWItems.steelNugget.itemID;
    }

    public int idDroppedOnConversion(Difficulty difficulty, int iMetadata) {
        return BTWItems.steelNugget.itemID;
    }
    @Override
    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k) {
        if (NightmareMode.worldState > 1) {
            return 3;
        }
        return 4;
    }
    // remove this to enable all 3 steel ore blocks in the creative menu. this only registers the 1st strata block
    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(blockID, 1, 2));
    }


    @Override
    public void dropBlockAsItemWithChance(World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier) {
        super.dropBlockAsItemWithChance(world, i, j, k, iMetadata, fChance, iFortuneModifier);
        if (!world.isRemote) {
            this.dropItemsIndividually(world, i, j, k, BTWItems.steelNugget.itemID, world.rand.nextInt(5) + 4, 0, 1.0F);
        }
    }
}
