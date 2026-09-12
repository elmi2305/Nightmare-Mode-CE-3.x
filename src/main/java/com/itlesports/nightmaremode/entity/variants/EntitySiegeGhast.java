package com.itlesports.nightmaremode.entity.variants;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntitySiegeGhast extends EntityTieredNetherGhast {
    public EntitySiegeGhast(World world) {
        super(world);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(80.0D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return NetherTierHelper.getTier(this.worldObj, this.posX, this.posZ) >= 3 && super.getCanSpawnHere();
    }

    @Override protected double activationRange() { return 64.0D; }
    @Override protected int attackWindup() { return 20; }
    @Override protected int attackCooldown() { return -55; }

    @Override
    protected void fireTieredProjectiles(EntityPlayer target, double dx, double dy, double dz) {
        this.worldObj.playAuxSFXAtEntity(null, 1008, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
        double horizontalLength = Math.sqrt(dx * dx + dz * dz);
        if (horizontalLength < 0.001D) horizontalLength = 1.0D;
        double perpendicularX = -dz / horizontalLength;
        double perpendicularZ = dx / horizontalLength;
        for (int volleyIndex = -1; volleyIndex <= 1; ++volleyIndex) {
            double spread = volleyIndex * 0.7D;
            double spreadX = dx + perpendicularX * spread;
            double spreadZ = dz + perpendicularZ * spread;
            EntitySiegeFireball fireball = new EntitySiegeFireball(this.worldObj, this, spreadX, dy, spreadZ);
            fireball.field_92057_e = 1;
            this.launchProjectile(fireball, spreadX, dy, spreadZ);
        }
    }

    @Override
    public void addPotionEffect(PotionEffect effect) {
        if (effect.getPotionID() != Potion.field_76443_y.id) {
            super.addPotionEffect(effect);
        }
    }
}
