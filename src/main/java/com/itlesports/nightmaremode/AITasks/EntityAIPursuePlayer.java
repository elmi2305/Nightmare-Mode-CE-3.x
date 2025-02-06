package com.itlesports.nightmaremode.AITasks;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;

import java.util.Collections;
import java.util.List;

public class EntityAIPursuePlayer extends EntityAITarget {
    private final Class targetClass;
    private final int targetChance;
    private float targetDistance;
    private final EntityAINearestAttackableTargetSorter theNearestAttackableTargetSorter;
    private EntityLivingBase targetEntity;

    public EntityAIPursuePlayer(EntityCreature par1EntityCreature, Class par2Class, int par3, boolean par4, boolean par5) {
        super(par1EntityCreature, par4, par5);
        this.targetDistance = 0.0F;
        this.targetClass = par2Class;
        this.targetChance = par3;
        this.theNearestAttackableTargetSorter = new EntityAINearestAttackableTargetSorter(par1EntityCreature);
        this.setMutexBits(0);
    }

    public boolean shouldExecute() {
        if (!NightmareUtils.getIsMobEclipsed(this.taskOwner) || (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0)) {
            return false;
        } else {
            EntityPlayer player = this.taskOwner.worldObj.getClosestVulnerablePlayerToEntity(this.taskOwner, 16);
            if (player != null) {
                this.targetEntity = player;
                return true;
            }
            return false;
        }
    }

    public void startExecuting() {
        this.taskOwner.getMoveHelper().setMoveTo(this.targetEntity.posX,this.targetEntity.posY,this.targetEntity.posZ, 0.3f);
    }

    protected double getTargetDistance() {
        return this.targetDistance == 0.0F ? super.getTargetDistance() : (double)this.targetDistance;
    }
}
