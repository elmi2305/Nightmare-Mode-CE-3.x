package com.itlesports.nightmaremode.entity;

import api.achievement.AchievementEventDispatcher;
import btw.block.BTWBlocks;
import btw.entity.BroadheadArrowEntity;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.mixin.entity.EntityAccessor;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;

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
        AxisAlignedBB var2;
        int var16;
        super.onEntityUpdate();
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, var1) * 180.0 / Math.PI);
        }
        if ((var16 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile)) > 0 && (var2 = Block.blocksList[var16].getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile)) != null && var2.isVecInside(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ))) {
            this.inGround = true;
        }
        if (this.arrowShake > 0) {
            --this.arrowShake;
        }
        if (this.inGround) {
            int var18 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
            int var19 = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
            if (var18 == this.inTile && (var19 == this.inData || var18 == BTWBlocks.detectorBlock.blockID)) {
                ++this.ticksInGround;
                if (this.ticksInGround >= 6000) {
                    this.setDead();
                }
            } else {
                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2f);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2f);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2f);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {
            float var11;
            int var9;
            ++this.ticksInAir;
            Vec3 var17 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
            Vec3 var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition mop = this.worldObj.rayTraceBlocks_do_do(var17, var3, false, true);
            var17 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
            var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            if (mop != null) {
                var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord);
            }
            Entity var5 = null;
            List var6 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            double var7 = 0.0;
            for (var9 = 0; var9 < var6.size(); ++var9) {
                double var14;
                MovingObjectPosition tempMop;
                Entity var10 = (Entity)var6.get(var9);
                if (!var10.canBeCollidedWith() || var10 == this.shootingEntity && this.ticksInAir < 5 || (tempMop = var10.boundingBox.expand(var11 = 0.3f, var11, var11).calculateIntercept(var17, var3)) == null || !((var14 = var17.distanceTo(tempMop.hitVec)) < var7) && var7 != 0.0) continue;
                var5 = var10;
                var7 = var14;
            }
            if (var5 != null) {
                mop = new MovingObjectPosition(var5);
            }
            if (mop != null && mop.entityHit instanceof EntityPlayer hitPlayer) {
                if (hitPlayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(hitPlayer)) {
                    mop = null;
                }
            }
            if (mop != null) {
                if (mop.entityHit != null) {
                    float var21 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    int var22 = MathHelper.ceiling_double_int((double)var21 * this.getDamage());
                    if (this.getIsCritical()) {
                        var22 += this.rand.nextInt(var22 / 2 + 2);
                    }
                    DamageSource var23 = null;
                    var23 = this.shootingEntity == null ? DamageSource.causeArrowDamage(this, this) : DamageSource.causeArrowDamage(this, this.shootingEntity);
                    if (this.isBurning() && !(mop.entityHit instanceof EntityEnderman)) {
                        mop.entityHit.setFire(5);
                    }
                    if (!mop.entityHit.isEntityInvulnerable()) {

                        mop.entityHit.attackEntityFrom(var23, (int)((float)var22 * this.getDamageMultiplier()));

                        if (mop.entityHit instanceof EntityLivingBase hitEntity) {

                            this.damageDone *= var22 * this.getDamageMultiplier();
                            ((EntityAccessor)mop.entityHit).setInvulnerable(false);

                            float xzVector = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                            if (!this.worldObj.isRemote) {
                                hitEntity.setArrowCountInEntity(hitEntity.getArrowCountInEntity() + 1);
                            }
                            if (this.knockbackStrength > 0 && xzVector > 0.0f) {
                                mop.entityHit.addVelocity(this.motionX * (double)this.knockbackStrength * (double)0.6f / (double)xzVector, 0.1, this.motionZ * (double)this.knockbackStrength * (double)0.6f / (double)xzVector);
                            }

                            if (!(this.shootingEntity instanceof EntityPlayer player)) {
                                return;
                            }

                            // Achievement: unique enemies hit
                            if (!this.entitiesHit.contains(hitEntity)) {
                                this.entitiesHit.add(hitEntity);
                                AchievementEventDispatcher.triggerEvent(
                                        NMAchievementEvents.ArrowEnemyHitEvent.class,
                                        player,
                                        this.entitiesHit.size()
                                );
                            }

                            // Achievement: damage
                            AchievementEventDispatcher.triggerEvent(
                                    NMAchievementEvents.ArrowDamageEvent.class,
                                    player,
                                    this.damageDone
                            );

                            // 50% chance to refund magic arrow if not using Infinity
                            if (this.rand.nextBoolean() && !player.capabilities.isCreativeMode) {
                                ItemStack held = player.getHeldItem();
                                if (held != null && EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, held) == 0) {
                                    player.inventory.addItemStackToInventory(new ItemStack(NMItems.magicArrow));
                                }
                            }


                            if (this.shootingEntity != null) {
                                EnchantmentThorns.func_92096_a(this.shootingEntity, hitEntity, this.rand);
                            }
                            if (this.shootingEntity != null && mop.entityHit != this.shootingEntity && mop.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                                ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(6, 0));
                            }
                        }
                        this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));

                    }
                } else {
                    this.xTile = mop.blockX;
                    this.yTile = mop.blockY;
                    this.zTile = mop.blockZ;
                    this.inTile = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
                    this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                    this.notifyCollidingBlockOfImpact();
                    this.motionX = (float)(mop.hitVec.xCoord - this.posX);
                    this.motionY = (float)(mop.hitVec.yCoord - this.posY);
                    this.motionZ = (float)(mop.hitVec.zCoord - this.posZ);
                    float var21 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / (double)var21 * (double)0.05f;
                    this.posY -= this.motionY / (double)var21 * (double)0.05f;
                    this.posZ -= this.motionZ / (double)var21 * (double)0.05f;
                    this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                    this.inGround = true;
                    this.arrowShake = 7;
                    this.setIsCritical(false);
                    if (this.inTile != 0) {
                        Block.blocksList[this.inTile].onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, this);
                    }
                }
            }
            if (this.getIsCritical()) {
                for (var9 = 0; var9 < 4; ++var9) {
                    this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)var9 / 4.0, this.posY + this.motionY * (double)var9 / 4.0, this.posZ + this.motionZ * (double)var9 / 4.0, -this.motionX, -this.motionY + 0.2, -this.motionZ);
                }
            }
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float var21 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.rotationPitch = (float)(Math.atan2(this.motionY, var21) * 180.0 / Math.PI);
            while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
                this.prevRotationPitch -= 360.0f;
            }
            while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
                this.prevRotationPitch += 360.0f;
            }
            while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
                this.prevRotationYaw -= 360.0f;
            }
            while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
                this.prevRotationYaw += 360.0f;
            }
            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f;
            float var24 = 0.99f;
            var11 = 0.05f;
            if (this.isInWater()) {
                for (int var26 = 0; var26 < 4; ++var26) {
                    float var27 = 0.25f;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)var27, this.posY - this.motionY * (double)var27, this.posZ - this.motionZ * (double)var27, this.motionX, this.motionY, this.motionZ);
                }
                var24 = 0.8f;
            }
            this.motionX *= (double)var24;
            this.motionY *= (double)var24;
            this.motionZ *= (double)var24;
            this.motionY -= (double)var11;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
        this.notifyAnyCollidingBlocks();

        if (!this.isDead && this.inGround) {
            for (int i = 0; i < 32; ++i) {
                this.worldObj.spawnParticle("iconcrack_266", this.posX, this.posY, this.posZ, (float)(Math.random() * 2.0 - 1.0) * 0.4f, (float)(Math.random() * 2.0 - 1.0) * 0.4f, (float)(Math.random() * 2.0 - 1.0) * 0.4f);
            }
            this.setDead();
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
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
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
