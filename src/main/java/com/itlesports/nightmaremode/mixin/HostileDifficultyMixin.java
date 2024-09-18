package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.HostileDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HostileDifficulty.class)
public class HostileDifficultyMixin {
    @Inject(method = "getAbandonmentRangeMultiplier", at = @At("RETURN"),remap = false, cancellable = true)
    private void noAbandonment(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(0f);
    }
    @Inject(method = "hasAbandonedStructures", at = @At("RETURN"),remap = false, cancellable = true)
    private void noAbandonmentStructures(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(false);
    }
}
