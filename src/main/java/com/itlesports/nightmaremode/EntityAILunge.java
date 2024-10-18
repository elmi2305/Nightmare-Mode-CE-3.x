package com.itlesports.nightmaremode;

import btw.world.util.difficulty.Difficulties;
import net.minecraft.src.*;

public class EntityAILunge extends EntityAITarget {
    private EntityLivingBase targetEntity;
    private int cooldown;


    public EntityAILunge(EntityCreature par1EntityCreature, boolean par2) {
        super(par1EntityCreature, par2);
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        if (this.taskOwner.getAttackTarget() instanceof EntityPlayer player) {
            this.targetEntity = player;
            return this.taskOwner.getDistanceSqToEntity(this.targetEntity) <= 30  // 5.4 blocks
                    && !this.taskOwner.getNavigator().noPath()
                    && this.taskOwner.onGround
                    && this.taskOwner.getHeldItem() == null
                    && this.taskOwner.worldObj != null // paranoid so I'm checking if it's null. a spawner should never execute this code
                    && this.taskOwner.worldObj.getDifficulty() == Difficulties.HOSTILE;
        }
        return false;
    }

    @Override
    public boolean continueExecuting(){
        if (cooldown <= 0) {
            double var1 = this.targetEntity.posX - this.taskOwner.posX;
            double var2 = this.targetEntity.posZ - this.taskOwner.posZ;
            Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
            vector.normalize();
            this.taskOwner.motionX = vector.xCoord * 0.2;
            this.taskOwner.motionY = 0.34;
            this.taskOwner.motionZ = vector.zCoord * 0.2;
            cooldown = 20 + this.taskOwner.rand.nextInt(20);
            // 1 second with a variance of 1s
        }

        cooldown -= 1;
        return cooldown <= 0;
    }
}
