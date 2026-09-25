package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.MouldingAndDecorativeBlock;
import btw.block.blocks.WoodMouldingAndDecorativeBlock;
import net.minecraft.src.Material;
import net.minecraft.src.StepSound;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WoodMouldingAndDecorativeBlock.class)
public class WoodMouldingAndDecorativeBlockMixin extends MouldingAndDecorativeBlock {

    public WoodMouldingAndDecorativeBlockMixin(int iBlockID, Material material, String sTextureName, String sColumnSideTextureName, int iMatchingCornerBlockID, float fHardness, float fResistance, StepSound stepSound, String name) {
        super(iBlockID, material, sTextureName, sColumnSideTextureName, iMatchingCornerBlockID, fHardness, fResistance, stepSound, name);
    }


    @Override
    public boolean isFallingBlock() {
        return false;
    }
}
