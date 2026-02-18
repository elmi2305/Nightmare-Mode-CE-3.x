package com.itlesports.nightmaremode.entity.underworld;

import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.behavior.ZombieSecondaryAttackBehavior;
import com.itlesports.nightmaremode.AITasks.EntityAIBurrow;
import com.itlesports.nightmaremode.AITasks.EntityAISummonBabies;
import net.minecraft.src.*;

public class FlowerZombie extends EntityZombie implements IFlowerMob{
    public FlowerZombie(World par1World) {
        super(par1World);
        this.isImmuneToFire = true;
        this.tasks.removeAllTasksOfClass(ZombieSecondaryAttackBehavior.class);
        this.tasks.addTask(1, new EntityAIBurrow(this, 5));
        this.tasks.addTask(1, new EntityAISummonBabies(this));

    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.25f);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(6.0);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(32);
    }

    @Override
    public float knockbackMagnitude() {
        return super.knockbackMagnitude() / 2;
    }

    @Override
    public boolean attackEntityFrom(DamageSource src, float dmg) {
        if(src == DamageSource.inWall) return false;

        if(src.getSourceOfDamage() instanceof EntityLivingBase b){
            if (this.rand.nextBoolean()) {
                b.attackEntityFrom(DamageSource.generic, 2);
            }
            if(this.getHealth() < this.getMaxHealth() / 2){
                dmg /= 2;
            }
        }
        return super.attackEntityFrom(src, dmg);
    }

    @Override
    public boolean attackEntityAsMob(Entity attackedEntity) {
        if(attackedEntity instanceof EntityLivingBase b){
            b.addPotionEffect(new PotionEffect(Potion.weakness.id, 60, 0));
            b.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 0));
        }
        return super.attackEntityAsMob(attackedEntity);
    }

}
