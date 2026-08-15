package com.itlesports.nightmaremode.entity.outer;

import net.minecraft.src.*;

public class EntityAngelDragon extends EntityDragon {
    private EntityPlayer angerTarget;
    private int angerTicks;
    private int destinationCooldown;
    private double destinationX;
    private double destinationY;
    private double destinationZ;
    private int ramCooldown;

    public EntityAngelDragon(World world) {
        super(world);
        this.noClip = true;
        this.setSize(8.0F, 4.0F);
        this.experienceValue = 80;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(80.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.32D);
    }

    @Override
    public void onLivingUpdate() {
        this.prevAnimTime = this.animTime;
        this.animTime += 0.025F;
        if (this.ramCooldown > 0) --this.ramCooldown;
        if (this.angerTicks > 0) --this.angerTicks;

        EntityPlayer nearest = this.worldObj.getClosestVulnerablePlayerToEntity(this, 48.0D);
        if (nearest != null && (this.angerTicks > 0 || this.getDistanceSqToEntity(nearest) <= 64.0D)) {
            this.angerTarget = nearest;
            this.angerTicks = Math.max(this.angerTicks, 200);
        } else if (this.angerTicks <= 0) {
            this.angerTarget = null;
        }

        if (this.angerTarget != null) {
            this.destinationX = this.angerTarget.posX;
            this.destinationY = this.angerTarget.posY + this.angerTarget.getEyeHeight();
            this.destinationZ = this.angerTarget.posZ;
        } else if (--this.destinationCooldown <= 0 || distanceSqToDestination() < 16.0D) {
            this.destinationX = this.posX + (this.rand.nextDouble() * 2.0D - 1.0D) * 45.0D;
            this.destinationY = Math.max(12.0D, Math.min(120.0D, this.posY + (this.rand.nextDouble() * 2.0D - 1.0D) * 24.0D));
            this.destinationZ = this.posZ + (this.rand.nextDouble() * 2.0D - 1.0D) * 45.0D;
            this.destinationCooldown = 80 + this.rand.nextInt(120);
        }

        double dx = this.destinationX - this.posX;
        double dy = this.destinationY - this.posY;
        double dz = this.destinationZ - this.posZ;
        double length = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
        if (length > 0.001D) {
            double acceleration = this.angerTarget == null ? 0.018D : 0.032D;
            this.motionX += dx / length * acceleration;
            this.motionY += dy / length * acceleration;
            this.motionZ += dz / length * acceleration;
            double maxSpeed = this.angerTarget == null ? 0.34D : 0.5D;
            double speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            if (speed > maxSpeed) {
                this.motionX = this.motionX / speed * maxSpeed;
                this.motionY = this.motionY / speed * maxSpeed;
                this.motionZ = this.motionZ / speed * maxSpeed;
            }
            this.rotationYaw = this.renderYawOffset = -((float)(Math.atan2(dx, dz) * 180.0D / Math.PI));
        }
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.96D;
        this.motionY *= 0.96D;
        this.motionZ *= 0.96D;

        if (!this.worldObj.isRemote && this.angerTarget != null && this.ramCooldown <= 0
                && this.boundingBox.expand(1.5D, 1.0D, 1.5D).intersectsWith(this.angerTarget.boundingBox)) {
            double oldX = this.angerTarget.motionX;
            double oldY = this.angerTarget.motionY;
            double oldZ = this.angerTarget.motionZ;
            this.angerTarget.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
            this.angerTarget.motionX = oldX;
            this.angerTarget.motionY = oldY;
            this.angerTarget.motionZ = oldZ;
            this.ramCooldown = 30;
        }
    }

    private double distanceSqToDestination() {
        double dx = this.destinationX - this.posX;
        double dy = this.destinationY - this.posY;
        double dz = this.destinationZ - this.posZ;
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (source.getEntity() instanceof EntityPlayer player) {
            this.angerTarget = player;
            this.angerTicks = 600;
        }
        return this.func_82195_e(source, damage);
    }

    @Override
    public boolean attackEntityFromPart(EntityDragonPart part, DamageSource source, float damage) {
        return this.attackEntityFrom(source, part == this.dragonPartHead ? damage : damage * 0.5F);
    }

    @Override
    protected void onDeathUpdate() {
        if (++this.deathTime >= 20) this.setDead();
    }

    @Override public Entity[] getParts() { return new Entity[0]; }
    @Override public boolean canBeCollidedWith() { return true; }
    @Override protected void despawnEntity() {}
    @Override public boolean getCanSpawnHere() { return this.worldObj.difficultySetting > 0; }
}
