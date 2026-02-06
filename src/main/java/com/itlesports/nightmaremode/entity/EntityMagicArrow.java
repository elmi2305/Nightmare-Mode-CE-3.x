package com.itlesports.nightmaremode.entity;

import api.achievement.AchievementEventDispatcher;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.mixin.entity.EntityAccessor;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.List;

public class EntityMagicArrow extends EntityArrow {
    private float damageDone = 0;
    private final List<EntityLivingBase> entitiesHit = new ArrayList<>();


    private int ticksInGround;
    private int ticksInAir;
    private int knockbackStrength;

    public EntityMagicArrow(World world, EntityLivingBase entityLiving, float f) {
        super(world, entityLiving, f);
    }

    public Item getCorrespondingItem() {
        return NMItems.magicArrow;
    }

    @Override
    public void onUpdate() {
        super.onEntityUpdate();

        initializeRotationIfNeeded();
        updateGroundState();

        if (arrowShake > 0) {
            arrowShake--;
        }

        if (inGround) {
            handleGroundedState();
        } else {
            handleAirborneState();
        }

        notifyAnyCollidingBlocks();
        handleGroundParticlesAndDeath();
    }

    private void initializeRotationIfNeeded() {
        if (prevRotationPitch == 0.0f && prevRotationYaw == 0.0f) {
            float horizontalSpeed = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
            prevRotationYaw = rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0 / Math.PI);
            prevRotationPitch = rotationPitch = (float)(Math.atan2(motionY, horizontalSpeed) * 180.0 / Math.PI);
        }
    }

    private void updateGroundState() {
        int blockId = worldObj.getBlockId(xTile, yTile, zTile);
        if (blockId > 0) {
            AxisAlignedBB boundingBox = Block.blocksList[blockId].getCollisionBoundingBoxFromPool(
                    worldObj, xTile, yTile, zTile
            );

            if (boundingBox != null && isPositionInsideBoundingBox(boundingBox)) {
                inGround = true;
            }
        }
    }

    private boolean isPositionInsideBoundingBox(AxisAlignedBB boundingBox) {
        Vec3 position = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
        return boundingBox.isVecInside(position);
    }

    private void handleGroundedState() {
        int currentBlockId = worldObj.getBlockId(xTile, yTile, zTile);
        int currentMetadata = worldObj.getBlockMetadata(xTile, yTile, zTile);

        boolean blockMatches = currentBlockId == inTile &&
                (currentMetadata == inData || currentBlockId == BTWBlocks.detectorBlock.blockID);

        if (blockMatches) {
            ticksInGround++;
            if (ticksInGround >= 6000) {
                setDead();
            }
        } else {
            resetFromGround();
        }
    }

    private void resetFromGround() {
        inGround = false;
        motionX *= rand.nextFloat() * 0.2f;
        motionY *= rand.nextFloat() * 0.2f;
        motionZ *= rand.nextFloat() * 0.2f;
        ticksInGround = 0;
        ticksInAir = 0;
    }

    private void handleAirborneState() {
        ticksInAir++;

        MovingObjectPosition collision = performRaytraceAndEntityChecks();
        collision = filterPlayerCollision(collision);

        if (collision != null) {
            if (collision.entityHit != null) {
                handleEntityHit(collision);
            } else {
                handleBlockHit(collision);
            }
        }

        spawnCriticalParticles();
        updatePositionAndRotation();
        applyPhysics();
        setPosition(posX, posY, posZ);
        doBlockCollisions();
    }

    private MovingObjectPosition performRaytraceAndEntityChecks() {
        Vec3 startPos = worldObj.getWorldVec3Pool().getVecFromPool(posX, posY, posZ);
        Vec3 endPos = worldObj.getWorldVec3Pool().getVecFromPool(
                posX + motionX, posY + motionY, posZ + motionZ
        );

        MovingObjectPosition blockCollision = worldObj.rayTraceBlocks_do_do(startPos, endPos, false, true);

        if (blockCollision != null) {
            endPos = worldObj.getWorldVec3Pool().getVecFromPool(
                    blockCollision.hitVec.xCoord,
                    blockCollision.hitVec.yCoord,
                    blockCollision.hitVec.zCoord
            );
        }

        Entity hitEntity = findClosestCollidingEntity(startPos, endPos);
        return hitEntity != null ? new MovingObjectPosition(hitEntity) : blockCollision;
    }

    private Entity findClosestCollidingEntity(Vec3 startPos, Vec3 endPos) {
        AxisAlignedBB searchBox = boundingBox
                .addCoord(motionX, motionY, motionZ)
                .expand(1.0, 1.0, 1.0);

        List<Entity> entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, searchBox);
        Entity closestEntity = null;
        double closestDistance = 0.0;

        for (Entity entity : entities) {
            if (!canArrowCollideWithThisEntity(entity)) {
                continue;
            }

            float expansion = 0.3f;
            AxisAlignedBB expandedBox = entity.boundingBox.expand(expansion, expansion, expansion);
            MovingObjectPosition intercept = expandedBox.calculateIntercept(startPos, endPos);

            if (intercept != null) {
                double distance = startPos.distanceTo(intercept.hitVec);
                if (closestEntity == null || distance < closestDistance) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        return closestEntity;
    }

    private boolean canArrowCollideWithThisEntity(Entity entity) {
        if (!entity.canBeCollidedWith()) {
            return false;
        }
        if (entity == shootingEntity && ticksInAir < 5) {
            return false;
        }
        return true;
    }

    private MovingObjectPosition filterPlayerCollision(MovingObjectPosition collision) {
        if (collision != null && collision.entityHit instanceof EntityPlayer hitPlayer) {
            if (shouldIgnorePlayerCollision(hitPlayer)) {
                return null;
            }
        }
        return collision;
    }

    private boolean shouldIgnorePlayerCollision(EntityPlayer hitPlayer) {
        if (hitPlayer.capabilities.disableDamage) {
            return true;
        }
        if (shootingEntity instanceof EntityPlayer shooter && !shooter.canAttackPlayer(hitPlayer)) {
            return true;
        }
        return false;
    }

    private void handleEntityHit(MovingObjectPosition collision) {
        Entity target = collision.entityHit;

        if (target.isEntityInvulnerable()) {
            return;
        }

        int damage = calculateDamage();
        DamageSource damageSource = createDamageSource();

        applyFireIfBurning(target);
        target.attackEntityFrom(damageSource, (int)((float)damage * getDamageMultiplier()));

        if (target instanceof EntityLivingBase livingTarget) {
            handleLivingEntityHit(livingTarget, damage);
        }

        playSound("random.bowhit", 1.0f, 1.2f / (rand.nextFloat() * 0.2f + 0.9f));
    }

    private int calculateDamage() {
        float speed = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        int baseDamage = MathHelper.ceiling_double_int((double)speed * getDamage());

        if (getIsCritical()) {
            baseDamage += rand.nextInt(baseDamage / 2 + 2);
        }

        return baseDamage;
    }

    private DamageSource createDamageSource() {
        return shootingEntity == null
                ? DamageSource.causeArrowDamage(this, this)
                : DamageSource.causeArrowDamage(this, shootingEntity);
    }

    private void applyFireIfBurning(Entity target) {
        if (isBurning() && !(target instanceof EntityEnderman)) {
            target.setFire(5);
        }
    }

    private void handleLivingEntityHit(EntityLivingBase target, int damage) {
        damageDone += damage * getDamageMultiplier();
        ((EntityAccessor)target).setInvulnerable(false);

        if (!worldObj.isRemote) {
            target.setArrowCountInEntity(target.getArrowCountInEntity() + 1);
        }

        applyKnockback(target);

        if (shootingEntity instanceof EntityPlayer player) {
            handlePlayerShooterEffects(player, target);
        }

        if (shootingEntity != null) {
            EnchantmentThorns.func_92096_a(shootingEntity, target, rand);
            notifyPlayerIfTargetIsPlayer(target);
        }
    }

    private void applyKnockback(Entity target) {
        if (knockbackStrength <= 0) {
            return;
        }

        float horizontalSpeed = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        if (horizontalSpeed > 0.0f) {
            double knockbackFactor = knockbackStrength * 0.6f / horizontalSpeed;
            target.addVelocity(
                    motionX * knockbackFactor,
                    0.1,
                    motionZ * knockbackFactor
            );
        }
    }

    private void handlePlayerShooterEffects(EntityPlayer player, EntityLivingBase target) {
        trackUniqueEnemyHit(player, target);
        trackDamageDealt(player);
        tryRefundMagicArrow(player);
    }

    private void trackUniqueEnemyHit(EntityPlayer player, EntityLivingBase target) {
        if (!entitiesHit.contains(target)) {
            entitiesHit.add(target);
            AchievementEventDispatcher.triggerEvent(
                    NMAchievementEvents.ArrowEnemyHitEvent.class,
                    player,
                    entitiesHit.size()
            );
        }
    }

    private void trackDamageDealt(EntityPlayer player) {
        AchievementEventDispatcher.triggerEvent(
                NMAchievementEvents.ArrowDamageEvent.class,
                player,
                damageDone
        );
    }

    private void tryRefundMagicArrow(EntityPlayer player) {
        if (!rand.nextBoolean() || player.capabilities.isCreativeMode) {
            return;
        }

        ItemStack heldItem = player.getHeldItem();
        if (heldItem != null && !hasInfinityEnchantment(heldItem)) {
            player.inventory.addItemStackToInventory(new ItemStack(NMItems.magicArrow));
        }
    }

    private boolean hasInfinityEnchantment(ItemStack item) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, item) > 0;
    }

    private void notifyPlayerIfTargetIsPlayer(Entity target) {
        if (target instanceof EntityPlayer && target != shootingEntity
                && shootingEntity instanceof EntityPlayerMP serverPlayer) {
            serverPlayer.playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(6, 0));
        }
    }

    private void handleBlockHit(MovingObjectPosition collision) {
        storeBlockCollisionData(collision);
        notifyCollidingBlockOfImpact();
        adjustPositionAfterBlockHit(collision);
        playSound("random.bowhit", 1.0f, 1.2f / (rand.nextFloat() * 0.2f + 0.9f));

        inGround = true;
        arrowShake = 7;
        setIsCritical(false);

        if (inTile != 0) {
            Block.blocksList[inTile].onEntityCollidedWithBlock(worldObj, xTile, yTile, zTile, this);
        }
    }

    private void storeBlockCollisionData(MovingObjectPosition collision) {
        xTile = collision.blockX;
        yTile = collision.blockY;
        zTile = collision.blockZ;
        inTile = worldObj.getBlockId(xTile, yTile, zTile);
        inData = worldObj.getBlockMetadata(xTile, yTile, zTile);
    }

    private void adjustPositionAfterBlockHit(MovingObjectPosition collision) {
        motionX = (float)(collision.hitVec.xCoord - posX);
        motionY = (float)(collision.hitVec.yCoord - posY);
        motionZ = (float)(collision.hitVec.zCoord - posZ);

        float totalMotion = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
        float offsetFactor = 0.05f / totalMotion;

        posX -= motionX * offsetFactor;
        posY -= motionY * offsetFactor;
        posZ -= motionZ * offsetFactor;
    }

    private void spawnCriticalParticles() {
        if (!getIsCritical()) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            double factor = i / 4.0;
            worldObj.spawnParticle(
                    "crit",
                    posX + motionX * factor,
                    posY + motionY * factor,
                    posZ + motionZ * factor,
                    -motionX,
                    -motionY + 0.2,
                    -motionZ
            );
        }
    }

    private void updatePositionAndRotation() {
        posX += motionX;
        posY += motionY;
        posZ += motionZ;

        float horizontalSpeed = MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ);
        rotationYaw = (float)(Math.atan2(motionX, motionZ) * 180.0 / Math.PI);
        rotationPitch = (float)(Math.atan2(motionY, horizontalSpeed) * 180.0 / Math.PI);

        normalizeRotationDifferences();

        rotationPitch = prevRotationPitch + (rotationPitch - prevRotationPitch) * 0.2f;
        rotationYaw = prevRotationYaw + (rotationYaw - prevRotationYaw) * 0.2f;
    }

    private void normalizeRotationDifferences() {
        while (rotationPitch - prevRotationPitch < -180.0f) {
            prevRotationPitch -= 360.0f;
        }
        while (rotationPitch - prevRotationPitch >= 180.0f) {
            prevRotationPitch += 360.0f;
        }
        while (rotationYaw - prevRotationYaw < -180.0f) {
            prevRotationYaw -= 360.0f;
        }
        while (rotationYaw - prevRotationYaw >= 180.0f) {
            prevRotationYaw += 360.0f;
        }
    }

    private void applyPhysics() {
        float drag = 0.99f;
        float gravity = 0.05f;

        if (isInWater()) {
            spawnWaterBubbles();
            drag = 0.8f;
        }

        motionX *= drag;
        motionY *= drag;
        motionZ *= drag;
        motionY -= gravity;
    }

    private void spawnWaterBubbles() {
        for (int i = 0; i < 4; i++) {
            float offset = 0.25f;
            worldObj.spawnParticle(
                    "bubble",
                    posX - motionX * offset,
                    posY - motionY * offset,
                    posZ - motionZ * offset,
                    motionX,
                    motionY,
                    motionZ
            );
        }
    }

    private void handleGroundParticlesAndDeath() {
        if (!isDead && inGround) {
            for (int i = 0; i < 32; i++) {
                worldObj.spawnParticle(
                        "iconcrack_266",
                        posX, posY, posZ,
                        (float)(Math.random() * 2.0 - 1.0) * 0.4f,
                        (float)(Math.random() * 2.0 - 1.0) * 0.4f,
                        (float)(Math.random() * 2.0 - 1.0) * 0.4f
                );
            }
            setDead();
        }
    }

    @Override
    protected float getDamageMultiplier() {
        return 1.333f;
    }

    private void notifyAnyCollidingBlocks() {
        Block blockHit;
        if (this.inGround && (blockHit = Block.blocksList[this.inTile]) != null) {
            blockHit.onArrowCollide(this.worldObj, this.xTile, this.yTile, this.zTile, this);
        }
    }
    private void notifyCollidingBlockOfImpact() {
        Block blockHit = Block.blocksList[this.inTile];
        if (blockHit != null) {
            blockHit.onArrowImpact(this.worldObj, this.xTile, this.yTile, this.zTile, this);
        }
    }

    public void setKnockbackStrength(int par1) {
        this.knockbackStrength = par1;
    }

    @Override
    public void setThrowableHeading(double par1, double par3, double par5, float par7, float par8) {
        float var9 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= (double)var9;
        par3 /= (double)var9;
        par5 /= (double)var9;
        par1 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)par8;
        par3 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)par8;
        par5 += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * (double)0.0075f * (double)par8;
        this.motionX = par1 *= (double)par7;
        this.motionY = par3 *= (double)par7;
        this.motionZ = par5 *= (double)par7;
        float var10 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0 / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, var10) * 180.0 / Math.PI);
        this.ticksInGround = 0;
    }

    @Override
    public void setVelocity(double par1, double par3, double par5) {
        this.motionX = par1;
        this.motionY = par3;
        this.motionZ = par5;
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float var7 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(par3, var7) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }
}
