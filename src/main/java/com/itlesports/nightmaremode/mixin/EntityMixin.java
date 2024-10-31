package com.itlesports.nightmaremode.mixin;

import btw.entity.InfiniteArrowEntity;
import btw.entity.mob.BTWSquidEntity;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    // TODO: make this code actually good

    @Redirect(method = "updateRiderPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;getMountedYOffset()D"))
    private double getEndCrystalOffset(Entity instance){
        Entity thisObject = (Entity)(Object)this;
        if(thisObject.riddenByEntity instanceof EntityMagmaCube && !thisObject.isInWater()){
            thisObject.setFire(1000);}
        if(thisObject.riddenByEntity instanceof EntityEnderCrystal){
            return -0.5125; // so the ender crystal base is barely underground
        } else if (thisObject.riddenByEntity instanceof BTWSquidEntity) {
            return thisObject.getEyeHeight();
        } else {return (double)thisObject.height * 0.75;}
    }
    @Redirect(method = "onStruckByLightning(Lbtw/entity/LightningBoltEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;dealFireDamage(I)V"))
    private void endermenImmune(Entity instance, int par1){
        Entity thisObj = (Entity)(Object)this;
        if (!thisObj.isImmuneToFire() && !(thisObj instanceof EntityEnderman)) {
            thisObj.attackEntityFrom(DamageSource.inFire, par1);
        }
    }

//    @Inject(method = "attackEntityFrom", at = @At("RETURN"),cancellable = true)
//    private void arrowsIgnoreInvincibility(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
//        if(par1DamageSource.getEntity() instanceof InfiniteArrowEntity){
//            cir.setReturnValue(true);
//        }
//    }
}
