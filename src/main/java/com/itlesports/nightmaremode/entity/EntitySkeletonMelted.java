package com.itlesports.nightmaremode.entity;

import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.*;

public class EntitySkeletonMelted extends EntitySkeleton {
    public EntitySkeletonMelted(World par1World) {
        super(par1World);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damage) {
        if(damageSource.getSourceOfDamage() instanceof EntityLivingBase attacker && this.rand.nextBoolean()){


            attacker.setFire(1 + NMUtils.getWorldProgress() * 2 + (this.rand.nextInt(3) == 0 ? 1 : 0));
        }
        if(damageSource.isFireDamage()){return false;}
        if(damageSource.getEntity() instanceof EntitySnowball){
            damage = 3;
        }
        return super.attackEntityFrom(damageSource, damage);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(20);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.2);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20);
    }
}