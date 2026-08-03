package com.itlesports.nightmaremode.world;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;
import net.minecraft.server.MinecraftServer;

/** Server-side helpers for maintaining persistent charged chunk loaders. */
public final class ChunkLoaderManager {
    private ChunkLoaderManager() {
    }

    public static void addLoader(World world, int x, int y, int z) {
        World dataWorld = getCanonicalDataWorld(world);
        ChunkLoaderData data = dataWorld.getData(NightmareMode.CHUNK_LOADERS);
        if (data.add(world.provider.dimensionId, x, y, z)) {
            dataWorld.setData(NightmareMode.CHUNK_LOADERS, data);
        }
        if (world instanceof WorldServer server) {
            server.theChunkProviderServer.loadChunk(x >> 4, z >> 4);
        }
    }

    public static void removeLoader(World world, int x, int y, int z) {
        World dataWorld = getCanonicalDataWorld(world);
        ChunkLoaderData data = dataWorld.getData(NightmareMode.CHUNK_LOADERS);
        if (data.remove(world.provider.dimensionId, x, y, z)) {
            dataWorld.setData(NightmareMode.CHUNK_LOADERS, data);
        }
    }

    public static boolean keepsChunkLoaded(WorldServer world, int chunkX, int chunkZ) {
        return getCanonicalDataWorld(world).getData(NightmareMode.CHUNK_LOADERS)
                .keepsChunkLoaded(world.provider.dimensionId, chunkX, chunkZ);
    }

    public static void loadChargedChunks(WorldServer world) {
        ChunkLoaderData data = getCanonicalDataWorld(world).getData(NightmareMode.CHUNK_LOADERS);
        for (ChunkLoaderData.LoaderPosition loader : data.getLoaders()) {
            if (loader.dimension == world.provider.dimensionId) {
                System.out.println("[ChunkLoader] dimension " + loader.dimension
                        + " loading chunk (" + loader.chunkX + ", " + loader.chunkZ + ")"
                        + " from block (" + loader.x + ", " + loader.y + ", " + loader.z + ")");
                world.theChunkProviderServer.loadChunk(loader.chunkX, loader.chunkZ);
            }
        }
    }

    private static World getCanonicalDataWorld(World world) {
        MinecraftServer server = world instanceof WorldServer worldServer
                ? worldServer.getMinecraftServer()
                : null;
        if (server != null) {
            WorldServer overworld = server.worldServerForDimension(0);
            if (overworld != null) {
                return overworld;
            }
        }
        return world;
    }
}
