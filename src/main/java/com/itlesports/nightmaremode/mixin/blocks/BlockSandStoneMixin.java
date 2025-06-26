package com.itlesports.nightmaremode.mixin.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.BlockSandStone;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockSandStone.class)
public class BlockSandStoneMixin extends Block {
    protected BlockSandStoneMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Override
    public boolean canEndermenPickUpBlock(World world, int x, int y, int z) {
        return true;
    }
}
