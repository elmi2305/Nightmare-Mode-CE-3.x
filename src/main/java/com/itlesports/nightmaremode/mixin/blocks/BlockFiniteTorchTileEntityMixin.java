package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.FiniteTorchTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FiniteTorchTileEntity.class)
public class BlockFiniteTorchTileEntityMixin {
    @ModifyConstant(method = "updateEntity", constant = @Constant(floatValue = 0.01f, ordinal = 1))
    private float modifyChanceOfFireSpread(float constant) {
        return 10000f;
    }
}
