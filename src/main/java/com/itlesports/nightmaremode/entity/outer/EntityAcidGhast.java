package com.itlesports.nightmaremode.entity.outer;

import net.minecraft.src.*;

public class EntityAcidGhast extends EntityOuterGhast {
    public EntityAcidGhast(World world) {
        super(world);
    }

    @Override protected double activationRange() { return 36.0D; }
    @Override protected int attackWindup() { return 28; }
    @Override protected int attackCooldown() { return -55; }

    @Override
    protected EntityLargeFireball createOuterFireball(EntityPlayer target, double dx, double dy, double dz) {
        return new EntityAcidFireball(this.worldObj, this, dx, dy, dz);
    }
}
