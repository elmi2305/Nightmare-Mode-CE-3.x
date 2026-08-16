package com.itlesports.nightmaremode.entity.outer;

import com.itlesports.nightmaremode.entity.underworld.EntityPollenCloud;
import net.minecraft.src.*;

public class EntityAcidFireball extends EntityLargeFireball {
    public EntityAcidFireball(World world) {
        super(world);
    }

    public EntityAcidFireball(World world, EntityLivingBase shooter, double dx, double dy, double dz) {
        super(world, shooter, dx, dy, dz);
        this.field_92057_e = 1;
    }

    @Override
    protected void onImpact(MovingObjectPosition hit) {
        if (!this.worldObj.isRemote) {
            if (hit.entityHit != null) {
                hit.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 6.0F);
            }
            this.worldObj.newExplosion(null, this.posX, this.posY, this.posZ, 1.0F, false,
                    this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
            this.worldObj.spawnEntityInWorld(new EntityPollenCloud(this.worldObj, this.posX, this.posY, this.posZ, 7.0D));
            this.setDead();
        }
    }
}
