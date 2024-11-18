package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulties;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityWitherSkull.class)
public class EntityWitherSkullMixin {
    @ModifyConstant(method = "onImpact", constant = @Constant(intValue = 1))
    private int increaseEffectAmplifier(int constant){
        EntityWitherSkull thisObj = (EntityWitherSkull)(Object)this;
        if(thisObj.rand.nextFloat() < 0.15 && thisObj.worldObj != null && thisObj.worldObj.getDifficulty() == Difficulties.HOSTILE){
            return 2;
        }
        return 1;
    }
    @Inject(method = "onImpact",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/EntityLivingBase;addPotionEffect(Lnet/minecraft/src/PotionEffect;)V")
    )
    private void applyAdditionalEffectsOnImpact(MovingObjectPosition pos, CallbackInfo ci){
        if (pos.entityHit.rand.nextFloat()<0.02) {
            ((EntityLivingBase)pos.entityHit).addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
        }
    }
    @ModifyConstant(method = "onImpact", constant = @Constant(floatValue = 8.0f))
    private float increaseDamage(float constant){
        return 12.0f;
    }

}
