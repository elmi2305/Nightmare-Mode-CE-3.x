package com.itlesports.nightmaremode.entity;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.World;

public class EntitySkeletonMelted extends EntitySkeleton {
    public EntitySkeletonMelted(World par1World) {
        super(par1World);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        if(damageSource.getSourceOfDamage() instanceof EntityLivingBase){
            damageSource.getSourceOfDamage().setFire(5);
        }
        if(damageSource.isFireDamage()){return false;}
        return super.attackEntityFrom(damageSource, damage);
    }
}