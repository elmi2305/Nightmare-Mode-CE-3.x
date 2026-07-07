package com.itlesports.nightmaremode.AITasks;

import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityCreature;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.MathHelper;
import net.minecraft.src.PathEntity;
import net.minecraft.src.World;

public class EntityAIAttackOnCollidePigman extends EntityAIBase {
    public World worldObj;
    public EntityCreature attacker;
    public int attackTick;
    public boolean longMemory;
    public double speed;
    public PathEntity entityPathEntity;
    public Class classTarget;
    private int repathInterval;

    // hard cap so A* never runs on targets we won't chase anyway
    private static final double MAX_CHASE_DISTANCE = 50.0D;
    private static final double MAX_CHASE_DISTANCE_SQ = MAX_CHASE_DISTANCE * MAX_CHASE_DISTANCE;
    // skip repath on far targets that haven't moved much - long paths are the expensive ones
    private static final double REPATH_MIN_TARGET_MOVE_SQ = 9.0D;
    private static final double NEAR_RANGE_SQ = 400.0D;
    private static final double MID_RANGE_SQ = 625.0D;
    private static final double FAR_RANGE_SQ = 1600.0D;

    private double attackRangeSq;
    private double lastTargetX;
    private double lastTargetY;
    private double lastTargetZ;

    public EntityAIAttackOnCollidePigman(EntityCreature creature, Class cl, double speed, boolean longMemory) {
        this(creature, speed, longMemory);
        this.classTarget = cl;
    }

    public EntityAIAttackOnCollidePigman(EntityCreature creature, double speed, boolean longMem) {
        this.attacker = creature;
        this.worldObj = creature.worldObj;
        this.speed = speed;
        this.longMemory = longMem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return false;
        }
        if (this.classTarget != null && !this.classTarget.isAssignableFrom(target.getClass())) {
            return false;
        }
        // cheap distance check before the expensive pathfinder call
        if (this.attacker.getDistanceSqToEntity(target) > MAX_CHASE_DISTANCE_SQ) {
            return false;
        }
        this.entityPathEntity = this.attacker.getNavigator().getPathToEntityLiving(target);
        return this.entityPathEntity != null;
    }

    @Override
    public boolean continueExecuting() {
        EntityLivingBase var1 = this.attacker.getAttackTarget();
        if (var1 == null || !var1.isEntityAlive()) {
            return false;
        }
        if (this.attacker.getDistanceSqToEntity(var1) > MAX_CHASE_DISTANCE_SQ) {
            return false; // target broke max range, drop chase instead of re-pathing forever
        }
        return !this.longMemory
                ? !this.attacker.getNavigator().noPath()
                : this.attacker.func_110176_b(MathHelper.floor_double(var1.posX), MathHelper.floor_double(var1.posY), MathHelper.floor_double(var1.posZ));
    }

    @Override
    public void startExecuting() {
        this.attacker.getNavigator().setPath(this.entityPathEntity, this.speed);
        this.repathInterval = 0;

        EntityLivingBase var1 = this.attacker.getAttackTarget();
        double dCombinedWidth = this.attacker.width + var1.width;
        this.attackRangeSq = dCombinedWidth * dCombinedWidth; // cached, width is static per entity
        this.lastTargetX = var1.posX;
        this.lastTargetY = var1.posY;
        this.lastTargetZ = var1.posZ;
    }

    @Override
    public void resetTask() {
        this.attacker.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        EntityLivingBase var1 = this.attacker.getAttackTarget();
        this.attacker.getLookHelper().setLookPositionWithEntity(var1, 30.0f, 30.0f);

        if ((this.longMemory || this.attacker.getEntitySenses().canSee(var1)) && --this.repathInterval <= 0) {
            double distSq = this.attacker.getDistanceSqToEntity(var1);
            double targetMoveSq = squareDist(var1.posX, var1.posY, var1.posZ, this.lastTargetX, this.lastTargetY, this.lastTargetZ);

            // far + stationary target: skip the costly repath, just recheck soon
            if (distSq <= MAX_CHASE_DISTANCE_SQ && (distSq < NEAR_RANGE_SQ || targetMoveSq >= REPATH_MIN_TARGET_MOVE_SQ)) {
                this.repathInterval = repathInterval(distSq);
                this.attacker.getNavigator().tryMoveToEntityLiving(var1, this.speed);
                this.lastTargetX = var1.posX;
                this.lastTargetY = var1.posY;
                this.lastTargetZ = var1.posZ;
            } else {
                this.repathInterval = 5;
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        if (var1 == this.attacker.riddenByEntity) {
            return;
        }
        if (this.attacker.getDistanceSq(var1.posX, var1.boundingBox.minY, var1.posZ) <= this.attackRangeSq && this.attackTick <= 0) {
            this.attackTick = 20;
            if (this.attacker.getHeldItem() != null) {
                this.attacker.swingItem();
            }
            this.attacker.attackEntityAsMob(var1);
        }
    }

    // longer distance = more expensive A* = throttle re-paths harder
    private int repathInterval(double distSq) {
        if (distSq > FAR_RANGE_SQ) {
            return 30 + this.attacker.getRNG().nextInt(15);
        }
        if (distSq > MID_RANGE_SQ) {
            return 15 + this.attacker.getRNG().nextInt(10);
        }
        return 4 + this.attacker.getRNG().nextInt(7);
    }

    private static double squareDist(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2, dy = y1 - y2, dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }
}
