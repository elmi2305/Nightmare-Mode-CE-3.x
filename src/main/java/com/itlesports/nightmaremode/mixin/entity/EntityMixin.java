package com.itlesports.nightmaremode.mixin.entity;

import api.entity.mob.KickingAnimal;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract void flingAwayFromEntity(Entity repulsingEntity, double dForceMultiplier);


    @Inject(method = "tryToSetFireToBlocksInContact", at = @At("HEAD"), cancellable = true)
    private void manageFireSpreadFromBurningEntities(CallbackInfo ci){
        Entity self = (Entity) (Object) this;
        int prog = NMUtils.getWorldProgress();

        if(prog > 0 && self instanceof EntityZombie) return;

        if (prog > 1) return;

        ci.cancel();
    }

    @Inject(method = "getBlockExplosionResistance", at = @At("HEAD"),cancellable = true)
    private void injectCorrectlyParameterizedExplosionMethod(Explosion par1Explosion, World par2World, int par3, int par4, int par5, Block par6Block, CallbackInfoReturnable<Float> cir){
            Entity thisObj = (Entity) (Object) this;
            cir.setReturnValue(par6Block.getExplosionResistance(thisObj, par2World, par3, par4, par5));
    }


    @Inject(method = "updateRiderPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;setPosition(DDD)V", shift = At.Shift.AFTER))
    private void riderHeightOffset(CallbackInfo ci) {
        Entity thisObj = (Entity) (Object) this;
        if (thisObj.riddenByEntity instanceof EntityEnderCrystal) {
            thisObj.riddenByEntity.setPosition(thisObj.posX, thisObj.posY - 0.5125D + thisObj.riddenByEntity.getYOffset(), thisObj.posZ);
        }
    }

    @Redirect(method = "onStruckByLightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Entity;dealFireDamage(I)V"))
    private void endermenImmuneToLightning(Entity instance, int par1) {
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
        if(NMUtils.getIsMobEclipsed(kickingAnimal)){
            this.flingAwayFromEntity(kickingAnimal, 3);
            ci.cancel();
        }
    }
}
