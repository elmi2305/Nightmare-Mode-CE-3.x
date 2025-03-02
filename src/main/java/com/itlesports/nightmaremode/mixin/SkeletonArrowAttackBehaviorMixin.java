package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.behavior.SkeletonArrowAttackBehavior;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(SkeletonArrowAttackBehavior.class)
public abstract class SkeletonArrowAttackBehaviorMixin extends EntityAIBase {
    @Unique private static final List<Integer> typesThatShouldRun = new ArrayList<>(Arrays.asList(0,2,3));

    @Shadow private EntityLivingBase entityAttackTarget;
    @Shadow @Final private EntityLiving entityOwner;
    @Shadow private float entityMoveSpeed;
    @Shadow private int attackCooldownCounter;

    @Unique boolean isExecuting;
    @Unique int ticksOnGround;

    @Redirect(method = "updateTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySenses;canSee(Lnet/minecraft/src/Entity;)Z"))
    private boolean superCriticalSkeletonEnhancedSight(EntitySenses instance, Entity entity){
        if(((EntitySkeleton)this.entityOwner).getSkeletonType() == NightmareMode.SKELETON_SUPERCRITICAL){
            return true;
        }
        return instance.canSee(entity);
    }

    @Inject(method = "continueExecuting", at = @At("HEAD"))
    private void manageRunningAway(CallbackInfoReturnable<Boolean> cir){
        if(NightmareUtils.getIsMobEclipsed(this.entityOwner) && this.entityAttackTarget instanceof EntityPlayer player && this.entityOwner.getEntitySenses().canSee(player) && typesThatShouldRun.contains(((EntitySkeleton)this.entityOwner).getSkeletonType())){
            double distToPlayer = this.entityOwner.getDistanceSqToEntity(player);
            int range = this.isExecuting ? 144 : 36;

            if(distToPlayer < range){
                this.isExecuting = true;
            }
            if(distToPlayer > 144){
                this.isExecuting = false;
            }

            if(this.isExecuting){
                if(this.entityOwner.onGround){
                    this.ticksOnGround++;
                    if (this.ticksOnGround % 2 == 1) {
                        this.entityOwner.motionX *= 1.28d;
                        this.entityOwner.jump();
                        this.entityOwner.motionZ *= 1.28d;
                    }
                }
                double x = (this.entityOwner.posX - player.posX)/2 + this.entityOwner.posX;
                double z = (this.entityOwner.posZ - player.posZ)/2 + this.entityOwner.posZ;
                for (int y = -3; y <= 3; y++) {
                    this.entityOwner.getMoveHelper().setMoveTo(x,this.entityOwner.posY + y, z,this.entityMoveSpeed + 0.45);
                }
                if (this.attackCooldownCounter <= 4) {
                    this.entityOwner.faceEntity(player,100f,100f);
                }
            }
        }
    }
}
