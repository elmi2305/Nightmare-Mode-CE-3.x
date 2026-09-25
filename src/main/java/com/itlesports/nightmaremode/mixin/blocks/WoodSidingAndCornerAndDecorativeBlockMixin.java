package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.SidingAndCornerAndDecorativeBlock;
import btw.block.blocks.WoodSidingAndCornerAndDecorativeBlock;
import net.minecraft.src.Material;
import net.minecraft.src.StepSound;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WoodSidingAndCornerAndDecorativeBlock.class)
public class WoodSidingAndCornerAndDecorativeBlockMixin extends SidingAndCornerAndDecorativeBlock {

    public WoodSidingAndCornerAndDecorativeBlockMixin(int iBlockID, Material material, String sTextureName, float fHardness, float fResistance, StepSound stepSound, String name) {
        super(iBlockID, material, sTextureName, fHardness, fResistance, stepSound, name);
    }

    @Override
    public boolean isFallingBlock() {
        return false;
    }
}
