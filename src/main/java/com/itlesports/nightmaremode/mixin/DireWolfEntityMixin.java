package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.DireWolfEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DireWolfEntity.class)
public class DireWolfEntityMixin {
    @Inject(method = "getSoundVolume", at = @At("HEAD"),cancellable = true)
    private void lowerSoundVolume(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(0.5f);
    }
}
