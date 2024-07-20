package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityMagmaCube;
import net.minecraft.src.EntitySmallFireball;
import net.minecraft.src.MovingObjectPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySmallFireball.class)
public class EntitySmallFireballMixin {
    @Unique boolean hitMagmaCube;
    @Inject(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySmallFireball;setDead()V", shift = At.Shift.BEFORE))
    private void helpWithIgnoringMagmaCubes(MovingObjectPosition par1, CallbackInfo ci){
        hitMagmaCube = par1.entityHit instanceof EntityMagmaCube;
    }
    @Redirect(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySmallFireball;setDead()V"))
    private void ignoreMagmaCubeCollision(EntitySmallFireball instance){
        EntitySmallFireball thisObj = (EntitySmallFireball)(Object)this;
        if(!hitMagmaCube){thisObj.setDead();}
    }
}
