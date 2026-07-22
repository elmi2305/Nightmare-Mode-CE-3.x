package com.itlesports.nightmaremode.block.blocks;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.blocks.templates.BlockHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityNetherrackAnvil;

public class BlockNetherrackAnvil extends BlockHammerAnvil {
    public BlockNetherrackAnvil(int id) {
        super(id, BTWBlocks.netherRockMaterial, BTWBlocks.oreStepSound,
                "ifhyNetherrackAnvil", "netherrack");
    }

    @Override
    protected TileEntityHammerAnvil createAnvilTileEntity() {
        return new TileEntityNetherrackAnvil();
    }
}
