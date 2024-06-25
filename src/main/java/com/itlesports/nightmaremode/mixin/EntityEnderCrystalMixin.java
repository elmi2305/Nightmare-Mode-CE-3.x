package com.itlesports.nightmaremode.mixin;

import btw.entity.LightningBoltEntity;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityEnderCrystal.class)
public abstract class EntityEnderCrystalMixin extends Entity{
    public EntityEnderCrystalMixin(World par1World) {
        super(par1World);
    }
    @Inject(
            method = "attackEntityFrom",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityEnderCrystal;setDead()V")
    )
    private void avengeDestroyer(DamageSource par1DamageSource, float par2, CallbackInfoReturnable<Boolean> cir) {
        Entity destroyer = par1DamageSource.getSourceOfDamage();
        if (destroyer instanceof EntityArrow) {
            destroyer = ((EntityArrow) destroyer).shootingEntity;
        } else if (destroyer instanceof EntityThrowable) {
            destroyer = ((EntityThrowable) destroyer).getThrower();
        }
        if (destroyer instanceof EntityPlayer) {
            Entity lightningbolt = new LightningBoltEntity(this.worldObj, destroyer.posX, destroyer.posY-0.5, destroyer.posZ);
            this.worldObj.addWeatherEffect(lightningbolt);
            System.out.println(this.worldObj);
        }
    }
}