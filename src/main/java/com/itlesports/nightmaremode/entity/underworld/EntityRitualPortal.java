package com.itlesports.nightmaremode.entity.underworld;

import api.entity.EntityWithCustomPacket;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityPortalCore;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * Used as the tracker entity for TileEntityPortalCore, the entry point to the underworld
 */
public class EntityRitualPortal extends EntityLiving implements EntityWithCustomPacket {

    private TileEntityPortalCore altar;

    // animation stuff
    public float rotationAngle = 0f;
    public float poleExtension = 0f;
    public float pulseScale = 1.0f;
    private int animationTimer = 0;


    public EntityRitualPortal(World world) {
        super(world);
        setSize(1.5f, 1.5f);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.preventEntitySpawning = true;
    }

    public EntityRitualPortal(World world, double x, double y, double z, TileEntityPortalCore altar) {
        this(world);
//        setPosition(x, y, z);
//        this.altar = altar;
        this.bindToAltar(altar);
        System.out.println("[RitualPortal] Entity spawned at (" + x + "," + y + "," + z + ") for altar at (" + altar.xCoord + "," + altar.yCoord + "," + altar.zCoord + ")");
    }

    @Override
    protected boolean canDespawn() { return false; }

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
    public void onEntityUpdate() {
        super.onEntityUpdate();

        motionX = 0;
        motionY = 0;
        motionZ = 0;

        // animation
        animationTimer++;
        rotationAngle = (rotationAngle + 2.0f) % 360f;
        poleExtension = (float) (Math.sin(animationTimer * 0.06)) * 0.5f + 0.5f;
        pulseScale    = 1.0f + (float) (Math.sin(animationTimer * 0.09)) * 0.08f;

        // verify the altar core still exists
        if (!worldObj.isRemote && ticksExisted % 40 == 0) {
            if (!altarCoreExists()) {
                System.out.println("[RitualPortal] Altar core no longer exists - killing entity");
                setDead();
            } else {
                System.out.println("[RitualPortal] Altar core still exists - entity alive");
            }
        }
    }

    private boolean altarCoreExists() {
        if (altar == null || altar.isInvalid()) {
            System.out.println("[RitualPortal] Altar reference is null or invalid");
            return false;
        }

        int blockID = worldObj.getBlockId(altar.xCoord, altar.yCoord, altar.zCoord);
        if (blockID == 0) {
            System.out.println("[RitualPortal] Altar core check: no block at (" + altar.xCoord + "," + altar.yCoord + "," + altar.zCoord + ")");
            return false;
        }
        boolean exists = blockID == NMBlocks.portalCore.blockID;
        System.out.println("[RitualPortal] Altar core check at (" + altar.xCoord + "," + altar.yCoord + "," + altar.zCoord + "): " + (exists ? "EXISTS" : "MISSING"));
        return exists;
    }


    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);

        if (altar != null) {
            tag.setInteger("AltarX", altar.xCoord);
            tag.setInteger("AltarY", altar.yCoord);
            tag.setInteger("AltarZ", altar.zCoord);
            System.out.println("[RitualPortal] Writing NBT: altar=(" + altar.xCoord + "," + altar.yCoord + "," + altar.zCoord + "), animTick=" + animationTimer);
        }
        tag.setInteger("AnimTick", animationTimer);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);

        int x = tag.getInteger("AltarX");
        int y = tag.getInteger("AltarY");
        int z = tag.getInteger("AltarZ");
        animationTimer = tag.getInteger("AnimTick");

        System.out.println("[RitualPortal] Reading NBT: altar=(" + x + "," + y + "," + z + "), animTick=" + animationTimer);

        // UNTESTED, it tries to find the altar nearby TODO: test this
        if (worldObj != null && worldObj.blockExists(x, y, z)) {
            TileEntity te = worldObj.getBlockTileEntity(x, y, z);
            if (te instanceof TileEntityPortalCore) {
                altar = (TileEntityPortalCore) te;
                System.out.println("[RitualPortal] Found altar tile entity by coordinates");
            } else {
                System.out.println("[RitualPortal] Could not find altar tile entity at stored coordinates");
            }
        }
    }


    public int getAltarX() { return altar != null ? altar.xCoord : 0; }
    public int getAltarY() { return altar != null ? altar.yCoord : 0; }
    public int getAltarZ() { return altar != null ? altar.zCoord : 0; }

    public TileEntityPortalCore getAltar() {
        return altar;
    }

    public void bindToAltar(TileEntityPortalCore altar) {
        this.altar = altar;
        setPosition(altar.xCoord + 0.5, altar.yCoord + 0.5, altar.zCoord + 0.5);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (altar != null) {
            altar = null;
        }
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            EntityRitualPortal par1EntityLivingBase = this;
            dataStream.writeInt(NMFields.PACKET_RITUAL_ENTITY);
            dataStream.writeInt(this.entityId);
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
        return false;
    }

    @Override
    public boolean shouldServerTreatAsOversized() {
        return false;
    }
}
