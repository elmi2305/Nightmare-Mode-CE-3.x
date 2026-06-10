package com.itlesports.nightmaremode.entity.underworld;

import api.entity.EntityWithCustomPacket;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityPortalCore;
import com.itlesports.nightmaremode.entity.EntityBloodAltar;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import static com.itlesports.nightmaremode.block.tileEntities.TileEntityPortalCore.BLOB_SPAWN_HEIGHT;
import static com.itlesports.nightmaremode.util.NMFields.UW_PORTAL_DURATION;

/**
 * Used as the tracker entity for TileEntityPortalCore
 */
public class EntityRitualPortal extends EntityLiving implements EntityWithCustomPacket {

    private TileEntityPortalCore altar;

    // animation stuff
    public float rotationAngle = 0f;
    public float poleExtension = 0f;
    public float pulseScale = 1.0f;
    private int animationTimer = 0;

    // cached altar position for client side rendering fallback
    private int cachedAltarX = 0;
    private int cachedAltarY = 0;
    private int cachedAltarZ = 0;


    public EntityRitualPortal(World world) {
        super(world);
        this.tasks.removeAllTasks();
        this.targetTasks.removeAllTasks();
        setSize(1.5f, 1.5f);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.preventEntitySpawning = true;
    }

    public EntityRitualPortal(World world, TileEntityPortalCore altar) {
        this(world);
        this.altar = altar;
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        return false;
    }
    @Override protected void damageEntity(DamageSource par1DamageSource, float par2) {}
    @Override public void moveEntity(double dMoveX, double dMoveY, double dMoveZ) {}
    @Override public void moveEntityWithHeading(float par1, float par2) {}
    @Override public void moveFlying(float par1, float par2, float par3) {}
    @Override public boolean isPushedByWater() {return false;}
    @Override protected boolean pushOutOfBlocks(double par1, double par3, double par5) {return false;}


    private void tryRelinkAltar() {
        if (worldObj.blockExists(cachedAltarX, cachedAltarY, cachedAltarZ)) {
            TileEntity te = worldObj.getBlockTileEntity(cachedAltarX, cachedAltarY, cachedAltarZ);
            if (te instanceof TileEntityPortalCore) {
                altar = (TileEntityPortalCore) te;
                System.out.println("[RitualPortal] Deferred altar relink successful");
            } else {
                System.out.println("[RitualPortal] No portal core at cached position — dying");
                setDead();
            }
        }
    }

    @Override
    public void onEntityUpdate() {
        if (needsAltarLookup && !worldObj.isRemote) {
            needsAltarLookup = false;
            tryRelinkAltar();
        }

        super.onEntityUpdate();

        // update entity size based on ritual progress
        float growthScale = getGrowthScale();
        float newSize = 1.5f * growthScale * 4;
        if (this.width != newSize || this.height != newSize) {
            this.setSize(newSize, newSize);
        }

        motionX = 0;
        motionY = 0;
        motionZ = 0;

        // animation
        animationTimer++;
        float volatility = getVolatility();
        float anger = getAngerLevel();

        rotationAngle = (rotationAngle + 2.0f * volatility) % 360f;
        poleExtension = (float) (Math.sin(animationTimer * 0.06 * volatility)) * 0.5f + 0.5f;
        pulseScale = (1.0f + getGrowthScale()) * (1.0f + (float) (Math.sin(animationTimer * 0.09 * volatility)) * 0.08f);

        // positional wobble more erratic as ritual progresses
        // adjust y position to account for models rotationpoint offsets
        // model renders with core at 4 and socket at 2 so visual center is around 1

        double yOffset = BLOB_SPAWN_HEIGHT;

        if (altar != null) {
            float wobbleAmount = 0.02f + anger * 0.08f;
            double wobbleX = Math.sin(animationTimer * 0.05 * volatility) * wobbleAmount;
            double wobbleY = Math.cos(animationTimer * 0.07 * volatility) * wobbleAmount * 0.5f;
            double wobbleZ = Math.sin(animationTimer * 0.06 * volatility + 1.0f) * wobbleAmount;

            setPosition(
                altar.xCoord + 0.5 + wobbleX,
                altar.yCoord + 0.5 + wobbleY + yOffset,
                altar.zCoord + 0.5 + wobbleZ
            );
        } else if (cachedAltarX != 0 || cachedAltarY != 0 || cachedAltarZ != 0) {
            System.out.println("using cached altar");
            // fallback use cached altar position for client side wobble
            float wobbleAmount = 0.02f + anger * 0.08f;
            double wobbleX = Math.sin(animationTimer * 0.05 * volatility) * wobbleAmount;
            double wobbleY = Math.cos(animationTimer * 0.07 * volatility) * wobbleAmount * 0.5f;
            double wobbleZ = Math.sin(animationTimer * 0.06 * volatility + 1.0f) * wobbleAmount;

            setPosition(
                cachedAltarX + 0.5 + wobbleX,
                cachedAltarY + 0.5 + wobbleY + yOffset,
                cachedAltarZ + 0.5 + wobbleZ
            );
        }

        // verify the altar core still exists
        if (!worldObj.isRemote && ticksExisted % 40 == 0) {
            if (!altarCoreExists()) {
//                System.out.println("[RitualPortal] Altar core no longer exists - killing entity");
                setDead();
            } else {
//                System.out.println("[RitualPortal] Altar core still exists - entity alive");
            }
        }
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
    protected void fall(float par1) {}

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
            System.out.println("[RitualPortal] writing nbt altar " + altar.xCoord + "," + altar.yCoord + "," + altar.zCoord + ", animtick " + animationTimer);
        } else {
            // write cached position as fallback
            tag.setInteger("AltarX", cachedAltarX);
            tag.setInteger("AltarY", cachedAltarY);
            tag.setInteger("AltarZ", cachedAltarZ);
        }
        tag.setInteger("AnimTick", animationTimer);
    }


    private boolean needsAltarLookup = false;

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        cachedAltarX = tag.getInteger("AltarX");
        cachedAltarY = tag.getInteger("AltarY");
        cachedAltarZ = tag.getInteger("AltarZ");
        animationTimer = tag.getInteger("AnimTick");
        needsAltarLookup = true; // defer - world is not fully ready yet
    }


    public int getAltarX() { return altar != null ? altar.xCoord : cachedAltarX; }
    public int getAltarY() { return altar != null ? altar.yCoord : cachedAltarY; }
    public int getAltarZ() { return altar != null ? altar.zCoord : cachedAltarZ; }

    public TileEntityPortalCore getAltar() {
        return altar;
    }

    public void bindToAltar(TileEntityPortalCore altar) {
        this.altar = altar;
        this.cachedAltarX = altar.xCoord;
        this.cachedAltarY = altar.yCoord;
        this.cachedAltarZ = altar.zCoord;
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
    public boolean shouldServerTreatAsOversized() {return false;}

    // ritual progression

    /** 0 to 1 progress through the ritual 0 calm start 1 maximum rage */
    public float getRitualProgress() {
        boolean client = this.worldObj.isRemote;

        // try to get progress from altar if available server side or client with reference
        if (altar != null && altar.isActive()) {
            return altar.getRitualProgress();
        }

        // fallback - use entity lifetime as proxy for ritual progress
        // this works on both client and server when altar reference is unavailable
        if (!isRitualActive()) {
            return 1.0f; // completed ritual
        }
        float progress = (float) ticksExisted / UW_PORTAL_DURATION;
//        return progress;
        return Math.min(progress, 1.0f);
    }

    /** returns the anger from 0.0f to 1.331f */
    public float getAngerLevel() {
        float progress = getRitualProgress() * 1.1f;
        return progress * progress * progress;
    }

    /** base scale multiplier based on ritual progress grows from 1.0 to 2.5x */
    public float getGrowthScale() {
        return 1.0f + getAngerLevel() * 2.5f;
    }

    /** movement volatility multiplier */
    public float getVolatility() {
        return 1.0f + getAngerLevel() * 1.2f;
    }

    /** tendril extension multiplier */
    public float getTendrilExtension() {
        return 1.0f + getAngerLevel() * 2.0f;
    }

    /** whether the blob should lunge at nearby players - visual only */
    public boolean shouldLunge() {
        // TODO implement better and test
        return getAngerLevel() > 0.6f;
    }

    /** lunge intensity based on anger 0 to 1 */
    public float getLungeIntensity() {
        if (!shouldLunge()) return 0f;
        return (getAngerLevel() - 0.6f) / 0.4f;
    }

    /** check if ritual is active based on entity lifetime fallback for client */
    private boolean isRitualActive() {
        if (altar != null) {
            return altar.isActive();
        }
        // fallback assume active if entity is young enough to be in ritual duration
        return ticksExisted < UW_PORTAL_DURATION;
    }

    /** get debug info for troubleshooting client side issues */
    public String getDebugInfo() {
        return String.format("entity[id=%d, ticks=%d, altar=%s, cachedpos=(%d,%d,%d), progress=%.2f, anger=%.2f]",
            entityId, ticksExisted,
            altar != null ? "valid" : "null",
            cachedAltarX, cachedAltarY, cachedAltarZ,
            getRitualProgress(), getAngerLevel());
    }
}
