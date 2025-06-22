package com.itlesports.nightmaremode.AITasks;

import btw.community.nightmaremode.NightmareMode;
import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;

import java.util.Set;

public class EntityAIChaseTargetSmart extends EntityAIBase {
    private final EntityCreature taskOwner;
    private final double moveSpeed;
    private EntityLivingBase targetEntity;
    private int repathDelay;
    private int attackTick;


    public EntityAIChaseTargetSmart(EntityCreature creature, double speed) {
        this.taskOwner = creature;
        this.moveSpeed = speed;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase foundTarget = this.taskOwner.getAttackTarget();
        if (foundTarget != null && foundTarget.isEntityAlive()) {
            if(foundTarget instanceof EntityPlayer && ((EntityPlayer) foundTarget).capabilities.isCreativeMode){
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
        this.repathDelay = 0;
        this.attackTick = 0;
    }


    // state:
    private boolean lastNavSuccess    = true;  // assume “good” at start


    // tuning constants
    private static final double   MAX_RANGE_SQ              = 32.0D * 32.0D;
    private static final double   VERTICAL_UNREACHABLE_Y    = 4.0D;           // any vertical gap > this is unreachable
    private static final int      CLOSE_REPATH_INTERVAL     = 8;             // ticks between nav when healthy & close
    private static final int      BROKEN_PROBE_BASE         = 30;             // min ticks before retrying nav/fallback
    private static final int      BROKEN_PROBE_VARIANCE     = 20;             // + rand(0..39) to BROKEN_PROBE_BASE
    private static final double   STALE_THRESHOLD_SQ        = 4.0D * 4.0D;  // 4-block threshold



    @Override
    public void updateTask() {
        // 1) Validate target
        if (this.targetEntity == null || !this.targetEntity.isEntityAlive()) {
            return;
        }


        // 2) Compute vector to player
        double dx    = this.targetEntity.posX - this.taskOwner.posX;
        double dy    = this.targetEntity.posY - this.taskOwner.posY;
        double dz    = this.targetEntity.posZ - this.taskOwner.posZ;
        double horizSq = dx*dx + dz*dz;
        double vert   = Math.abs(dy);
        this.performExtendedMeleeAttack();

        // 3) Detect stale navigator path
        if (!this.taskOwner.getNavigator().noPath()) {
            PathEntity path = this.taskOwner.getNavigator().getPath();
            PathPoint end = path.getFinalPathPoint();

            double ex = end.xCoord + 0.5 - this.targetEntity.posX; // X diff
            double ey = end.yCoord      - this.targetEntity.posY; // Y diff (no center adjust for Y)
            double ez = end.zCoord + 0.5 - this.targetEntity.posZ; // Z diff

            double distSq = ex * ex + ey * ey + ez * ez;

            if (distSq > STALE_THRESHOLD_SQ) {
                // Path is too far off — cancel and fallback
                this.taskOwner.getNavigator().clearPathEntity();
                this.lastNavSuccess = false;
                this.repathDelay = 0;
            }
        }


        // 4) Gate further logic behind repathDelay
        if (--this.repathDelay > 0) {
            return;
        }

        // 5) Vertical unreachable?
        if (vert > VERTICAL_UNREACHABLE_Y) {
            applyFallbackMotion(dx, dz);
            scheduleBrokenProbe();
            return;
        }

        // 6) Horizontal far?
        if (horizSq > MAX_RANGE_SQ) {
            this.lastNavSuccess = false;
            applyFallbackMotion(dx, dz);
            scheduleBrokenProbe();
            return;
        }

        // 7) Navigator broken → fallback + probe
        if (!this.lastNavSuccess) {
            applyFallbackMotion(dx, dz);
            boolean success = this.taskOwner.getNavigator()
                    .tryMoveToEntityLiving(this.targetEntity, this.moveSpeed);
            this.lastNavSuccess = success;
            scheduleBrokenProbe();
            return;
        }

        // 8) Navigator healthy & close enough → run A*
        double horiz = Math.sqrt(horizSq);
        int    interval = (horiz <= 8.0D)
                ? 2 + this.taskOwner.getRNG().nextInt(3)
                : CLOSE_REPATH_INTERVAL;

        boolean success = this.taskOwner.getNavigator()
                .tryMoveToEntityLiving(this.targetEntity, this.moveSpeed);
        this.lastNavSuccess = success;
        this.repathDelay    = success ? interval : (BROKEN_PROBE_BASE + this.taskOwner.getRNG().nextInt(BROKEN_PROBE_VARIANCE));
    }

    /** Applies cheap motion without raycasts. */
    private void applyFallbackMotion(double dx, double dz) {
        Vec3 dir = Vec3.createVectorHelper(dx, 0.0, dz).normalize();
        this.taskOwner.motionX += dir.xCoord * 0.1;
        this.taskOwner.motionZ += dir.zCoord * 0.1;
    }

    /** Schedule next broken‐nav probe sooner. */
    private void scheduleBrokenProbe() {
        this.repathDelay = BROKEN_PROBE_BASE + this.taskOwner.getRNG().nextInt(BROKEN_PROBE_VARIANCE);
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

    // You should define or import these as needed
    private Set<Integer> getLongRangeItems() {
        return NightmareUtils.LONG_RANGE_ITEMS;
    }

    private Set<Integer> getLesserRangeItems() {
        return NightmareUtils.LESSER_RANGE_ITEMS;
    }
}


