package com.itlesports.nightmaremode.entity.outer;

import net.minecraft.src.*;

public class EntityAngelGhast extends EntityOuterGhast {
    private int angerTicks;

    public EntityAngelGhast(World world) {
        super(world);
    }

    @Override protected double activationRange() { return 16.0D; }
    @Override protected int attackWindup() { return 22; }
    @Override protected int attackCooldown() { return -42; }
    @Override protected boolean isProvoked() { return this.angerTicks > 0; }

    @Override
    public void onLivingUpdate() {
        if (this.angerTicks > 0) --this.angerTicks;
        super.onLivingUpdate();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (source.getEntity() instanceof EntityPlayer) this.angerTicks = 600;
        return super.attackEntityFrom(source, damage);
    }

    @Override
    protected EntityLargeFireball createOuterFireball(EntityPlayer target, double dx, double dy, double dz) {
        return new EntityAngelFireball(this.worldObj, this, dx, dy, dz);
    }
}
