package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulties;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity implements EntityAccessor {
    @Shadow public abstract boolean isEntityAlive();

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }
    @ModifyConstant(method = "getEyeHeight", constant = @Constant(floatValue = 0.85f))
    private float modifyWitherSkeletonSight(float constant){
        EntityLivingBase thisObj = (EntityLivingBase)(Object)this;
        if(thisObj.worldObj.getDifficulty() != Difficulties.HOSTILE){
            return constant;
        }
        if(thisObj instanceof EntitySkeleton skeleton && skeleton.getSkeletonType()==1){
            return 0.6f;
        } else{return 0.85f;}
    }
}