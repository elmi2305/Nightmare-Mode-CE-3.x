package com.itlesports.nightmaremode.block.blocks;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.blocks.templates.BlockHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityDiamondAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import net.minecraft.src.Material;

public class BlockDiamondAnvil extends BlockHammerAnvil {
    public BlockDiamondAnvil(int id) {
        super(id, Material.rock, BTWBlocks.gemStepSound, "ifhyDiamondAnvil", "nightmare:ifhyDiamondAnvil");
    }

    @Override
    protected TileEntityHammerAnvil createAnvilTileEntity() {
        return new TileEntityDiamondAnvil();
    }
}
