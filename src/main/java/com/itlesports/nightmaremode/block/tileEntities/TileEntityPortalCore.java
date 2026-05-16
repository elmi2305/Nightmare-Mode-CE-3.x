package com.itlesports.nightmaremode.block.tileEntities;


import api.block.TileEntityDataPacketHandler;
import com.itlesports.nightmaremode.entity.underworld.EntityRitualPortal;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.underworld.RitualState;
import com.itlesports.nightmaremode.util.underworld.RitualStructureValidator;
import net.minecraft.src.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.itlesports.nightmaremode.util.NMFields.UW_PORTAL_DURATION;

/**
 *   INVALID     ->  periodic structure check -> VALID_IDLE
 *   VALID_IDLE  ->  catalyst inserted        -> ACTIVE
 *   VALID_IDLE  ->  structure broken         -> INVALID
 *   ACTIVE      ->  structure broken         -> FAILED
 *   ACTIVE      ->  RITUAL_DURATION elapsed  -> COMPLETE
 *   FAILED      ->  FAILED_COOLDOWN elapsed  -> INVALID (reset)
 * Client-side fields (beamHeight, pulsePhase) are updated locally and used by TileEntityPortalCoreRenderer
 */
public class TileEntityPortalCore extends TileEntity implements TileEntityDataPacketHandler {

    /** ticks in failed state before resetting to invalid */
    private static final int FAILED_COOLDOWN = 20 * 8;

    /** how often ticks to re-validate structure in invalid valid idle */
    private static final int VALIDATION_INTERVAL = 40;

    /** how far above the core the blob entity spawns */
    public static final double BLOB_SPAWN_HEIGHT = 12.0;

    private RitualState state = RitualState.INVALID;
    private int ritualTicks   = 0;
    private int failedTicks   = 0;

    /**
     * UUIDs of spawned blob entities for persistent tracking across world loads
     * used instead of entity ids which become stale after world reloads
     */
    private Set<UUID> blobEntityUUIDs = new HashSet<>();

    /** current rendered beam height grows to max during active shrinks otherwise */
    public float beamHeight  = 0f;
    public float pulsePhase  = 0f;


    @Override
    public void updateEntity() {
        if (worldObj.isRemote) {
            tickClientEffects();
            return;
        }
        tickServer();
    }


    private void tickServer() {
        long worldTime = worldObj.getTotalWorldTime();

        switch (state) {

            case INVALID:
                if (worldTime % VALIDATION_INTERVAL == 0) {
                    if (RitualStructureValidator.validate(worldObj, xCoord, yCoord, zCoord)) {
                        transitionTo(RitualState.VALID_IDLE);
                    }
                }
                break;

            case VALID_IDLE:
                if (worldTime % VALIDATION_INTERVAL == 0) {
                    if (!RitualStructureValidator.isIntact(worldObj, xCoord, yCoord, zCoord)) {
                        System.out.println("[PortalCore] Structure broken - transitioning to INVALID");
                        transitionTo(RitualState.INVALID);
                    }
                }
                break;

            case ACTIVE:
                    if (worldTime % VALIDATION_INTERVAL == 0) {
                    if (!RitualStructureValidator.isIntact(worldObj, xCoord, yCoord, zCoord)) {
                        System.out.println("[PortalCore] Structure broken during ACTIVE ritual - failing");
                        failRitual();
                        return;
                    }
                }

                if (worldTime % 20 == 0) {
                    if (findPortalEntity() == null) {
                        System.out.println("[PortalCore] Blob entity died during ACTIVE ritual - failing");
                        failRitual();
                        return;
                    }
                }

                if (ritualTicks % 80 == 0) {
                    sustainStorm();
                }

                if (ritualTicks % 55 == 0) {
                    spawnAltarLightning();
                }

                if (ritualTicks % 10 == 0) {
                    spawnRitualParticles();
                }

                ritualTicks++;

                if (ritualTicks >= UW_PORTAL_DURATION) {
                    completeRitual();
                }

                markDirtyAndSync();
                break;

            case FAILED:
                failedTicks++;
                if (failedTicks >= FAILED_COOLDOWN) {
                    failedTicks = 0;
                    transitionTo(RitualState.INVALID);
                }
                break;

            case COMPLETE:
                // todo portal open
                break;
        }
    }


    private void tickClientEffects() {
        if (state == RitualState.ACTIVE) {
            float progress = getRitualProgress();

            // beam grows faster and pulses more intensely as ritual progresses
            beamHeight = Math.min(beamHeight + 1.5f + progress * 2.0f, 255f);
            pulsePhase += 0.05f + progress * 0.1f;
            if (pulsePhase > (float) (Math.PI * 2)) {
                pulsePhase -= (float) (Math.PI * 2);
            }
        } else {
            beamHeight = Math.max(beamHeight - 3f, 0f);
        }
    }


    /**
     * Called when a player right-clicks the core holding an item.
     * Returns true if the catalyst was consumed and the ritual started.
     */
    public boolean tryInsertCatalyst(ItemStack stack) {
        System.out.println("[PortalCore] Catalyst insertion attempt at (" + xCoord + "," + yCoord + "," + zCoord + "), current state: " + state);
        if (state != RitualState.VALID_IDLE) {
            System.out.println("[PortalCore] Catalyst rejected - not in VALID_IDLE state");
            return false;
        }
        if (!isWitherSoul(stack)) {
            System.out.println("[PortalCore] Catalyst rejected - not a wither soul");
            return false;
        }

        System.out.println("[PortalCore] Catalyst accepted - starting ritual");
        startRitual();
        return true;
    }

    private boolean isWitherSoul(ItemStack stack) {
        if (stack == null) return false;
        return stack.itemID == NMItems.witherSoul.itemID;
    }

    private void startRitual() {
        transitionTo(RitualState.ACTIVE);
        ritualTicks = 0;

        sustainStorm();
        spawnBlobEntity();
        markDirtyAndSync();
    }

    private void failRitual() {
        System.out.println("[PortalCore] Ritual failed at (" + xCoord + "," + yCoord + "," + zCoord + ")");
        killBlobEntity();
        transitionTo(RitualState.FAILED);
        failedTicks = 0;

        worldObj.createExplosion(null,
                xCoord + 0.5, yCoord + 0.5, zCoord + 0.5,
                2.0f, false);

        markDirtyAndSync();
    }

    private void completeRitual() {
        killBlobEntity();
        transitionTo(RitualState.COMPLETE);

        System.out.println("we have completed");

        // todo portal open logic here

        markDirtyAndSync();
    }

    /** Called from PortalCoreBlock.breakBlock() */
    public void onCoreRemoved() {
        System.out.println("[PortalCore] Core removed at (" + xCoord + "," + yCoord + "," + zCoord + "), state: " + state);
        if (state == RitualState.ACTIVE) {
            killBlobEntity();
        }
    }


    private void sustainStorm() {
//        system.out.println("[portalcore] sustaining storm at " + xCoord + "," + yCoord + "," + zCoord);
        WorldInfo info = worldObj.getWorldInfo();

        if (!info.isThundering()) {
            info.setThundering(true);
            info.setThunderTime(UW_PORTAL_DURATION * 2);

        }

        if (!info.isRaining()) {
            info.setRaining(true);
            info.setRainTime(UW_PORTAL_DURATION * 2);

        }
    }

    private void spawnAltarLightning() {
        int lx = xCoord + worldObj.rand.nextInt(7) - 3;
        int lz = zCoord + worldObj.rand.nextInt(7) - 3;
        EntityLightningBolt bolt = new EntityLightningBolt(worldObj, lx, yCoord, lz);
        worldObj.addWeatherEffect(bolt);
    }

    private void spawnRitualParticles() {
        float progress = getRitualProgress();

        int particleCount = 1 + (int) (progress * 4);

        for (int i = 0; i < particleCount; i++) {
            double px = xCoord + 0.5 + worldObj.rand.nextDouble() * 2.0 - 1.0;
            double py = yCoord + 1.0 + worldObj.rand.nextDouble() * 3.0;
            double pz = zCoord + 0.5 + worldObj.rand.nextDouble() * 2.0 - 1.0;

            worldObj.spawnParticle("portal", px, py, pz, 0, 0.1, 0);
        }
    }


    private void spawnBlobEntity() {
        double ex = xCoord + 0.5d;
        double ey = yCoord + BLOB_SPAWN_HEIGHT;
        double ez = zCoord + 0.5d;

        System.out.println("[PortalCore] Spawning blob entity at (" + ex + "," + ey + "," + ez + ")");
        EntityRitualPortal blob = new EntityRitualPortal(worldObj, this);
        blob.setPositionAndUpdate(ex, ey, ez);
        worldObj.spawnEntityInWorld(blob);
        blobEntityUUIDs.add(blob.getUniqueID());
        System.out.println("[PortalCore] Blob entity spawned with UUID: " + blob.getUniqueID());
    }

    private void killBlobEntity() {
        System.out.println("[PortalCore] Killing blob entities at (" + xCoord + "," + yCoord + "," + zCoord + ")");
        Set<EntityRitualPortal> blobs = findPortalEntities();
        for (EntityRitualPortal blob : blobs) {
            if (blob != null && !blob.isDead) {
                blob.setDead();
                System.out.println("[PortalCore] Blob entity killed: " + blob.getUniqueID());
            }
        }
        blobEntityUUIDs.clear();
    }

    /**
     * Finds all blob entities by UUID, falling back to a positional search
     * in case UUIDs are stale (world reload, chunk unload, etc.).
     * Returns a validated set of alive entities.
     */
    private Set<EntityRitualPortal> findPortalEntities() {
        Set<EntityRitualPortal> foundEntities = new HashSet<>();

        // first try to find by uuids
        if (blobEntityUUIDs != null && !blobEntityUUIDs.isEmpty()) {
            for (Object entityObj : worldObj.loadedEntityList) {
                if (entityObj instanceof EntityRitualPortal) {
                    EntityRitualPortal entity = (EntityRitualPortal) entityObj;
                    if (blobEntityUUIDs.contains(entity.getUniqueID()) && entity.isEntityAlive()) {
                        foundEntities.add(entity);
                    }
                }
            }
        }

        // positional fallback search within the column above the altar
        if (foundEntities.isEmpty()) {
            AxisAlignedBB searchBox = AxisAlignedBB.getAABBPool().getAABB(
                    xCoord - 2, yCoord,      zCoord - 2,
                    xCoord + 3, yCoord + 12, zCoord + 3);

            List<EntityRitualPortal> positionalFound =
                    worldObj.getEntitiesWithinAABB(EntityRitualPortal.class, searchBox);

            for (EntityRitualPortal blob : positionalFound) {
                if (blob.getAltarX() == xCoord
                        && blob.getAltarY() == yCoord
                        && blob.getAltarZ() == zCoord
                        && blob.isEntityAlive()) {
                    foundEntities.add(blob);
                    blobEntityUUIDs.add(blob.getUniqueID()); // refresh cached uuid
                }
            }
        }

        if (foundEntities.isEmpty()) {
//            System.out.println("[PortalCore] No blob entities found");
        } else {
//            System.out.println("[PortalCore] Found " + foundEntities.size() + " blob entities");
        }

        return foundEntities;
    }

    /**
     * Finds a single portal entity (for compatibility with existing code).
     * Returns the first valid entity or null if none found.
     */
    private EntityRitualPortal findPortalEntity() {
        Set<EntityRitualPortal> entities = findPortalEntities();
        return entities.isEmpty() ? null : entities.iterator().next();
    }


    private void transitionTo(RitualState next) {
        System.out.println("[PortalCore] State transition: " + this.state + " -> " + next + " at (" + xCoord + "," + yCoord + "," + zCoord + ")");
        this.state = next;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    private void markDirtyAndSync() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }


    @Override
    public void invalidate() {
        super.invalidate();
        System.out.println("[PortalCore] Tile entity invalidated - cleaning up blob entities");
        killBlobEntity();
    }

    @Override
    public void readNBTFromPacket(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag);
    }


    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("RitualState",  state.ordinal());
        tag.setInteger("RitualTicks",  ritualTicks);
        tag.setInteger("FailedTicks",  failedTicks);

        if (blobEntityUUIDs != null) {
            NBTTagList list = new NBTTagList();
            for (UUID uuid : blobEntityUUIDs) {
                NBTTagCompound currentTag = new NBTTagCompound();
                currentTag.setString("uuid", uuid.toString());
                list.appendTag(currentTag);
            }
            tag.setTag("blobEntityUUIDs", list);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        int ord = tag.getInteger("RitualState");
        state = RitualState.values()[Math.max(0, Math.min(ord, RitualState.values().length - 1))];
        ritualTicks = tag.getInteger("RitualTicks");
        failedTicks = tag.getInteger("FailedTicks");

        if (blobEntityUUIDs == null) {
            blobEntityUUIDs = new HashSet<>();
        }

        if (tag.hasKey("blobEntityUUIDs")) {
            NBTTagList list = tag.getTagList("blobEntityUUIDs");
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound currentTag = (NBTTagCompound) list.tagAt(i);
                String uuidString = currentTag.getString("uuid");
                if (uuidString != null && !uuidString.isEmpty()) {
                    blobEntityUUIDs.add(UUID.fromString(uuidString));
                }
            }
        }
    }



    public RitualState getState(){ return state; }
    public int getRitualTicks(){ return ritualTicks; }
    public boolean isActive() { return state == RitualState.ACTIVE; }

    /** 0 to 1 progress through the ritual used by renderer for effects */
    public float getRitualProgress() {
        if (state != RitualState.ACTIVE) return 0f;
        return (float) ritualTicks / UW_PORTAL_DURATION;
    }
}