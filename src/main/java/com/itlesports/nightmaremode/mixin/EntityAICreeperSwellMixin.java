package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.entity.EntityMetalCreeper;
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

    @Shadow public EntityCreeper swellingCreeper;

    @Redirect(method = "updateTask", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntitySenses;canSee(Lnet/minecraft/src/Entity;)Z"))
    private boolean canSeeThroughWalls(EntitySenses senses, Entity entity){
        if (this.creeperAttackTarget.worldObj.getDifficulty() == Difficulties.HOSTILE) {
            return true;
        }
        return senses.canSee(this.creeperAttackTarget);
    }
    @ModifyConstant(method = "updateTask", constant = @Constant(doubleValue = 36.0))
    private double increaseCreeperExplosionRadiusSlightly(double constant){
        if(this.swellingCreeper instanceof EntityMetalCreeper){
            return 81;
        }
        return constant + NightmareUtils.getWorldProgress(this.swellingCreeper.worldObj) * 3;
    }
}
