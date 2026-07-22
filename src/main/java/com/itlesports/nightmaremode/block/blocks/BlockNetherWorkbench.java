package com.itlesports.nightmaremode.block.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BlockWorkbench;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;

public class BlockNetherWorkbench extends BlockWorkbench {
    public BlockNetherWorkbench(int id) {
        super(id);
        this.setHardness(2.5F);
        this.setResistance(5.0F);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setUnlocalizedName("ifhyNetherWorkbench");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("netherrack");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return this.blockIcon;
    }
}
