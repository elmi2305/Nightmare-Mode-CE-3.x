package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import api.world.BlockPos;
import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
import com.itlesports.nightmaremode.underworld.poi.LootEntry;
import net.minecraft.src.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class NMStructure extends ComponentScatteredFeature {
    private static final String TRANSFORM_VERSION = "NMTransformVersion";
    private static final String RANDOM_SEED = "NMStructureSeed";
    private int transformVersion;
    private long structureSeed;
    private boolean hasStructureSeed;
    private final Set<Integer> loggedMappings = new HashSet<>();

    public NMStructure() {}

    public NMStructure(Random random, int x, int y, int z, int xSize, int ySize, int zSize) {
        super(random, x, y, z, xSize, ySize, zSize);
        transformVersion = 1;
        structureSeed = random.nextLong();
        hasStructureSeed = true;
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        return false;
    }

    // retained for callers of the old indexed overload; new structures use numeric rules.
    @Deprecated protected int[] paletteIDs = new int[256];
    @Deprecated protected int[] meta = new int[256];
    protected ArrayList<LootEntry> lootPool = new ArrayList<>();
    protected Random structureRand = new Random();
    protected int verticalLootThreshold = 256;

    @Override
    protected void func_143012_a(NBTTagCompound tag) {
        super.func_143012_a(tag);
        tag.setInteger(TRANSFORM_VERSION, transformVersion);
        if (hasStructureSeed) tag.setLong(RANDOM_SEED, structureSeed);
    }

    @Override
    protected void func_143011_b(NBTTagCompound tag) {
        super.func_143011_b(tag);
        transformVersion = tag.getInteger(TRANSFORM_VERSION);
        hasStructureSeed = tag.hasKey(RANDOM_SEED);
        structureSeed = tag.getLong(RANDOM_SEED);
    }

    @Override
    protected int getXWithOffset(int x, int z) {
        if (transformVersion == 0 || coordBaseMode < 0) return super.getXWithOffset(x, z);
        return boundingBox.minX + StructureRotation.x(x, z, scatteredFeatureSizeX, scatteredFeatureSizeZ, coordBaseMode);
    }

    @Override
    protected int getZWithOffset(int x, int z) {
        if (transformVersion == 0 || coordBaseMode < 0) return super.getZWithOffset(x, z);
        return boundingBox.minZ + StructureRotation.z(x, z, scatteredFeatureSizeX, scatteredFeatureSizeZ, coordBaseMode);
    }

    private long positionSeed(World world, int x, int y, int z) {
        long seed = hasStructureSeed ? structureSeed : world.getSeed()
                ^ (long) boundingBox.minX * 341873128712L ^ (long) boundingBox.minZ * 132897987541L;
        return seed ^ (long) x * 3129871L ^ (long) y * 116129781L ^ (long) z * 42317861L;
    }

    public void placeFromNBT(World world, StructureBoundingBox box, String resourcePath, StructurePalette palette) {
        placeTemplate(world, box, resourcePath, palette, null);
    }

    @Deprecated
    public void placeFromNBT(World world, StructureBoundingBox box, String resourcePath, int[] blockIDs) {
        placeTemplate(world, box, resourcePath, null, blockIDs);
    }

    private void placeTemplate(World world, StructureBoundingBox box, String resourcePath,
                               StructurePalette palette, int[] legacyIDs) {
        if (world.isRemote) return;
        try {
            StructureTemplate template = StructureTemplate.load(resourcePath);
            Set<Integer> checkedStates = new HashSet<>();
            // validate all mappings before placing any blocks, including states outside this chunk.
            for (StructureTemplate.Entry entry : template.blocks) {
                if (entry.blockID == 0) continue;
                int key = legacyIDs == null ? (entry.blockID << 4) | entry.metadata : entry.paletteIndex;
                if (!checkedStates.add(key)) continue;
                int target;
                if (legacyIDs != null) {
                    if (entry.paletteIndex <= 0 || entry.paletteIndex >= legacyIDs.length
                            || entry.paletteIndex >= meta.length) {
                        throw new IllegalArgumentException("invalid legacy palette index " + entry.paletteIndex);
                    }
                    target = legacyIDs[entry.paletteIndex];
                } else {
                    StructurePalette.Replacement replacement = palette.resolve(entry.blockID, entry.metadata);
                    if (replacement == null) throw new IllegalArgumentException("unmapped source " + entry.blockID + ":" + entry.metadata);
                    target = replacement.blockID;
                }
                if (target != 0 && (target >= Block.blocksList.length || Block.blocksList[target] == null)) {
                    throw new IllegalArgumentException("unregistered target block " + target);
                }
            }
            int totalWeight = 0;
            for (LootEntry entry : lootPool) totalWeight += entry.weight;
            Set<BlockPos> lootLocations = new LinkedHashSet<>();
            for (StructureTemplate.Entry entry : template.blocks) {
                if (entry.blockID == 0) continue;
                int x = getXWithOffset(entry.x, entry.z);
                int y = getYWithOffset(entry.y);
                int z = getZWithOffset(entry.x, entry.z);
                // never read or populate containers outside the current population box.
                if (!box.isVecInside(x, y, z)) continue;
                structureRand.setSeed(positionSeed(world, entry.x, entry.y, entry.z));
                StructurePalette.Replacement replacement = legacyIDs == null ? palette.resolve(entry.blockID, entry.metadata) : null;
                int target = replacement == null ? legacyIDs[entry.paletteIndex] : replacement.blockID;
                int metadata = replacement == null ? legacyMetadata(entry.paletteIndex, target)
                        : replacement.metadata(entry.metadata, structureRand);
                if (target == BTWBlocks.plainCandle.blockID) {
                    target = BTWBlocks.coloredCandle[structureRand.nextInt(BTWBlocks.coloredCandle.length)].blockID;
                }
                Block targetBlock = target == 0 ? null : Block.blocksList[target];
                Block sourceBlock = entry.blockID < Block.blocksList.length ? Block.blocksList[entry.blockID] : null;
                boolean stairs = StructureBlockRotation.isStairs(sourceBlock) && StructureBlockRotation.isStairs(targetBlock);
                if (stairs && legacyIDs == null) metadata = entry.metadata;
                int localMetadata = metadata & 15;
                metadata = rotateMetadata(targetBlock, localMetadata);
                if (NightmareMode.devMode && loggedMappings.add((entry.blockID << 4) | entry.metadata)) {
                    System.out.println("[Underworld/Palette] " + resourcePath + " source=" + entry.blockID + ":" + entry.metadata
                            + " match=" + (replacement == null ? "legacy-index" : palette.hasExactRule(entry.blockID, entry.metadata) ? "exact" : "wildcard")
                            + " -> " + (replacement == null ? Integer.toString(target) : replacement.describe())
                            + (stairs ? " (stairs retain source metadata)" : "")
                            + " sample=" + target + ":" + localMetadata + " -> " + target + ":" + metadata
                            + " orientation=" + coordBaseMode + " transform=" + transformVersion);
                }
                placeBlockAtCurrentPosition(world, target, metadata, entry.x, entry.y, entry.z, box);
                if (target == NMBlocks.bloodChest.blockID && world.getBlockId(x, y, z) == target) {
                    lootLocations.add(new BlockPos(x, y, z));
                }
            }
            for(BlockPos bp : lootLocations){
                structureRand.setSeed(positionSeed(world, bp.x, bp.y, bp.z));
                TileEntity chest = world.getBlockTileEntity(bp.x, bp.y, bp.z);
                if(chest == null){
                    continue;
                }
                // add the loot
                if (!(chest instanceof TileEntityBloodChest)) {
                    continue;
                }
                TileEntityBloodChest chestTE = (TileEntityBloodChest) chest;

                // clear the chest first if needed (vanilla dungeons start empty)
                for (int slot = 0; slot < chestTE.getSizeInventory(); slot++) {
                    chestTE.setInventorySlotContents(slot, null);
                }

                int numItems = structureRand.nextInt(7) + 4;
                if(bp.y > verticalLootThreshold){
                    // this is just the upper 2 chests
                    numItems += 10;
                }

                for (int itemIndex = 0; itemIndex < numItems; itemIndex++) {
                    if (totalWeight <= 0) break; // no loot defined

                    // weighted random selection
                    int randWeight = structureRand.nextInt(totalWeight);
                    int currentWeight = 0;
                    LootEntry selected = null;
                    for (LootEntry entry : lootPool) {
                        currentWeight += entry.weight;
                        if (randWeight < currentWeight) {
                            selected = entry;
                            break;
                        }
                    }

                    if (selected != null) {
                        ItemStack toAdd = selected.stack.copy();
                        toAdd.stackSize = Math.min(toAdd.getMaxStackSize(), selected.minCount
                                + structureRand.nextInt(selected.maxCount - selected.minCount + 1));
                        if (toAdd.stackSize <= 0) continue;

                        // find a random empty slot (like vanilla randomization)
                        int attempts = 0;
                        int slot = structureRand.nextInt(chestTE.getSizeInventory());
                        while (chestTE.getStackInSlot(slot) != null && attempts < 10) { // limit attempts to avoid infinite loop
                            slot = structureRand.nextInt(chestTE.getSizeInventory());
                            attempts++;
                        }
                        if (chestTE.getStackInSlot(slot) == null) {
                            chestTE.setInventorySlotContents(slot, toAdd);
                        }
                    }
                }
                chestTE.onInventoryChanged();
            }

        } catch (Exception exception) {
            System.err.println("[Underworld/Palette] failed to place " + resourcePath + ": " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private int legacyMetadata(int index, int target) {
        int value = meta[index];
        if (value < 0) return structureRand.nextInt(-value);
        if (target == Block.oreIron.blockID) return structureRand.nextInt(2) + 1;
        if (target == BTWBlocks.soulforge.blockID) return structureRand.nextInt(4) + 2;
        return value;
    }

    private int rotateMetadata(Block block, int metadata) {
        // missing version fields identify saved pieces that used the old transforms.
        if (block == null || transformVersion == 0 || coordBaseMode < 0) return metadata;
        return StructureBlockRotation.rotate(block, metadata, coordBaseMode);
    }
}
