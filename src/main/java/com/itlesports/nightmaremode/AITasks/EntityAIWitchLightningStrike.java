package com.itlesports.nightmaremode.AITasks;

import btw.entity.LightningBoltEntity;
import net.minecraft.src.*;

import java.util.List;

public class EntityAIWitchLightningStrike extends EntityAITarget {
    private int cooldown;

    public EntityAIWitchLightningStrike(EntityCreature par1EntityCreature) {
        super(par1EntityCreature, true, true);
    }


    @Override
    public boolean shouldExecute() {
        if(this.taskOwner.getAttackTarget() instanceof EntityPlayer targetPlayer) {
            return
                    this.taskOwner.getEntitySenses().canSee(targetPlayer)
                    && this.taskOwner.getDistanceSqToEntity(targetPlayer) < 576;
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        if(this.cooldown >= 200){
            this.cooldown = 200;
            List list = this.taskOwner.worldObj.getEntitiesWithinAABBExcludingEntity(this.taskOwner, this.taskOwner.boundingBox.expand(10,4,10));
            for (Object tempEntity : list) {
                if(tempEntity instanceof EntityCreeper creeper && creeper.getDataWatcher().getWatchableObjectByte(17) == 0){
                    creeper.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 1000,0));
                    Entity lightningbolt = new LightningBoltEntity(this.taskOwner.worldObj, creeper.posX, creeper.posY + 1, creeper.posZ);
                    this.taskOwner.worldObj.addWeatherEffect(lightningbolt);
                    this.cooldown = 0;
                    break;
                }
            }
        }
        this.cooldown++;
        return super.continueExecuting();
    }
}
