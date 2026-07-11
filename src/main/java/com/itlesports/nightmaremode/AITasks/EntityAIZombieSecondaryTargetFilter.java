package com.itlesports.nightmaremode.AITasks;

import com.itlesports.nightmaremode.entity.zombies.EntityZombieVariant;
import net.minecraft.src.*;

public class EntityAIZombieSecondaryTargetFilter
        implements IEntitySelector {
    EntityZombieVariant zombie;

    public EntityAIZombieSecondaryTargetFilter(EntityZombieVariant zombie) {
        this.zombie = zombie;
    }

    @Override
    public boolean isEntityApplicable(Entity entity) {
        return isValidTargetOverrideHelper(entity);
    }

    public boolean isValidTargetOverrideHelper(Entity var1)
    {
        if(!this.zombie.attacksAnimals) return false;
        return var1 instanceof EntityCow || var1 instanceof EntityPig || var1 instanceof EntityHorse || var1 instanceof EntitySheep || var1 instanceof EntityVillager;
    }
}