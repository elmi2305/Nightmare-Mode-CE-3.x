package com.itlesports.nightmaremode.entity.outer;

import com.itlesports.nightmaremode.world.BalanceProfile;

import net.minecraft.src.*;

public class EntityIceGolem extends EntityIronGolem {
    public EntityIceGolem(World world) {
        super(world);
        this.targetTasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
        this.targetTasks.removeAllTasksOfClass(EntityAIDefendVillage.class);
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
    }

    @Override
    protected void updateAITick() {}

    @Override
    public boolean canAttackClass(Class targetClass) {
        return EntityPlayer.class.isAssignableFrom(targetClass);
    }

    @Override
    protected void dropFewItems(boolean killedByPlayer, int looting) {}

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(BalanceProfile.isCombatEasy() ? 40.0D : 140.0D);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(24.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.2D);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.difficultySetting > 0
                && this.worldObj.checkNoEntityCollision(this.boundingBox)
                && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
                && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    @Override
    public void readEntityFromNBT(net.minecraft.src.NBTTagCompound tag) {
        com.itlesports.nightmaremode.util.EasyCombatStats defaults = com.itlesports.nightmaremode.util.EasyCombatStats.beforeLoad(this);
        super.readEntityFromNBT(tag);
        com.itlesports.nightmaremode.util.EasyCombatStats.afterLoad(this, defaults);
    }
}
