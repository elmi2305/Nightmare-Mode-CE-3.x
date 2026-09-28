package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.BTWBlocks;
import btw.block.blocks.BasketBlock;
import btw.block.blocks.WickerBasketBlock;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WickerBasketBlock.class)
public abstract class WickerBasketBlockMixin extends BasketBlock {
    protected WickerBasketBlockMixin(int id, Material material) {
        super(id, material);
    }

    @Override
    public int getEfficientToolLevel(IBlockAccess access, int x, int y, int z) {
        return 0;
    }

    @Inject(method = "dropComponentItemsOnBadBreak", at = @At("HEAD"), cancellable = true)
    private void dropBasketOnHandBreak(World world, int x, int y, int z, int metadata, float chance,
                                       CallbackInfoReturnable<Boolean> cir) {
        this.dropItemsIndividually(world, x, y, z, BTWBlocks.wickerBasket.blockID, 1, 0, 1);
        cir.setReturnValue(true);
    }
}
