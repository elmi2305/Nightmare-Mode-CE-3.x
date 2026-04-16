package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityWolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityWolf.class)
public class EntityWolfMixin {
    @Inject(method = "func_142018_a", at = @At("HEAD"), cancellable = true)
    private void avoidAttackingCreeperVariants(EntityLivingBase e1, EntityLivingBase e2, CallbackInfoReturnable<Boolean> cir){
        if(e1 instanceof EntityCreeperVariant){
            cir.setReturnValue(false);
        }
    }
}
