package com.itlesports.nightmaremode.AITasks;

import com.itlesports.nightmaremode.entity.zombies.EntityZombieVariant;
import net.minecraft.src.*;

public class EntityAIZombieSecondaryAttackBehavior
        extends EntityAIAttackOnCollide {
    private EntityZombieVariant zombie;

    public EntityAIZombieSecondaryAttackBehavior(EntityZombieVariant zombie) {
        super(zombie, EntityCreature.class, 1.0, true);
        this.zombie = zombie;
    }

    @Override
    public boolean continueExecuting() {
        EntityLivingBase var1 = this.attacker.getAttackTarget();
        if (var1 == null || isValidTargetOverrideHelper()) {
            return false;
        }
        return super.continueExecuting();
    }

    public boolean isValidTargetOverrideHelper()
    {
        EntityLivingBase var1 = this.attacker.getAttackTarget();
        return var1 instanceof EntityCow || var1 instanceof EntityPig || var1 instanceof EntityHorse || var1 instanceof EntitySheep || var1 instanceof EntityVillager;
    }

}
