package com.itlesports.nightmaremode.entity;

import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehaviorHostile;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityZombieImposter extends EntityZombie {
    public EntityZombieImposter(World par1World) {
        super(par1World);
        this.tasks.removeAllTasksOfClass(ZombieBreakBarricadeBehaviorHostile.class);
        this.isImmuneToFire = true;
    }
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.40d);
        if (this.worldObj != null) {
            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20 + progress * (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 5 : 2));
            // 20 -> 25 -> 30 -> 35
            // relaxed: 24 + 26
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3.0 + progress * (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 3 : 1));
        }

        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(32);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(4);
    }

    @Override
    public boolean isAffectedByMovementModifiers() {
        return false;
    }

    @Override
    protected void checkForCatchFireInSun() {}

    @Override
    public void onUpdate() {
        if(!this.hasAttackTarget()) {
            EntityPlayer closestPlayer = this.worldObj.getClosestVulnerablePlayerToEntity(this, 20);
            if (closestPlayer != null) {
                this.setAttackTarget(closestPlayer);
            }
        }
        super.onUpdate();
    }
}
