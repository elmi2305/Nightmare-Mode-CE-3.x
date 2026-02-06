package com.itlesports.nightmaremode.AITasks;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

public class EntityAIChasePlayer extends EntityAIBase {
    private static final double CHASE_DISTANCE_SQ = 256;
    private final EntityAnimal theAnimal;
    private EntityPlayer targetEntity;
    private final float animalApproachSpeed;
    private int pathTimer;

    public EntityAIChasePlayer(EntityAnimal theAnimal, float animalApproachSpeed) {
        this.theAnimal = theAnimal;
        this.animalApproachSpeed = animalApproachSpeed;
        this.pathTimer = 6;
        this.setMutexBits(9);
    }

    @Override
    public boolean shouldExecute() {
        if (NMUtils.getIsMobEclipsed(this.theAnimal)) {
            EntityPlayer possibleTarget = this.theAnimal.worldObj.getClosestVulnerablePlayerToEntity(this.theAnimal, 16);
            if (possibleTarget != null && possibleTarget.isEntityAlive() && this.theAnimal.getDistanceSqToEntity(possibleTarget) < CHASE_DISTANCE_SQ) {
                this.targetEntity = possibleTarget;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        if (this.targetEntity != null) {
            this.theAnimal.setAttackTarget(this.targetEntity);
            this.theAnimal.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0F, 30.0F);

            if (--this.pathTimer <= 0) {
                this.pathTimer = 4 + this.theAnimal.getRNG().nextInt(4);
                this.theAnimal.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.animalApproachSpeed);
            }
        }
        return this.targetEntity != null && this.targetEntity.isEntityAlive() &&
                this.theAnimal.getDistanceSqToEntity(this.targetEntity) < CHASE_DISTANCE_SQ;
    }

    @Override
    public void startExecuting() {
        this.theAnimal.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.animalApproachSpeed);
    }

    @Override
    public void resetTask() {
        this.targetEntity = null;
        this.theAnimal.getNavigator().clearPathEntity();
        this.theAnimal.setAttackTarget(null);
    }
}