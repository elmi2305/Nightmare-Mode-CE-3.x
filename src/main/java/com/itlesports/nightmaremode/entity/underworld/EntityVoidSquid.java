package com.itlesports.nightmaremode.entity.underworld;

import api.achievement.AchievementEventDispatcher;
import api.entity.ClosestEntitySelectionCriteria;
import api.entity.mob.possession.PossessionSource;
import api.inventory.InventoryUtils;
import api.world.BlockPos;
import api.world.WorldUtils;
import api.world.difficulty.DifficultyParam;
import btw.achievement.BTWAchievementEvents;
import btw.entity.mob.BTWSquidEntity;
import btw.item.BTWItems;
import btw.util.BTWSounds;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.util.interfaces.DamageSourceExt;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Iterator;
import java.util.List;

import static com.itlesports.nightmaremode.util.NMFields.HARDMODE;
import static com.itlesports.nightmaremode.util.NMFields.PREHARDMODE;

public class EntityVoidSquid extends BTWSquidEntity {
    public static final float BRIGHTNESS_AGGRESSION_THRESHOLD = 0.1f;
    private static final float SAFE_ATTACK_DEPTH = 0.5f;
    private static final int SAFE_ATTACK_DEPTH_TEST_MAXIMUM = 1;
    private static final float SAFE_PASSIVE_DEPTH = 3.0f;
    private static final int SAFE_PASSIVE_DEPTH_TEST_MAXIMUM = 4;
    private static final double AGGRESSION_RANGE = 16.0;
    private static final int CHANCE_TO_REEVALUATE_TARGET = 400;
    private static final int TENTACLE_ATTACK_TICKS_TO_COOLDOWN = 100;
    private static final double TENTACLE_ATTACK_RANGE = 6.0;
    public static final int TENTACLE_ATTACK_DURATION = 20;
    private static final double TENTACLE_ATTACK_TIP_COLLISION_WIDTH = 0.2;
    private static final double TENTACLE_ATTACK_TIP_COLLISION_HALF_WIDTH = 0.1;
    private int tentacleAttackCooldownTimer = 100;
    public int tentacleAttackInProgressCounter = -1;
    private double tentacleAttackTargetX = 0.0;
    private double tentacleAttackTargetY = 0.0;
    private double tentacleAttackTargetZ = 0.0;
    private int headCrabDamageCounter = 40;
    public float squidPitch = 0.0f;
    public float prevSquidPitch = 0.0f;
    public float squidYaw = 0.0f;
    public float prevSquidYaw = 0.0f;
    private float squidYawSpeed = 0.0f;
    public float tentacleAngle = 0.0f;
    public float prevTentacleAngle = 0.0f;
    private float tentacleAnimProgress = 0.0f;
    private float prevTentacleAnimProgress = 0.0f;
    private float tentacleAnimSpeed = 0.0f;
    private float randomMotionSpeed = 0.0f;
    private float randomMotionVecX = 0.0f;
    private float randomMotionVecY = 0.0f;
    private float randomMotionVecZ = 0.0f;
    private Entity entityToNotReCrab = null;
    private int reCrabEntityCountdown = 0;
    private static final int RE_CRAB_ENTITY_TICKS = 5;
    private static final float POSSESSED_LEAP_DEPTH = 0.5f;
    private static final int POSSESSED_LEAP_COUNTDOWN_DURATION = 200;
    private static final int POSSESSED_LEAP_PROPULSION_DURATION = 10;
    private int possessedLeapCountdown = 0;
    private int possessedLeapPropulsionCountdown = 0;
    private final float possessedLeapGhastConversionChance = 0.25f;
    private float possessedLeapGhastConversionDiceRoll = 1.0f;
    private static final int SQUID_POSSESSION_MAX_COUNT = 50;
    private int recentParryCount;
    private int calamariDropCountdown;
    private int squidOnHeadTimer;
    private boolean isValidForEventLoot;

    public EntityVoidSquid(World world) {
        super(world);
        this.setSize(0.95f, 0.95f);
        this.tentacleAnimSpeed = 1.0f / (this.rand.nextFloat() + 1.0f) * 0.2f;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        double hp = (NMUtils.getWorldProgress() > PREHARDMODE ? 20 * (NMUtils.getWorldProgress() + 1) : 25) * NMUtils.getBuffedSquidBonus() * NMUtils.getNiteMultiplier();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(hp);
    }

    @Override
    protected String getLivingSound() {
        return BTWSounds.SQUID_IDLE.sound();
    }

    @Override
    protected String getHurtSound() {
        return BTWSounds.SQUID_HURT.sound();
    }

    @Override
    protected String getDeathSound() {
        return BTWSounds.SQUID_DEATH.sound();
    }

    @Override
    protected float getSoundVolume() {
        return 0.4f;
    }

    @Override
    protected int getDropItemId() {
        return 0;
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        int iNumInkSacks = this.rand.nextInt(3 + iLootingModifier) + 1;
        for (int iTempInkSack = 0; iTempInkSack < iNumInkSacks; ++iTempInkSack) {
            this.entityDropItem(new ItemStack(Item.dyePowder, 1, 0), 0.0f);
        }
        if (this.rand.nextInt(4) - iLootingModifier <= 0) {
            this.dropItem(NMItems.voidSack.itemID, 1);
        }

        if (!bKilledByPlayer || !isValidForEventLoot) {
            return;
        }

        dropBloodOrbs(iLootingModifier);
        dropDarksunFragments(iLootingModifier);
        dropCalamari(iLootingModifier);
    }
    @Unique
    private void dropBloodOrbs(int lootingModifier) {
        int bloodOrbID = NMUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID == 0) {
            return;
        }

        int count = this.rand.nextInt(4) + 2;
        if (lootingModifier > 0) {
            count += this.rand.nextInt(lootingModifier + 1);
        }

        for (int i = 0; i < count; i++) {
            this.dropItem(bloodOrbID, 1);
        }
    }

    private void dropDarksunFragments(int lootingModifier) {
        if (!NMUtils.getIsEclipse()) {
            return;
        }

        for (int i = 0; i < (lootingModifier * 2) + 1; i++) {
            if (this.rand.nextInt(12) == 0) {
                this.dropItem(NMItems.darksunFragment.itemID, 1);
                if (this.rand.nextBoolean()) {
                    break;
                }
            }
        }
    }

    private void dropCalamari(int lootingModifier) {
        if (this.calamariDropCountdown <= 0) {
            return;
        }

        int worldProgress = this.worldObj != null ? NMUtils.getWorldProgress() : 0;
        for (int i = 0; i < (lootingModifier * 2) + 1; i++) {
            if (this.rand.nextInt(4 + worldProgress) == 0) {
                this.dropItem(NMItems.calamari.itemID, 1);
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.entityLivingOnLivingUpdate();
        this.calamariDropCountdown = Math.max(--this.calamariDropCountdown, 0);

        if (this.ticksExisted % 160 == 0) {
            this.recentParryCount = Math.max(recentParryCount - 1, 0);
        }
        this.prevSquidPitch = this.squidPitch;
        this.prevSquidYaw = this.squidYaw;
        this.prevTentacleAnimProgress = this.tentacleAnimProgress;
        this.prevTentacleAngle = this.tentacleAngle;
        this.updateTentacleAttack();
        if (!this.isEntityAlive()) {
            if (!this.worldObj.isRemote) {
                this.motionX = 0.0;

                this.motionY -= 0.08;
                this.motionY *= 0.98f;
                this.motionZ = 0.0;
            }
            return;
        }
        this.tentacleAnimProgress += this.tentacleAnimSpeed;
        if (this.tentacleAnimProgress > (float)Math.PI * 2) {
            this.tentacleAnimProgress -= (float)Math.PI * 2;
            if (this.rand.nextInt(10) == 0) {
                this.tentacleAnimSpeed = 1.0f / (this.rand.nextFloat() + 1.0f) * 0.2f;
            }
        }
        if (this.ridingEntity != null && !this.ridingEntity.isEntityAlive()) {
            this.mountEntity(null);
            if (!this.worldObj.isRemote) {
                this.worldObj.playAuxSFX(2226, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);
            }
        }
        if (!this.inWater && this.getAir() % 100 == 0 && (this.isPossessed() || this.isHeadCrab() || this.isBeingRainedOn())) {
            this.setAir(300);
        }
        if (this.isHeadCrab()) {
            this.updateHeadCrab();
            return;
        }
        if (this.isInWater()) {
            if (this.tentacleAnimProgress < (float)Math.PI) {
                float var1 = this.tentacleAnimProgress / (float)Math.PI;
                this.tentacleAngle = MathHelper.sin(var1 * var1 * (float)Math.PI) * (float)Math.PI * 0.25f;
                if ((double)var1 > 0.75) {
                    this.randomMotionSpeed = 1.0f;
                    this.squidYawSpeed = 1.0f;
                } else {
                    this.squidYawSpeed *= 0.8f;
                }
            } else {
                this.tentacleAngle = 0.0f;
                this.randomMotionSpeed *= 0.9f;
                this.squidYawSpeed *= 0.99f;
            }
            if (!this.worldObj.isRemote) {
                this.motionX = this.randomMotionVecX * this.randomMotionSpeed;
                this.motionY = this.randomMotionVecY * this.randomMotionSpeed;
                this.motionZ = this.randomMotionVecZ * this.randomMotionSpeed;
                if (this.possessedLeapPropulsionCountdown > 0) {
                    this.motionY = 1.0;
                }
            }
            if (this.possessedLeapPropulsionCountdown > 0) {
                --this.possessedLeapPropulsionCountdown;
            }
            if (this.tentacleAttackInProgressCounter >= 0) {
                this.orientToTentacleAttackPoint();
            } else if (this.entityToAttack != null) {
                this.orientToEntity(this.entityToAttack);
            } else {
                this.orientToMotion();
            }
        } else {
            this.possessedLeapPropulsionCountdown = 0;
            this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.tentacleAnimProgress)) * (float)Math.PI * 0.25f;
            if (!this.worldObj.isRemote) {
                this.motionX = 0.0;
                this.motionY -= 0.08;
                this.motionY *= 0.98f;
                this.motionZ = 0.0;
            }
            if (this.tentacleAttackInProgressCounter >= 0) {
                this.orientToTentacleAttackPoint();
            } else {
                this.squidPitch = this.motionY > 0.5 ? 0.0f : (float)((double)this.squidPitch + (double)(-90.0f - this.squidPitch) * 0.02);
            }
        }
    }

    @Override
    public void moveEntityWithHeading(float par1, float par2) {
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
    }

    @Override
    protected void fall(float par1) {}

    @Override
    protected void updateEntityActionState() {
        --this.tentacleAttackCooldownTimer;

        if (this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
            --this.tentacleAttackCooldownTimer;
            if (NMUtils.getWorldProgress() > HARDMODE) {
                this.tentacleAttackCooldownTimer -= (int)(2 * NMUtils.getBuffedSquidBonus());
            }
        }

        this.checkForHeadCrab();
        if (this.isHeadCrab()) {
            this.updateHeadCrabActionState();
            return;
        }

        int range = this.posY < 50 ? (this.posY < 10 ? 5 : 10) : (NMUtils.getIsEclipse() ? 16 : 24);

        if (this.entityToAttack == null) {
            Entity targetEntity;
            if ((targetEntity = this.findClosestValidAttackTargetWithinRange(range)) != null) {
                this.setTarget(targetEntity);
            }
        } else if (!this.entityToAttack.isValidOngoingAttackTargetForSquid()) {
            this.setTarget(null);
        } else if ((double)this.getDistanceToEntity(this.entityToAttack) > 16.0D) {
            this.setTarget(null);
        } else if (this.rand.nextInt(400) == 0) {
            this.setTarget(this.findClosestValidAttackTargetWithinRange(range));
        }

        if (this.entityToAttack != null) {
            double dDeltaX = this.entityToAttack.posX - this.posX;
            double dDeltaY = this.entityToAttack.posY + (double)this.entityToAttack.getEyeHeight() - (this.posY + (double)(this.height / 2.0f));
            double dDeltaZ = this.entityToAttack.posZ - this.posZ;
            double dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;

            if (dDistSqToTarget > 0.25D) {
                double dDistToTarget = MathHelper.sqrt_double(dDistSqToTarget);
                this.randomMotionVecX = (float)(dDeltaX / dDistToTarget * 0.4D);
                this.randomMotionVecY = (float)(dDeltaY / dDistToTarget * 0.4D);
                this.randomMotionVecZ = (float)(dDeltaZ / dDistToTarget * 0.4D);

                if (this.tentacleAttackInProgressCounter < 0 && this.tentacleAttackCooldownTimer <= 0 && this.rand.nextInt(20) == 0) {
                    this.attemptTentacleAttackOnTarget();
                }
            } else {
                this.randomMotionVecX = 0.0f;
                this.randomMotionVecY = 0.0f;
                this.randomMotionVecZ = 0.0f;
            }
        } else if (this.rand.nextInt(50) == 0 || this.randomMotionVecX == 0.0f && this.randomMotionVecY == 0.0f && this.randomMotionVecZ == 0.0f) {
            float fHeading = this.rand.nextFloat() * (float)Math.PI * 2.0f;
            this.randomMotionVecX = MathHelper.cos(fHeading) * 0.2f;
            this.randomMotionVecY = (this.rand.nextFloat() - 0.5f) * 0.2f;
            this.randomMotionVecZ = MathHelper.sin(fHeading) * 0.2f;
        }

        ++this.entityAge;
        this.despawnEntity();
    }

    @Override
    protected double minDistFromPlayerForDespawn() {
        return 64.0;
    }

    @Override
    protected boolean canDespawn() {
        return !this.isHeadCrab();
    }

    @Override
    public boolean getCanSpawnHere() {
        int targetY = this.worldObj.getPrecipitationHeight((int) this.posX, (int) this.posZ) + this.rand.nextInt(100);
        return this.rand.nextInt(12) == 0 && this.noPlayerNearby() && this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 16) == null && this.posY >= targetY;
    }
    private boolean noPlayerNearby() {
        return this.worldObj.checkNoEntityCollision(this.boundingBox) && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty();
    }
    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float iDamageAmount) {
        this.isValidForEventLoot = damageSource.getEntity() instanceof EntityPlayer;

        if (this.isHeadCrab()) {
            if (damageSource == DamageSource.inWall) {
                return false;
            }
        } else {
            if (this.isPossessed() && damageSource == DamageSource.fall) {
                return false;
            }
            if (!this.worldObj.isRemote) {
                if(damageSource.getSourceOfDamage() instanceof EntityPlayer){
                    this.calamariDropCountdown = 40;
                }
            }
            if (super.attackEntityFrom(damageSource, iDamageAmount)) {
                Entity attackingEntity;
                if (!this.worldObj.isRemote && (attackingEntity = damageSource.getEntity()) != null && attackingEntity != this) {
                    this.setTarget(attackingEntity);
                }
                return true;
            }
            return false;
        }
        return super.attackEntityFrom(damageSource, iDamageAmount);
    }

    @Override
    public void checkForScrollDrop() {}

    @Override
    public AxisAlignedBB getVisualBoundingBox() {
        if (this.tentacleAttackInProgressCounter >= 0) {
            double dExpandByAmount = 6.25;
            return this.boundingBox.expand(dExpandByAmount, dExpandByAmount, dExpandByAmount);
        }
        return this.boundingBox;
    }

    @Override
    public void setTarget(Entity targetEntity) {
        if (!this.worldObj.isRemote && targetEntity != this.entityToAttack) {
            this.entityToAttack = targetEntity;
            this.transmitAttackTargetToClients();
        } else {
            this.entityToAttack = targetEntity;
        }
    }

    @Override
    public boolean getCanCreatureTypeBePossessed() {
        return true;
    }

    @Override
    public boolean getCanCreatureBePossessedFromDistance(boolean bPersistentSpirit) {
        return (bPersistentSpirit || this.worldObj.getNumEntitiesThatApplyToSquidPossessionCap() < 50) && this.isEntityAlive() && !this.isPossessed();
    }

    @Override
    public boolean onPossessedRidingEntityDeath(PossessionSource<?> source) {
        if (this.isEntityAlive() && !this.isPossessed()) {
            this.initiatePossession(source);
            return true;
        }
        return false;
    }

    @Override
    public void initiatePossession(PossessionSource<?> source) {
        super.initiatePossession(source);
        this.setPersistent(true);
    }

    @Override
    protected void handlePossession() {
        super.handlePossession();
        if (this.possessedLeapCountdown > 0) {
            --this.possessedLeapCountdown;
        }
        if (!this.worldObj.isRemote && this.isFullyPossessed()) {
            if (this.ridingEntity == null && !this.inWater && !this.onGround) {
                if (this.possessedLeapGhastConversionDiceRoll <= 0.25f && this.motionY <= 0.0) {
                    EntityGhast ghast = new EntityGhast(this.worldObj);
                    ghast.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0f);
                    if (this.worldObj.checkNoEntityCollision(ghast.boundingBox, this) && this.worldObj.getCollidingBoundingBoxes(this, ghast.boundingBox).isEmpty() && !this.worldObj.isAnyLiquid(ghast.boundingBox)) {
                        this.worldObj.playAuxSFX(2273, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);
                        this.setDead();
                        ghast.setPersistent(true);
                        this.worldObj.spawnEntityInWorld(ghast);
                        AchievementEventDispatcher.triggerEventForNearbyPlayers(BTWAchievementEvents.SquidTransformsIntoGhastEvent.class, this.worldObj, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 32);
                    }
                }
            } else if (!this.inWater || this.motionY <= 0.0) {
                this.possessedLeapGhastConversionDiceRoll = 1.0f;
            }
        }
    }

    @Override
    public boolean doesEntityApplyToSquidPossessionCap() {
        return this.isEntityAlive() && this.isNoDespawnRequired();
    }

    @Override
    public boolean isValidZombieSecondaryTarget(EntityZombie zombie) {
        return !this.inWater && this.ridingEntity == null && zombie.riddenByEntity == null;
    }

    @Override
    public boolean attractsLightning() {
        return false;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.5f;
    }

    @Override
    public boolean doesNotDismountInWater() {
        return true;
    }

    private void updateHeadCrabActionState() {
        Entity sharedTarget = this.ridingEntity.getHeadCrabSharedAttackTarget();
        if (sharedTarget == this) {
            sharedTarget = null;
        }
        this.setTarget(sharedTarget);
        if (this.entityToAttack != null && this.tentacleAttackInProgressCounter < 0 && this.tentacleAttackCooldownTimer <= 0 && this.rand.nextInt(20) == 0) {
            this.attemptTentacleAttackOnTarget();
        }
        if (this.isFullyPossessed() && this.possessedLeapCountdown <= 0 && !this.inWater && this.rand.nextInt(100) == 0) {
            this.mountEntity(null);
            this.possessedLeap();
        }
    }

    private void orientToMotion() {
        float fMotionVectorFlatLength = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = this.renderYawOffset = this.interpolateAngle(this.renderYawOffset, -((float)Math.atan2(this.motionX, this.motionZ)) * 180.0f / (float)Math.PI, 1.0f);
        this.squidPitch += (-((float)Math.atan2(fMotionVectorFlatLength, this.motionY)) * 180.0f / (float)Math.PI - this.squidPitch) * 0.1f;
        this.squidYaw += (float)Math.PI * this.squidYawSpeed * 1.5f;
    }

    private void orientToEntity(Entity entity) {
        double dDeltaX = entity.posX - this.posX;
        double dDeltaY = entity.posY + (double)entity.getEyeHeight() - (this.posY + (double)(this.height / 2.0f));
        double dDeltaZ = entity.posZ - this.posZ;
        double dFlatDist = MathHelper.sqrt_double(dDeltaX * dDeltaX + dDeltaZ * dDeltaZ);
        this.rotationYaw = this.renderYawOffset = this.interpolateAngle(this.renderYawOffset, -((float)Math.atan2(dDeltaX, dDeltaZ)) * 180.0f / (float)Math.PI, 1.0f);
        this.squidPitch = this.interpolateAngle(this.squidPitch, -((float)(Math.atan2(dFlatDist, dDeltaY) * 180.0 / Math.PI)), 10.0f);
        this.squidYaw += (float)Math.PI * this.squidYawSpeed * 1.5f;
    }

    private void orientToTentacleAttackPoint() {
        double dDeltaX = this.tentacleAttackTargetX - this.posX;
        double dDeltaY = this.tentacleAttackTargetY - (this.posY + (double)(this.height / 2.0f));
        double dDeltaZ = this.tentacleAttackTargetZ - this.posZ;
        double dFlatDist = MathHelper.sqrt_double(dDeltaX * dDeltaX + dDeltaZ * dDeltaZ);
        this.rotationYaw = this.renderYawOffset = this.interpolateAngle(this.renderYawOffset, -((float)Math.atan2(dDeltaX, dDeltaZ)) * 180.0f / (float)Math.PI, 50.0f);
        this.squidPitch = this.interpolateAngle(this.squidPitch, -((float)(Math.atan2(dFlatDist, dDeltaY) * 180.0 / Math.PI - 150.0)), 50.0f);
        this.squidYaw = this.interpolateAngle(this.squidYaw, 0.0f, 50.0f);
    }

    private Entity findClosestValidAttackTargetWithinRange(double dRange) {
        Entity targetEntity = null;
        double dClosestDistSq = dRange * dRange;
        for (int iPlayerCount = 0; iPlayerCount < this.worldObj.playerEntities.size(); ++iPlayerCount) {
            double dDeltaZ;
            double dDeltaY;
            double dDeltaX;
            double dDistSq;
            EntityPlayer tempPlayer = (EntityPlayer)this.worldObj.playerEntities.get(iPlayerCount);
            if (
                    tempPlayer.capabilities.disableDamage
                            || !tempPlayer.isEntityAlive()
                            || !((dDistSq = (dDeltaX = tempPlayer.posX - this.posX) * dDeltaX + (dDeltaY = tempPlayer.posY - this.posY) * dDeltaY + (dDeltaZ = tempPlayer.posZ - this.posZ) * dDeltaZ)
                            < dClosestDistSq)) continue;
            targetEntity = tempPlayer;
            dClosestDistSq = dDistSq;
        }
        return targetEntity;
    }

    private void checkForHeadCrab() {
        if(this.entityToAttack != null && this.posY < this.entityToAttack.posY + this.entityToAttack.getEyeHeight() + 1 && (NMUtils.getIsMobEclipsed(this) || NMUtils.getBuffedSquidBonus() >= 2) && !this.entityToAttack.hasHeadCrabbedSquid() && !this.entityToAttack.isInWater()){
            this.motionY = 0.2f;
        }
        if (this.isEntityAlive()) {
            if (this.ridingEntity == null) {
                if (this.motionY < 0.5) {
                    if (this.reCrabEntityCountdown > 0) {
                        --this.reCrabEntityCountdown;
                    } else {
                        this.entityToNotReCrab = null;
                    }
                    EntityLivingBase target = this.getValidHeadCrabTargetInRange();
                    if (target != null) {
                        this.mountEntity(target);
                        this.playSound("mob.slime.attack", 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
                        this.headCrabDamageCounter = (int) (15 / NMUtils.getBuffedSquidBonus());
                        target.onHeadCrabbedBySquid(this);
                    }
                }
            } else {
                this.entityToNotReCrab = this.ridingEntity;
                this.reCrabEntityCountdown = 5;
            }
        }
    }

    @Override
    public boolean isInWater() {
        return true;
    }
    @Override
    public boolean isInsideOfMaterial(Material par1Material) {
        if(par1Material == Material.water){
            return true;
        }
        return super.isInsideOfMaterial(par1Material);
    }


    private EntityLivingBase getValidHeadCrabTargetInRange() {
        double dRange = 0.25;
        if (!this.isInWater()) {
            dRange = 0.5;
        }
        List<EntityLivingBase> entityList = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand(dRange, dRange, dRange));
        for (EntityLivingBase tempEntity : entityList) {
            if (!tempEntity.getCanBeHeadCrabbed(this.isInWater()) || tempEntity == this.entityToNotReCrab || !this.canEntityBeSeen(tempEntity)) continue;
            return tempEntity;
        }
        return null;
    }

    private void updateHeadCrab() {

        this.squidOnHeadTimer++;
        if (rand.nextInt(60) == 0) {
            this.playSound("mob.ghast.scream",0.3F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if(this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) && this.ridingEntity instanceof EntityPlayer player) {
            if (this.squidOnHeadTimer > 100 && !EntityBloodWither.isBossActive()) {
                player.addPotionEffect(new PotionEffect(Potion.blindness.id, 200, 0));
            }
            switch (NMUtils.getWorldProgress()) {
                case 1:
                    if (!player.isPotionActive(Potion.poison)) {
                        player.addPotionEffect(new PotionEffect(Potion.poison.id, (int) (120 * NMUtils.getBuffedSquidBonus()),0));
                    }
                    break;
                case 2,3:
                    if (!player.isPotionActive(Potion.wither)) {
                        player.addPotionEffect(new PotionEffect(Potion.wither.id, (int) (120 * NMUtils.getBuffedSquidBonus()),0));
                    }
                    break;
                default:
                    break;
            }
        }


        this.tentacleAnimSpeed = 0.2f;
        this.squidPitch = 0.0f;
        float fSinTentacle = MathHelper.sin(this.tentacleAnimProgress);
        this.tentacleAngle = MathHelper.abs(MathHelper.sin(fSinTentacle)) * (float)Math.PI * 0.25f;
        if (!this.worldObj.isRemote) {
            --this.headCrabDamageCounter;
            if (this.headCrabDamageCounter <= 0) {
                if (!this.ridingEntity.isImmuneToHeadCrabDamage()) {
                    DamageSource squidSource = DamageSource.causeMobDamage(this);
                    squidSource.setDamageBypassesArmor();
                    ((DamageSourceExt) squidSource).nightmareMode$setUnblockable(true);
                    ((DamageSourceExt) squidSource).nightmareMode$setHungerDrain(0.1f);
                    this.ridingEntity.attackEntityFrom(squidSource, 1.0f);
                }
                this.headCrabDamageCounter = (int) (11 / NMUtils.getBuffedSquidBonus());
            }
            if (this.ridingEntity.ridingEntity != null) {
                this.ridingEntity.mountEntity(this.ridingEntity.ridingEntity);
                if (this.ridingEntity.ridingEntity != null) {
                    this.ridingEntity.ridingEntity.riddenByEntity = null;
                    this.ridingEntity.ridingEntity = null;
                }
            }
        } else {
            float fPrevSinTentacle = MathHelper.sin(this.prevTentacleAnimProgress);
            if (fPrevSinTentacle <= 0.0f && fSinTentacle > 0.0f || fPrevSinTentacle > 0.0f && fSinTentacle <= 0.0f) {
                if (!this.ridingEntity.isImmuneToHeadCrabDamage()) {
                    this.worldObj.playSound(this.posX, this.posY, this.posZ, "random.eat", 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 0.8f);
                    this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.slime.big", 0.5f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 0.7f);
                } else {
                    this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.slime.big", 0.025f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 0.7f);
                }
            }
            if (this.ridingEntity instanceof EntityLivingBase ridingCreature) {
                this.squidYaw = -ridingCreature.rotationYawHead;
                this.prevSquidYaw = -ridingCreature.prevRotationYawHead;
                this.renderYawOffset = 0.0f;
                this.rotationYaw = 0.0f;
            }
        }
    }

    private void attemptTentacleAttackOnTarget() {
        if(this.entityToAttack.hasHeadCrabbedSquid()) return;

        double dDeltaX = this.entityToAttack.posX - this.posX;
        double dDeltaY = this.entityToAttack.posY + (double)(this.entityToAttack.height / 2.0f) - (this.posY + (double)(this.height / 2.0f));
        double dDeltaZ = this.entityToAttack.posZ - this.posZ;
        double dDistSqToTarget = dDeltaX * dDeltaX + dDeltaY * dDeltaY + dDeltaZ * dDeltaZ;
        double range = (NMUtils.getIsMobEclipsed(this) ? 144.0 : 81) * (NMUtils.getBuffedSquidBonus() * NMUtils.getBuffedSquidBonus());
        if (dDistSqToTarget < range) {
            double dDistToTarget = MathHelper.sqrt_double(dDistSqToTarget);
            double dUnitVectorToTargetX = dDeltaX / dDistToTarget;
            double dUnitVectorToTargetY = dDeltaY / dDistToTarget;
            double dUnitVectorToTargetZ = dDeltaZ / dDistToTarget;
            this.launchTentacleAttackInDirection(dUnitVectorToTargetX, dUnitVectorToTargetY, dUnitVectorToTargetZ);
        }
    }

    private void launchTentacleAttackInDirection(double dUnitVectorToTargetX, double dUnitVectorToTargetY, double dUnitVectorToTargetZ) {
        this.tentacleAttackInProgressCounter = 0;
        double multiplier = 9d;
        this.tentacleAttackCooldownTimer = 60 - (NMUtils.getWorldProgress() * 10);
        this.tentacleAttackTargetX = this.posX + dUnitVectorToTargetX * multiplier;
        this.tentacleAttackTargetY = this.posY + (double)(this.height / 2.0f) + dUnitVectorToTargetY * multiplier;
        this.tentacleAttackTargetZ = this.posZ + dUnitVectorToTargetZ * multiplier;
        this.transmitTentacleAttackToClients();
    }

    private void transmitTentacleAttackToClients() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeInt(this.entityId);
            dataStream.writeByte(1);
            dataStream.writeInt(MathHelper.floor_double(this.tentacleAttackTargetX * 32.0));
            dataStream.writeInt(MathHelper.floor_double(this.tentacleAttackTargetY * 32.0));
            dataStream.writeInt(MathHelper.floor_double(this.tentacleAttackTargetZ * 32.0));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        Packet250CustomPayload packet = new Packet250CustomPayload("btw|EV", byteStream.toByteArray());
        WorldUtils.sendPacketToAllPlayersTrackingEntity((WorldServer)this.worldObj, this, packet);
    }

    public void onClientNotifiedOfTentacleAttack(double dTargetX, double dTargetY, double dTargetZ) {
        this.tentacleAttackInProgressCounter = 0;
        this.tentacleAttackTargetX = dTargetX;
        this.tentacleAttackTargetY = dTargetY;
        this.tentacleAttackTargetZ = dTargetZ;
        this.worldObj.playSound(this.posX, this.posY, this.posZ, "random.bow", 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 0.5f);
        this.worldObj.playSound(this.posX, this.posY, this.posZ, "mob.slime.big", 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 0.5f);
        if (this.inWater) {
            int iParticleCount;
            for (iParticleCount = 0; iParticleCount < 150; ++iParticleCount) {
                this.worldObj.spawnParticle("bubble", this.posX + this.rand.nextDouble() * 2.0 - 1.0, this.posY + this.rand.nextDouble(), this.posZ + this.rand.nextDouble() * 2.0 - 1.0, 0.0, 0.0, 0.0);
            }
            for (iParticleCount = 0; iParticleCount < 10; ++iParticleCount) {
                this.worldObj.spawnParticle("splash", this.posX + this.rand.nextDouble() * 2.0 - 1.0, this.posY + (double)this.height, this.posZ + this.rand.nextDouble() * 2.0 - 1.0, 0.0, 0.0, 0.0);
            }
            this.worldObj.playSound(this.posX, this.posY, this.posZ, "liquid.splash", 1.0f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
        }
    }

    private void updateTentacleAttack() {
        if (this.tentacleAttackInProgressCounter >= 0) {
            ++this.tentacleAttackInProgressCounter;
            if (this.tentacleAttackInProgressCounter >= 20) {
                this.tentacleAttackInProgressCounter = -1;
            } else if (this.tentacleAttackInProgressCounter <= 10) {
                Vec3 tentacleTip = this.computeTentacleAttackTip(this.tentacleAttackInProgressCounter);
                AxisAlignedBB tipBox = AxisAlignedBB.getAABBPool().getAABB(tentacleTip.xCoord - 0.1, tentacleTip.yCoord - 0.1, tentacleTip.zCoord - 0.1, tentacleTip.xCoord + 0.1, tentacleTip.yCoord + 0.1, tentacleTip.zCoord + 0.1);
                List<EntityLivingBase> potentialCollisionList = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, tipBox);
                if (!potentialCollisionList.isEmpty()) {
                    for (EntityLivingBase tempEntity : potentialCollisionList) {
                        if (tempEntity instanceof BTWSquidEntity || tempEntity == this.ridingEntity) continue;
                        this.retractTentacleAttackOnCollision();
                        if (this.worldObj.isRemote) break;
                        this.teleportPlayer(tempEntity);
//                        this.tentacleAttackFlingTarget(tempEntity, true);
                        break;
                    }
                }
            }
        }
    }

    private void retractTentacleAttackOnCollision() {
        int iTurningPoint = 10;
        if (this.tentacleAttackInProgressCounter < iTurningPoint) {
            this.tentacleAttackInProgressCounter = iTurningPoint + (iTurningPoint - this.tentacleAttackInProgressCounter);
        }
    }

    public Vec3 computeTentacleAttackTip(float fAttackProgressTick) {
        double dAttackProgressSin = this.getAttackProgressSin(fAttackProgressTick);
        double dDeltaX = this.tentacleAttackTargetX - this.posX;
        double dDeltaY = this.tentacleAttackTargetY - (this.posY + (double)(this.height / 2.0f));
        double dDeltaZ = this.tentacleAttackTargetZ - this.posZ;
        double dTipOffsetX = dDeltaX * dAttackProgressSin;
        double dTipOffsetY = dDeltaY * dAttackProgressSin;
        double dTipOffsetZ = dDeltaZ * dAttackProgressSin;
        return Vec3.createVectorHelper(this.posX + dTipOffsetX, this.posY + (double)(this.height / 2.0f) + dTipOffsetY, this.posZ + dTipOffsetZ);
    }

    public double getAttackProgressSin(float fAttackProgressTick) {
        double dAttackProgress = fAttackProgressTick / 20.0f;
        return MathHelper.sin((float)(dAttackProgress * Math.PI));
    }
    private void teleportPlayer(EntityLivingBase tempEntity) {
        if (tempEntity instanceof EntityPlayer && ((EntityPlayer) tempEntity).isBlocking()) {
            Vec3 lookVec = tempEntity.getLookVec();
            Vec3 directionToSquid = Vec3.createVectorHelper(
                    this.posX - tempEntity.posX,
                    this.posY - (tempEntity.posY + tempEntity.getEyeHeight()),
                    this.posZ - tempEntity.posZ
            ).normalize();

            double dotProduct = lookVec.dotProduct(directionToSquid);

            if (dotProduct > 0.7) {
                ((EntityPlayer) tempEntity).setItemInUse(null, 0);
                tempEntity.getHeldItem().attemptDamageItem(this.rand.nextInt(6) + 2 + 2 * this.recentParryCount, this.rand);
                this.retractTentacleAttackOnCollision();
                this.recentParryCount += 1;
                return;
            }
        }

        if(!(tempEntity instanceof EntityPlayer)) {
            tempEntity.attackEntityFrom(DamageSource.generic, 5f);
            return;
        }
        if (tempEntity.isRiding()) {
            tempEntity.dismountEntity(tempEntity.ridingEntity);
            tempEntity.mountEntity(null);
        }
        tempEntity.setPositionAndUpdate(this.posX, this.posY, this.posZ);
        if (tempEntity instanceof EntityPlayer) {
            this.playSound("mob.endermen.portal", 2.0F, 1.0F);
        } else {
            this.worldObj.playSoundAtEntity(this, "mob.endermen.portal", 1.0F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    public boolean isHeadCrab() {
        return this.ridingEntity != null && this.ridingEntity instanceof EntityLivingBase;
    }

    private boolean isSurroundedByWater(int x, int y, int z) {
        int blockID = this.worldObj.getBlockId(x, y, z);
        if (!this.isWaterBlockID(blockID)) {
            return false;
        }
        for (int side = 0; side < 6; ++side) {
            blockID = this.worldObj.getBlockId(x + Facing.offsetsXForSide[side], y + Facing.offsetsYForSide[side], z + Facing.offsetsZForSide[side]);
            if (this.isWaterBlockID(blockID)) continue;
            return false;
        }
        return true;
    }

    private boolean isWaterBlockID(int blockID) {
        return blockID == Block.waterMoving.blockID || blockID == Block.waterStill.blockID;
    }

    public float getDepthBeneathSurface(float fMaxDepthToConsider) {
        float fDepth = -1.0f;
        int iPosI = MathHelper.floor_double(this.posX);
        int iPosJ = (int)this.posY;
        int iPosK = MathHelper.floor_double(this.posZ);
        int iTempBlockID = this.worldObj.getBlockId(iPosI, iPosJ, iPosK);
        int iTempBlockAboveID = this.worldObj.getBlockId(iPosI, iPosJ + 1, iPosK);
        if (iTempBlockID == Block.waterStill.blockID || iTempBlockID == Block.waterMoving.blockID || iTempBlockAboveID == Block.waterStill.blockID || iTempBlockAboveID == Block.waterMoving.blockID) {
            fDepth = 0.0f;
            fDepth = (float)((double)fDepth + (this.posY - (double)iPosJ));
            int iJOffset = 1;
            while (fDepth < fMaxDepthToConsider) {
                iTempBlockID = this.worldObj.getBlockId(iPosI, iPosJ + iJOffset, iPosK);
                if (iTempBlockID == Block.waterStill.blockID || iTempBlockID == Block.waterMoving.blockID) {
                    fDepth += 1.0f;
                } else {
                    if (fDepth != 0.0f || !(this.posY > 32.0)) break;
                    break;
                }
                ++iJOffset;
            }
        }
        return fDepth;
    }

    private float interpolateAngle(float fStartAngle, float fDestAngle, float fMaxIncrement) {
        float fDelta = MathHelper.wrapAngleTo180_float(fDestAngle - fStartAngle);
        if (fDelta > fMaxIncrement) {
            fDelta = fMaxIncrement;
        } else if (fDelta < -fMaxIncrement) {
            fDelta = -fMaxIncrement;
        }
        return fStartAngle + fDelta;
    }

    private void possessedLeap() {
        this.motionY = 1.0;
        this.isAirBorne = true;
        this.possessedLeapCountdown = 200;
        this.possessedLeapGhastConversionDiceRoll = this.rand.nextFloat();
        if (this.inWater) {
            this.possessedLeapPropulsionCountdown = 10;
            this.playSound("liquid.splash", 1.0f, this.rand.nextFloat() * 0.1f + 0.5f);
        } else {
            this.possessedLeapPropulsionCountdown = 0;
            this.playSound("mob.slime.big", 1.0f, this.rand.nextFloat() * 0.1f + 0.5f);
        }
    }

    private AxisAlignedBB getGhastConversionCollisionBoxFromPool() {
        double dWidthOffset = this.width / 16.0f;
        return AxisAlignedBB.getAABBPool().getAABB(this.boundingBox.minX + dWidthOffset, this.boundingBox.maxY, this.boundingBox.minZ + dWidthOffset, this.boundingBox.maxX - dWidthOffset, this.boundingBox.maxY + (double)0.1f, this.boundingBox.maxZ - dWidthOffset);
    }
}
