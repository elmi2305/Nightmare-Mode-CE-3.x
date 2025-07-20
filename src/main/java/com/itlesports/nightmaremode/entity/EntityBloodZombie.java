package com.itlesports.nightmaremode.entity;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.mob.behavior.SimpleWanderBehavior;
import com.itlesports.nightmaremode.AITasks.EntityAIChaseTargetSmart;
import com.itlesports.nightmaremode.AITasks.EntityAILiteHorde;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public class EntityBloodZombie extends EntityZombie {
    public EntityBloodZombie(World par1World) {
        super(par1World);
        this.tasks.removeAllTasksOfClass(EntityAIAttackOnCollide.class);
        this.tasks.removeAllTasksOfClass(EntityAIChaseTargetSmart.class);
        if (NightmareMode.hordeMode) {
            this.tasks.addTask(6, new EntityAIChaseTargetSmart(this, 1.0f));
        } else{
            this.tasks.addTask(6, new EntityAILiteHorde(this, 1.0f));
        }

        this.tasks.removeAllTasksOfClass(SimpleWanderBehavior.class);

        NightmareUtils.manageEclipseChance(this,8);
    }


    private boolean canBreakBlocks;


    @Override
    public boolean getCanSpawnHere() {
        int worldProgress = NightmareUtils.getWorldProgress();
        int moonPhase = this.worldObj.getMoonPhase();
        double y = this.posY;



        if (y < 40) {
            return false;
        }

        if (this.worldObj.isDaytime()) {
            return false;
        }
        EntityPlayer nearbyPlayer = null;
        double closestDistSq = 70 * 70;

        for (Object obj : this.worldObj.playerEntities) {
            if (!(obj instanceof EntityPlayer player)) continue;

            double dx = player.posX - this.posX;
            double dz = player.posZ - this.posZ;
            double distSq = dx * dx + dz * dz;

            if (distSq <= closestDistSq) {
                closestDistSq = distSq;
                nearbyPlayer = player;
            }
        }

        if (nearbyPlayer == null) {
            return false;
        }

        double chance = 0;
        if (moonPhase == 4) {
            chance = 0.7 + worldProgress * 0.1;
        }
        boolean lowYRange = y >= 40 && y <= 60;
        if (lowYRange) {
            chance *= 0.15; // 85% reduction
        }


        return this.rand.nextDouble() < chance && super.getCanSpawnHere();
    }

    @Override
    protected void checkForCatchFireInSun() {}

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int lootingLevel) {
        if(this.rand.nextInt(16) == 0 && bKilledByPlayer){
            this.dropItem(Item.porkRaw.itemID,1);
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

            int itemID = NMItems.decayedFlesh.itemID;

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

        if(!this.hasAttackTarget()){
            EntityPlayer player = this.worldObj.getClosestPlayerToEntity(this,120);
            if(player != null && !player.capabilities.isCreativeMode){
                this.setAttackTarget(player);
                this.getMoveHelper().setMoveTo(player.posX,player.posY,player.posZ,1.2f);
            }
        }

        super.onLivingUpdate();
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.39f);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((26 + NightmareUtils.getWorldProgress() * 6) * NightmareUtils.getNiteMultiplier());
    }

    @Override
    protected void addRandomArmor() {
        if (this.rand.nextFloat() < 0.03f && this.worldObj.getWorldTime() > 120000) {
            int iHeldType = this.rand.nextInt(3);
            if (iHeldType == 0) {
                this.setCurrentItemOrArmor(0, new ItemStack(Item.swordIron));
            } else {
                this.setCurrentItemOrArmor(0, new ItemStack(Item.shovelIron));
            }
            this.equipmentDropChances[0] = 1f;
        }
    }


    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        if (this.worldObj.isRemote && this.hurtResistantTime == 0) {
            for(int i = 0; i < Math.min(par2 - 1, 3); i++) {
                EntityXPOrb tempXPOrb = new EntityXPOrb(this.worldObj, this.posX, this.posY + this.height - this.rand.nextFloat(), this.posZ, 2, true);
                tempXPOrb.motionX = this.rand.nextFloat() * 0.1 * (this.rand.nextBoolean() ? -1 : 1);
                tempXPOrb.motionY = this.rand.nextFloat() * 0.2;
                tempXPOrb.motionZ = this.rand.nextFloat() * 0.1 * (this.rand.nextBoolean() ? -1 : 1);

                this.worldObj.spawnEntityInWorld(tempXPOrb);
            }
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    public boolean attackEntityAsMob(Entity attackedEntity) {
        return super.attackEntityAsMob(attackedEntity);
    }

    public boolean canBreakBlocks() {
        return canBreakBlocks;
    }

    public void setCanBreakBlocks(boolean canBreakBlocks) {
        this.canBreakBlocks = canBreakBlocks;
    }
}
