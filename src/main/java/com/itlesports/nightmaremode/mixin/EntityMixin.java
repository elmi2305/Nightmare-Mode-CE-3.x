package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityEnderCrystal;
import net.minecraft.src.EntityEnderman;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

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
}
