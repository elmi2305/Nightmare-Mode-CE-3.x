package com.itlesports.nightmaremode.entity;

import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.*;

public class EntitySkeletonMelted extends EntitySkeleton {
    public EntitySkeletonMelted(World par1World) {
        super(par1World);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        if(damageSource.getSourceOfDamage() instanceof EntityLivingBase){
            damageSource.getSourceOfDamage().setFire(3 + NMUtils.getWorldProgress() * 2);
        }
        if(damageSource.isFireDamage()){return false;}
        if(damageSource.getEntity() instanceof EntitySnowball){
            damage = 3;
        }
        return super.attackEntityFrom(damageSource, damage);
    }
}