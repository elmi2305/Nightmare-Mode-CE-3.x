package com.itlesports.nightmaremode.entity.outer;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.Potion;
import net.minecraft.src.PotionEffect;
import net.minecraft.src.World;

public class EntityIceZombie extends EntityZombie {
    public EntityIceZombie(World world) {
        super(world);
        this.isImmuneToFire = false;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        if (target instanceof EntityLivingBase living) {
            living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 80, 0));
        }
        return super.attackEntityAsMob(target);
    }
}
