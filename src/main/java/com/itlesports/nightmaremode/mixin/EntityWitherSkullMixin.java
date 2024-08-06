package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityWitherSkull.class)
public class EntityWitherSkullMixin {
    @ModifyConstant(method = "onImpact", constant = @Constant(intValue = 1))
    private int increaseEffectAmplifier(int constant){
        EntityWitherSkull thisObj = (EntityWitherSkull)(Object)this;
        if(thisObj.rand.nextFloat()<0.85){
            return 1;
        }
        return 2;
    }
    @Inject(method = "onImpact",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityLivingBase;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void applyAdditionalEffectsOnImpact(MovingObjectPosition pos, CallbackInfo ci, int iDamage){
        if (pos.entityHit.rand.nextFloat()<0.02) {
            ((EntityLivingBase)pos.entityHit).addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
        }
    }
    @ModifyConstant(method = "onImpact", constant = @Constant(floatValue = 8.0f))
    private float increaseDamage(float constant){
        return 12.0f;
    }

}
