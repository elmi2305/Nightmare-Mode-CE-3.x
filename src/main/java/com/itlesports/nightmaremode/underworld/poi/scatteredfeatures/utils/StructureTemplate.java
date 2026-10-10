package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** cached numeric template data; palette indices are only an internal storage detail. */
public final class StructureTemplate {
    private static final Map<String, StructureTemplate> CACHE = new HashMap<>();
    public final List<Entry> blocks;

    private StructureTemplate(NBTTagCompound root) throws IOException {
        if (root.hasKey("palettes")) throw new IOException("multiple structure palettes are unsupported");
        NBTTagList palette = root.getTagList("palette");
        int[][] states = new int[palette.tagCount()][2];
        for (int i = 0; i < states.length; i++) {
            NBTTagCompound state = (NBTTagCompound) palette.tagAt(i);
            if (!state.hasKey("id")) throw new IOException("palette entry " + i
                    + " has no numeric id; import it with tools/structure_palette.py --normalize");
            states[i][0] = state.getInteger("id");
            states[i][1] = state.getInteger("meta");
            validateState(states[i][0], states[i][1]);
        }
        NBTTagList size = root.getTagList("size");
        if (size.tagCount() != 3) throw new IOException("structure size must have three coordinates");
        int[] dimensions = new int[3];
        for (int axis = 0; axis < 3; axis++) {
            dimensions[axis] = ((NBTTagInt) size.tagAt(axis)).data;
            if (dimensions[axis] <= 0) throw new IOException("invalid structure size");
        }
        List<Entry> entries = new ArrayList<>();
        NBTTagList blocks = root.getTagList("blocks");
        for (int i = 0; i < blocks.tagCount(); i++) {
            NBTTagCompound block = (NBTTagCompound) blocks.tagAt(i);
            NBTTagList position = block.getTagList("pos");
            if (position.tagCount() != 3) throw new IOException("block " + i + " has invalid position");
            int[] xyz = new int[3];
            for (int axis = 0; axis < 3; axis++) {
                xyz[axis] = ((NBTTagInt) position.tagAt(axis)).data;
                if (xyz[axis] < 0 || xyz[axis] >= dimensions[axis]) {
                    throw new IOException("block " + i + " is outside the template size");
                }
            }
            int id;
            int metadata;
            int state = block.getInteger("state");
            if (block.hasKey("id")) {
                id = block.getInteger("id");
                metadata = block.getInteger("meta");
            } else {
                if (!block.hasKey("state") || state < 0 || state >= states.length) {
                    throw new IOException("block " + i + " has invalid palette index " + state);
                }
                id = states[state][0];
                metadata = states[state][1];
            }
            validateState(id, metadata);
            // template air has always been omitted; do not cache hundreds of thousands of empty entries.
            if (id != 0) entries.add(new Entry(xyz[0], xyz[1], xyz[2], id, metadata, state));
        }
        this.blocks = Collections.unmodifiableList(entries);
    }

    private static void validateState(int id, int metadata) throws IOException {
        if (id < 0 || id > 4095 || metadata < 0 || metadata > 15) {
            throw new IOException("invalid numeric structure state: " + id + ":" + metadata);
        }
    }

    public static synchronized StructureTemplate load(String resourcePath) throws IOException {
        StructureTemplate template = CACHE.get(resourcePath);
        if (template == null) {
            try (InputStream stream = NightmareMode.class.getClassLoader().getResourceAsStream(resourcePath)) {
                if (stream == null) throw new IOException("structure resource not found: " + resourcePath);
                template = new StructureTemplate(CompressedStreamTools.readCompressed(stream));
            }
            CACHE.put(resourcePath, template);
        }
        return template;
    }

    public static final class Entry {
        public final int x, y, z, blockID, metadata, paletteIndex;

        private Entry(int x, int y, int z, int blockID, int metadata, int paletteIndex) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockID = blockID;
            this.metadata = metadata;
            this.paletteIndex = paletteIndex;
        }
    }
}
