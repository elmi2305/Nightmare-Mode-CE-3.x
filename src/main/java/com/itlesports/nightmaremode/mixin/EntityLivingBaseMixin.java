package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    @Inject(method = "isPotionActive(Lnet/minecraft/src/Potion;)Z", at = @At("HEAD"), cancellable = true)
    private void playerNightVisionBypassDuringBloodMoon(Potion par1Potion, CallbackInfoReturnable<Boolean> cir){
        EntityLivingBase thisObj = (EntityLivingBase)(Object)this;

        if(thisObj instanceof EntityPlayer player && par1Potion.id == Potion.nightVision.id && NightmareUtils.getIsBloodMoon()){
            cir.setReturnValue(true);
        }
    }

}