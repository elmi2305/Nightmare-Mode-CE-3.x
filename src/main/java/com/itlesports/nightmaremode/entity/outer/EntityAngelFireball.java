package com.itlesports.nightmaremode.entity.outer;

import net.minecraft.src.*;

public class EntityAngelFireball extends EntityLargeFireball {
    public EntityAngelFireball(World world) {
        super(world);
    }

    public EntityAngelFireball(World world, EntityLivingBase shooter, double dx, double dy, double dz) {
        super(world, shooter, dx, dy, dz);
    }

    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (!this.worldObj.isRemote) {
            if (hit.entityHit != null) {
                double oldX = hit.entityHit.motionX;
                double oldY = hit.entityHit.motionY;
                double oldZ = hit.entityHit.motionZ;
                hit.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 6.0F);
                hit.entityHit.motionX = oldX;
                hit.entityHit.motionY = oldY;
                hit.entityHit.motionZ = oldZ;
            }
            this.setDead();
        }
    }
}
