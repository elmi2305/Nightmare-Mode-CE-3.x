package com.itlesports.nightmaremode.AITasks;


import com.itlesports.nightmaremode.entity.EntityFauxVillager;
import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityCreature;
import net.minecraft.src.EntityPlayer;

public class EntityAIFollowPlayerIfWatched extends EntityAIBase {
    private final EntityCreature mob;

    public EntityAIFollowPlayerIfWatched(EntityCreature creature) {
        this.mob = creature;
        this.setMutexBits(1); // movement
    }

    @Override
    public boolean shouldExecute() {
        if (!(this.mob instanceof EntityFauxVillager angryVillager)) return false;
        EntityPlayer target = angryVillager.followTarget;
        return target != null;
    }

    @Override
    public boolean continueExecuting() {
        if (!(this.mob instanceof EntityFauxVillager angryVillager)) return false;
        EntityPlayer target = angryVillager.followTarget;
        return target != null;
    }

    @Override
    public void updateTask() {
        EntityFauxVillager angryVillager = (EntityFauxVillager) this.mob;
        EntityPlayer target = angryVillager.followTarget;
        if (target == null) return;

        int anger = angryVillager.getAngerTicks();

        float minSpeed = 0.5F;
        float maxSpeed = 0.9F;
        float progress = Math.min((float) anger / EntityFauxVillager.MAX_ANGER, 1.0F); // Normalize 0–1500
        float speed = minSpeed + (maxSpeed - minSpeed) * progress;

        this.mob.getNavigator().tryMoveToEntityLiving(target, speed);
    }

    @Override
    public void resetTask() {
        ((EntityFauxVillager) this.mob).followTarget = null;
        this.mob.getNavigator().clearPathEntity();
    }

}
