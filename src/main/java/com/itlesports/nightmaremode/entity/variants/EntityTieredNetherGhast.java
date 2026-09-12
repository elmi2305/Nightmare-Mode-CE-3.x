package com.itlesports.nightmaremode.entity.variants;

import net.minecraft.src.EntityFireball;
import net.minecraft.src.EntityGhast;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

abstract class EntityTieredNetherGhast extends EntityGhast {
    private EntityPlayer tieredTarget;
    private int retargetCooldown;

    EntityTieredNetherGhast(World world) {
        super(world);
    }

    protected abstract double activationRange();
    protected abstract int attackWindup();
    protected abstract int attackCooldown();
    protected abstract void fireTieredProjectiles(EntityPlayer target, double dx, double dy, double dz);

    @Override
    protected void updateEntityActionState() {
        ++this.entityAge;
        this.despawnEntity();
        this.prevAttackCounter = this.attackCounter;

        if (this.tieredTarget == null || !this.tieredTarget.isEntityAlive() || --this.retargetCooldown <= 0) {
            this.tieredTarget = this.worldObj.getClosestVulnerablePlayerToEntity(this, this.activationRange());
            this.retargetCooldown = 20;
        }

        this.updateCourse();
        if (this.tieredTarget == null) {
            this.renderYawOffset = this.rotationYaw = -((float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI));
            if (this.attackCounter > 0) --this.attackCounter;
        } else {
            double dx = this.tieredTarget.posX - this.posX;
            double dy = this.tieredTarget.posY + this.tieredTarget.getEyeHeight() - (this.posY + this.height * 0.5D);
            double dz = this.tieredTarget.posZ - this.posZ;
            this.renderYawOffset = this.rotationYaw = -((float)(Math.atan2(dx, dz) * 180.0D / Math.PI));
            if (this.canEntityBeSeen(this.tieredTarget)) {
                if (this.attackCounter == 10) {
                    this.worldObj.playAuxSFXAtEntity(null, 1007, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
                }
                if (++this.attackCounter >= this.attackWindup()) {
                    this.fireTieredProjectiles(this.tieredTarget, dx, dy, dz);
                    this.attackCounter = this.attackCooldown();
                }
            } else if (this.attackCounter > 0) {
                --this.attackCounter;
            }
        }

        if (!this.worldObj.isRemote) {
            this.dataWatcher.updateObject(16, (byte)(this.attackCounter > 10 ? 1 : 0));
        }
    }

    protected void launchProjectile(EntityFireball projectile, double dx, double dy, double dz) {
        double length = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
        if (length < 0.001D) length = 1.0D;
        double normalizedX = dx / length;
        double normalizedY = dy / length;
        double normalizedZ = dz / length;
        projectile.accelerationX = normalizedX * 0.1D;
        projectile.accelerationY = normalizedY * 0.1D;
        projectile.accelerationZ = normalizedZ * 0.1D;
        projectile.setPosition(this.posX + normalizedX * 4.0D,
                this.posY + this.height * 0.5D + normalizedY * 4.0D - projectile.height * 0.5D,
                this.posZ + normalizedZ * 4.0D);
        this.worldObj.spawnEntityInWorld(projectile);
    }

    private void updateCourse() {
        if (this.tieredTarget != null && this.getDistanceSqToEntity(this.tieredTarget) > 12.0D * 12.0D) {
            this.waypointX = this.tieredTarget.posX;
            this.waypointY = this.tieredTarget.posY + 5.0D;
            this.waypointZ = this.tieredTarget.posZ;
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
}
