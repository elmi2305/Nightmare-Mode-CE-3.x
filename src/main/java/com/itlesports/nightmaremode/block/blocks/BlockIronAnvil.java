package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.BlockHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityIronAnvil;
import net.minecraft.src.Material;

public class BlockIronAnvil extends BlockHammerAnvil {
    public BlockIronAnvil(int id) {
        super(id, Material.rock, net.minecraft.src.Block.soundMetalFootstep, "ifhyIronAnvil", "nightmare:ifhyIronAnvil");
    }

    @Override
    protected TileEntityHammerAnvil createAnvilTileEntity() {
        return new TileEntityIronAnvil();
    }
}
