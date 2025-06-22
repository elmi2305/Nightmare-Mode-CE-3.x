package com.itlesports.nightmaremode.AITasks;

import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import net.minecraft.src.*;

public class ZombieEnragedBehavior extends ZombieBreakBarricadeBehavior {
    public ZombieEnragedBehavior(EntityLiving entity) {
        super(entity);
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.associatedEntity.getAttackTarget();
        if (target != null && this.associatedEntity.getNavigator().getCanBreakDoors()) {
            // Attempt to find a barricade block between zombie and its target.
            if (this.determineBlockToBreak(target)) {
                double dx = this.associatedEntity.posX - (this.doorPosX + 0.5D);
                double dy = this.associatedEntity.posY - this.doorPosY;
                double dz = this.associatedEntity.posZ - (this.doorPosZ + 0.5D);
                double distSq = dx * dx + dy * dy + dz * dz;

                // If close enough to break immediately, begin breaking.
                if (distSq <= 2.25D) {
                    return true;
                }

                // Otherwise, move toward that block (inch forward).
                this.associatedEntity.getNavigator().tryMoveToXYZ(
                        this.doorPosX + 0.5D,
                        this.doorPosY,
                        this.doorPosZ + 0.5D,
                        1.0D
                );
                return false;
            }

            // No barricade detected, so path toward the player normally.
            this.associatedEntity.getNavigator().tryMoveToEntityLiving(target, 1.0D);
        }
        return false;
    }

    /**
     * Copies the parent’s logic for ray-tracing a block between the zombie and its target,
     * but only returns true if we find a valid block to break.
     */
    private boolean determineBlockToBreak(EntityLivingBase targetEntity) {
        World world = this.associatedEntity.worldObj;
        Vec3 start = Vec3.createVectorHelper(
                this.associatedEntity.posX,
                this.associatedEntity.posY + this.associatedEntity.getEyeHeight(),
                this.associatedEntity.posZ
        );
        Vec3 end = Vec3.createVectorHelper(
                targetEntity.posX,
                targetEntity.posY + targetEntity.getEyeHeight(),
                targetEntity.posZ
        );

        MovingObjectPosition mop = world.rayTraceBlocks_do_do(start, end, false, true);
        if (mop != null && mop.typeOfHit == EnumMovingObjectType.TILE) {
            this.doorPosX = mop.blockX;
            this.doorPosY = mop.blockY;
            this.doorPosZ = mop.blockZ;

            double blockCenterX = this.doorPosX + 0.5D;
            double blockCenterY = this.doorPosY;
            double blockCenterZ = this.doorPosZ + 0.5D;
            double distSq = this.associatedEntity.getDistanceSq(blockCenterX, blockCenterY, blockCenterZ);

            // Only consider it a valid barricade if within ~2.5 blocks horizontally
            if (distSq <= 6.0D) {
                this.targetBlock = this.shouldBreakBarricadeAtPos(
                        world, this.doorPosX, this.doorPosY, this.doorPosZ
                );
                if (this.targetBlock != null) {
                    return true;
                }
            }
        }

        // Fallback: check the block at the zombie’s feet (in case the raytrace missed)
        this.doorPosX = MathHelper.floor_double(this.associatedEntity.posX);
        this.doorPosY = MathHelper.floor_double(this.associatedEntity.posY + 1.0D);
        this.doorPosZ = MathHelper.floor_double(this.associatedEntity.posZ);
        this.targetBlock = this.shouldBreakBarricadeAtPos(
                world, this.doorPosX, this.doorPosY, this.doorPosZ
        );
        if (this.targetBlock == null) {
            this.doorPosY = MathHelper.floor_double(this.associatedEntity.posY);
            this.targetBlock = this.shouldBreakBarricadeAtPos(
                    world, this.doorPosX, this.doorPosY, this.doorPosZ
            );
        }
        return (this.targetBlock != null);
    }

    @Override
    public boolean continueExecuting() {
        // Stop if we've broken long enough or the block is gone/changed.
        if (this.breakingTime > 60
                || this.associatedEntity.worldObj.getBlockId(doorPosX, doorPosY, doorPosZ) != this.targetBlock.blockID) {
            return false;
        }

        // Must remain within ~3 blocks of the break target to continue.
        double dx = this.associatedEntity.posX - (this.doorPosX + 0.5D);
        double dy = this.associatedEntity.posY - this.doorPosY;
        double dz = this.associatedEntity.posZ - (this.doorPosZ + 0.5D);
        double distSq = dx * dx + dy * dy + dz * dz;

        return distSq < 9.0D;
    }

    @Override
    public void updateTask() {
        // Face the block being broken.
        double dx = (this.doorPosX + 0.5D) - this.associatedEntity.posX;
        double dz = (this.doorPosZ + 0.5D) - this.associatedEntity.posZ;
        this.associatedEntity.rotationYaw = (float)(Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;

        // If the block is more than 3 blocks above, climb up.
        if (this.doorPosY - this.associatedEntity.posY > 3.0D) {
            this.associatedEntity.motionY = 0.1D;
            this.associatedEntity.isAirBorne = true;
        }

        // Perform the fast breaking logic.
        if (this.associatedEntity.worldObj.getBlockId(doorPosX, doorPosY, doorPosZ) == this.targetBlock.blockID) {
            ++this.breakingTime;

            // Visual progress: send block crack stage (0–9) based on breakingTime.
            int stage = this.breakingTime * 10 / 60;
//            this.associatedEntity.worldObj.sendBlockBreakProgress(
//                    this.associatedEntity.getEntityId(),
//                    this.doorPosX, this.doorPosY, this.doorPosZ,
//                    stage
//            );

            // Once breakingTime reaches 60, destroy the block immediately.
            if (this.breakingTime >= 60) {
                this.associatedEntity.worldObj.setBlockToAir(this.doorPosX, this.doorPosY, this.doorPosZ);
                this.associatedEntity.worldObj.playAuxSFX(
                        1012,
                        this.doorPosX, this.doorPosY, this.doorPosZ,
                        0
                );
            }
        }
    }
}
