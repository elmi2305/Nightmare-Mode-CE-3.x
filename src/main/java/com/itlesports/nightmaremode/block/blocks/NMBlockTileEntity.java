package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.NMBlock;
import net.minecraft.src.ITileEntityProvider;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public abstract class NMBlockTileEntity extends NMBlock implements ITileEntityProvider {

    public NMBlockTileEntity(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return null;
    }
}
