package com.itlesports.nightmaremode.block.blocks;

import net.minecraft.src.BlockContainer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public class NMBlockContainer extends BlockContainer {
    protected NMBlockContainer(int i, Material material) {
        super(i, material);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return null;
    }
}
