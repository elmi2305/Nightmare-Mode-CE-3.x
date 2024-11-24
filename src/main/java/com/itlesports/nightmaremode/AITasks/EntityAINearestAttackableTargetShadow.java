package com.itlesports.nightmaremode.AITasks;


import btw.world.util.difficulty.Difficulties;
import net.minecraft.src.*;

import java.util.Collections;
import java.util.List;

public class EntityAINearestAttackableTargetShadow extends EntityAINearestAttackableTarget {
    // the only difference between this class and the one it extends is the y parameter in the bounding box expansion. it's increased from 4.0 to like 10
    private final Class targetClass;
    private final int targetChance;
    private float targetDistance;
    private final EntityAINearestAttackableTargetSorter theNearestAttackableTargetSorter;
    private final IEntitySelector targetEntitySelector;
    private EntityLivingBase targetEntity;

    public EntityAINearestAttackableTargetShadow(EntityCreature par1EntityCreature, Class par2Class, int par3, boolean par4, boolean par5, IEntitySelector par6IEntitySelector) {
        super(par1EntityCreature,par2Class,par3,par4,par5,par6IEntitySelector);

        this.targetDistance = 0.0F;
        this.targetClass = par2Class;
        this.targetChance = par3;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTargetSorter(par1EntityCreature);
        this.setMutexBits(1);
        this.targetEntitySelector = new EntityAINearestAttackableTargetSelector(this, par6IEntitySelector);
    }


    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        } else {
            double verticalRange = this.taskOwner.worldObj.getDifficulty() == Difficulties.HOSTILE ? 10.0 : 6;
            double var1 = this.getTargetDistance();
            List var3 = this.taskOwner.worldObj.selectEntitiesWithinAABB(this.targetClass, this.taskOwner.boundingBox.expand(var1, verticalRange, var1), this.targetEntitySelector);
            Collections.sort(var3, this.theNearestAttackableTargetSorter);
            if (var3.isEmpty()) {
                return false;
            } else {
                this.targetEntity = (EntityLivingBase)var3.get(0);
                return true;
            }
        }
    }

    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }

    protected double getTargetDistance() {
        return this.targetDistance == 0.0F ? super.getTargetDistance() : (double)this.targetDistance;
    }
}