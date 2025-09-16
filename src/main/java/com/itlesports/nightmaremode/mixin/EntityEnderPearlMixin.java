package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityEnderPearl;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.MovingObjectPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityEnderPearl.class)
public class EntityEnderPearlMixin {
    @Inject(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityEnderPearl;setDead()V"))
    private void teleportEntity(MovingObjectPosition movingObjectPosition, CallbackInfo ci){
        EntityEnderPearl thisObj = (EntityEnderPearl) (Object)this;
        if(thisObj.getThrower() != null){
            thisObj.getThrower().setPositionAndUpdate(thisObj.posX, thisObj.posY, thisObj.posZ);
        }
    }
    @Redirect(method = "onImpact", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"))
    private boolean manageNoHitPearling(EntityLivingBase instance, DamageSource var9, float var7){
        if(!NightmareMode.noHit){
            return instance.attackEntityFrom(var9,var7);
        }
        return false;
    }
}
