package com.itlesports.nightmaremode.world;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

import java.util.HashSet;
import java.util.Set;

/** Persisted locations of charged chunk-loader blocks across every dimension. */
public class ChunkLoaderData {
    private final Set<LoaderPosition> loaders = new HashSet<>();

    public boolean add(int dimension, int x, int y, int z) {
        return this.loaders.add(new LoaderPosition(dimension, x, y, z));
    }

    public boolean remove(int dimension, int x, int y, int z) {
        return this.loaders.remove(new LoaderPosition(dimension, x, y, z));
    }

    public boolean keepsChunkLoaded(int dimension, int chunkX, int chunkZ) {
        for (LoaderPosition loader : this.loaders) {
            if (loader.dimension == dimension && loader.chunkX == chunkX && loader.chunkZ == chunkZ) {
                return true;
            }
        }
        return false;
    }

    public Set<LoaderPosition> getLoaders() {
        return this.loaders;
    }

    public static ChunkLoaderData readFromNBT(NBTTagCompound tag) {
        ChunkLoaderData data = new ChunkLoaderData();
        NBTTagList list = tag.getTagList("ChunkLoaders");
        for (int index = 0; index < list.tagCount(); ++index) {
            NBTTagCompound entry = (NBTTagCompound)list.tagAt(index);
            int x = entry.getInteger("X");
            int z = entry.getInteger("Z");
            int chunkX = entry.hasKey("ChunkX") ? entry.getInteger("ChunkX") : x >> 4;
            int chunkZ = entry.hasKey("ChunkZ") ? entry.getInteger("ChunkZ") : z >> 4;
            data.loaders.add(new LoaderPosition(entry.getInteger("Dimension"), x, entry.getInteger("Y"), z, chunkX, chunkZ));
        }
        return data;
    }

    public static void writeToNBT(NBTTagCompound tag, ChunkLoaderData data) {
        NBTTagList list = new NBTTagList("ChunkLoaders");
        for (LoaderPosition loader : data.loaders) {
            NBTTagCompound entry = new NBTTagCompound();
            entry.setInteger("Dimension", loader.dimension);
            entry.setInteger("X", loader.x);
            entry.setInteger("Y", loader.y);
            entry.setInteger("Z", loader.z);
            entry.setInteger("ChunkX", loader.chunkX);
            entry.setInteger("ChunkZ", loader.chunkZ);
            list.appendTag(entry);
        }
        tag.setTag("ChunkLoaders", list);
    }

    public static final class LoaderPosition {
        public final int dimension;
        public final int x;
        public final int y;
        public final int z;
        public final int chunkX;
        public final int chunkZ;

        private LoaderPosition(int dimension, int x, int y, int z) {
            this(dimension, x, y, z, x >> 4, z >> 4);
        }

        private LoaderPosition(int dimension, int x, int y, int z, int chunkX, int chunkZ) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof LoaderPosition position)) {
                return false;
            }
            return this.dimension == position.dimension && this.x == position.x && this.y == position.y && this.z == position.z;
        }

        @Override
        public int hashCode() {
            int result = this.dimension;
            result = 31 * result + this.x;
            result = 31 * result + this.y;
            return 31 * result + this.z;
        }
    }
}
