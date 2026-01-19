package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.EntityThrowable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(EntityThrowable.class)
public class EntityThrowableMixin {
    @Redirect(method = "setThrowableHeading", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextGaussian()D"))
    private double noRandomness(Random instance){
        return 0.1;
    }


    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;canBeCollidedWith()Z"))
    private void allowSnowballsToHitPlayers(CallbackInfo ci){

    }
}
