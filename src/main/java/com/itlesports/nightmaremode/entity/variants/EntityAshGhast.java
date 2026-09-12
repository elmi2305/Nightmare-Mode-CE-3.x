package com.itlesports.nightmaremode.entity.variants;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntitySmallFireball;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityAshGhast extends EntityTieredNetherGhast {
    public EntityAshGhast(World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return NetherTierHelper.getTier(this.worldObj, this.posX, this.posZ) >= 2 && super.getCanSpawnHere();
    }

    @Override protected double activationRange() { return 64.0D; }
    @Override protected int attackWindup() { return 12; }
    @Override protected int attackCooldown() { return -16; }

    @Override
    protected void fireTieredProjectiles(EntityPlayer target, double dx, double dy, double dz) {
        this.worldObj.playAuxSFXAtEntity(null, 1009, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
        this.launchProjectile(new EntitySmallFireball(this.worldObj, this, dx, dy, dz), dx, dy, dz);
    }

    @Override
    public void addPotionEffect(PotionEffect effect) {
        if (effect.getPotionID() != Potion.field_76443_y.id) {
            super.addPotionEffect(effect);
        }
    }
}
