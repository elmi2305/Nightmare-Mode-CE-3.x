package com.itlesports.nightmaremode.entity.outer;

import net.minecraft.src.*;

public class EntityAcidProjectile extends EntitySnowball {
    public EntityAcidProjectile(World world) {
        super(world);
    }

    public EntityAcidProjectile(World world, EntityLivingBase thrower, EntityLivingBase target) {
        super(world, thrower);
        double dx = target.posX - this.posX;
        double dy = target.posY + target.getEyeHeight() - this.posY;
        double dz = target.posZ - this.posZ;
        this.setThrowableHeading(dx, dy, dz, 0.9F, 3.0F);
    }

    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (!this.worldObj.isRemote && hit.entityHit instanceof EntityLivingBase living) {
            living.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 3.0F);
            living.addPotionEffect(new PotionEffect(Potion.poison.id, 100, 0));
        }
        for (int i = 0; i < 12; ++i) {
            this.worldObj.spawnParticle("happyVillager", this.posX, this.posY, this.posZ,
                    (this.rand.nextDouble() - 0.5D) * 0.15D,
                    this.rand.nextDouble() * 0.12D,
                    (this.rand.nextDouble() - 0.5D) * 0.15D);
        }
        if (!this.worldObj.isRemote) this.setDead();
    }
}
