package com.itlesports.nightmaremode.AITasks;

import com.itlesports.nightmaremode.entity.EntityCreeperVariant;
import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityLivingBase;

public class EntityAICreeperVariantSwell extends EntityAIBase {
    // identical to EntityAICreeperSwell, but takes EntityCreeperVariant as param instead of EntityCreeper
    public EntityCreeperVariant swellingCreeper;
    public EntityLivingBase creeperAttackTarget;

    public EntityAICreeperVariantSwell(EntityCreeperVariant par1EntityCreeper) {
        this.swellingCreeper = par1EntityCreeper;
        this.setMutexBits(1);
    }

    @Override
    public void startExecuting() {
        this.swellingCreeper.getNavigator().clearPathEntity();
        this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();
    }

    @Override
    public void resetTask() {
        this.creeperAttackTarget = null;
    }

    @Override
    public boolean shouldExecute() {
        if (this.swellingCreeper.getCreeperState() <= 0 && this.swellingCreeper.getNeuteredState() > 0) {
            return false;
        }
        if (this.swellingCreeper.getIsDeterminedToExplode()) {
            return true;
        }
        EntityLivingBase var1 = this.swellingCreeper.getAttackTarget();
        return this.swellingCreeper.getCreeperState() > 0 || var1 != null && this.swellingCreeper.getDistanceSqToEntity(var1) < 9.0;
    }

    @Override
    public void updateTask() {
        if (this.swellingCreeper.getNeuteredState() > 0) {
            this.swellingCreeper.setCreeperState(-1);
        } else if (!(this.swellingCreeper.getIsDeterminedToExplode() || this.creeperAttackTarget != null && !(this.swellingCreeper.getDistanceSqToEntity(this.creeperAttackTarget) > 36.0) && this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget))) {
            this.swellingCreeper.setCreeperState(-1);
        } else {
            this.swellingCreeper.setCreeperState(1);
        }
    }
}