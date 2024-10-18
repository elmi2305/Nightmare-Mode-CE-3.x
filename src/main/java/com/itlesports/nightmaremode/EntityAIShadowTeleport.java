package com.itlesports.nightmaremode;

import btw.world.util.difficulty.Difficulties;
import net.minecraft.src.*;

import java.util.Collections;
import java.util.List;

public class EntityAIShadowTeleport extends EntityAITarget {
    private EntityLivingBase targetEntity;
    private int cooldown;

    public EntityAIShadowTeleport(EntityCreature par1EntityCreature, boolean par2, boolean par3) {
        super(par1EntityCreature, par2, par3);
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        if (this.taskOwner.getAttackTarget() instanceof EntityPlayer player) {
            this.targetEntity = player;
            return this.taskOwner.getDistanceSqToEntity(this.targetEntity) <= (this.taskOwner.worldObj.getDifficulty() == Difficulties.HOSTILE ? 256 : 100);
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        int targetX;
        if (this.taskOwner.rand.nextInt(2) == 0) {
            targetX = MathHelper.floor_double(this.targetEntity.posX + this.taskOwner.rand.nextInt(3)+1);
        } else{
            targetX = MathHelper.floor_double(this.targetEntity.posX - this.taskOwner.rand.nextInt(3)+1);
        }
        int targetY = MathHelper.floor_double(this.targetEntity.posY);
        int targetZ;
        if (this.taskOwner.rand.nextInt(2)==0) {
            targetZ = MathHelper.floor_double(this.targetEntity.posZ + this.taskOwner.rand.nextInt(3)+1);
        } else{
            targetZ = MathHelper.floor_double(this.targetEntity.posZ - this.taskOwner.rand.nextInt(3)+1);
        }


        if (cooldown <= 0) {
            cooldown = 0;
            for(int i = -1; i < 2; i++) {
                if(this.taskOwner.worldObj.getBlockId(targetX, targetY + i, targetZ) == 0 && this.taskOwner.worldObj.getBlockId(targetX, targetY + i -1, targetZ) != 0){
                    this.taskOwner.setPositionAndUpdate(targetX,targetY + i, targetZ);
                    this.taskOwner.playSound("mob.endermen.portal",2.0F,1.0F);

                    cooldown = 20 + this.taskOwner.rand.nextInt(20)+1;
                    break;
                }
            }
        }
        cooldown -= 1;
        return cooldown <= 0;
    }
}
