package com.itlesports.nightmaremode.mixin;

import btw.block.tileentity.UnfiredBrickTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(UnfiredBrickTileEntity.class)
public class UnfiredBrickTileEntityMixin {
    @ModifyConstant(method = "updateCooking", constant = @Constant(intValue = 11900),remap = false)
    private int reduceClayCookTime(int constant){
        return 11600;
    }
}
