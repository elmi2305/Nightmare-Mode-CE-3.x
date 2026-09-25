package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.WoodSlabBlock;
import net.minecraft.src.BlockHalfSlab;
import net.minecraft.src.Material;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WoodSlabBlock.class)
public abstract class WoodSlabBlockMixin extends BlockHalfSlab {
    public WoodSlabBlockMixin(int par1, boolean par2, Material par3Material) {
        super(par1, par2, par3Material);
    }

    @Override
    public boolean isFallingBlock() {
        return false;
    }
}
