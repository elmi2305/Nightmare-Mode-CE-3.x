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
        NightmareUtils.manageEclipseChance(this,2);

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
        } else if(par1DamageSource == DamageSource.generic && NightmareUtils.getIsMobEclipsed(this) && par1DamageSource.getSourceOfDamage() instanceof EntityPlayer target){
            this.teleportBehindTarget(target);
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int lootingLevel) {
        if(this.rand.nextInt(12) == 0 && WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()){
            this.dropItem(Item.enderPearl.itemID,1);
        }

        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0 && bKilledByPlayer) {
            int dropCount = this.rand.nextInt(3); // 0 - 2
            for (int i = 0; i < dropCount; ++i) {
                this.dropItem(bloodOrbID, 1);
            }
        }
        if (bKilledByPlayer && NightmareUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (lootingLevel * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = NMItems.charredFlesh.itemID;

            int var4 = this.rand.nextInt(3);
            if (lootingLevel > 0) {
                var4 += this.rand.nextInt(lootingLevel + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }
        }
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.26d);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(4.0 * NightmareUtils.getNiteMultiplier());
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
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(10 * NightmareUtils.getNiteMultiplier());
    }
    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {}

    @Override
    public float knockbackMagnitude() {
        return 0;
    }

    @Override
    protected void addRandomArmor() {
        if(NightmareUtils.getIsMobEclipsed(this)){
            if (this.rand.nextFloat() < 0.05f) {
                int iHeldType = this.rand.nextInt(3);
                if (iHeldType == 0) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.swordIron));
                } else {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.shovelIron));
                }
                this.equipmentDropChances[0] = 1f;
            }
        }
    }
    private void teleportBehindTarget(EntityPlayer targetPlayer) {
        // Get player's facing direction (yaw) and calculate the opposite direction
        float yaw = targetPlayer.rotationYaw + 180; // 180° to teleport behind
        double radians = Math.toRadians(yaw);

        // Random distance between 5 and 10 blocks
        double distance = 5 + this.rand.nextInt(6);

        // Calculate the position behind the player
        int xOffset = (int) (-Math.sin(radians) * distance);
        int zOffset = (int) (Math.cos(radians) * distance);

        int targetX = MathHelper.floor_double(targetPlayer.posX + xOffset);
        int targetY = MathHelper.floor_double(targetPlayer.posY);
        int targetZ = MathHelper.floor_double(targetPlayer.posZ + zOffset);

        // Ensure the teleport location is valid
        if (this.worldObj.getBlockId(targetX, targetY, targetZ) == 0 &&
                this.worldObj.getBlockId(targetX, targetY - 1, targetZ) != 0 &&
                this.worldObj.getBlockId(targetX, targetY + 1, targetZ) == 0) {
            this.setPositionAndUpdate(targetX, targetY, targetZ);
            this.playSound("mob.endermen.portal", 2.0F, 1.0F);
        }
    }

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

    @Override
    protected void attackEntity(Entity attackedEntity, float fDistanceToTarget) {
        if(NightmareUtils.getIsMobEclipsed(this) && attackedEntity instanceof EntityPlayer){
            ((EntityPlayer)attackedEntity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 60, 1));
            ((EntityPlayer)attackedEntity).addPotionEffect(new PotionEffect(Potion.weakness.id, 60, 0));
            ((EntityPlayer)attackedEntity).addPotionEffect(new PotionEffect(Potion.blindness.id, 60, 0));
        }
        super.attackEntity(attackedEntity, fDistanceToTarget);
    }
}
