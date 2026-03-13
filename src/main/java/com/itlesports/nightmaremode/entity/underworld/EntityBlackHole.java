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
        AxisAlignedBB box = this.boundingBox.expand(this.radius, this.radius * 0.9D, this.radius);
        List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);

        for (EntityLivingBase living : entities) {
            if (living instanceof EntityBlackHole) continue;

            double dx = this.posX - living.posX;
            double dy = this.posY + 0.6D - living.posY;
            double dz = this.posZ - living.posZ;
            double distSq = dx*dx + dy*dy + dz*dz;
            if (distSq < 0.25D) continue;

            double dist = Math.sqrt(distSq);

            // Stronger pull for players (fixes the "players not affected" issue)
            boolean isPlayer = living instanceof EntityPlayer;
            double pullStrength = isPlayer ? 0.285D : 0.225D;

            double pull = pullStrength / (dist + 0.8D);
//            System.out.println((this.worldObj.isRemote ? "CLIENT" : "SERVER") + " " + (living.worldObj.isRemote ? "CLIENT" : "SERVER"));
            if(isPlayer){
                living.isAirBorne = true;
                living.onGround = false;
//                living.setJumping(true);
                living.velocityChanged = true;
                living.addVelocity((float) (dx*pull), (float) (dy * pull * 0.75D + 0.035D), (float) (dz*pull));
            } else {
                living.motionX += dx * pull;
                living.motionY += dy * pull * 0.75D + 0.035D; // upward bias
                living.motionZ += dz * pull;
            }
            // Close range: spin + violent flinging (orbital chaos until explosion)
            if (dist < 2.7D) {
                // Perpendicular spin force
                double spin = 0.195D;
                living.motionX += -dz * spin;
                living.motionZ +=  dx * spin;

                // Occasional big random flings
                if (this.rand.nextInt(5) == 0) {
                    living.motionX += (this.rand.nextDouble() - 0.5D) * 0.85D;
                    living.motionY += this.rand.nextDouble() * 0.6D + 0.2D;
                    living.motionZ += (this.rand.nextDouble() - 0.5D) * 0.85D;
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