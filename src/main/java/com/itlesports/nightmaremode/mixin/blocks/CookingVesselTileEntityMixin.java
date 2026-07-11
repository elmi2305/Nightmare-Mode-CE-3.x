package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.CookingVesselTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CookingVesselTileEntity.class)
public class CookingVesselTileEntityMixin {
    @Shadow(remap = false) protected int stokedCooldownCounter;

    @ModifyConstant(method = "performNormalFireUpdate", constant = @Constant(intValue = 4350),remap = false)
    private int lowerCookTimeNormal(int constant){
        return 24000;
    }
    @ModifyConstant(method = "performStokedFireUpdate", constant = @Constant(intValue = 4350),remap = false)
    private int lowerCookTimeStoked(int constant){
        return 48000;
    }

    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 4350))
    private int lowerCookTimeVisually(int constant){
        if(this.stokedCooldownCounter > 0){
            return 48000;
        }
        return 24000;
    }
}
