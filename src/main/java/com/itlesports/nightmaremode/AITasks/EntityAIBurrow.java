package com.itlesports.nightmaremode.AITasks;

import btw.util.BTWSounds;
import net.minecraft.src.*;

public class EntityAIBurrow extends EntityAIBase {

    private final EntityCreature entity;
    private EntityPlayer targetPlayer;
    private double targetX, targetY, targetZ;
    private double targetBuriedY; // Y where it stops sinking (old location)
    private double buriedTargetY; // Y where it starts rising (new location, 5 blocks underground)
    private double burrowStartX, burrowStartZ;

    private final int burrowTicks; // how long to stay fully underground
    private int taskTimer; // countdown while fully burrowed
    private int cooldownTimer;
    private final int cooldownMax = 80;

    private final double burrowDepth = 3.0D; // how many blocks deep to burrow

    private boolean isBurrowed = false;
    private int burrowPhase = 0; // 0 = idle, 1 = sinking, 2 = fully burrowed (invisible), 3 = rising

    public EntityAIBurrow(EntityCreature entity, int burrowTicks) {
        this.entity = entity;
        this.burrowTicks = burrowTicks;
        this.cooldownTimer = 20;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (cooldownTimer > 0) {
            cooldownTimer--;
            return false;
        }

        EntityPlayer player = this.entity.worldObj.getClosestPlayerToEntity(this.entity, 25.0D);
        if (player != null && this.entity.getDistanceSqToEntity(player) >= 36) {
            this.targetPlayer = player;
//            System.out.println("DEBUG: EntityAIBurrow - Player in range (>=16), starting burrow for " + this.entity.getEntityName());
            return true;
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        return this.isBurrowed; // stays true during sinking + buried + rising
    }

    @Override
    public void startExecuting() {
//        System.out.println("DEBUG: EntityAIBurrow.startExecuting() - " + this.entity.getEntityName() + " BEGIN");


        this.burrowStartX = this.entity.posX;
        this.burrowStartZ = this.entity.posZ;
        this.targetBuriedY = this.entity.posY - this.burrowDepth;

        this.entity.getNavigator().clearPathEntity(); // stop any old pathfinding

        this.burrowPhase = 1;
        this.isBurrowed = true;

        this.entity.noClip = true;
        this.entity.setInvisible(false); // visible while digging down
        this.entity.motionX = this.entity.motionY = this.entity.motionZ = 0.0D;
    }

    @Override
    public void updateTask() {
        this.entity.motionX = 0.0D;
        this.entity.motionZ = 0.0D;
        this.entity.fallDistance = 0.0F; // safety from fall damage

        if (this.burrowPhase == 1) {
            // SINKING
            this.entity.motionY = -0.2D;

            if(this.entity.ticksExisted % 4 == 0){
                this.entity.playSound(BTWSounds.DIRT_BREAK.sound(), 1.0f, 0.9f);
            }

            if (this.entity.posY <= this.targetBuriedY + 0.05D) {
                this.entity.setPositionAndUpdate(this.burrowStartX, this.targetBuriedY, this.burrowStartZ);
                this.entity.motionY = 0.0D;
                this.entity.setInvisible(true); // invisible ONLY when fully buried
                this.burrowPhase = 2;
                this.taskTimer = this.burrowTicks;
//                System.out.println("DEBUG: " + this.entity.getEntityName() + " FULLY BURIED (invisible) at Y=" + (int)this.targetBuriedY);
            }

        } else if (this.burrowPhase == 2) {
            // FULLY BURIED
            this.entity.motionY = 0.0D;
            this.taskTimer--;
            calculateUnburrowPosition();

            if (this.taskTimer <= 0) {
                // teleport underground to new spot
                this.entity.setPositionAndUpdate(this.targetX, this.buriedTargetY, this.targetZ);
                this.entity.setInvisible(false); // become visible for emerge animation
                this.burrowPhase = 3;
//                System.out.println("DEBUG: " + this.entity.getEntityName() + " START RISING at new spot " + (int)this.targetX + ", " + (int)this.buriedTargetY + ", " + (int)this.targetZ);
            }

        } else if (this.burrowPhase == 3) {
            // RISING
            this.entity.motionY = 0.2D;

            if(this.entity.ticksExisted % 3 == 0){
                this.entity.playSound(BTWSounds.DIRT_BREAK.sound(), 1.0f, 0.9f);
            }

            if (this.entity.posY >= this.targetY - 0.05D) {
                this.entity.setPositionAndUpdate(this.targetX, this.targetY, this.targetZ);
                this.entity.motionY = 0.0D;
                this.burrowPhase = 0;
                this.isBurrowed = false;
                this.entity.noClip = false;
                this.entity.setInvisible(false);
//                System.out.println("DEBUG: " + this.entity.getEntityName() + " FULLY UNBURROWED at surface!");
                 this.entity.setAttackTarget(this.targetPlayer); // in case it was lost
            }
        }
    }

    @Override
    public void resetTask() {
//        System.out.println("DEBUG: EntityAIBurrow.resetTask() - " + this.entity.getEntityName() + " END (forced)");

        if (this.burrowPhase != 0) {
            // safety: always pop to surface if task is interrupted
            this.entity.setPositionAndUpdate(this.targetX, this.targetY, this.targetZ);
            this.entity.noClip = false;
            this.entity.setInvisible(false);
//            System.out.println("DEBUG: Force-unburrowed to surface due to task reset");
        }

        this.burrowPhase = 0;
        this.isBurrowed = false;
        this.cooldownTimer = this.cooldownMax;
        this.targetPlayer = null;
    }

    private void calculateUnburrowPosition() {
        double radius = 0.75d + this.entity.getRNG().nextDouble() * 0.25;
        double angle = this.entity.getRNG().nextDouble() * 2.0D * Math.PI;

        this.targetX = this.targetPlayer.posX + Math.cos(angle) * radius;
        this.targetZ = this.targetPlayer.posZ + Math.sin(angle) * radius;

        int topBlock = this.entity.worldObj.getTopSolidOrLiquidBlock((int) this.targetX, (int) this.targetZ) - 1;
        this.targetY = topBlock + 1.0D;
        this.buriedTargetY = this.targetY - this.burrowDepth; // start rising at the right depth depending on the new ground

//        System.out.println("DEBUG: Calculated unburrow spot -> surface " + (int)this.targetX + ", " + (int)this.targetY + ", " + (int)this.targetZ + " | buried start Y=" + (int)this.buriedTargetY);
    }
}