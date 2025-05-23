package com.itlesports.nightmaremode.AITasks;

import net.minecraft.src.*;

public class SkeletonChaseSmart extends EntityAIBase {
    private final EntityLiving entityOwner;
    private final IRangedAttackMob rangedAttacker;
    private EntityLivingBase target;

    private final float moveSpeed;
    private final int attackInterval;
    private final double attackRange;
    private final double attackRangeSq;

    private int repathDelay = 0;
    private int attackCooldown = 0;
    private int visibilityCounter = 0;

    private static final int MAX_REPATH_DELAY = 40;
    private static final double MAX_CHASE_DISTANCE_SQ = 256 * 256; // 256 block range

    public SkeletonChaseSmart(IRangedAttackMob rangedAttacker, float moveSpeed, int attackInterval, float attackRange) {
        this.entityOwner = (EntityLiving) rangedAttacker;
        this.rangedAttacker = rangedAttacker;
        this.moveSpeed = moveSpeed;
        this.attackInterval = attackInterval;
        this.attackRange = attackRange;
        this.attackRangeSq = attackRange * attackRange;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase foundTarget = this.entityOwner.getAttackTarget();
        if (foundTarget != null && foundTarget.isEntityAlive()) {
            this.target = foundTarget;
            return true;
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        return this.target != null && this.target.isEntityAlive();
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.visibilityCounter = 0;
        this.attackCooldown = 0;
        this.repathDelay = 0;
    }

    @Override
    public void updateTask() {
        if (this.target == null) return;

        this.entityOwner.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);

        double dx = this.target.posX - this.entityOwner.posX;
        double dz = this.target.posZ - this.entityOwner.posZ;
        double distSqXZ = dx * dx + dz * dz;
        boolean canSee = this.entityOwner.getEntitySenses().canSee(this.target);

        if (canSee) {
            ++this.visibilityCounter;
        } else {
            this.visibilityCounter = 0;
        }

        // Naive long-range pursuit
        if (distSqXZ > this.attackRangeSq) {
            if (distSqXZ > MAX_CHASE_DISTANCE_SQ) {
                this.entityOwner.getNavigator().clearPathEntity();
                return;
            }
            // Only path occasionally
            if (--this.repathDelay <= 0) {
                this.entityOwner.getNavigator().tryMoveToEntityLiving(this.target, this.moveSpeed);
                this.repathDelay = MAX_REPATH_DELAY + this.entityOwner.getRNG().nextInt(20);
            }
        } else if (this.visibilityCounter >= 10) {
            this.entityOwner.getNavigator().clearPathEntity();
        }

        if (--this.attackCooldown <= 0) {
            if (distSqXZ <= this.attackRangeSq && canSee) {
                float rangeFactor = MathHelper.sqrt_double(this.entityOwner.getDistanceSqToEntity(this.target)) / (float)this.attackRange;
                this.rangedAttacker.attackEntityWithRangedAttack(this.target, MathHelper.clamp_float(rangeFactor, 0.1F, 1.0F));
                this.attackCooldown = this.attackInterval;
            }
        }
    }
}
