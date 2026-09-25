package com.itlesports.nightmaremode.mixin.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.BlockWood;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockWood.class)
public class BlockWoodMixin extends Block {

    protected BlockWoodMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Override
    public boolean canEndermenPickUpBlock(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean isFallingBlock() {
        return false;
    }
}
