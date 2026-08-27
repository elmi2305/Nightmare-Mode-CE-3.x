package com.itlesports.nightmaremode.agriculture;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class ChunkAttributes {
    public static final float MAX_VALUE = 100.0F;
    private static final String NBT_KEY = "NMChunkAttributes";
    private static final int DATA_VERSION = 4;
    private static final long MIGRATED_FERTILIZER_DURATION = 24000L;

    private final EnumMap<ChunkAttribute, Float> values = new EnumMap<>(ChunkAttribute.class);
    private final Map<Integer, FertilizerData> farmlandFertilizers = new HashMap<>();
    private boolean initialized;
    private int ownerChunkX;
    private int ownerChunkZ;
    private int ownerDimension;
    private long rollSeed;
    private int fish;
    private int maxFish;
    private boolean fishInitialized;
    private float pollution;
    private byte pollutionVisualBand = -1;
    private float lastClientSyncedPollution = Float.NaN;

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
            long rollSeed,
            int fishCapacity
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
        this.initializeFish(fishCapacity);
        // Pollution is deliberately never rolled from terrain generation.
        this.pollution = 0.0F;
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

    public float getPollution() {
        return this.pollution;
    }

    public void addPollution(float amount) {
        this.pollution = Math.max(0.0F, Math.min(10000.0F, this.pollution + amount));
    }

    /** Used by the client-side pollution visual sync. */
    public void setPollution(float pollution) {
        this.pollution = Math.max(0.0F, Math.min(10000.0F, pollution));
    }

    public byte getPollutionVisualBand() {
        return this.pollutionVisualBand;
    }

    public void setPollutionVisualBand(byte pollutionVisualBand) {
        this.pollutionVisualBand = pollutionVisualBand;
    }

    public float getLastClientSyncedPollution() {
        return this.lastClientSyncedPollution;
    }

    public void setLastClientSyncedPollution(float lastClientSyncedPollution) {
        this.lastClientSyncedPollution = lastClientSyncedPollution;
    }

    public void setFarmlandFertilizer(
            int localX,
            int y,
            int localZ,
            ChunkAttribute attribute,
            long expiresAt
    ) {
        this.farmlandFertilizers.put(
                positionKey(localX, y, localZ),
                new FertilizerData(attribute, expiresAt)
        );
    }

    public FertilizerData getFarmlandFertilizer(int localX, int y, int localZ) {
        return this.farmlandFertilizers.get(positionKey(localX, y, localZ));
    }

    public void clearFarmlandFertilizer(int localX, int y, int localZ) {
        this.farmlandFertilizers.remove(positionKey(localX, y, localZ));
    }

    public boolean hasFishData() {
        return this.fishInitialized;
    }

    public void initializeFish(int capacity) {
        this.maxFish = Math.max(0, capacity);
        this.fish = this.maxFish;
        this.fishInitialized = true;
    }

    public int getFish() {
        return this.fish;
    }

    public int getMaxFish() {
        return this.maxFish;
    }

    public boolean takeFish() {
        if (this.fish <= 0) {
            return false;
        }
        --this.fish;
        return true;
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
        tag.setInteger("Fish", this.fish);
        tag.setInteger("MaxFish", this.maxFish);
        tag.setFloat("Pollution", this.pollution);

        NBTTagList fertilizers = new NBTTagList("FarmlandFertilizers");
        for (Map.Entry<Integer, FertilizerData> entry : this.farmlandFertilizers.entrySet()) {
            NBTTagCompound fertilizerTag = new NBTTagCompound();
            fertilizerTag.setInteger("Position", entry.getKey());
            fertilizerTag.setInteger("Attribute", entry.getValue().attribute.ordinal());
            fertilizerTag.setLong("ExpiresAt", entry.getValue().expiresAt);
            fertilizers.appendTag(fertilizerTag);
        }
        tag.setTag("FarmlandFertilizerData", fertilizers);
        chunkTag.setTag(NBT_KEY, tag);
    }

    public boolean readFromNBT(
            NBTTagCompound chunkTag,
            int chunkX,
            int chunkZ,
            int dimension,
            long worldTime
    ) {
        if (!chunkTag.hasKey(NBT_KEY)) {
            return false;
        }

        NBTTagCompound tag = chunkTag.getCompoundTag(NBT_KEY);
        if (tag.getInteger("Version") < 2
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
        this.pollution = Math.max(0.0F, Math.min(10000.0F, tag.getFloat("Pollution")));
        if (tag.hasKey("MaxFish")) {
            this.maxFish = Math.max(0, tag.getInteger("MaxFish"));
            this.fish = Math.max(0, Math.min(this.maxFish, tag.getInteger("Fish")));
            this.fishInitialized = true;
        } else {
            this.fish = 0;
            this.maxFish = 0;
            this.fishInitialized = false;
        }

        this.farmlandFertilizers.clear();
        if (tag.hasKey("FarmlandFertilizerData")) {
            NBTTagList fertilizerTags = tag.getTagList("FarmlandFertilizerData");
            for (int index = 0; index < fertilizerTags.tagCount(); ++index) {
                NBTTagCompound fertilizerTag = (NBTTagCompound)fertilizerTags.tagAt(index);
                int ordinal = fertilizerTag.getInteger("Attribute");
                if (ordinal >= 0 && ordinal < ChunkAttribute.values().length) {
                    this.farmlandFertilizers.put(
                            fertilizerTag.getInteger("Position"),
                            new FertilizerData(
                                    ChunkAttribute.values()[ordinal],
                                    fertilizerTag.getLong("ExpiresAt")
                            )
                    );
                }
            }
        } else {
            for (int packed : tag.getIntArray("FarmlandFertilizers")) {
                int ordinal = packed & 7;
                if (ordinal < ChunkAttribute.values().length) {
                    this.farmlandFertilizers.put(
                            packed >>> 3,
                            new FertilizerData(
                                    ChunkAttribute.values()[ordinal],
                                    worldTime + MIGRATED_FERTILIZER_DURATION
                            )
                    );
                }
            }
        }
        this.initialized = true;
        return true;
    }

    public static final class FertilizerData {
        private final ChunkAttribute attribute;
        private final long expiresAt;

        private FertilizerData(ChunkAttribute attribute, long expiresAt) {
            this.attribute = attribute;
            this.expiresAt = expiresAt;
        }

        public ChunkAttribute getAttribute() {
            return this.attribute;
        }

        public boolean isExpired(long worldTime) {
            return worldTime >= this.expiresAt;
        }
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
