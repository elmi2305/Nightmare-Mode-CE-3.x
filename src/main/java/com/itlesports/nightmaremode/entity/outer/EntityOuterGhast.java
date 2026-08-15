package com.itlesports.nightmaremode.entity.outer;

import net.minecraft.src.*;

abstract class EntityOuterGhast extends EntityGhast {
    private EntityPlayer outerTarget;
    private int retargetCooldown;

    EntityOuterGhast(World world) {
        super(world);
    }

    protected abstract double activationRange();
    protected abstract int attackWindup();
    protected abstract int attackCooldown();
    protected abstract EntityLargeFireball createOuterFireball(EntityPlayer target, double dx, double dy, double dz);

    protected boolean isProvoked() {
        return false;
    }

    protected boolean mayTarget(EntityPlayer player) {
        return player != null && (isProvoked() || this.getDistanceSqToEntity(player) <= activationRange() * activationRange());
    }

    @Override
    protected void updateEntityActionState() {
        ++this.entityAge;
        this.despawnEntity();
        this.prevAttackCounter = this.attackCounter;

        if (this.outerTarget == null || !this.outerTarget.isEntityAlive() || --this.retargetCooldown <= 0) {
            EntityPlayer nearest = this.worldObj.getClosestVulnerablePlayerToEntity(this, 48.0D);
            this.outerTarget = mayTarget(nearest) ? nearest : null;
            this.retargetCooldown = 20;
        }

        updateCourse();
        if (this.outerTarget == null) {
            this.renderYawOffset = this.rotationYaw = -((float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI));
            if (this.attackCounter > 0) --this.attackCounter;
        } else {
            double dx = this.outerTarget.posX - this.posX;
            double dy = this.outerTarget.posY + this.outerTarget.getEyeHeight() - (this.posY + this.height * 0.5D);
            double dz = this.outerTarget.posZ - this.posZ;
            double distanceSq = dx * dx + dy * dy + dz * dz;
            this.renderYawOffset = this.rotationYaw = -((float)(Math.atan2(dx, dz) * 180.0D / Math.PI));
            if (distanceSq <= activationRange() * activationRange() && this.canEntityBeSeen(this.outerTarget)) {
                if (this.attackCounter == 10) {
                    this.worldObj.playAuxSFXAtEntity(null, 1007, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                }
                if (++this.attackCounter >= attackWindup()) {
                    fireOuterProjectile(this.outerTarget, dx, dy, dz);
                    this.attackCounter = attackCooldown();
                }
            } else if (this.attackCounter > 0) {
                --this.attackCounter;
            }
        }

        if (!this.worldObj.isRemote) {
            this.dataWatcher.updateObject(16, (byte)(this.attackCounter > 10 ? 1 : 0));
        }
    }

    private void updateCourse() {
        if (this.outerTarget != null && this.getDistanceSqToEntity(this.outerTarget) > 12.0D * 12.0D) {
            this.waypointX = this.outerTarget.posX;
            this.waypointY = this.outerTarget.posY + 5.0D;
            this.waypointZ = this.outerTarget.posZ;
        } else {
            double dx = this.waypointX - this.posX;
            double dy = this.waypointY - this.posY;
            double dz = this.waypointZ - this.posZ;
            double distanceSq = dx * dx + dy * dy + dz * dz;
            if (distanceSq < 4.0D || distanceSq > 1600.0D) {
                this.waypointX = this.posX + (this.rand.nextDouble() * 2.0D - 1.0D) * 14.0D;
                this.waypointY = this.posY + (this.rand.nextDouble() * 2.0D - 1.0D) * 10.0D;
                this.waypointZ = this.posZ + (this.rand.nextDouble() * 2.0D - 1.0D) * 14.0D;
            }
        }

        if (--this.courseChangeCooldown <= 0) {
            this.courseChangeCooldown = 3 + this.rand.nextInt(5);
            double dx = this.waypointX - this.posX;
            double dy = this.waypointY - this.posY;
            double dz = this.waypointZ - this.posZ;
            double length = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
            if (length > 0.001D) {
                this.motionX += dx / length * 0.075D;
                this.motionY += dy / length * 0.075D;
                this.motionZ += dz / length * 0.075D;
            }
        }
    }

    private void fireOuterProjectile(EntityPlayer target, double dx, double dy, double dz) {
        this.worldObj.playAuxSFXAtEntity(null, 1008, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
        EntityLargeFireball fireball = createOuterFireball(target, dx, dy, dz);
        double length = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
        if (length < 0.001D) length = 1.0D;
        fireball.posX = this.posX + dx / length * 4.0D;
        fireball.posY = this.posY + this.height * 0.5D + dy / length * 4.0D - fireball.height * 0.5D;
        fireball.posZ = this.posZ + dz / length * 4.0D;
        this.worldObj.spawnEntityInWorld(fireball);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.difficultySetting > 0
                && this.worldObj.checkNoEntityCollision(this.boundingBox)
                && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
                && !this.worldObj.isAnyLiquid(this.boundingBox);
    }
}
