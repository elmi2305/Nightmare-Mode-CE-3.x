package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.BlockHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityStoneAnvil;
import net.minecraft.src.Block;
import net.minecraft.src.Material;

public class BlockStoneAnvil extends BlockHammerAnvil {
    public BlockStoneAnvil(int id) {
        super(id, Material.rock, Block.soundStoneFootstep, "ifhyStoneAnvil", "nightmare:ifhyStoneAnvil");
    }

    @Override
    protected TileEntityHammerAnvil createAnvilTileEntity() {
        return new TileEntityStoneAnvil();
    }
}
