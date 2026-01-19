package com.itlesports.nightmaremode.block.blocks;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public class BlockCrudeObsidian extends NMBlock{
    public BlockCrudeObsidian(int par1) {
        super(par1, Material.rock);

        this.setHardness(30f);
        this.setResistance(35f);
        this.setUnlocalizedName("nmCrudeObsidian");
        this.setTextureName("nmCrudeObsidian");
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setPicksEffectiveOn();
    }

//    @Override
//    public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k) {
//        return true;
//    }
//
//    @Override
//    public boolean getCanBeSetOnFireDirectlyByItem(IBlockAccess blockAccess, int i, int j, int k) {
//        return true;
//    }
}
