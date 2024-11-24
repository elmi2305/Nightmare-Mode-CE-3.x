package com.itlesports.nightmaremode;

import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.behavior.SimpleWanderBehavior;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehaviorHostile;
import btw.entity.mob.behavior.ZombieSecondaryAttackBehavior;
import btw.entity.util.ZombieSecondaryTargetFilter;
import net.minecraft.src.*;

public class NightmareEntity extends EntityZombie {
    public NightmareEntity(World par1World) {
        super(par1World);
        this.tasks.removeAllTasks();
        this.targetTasks.removeAllTasks();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0f));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.getNavigator().setBreakDoors(false);
        this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0, false));
        this.tasks.addTask(6, new SimpleWanderBehavior(this, 1.0f));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false, false, null, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.2f);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(15.0d);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(200);
//        this.getEntityAttribute(BTWAttributes.armor).setAttribute(20);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(64.0d);
    }

    @Override
    public int getTotalArmorValue() {
        int var1 = super.getTotalArmorValue();
        if (var1 > 20) {
            var1 = 20;
        }
        return var1;
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    protected String getLivingSound() {
        return "portal.portal";
    }

    @Override
    protected String getHurtSound() {
        return "random.classic_hurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.ghast.death";
    }

    @Override
    protected void playStepSound(int par1, int par2, int par3, int par4) {
        this.playSound("mob.zombie.step", 0.15f, 1.0f);
    }
}
