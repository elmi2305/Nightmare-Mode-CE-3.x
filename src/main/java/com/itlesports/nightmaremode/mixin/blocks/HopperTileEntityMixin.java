package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.tileentity.HopperTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(HopperTileEntity.class)
public class HopperTileEntityMixin {
    @ModifyConstant(method = "attemptToEjectXPIntoHopper", constant = @Constant(intValue = 100),remap = false)
    private int increaseExperienceCapacity(int xp){
        return 1000;
    }
    @ModifyConstant(method = "attemptToSwallowXPOrb", constant = @Constant(intValue = 100),remap = false)
    private int increaseExperienceCapacity1(int xp){
        return 1000;
    }
}
