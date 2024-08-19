package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySnowball.class)
public class EntitySnowballMixin {
    @Inject(method = "onImpact", at = @At("TAIL"))
    private void hookEntity(MovingObjectPosition par1, CallbackInfo ci) {
        EntitySnowball thisObj = (EntitySnowball) (Object) this;
        if (par1.entityHit instanceof EntityLivingBase target) {
            if (target != null && thisObj.getThrower() != null) {
                if (thisObj.getThrower() instanceof EntityPlayer player) {
                    double var1 = player.posX - target.posX;
                    double var2 = player.posZ - target.posZ;
                    double var3 = player.posY - target.posY;
                    Vec3 vector = Vec3.createVectorHelper(var1, var3, var2);
                    vector.normalize();
                    target.motionX = vector.xCoord * 0.085;
                    target.motionZ = vector.zCoord * 0.085;
                    double yMotion = vector.yCoord * 0.06 + 0.3;
                    if (yMotion > 0.75) {
                        yMotion = 0.75;
                    }
                    if (target.motionY <= -0.07 && target.motionY >= -0.08) {
                        target.motionY = yMotion;
                    }
                }
            }
        }
    }
}
