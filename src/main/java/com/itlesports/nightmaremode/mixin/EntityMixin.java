package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.KickingAnimal;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityEnderCrystal;
import net.minecraft.src.EntityEnderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract void flingAwayFromEntity(Entity repulsingEntity, double dForceMultiplier);

    @Inject(method = "updateRiderPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;setPosition(DDD)V", shift = At.Shift.AFTER))
    private void riderHeightOffset(CallbackInfo ci) {
        Entity thisObj = (Entity) (Object) this;
        if (thisObj.riddenByEntity instanceof EntityEnderCrystal) {
            thisObj.riddenByEntity.setPosition(thisObj.posX, thisObj.posY - 0.5125D + thisObj.riddenByEntity.getYOffset(), thisObj.posZ);
        }
    }
    @Redirect(method = "onStruckByLightning(Lbtw/entity/LightningBoltEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;dealFireDamage(I)V"))
    private void endermenImmune(Entity instance, int par1) {
        Entity thisObj = (Entity) (Object) this;
        if (!thisObj.isImmuneToFire() && !(thisObj instanceof EntityEnderman)) {
            thisObj.attackEntityFrom(DamageSource.inFire, par1);
        }
    }

    @Redirect(method = "moveEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;isSneaking()Z"))
    private boolean manageAprilFoolsSneaking(Entity instance){
        if(NightmareMode.isAprilFools){
            return false;
        }
        return instance.isSneaking();
    }

    @Inject(method = "onKickedByAnimal", at = @At("HEAD"),cancellable = true)
    private void increaseKickKnockbackOnEclipse(KickingAnimal kickingAnimal, CallbackInfo ci){
        if(NightmareUtils.getIsMobEclipsed(kickingAnimal)){
            this.flingAwayFromEntity(kickingAnimal, 3);
            ci.cancel();
        }
    }
}
