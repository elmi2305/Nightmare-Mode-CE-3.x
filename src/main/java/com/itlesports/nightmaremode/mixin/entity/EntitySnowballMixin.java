package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySnowball.class)
public class EntitySnowballMixin {

    @Inject(method = "onImpact", at = @At("HEAD"))
    private void hookEntity(MovingObjectPosition mop, CallbackInfo ci) {
        EntitySnowball snowball = (EntitySnowball) (Object) this;

        if (mop.entityHit instanceof EntityLivingBase hitEntity) {
            EntityLivingBase thrower = snowball.getThrower();
            if (hitEntity != null && thrower != null) {
                double dx = thrower.posX - hitEntity.posX;
                double dz = thrower.posZ - hitEntity.posZ;
                double dy = thrower.posY - hitEntity.posY;
                Vec3 vector = Vec3.createVectorHelper(dx, dy, dz);
                vector.normalize();
                if(hitEntity instanceof EntityPlayer) {
                    if(((EntityPlayer) hitEntity).capabilities.isCreativeMode || ((EntityPlayer) hitEntity).capabilities.disableDamage || hitEntity.isEntityInvulnerable()) return;
                    // cannot kb invincible players
                    hitEntity.velocityChanged = true;
                }
                hitEntity.motionX = vector.xCoord * 0.085;
                hitEntity.motionZ = vector.zCoord * 0.085;
                double yMotion = vector.yCoord * 0.06 + 0.3;
                if (yMotion > 0.75) {
                    yMotion = 0.75;
                }
                if (hitEntity.motionY <= -0.07 && hitEntity.motionY >= -0.08) {
                    // entity must be falling to be raised. this makes raising entities with snowball spam less effective
                    if(thrower == hitEntity) return; // you cannot launch yourself up
                    hitEntity.motionY = yMotion;
                }
                if(hitEntity instanceof EntityAnimal){
                    hitEntity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40,0));
                }
            }
        }
    }
}
