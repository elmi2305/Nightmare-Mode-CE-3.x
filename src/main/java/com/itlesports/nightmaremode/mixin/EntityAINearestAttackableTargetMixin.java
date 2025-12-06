package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.entity.EntityBloodZombie;
import com.itlesports.nightmaremode.entity.EntityShadowZombie;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityAINearestAttackableTarget.class)
public abstract class EntityAINearestAttackableTargetMixin extends EntityAITarget {
    @Shadow private EntityLivingBase targetEntity;

    public EntityAINearestAttackableTargetMixin(EntityCreature par1EntityCreature, boolean par2) {
        super(par1EntityCreature, par2);
    }
    @ModifyArg(method = "shouldExecute", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/AxisAlignedBB;expand(DDD)Lnet/minecraft/src/AxisAlignedBB;"),index = 1)
    private double increaseVerticalDetectionRange(double par1){
        if(this.taskOwner instanceof EntityShadowZombie){
            return 10.0;
        }
        if(this.taskOwner instanceof EntitySkeleton){
            return 12.0;
        }
        return 6.0;
    }

    @Inject(method = "shouldExecute", at = @At("HEAD"),cancellable = true)
    private void forceTargetPlayer(CallbackInfoReturnable<Boolean> cir){
        if (NightmareMode.hordeMode || this.taskOwner instanceof EntityBloodZombie) {
            if (this.targetEntity != null) return;
            EntityPlayer nearestPlayer = this.taskOwner.worldObj.getClosestPlayerToEntity(this.taskOwner, -1);
            if(nearestPlayer != null){
                this.targetEntity = nearestPlayer;
                cir.setReturnValue(true);
            }
        }
    }


    @Inject(method = "getTargetDistance", at = @At("HEAD"),cancellable = true)
    private void bypassExpensiveAABB(CallbackInfoReturnable<Double> cir){
        if (NightmareMode.hordeMode || this.taskOwner instanceof EntityBloodZombie) {
            cir.setReturnValue(1d);
        }
    }
}
