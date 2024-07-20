package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.behavior.WolfHowlBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// WOLVES PLEASE SHUT THE FUCK UP
// only tamed sitting wolves howl now, but that shouldn't be a problem

@Mixin(WolfHowlBehavior.class)
public class WolfHowlBehaviorMixin {
    @Inject(method = "shouldExecute", at = @At("HEAD"), cancellable = true)
    private void alwaysReturnFalse(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
