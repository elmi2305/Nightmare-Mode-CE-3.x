package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntitySnowman.class)
public class EntitySnowmanMixin extends EntityGolem {
    public EntitySnowmanMixin(World world) {
        super(world);
        this.targetTasks.removeTask( new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, true, false, IMob.mobSelector));

        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityMob.class, 1, true, false, IMob.mobSelector));


    }

    @ModifyConstant(method = "onLivingUpdate", constant = @Constant(floatValue = 1.0f))
    private float increaseTemperatureCondition(float constant){
        return constant * 2;
    }
}
