package com.itlesports.nightmaremode.block.blocks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.blocks.templates.NMBlockOre;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class BlockEclipseOre extends NMBlockOre {
    public BlockEclipseOre(int iBlockID) {
        super(iBlockID);
    }

    @Override
    public boolean canBeMined(IBlockAccess world, int i, int j, int k) {
        return NightmareMode.worldState >= NMFields.POSTWITHER;
    }

    @Override
    public int idDroppedOnConversion(boolean var1, int var2) {
        return 0;
    }

    @Override
    public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide) {
        return super.convertBlock(stack, world, i, j, k, iFromSide);
    }

    @Override
    public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k) {
        // check tool level here
        return super.canConvertBlock(stack, world, i, j, k);
    }
}
