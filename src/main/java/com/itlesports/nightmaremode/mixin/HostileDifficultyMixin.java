package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.HostileDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HostileDifficulty.class)
public class HostileDifficultyMixin {
    @Inject(method = "getAbandonmentRangeMultiplier", at = @At("RETURN"),cancellable = true,remap = false)
    private void noAbandonment(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(0f);
    }
    @Inject(method = "hasAbandonedStructures", at = @At("RETURN"),cancellable = true,remap = false)
    private void noAbandonment1(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(false);
    }
}
