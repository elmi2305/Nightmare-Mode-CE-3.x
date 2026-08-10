package com.itlesports.nightmaremode.block.blocks;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.blocks.templates.BlockHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityIronAnvil;
import net.minecraft.src.Material;

public class BlockIronAnvil extends BlockHammerAnvil {
    public BlockIronAnvil(int id) {
        super(id, Material.rock, BTWBlocks.gemStepSound, "ifhyIronAnvil", "nightmare:ifhyIronAnvil");
    }

    @Override
    protected TileEntityHammerAnvil createAnvilTileEntity() {
        return new TileEntityIronAnvil();
    }
}