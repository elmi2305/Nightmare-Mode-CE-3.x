package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.BlockHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityLithiumAnvil;
import net.minecraft.src.Block;
import net.minecraft.src.Material;

public class BlockLithiumAnvil extends BlockHammerAnvil {
    public BlockLithiumAnvil(int id) {
        super(id, Material.rock, Block.soundMetalFootstep, "ifhyLithiumAnvil", "nightmare:ifhyLithiumAnvil");
    }

    @Override
    protected TileEntityHammerAnvil createAnvilTileEntity() {
        return new TileEntityLithiumAnvil();
    }
}
