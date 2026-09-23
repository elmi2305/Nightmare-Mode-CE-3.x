package com.itlesports.nightmaremode.util;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WitherRitualTemplate {
    public static final int WITHER = 0;
    public static final int BLOOD_WITHER = 1;

    private static final String[] PATHS = {
            "structures/witherRitual.nbt",
            "structures/bloodWitherRitual.nbt"
    };
    private static final Template[] TEMPLATES = new Template[PATHS.length];
    private static final boolean[] LOAD_ATTEMPTED = new boolean[PATHS.length];

    private WitherRitualTemplate() {}

    public static boolean matches(World world, int x, int y, int z, boolean alongX, int ritual) {
        Template template = getTemplate(ritual);
        if (template == null) return false;

        for (int rotation = 0; rotation < 4; rotation++) {
            if ((template.alongX ^ ((rotation & 1) != 0)) != alongX) continue;
            if (template.matches(world, x, y, z, rotation)) return true;
        }
        return false;
    }

    public static int build(World world, int x, int floorY, int z, int rotation, int ritual) {
        Template template = getTemplate(ritual);
        if (template == null) throw new IllegalStateException("wither ritual template could not be loaded");
        int skullY = floorY + template.skullY;
        if (floorY < 0 || floorY + template.topY >= world.getHeight() || skullY < 60 || skullY > 200) {
            throw new IllegalArgumentException("ritual skulls must be between Y 60 and 200, with room for the whole structure");
        }
        return template.build(world, x, floorY, z, rotation);
    }

    private static synchronized Template getTemplate(int ritual) {
        if (ritual < 0 || ritual >= PATHS.length) return null;
        if (!LOAD_ATTEMPTED[ritual]) {
            LOAD_ATTEMPTED[ritual] = true;
            TEMPLATES[ritual] = load(PATHS[ritual], ritual);
        }
        return TEMPLATES[ritual];
    }

    private static Template load(String path, int ritual) {
        try (InputStream input = NightmareMode.class.getClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                System.err.println("Wither ritual NBT resource not found: " + path);
                return null;
            }

            NBTTagCompound root = CompressedStreamTools.readCompressed(input);
            NBTTagList palette = root.getTagList("palette");
            NBTTagList blocks = root.getTagList("blocks");
            if (palette.tagCount() == 0 || blocks.tagCount() == 0) {
                throw new IllegalArgumentException("missing palette or blocks");
            }

            BlockState[] states = new BlockState[palette.tagCount()];
            for (int i = 0; i < states.length; i++) {
                NBTTagCompound entry = (NBTTagCompound) palette.tagAt(i);
                if (!entry.hasKey("blockID") || !entry.hasKey("metadata")) {
                    throw new IllegalArgumentException("missing numeric block state at palette index " + i);
                }
                states[i] = new BlockState(entry.getInteger("blockID"), entry.getInteger("metadata"));
            }

            List<TemplateBlock> entries = new ArrayList<>();
            Map<String, TemplateBlock> positions = new HashMap<>();
            for (int i = 0; i < blocks.tagCount(); i++) {
                NBTTagCompound block = (NBTTagCompound) blocks.tagAt(i);
                NBTTagList pos = block.getTagList("pos");
                int state = block.getInteger("state");
                if (pos.tagCount() != 3 || state < 0 || state >= states.length) {
                    throw new IllegalArgumentException("invalid block position or palette state at index " + i);
                }
                int px = ((NBTTagInt) pos.tagAt(0)).data;
                int py = ((NBTTagInt) pos.tagAt(1)).data;
                int pz = ((NBTTagInt) pos.tagAt(2)).data;
                TemplateBlock entry = new TemplateBlock(px, py, pz, state);
                entries.add(entry);
                if (positions.put(key(px, py, pz), entry) != null) {
                    throw new IllegalArgumentException("duplicate block position at index " + i);
                }
            }

            TemplateBlock center = null;
            boolean alongX = false;
            int boneID = ritual == BLOOD_WITHER ? NMBlocks.bloodBones.blockID : BTWBlocks.aestheticOpaque.blockID;
            int boneMetadata = ritual == BLOOD_WITHER ? 0 : 15;
            for (TemplateBlock entry : entries) {
                if (!states[entry.state].isSkull()) continue;
                for (int axis = 0; axis < 2; axis++) {
                    int dx = axis == 0 ? 1 : 0;
                    int dz = axis == 0 ? 0 : 1;
                    TemplateBlock left = positions.get(key(entry.x - dx, entry.y, entry.z - dz));
                    TemplateBlock right = positions.get(key(entry.x + dx, entry.y, entry.z + dz));
                    TemplateBlock body = positions.get(key(entry.x, entry.y - 1, entry.z));
                    TemplateBlock stem = positions.get(key(entry.x, entry.y - 2, entry.z));
                    TemplateBlock bodyLeft = positions.get(key(entry.x - dx, entry.y - 1, entry.z - dz));
                    TemplateBlock bodyRight = positions.get(key(entry.x + dx, entry.y - 1, entry.z + dz));
                    if (left == null || right == null || body == null || stem == null || bodyLeft == null || bodyRight == null) continue;
                    if (!states[left.state].isSkull() || !states[right.state].isSkull()) continue;
                    if (!states[body.state].is(boneID, boneMetadata)
                            || !states[stem.state].is(boneID, boneMetadata)
                            || !states[bodyLeft.state].is(boneID, boneMetadata)
                            || !states[bodyRight.state].is(boneID, boneMetadata)) continue;
                    if (center != null) throw new IllegalArgumentException("multiple wither T shapes in schematic");
                    center = entry;
                    alongX = axis == 0;
                }
            }
            if (center == null) throw new IllegalArgumentException("no wither T shape in schematic");

            int skullCount = 0;
            int boneCount = 0;
            for (TemplateBlock entry : entries) {
                if (states[entry.state].isSkull()) skullCount++;
                if (states[entry.state].is(boneID, boneMetadata)) boneCount++;
            }
            if (skullCount != 3 || boneCount != 4) {
                throw new IllegalArgumentException("schematic must contain exactly three skulls and four bones");
            }

            List<RequiredBlock> required = new ArrayList<>();
            List<RequiredBlock> allBlocks = new ArrayList<>();
            int topY = 0;
            for (TemplateBlock entry : entries) {
                BlockState state = states[entry.state];
                int dx = entry.x - center.x;
                int dy = entry.y - center.y;
                int dz = entry.z - center.z;
                RequiredBlock block = new RequiredBlock(dx, dy, dz, state);
                allBlocks.add(block);
                if (state.id != 0) required.add(block);
                if (entry.y > topY) topY = entry.y;
            }
            if (required.size() <= 7) throw new IllegalArgumentException("schematic contains no surrounding ritual blocks");
            allBlocks.sort(Comparator.comparingInt(block -> block.y));
            return new Template(alongX, center.y, topY, required, allBlocks);
        } catch (Exception exception) {
            System.err.println("Failed to load wither ritual NBT: " + path);
            exception.printStackTrace();
            return null;
        }
    }

    private static String key(int x, int y, int z) {
        return x + "," + y + "," + z;
    }

    private static final class TemplateBlock {
        final int x, y, z, state;

        TemplateBlock(int x, int y, int z, int state) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.state = state;
        }
    }

    private static final class BlockState {
        final int id, metadata;

        BlockState(int id, int metadata) {
            this.id = id;
            this.metadata = metadata;
        }

        boolean is(int blockID, int blockMetadata) {
            return id == blockID && metadata == blockMetadata;
        }

        boolean isSkull() {
            return id == Block.skull.blockID;
        }

        boolean matches(World world, int x, int y, int z) {
            return world.getBlockId(x, y, z) == id
                    && (isSkull() ? world.getBlockTileEntity(x, y, z) instanceof TileEntitySkull
                    && ((TileEntitySkull) world.getBlockTileEntity(x, y, z)).getSkullType() == 5
                    : world.getBlockMetadata(x, y, z) == metadata);
        }
    }

    private static final class RequiredBlock {
        final int x, y, z;
        final BlockState state;

        RequiredBlock(int x, int y, int z, BlockState state) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.state = state;
        }
    }

    private static final class Template {
        final boolean alongX;
        final int skullY;
        final int topY;
        final List<RequiredBlock> blocks;
        final List<RequiredBlock> allBlocks;

        Template(boolean alongX, int skullY, int topY, List<RequiredBlock> blocks, List<RequiredBlock> allBlocks) {
            this.alongX = alongX;
            this.skullY = skullY;
            this.topY = topY;
            this.blocks = blocks;
            this.allBlocks = allBlocks;
        }

        boolean matches(World world, int x, int y, int z, int rotation) {
            for (RequiredBlock block : blocks) {
                if (!block.state.matches(world, x + rotatedX(block.x, block.z, rotation),
                        y + block.y, z + rotatedZ(block.x, block.z, rotation))) return false;
            }
            return true;
        }

        int build(World world, int x, int floorY, int z, int rotation) {
            int placed = 0;
            for (RequiredBlock block : allBlocks) {
                if (block.state.id != 0) continue;
                int px = x + rotatedX(block.x, block.z, rotation);
                int py = floorY + skullY + block.y;
                int pz = z + rotatedZ(block.x, block.z, rotation);
                if (world.getBlockId(px, py, pz) != 0) world.setBlock(px, py, pz, 0, 0, 2);
            }
            for (RequiredBlock block : allBlocks) {
                if (block.state.id == 0) continue;
                int px = x + rotatedX(block.x, block.z, rotation);
                int py = floorY + skullY + block.y;
                int pz = z + rotatedZ(block.x, block.z, rotation);
                if (world.getBlockId(px, py, pz) != block.state.id
                        || world.getBlockMetadata(px, py, pz) != block.state.metadata) {
                    world.setBlock(px, py, pz, block.state.id, block.state.metadata, 2);
                }
                if (block.state.isSkull()) {
                    TileEntity tileEntity = world.getBlockTileEntity(px, py, pz);
                    if (!(tileEntity instanceof TileEntitySkull)) {
                        tileEntity = new TileEntitySkull();
                        world.setBlockTileEntity(px, py, pz, tileEntity);
                    }
                    TileEntitySkull skull = (TileEntitySkull) tileEntity;
                    skull.setSkullType(5, "");
                    skull.setSkullRotation(0);
                    skull.onInventoryChanged();
                    world.markBlockForUpdate(px, py, pz);
                }
                placed++;
            }
            return placed;
        }

        private static int rotatedX(int x, int z, int rotation) {
            switch (rotation) {
                case 1: return -z;
                case 2: return -x;
                case 3: return z;
                default: return x;
            }
        }

        private static int rotatedZ(int x, int z, int rotation) {
            switch (rotation) {
                case 1: return x;
                case 2: return -z;
                case 3: return -x;
                default: return z;
            }
        }
    }
}
