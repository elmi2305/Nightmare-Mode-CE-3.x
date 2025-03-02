package com.itlesports.nightmaremode.entity;

import net.minecraft.src.*;

public class EntityNightmareGolem extends EntityIronGolem {
    public EntityNightmareGolem(World par1World) {
        super(par1World);
        this.targetTasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, false, true, IMob.mobSelector));
    }

    @Override
    protected void updateAITick() {}

    @Override
    public boolean canAttackClass(Class par1Class) {
        return par1Class == EntityPlayer.class;
    }
    @Override
    protected void dropFewItems(boolean par1, int par2) {}

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(200);
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(40);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.27);
    }

    @Override
    public void onLivingUpdate() {
        if (!(this.getAttackTarget() instanceof EntityPlayer)) {
            EntityPlayer target = this.worldObj.getClosestVulnerablePlayer(this.posX,this.posY,this.posZ, 30);
            if(target != null){
                this.setAttackTarget(target);
            }
        }
        super.onLivingUpdate();
    }
}
