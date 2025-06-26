package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.CobblestoneBlock;
import btw.item.BTWItems;
import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CobblestoneBlock.class)
public class CobblestoneBlockMixin extends Block {
    protected CobblestoneBlockMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "idDropped", at = @At("RETURN"), cancellable = true)
    private void dropStoneOnBreak(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(BTWItems.stone.itemID);
        // makes mortared cobblestone drop 1 loose rock instead of the full cobblestone block. this is done to nerf day 1 village strategies
    }

    @Override
    public boolean canEndermenPickUpBlock(World world, int x, int y, int z) {
        return true;
    }
}
