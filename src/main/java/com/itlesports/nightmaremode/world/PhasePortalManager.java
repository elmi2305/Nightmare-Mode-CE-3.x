package com.itlesports.nightmaremode.world;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.util.interfaces.PhaseTransitEntity;
import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;

import java.util.List;

public final class PhasePortalManager {
    private static final int PHASE_PORTAL_COOLDOWN = 100;
    public static final String[] COLOR_NAMES = {"black","red","green","brown","blue","purple","cyan","light gray",
            "gray","pink","lime","yellow","light blue","magenta","orange","white"};
    private static final ThreadLocal<PhasePortalData.Endpoint> TRANSFER_TARGET = new ThreadLocal<>();

    private PhasePortalManager() {}

    public static PhasePortalData data(World world) {
        return canonical(world).getData(NightmareMode.PHASE_PORTALS);
    }

    public static void save(World world, PhasePortalData data) { canonical(world).setData(NightmareMode.PHASE_PORTALS, data); }

    public static boolean register(World world, PhasePortalData.Endpoint endpoint) {
        PhasePortalData data = data(world);
        prune(world, data, endpoint.color);
        if (data.getEndpoints(endpoint.color).size() >= 2) return false;
        data.add(endpoint);
        save(world, data);
        return true;
    }

    public static void remove(World world, int dimension, int x, int y, int z) {
        PhasePortalData data = data(world);
        if (data.remove(dimension, x, y, z)) save(world, data);
    }

    public static PhasePortalData.Endpoint find(World world, int color, int x, int y, int z) {
        for (PhasePortalData.Endpoint endpoint : data(world).getEndpoints(color))
            if (endpoint.contains(world.provider.dimensionId, x, y, z)) return endpoint;
        return null;
    }

    public static PhasePortalData.Endpoint counterpart(World world, PhasePortalData.Endpoint source) {
        for (PhasePortalData.Endpoint endpoint : data(world).getEndpoints(source.color)) if (endpoint != source
                && !(endpoint.dimension == source.dimension && endpoint.x == source.x && endpoint.y == source.y && endpoint.z == source.z)) return endpoint;
        return null;
    }

    public static void teleport(Entity entity, PhasePortalData.Endpoint target) {
        if (entity.worldObj.isRemote || entity.timeUntilPortal > 0) return;
        if (entity instanceof PhaseTransitEntity transit && transit.nm$mustLeavePhasePortal()) return;
        if (entity instanceof PhaseTransitEntity transit && entity.dimension == 0) {
            OverworldTierHelper.Region region = OverworldTierHelper.getRegion(entity.worldObj, entity.posX, entity.posZ);
            if (region != OverworldTierHelper.Region.INNER && region != OverworldTierHelper.Region.GREAT_VOID
                    && region != OverworldTierHelper.Region.BEYOND) transit.nightmareMode$setPhaseOrigin(region.ordinal());
        }
        if (entity instanceof PhaseTransitEntity transit) transit.nm$setMustLeavePhasePortal(true);
        entity.timeUntilPortal = Math.max(entity.getPortalCooldown(), PHASE_PORTAL_COOLDOWN);
        double x = target.x + (target.axis == 0 ? 1.0D : 0.5D);
        double z = target.z + (target.axis == 1 ? 1.0D : 0.5D);
        double y = target.y + 0.1D;
        if (entity.dimension == target.dimension) {
            if (entity.worldObj instanceof WorldServer serverWorld)
                serverWorld.theChunkProviderServer.loadChunk(target.x >> 4, target.z >> 4);
            entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
            if (entity instanceof EntityPlayerMP player)
                player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
            return;
        }
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer destination = server.worldServerForDimension(target.dimension);
        destination.theChunkProviderServer.loadChunk(target.x >> 4, target.z >> 4);
        TRANSFER_TARGET.set(target);
        try {
            entity.travelToDimension(target.dimension);
        } finally {
            TRANSFER_TARGET.remove();
        }
    }

    public static PhasePortalData.Endpoint getTransferTarget() { return TRANSFER_TARGET.get(); }

    private static void prune(World world, PhasePortalData data, int color) {
        MinecraftServer server = MinecraftServer.getServer();
        boolean changed = false;
        for (PhasePortalData.Endpoint endpoint : List.copyOf(data.getEndpoints(color))) {
            WorldServer endpointWorld = server.worldServerForDimension(endpoint.dimension);
            endpointWorld.theChunkProviderServer.loadChunk(endpoint.x >> 4, endpoint.z >> 4);
            if (endpointWorld.getBlockId(endpoint.x, endpoint.y, endpoint.z) != NMBlocks.phasePortal.blockID) {
                changed |= data.remove(endpoint.dimension, endpoint.x, endpoint.y, endpoint.z);
            }
        }
        if (changed) save(world, data);
    }

    private static World canonical(World world) {
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer overworld = server == null ? null : server.worldServerForDimension(0);
        return overworld == null ? world : overworld;
    }
}
