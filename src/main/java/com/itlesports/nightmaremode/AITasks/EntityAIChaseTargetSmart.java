package com.itlesports.nightmaremode.AITasks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.elements.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMUtils;
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

    private boolean lastNavSuccess    = true;

    private static final double   MAX_RANGE_SQ              = 32.0D * 32.0D;
    private static final double   VERTICAL_UNREACHABLE_Y    = 4.0D;
    private static final int      CLOSE_REPATH_INTERVAL     = 8;
    private static final int      BROKEN_PROBE_BASE         = 30;
    private static final int      BROKEN_PROBE_VARIANCE     = 20;
    private static final double   STALE_THRESHOLD_SQ        = 4.0D * 4.0D;

    @Override
    public void updateTask() {

        if (this.targetEntity == null || !this.targetEntity.isEntityAlive()) {
            return;
        }

        double dx    = this.targetEntity.posX - this.taskOwner.posX;
        double dy    = this.targetEntity.posY - this.taskOwner.posY;
        double dz    = this.targetEntity.posZ - this.taskOwner.posZ;
        double horizSq = dx*dx + dz*dz;
        double vert   = Math.abs(dy);
        this.performExtendedMeleeAttack();

        if (!this.taskOwner.getNavigator().noPath()) {
            PathEntity path = this.taskOwner.getNavigator().getPath();
            PathPoint end = path.getFinalPathPoint();

            double ex = end.xCoord + 0.5 - this.targetEntity.posX;
            double ey = end.yCoord      - this.targetEntity.posY;
            double ez = end.zCoord + 0.5 - this.targetEntity.posZ;

            double distSq = ex * ex + ey * ey + ez * ez;

            if (distSq > STALE_THRESHOLD_SQ) {

                this.taskOwner.getNavigator().clearPathEntity();
                this.lastNavSuccess = false;
                this.repathDelay = 0;
            }
        }

        if (--this.repathDelay > 0) {
            return;
        }

        if (vert > VERTICAL_UNREACHABLE_Y) {
            applyFallbackMotion(dx, dz);
            scheduleBrokenProbe();
            return;
        }

        if (horizSq > MAX_RANGE_SQ) {
            this.lastNavSuccess = false;
            applyFallbackMotion(dx, dz);
            scheduleBrokenProbe();
            return;
        }

        if (!this.lastNavSuccess) {
            applyFallbackMotion(dx, dz);
            boolean success = this.taskOwner.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.moveSpeed);
            this.lastNavSuccess = success;
            scheduleBrokenProbe();
            return;
        }

        double horiz = Math.sqrt(horizSq);
        int    interval = (horiz <= 8.0D)
                ? 2 + this.taskOwner.getRNG().nextInt(3)
                : CLOSE_REPATH_INTERVAL;

        boolean success = this.taskOwner.getNavigator()
                .tryMoveToEntityLiving(this.targetEntity, this.moveSpeed);
        this.lastNavSuccess = success;
        this.repathDelay    = success ? interval : (BROKEN_PROBE_BASE + this.taskOwner.getRNG().nextInt(BROKEN_PROBE_VARIANCE));
    }

    private void applyFallbackMotion(double dx, double dz) {
        Vec3 dir = Vec3.createVectorHelper(dx, 0.0, dz).normalize();
        this.taskOwner.motionX += dir.xCoord * 0.1;
        this.taskOwner.motionZ += dir.zCoord * 0.1;
    }

    private void scheduleBrokenProbe() {
        this.repathDelay = BROKEN_PROBE_BASE + this.taskOwner.getRNG().nextInt(BROKEN_PROBE_VARIANCE);
    }

    private void performExtendedMeleeAttack() {
        if (this.targetEntity == null) return;

        if (this.taskOwner.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {

            double distanceSq = this.taskOwner.getDistanceSqToEntity(this.targetEntity);
            int attackRange = computeRangeForHeldItem(this.taskOwner.getHeldItem());

            if (distanceSq < attackRange && this.attackTick <= 1) {
                if (!this.taskOwner.canEntityBeSeen(this.targetEntity)) return;
                this.taskOwner.swingItem();
                this.taskOwner.attackEntityAsMob(this.targetEntity);
                this.attackTick = 13 - NMUtils.getWorldProgress() * 2;
            }

            if (NMUtils.getIsMobEclipsed(this.taskOwner) && distanceSq < 3) {
                this.taskOwner.swingItem();
                this.taskOwner.attackEntityAsMob(this.targetEntity);
                this.attackTick = 20;
            }
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
        return NMUtils.LONG_RANGE_ITEMS;
    }

    private Set<Integer> getLesserRangeItems() {
        return NMUtils.LESSER_RANGE_ITEMS;
    }
}
