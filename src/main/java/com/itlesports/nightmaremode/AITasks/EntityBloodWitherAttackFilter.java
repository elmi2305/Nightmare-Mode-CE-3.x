package com.itlesports.nightmaremode.AITasks;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EnumCreatureAttribute;
import net.minecraft.src.IEntitySelector;

public class EntityBloodWitherAttackFilter implements IEntitySelector {
    public EntityBloodWitherAttackFilter() {}
    // simply a copy of the EntityWitherAttackFilter, but made accessible. Hey, it's better than using AW

    @Override
    public boolean isEntityApplicable(Entity entity) {
        return entity instanceof EntityLivingBase && ((EntityLivingBase)entity).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
    }
}

