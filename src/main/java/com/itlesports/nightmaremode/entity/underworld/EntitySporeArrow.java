package com.itlesports.nightmaremode.entity.underworld;

import api.entity.EntityWithCustomPacket;
import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.List;

import static com.itlesports.nightmaremode.util.NMFields.VEHICLE_SPORE;

/**
* Explodes into a spore cloud on impact
 */
public class EntitySporeArrow extends EntityArrow implements EntityWithCustomPacket {

    private int ticksInAir;

    public EntitySporeArrow(World world) {
        super(world);
    }
    public EntitySporeArrow(World par1World, double par2, double par4, double par6) {
        super(par1World);
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
        this.setPosition(par2, par4, par6);
        this.yOffset = 0.0f;
    }

    public EntitySporeArrow(World world, EntityLivingBase shooter, float velocity) {
        super(world, shooter, velocity);
        this.setIsCritical(this.rand.nextFloat() < 0.3F); // 30% chance for critical (nice green sparkle)
    }

    @Override
    public void onUpdate() {
        super.onEntityUpdate();



        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f) * 180.0D / Math.PI);
        }

        if (this.worldObj.isRemote) {
            spawnSporeTrail();
        }

        Vec3 start = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
        Vec3 end   = this.worldObj.getWorldVec3Pool().getVecFromPool(
                this.posX + this.motionX,
                this.posY + this.motionY,
                this.posZ + this.motionZ
        );

        MovingObjectPosition mop = this.worldObj.rayTraceBlocks_do_do(start, end, false, true);

        if (mop == null) {
            mop = this.getEntityHit(start, end);
        }

        if(!this.inGround){
            this.ticksInAir++;
        }
        if (mop != null) {
            this.onSporeImpact(mop);
        } else {
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;

            float speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.rotationPitch = (float)(Math.atan2(this.motionY, (double)speed) * 180.0D / Math.PI);

            this.motionX *= 0.99D;
            this.motionY *= 0.99D;
            this.motionZ *= 0.99D;
            this.motionY -= 0.05D; // gravity

            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }


    private void spawnSporeTrail() {
        for (int i = 0; i < 4; i++) {
            double px = this.posX + (this.motionX * i) * 0.25D;
            double py = this.posY + (this.motionY * i) * 0.25D + 0.1D;
            double pz = this.posZ + (this.motionZ * i) * 0.25D;

            this.worldObj.spawnParticle("happyVillager", px, py, pz, 0.0D, 0.05D, 0.0D);
            if (this.rand.nextInt(3) == 0) {
                this.worldObj.spawnParticle("reddust", px, py, pz, 0.2D, 0.8D, 0.1D); // green poison tint
            }
        }
    }


    private MovingObjectPosition getEntityHit(Vec3 start, Vec3 end) {
        List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this,
                this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));

        Entity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : list) {
            if (entity == this.shootingEntity && this.ticksInAir < 5) continue;
            if (!entity.canBeCollidedWith()) continue;

            AxisAlignedBB aabb = entity.boundingBox.expand(0.3D, 0.3D, 0.3D);
            MovingObjectPosition intercept = aabb.calculateIntercept(start, end);

            if (intercept != null) {
                double dist = start.distanceTo(intercept.hitVec);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                }
            }
        }

        return closest != null ? new MovingObjectPosition(closest) : null;
    }


    private void onSporeImpact(MovingObjectPosition mop) {
        if (mop.entityHit != null) {
            if (!mop.entityHit.isEntityInvulnerable()) {
                float damage = (float)(MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ) * 2.0D);
                if (getIsCritical()) damage *= 1.5F;

                DamageSource src = DamageSource.causeArrowDamage(this, this.shootingEntity != null ? this.shootingEntity : this);
                mop.entityHit.attackEntityFrom(src, damage);

                if (mop.entityHit instanceof EntityLivingBase living) {
                    living.addPotionEffect(new PotionEffect(Potion.poison.id, 120, 1)); // Poison II for 6 seconds
                }
            }
        }

        if (!this.worldObj.isRemote) {
            double spawnY = mop.hitVec != null ? mop.hitVec.yCoord + 0.6D : this.posY + 0.6D;

            EntityPollenCloud cloud = new EntityPollenCloud(this.worldObj,
                    mop.hitVec != null ? mop.hitVec.xCoord : this.posX,
                    spawnY,
                    mop.hitVec != null ? mop.hitVec.zCoord : this.posZ,
                    3 + this.rand.nextDouble());

            this.worldObj.spawnEntityInWorld(cloud);

//            System.out.println("DEBUG: SporeArrow exploded into pollen cloud at " + (int) this.posX + ", " + (int) this.posY + ", " + (int) this.posZ);
        }



        this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
        this.setDead();
    }

    @Override
    public Item getCorrespondingItem() {
        return BTWItems.rottenArrow;
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        return new Packet23VehicleSpawn(this, getVehicleSpawnPacketType(), this.shootingEntity == null ? this.entityId : this.shootingEntity.entityId);
    }

    @Override
    public int getTrackerViewDistance() {
        return 64;
    }

    @Override
    public int getTrackerUpdateFrequency() {
        return 20;
    }

    @Override
    public boolean getTrackMotion() {
        return false;
    }

    @Override
    public boolean shouldServerTreatAsOversized() {
        return false;
    }

    public static int getVehicleSpawnPacketType() {
        return VEHICLE_SPORE;
    }
}