package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.TileEntityBrewingStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityBrewingStand.class)
public class TileEntityBrewingStandMixin {
    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 400))
    private int reduceBrewTime(int constant){
        return 20;
    }
//    @Inject(method = "getBrewTime", at = @At("HEAD"),cancellable = true)
//    private void reduceBrewTime0(CallbackInfoReturnable<Integer> cir){
//        cir.setReturnValue(100);
//    }
}
