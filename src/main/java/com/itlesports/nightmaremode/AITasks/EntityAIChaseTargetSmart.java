package com.itlesports.nightmaremode.AITasks;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.RottenArrowEntity;
import btw.item.BTWItems;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;

import java.util.List;
import java.util.Set;

public class EntityAIChaseTargetSmart extends EntityAIBase {
    private final EntityCreature taskOwner;
    private final double moveSpeed;
    private EntityLivingBase targetEntity;
    private int stuckCounter;
    private int repathDelay;
    private int attackTick;
    private double lastPosX, lastPosZ;
    private int stuckInPlaceTicks = 0;
    private int stuckInPlaceInstances = 0;
    private static final int STUCK_IN_PLACE_THRESHOLD = 10;


    private static final int MAX_PATHFINDING_RANGE = 32;
    private static final int STUCK_THRESHOLD = 60;

    public EntityAIChaseTargetSmart(EntityCreature creature, double speed) {
        this.taskOwner = creature;
        this.moveSpeed = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.taskOwner.getAttackTarget();
        if (target == null || !target.isEntityAlive()) return false;

        double distanceSq = this.taskOwner.getDistanceSqToEntity(target);
        if (distanceSq > MAX_PATHFINDING_RANGE * MAX_PATHFINDING_RANGE) {
            this.targetEntity = target;
            return true;
        }

        this.taskOwner.getNavigator().tryMoveToEntityLiving(target, this.moveSpeed);
        this.targetEntity = target;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return this.targetEntity != null && this.targetEntity.isEntityAlive();
    }

    @Override
    public void resetTask() {
        this.targetEntity = null;
        this.stuckCounter = 0;
        this.repathDelay = 0;
        this.attackTick = 0;
    }

    @Override
    public void updateTask() {
        if (this.targetEntity == null) return;

        this.taskOwner.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0F, 30.0F);

        double dx = this.targetEntity.posX - this.taskOwner.posX;
        double dz = this.targetEntity.posZ - this.taskOwner.posZ;
        double dy = this.targetEntity.posY - this.taskOwner.posY;
        double distanceSq = dx * dx + dz * dz + dy * dy;

        if (distanceSq > MAX_PATHFINDING_RANGE * MAX_PATHFINDING_RANGE * MAX_PATHFINDING_RANGE) {
            this.taskOwner.getMoveHelper().setMoveTo(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ, this.moveSpeed);
        } else {
            if (--this.repathDelay <= 0) {
                this.taskOwner.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.moveSpeed);
                this.repathDelay = 30 + this.taskOwner.getRNG().nextInt(15);
            }
        }

// Handle stuck pathing
        if (this.taskOwner.getNavigator().noPath()) {
            if (++this.stuckCounter > STUCK_THRESHOLD) {
                this.taskOwner.getMoveHelper().setMoveTo(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ, this.moveSpeed);
                this.repathDelay = 40;
            }
        } else {
            this.stuckCounter = 0;
        }

// Tick down cooldowns
        this.attackTick = Math.max(this.attackTick - 1, 0);

// Handle stuck-in-place detection
        boolean barelyMoved = Math.abs(this.taskOwner.posX - this.lastPosX) < 0.01 && Math.abs(this.taskOwner.posZ - this.lastPosZ) < 0.01;
        if (barelyMoved) {
            this.stuckInPlaceTicks++;

            if (this.stuckInPlaceTicks > STUCK_IN_PLACE_THRESHOLD && this.taskOwner.onGround) {
                this.stuckInPlaceInstances++;

                // Alternate between jumping and recalculating path
                if ((this.stuckInPlaceInstances % 3) == 0) {
                    // Every third time: try to recalculate the path
                    this.taskOwner.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.moveSpeed); // expensive pathing operation
                    this.repathDelay = 40;
                } else {
                    // Otherwise: jump
                    this.taskOwner.jump();
                    this.taskOwner.isAirBorne = true;
                }

                // Reset tick counter so we don’t spam either action
                this.stuckInPlaceTicks = 0;
            }
        } else {
            this.stuckInPlaceTicks = 0;
            this.lastPosX = this.taskOwner.posX;
            this.lastPosZ = this.taskOwner.posZ;
        }

// Failsafe for stuck mobs
        if (this.stuckInPlaceInstances > 6 && this.taskOwner instanceof EntityCreeper) {
            this.taskOwner.onKickedByAnimal(null);
        }



        performExtendedMeleeAttack();
        // === Custom Logic End ===
    }

    private void performExtendedMeleeAttack() {
        if (this.targetEntity == null) return;

        if (this.taskOwner.worldObj.getDifficulty() != Difficulties.HOSTILE) return;
        if (!this.taskOwner.canEntityBeSeen(this.targetEntity)) return;

        double distanceSq = this.taskOwner.getDistanceSqToEntity(this.targetEntity);
        int attackRange = computeRangeForHeldItem(this.taskOwner.getHeldItem());

        if (distanceSq < attackRange && this.attackTick <= 1) {
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

    private boolean isPlayerHoldingBow(EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        if (held == null) return false;

        int id = held.itemID;
        return id == Item.bow.itemID || id == BTWItems.compositeBow.itemID;
    }

    private boolean isHoldingLongRangeItem(EntityLiving entity) {
        ItemStack held = entity.getHeldItem();
        return held != null && getLongRangeItems().contains(held.itemID);
    }

    // You should define or import these as needed
    private Set<Integer> getLongRangeItems() {
        return NightmareUtils.LONG_RANGE_ITEMS;
    }

    private Set<Integer> getLesserRangeItems() {
        return NightmareUtils.LESSER_RANGE_ITEMS;
    }
}


