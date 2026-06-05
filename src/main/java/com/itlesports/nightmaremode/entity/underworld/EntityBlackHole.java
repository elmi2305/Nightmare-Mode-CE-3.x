package com.itlesports.nightmaremode.entity.underworld;

import api.entity.EntityWithCustomPacket;
import btw.entity.attribute.BTWAttributes;
import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

public class EntityBlackHole extends EntityLiving implements EntityWithCustomPacket {

    private int lifeTimer = 200;
    private double radius;

    public EntityBlackHole(World world) {
        super(world);
        this.setSize(1.2F, 1.2F);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.renderDistanceWeight = 20.0D;
        this.radius = 10.0D;
    }

    public EntityBlackHole(World world, double rad) {
        this(world);
        this.radius = rad;
    }

    public EntityBlackHole(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y + 0.8D, z);
    }

    public EntityBlackHole(World world, double x, double y, double z, double rad) {
        this(world, x, y, z);
        this.radius = rad;
    }

    @Override
    protected float getSoundVolume() {
        return 0f;
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        return false;
    }

//    @Override
//    public void onLivingUpdate() {
//        super.onLivingUpdate();
//        this.motionX = 0;
//        this.motionY = 0;
//        this.motionZ = 0;
//    }
//
//    @Override
//    public void addVelocity(double par1, double par3, double par5) {}

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.motionX = this.motionY = this.motionZ = 0.0D;

        if (--this.lifeTimer <= 0) {
            this.explode();
            this.setDead();
            return;
        }

        if (this.worldObj.isRemote) {
            this.spawnBlackHoleParticles();
        } else {
            this.pullNearbyEntities();
        }
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {}

    @Override
    public float knockbackMagnitude() {
        return 0f;
    }

    private void spawnBlackHoleParticles() {
        for (int i = 0; i < 18; i++) {
            double ox = (this.rand.nextDouble() - 0.5D) * this.radius * 2.0D;
            double oy = (this.rand.nextDouble() - 0.5D) * this.radius * 1.6D;
            double oz = (this.rand.nextDouble() - 0.5D) * this.radius * 2.0D;
            this.worldObj.spawnParticle("largesmoke", this.posX + ox, this.posY + oy, this.posZ + oz, -ox * 0.1D, -oy * 0.1D, -oz * 0.1D);
            if (this.rand.nextInt(4) == 0) {
                this.worldObj.spawnParticle("portal", this.posX + ox, this.posY + oy, this.posZ + oz, -ox * 0.3D, -oy * 0.3D, -oz * 0.3D);
            }
        }
    }

    private void pullNearbyEntities() {
        AxisAlignedBB box = this.boundingBox.expand(
                this.radius * 3.5D,
                this.radius * 3.35D,
                this.radius * 3.5D
        );
        List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);

        for (EntityLivingBase living : entities) {
            if (living instanceof EntityBlackHole) continue;

            double dx = this.posX - living.posX;
            double dy = this.posY + 0.6D - living.posY;
            double dz = this.posZ - living.posZ;
            double distSq = dx*dx + dy*dy + dz*dz;
            if (distSq < 0.25D) continue;

            double dist = Math.sqrt(distSq);

            double normalizedDist = dist / this.radius;
            normalizedDist = Math.min(1.0D, Math.max(0.0D, normalizedDist));

            double falloff = 1.0D - normalizedDist;


            falloff *= falloff;

            boolean isPlayer = living instanceof EntityPlayer;
            double maxPull = isPlayer ? 0.42D : 1.75D;

            // weak pull even at the edge of the radius
            double minPull = maxPull * 0.04D;

            double pull = minPull + (maxPull - minPull) * falloff;

            dx /= dist;
            dy /= dist;
            dz /= dist;

//            System.out.println((this.worldObj.isRemote ? "CLIENT" : "SERVER") + " " + (living.worldObj.isRemote ? "CLIENT" : "SERVER"));
            if (isPlayer) {
                living.isAirBorne = true;
                living.onGround = false;
                living.velocityChanged = true;

                living.addVelocity(
                        dx * pull,
                        dy * pull * 0.75D + pull * 0.15D,
                        dz * pull
                );
            } else {
                living.isAirBorne = true;
                living.onGround = false;
                living.velocityChanged = true;

                living.motionX += dx * pull;
                living.motionY += dy * pull * 0.75D + pull * 0.15D;
                living.motionZ += dz * pull;
            }


            double chaosRadius = this.radius * 0.35D;

            if (dist < chaosRadius) {
                double proximity = 1.0D - (dist / chaosRadius);

                double spin = 0.08D + proximity * 0.22D;

                living.motionX += -dz * spin;
                living.motionZ +=  dx * spin;

                if (this.rand.nextInt(5) == 0) {
                    double fling = proximity * 0.85D;

                    living.motionX += (this.rand.nextDouble() - 0.5D) * fling;
                    living.motionY += this.rand.nextDouble() * fling;
                    living.motionZ += (this.rand.nextDouble() - 0.5D) * fling;
                }
            }
        }
    }

    private void explode() {
        if (!this.worldObj.isRemote) {
            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 4.5F, true);
            AxisAlignedBB box = this.boundingBox.expand(7.0D, 5.0D, 7.0D);
            List<EntityLivingBase> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
            for (EntityLivingBase e : list) {
                if (e != this) {
                    e.attackEntityFrom(DamageSource.magic, 14.0F);
                }
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        nbt.setInteger("Life", this.lifeTimer);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        this.lifeTimer = nbt.getInteger("Life");
    }

    @Override
    public ItemStack getHeldItem() {
        return null;
    }

    @Override
    public ItemStack getCurrentItemOrArmor(int var1) {
        return null;
    }

    @Override
    public void setCurrentItemOrArmor(int var1, ItemStack var2) {}

    @Override
    public ItemStack[] getLastActiveItems() {
        return new ItemStack[0];
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeInt(NMFields.PACKET_BLACKHOLE);
            dataStream.writeInt(this.entityId);
            dataStream.writeDouble(this.radius);
            new Packet24MobSpawn(this).writePacketData(dataStream);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Packet250CustomPayload("btw|SE", byteStream.toByteArray());
    }

    @Override
    public int getTrackerViewDistance() {
        return 80;
    }

    @Override
    public int getTrackerUpdateFrequency() {
        return 3;
    }

    @Override
    public boolean getTrackMotion() {
        return true;
    }

    @Override
    public boolean shouldServerTreatAsOversized() {
        return false;
    }
}