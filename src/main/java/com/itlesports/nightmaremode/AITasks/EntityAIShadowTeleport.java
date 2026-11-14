package com.itlesports.nightmaremode.AITasks;

import com.itlesports.nightmaremode.world.BalanceProfile;

import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

public class EntityAIShadowTeleport extends EntityAITarget {
    private EntityLivingBase targetEntity;
    private int cooldown;

    public EntityAIShadowTeleport(EntityCreature par1EntityCreature, boolean shouldCheckSight, boolean par3) {
        super(par1EntityCreature, shouldCheckSight, par3);
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        this.targetEntity = this.taskOwner.getAttackTarget() instanceof EntityPlayer player ? player : null;
        return this.isTargetInRange();
    }

    private boolean isTargetInRange() {
        return this.targetEntity != null && this.targetEntity.isEntityAlive()
                && this.taskOwner.getAttackTarget() == this.targetEntity
                && this.taskOwner.getDistanceSqToEntity(this.targetEntity) <= this.getTeleportRangeSquared();
    }

    private double getTeleportRangeSquared() {
        return BalanceProfile.isCombatEasy() ? 25.0D : 64.0D;
    }

    @Override
    public boolean continueExecuting() {
        if (!this.isTargetInRange()) return false;
        if (this.cooldown == 0) {
            boolean bIsBloodmoon = NMUtils.getIsBloodMoon();
            int distance = 3 + this.taskOwner.rand.nextInt(2);
            int minimumOffset = BalanceProfile.isCombatEasy() ? 1 : bIsBloodmoon ? 0 : 1 + distance;
            int xOffset = (this.taskOwner.rand.nextBoolean() ? -1 : 1) * (this.taskOwner.rand.nextInt(3) + minimumOffset);
            int zOffset = (this.taskOwner.rand.nextBoolean() ? -1 : 1) * (this.taskOwner.rand.nextInt(3) + minimumOffset);

            int targetX = MathHelper.floor_double(this.targetEntity.posX + xOffset);
            int targetY = MathHelper.floor_double(this.targetEntity.posY);
            int targetZ = MathHelper.floor_double(this.targetEntity.posZ + zOffset);

            if (this.taskOwner.getDistanceSq(targetX, targetY, targetZ) <= this.getTeleportRangeSquared()
                    && this.canTeleportHere(this.taskOwner.worldObj, targetX,targetY,targetZ)){

                int shadowCooldown = NMUtils.getIsBloodMoon() ? 10 : 20;
                this.taskOwner.setPositionAndUpdate(targetX,targetY, targetZ);
                this.taskOwner.playSound("mob.endermen.portal",2.0F,1.0F);
                this.cooldown = shadowCooldown + this.taskOwner.rand.nextInt(20)+1;
            }
        }

        this.cooldown = Math.max(--this.cooldown, 0);
        return this.cooldown == 0;
    }


    private boolean canTeleportHere(World world, int targetX, int targetY, int targetZ) {
        return world.getBlockId(targetX, targetY, targetZ) == 0
                && world.getBlockId(targetX, targetY + 1, targetZ) == 0
                && world.getBlockId(targetX, targetY - 1, targetZ) != 0;
    }
}
