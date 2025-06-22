package com.itlesports.nightmaremode.entity;

import net.minecraft.src.DamageSource;
import net.minecraft.src.EntitySkeleton;
import net.minecraft.src.World;

public class EntitySkeletonDrowned extends EntitySkeleton {
    public EntitySkeletonDrowned(World par1World) {
        super(par1World);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        if(damageSource == DamageSource.drown){return false;}
        return super.attackEntityFrom(damageSource, damage);
    }
}
