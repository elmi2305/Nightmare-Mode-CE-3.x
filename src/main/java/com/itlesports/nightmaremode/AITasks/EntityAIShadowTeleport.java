package com.itlesports.nightmaremode.AITasks;

import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
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
            double bloodMoonModifier = NightmareUtils.getIsBloodMoon() ? 1.5 : 1;
            return this.taskOwner.getDistanceSqToEntity(this.targetEntity) <= ((this.taskOwner.worldObj.getDifficulty() == Difficulties.HOSTILE ? 400 : 100) * bloodMoonModifier);
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        int xOffset = (this.taskOwner.rand.nextBoolean() ? -1 : 1) * (this.taskOwner.rand.nextInt(3)+1);
        int zOffset = (this.taskOwner.rand.nextBoolean() ? -1 : 1) * (this.taskOwner.rand.nextInt(3)+1);

        int targetX = MathHelper.floor_double(this.targetEntity.posX + xOffset);
        int targetY = MathHelper.floor_double(this.targetEntity.posY);
        int targetZ = MathHelper.floor_double(this.targetEntity.posZ + zOffset);

        if(this.canTeleportHere(this.taskOwner.worldObj, targetX,targetY,targetZ) && this.cooldown == 0){

            int shadowCooldown = NightmareUtils.getIsBloodMoon() ? 10 : 20;
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
