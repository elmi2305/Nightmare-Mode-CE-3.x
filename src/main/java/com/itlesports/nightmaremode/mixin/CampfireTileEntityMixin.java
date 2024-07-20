package com.itlesports.nightmaremode.mixin;

import btw.block.tileentity.CampfireTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireTileEntity.class)
public class CampfireTileEntityMixin {
    @Shadow(remap = false) private int cookBurningCounter;

    @Inject(method = "updateCookState", at = @At(value = "FIELD", target ="Lbtw/item/BTWItems;burnedMeat:Lnet/minecraft/src/Item;", shift = At.Shift.AFTER))
    private void incrementBurnTimer(CallbackInfo ci){
        this.cookBurningCounter+=5;         // food burns 6x faster, taking 20 seconds to burn instead of 2 minutes
    }
}
