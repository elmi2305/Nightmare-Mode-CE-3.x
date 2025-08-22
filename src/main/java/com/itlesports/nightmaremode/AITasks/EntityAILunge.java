package com.itlesports.nightmaremode.AITasks;

import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.entity.EntityBloodZombie;
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
            boolean isEclipse = NMUtils.getIsMobEclipsed(this.taskOwner);
            int range = isEclipse ? 50 : 30;

            return (this.taskOwner.getDistanceSqToEntity(this.targetEntity) <= range || this.taskOwner instanceof EntityBloodZombie)  // 5.4 blocks
                    && !this.taskOwner.getNavigator().noPath()
                    && this.taskOwner.onGround
                    && this.taskOwner.worldObj.getDifficulty() == Difficulties.HOSTILE;
        }
        return false;
    }

    private static double clamp(double input, double radius) {
        if (input > radius) {
            return radius;
        } else return Math.max(input, -radius);
    }

    @Override
    public boolean continueExecuting(){
        boolean isHoldingItem = this.taskOwner.getHeldItem() != null;
        boolean isEclipse = NMUtils.getIsMobEclipsed(this.taskOwner);

        if(isHoldingItem && !isEclipse){return false;}
        // ensures normal behavior on non-eclipse

        if (this.cooldown <= 0) {
            double var1 = this.targetEntity.posX - this.taskOwner.posX;
            double var2 = this.targetEntity.posZ - this.taskOwner.posZ;
            Vec3 vector = Vec3.createVectorHelper(var1, 0, var2);
            vector.normalize();
            this.taskOwner.motionX = clamp(vector.xCoord * 0.2, 1.0);
            this.taskOwner.motionY = 0.34;
            this.taskOwner.motionZ = clamp(vector.zCoord * 0.2, 1.0);

            if(isEclipse){
                this.cooldown = isHoldingItem ? 20 + this.taskOwner.rand.nextInt(20) : 0;
            }
            else {
                this.cooldown = 20 + this.taskOwner.rand.nextInt(20);
                // 1 second with a variance of 1s
            }
        }

        this.cooldown = Math.max(cooldown - 1, 0);
        return this.cooldown == 0 && this.taskOwner.onGround;
    }
}
