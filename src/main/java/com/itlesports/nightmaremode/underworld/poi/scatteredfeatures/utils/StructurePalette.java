package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/** numeric source states, with exact metadata rules taking priority over wildcards. */
public final class StructurePalette {
    public static final int ANY_METADATA = -1;
    private final Map<Integer, Replacement> exact = new HashMap<>();
    private final Map<Integer, Replacement> wildcard = new HashMap<>();

    public StructurePalette map(int sourceID, int targetID, int targetMetadata) {
        return map(sourceID, ANY_METADATA, targetID, targetMetadata);
    }

    public StructurePalette map(int sourceID, int sourceMetadata, int targetID, int targetMetadata) {
        return add(sourceID, sourceMetadata, new Replacement(targetID, targetMetadata, 0, false));
    }

    public StructurePalette preserveMetadata(int sourceID, int targetID) {
        return add(sourceID, ANY_METADATA, new Replacement(targetID, 0, 0, true));
    }

    public StructurePalette randomMetadata(int sourceID, int sourceMetadata, int targetID, int min, int maxExclusive) {
        // legacy templates sometimes randomize a wider value before the world masks it to four bits.
        if (min < 0 || min > 15 || maxExclusive > 256 || maxExclusive <= min) {
            throw new IllegalArgumentException("invalid metadata range: " + min + ".." + maxExclusive);
        }
        return add(sourceID, sourceMetadata, new Replacement(targetID, min, maxExclusive, false));
    }

    private StructurePalette add(int sourceID, int sourceMetadata, Replacement replacement) {
        if (sourceID < 0 || sourceID > 4095 || replacement.blockID < 0 || replacement.blockID > 4095
                || sourceMetadata < ANY_METADATA || sourceMetadata > 15
                || replacement.metadata < 0 || replacement.metadata > 15) {
            throw new IllegalArgumentException("invalid structure block ID or metadata");
        }
        Map<Integer, Replacement> rules = sourceMetadata == ANY_METADATA ? wildcard : exact;
        int key = sourceMetadata == ANY_METADATA ? sourceID : (sourceID << 4) | sourceMetadata;
        if (rules.containsKey(key)) {
            throw new IllegalArgumentException("duplicate structure mapping: " + sourceID + ":" + sourceMetadata);
        }
        rules.put(key, replacement);
        return this;
    }

    public Replacement resolve(int sourceID, int sourceMetadata) {
        Replacement result = exact.get((sourceID << 4) | sourceMetadata);
        return result != null ? result : wildcard.get(sourceID);
    }

    public boolean hasExactRule(int sourceID, int sourceMetadata) {
        return exact.containsKey((sourceID << 4) | sourceMetadata);
    }

    public static final class Replacement {
        public final int blockID;
        private final int metadata;
        private final int maxExclusive;
        private final boolean preserve;

        private Replacement(int blockID, int metadata, int maxExclusive, boolean preserve) {
            this.blockID = blockID;
            this.metadata = metadata;
            this.maxExclusive = maxExclusive;
            this.preserve = preserve;
        }

        public int metadata(int sourceMetadata, Random random) {
            return preserve ? sourceMetadata : maxExclusive == 0 ? metadata
                    : metadata + random.nextInt(maxExclusive - metadata);
        }

        public String describe() {
            return blockID + ":" + (preserve ? "source-meta" : maxExclusive == 0 ? Integer.toString(metadata)
                    : "random[" + metadata + "," + maxExclusive + ")");
        }
    }
}
