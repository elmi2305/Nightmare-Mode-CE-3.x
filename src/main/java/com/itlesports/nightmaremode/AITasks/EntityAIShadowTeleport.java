package com.itlesports.nightmaremode.AITasks;

import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

public class EntityAIShadowTeleport extends EntityAITarget {
    private EntityLivingBase targetEntity;
    private int cooldown;

    public EntityAIShadowTeleport(EntityCreature par1EntityCreature, boolean par2, boolean par3) {
        super(par1EntityCreature, par2, par3);
        this.setMutexBits(0);
    }

    @Override
    public boolean shouldExecute() {
        if (this.taskOwner.getAttackTarget() instanceof EntityPlayer player) {
            this.targetEntity = player;
            double bloodMoonModifier = NMUtils.getIsBloodMoon() ? 4 : 1;
            int distance = this.taskOwner.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class) ? 400 : 100;
            return this.taskOwner.getDistanceSqToEntity(this.targetEntity) <= distance * bloodMoonModifier;
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        boolean bIsBloodmoon = NMUtils.getIsBloodMoon();
        int minimumOffset = bIsBloodmoon ? 0 : 1;
        int xOffset = (this.taskOwner.rand.nextBoolean() ? -1 : 1) * (this.taskOwner.rand.nextInt(3) + minimumOffset);
        int zOffset = (this.taskOwner.rand.nextBoolean() ? -1 : 1) * (this.taskOwner.rand.nextInt(3) + minimumOffset);

        int targetX = MathHelper.floor_double(this.targetEntity.posX + xOffset);
        int targetY = MathHelper.floor_double(this.targetEntity.posY);
        int targetZ = MathHelper.floor_double(this.targetEntity.posZ + zOffset);

        if(this.canTeleportHere(this.taskOwner.worldObj, targetX,targetY,targetZ) && this.cooldown == 0){

            int shadowCooldown = NMUtils.getIsBloodMoon() ? 10 : 20;
            this.taskOwner.setPositionAndUpdate(targetX,targetY, targetZ);
            this.taskOwner.playSound("mob.endermen.portal",2.0F,1.0F);
            this.cooldown = shadowCooldown + this.taskOwner.rand.nextInt(20)+1;
        }

        this.cooldown = Math.max(--this.cooldown, 0);
        return this.cooldown == 0;
    }


    private boolean canTeleportHere(World world, int targetX, int targetY, int targetZ) {
        return world.getBlockId(targetX, targetY, targetZ) == 0
                && world.getBlockId(targetX, targetY - 1, targetZ) != 0
                && world.getBlockId(targetX, targetY + 1, targetZ) == 0;
    }
}
