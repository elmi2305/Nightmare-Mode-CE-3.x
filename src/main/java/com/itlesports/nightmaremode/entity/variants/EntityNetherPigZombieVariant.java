package com.itlesports.nightmaremode.entity.variants;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPigZombie;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public abstract class EntityNetherPigZombieVariant extends EntityPigZombie {
    protected EntityNetherPigZombieVariant(World world) {
        super(world);
        this.isImmuneToFire = true;
    }

    protected abstract int getMinimumNetherTier();

    protected abstract int getFireSecondsOnHit();

    protected double getVariantHealth() {
        return 30.0D;
    }

    protected double getVariantDamage() {
        return 7.0D;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(this.getVariantHealth());
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(this.getVariantDamage());
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        boolean hit = super.attackEntityAsMob(target);
        if (hit) target.setFire(this.getFireSecondsOnHit());
        return hit;
    }

    @Override
    public boolean getCanSpawnHere() {
        return NetherTierHelper.getTier(this.worldObj, this.posX, this.posZ) >= this.getMinimumNetherTier()
                && super.getCanSpawnHere();
    }
}
