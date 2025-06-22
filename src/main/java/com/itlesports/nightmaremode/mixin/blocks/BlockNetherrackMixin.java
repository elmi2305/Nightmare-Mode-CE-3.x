package com.itlesports.nightmaremode.mixin.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.BlockNetherrack;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockNetherrack.class)
public class BlockNetherrackMixin extends Block {
    protected BlockNetherrackMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Override
    public boolean isBreakableBarricade(World world, int i, int j, int k) {
        return true;
    }
}
