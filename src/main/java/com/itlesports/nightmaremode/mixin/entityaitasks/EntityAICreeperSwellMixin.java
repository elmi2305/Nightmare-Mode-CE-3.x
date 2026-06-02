package com.itlesports.nightmaremode.mixin.entityaitasks;

import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityAICreeperSwell.class)
public abstract class EntityAICreeperSwellMixin extends EntityAIBase{
    @Shadow public EntityLivingBase creeperAttackTarget;

    @Shadow public EntityCreeper swellingCreeper;

    @Redirect(method = "updateTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySenses;canSee(Lnet/minecraft/src/Entity;)Z"))
    private boolean canSeeThroughWalls(EntitySenses senses, Entity entity){
        if (this.creeperAttackTarget.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
            return true;
        }
        return senses.canSee(this.creeperAttackTarget);
    }
    @ModifyConstant(method = "updateTask", constant = @Constant(doubleValue = 36.0))
    private double increaseRadiusOfCreeperContinueSwelling(double constant){
        return constant + NMUtils.getWorldProgress() * 3;
    }
    @Inject(method = "shouldExecute", at = @At(value = "RETURN", ordinal = 2),cancellable = true)
    private void checkHeightDifference(CallbackInfoReturnable<Boolean> cir){
        if(this.swellingCreeper.getAttackTarget() != null){
            EntityLivingBase target = this.swellingCreeper.getAttackTarget();
            // if the original return value failed
            double dx = this.swellingCreeper.posX - target.posX;
            double dz = this.swellingCreeper.posZ - target.posZ;
            double xzSq = dx * dx + dz * dz;
            if(xzSq < 2){
                // 1.41 blocks of xzSq gap
                double dy = this.swellingCreeper.posY - target.posY;
                if (dy < 3.6 && dy > 1){
                    cir.setReturnValue(true);
                }
            }

        }
    }
}
