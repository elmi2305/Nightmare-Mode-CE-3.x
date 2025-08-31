package com.itlesports.nightmaremode.entity;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.AITasks.EntityAIFollowPlayerIfWatched;
import net.minecraft.src.*;

import java.util.List;

public class EntityFauxVillager extends EntityMob {
    private int angerTicks = 0;
    public EntityPlayer followTarget;
    public static final int MAX_ANGER = 1500;
    private static final int FOLLOW_ANGER_THRESHOLD = 500;

    public EntityFauxVillager(World par1World) {
        super(par1World);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAIMoveTowardsRestriction(this, 0.6));
        this.tasks.addTask(4, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0f, 1.0f));
        this.tasks.addTask(5, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f));
        this.tasks.addTask(10, new EntityAIFollowPlayerIfWatched(this));
        this.experienceValue = 50;
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.worldObj.isDaytime() && this.worldObj.canBlockSeeTheSky((int) this.posX, (int) this.posY + 1, (int) this.posZ) && super.getCanSpawnHere() && this.rand.nextInt(8) == 0;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setAttribute(30);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(20);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.45);
    }

    protected boolean isPlayerStaringAtMe(EntityPlayer player) {
        Vec3 vLook = player.getLook(1.0f).normalize();
        Vec3 vDelta = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX - player.posX, this.posY + (double)this.getEyeHeight() - (player.posY + (double)player.getEyeHeight()), this.posZ - player.posZ);
        double dDist = vDelta.lengthVector();
        double dotDelta = vLook.dotProduct(vDelta.normalize());
        if (dotDelta > 1.0 - 0.1 / dDist) {
            return player.canEntityBeSeen(this);
        }
        return false;
    }

    @Override
    protected Entity findPlayerToAttack() {
        return null;
    }
    @Override
    protected void attackEntity(Entity par1Entity, float par2) {}

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        this.angerTicks += (int) (400f * par2);
        if (par2 > 7.0F) {
            float capped = Math.min(par2, 15.0F);
            float chance = (capped - 7.0F) / 8;

            if ((this.rand.nextFloat() * 1.2f) < chance) {
                par2 = 20;
            } else{
                par2 /= 3;
            }
        }

        // Default damage behavior
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    protected void updateAITasks() {
        // look for the nearest player within 30 blocks
        EntityPlayer nearest = worldObj.getClosestVulnerablePlayerToEntity(this, 30.0D);

        if (nearest != null) {
            double dist = this.getDistanceToEntity(nearest);

            // 1) Anger buildup if close, otherwise decay
            if (dist < 12) {
                float factor = 1.0F - (float)(dist / 12.0); // 0.0 to 1.0
                int increase = Math.round(1.5F + 4.0F * factor); // Between ~1.5 to ~5.5
                this.angerTicks = Math.min(this.angerTicks + increase, MAX_ANGER);
            } else {
                this.angerTicks = Math.max(this.angerTicks - 1, 0);
            }
            if (this.isPlayerStaringAtMe(nearest)) {
                this.angerTicks = Math.min(this.angerTicks + 3, MAX_ANGER);
            }

            // 2) Start following once angry enough and not too close
            if (this.angerTicks >= FOLLOW_ANGER_THRESHOLD && dist > 1.0D) {
                this.followTarget = nearest;
            } else {
                this.followTarget = null;
            }

            // 3) Final transformation when max anger reached
            if (this.angerTicks >= MAX_ANGER) {
                this.transformToEnemy(this.followTarget);
                this.transformNearbyVillagers(this.followTarget);
            }

        } else {
            // No player around → cool off and stop following
            this.angerTicks = Math.max(angerTicks - 1, 0);
            this.followTarget = null;
        }
        super.updateAITasks();
    }

    @Override
    protected boolean isAIEnabled() {
        return true;
    }

    @Override
    public void entityMobOnLivingUpdate() {
            // Head twitch: sudden snap if angry

        if (this.angerTicks > (MAX_ANGER - 300) && this.rand.nextInt(40) == 0) {
            float twitchYaw = (this.rand.nextFloat() - 0.5F) * 160F;   // -80 to +80
            float twitchPitch = (this.rand.nextFloat() - 0.5F) * 60F;  // -30 to +30

            this.rotationYawHead += twitchYaw;
            this.renderYawOffset += twitchYaw * 0.5F;
            this.rotationPitch += twitchPitch;
        }
        super.entityMobOnLivingUpdate();
    }

    @Override
    protected boolean interact(EntityPlayer par1EntityPlayer) {
        if (!par1EntityPlayer.capabilities.isCreativeMode) {
            this.transformToEnemy(par1EntityPlayer);
            this.transformNearbyVillagers(par1EntityPlayer);
        }
        return false;
    }
    public void transformNearbyVillagers(EntityPlayer target) {
        double radius = 8d;
        AxisAlignedBB area = AxisAlignedBB.getBoundingBox(
                this.posX - radius, this.posY - radius, this.posZ - radius,
                this.posX + radius, this.posY + radius, this.posZ + radius
        );
        List nearby = this.worldObj.getEntitiesWithinAABB(EntityFauxVillager.class, area);

        for (Object obj : nearby) {
            if (obj == this) continue;
            if (obj instanceof EntityFauxVillager villager) {
                villager.transformToEnemy(target);
            }
        }
    }


    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        if(bKilledByPlayer){
            this.dropItem(BTWItems.tannedLeather.itemID, 1);
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        if(this.rand.nextBoolean()) {
            boolean bKilledByPlayer = source.getSourceOfDamage() instanceof EntityPlayer;
            if (bKilledByPlayer && !this.worldObj.isRemote) {
                int iTempCount;
                int numMeat = this.rand.nextInt(2);
                for (iTempCount = 0; iTempCount < numMeat; ++iTempCount) {
                    this.dropItem(BTWItems.rawMysteryMeat.itemID, 1);
                }
            }
        }

        super.onDeath(source);
    }


    public void transformToEnemy(EntityPlayer target){
        for (int i = 0; i < 40; i++) {
            double offsetX = (this.rand.nextDouble() - 0.5D) * 3.0D;
            double offsetY = this.rand.nextDouble() * this.height * 1.2D;
            double offsetZ = (this.rand.nextDouble() - 0.5D) * 3.0D;

            this.worldObj.playAuxSFX(2278, (int) (this.posX + offsetX), (int) (this.posY + offsetY), (int) (this.posZ + offsetZ), 0);
        }

        if (!this.worldObj.isRemote) {
            EntityZombieImposter zombie = new EntityZombieImposter(this.worldObj);
            zombie.copyLocationAndAnglesFrom(this);
            zombie.setAttackTarget(target);
            zombie.setCurrentItemOrArmor(0, new ItemStack(Item.axeIron));
            zombie.setEquipmentDropChance(0,0f);
            this.worldObj.spawnEntityInWorld(zombie);
        }
        this.setDead();
    }

    @Override
    protected String getLivingSound() {
        return this.rand.nextBoolean() ? "mob.villager.haggle" : "mob.villager.idle";
    }
    @Override
    protected String getHurtSound() {
        return "mob.villager.hit";
    }
    @Override
    protected String getDeathSound() {
        return "mob.villager.death";
    }

    @Override
    protected boolean isValidLightLevel() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);

        int skyLight = this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
        if (skyLight <= this.rand.nextInt(32)) {
            return false;
        }

        // Reject if ANY artificial light is present
        int blockLightValue = this.worldObj.getBlockLightValueNoSky(x, y, z);
        if (blockLightValue > 0) {
            return false;
        }

        int naturalLightValue = this.worldObj.getBlockNaturalLightValue(x, y, z);
        if (this.worldObj.isThundering()) {
            naturalLightValue = Math.min(naturalLightValue, 5);
        }

        // Require good natural light too (e.g., not shaded or thunder-darkened)
        return naturalLightValue > this.rand.nextInt(8);
    }
    public int getAngerTicks() {
        return this.angerTicks;
    }
}
