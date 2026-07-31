package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import api.world.BlockPos;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.underworld.poi.LootEntry;
import net.minecraft.src.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class NMStructure extends ComponentScatteredFeature {
    public NMStructure() {}

    protected NMStructure(Random random, int x, int y, int z, int xSize, int ySize, int zSize) {
        super(random, x, y, z, xSize, ySize, zSize);
    }

    protected abstract String getStructurePath();

    protected abstract PaletteEntry[] getPalette();

    protected List<LootEntry> getLootPool() {
        return new ArrayList<>();
    }

    protected int getLootContainerBlockID() {
        return Block.chest.blockID;
    }

    protected boolean isLootContainer(int blockID) {
        return blockID == getLootContainerBlockID();
    }

    protected int getLootRollCount(BlockPos position, Random random) {
        return random.nextInt(7) + 4;
    }

    protected void configureSpawner(World world, BlockPos position, TileEntityMobSpawner spawner, Random random) {}

    protected void afterBlockPlaced(World world, BlockPos position, int blockID, int metadata, Random random) {}

    @Override
    public final boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        return placeFromNBT(world, random, box, getStructurePath());
    }

    protected final boolean placeFromNBT(World world, Random random, StructureBoundingBox box, String resourcePath) {
        try (InputStream input = NightmareMode.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                System.err.println("NBT resource not found: " + resourcePath);
                return false;
            }

            NBTTagCompound root = CompressedStreamTools.readCompressed(input);
            PaletteEntry[] palette = getPalette();
            NBTTagList blocks = root.getTagList("blocks");
            Set<BlockPos> lootLocations = new HashSet<>();
            Set<BlockPos> spawnerLocations = new HashSet<>();

            for (int index = 0; index < blocks.tagCount(); index++) {
                NBTTagCompound block = (NBTTagCompound) blocks.tagAt(index);
                NBTTagList posTag = block.getTagList("pos");
                int localX = ((NBTTagInt) posTag.tagAt(0)).data;
                int localY = ((NBTTagInt) posTag.tagAt(1)).data;
                int localZ = ((NBTTagInt) posTag.tagAt(2)).data;
                int state = block.getInteger("state");

                if (state <= 0 || state >= palette.length || palette[state] == null) {
                    continue;
                }

                PaletteEntry entry = palette[state];
                int blockID = entry.getBlockID(this, random);
                int metadata = entry.getMetadata(random, blockID);
                BlockPos worldPosition = getWorldPosition(localX, localY, localZ);

                if (isLootContainer(blockID)) {
                    lootLocations.add(worldPosition);
                }
                if (blockID == Block.mobSpawner.blockID) {
                    spawnerLocations.add(worldPosition);
                }

                placeBlockAtCurrentPosition(world, blockID, metadata, localX, localY, localZ, box);
                if (box.isVecInside(worldPosition.x, worldPosition.y, worldPosition.z)) {
                    afterBlockPlaced(world, worldPosition, blockID, metadata, random);
                }
            }

            if (!world.isRemote) {
                fillLootContainers(world, lootLocations, random);
                configureSpawners(world, spawnerLocations, random);
            }
            return true;
        } catch (Exception exception) {
            System.err.println("Failed to place NBT structure: " + resourcePath);
            exception.printStackTrace();
            return false;
        }
    }

    private BlockPos getWorldPosition(int localX, int localY, int localZ) {
        return new BlockPos(
                getXWithOffset(localX, localZ),
                getYWithOffset(localY),
                getZWithOffset(localX, localZ)
        );
    }

    private void fillLootContainers(World world, Set<BlockPos> locations, Random random) {
        List<LootEntry> lootPool = getLootPool();
        int totalWeight = 0;
        for (LootEntry entry : lootPool) {
            if (entry.weight > 0) {
                totalWeight += entry.weight;
            }
        }

        for (BlockPos position : locations) {
            TileEntity tileEntity = world.getBlockTileEntity(position.x, position.y, position.z);
            if (!(tileEntity instanceof IInventory)) {
                continue;
            }

            IInventory inventory = (IInventory) tileEntity;
            for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                inventory.setInventorySlotContents(slot, null);
            }

            int rolls = getLootRollCount(position, random);
            for (int roll = 0; roll < rolls && totalWeight > 0; roll++) {
                LootEntry selected = selectLootEntry(lootPool, totalWeight, random);
                if (selected == null) {
                    continue;
                }

                ItemStack stack = selected.stack.copy();
                int range = selected.maxCount - selected.minCount + 1;
                stack.stackSize = range > 0 ? selected.minCount + random.nextInt(range) : selected.minCount;
                stack.stackSize = Math.min(stack.stackSize, stack.getMaxStackSize());
                if (stack.stackSize <= 0) {
                    continue;
                }

                int slot = findEmptySlot(inventory, random);
                if (slot >= 0) {
                    inventory.setInventorySlotContents(slot, stack);
                }
            }
            inventory.onInventoryChanged();
        }
    }

    private LootEntry selectLootEntry(List<LootEntry> lootPool, int totalWeight, Random random) {
        int target = random.nextInt(totalWeight);
        int currentWeight = 0;
        for (LootEntry entry : lootPool) {
            if (entry.weight <= 0) {
                continue;
            }
            currentWeight += entry.weight;
            if (target < currentWeight) {
                return entry;
            }
        }
        return null;
    }

    private int findEmptySlot(IInventory inventory, Random random) {
        if (inventory.getSizeInventory() <= 0) {
            return -1;
        }
        int start = random.nextInt(inventory.getSizeInventory());
        for (int offset = 0; offset < inventory.getSizeInventory(); offset++) {
            int slot = (start + offset) % inventory.getSizeInventory();
            if (inventory.getStackInSlot(slot) == null) {
                return slot;
            }
        }
        return -1;
    }

    private void configureSpawners(World world, Set<BlockPos> locations, Random random) {
        for (BlockPos position : locations) {
            TileEntity tileEntity = world.getBlockTileEntity(position.x, position.y, position.z);
            if (tileEntity instanceof TileEntityMobSpawner) {
                configureSpawner(world, position, (TileEntityMobSpawner) tileEntity, random);
            }
        }
    }

    protected interface BlockIDProvider {
        int get(Random random);
    }

    protected interface MetadataProvider {
        int get(Random random, int blockID);
    }

    protected static final class PaletteEntry {
        private final BlockIDProvider blockIDProvider;
        private final MetadataProvider metadataProvider;
        private final boolean lootContainer;

        private PaletteEntry(BlockIDProvider blockIDProvider, MetadataProvider metadataProvider, boolean lootContainer) {
            this.blockIDProvider = blockIDProvider;
            this.metadataProvider = metadataProvider;
            this.lootContainer = lootContainer;
        }

        private int getBlockID(NMStructure structure, Random random) {
            return lootContainer ? structure.getLootContainerBlockID() : blockIDProvider.get(random);
        }

        private int getMetadata(Random random, int blockID) {
            return metadataProvider.get(random, blockID);
        }
    }

    protected static PaletteEntry block(int blockID, int metadata) {
        return block(random -> blockID, (random, resolvedBlockID) -> metadata);
    }

    protected static PaletteEntry block(int blockID, MetadataProvider metadataProvider) {
        return block(random -> blockID, metadataProvider);
    }

    protected static PaletteEntry block(BlockIDProvider blockIDProvider, int metadata) {
        return block(blockIDProvider, (random, resolvedBlockID) -> metadata);
    }

    protected static PaletteEntry block(BlockIDProvider blockIDProvider, MetadataProvider metadataProvider) {
        return new PaletteEntry(blockIDProvider, metadataProvider, false);
    }

    protected static PaletteEntry lootContainer(int metadata) {
        return lootContainer((random, blockID) -> metadata);
    }

    protected static PaletteEntry lootContainer(MetadataProvider metadataProvider) {
        return new PaletteEntry(random -> 0, metadataProvider, true);
    }

    protected static MetadataProvider randomMetadata(int exclusiveMaximum) {
        return (random, blockID) -> random.nextInt(exclusiveMaximum);
    }
}
