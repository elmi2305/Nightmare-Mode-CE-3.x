package com.itlesports.nightmaremode.AITasks;

import com.itlesports.nightmaremode.util.CarcassHarvesting;
import com.itlesports.nightmaremode.util.interfaces.CarcassAnimal;
import net.minecraft.src.*;

public class EntityAIHarvestCarcass extends EntityAIBase {
    private final EntityZombie zombie;
    private EntityLivingBase corpse;

    public EntityAIHarvestCarcass(EntityZombie zombie) {
        this.zombie = zombie;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.zombie.getAttackTarget() != null || !this.zombie.isEntityAlive()) return false;
        this.corpse = findNearbyCorpse(this.zombie, EntityAnimal.class);
        return this.corpse != null;
    }

    @Override
    public boolean continueExecuting() {
        return this.zombie.getAttackTarget() == null && isHarvestable(this.corpse)
                && this.zombie.getDistanceSqToEntity(this.corpse) <= 256.0D;
    }

    @Override
    public void updateTask() {
        this.zombie.getLookHelper().setLookPositionWithEntity(this.corpse, 30.0F, 30.0F);
        if (this.zombie.getDistanceSqToEntity(this.corpse) <= 4.0D) {
            CarcassHarvesting.harvestByMob(this.corpse);
        } else if (this.zombie.ticksExisted % 10 == 0) {
            this.zombie.getNavigator().tryMoveToXYZ(this.corpse.posX, this.corpse.posY, this.corpse.posZ, 1.0D);
        }
    }

    @Override
    public void resetTask() {
        this.corpse = null;
        this.zombie.getNavigator().clearPathEntity();
    }

    public static boolean isHarvestable(EntityLivingBase entity) {
        return entity != null && !entity.isDead && entity instanceof CarcassAnimal carcass
                && carcass.nm$isCarcass() && carcass.nm$getHarvesterId() < 0;
    }

    public static EntityLivingBase findNearbyCorpse(EntityLiving mob, Class<? extends EntityAnimal> type) {
        EntityPlayer player = mob.worldObj.getClosestVulnerablePlayerToEntity(mob, 16.0D);
        if (player != null && mob.canEntityBeSeen(player)) return null;
        EntityLivingBase nearest = null;
        double distance = 256.0D;
        for (Object candidate : mob.worldObj.getEntitiesWithinAABB(type, mob.boundingBox.expand(16.0D, 4.0D, 16.0D))) {
            EntityLivingBase entity = (EntityLivingBase)candidate;
            double nextDistance = mob.getDistanceSqToEntity(entity);
            if (isHarvestable(entity) && nextDistance < distance && mob.canEntityBeSeen(entity)) {
                nearest = entity;
                distance = nextDistance;
            }
        }
        return nearest;
    }
}
