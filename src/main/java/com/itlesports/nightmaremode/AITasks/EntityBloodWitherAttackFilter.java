package com.itlesports.nightmaremode.AITasks;

import net.minecraft.src.*;

public class EntityBloodWitherAttackFilter implements IEntitySelector {
    public EntityBloodWitherAttackFilter() {}
    // simply a copy of the EntityWitherAttackFilter, but made accessible. Hey, it's better than using AW

    @Override
    public boolean isEntityApplicable(Entity entity) {
        return entity instanceof EntityPlayer;
    }
}

