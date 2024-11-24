package com.itlesports.nightmaremode.AITasks;

import net.minecraft.src.*;

public class MobFleeBehavior extends EntityAIBase {


    private EntityCreature theEntity;
    private float speed;
    private double targetPosX;
    private double targetPosY;
    private double targetPosZ;

    public MobFleeBehavior(EntityCreature entity, float fSpeed) {
        this.theEntity = entity;
        this.speed = fSpeed;
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        Vec3 targetVec = null;
        if (this.theEntity.getAttackTarget() instanceof EntityPlayer && this.theEntity.getDistanceSqToEntity(this.theEntity.getAttackTarget()) < 25) {
            targetVec = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 5, 4, this.theEntity.worldObj.getWorldVec3Pool().getVecFromPool(this.theEntity.getAttackTarget().posX, this.theEntity.getAttackTarget().posY, this.theEntity.getAttackTarget().posZ));
        }
        if (targetVec != null) {
            this.targetPosX = targetVec.xCoord;
            this.targetPosY = targetVec.yCoord;
            this.targetPosZ = targetVec.zCoord;
            return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        this.theEntity.getNavigator().tryMoveToXYZ(this.targetPosX, this.targetPosY, this.targetPosZ, (double)this.speed);
    }

    @Override
    public boolean continueExecuting() {
//        if (!this.theEntity.getNavigator().noPath()) {
            EntityLivingBase target = this.theEntity.getAttackTarget();

            double dDistanceSqToTarget;
            if (target != null) {
                dDistanceSqToTarget = this.theEntity.getDistanceSq(this.targetPosX, this.targetPosY, this.targetPosZ);
                return dDistanceSqToTarget < 64;
            }
//        }
        return false;
    }

}
