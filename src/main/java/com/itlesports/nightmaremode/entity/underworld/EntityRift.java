package com.itlesports.nightmaremode.entity.underworld;

import api.entity.EntityWithCustomPacket;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenBlightlands;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class EntityRift extends EntityLiving implements EntityWithCustomPacket {
    private int destinationDimension = Integer.MIN_VALUE;
    private int destinationX;
    private int destinationY;
    private int destinationZ;
    private String linkId = "";
    private final Map<UUID, Long> recentTransit = new HashMap<>();

    public EntityRift(World world) {
        super(world);
        preventEntitySpawning = true;
        noClip = false;
        setSize(1.0F, 5.0F);
        renderDistanceWeight = 20.0D;
        isImmuneToFire = true;
    }

    public EntityRift(World world, double x, double y, double z) {
        this(world);
        setPositionAndUpdate(x, y, z);
    }

    public void setDestination(int dimension, int x, int y, int z, String identity) {
        destinationDimension = dimension;
        destinationX = x;
        destinationY = y;
        destinationZ = z;
        linkId = identity;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityPlayer) {
        if (!(entityPlayer instanceof EntityPlayerMP player) || worldObj.isRemote) return;
        long now = worldObj.getTotalWorldTime();
        Long last = recentTransit.get(player.getUniqueID());
        if (last != null && now - last < 40L) return;
        recentTransit.put(player.getUniqueID(), now);

        int sourceDimension = player.dimension;
        int sourceX = MathHelper.floor_double(posX);
        int sourceY = MathHelper.floor_double(posY);
        int sourceZ = MathHelper.floor_double(posZ);
        if (destinationDimension == Integer.MIN_VALUE) {
            destinationDimension = sourceDimension == NMFields.UNDERWORLD_DIMENSION ? 0 : NMFields.UNDERWORLD_DIMENSION;
            linkId = UUID.randomUUID().toString();
            if (destinationDimension == NMFields.UNDERWORLD_DIMENSION) {
                int[] landing = findUnderworldLanding(player.mcServer.worldServerForDimension(destinationDimension), sourceX, sourceZ);
                destinationX = landing[0];
                destinationY = landing[1];
                destinationZ = landing[2];
            } else {
                destinationX = sourceX;
                destinationY = sourceY;
                destinationZ = sourceZ;
            }
        }

        player.mcServer.getConfigurationManager().transferPlayerToDimension(player, destinationDimension);
        WorldServer destinationWorld = player.mcServer.worldServerForDimension(destinationDimension);
        if (destinationDimension == NMFields.UNDERWORLD_DIMENSION) buildLanding(destinationWorld, destinationX, destinationY, destinationZ);
        EntityRift returnRift = findLinkedRift(destinationWorld, destinationX, destinationY, destinationZ);
        if (returnRift == null) {
            returnRift = new EntityRift(destinationWorld, destinationX + .5, destinationY, destinationZ + .5);
            returnRift.setDestination(sourceDimension, sourceX, sourceY, sourceZ, linkId);
            destinationWorld.spawnEntityInWorld(returnRift);
        }
        returnRift.recentTransit.put(player.getUniqueID(), destinationWorld.getTotalWorldTime());
        player.setPositionAndUpdate(destinationX + 2.5, destinationY, destinationZ + .5);
        if (NightmareMode.devMode) {
            System.out.println("[Underworld/Rift] " + player.username + " " + sourceDimension + " -> "
                    + destinationDimension + " destination=" + destinationX + "," + destinationY + "," + destinationZ
                    + " link=" + linkId);
        }
    }

    private int[] findUnderworldLanding(WorldServer world, int originX, int originZ) {
        for (int radius = 0; radius <= 512; radius += 16) {
            for (int dx = -radius; dx <= radius; dx += 16) {
                for (int dz = -radius; dz <= radius; dz += 16) {
                    if (radius > 0 && Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    int x = originX + dx;
                    int z = originZ + dz;
                    BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
                    if (!(biome instanceof BiomeGenBlightlands)) continue;
                    int y = world.getTopSolidOrLiquidBlock(x, z) + 1;
                    if (isSafe(world, x, y, z)) return new int[]{x, y, z};
                }
            }
        }
        return new int[]{originX, 100, originZ};
    }

    private boolean isSafe(World world, int x, int y, int z) {
        if (y < 2 || y >= world.getHeight() - 2) return false;
        int ground = world.getBlockId(x, y - 1, z);
        return world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z)
                && ground > 0 && Block.blocksList[ground] != null && Block.blocksList[ground].blockMaterial.blocksMovement();
    }

    private void buildLanding(World world, int x, int y, int z) {
        for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) {
            world.setBlock(x + dx, y - 1, z + dz, NMBlocks.underCobble.blockID, 0, 2);
        }
        for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) {
            world.setBlockToAir(x + dx, y, z + dz);
            world.setBlockToAir(x + dx, y + 1, z + dz);
        }
    }

    private EntityRift findLinkedRift(World world, int x, int y, int z) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x - 8, y - 8, z - 8, x + 9, y + 9, z + 9);
        List<?> entities = world.getEntitiesWithinAABB(EntityRift.class, box);
        for (Object object : entities) {
            EntityRift rift = (EntityRift)object;
            if (linkId.equals(rift.linkId)) return rift;
        }
        return null;
    }

    @Override public boolean isEntityInvulnerable() { return true; }
    @Override protected boolean canDespawn() { return false; }
    @Override protected void despawnEntity() {}
    @Override public boolean doesEntityApplyToSpawnCap() { return false; }
    @Override public boolean isEntityAlive() { return !isDead; }
    @Override public ItemStack getHeldItem() { return null; }
    @Override public void knockBack(Entity entity, float strength, double x, double z) {}
    @Override public float knockbackMagnitude() { return 0.0F; }
    @Override public ItemStack getCurrentItemOrArmor(int slot) { return null; }
    @Override public void setCurrentItemOrArmor(int slot, ItemStack stack) {}
    @Override public ItemStack[] getLastActiveItems() { return new ItemStack[0]; }
    @Override public boolean attackEntityFrom(DamageSource source, float damage) { return false; }
    @Override protected void damageEntity(DamageSource source, float damage) {}
    @Override public void moveEntity(double x, double y, double z) {}
    @Override public void moveEntityWithHeading(float strafe, float forward) {}
    @Override public void moveFlying(float strafe, float forward, float friction) {}
    @Override public boolean isPushedByWater() { return false; }
    @Override protected boolean pushOutOfBlocks(double x, double y, double z) { return false; }
    @Override public boolean canBePushed() { return false; }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("DestinationDimension", destinationDimension);
        nbt.setInteger("DestinationX", destinationX);
        nbt.setInteger("DestinationY", destinationY);
        nbt.setInteger("DestinationZ", destinationZ);
        nbt.setString("LinkId", linkId);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        destinationDimension = nbt.hasKey("DestinationDimension") ? nbt.getInteger("DestinationDimension") : Integer.MIN_VALUE;
        destinationX = nbt.getInteger("DestinationX");
        destinationY = nbt.getInteger("DestinationY");
        destinationZ = nbt.getInteger("DestinationZ");
        linkId = nbt.getString("LinkId");
    }

    @Override
    public Packet getSpawnPacketForThisEntity() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream dataStream = new DataOutputStream(byteStream);
        try {
            dataStream.writeInt(NMFields.PACKET_RIFT);
            dataStream.writeInt(entityId);
            new Packet24MobSpawn(this).writePacketData(dataStream);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return new Packet250CustomPayload("btw|SE", byteStream.toByteArray());
    }

    @Override public int getTrackerViewDistance() { return 80; }
    @Override public int getTrackerUpdateFrequency() { return 3; }
    @Override public boolean getTrackMotion() { return true; }
    @Override public boolean shouldServerTreatAsOversized() { return false; }

    @Override
    public void onUpdate() {
        super.onUpdate();
        motionX = motionY = motionZ = 0.0D;
    }
}
