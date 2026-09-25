package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.StairsBlock;
import btw.block.blocks.StairsBlockBase;
import net.minecraft.src.Material;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StairsBlock.class)
public class StairsBlockMixin extends StairsBlockBase {
    protected StairsBlockMixin(int iBlockID, Material material) {
        super(iBlockID, material);
    }

    @Override
    public boolean isFallingBlock() {
        return false;
    }
}
