package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.EntityAINearestAttackableTarget;
import net.minecraft.src.EntityAITarget;
import net.minecraft.src.EntityCreature;
import net.minecraft.src.EntitySkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityAINearestAttackableTarget.class)
public abstract class EntityAINearestAttackableTargetMixin extends EntityAITarget {
    public EntityAINearestAttackableTargetMixin(EntityCreature par1EntityCreature, boolean par2) {
        super(par1EntityCreature, par2);
    }
    @ModifyArg(method = "shouldExecute", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/AxisAlignedBB;expand(DDD)Lnet/minecraft/src/AxisAlignedBB;"),index = 1)
    private double increaseVerticalDetectionRange(double par1){
        if(this.taskOwner instanceof EntitySkeleton){
            return 12.0;
        }
        return 6.0;
    }
}
