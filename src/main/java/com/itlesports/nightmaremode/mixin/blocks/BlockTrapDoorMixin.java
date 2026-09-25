package com.itlesports.nightmaremode.mixin.blocks;

import net.minecraft.src.Block;
import net.minecraft.src.BlockTrapDoor;
import net.minecraft.src.Material;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockTrapDoor.class)
public class BlockTrapDoorMixin extends Block {
    protected BlockTrapDoorMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Override
    public boolean isFallingBlock() {
        return false;
    }
}
