package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.FenceBlock;
import btw.block.blocks.FenceBlockWood;
import net.minecraft.src.Material;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FenceBlockWood.class)
public class FenceBlockWoodMixin extends FenceBlock {

    public FenceBlockWoodMixin(int iBlockID, String sIconName, Material material) {
        super(iBlockID, sIconName, material);
    }


    @Override
    public boolean isFallingBlock() {
        return false;
    }
}
