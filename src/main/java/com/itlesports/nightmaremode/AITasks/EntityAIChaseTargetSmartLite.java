package com.itlesports.nightmaremode.AITasks;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;

import java.util.Set;

public class EntityAIChaseTargetSmartLite extends EntityAIBase {
    // a lite version of the horde mode AI used by blood zombies

    private final EntityCreature taskOwner;
    private final double moveSpeed;
    private EntityLivingBase targetEntity;
    private int repathDelay;
    private int attackTick;

    public EntityAIChaseTargetSmartLite(EntityCreature creature, double speed) {
        this.taskOwner = creature;
        this.moveSpeed = speed;
        this.setMutexBits(3);
    }
    // near your other fields:
    private double lastPosX, lastPosZ;
    private int    stuckInPlaceTicks;

    // tuning constant:
    private static final int STUCK_IN_PLACE_THRESHOLD = 10;


    @Override
    public boolean shouldExecute() {
        EntityLivingBase foundTarget = this.taskOwner.getAttackTarget();
        if (foundTarget != null && foundTarget.isEntityAlive()) {
            if (foundTarget instanceof EntityPlayer &&
                    ((EntityPlayer) foundTarget).capabilities.isCreativeMode) {
                return false;
            }
            this.targetEntity = foundTarget;
            return true;
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        return this.targetEntity != null && this.targetEntity.isEntityAlive();
    }

    @Override
    public void resetTask() {
        this.targetEntity = null;
        this.repathDelay  = 0;
        this.attackTick   = 0;
    }

    // tuning constants
    private static final double MAX_RANGE_SQ            = 32.0D * 32.0D;
    private static final int    CLOSE_REPATH_BASE       = 10;  // base ticks between expensive path calls
    private static final int    CLOSE_REPATH_VARIANCE   = 15;  // +0–14 randomness
    private static final double VERY_CLOSE_RANGE_SQ     = 10.0D * 10.0D;
    private static final int    VERY_CLOSE_REPATH_MIN   = 2;   // min ticks when very close
    private static final int    VERY_CLOSE_REPATH_VAR   = 2;   // +0–2 randomness

    @Override
    public void updateTask() {
        // 1) Validate
        if (this.targetEntity == null || !this.targetEntity.isEntityAlive()) {
            return;
        }
        this.taskOwner.getLookHelper()
                .setLookPositionWithEntity(this.targetEntity, 30.0F, 30.0F);


        // 2) Compute squared horizontal distance & melee
        double dx      = this.targetEntity.posX - this.taskOwner.posX;
        double dz      = this.targetEntity.posZ - this.taskOwner.posZ;
        double horizSq = dx*dx + dz*dz;
        performExtendedMeleeAttack();

        // 3) Gate by repathDelay
        if (--this.repathDelay > 0) {
            checkStuckAndJump();
            return;
        }

        // 4) FAR: cheap follow
        if (horizSq > MAX_RANGE_SQ) {
            this.taskOwner.getMoveHelper().setMoveTo(
                    this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ, 1.4f
            );
            this.repathDelay = CLOSE_REPATH_BASE + this.taskOwner.getRNG().nextInt(CLOSE_REPATH_VARIANCE);
        }
        // 5) CLOSE: navigator + one‑off fallback
        else {
            boolean success = this.taskOwner.getNavigator()
                    .tryMoveToEntityLiving(this.targetEntity, this.moveSpeed);

            if (!success) {
                this.taskOwner.getMoveHelper().setMoveTo(
                        this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ, this.moveSpeed
                );
                this.repathDelay = CLOSE_REPATH_BASE + this.taskOwner.getRNG().nextInt(CLOSE_REPATH_VARIANCE);
            } else {
                if (horizSq <= VERY_CLOSE_RANGE_SQ) {
                    this.repathDelay = VERY_CLOSE_REPATH_MIN + this.taskOwner.getRNG().nextInt(VERY_CLOSE_REPATH_VAR);
                } else {
                    this.repathDelay = CLOSE_REPATH_BASE + this.taskOwner.getRNG().nextInt(CLOSE_REPATH_VARIANCE);
                }
            }
        }

        // 6) After moving, check for stuckness
        checkStuckAndJump();
    }

    /** If the zombie hasn’t moved in X/Z for STUCK_IN_PLACE_THRESHOLD ticks, make it jump once. */
    private void checkStuckAndJump() {
        boolean barelyMoved = Math.abs(this.taskOwner.posX - this.lastPosX) < 0.01
                && Math.abs(this.taskOwner.posZ - this.lastPosZ) < 0.01;

        if (barelyMoved && this.taskOwner.onGround) {
            if (++this.stuckInPlaceTicks > STUCK_IN_PLACE_THRESHOLD) {
                this.taskOwner.jump();
                this.taskOwner.isAirBorne = true;
                this.stuckInPlaceTicks = 0;
            }
        } else {
            this.stuckInPlaceTicks = 0;
        }

        // update last positions
        this.lastPosX = this.taskOwner.posX;
        this.lastPosZ = this.taskOwner.posZ;
    }


    private void performExtendedMeleeAttack() {
        if (this.targetEntity == null) return;

        if (this.taskOwner.worldObj.getDifficulty() != Difficulties.HOSTILE) return;

        double distanceSq = this.taskOwner.getDistanceSqToEntity(this.targetEntity);
        int attackRange = computeRangeForHeldItem(this.taskOwner.getHeldItem());

        if (distanceSq < attackRange && this.attackTick <= 1) {
            if (!this.taskOwner.canEntityBeSeen(this.targetEntity)) return;
            this.taskOwner.swingItem();
            this.taskOwner.attackEntityAsMob(this.targetEntity);
            this.attackTick = 13 - NightmareUtils.getWorldProgress(this.taskOwner.worldObj) * 2;
        }

        if (NightmareUtils.getIsMobEclipsed(this.taskOwner) && distanceSq < 3) {
            this.taskOwner.swingItem();
            this.taskOwner.attackEntityAsMob(this.targetEntity);
            this.attackTick = 20;
        }
    }


    private int computeRangeForHeldItem(ItemStack heldItem) {
        if (heldItem == null) return NightmareMode.isAprilFools ? 7 : 2;

        int id = heldItem.itemID;
        if (getLongRangeItems().contains(id)) {
            return getLesserRangeItems().contains(id) ? 5 : 10;
        }
        return NightmareMode.isAprilFools ? 7 : 2;
    }

    private Set<Integer> getLongRangeItems() {
        return NightmareUtils.LONG_RANGE_ITEMS;
    }

    private Set<Integer> getLesserRangeItems() {
        return NightmareUtils.LESSER_RANGE_ITEMS;
    }
}
