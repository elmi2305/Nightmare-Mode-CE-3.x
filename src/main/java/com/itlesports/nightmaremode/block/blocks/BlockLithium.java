package com.itlesports.nightmaremode.block.blocks;

import btw.block.blocks.OreStorageBlock;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.Block;

public class BlockLithium extends OreStorageBlock {
    public BlockLithium(int id) {
        super(id);
        this.setHardness(5.0F);
        this.setResistance(10.0F);
        this.setStepSound(Block.soundMetalFootstep);
        this.setUnlocalizedName("ifhyLithiumBlock");
        this.setTextureName("nightmare:ifhyLithiumBlock");
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }
}
