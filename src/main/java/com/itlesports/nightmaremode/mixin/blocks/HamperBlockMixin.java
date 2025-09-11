package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.BTWBlocks;
import btw.block.blocks.BasketBlock;
import btw.block.blocks.HamperBlock;
import btw.item.BTWItems;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HamperBlock.class)
public abstract class HamperBlockMixin extends BasketBlock {
    protected HamperBlockMixin(int iBlockID, Material material) {
        super(iBlockID, material);
    }

    @Inject(method = "getEfficientToolLevel", at = @At("RETURN"),cancellable = true)
    private void allowMiningWithBadTool(IBlockAccess blockAccess, int i, int j, int k, CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(0);
    }
    @Inject(method = "dropComponentItemsOnBadBreak", at = @At("HEAD"), cancellable = true)
    private void dropItemOnHamperBreak(World world, int x, int y, int z, int metadata, float dropChance, CallbackInfoReturnable<Boolean> cir) {
        this.dropItemsIndividually(world, x, y, z, BTWBlocks.hamper.blockID, 1, 0, 1);
        cir.setReturnValue(true);
    }
}
