package com.itlesports.nightmaremode.block.blocks;

import net.minecraft.src.BlockWorkbench;
import net.minecraft.src.CreativeTabs;

/** an entirely Underworld-native route back into ordinary 3x3 crafting. */
public class BlockRiftWorkbench extends BlockWorkbench {
    public BlockRiftWorkbench(int id) {
        super(id);
        this.setHardness(6.0F);
        this.setResistance(8.0F);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setUnlocalizedName("nmRiftWorkbench");
        this.setTextureName("nightmare:nmRiftWorkbench");
    }

    @Override public String getModId() { return "nightmare"; }
}
