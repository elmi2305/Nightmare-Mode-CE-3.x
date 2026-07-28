package com.itlesports.nightmaremode.agriculture;

import net.minecraft.src.NBTTagCompound;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class ChunkAttributes {
    public static final float MAX_VALUE = 100.0F;
    private static final String NBT_KEY = "NMChunkAttributes";
    private static final int DATA_VERSION = 2;

    private final EnumMap<ChunkAttribute, Float> values = new EnumMap<>(ChunkAttribute.class);
    private final Map<Integer, ChunkAttribute> farmlandFertilizers = new HashMap<>();
    private boolean initialized;
    private int ownerChunkX;
    private int ownerChunkZ;
    private int ownerDimension;
    private long rollSeed;

    public boolean isInitialized() {
        return this.initialized;
    }

    public boolean belongsTo(int chunkX, int chunkZ, int dimension) {
        return this.initialized
                && this.ownerChunkX == chunkX
                && this.ownerChunkZ == chunkZ
                && this.ownerDimension == dimension;
    }

    public long getRollSeed() {
        return this.rollSeed;
    }

    public void initialize(
            EnumMap<ChunkAttribute, Float> initialValues,
            int chunkX,
            int chunkZ,
            int dimension,
            long rollSeed
    ) {
        this.values.clear();
        for (ChunkAttribute attribute : ChunkAttribute.values()) {
            this.values.put(attribute, clamp(initialValues.get(attribute)));
        }
        this.farmlandFertilizers.clear();
        this.ownerChunkX = chunkX;
        this.ownerChunkZ = chunkZ;
        this.ownerDimension = dimension;
        this.rollSeed = rollSeed;
        this.initialized = true;
    }

    public float get(ChunkAttribute attribute) {
        Float value = this.values.get(attribute);
        return value == null ? 0.0F : value;
    }

    public void add(ChunkAttribute attribute, float amount) {
        this.values.put(attribute, clamp(this.get(attribute) + amount));
    }

    public void consume(ChunkAttribute attribute, float amount) {
        this.add(attribute, -amount);
    }

    public void setFarmlandFertilizer(int localX, int y, int localZ, ChunkAttribute attribute) {
        this.farmlandFertilizers.put(positionKey(localX, y, localZ), attribute);
    }

    public ChunkAttribute getFarmlandFertilizer(int localX, int y, int localZ) {
        return this.farmlandFertilizers.get(positionKey(localX, y, localZ));
    }

    public void clearFarmlandFertilizer(int localX, int y, int localZ) {
        this.farmlandFertilizers.remove(positionKey(localX, y, localZ));
    }

    public void writeToNBT(NBTTagCompound chunkTag) {
        if (!this.initialized) {
            return;
        }

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Version", DATA_VERSION);
        tag.setInteger("OwnerChunkX", this.ownerChunkX);
        tag.setInteger("OwnerChunkZ", this.ownerChunkZ);
        tag.setInteger("OwnerDimension", this.ownerDimension);
        tag.setLong("RollSeed", this.rollSeed);
        for (ChunkAttribute attribute : ChunkAttribute.values()) {
            tag.setFloat(attribute.name(), this.get(attribute));
        }

        int[] fertilizers = new int[this.farmlandFertilizers.size()];
        int index = 0;
        for (Map.Entry<Integer, ChunkAttribute> entry : this.farmlandFertilizers.entrySet()) {
            fertilizers[index++] = entry.getKey() << 3 | entry.getValue().ordinal();
        }
        tag.setIntArray("FarmlandFertilizers", fertilizers);
        chunkTag.setTag(NBT_KEY, tag);
    }

    public boolean readFromNBT(NBTTagCompound chunkTag, int chunkX, int chunkZ, int dimension) {
        if (!chunkTag.hasKey(NBT_KEY)) {
            return false;
        }

        NBTTagCompound tag = chunkTag.getCompoundTag(NBT_KEY);
        if (tag.getInteger("Version") < DATA_VERSION
                || tag.getInteger("OwnerChunkX") != chunkX
                || tag.getInteger("OwnerChunkZ") != chunkZ
                || tag.getInteger("OwnerDimension") != dimension) {
            return false;
        }

        this.values.clear();
        for (ChunkAttribute attribute : ChunkAttribute.values()) {
            this.values.put(attribute, clamp(tag.getFloat(attribute.name())));
        }

        this.ownerChunkX = chunkX;
        this.ownerChunkZ = chunkZ;
        this.ownerDimension = dimension;
        this.rollSeed = tag.getLong("RollSeed");
        this.farmlandFertilizers.clear();
        for (int packed : tag.getIntArray("FarmlandFertilizers")) {
            int ordinal = packed & 7;
            if (ordinal < ChunkAttribute.values().length) {
                this.farmlandFertilizers.put(packed >>> 3, ChunkAttribute.values()[ordinal]);
            }
        }
        this.initialized = true;
        return true;
    }

    private static int positionKey(int localX, int y, int localZ) {
        return (y & 255) << 8 | (localZ & 15) << 4 | localX & 15;
    }

    private static float clamp(Float value) {
        if (value == null) {
            return 0.0F;
        }
        return Math.max(0.0F, Math.min(MAX_VALUE, value));
    }
}
