package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.ScrewPumpBlock;
import api.block.util.MechPowerUtils;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScrewPumpBlock.class)
public class ScrewPumpBlockMixin {
    @Inject(method = "isInputtingMechanicalPower", at = @At("HEAD"),cancellable = true)
    private void makeScrewPumpAlwaysPowered(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(world.isBlockGettingPowered(i,j,k) || MechPowerUtils.isBlockPoweredByAxleToSide(world, i,j,k,0));
    }
}
