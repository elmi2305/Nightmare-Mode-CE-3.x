package com.itlesports.nightmaremode.AITasks;
import java.util.Random;

import java.util.Random;

import net.minecraft.src.EntityAIBase;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

public class ZombieParkourBehavior extends EntityAIBase {
    private final EntityLiving zombie;
    private final World world;
    private final Random rand;

    int ticksOnGround = 0;

    // Target landing coordinates
    private int landX, landY, landZ;
    // Jump state
    private boolean isJumping = false;

    // Gravity constant in MC 1.6.4
    private static final double GRAVITY = 0.095D;
    // Base jump velocity to clear one block
    private static final double BASE_JUMP_VELOCITY = 0.42D;
    // Additional vertical boost per extra block height
    private static final double EXTRA_VERTICAL_PER_BLOCK = 0.15D;

    // How many random attempts to find a valid block each time
    private static final int MAX_SCAN_ATTEMPTS = 20;
    // Horizontal scan radius (in blocks)
    private static final int HORIZONTAL_RADIUS = 4;
    // Vertical scan range (relative to zombie’s current floor Y)
    private static final int MIN_VERTICAL_OFFSET = -1;
    private static final int MAX_VERTICAL_OFFSET = 2;

    public ZombieParkourBehavior(EntityLiving zombie) {
        this.zombie = zombie;
        this.world = zombie.worldObj;
        this.rand = zombie.getRNG();
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.zombie.getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return false;
        }
        if (!this.zombie.onGround) {
            return false;
        } else{
            this.ticksOnGround = Math.min(this.ticksOnGround + 1, 40);
        }

        return this.zombie.getNavigator().noPath() && this.ticksOnGround == 40;
    }

    @Override
    public void startExecuting() {
        this.ticksOnGround = 0;
        this.isJumping = false;
    }

    @Override
    public boolean continueExecuting() {
        if (this.isJumping) {
            return !this.zombie.onGround;
        }
        EntityLivingBase target = this.zombie.getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return false;
        }
        return this.zombie.getNavigator().noPath();
    }

    @Override
    public void resetTask() {
        this.isJumping = false;
        this.ticksOnGround = 0;
    }

    @Override
    public void updateTask() {
        if (this.isJumping) {
            return;
        }
        EntityLivingBase target = this.zombie.getAttackTarget();
        if (target == null || !target.isEntityAlive()) {
            return;
        }

        int baseX = MathHelper.floor_double(this.zombie.posX);
        int baseY = MathHelper.floor_double(this.zombie.posY);
        int baseZ = MathHelper.floor_double(this.zombie.posZ);

        double dxToPlayer = target.posX - this.zombie.posX;
        double dzToPlayer = target.posZ - this.zombie.posZ;
        double currentDistSq = dxToPlayer * dxToPlayer + dzToPlayer * dzToPlayer;

        for (int i = 0; i < MAX_SCAN_ATTEMPTS; i++) {
            int dx = this.rand.nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;
            int dz = this.rand.nextInt(HORIZONTAL_RADIUS * 2 + 1) - HORIZONTAL_RADIUS;
            int dy = this.rand.nextInt(MAX_VERTICAL_OFFSET - MIN_VERTICAL_OFFSET + 1) + MIN_VERTICAL_OFFSET;

            int candidateX = baseX + dx;
            int candidateY = baseY + dy;
            int candidateZ = baseZ + dz;

            if (this.world.getBlockId(candidateX, candidateY, candidateZ) == 0) {
                continue;
            }
            if (this.world.getBlockId(candidateX, candidateY + 1, candidateZ) != 0) {
                continue;
            }
            if (this.world.getBlockId(candidateX, candidateY + 2, candidateZ) != 0) {
                continue;
            }
            if (this.world.getBlockId(candidateX, candidateY + 3, candidateZ) != 0) {
                continue;
            }

            double centerX = candidateX + 0.5D;
            double centerZ = candidateZ + 0.5D;
            double dxCandToPlayer = target.posX - centerX;
            double dzCandToPlayer = target.posZ - centerZ;
            double candDistSq = dxCandToPlayer * dxCandToPlayer + dzCandToPlayer * dzCandToPlayer;

            if (candDistSq >= currentDistSq) {
                continue;
            }

            this.landX = candidateX;
            this.landY = candidateY;
            this.landZ = candidateZ;
            this.doJumpToTarget();
            break;
        }
    }

    private void doJumpToTarget() {
        double startX = this.zombie.posX;
        double startY = this.zombie.posY;
        double startZ = this.zombie.posZ;

        double destX = this.landX + 0.5D;
        double destY = this.landY + 1.0D;
        double destZ = this.landZ + 0.5D;

        double deltaY = destY - startY;
        double dx = destX - startX;
        double dz = destZ - startZ;
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        double v0 = BASE_JUMP_VELOCITY + EXTRA_VERTICAL_PER_BLOCK * Math.max(0.0D, deltaY);
        double a = 0.5D * GRAVITY;
        double b = -v0;
        double c = deltaY;
        double disc = b * b - 4.0D * a * c;
        if (disc < 0.0D) {
            return;
        }
        double t = (-b + Math.sqrt(disc)) / (2.0D * a);
        if (t <= 0.0D) {
            return;
        }

        double horizSpeed = horizontalDist / t;
        double dxNorm = dx / horizontalDist;
        double dzNorm = dz / horizontalDist;

        this.zombie.motionX = dxNorm * horizSpeed;
        this.zombie.motionZ = dzNorm * horizSpeed;
        this.zombie.motionY = v0;

        this.isJumping = true;
    }
}








