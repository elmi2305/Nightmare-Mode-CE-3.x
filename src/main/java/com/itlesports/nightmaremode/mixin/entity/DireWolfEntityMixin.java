package com.itlesports.nightmaremode.mixin.entity;

import btw.entity.mob.DireWolfEntity;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DireWolfEntity.class)
public class DireWolfEntityMixin {
    @Inject(method = "onLivingUpdate", at = @At("HEAD"), cancellable = true)
    private void stopDireWolfCarcassUpdate(CallbackInfo ci) {
        if ((Object)this instanceof CarcassAnimal carcass && carcass.nm$isCarcass()) {
            ci.cancel();
        }
    }

    @Inject(method = "getSoundVolume", at = @At("HEAD"),cancellable = true)
    private void lowerSoundVolume(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(2.0f);
    }
}
