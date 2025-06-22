package com.itlesports.nightmaremode.AITasks;

import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityAIBase;
import net.minecraft.src.MathHelper;

/**
 * Makes a zombie attempt to climb straight up whenever its attack target is
 * at least 3 blocks higher than itself AND it is collided horizontally.
 */
public class EntityAIZombieClimb extends EntityAIBase {
    private final EntityLiving zombie;
    private EntityLivingBase target;
    private final double climbSpeed;
    private final float originalStepHeight;
    private final float climbStepHeight;

    public EntityAIZombieClimb(EntityLiving zombieIn) {
        this.zombie = zombieIn;
        // move speed while climbing (you can adjust as needed)
        this.climbSpeed = 0.5D;
        // store zombie's normal step height so we can restore later
        this.originalStepHeight = zombieIn.stepHeight;
        // when climbing, allow stepping up 1.0F blocks (so it can step up full‐block “ledges”)
        this.climbStepHeight = 1.0F;
        // AI tasks that involve movement/jumping usually need mutex bits 3 (move + look)
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        // Must have an attack target
        this.target = this.zombie.getAttackTarget();
        if (this.target == null) {
            return false;
        }
        // Calculate vertical difference
        double deltaY = this.target.posY - this.zombie.posY;
        // Only trigger if the target is at least 3 blocks above
        if (deltaY < 3.0D) {
            return false;
        }
        // Only trigger if zombie is “bumping” into a block (i.e., stuck horizontally)
        return this.zombie.isCollidedHorizontally;
    }

    @Override
    public boolean continueExecuting() {
        if (this.target == null) {
            return false;
        }
        // Recompute vertical difference each tick
        double deltaY = this.target.posY - this.zombie.posY;
        // Continue only while target still ≥3 blocks up AND still collided horizontally
        return deltaY >= 3.0D && this.zombie.isCollidedHorizontally;
    }

    @Override
    public void startExecuting() {
        // Raise the zombie's stepHeight so it can step up single blocks
        this.zombie.stepHeight = this.climbStepHeight;
    }

    @Override
    public void resetTask() {
        // Restore original stepHeight
        this.zombie.stepHeight = this.originalStepHeight;
        this.target = null;
    }

    @Override
    public void updateTask() {
        if (this.target == null) {
            return;
        }

        // Face directly toward the target
        double dx = this.target.posX - this.zombie.posX;
        double dz = this.target.posZ - this.zombie.posZ;
        float yaw = (float)(Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
        this.zombie.rotationYaw = updateRotation(this.zombie.rotationYaw, yaw, 30.0F);

        // If on the ground and collided horizontally, make it jump
        if (this.zombie.onGround && this.zombie.isCollidedHorizontally) {
            this.zombie.motionY = 0.42F; // basic jump impulse (same as Player jump)
        }

        // Move forward toward the target at climbSpeed
        this.zombie.moveEntityWithHeading(0.0F, (float) this.climbSpeed);
    }

    /**
     * Smoothly rotates currentYaw toward targetYaw, but by at most maxIncrement degrees.
     */
    private float updateRotation(float currentYaw, float targetYaw, float maxIncrement) {
        float angleDiff = MathHelper.wrapAngleTo180_float(targetYaw - currentYaw);

        if (angleDiff > maxIncrement) {
            angleDiff = maxIncrement;
        }
        if (angleDiff < -maxIncrement) {
            angleDiff = -maxIncrement;
        }

        return currentYaw + angleDiff;
    }
}
