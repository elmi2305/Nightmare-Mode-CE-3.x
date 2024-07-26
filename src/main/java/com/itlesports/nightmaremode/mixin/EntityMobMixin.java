package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMob.class)
public class EntityMobMixin{

    @Inject(method = "entityMobAttackEntityFrom", at = @At("HEAD"),cancellable = true)
    private void witchImmunity(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir){
        EntityMob thisObj = (EntityMob)(Object)this;
        if((par1DamageSource == DamageSource.magic || par1DamageSource == DamageSource.wither || par1DamageSource == DamageSource.fallingBlock) && thisObj instanceof EntityWitch){
            cir.setReturnValue(false);
        }
    }
}
