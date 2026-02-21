package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityAICreeperSwell.class)
public abstract class EntityAICreeperSwellMixin extends EntityAIBase{
    @Shadow public EntityLivingBase creeperAttackTarget;

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
}
