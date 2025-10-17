package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.CookingVesselTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CookingVesselTileEntity.class)
public class CookingVesselTileEntityMixin {
    @ModifyConstant(method = "performNormalFireUpdate", constant = @Constant(intValue = 4350),remap = false)
    private int lowerCookTimeNormal(int constant){
        return Math.max(constant - 2800, 400);
    }
    @ModifyConstant(method = "performStokedFireUpdate", constant = @Constant(intValue = 4350),remap = false)
    private int lowerCookTimeStoked(int constant){
        return Math.max(constant - 2800, 400);
    }

    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 4350))
    private int lowerCookTimeVisually(int constant){
        return Math.max(constant - 2800, 400);
    }
}
