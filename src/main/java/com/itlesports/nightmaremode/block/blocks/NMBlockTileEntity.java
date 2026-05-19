package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.NMBlock;
import net.minecraft.src.ITileEntityProvider;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public abstract class NMBlockTileEntity extends NMBlock implements ITileEntityProvider {

    public NMBlockTileEntity(int id, Material m) {
        super(id, m);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return null;
    }

    @Override
    public void breakBlock(World w, int x, int y, int z, int par5, int par6) {
        super.breakBlock(w, x, y, z, par5, par6);
        w.removeBlockTileEntity(x,y,z);
    }
}
