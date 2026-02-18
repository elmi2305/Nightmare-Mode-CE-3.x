package com.itlesports.nightmaremode.AITasks;

import net.minecraft.src.*;

import java.util.Random;


public class EntityAISummonBabies extends EntityAIBase {

    private final EntityCreature entity;
    private EntityPlayer targetPlayer;

    private EntityCreature[] summons = new EntityCreature[3];
    private double[] targetXs = new double[3];
    private double[] targetZs = new double[3];
    private double[] surfaceYs = new double[3];
    private double[] buriedYs = new double[3];

    private int cooldownTimer = 20;
    private final int cooldownMax = 400;

    private final double depth = 3.0D; // bury depth

    private boolean isSummoning = false;


    private double riseProgress = 0.0; // 0.0 - 1.0
    private final double riseDurationTicks = 16.0;

    public EntityAISummonBabies(EntityCreature entity) {
        this.entity = entity;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (cooldownTimer > 0) {
            cooldownTimer--;
            return false;
        }

        EntityPlayer player = this.entity.worldObj.getClosestPlayerToEntity(this.entity, 20.0D);
        if (player != null && this.entity.getDistanceSqToEntity(player) <= 36) {
            this.targetPlayer = player;
//            System.out.println("DEBUG: EntityAISummonBabies - Player 8-20 blocks away, starting summons for " + this.entity.getEntityName());
            return true;
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        return this.isSummoning;
    }

    @Override
    public void startExecuting() {
//        System.out.println("DEBUG: EntityAISummonBabies.startExecuting() - " + this.entity.getEntityName() + " BEGIN SUMMONS");

        calculatePositions();
        spawnAllBuried();
        this.riseProgress = 0.0;
        this.isSummoning = true;

        for (EntityCreature baby : this.summons) {
            if (baby != null && !baby.isDead) {
                baby.setInvisible(true);
                baby.noClip = true;
                baby.getNavigator().clearPathEntity();
            }
        }

        this.entity.getNavigator().clearPathEntity();
        this.entity.motionX = this.entity.motionY = this.entity.motionZ = 0.0D;
    }
    @Override
    public void updateTask() {
        this.entity.motionX = 0.0D;
        this.entity.motionZ = 0.0D;
        this.entity.fallDistance = 0.0F;

        if (!this.isSummoning) return;

        this.riseProgress += 1.0 / this.riseDurationTicks;
        if (this.riseProgress >= 1.0) {
            finishAllRises();
            return;
        }

        double currentYFactor = this.riseProgress;

        for (int i = 0; i < 3; i++) {
            EntityCreature baby = this.summons[i];
            if (baby == null || baby.isDead) continue;

            double targetY = this.buriedYs[i] + (this.surfaceYs[i] - this.buriedYs[i]) * currentYFactor;

            baby.setPosition(
                    this.targetXs[i],
                    targetY,
                    this.targetZs[i]
            );
            baby.motionX = baby.motionY = baby.motionZ = 0.0D;
            baby.noClip = true;  // keep enforcing

            if (this.riseProgress >= 0.05) {
                // become visible almost immediately
                baby.setInvisible(false);
            }
        }
    }

    private void finishAllRises() {
        for (int i = 0; i < 3; i++) {
            EntityCreature baby = this.summons[i];
            if (baby == null || baby.isDead) continue;

            baby.setPositionAndUpdate(
                    this.targetXs[i],
                    this.surfaceYs[i],
                    this.targetZs[i]
            );
            baby.noClip = false;
            baby.motionX = baby.motionY = baby.motionZ = 0.0D;
            baby.setAttackTarget(this.targetPlayer);
//            System.out.println("DEBUG: Baby " + i + " finished rise");
        }

        this.isSummoning = false;
        this.cooldownTimer = this.cooldownMax;
        this.targetPlayer = null;
    }

    @Override
    public void resetTask() {
//        System.out.println("DEBUG: EntityAISummonBabies.resetTask() - " + this.entity.getEntityName() + " SUMMONS INTERRUPTED");

        // something interrupted me
        for (int i = 0; i < 3; i++) {
            EntityCreature baby = this.summons[i];
            if (baby != null && !baby.isDead) {
                baby.setPosition(this.targetXs[i], this.surfaceYs[i], this.targetZs[i]);
                baby.noClip = false;
                baby.setInvisible(false);
                baby.motionX = baby.motionY = baby.motionZ = 0.0D;
                baby.setAttackTarget(this.targetPlayer);
            }
        }

        this.isSummoning = false;
        this.cooldownTimer = this.cooldownMax;
        this.targetPlayer = null;
        this.entity.getNavigator().clearPathEntity();
    }

    private void calculatePositions() {
        Random rand = this.entity.getRNG();
        int found = 0;
        int attempts = 0;
        while (found < 3 && attempts < 50) {
            attempts++;
            double radius = 2.5D + rand.nextDouble() * 2.5D;
            double angle = rand.nextDouble() * Math.PI * 2.0D;

            double tx = this.entity.posX + Math.cos(angle) * radius;
            double tz = this.entity.posZ + Math.sin(angle) * radius;

            // avoid spawning too close to the player
            double pdx = tx - this.targetPlayer.posX;
            double pdz = tz - this.targetPlayer.posZ;
            if (pdx * pdx + pdz * pdz < 4.0D) continue;

            int topY = this.entity.worldObj.getTopSolidOrLiquidBlock((int) tx, (int) tz);
            if (topY < this.entity.posY - 5 || topY > this.entity.posY + 10) continue;  // Reasonable height

            this.targetXs[found] = tx;
            this.targetZs[found] = tz;
            this.surfaceYs[found] = topY;
            this.buriedYs[found] = topY - this.depth;

//            System.out.println("DEBUG: Summon pos " + found + ": " + (int) tx + ", Y=" + (int) this.surfaceYs[found] + ", " + (int) tz);
            found++;
        }
    }

    private void spawnAllBuried() {
        for (int i = 0; i < 3; i++) {
            EntityZombie baby = new EntityZombie(this.entity.worldObj);
            baby.setChild(true);

            double bx = this.targetXs[i];
            double by = this.buriedYs[i];
            double bz = this.targetZs[i];

            baby.setPosition(bx, by, bz);
            baby.noClip = true;
//            baby.setInvisible(true);
            baby.motionX = baby.motionY = baby.motionZ = 0.0D;
            baby.setHealth(baby.getMaxHealth());
//            baby.addPotionEffect(new PotionEffect(Potion.heal.id, 100000, 0));
            baby.rotationYaw = (float) (this.entity.getRNG().nextDouble() * 360.0D);

            this.entity.worldObj.spawnEntityInWorld(baby);
            this.summons[i] = baby;
            baby.getNavigator().clearPathEntity();  // make sure the baby didn't path somewhere
            System.out.println("DEBUG: Buried baby " + i + " spawned at Y=" + (int) by);
        }
    }
}