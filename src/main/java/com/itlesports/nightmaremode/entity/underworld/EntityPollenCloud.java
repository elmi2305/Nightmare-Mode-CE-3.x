package com.itlesports.nightmaremode.entity.underworld;

import api.entity.EntityWithCustomPacket;
import com.itlesports.nightmaremode.entity.creepers.EntityCreeperVariant;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;


public class EntityPollenCloud extends EntityLivingBase implements EntityWithCustomPacket {

    private int lifeTimer = 200;
    private double radius;

    public EntityPollenCloud(World world) {
        super(world);
        this.setSize(0f,0f);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.renderDistanceWeight = 0;
        this.setInvisible(true);
        this.radius = 5d;
    }
    public EntityPollenCloud(World world, double rad) {
        this(world);
        this.radius = rad;
    }

    public EntityPollenCloud(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y + 0.6D, z);
    }
    public EntityPollenCloud(World world, double x, double y, double z, double rad) {
        this(world, x, y, z);
        this.radius = rad;
        System.out.println("set to " + rad);
    }

    @Override
    protected float getSoundVolume() {
        return 0f;
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.motionX = this.motionY = this.motionZ = 0.0D; // never moves


        if (--this.lifeTimer <= 0) {
            this.setDead();
            return;
        }

        if (this.worldObj.isRemote) {
            this.spawnPollenParticles();
        } else {
            this.applyPoisonToNearby();
        }
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5) {}

    @Override
    public float knockbackMagnitude() {
        return 0f;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
    }

    /**
     * Beautiful floral pollen effect:
     * • happyVillager = bright green/yellow sparkles (perfect pollen look)
     * • reddust = subtle poison-green tint on some particles
     */
    private void spawnPollenParticles() {
        int amountOfParticles = MathHelper.floor_double(5.6d * radius); // 28 on 5 radius
        for (int i = 0; i < amountOfParticles; i++) {

            // generate a uniform random point inside a sphere
            double u = this.rand.nextDouble();
            double v = this.rand.nextDouble();
            double w = this.rand.nextDouble();

            double theta = 2.0D * Math.PI * u;
            double phi = Math.acos(2.0D * v - 1.0D);

            double r = this.radius * Math.cbrt(w);

            double sinPhi = Math.sin(phi);

            double ox = r * sinPhi * Math.cos(theta);
            double oy = r * sinPhi * Math.sin(theta);
            double oz = r * Math.cos(phi);

            double px = this.posX + ox;
            double py = this.posY + oy;
            double pz = this.posZ + oz;

            this.worldObj.spawnParticle("happyVillager", px, py, pz, 0.0D, 0.08D, 0.0D);

            if (this.rand.nextInt(3) == 0) {
                this.worldObj.spawnParticle("reddust", px, py, pz, 0.25D, 0.85D, 0.15D);
            }

            if (this.rand.nextInt(4) == 0) {
                this.worldObj.spawnParticle("largesmoke", px, py - 0.3D, pz, 0.0D, 0.02D, 0.0D);
            }
        }
    }

    /**
     * Applies Poison II to everything inside the cloud every tick.
     * Short duration + every-tick refresh = constant damage while inside.
     */
    private void applyPoisonToNearby() {
        AxisAlignedBB box = this.boundingBox.expand(this.radius, this.radius * 0.7D, this.radius);
        List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);

        for (EntityLivingBase living : entities) {

            if (living instanceof EntityCreeperVariant && living.getDistanceSqToEntity(this) < 1.0D) continue;

            if(living.isPotionActive(Potion.poison)) continue;

            living.addPotionEffect(new PotionEffect(Potion.poison.id, 80, 1));
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
    public void setCurrentItemOrArmor(int var1, ItemStack var2) {

    }

    @Override
    public ItemStack[] getLastActiveItems() {
        return new ItemStack[0];
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            EntityPollenCloud par1EntityLivingBase = this;
            dataStream.writeInt(NMFields.PACKET_SPORE);
            dataStream.writeInt(this.entityId);
            dataStream.writeDouble(this.radius);
            new Packet24MobSpawn(par1EntityLivingBase).writePacketData(dataStream);
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