package com.itlesports.nightmaremode.world;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;

/** Server-side helpers for maintaining persistent charged chunk loaders. */
public final class ChunkLoaderManager {
    private ChunkLoaderManager() {
    }

    public static void addLoader(World world, int x, int y, int z) {
        ChunkLoaderData data = world.getData(NightmareMode.CHUNK_LOADERS);
        if (data.add(world.provider.dimensionId, x, y, z)) {
            world.setData(NightmareMode.CHUNK_LOADERS, data);
        }
        if (world instanceof WorldServer server) {
            server.theChunkProviderServer.loadChunk(x >> 4, z >> 4);
        }
    }

    public static void removeLoader(World world, int x, int y, int z) {
        ChunkLoaderData data = world.getData(NightmareMode.CHUNK_LOADERS);
        if (data.remove(world.provider.dimensionId, x, y, z)) {
            world.setData(NightmareMode.CHUNK_LOADERS, data);
        }
    }

    public static boolean keepsChunkLoaded(WorldServer world, int chunkX, int chunkZ) {
        return world.getData(NightmareMode.CHUNK_LOADERS).keepsChunkLoaded(world.provider.dimensionId, chunkX, chunkZ);
    }

    public static void loadChargedChunks(WorldServer world) {
        ChunkLoaderData data = world.getData(NightmareMode.CHUNK_LOADERS);
        for (ChunkLoaderData.LoaderPosition loader : data.getLoaders()) {
            if (loader.dimension == world.provider.dimensionId) {
                System.out.println("[ChunkLoader] dimension " + loader.dimension
                        + " loading chunk (" + loader.chunkX + ", " + loader.chunkZ + ")"
                        + " from block (" + loader.x + ", " + loader.y + ", " + loader.z + ")");
                world.theChunkProviderServer.loadChunk(loader.chunkX, loader.chunkZ);
            }
        }
    }
}
