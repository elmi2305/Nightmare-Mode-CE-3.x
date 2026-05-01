package com.itlesports.nightmaremode.entity;

import api.entity.EntityWithCustomPacket;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodBone;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class EntityBloodAltar extends EntityLiving implements EntityWithCustomPacket, IBossDisplayData {
    private TileEntityBloodBone altar;

    public EntityBloodAltar(World world) {
        super(world);
        this.setHealth(this.getMaxHealth());

        this.setSize(0.1f, 0.1f); // Increased size for better tracking
        this.isImmuneToFire = true;
        this.noClip = true;
        this.renderDistanceWeight = 10.0f; // Increased render distance for better tracking
    }

    public void notifyOfSacrifice(){
        if(this.altar == null) return;
        this.altar.sacrifice();
    }

    public TileEntityBloodBone getAltar() {
        return this.altar;
    }


    public void bindToAltar(TileEntityBloodBone altar) {
        this.altar = altar;
        this.setPosition(altar.xCoord + 0.5, altar.yCoord + 0.5, altar.zCoord + 0.5);
    }
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange).setAttribute(0);
        this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth).setAttribute(128);
        this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.attackDamage).setAttribute(0);
    }

    @Override
    public String getEntityName() {
        return I18n.getString("entity.entityBloodAltar.name");
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if((altar == null || altar.isInvalid()) && !this.worldObj.isRemote) {
            this.setDead();
            return;
        }

//        if (this.ticksExisted % 60 == 0) {
//            System.out.println("I'm alive on the " + (worldObj.isRemote ? "CLIENT" : "SERVER"));
//        }
        if(altar == null || !altar.isActive()) return;
        // Sync position
        if (this.ticksExisted % 120 == 0) {
            System.out.println("I'm active on the " + (worldObj.isRemote ? "CLIENT" : "SERVER"));
        }
        this.setPosition(altar.xCoord + 0.5, altar.yCoord + 0.5, altar.zCoord + 0.5);
        this.setHealth(128 - altar.getSuccessfulIncrements());
//        if (this.ticksExisted % 60 == 0) {
//            System.out.println("My position is: " + (altar.xCoord + 0.5) + " " + (altar.yCoord + 0.5) + " " + (altar.zCoord + 0.5));
//        }
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        // clean up altar reference when entity dies
        if(altar != null) {
            altar = null;
        }
    }

    @Override
    protected boolean canDespawn() { return false; }

    @Override
    public boolean canBePushed() { return false; }

    @Override
    public boolean isEntityInvulnerable() {
        return true;
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return true;
    }

    @Override
    public float getShadowSize() {
        return 0.0F;
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeInt(NMFields.PACKET_BLOOD_ALTAR);
            dataStream.writeInt(this.entityId);
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
        return false;
    }

    @Override
    public boolean shouldServerTreatAsOversized() {
        return false;
    }
}