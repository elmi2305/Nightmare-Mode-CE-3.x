package com.itlesports.nightmaremode;

import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import btw.world.util.difficulty.Difficulties;
import net.minecraft.src.*;

public class EntityShadowZombie extends EntityZombie {
    public EntityShadowZombie(World par1World) {
        super(par1World);
        this.isImmuneToFire = true;
        this.tasks.removeAllTasksOfClass(ZombieBreakBarricadeBehavior.class);
        this.tasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
        this.targetTasks.addTask(2, new EntityAINearestAttackableTargetShadow(this, EntityPlayer.class, 0, true, false, (IEntitySelector)null));
        this.targetTasks.removeAllTasksOfClass(EntityAILunge.class);
        this.targetTasks.addTask(2, new EntityAIShadowTeleport(this, false, false));
    }
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if (this.worldObj.getDifficulty().canCreepersBreachWalls() && par1DamageSource.isExplosion()) {
            par2 /= 2.0f;
        }
        if (par1DamageSource == DamageSource.inWall){return false;}
        else if (par1DamageSource == DamageSource.fall){return false;}
        else if (par1DamageSource == DamageSource.onFire){return false;}
        else if (par1DamageSource == DamageSource.inFire){return false;}
        else if (par1DamageSource == DamageSource.lava){return false;}
        else if (par1DamageSource instanceof EntityDamageSourceIndirect && ((EntityDamageSourceIndirect)par1DamageSource).getSourceOfDamage() instanceof EntityArrow arrow && arrow.shootingEntity instanceof EntityPlayer target){
            arrow.setDead();
            this.teleportToTarget(target);
            return false;
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.26);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(4.0);
        double followDistance = 16.0;
        if (this.worldObj != null) {
            followDistance *= (double)this.worldObj.getDifficulty().getZombieFollowDistanceMultiplier();
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(24.0 + (NightmareUtils.getGameProgressMobsLevel(this.worldObj) * (4 - (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 2))));
            // 24 -> 28 -> 32 -> 36
            // relaxed: 24 + 26
            this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0 + NightmareUtils.getGameProgressMobsLevel(this.worldObj) * (2 - (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 0 : 1)));
            // 4 -> 6 -> 8 -> 10
            // relaxed: 4 -> 5 -> 6 -> 7
        }

        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(followDistance);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(10);
    }

    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {}

    @Override
    protected void addRandomArmor() {}

    private void teleportToTarget(EntityPlayer targetPlayer){
        int targetX;
        if (this.rand.nextInt(2)==0) {
            targetX = MathHelper.floor_double(targetPlayer.posX + this.rand.nextInt(3)+1);
        } else{
            targetX = MathHelper.floor_double(targetPlayer.posX - this.rand.nextInt(3)+1);
        }
        int targetY = MathHelper.floor_double(targetPlayer.posY);
        int targetZ;
        if (this.rand.nextInt(2)==0) {
            targetZ = MathHelper.floor_double(targetPlayer.posZ + this.rand.nextInt(3)+1);
        } else{
            targetZ = MathHelper.floor_double(targetPlayer.posZ - this.rand.nextInt(3)+1);
        }

        for(int i = -1; i < 2; i++) {
            if(this.worldObj.getBlockId(targetX, targetY + i, targetZ) == 0 && this.worldObj.getBlockId(targetX, targetY + i -1, targetZ) != 0){
                this.setPositionAndUpdate(targetX,targetY + i, targetZ);
                this.playSound("mob.endermen.portal",2.0F,1.0F);
                break;
            }
        }
    }
}
