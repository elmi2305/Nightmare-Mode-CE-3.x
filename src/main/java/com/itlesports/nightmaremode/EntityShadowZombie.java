package com.itlesports.nightmaremode;

import btw.entity.attribute.BTWAttributes;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import btw.world.util.WorldUtils;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.AITasks.EntityAILunge;
import com.itlesports.nightmaremode.AITasks.EntityAINearestAttackableTargetShadow;
import com.itlesports.nightmaremode.AITasks.EntityAIShadowTeleport;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public class EntityShadowZombie extends EntityZombie {
    public EntityShadowZombie(World par1World) {
        super(par1World);
        this.isImmuneToFire = true;
        this.tasks.removeAllTasksOfClass(ZombieBreakBarricadeBehavior.class);
        this.tasks.removeAllTasksOfClass(EntityAINearestAttackableTarget.class);
        this.targetTasks.addTask(2, new EntityAINearestAttackableTargetShadow(this, EntityPlayer.class, 0, true, false, null));
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
        else if (par1DamageSource instanceof EntityDamageSourceIndirect && par1DamageSource.getSourceOfDamage() instanceof EntityArrow arrow && arrow.shootingEntity instanceof EntityPlayer target){
            arrow.setDead();
            this.teleportToTarget(target);
            return false;
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    protected void dropFewItems(boolean par1, int par2) {
        if(this.rand.nextInt(12) == 0 && WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()){
            this.dropItem(Item.enderPearl.itemID,1);
        }

        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0) {
            int dropCount = this.rand.nextInt(3); // 0 - 2
            for (int i = 0; i < dropCount; ++i) {
                this.dropItem(bloodOrbID, 1);
            }
        }
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.26d);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(4.0);
        double followDistance = 16.0;
        if (this.worldObj != null) {
            int progress = NightmareUtils.getWorldProgress(this.worldObj);
            if(NightmareUtils.getIsBloodMoon()){
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((24.0 + progress * 6));
                // 30 -> 36 -> 42 -> 48
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0 + progress * 2);
                this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.29d);

            } else {
                this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(24.0 + progress * (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 4 : 2));
                // 24 -> 28 -> 32 -> 36
                // relaxed: 24 + 26
                this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(4.0 + progress * (this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 2 : 1));
                // 4 -> 6 -> 8 -> 10
                // relaxed: 4 -> 5 -> 6 -> 7
            }
            followDistance *= this.worldObj.getDifficulty().getZombieFollowDistanceMultiplier();
        }

        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(followDistance);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(10);
    }
    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {}

    @Override
    protected void addRandomArmor() {}

    private void teleportToTarget(EntityPlayer targetPlayer){
        int xOffset = (this.rand.nextBoolean() ? -1 : 1) * (this.rand.nextInt(3)+1);
        int zOffset = (this.rand.nextBoolean() ? -1 : 1) * (this.rand.nextInt(3)+1);

        int targetX = MathHelper.floor_double(targetPlayer.posX + xOffset);
        int targetY = MathHelper.floor_double(targetPlayer.posY);
        int targetZ = MathHelper.floor_double(targetPlayer.posZ + zOffset);

        if(this.worldObj.getBlockId(targetX, targetY, targetZ) == 0 && this.worldObj.getBlockId(targetX, targetY-1, targetZ) != 0 && this.worldObj.getBlockId(targetX, targetY+1, targetZ) == 0){
            this.setPositionAndUpdate(targetX,targetY, targetZ);
            this.playSound("mob.endermen.portal",2.0F,1.0F);
        }
    }
}
