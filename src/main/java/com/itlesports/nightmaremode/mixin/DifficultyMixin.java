package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Difficulty.class)
public class DifficultyMixin {
    @Inject(method = "isHostile", at = @At("RETURN"),cancellable = true,remap = false)
    private void alwaysHostile(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
    }
}
