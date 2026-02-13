package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import api.world.BlockPos;
import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.NMPostItems;
import com.itlesports.nightmaremode.underworld.poi.LootEntry;
import net.minecraft.src.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class ObsidianSpike extends ComponentScatteredFeature {
    public ObsidianSpike() {
    } // required

    private static final int[] paletteIDs = new int[]{
            Block.jukebox.blockID,
            NMBlocks.mushroomCapYellow.blockID,
            NMBlocks.mushroomCapWhite.blockID,
            NMBlocks.mushroomWallYellow.blockID,
            NMBlocks.mushroomStem.blockID,
            NMBlocks.mushroomStem.blockID,
            Block.hay.blockID,
            NMBlocks.mushroomFloorPartialYellow.blockID,
            NMBlocks.flowerGrass.blockID,
            NMBlocks.mushroomStem.blockID,
            NMBlocks.mushroomStem.blockID,
            NMBlocks.mushroomTopFloorYellow.blockID,
            BTWBlocks.unlitCampfire.blockID,
            NMBlocks.yellowFlowerRoots.blockID,
            NMBlocks.mushroomWallPurple.blockID,
            Block.mobSpawner.blockID,
            NMBlocks.mushInnardsBreakable.blockID,
            Block.blockIron.blockID,
            NMBlocks.mushroomFloorPartialPurple.blockID,
            BTWBlocks.saw.blockID,
            Block.pistonStickyBase.blockID,
            Block.bookShelf.blockID,
            BTWBlocks.hopper.blockID,
    };


    private static int[] meta = new int[]{
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
    };

    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        String path = "structures/spike.nbt";
        placeFromNBT(world, box, path, paletteIDs);
        return true;
    }

    public void placeFromNBT(World world, StructureBoundingBox box, String resourcePath, int[] blockIDs) {
        try {
            InputStream in = NightmareMode.class.getClassLoader().getResourceAsStream(resourcePath);

            if (in == null) {
                System.err.println("NBT resource not found: " + resourcePath);
                return;
            }

            NBTTagCompound root = CompressedStreamTools.readCompressed(in);
            in.close();

            ArrayList<LootEntry> lootPool = new ArrayList<>();
            // lootPool.add(new LootEntry(new ItemStack([ITEM]]), [WEIGHT], [MIN COUNT], [MAX COUNT]));
            lootPool.add(new LootEntry(new ItemStack(NMItems.bloodOrb), 10, 3, 13));
            lootPool.add(new LootEntry(new ItemStack(Item.appleRed), 10, 1, 2));
            lootPool.add(new LootEntry(new ItemStack(BTWItems.brownMushroom), 25, 6, 20));
            lootPool.add(new LootEntry(new ItemStack(BTWItems.redMushroom), 25, 10, 12));
            lootPool.add(new LootEntry(new ItemStack(Item.potion, 1, 8197), 15, 1, 3)); // healing 1
            lootPool.add(new LootEntry(new ItemStack(Item.potion, 1, 16421), 3, 0, 2)); // splash healing 2
            lootPool.add(new LootEntry(new ItemStack(NMItems.bloodIngot), 30, 0, 1));
            lootPool.add(new LootEntry(new ItemStack(NMItems.refinedDiamondIngot), 24, 1, 2));
            lootPool.add(new LootEntry(new ItemStack(Item.diamond), 5, 1, 2));
            lootPool.add(new LootEntry(new ItemStack(NMItems.friedCalamari), 15, 1, 4));
            lootPool.add(new LootEntry(new ItemStack(NMItems.darksunFragment), 15, 1, 3));
            lootPool.add(new LootEntry(new ItemStack(NMPostItems.bloodMoonBottle), 2, 0, 1));
            lootPool.add(new LootEntry(new ItemStack(NMItems.bloodBoots), 1, 0, 1));
            lootPool.add(new LootEntry(new ItemStack(NMItems.bloodShovel), 2, 0, 1));

            // Calculate total weight once (for efficiency)
            int totalWeight = 0;
            for (LootEntry entry : lootPool) {
                totalWeight += entry.weight;
            }

            NBTTagList blocks = root.getTagList("blocks");
            HashSet<BlockPos> lootLocations = new HashSet<>();
            HashSet<BlockPos> spawnerLocations = new HashSet<>();
            for (int i = 0; i < blocks.tagCount(); i++) {
                NBTTagCompound block = (NBTTagCompound) blocks.tagAt(i);

                NBTTagList posTag = block.getTagList("pos");
                int x = ((NBTTagInt) posTag.tagAt(0)).data;
                int y = ((NBTTagInt) posTag.tagAt(1)).data;
                int z = ((NBTTagInt) posTag.tagAt(2)).data;

                int state = block.getInteger("state");
                if (state <= 0 || state >= blockIDs.length) continue;
                int mappedID = getBlockID(state);

                if(mappedID == BTWBlocks.plainCandle.blockID){
                    mappedID = BTWBlocks.coloredCandle[this.structureRand.nextInt(BTWBlocks.coloredCandle.length)].blockID;
                }
                if(mappedID == NMBlocks.bloodChest.blockID){
                    int zPos = this.getZWithOffset(x, z);
                    int yPos = this.getYWithOffset(y);
                    int xPos = this.getXWithOffset(x, z);

                    lootLocations.add(new BlockPos(xPos, yPos, zPos));
                }
                if(mappedID == Block.mobSpawner.blockID){
                    int zPos = this.getZWithOffset(x, z);
                    int yPos = this.getYWithOffset(y);
                    int xPos = this.getXWithOffset(x, z);

                    spawnerLocations.add(new BlockPos(xPos, yPos, zPos));
                }

                this.place(world, mappedID, getMeta(state, mappedID), x, y, z, box);
            }

            for(BlockPos bp : lootLocations){
                TileEntity chest = world.getBlockTileEntity(bp.x, bp.y, bp.z);
                if(chest == null){
                    continue;
                }
                // add the loot
                if (!(chest instanceof TileEntityBloodChest)) {
                    continue;
                }
                TileEntityBloodChest chestTE = (TileEntityBloodChest) chest;

                // Clear the chest first if needed (vanilla dungeons start empty)
                for (int slot = 0; slot < chestTE.getSizeInventory(); slot++) {
                    chestTE.setInventorySlotContents(slot, null);
                }

                int numItems = structureRand.nextInt(7) + 4;
                if(bp.y > 93){
                    // this is just the upper 2 chests
                    numItems += 10;
                }

                for (int itemIndex = 0; itemIndex < numItems; itemIndex++) {
                    if (totalWeight <= 0) break; // No loot defined

                    // Weighted random selection
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
                        toAdd.stackSize = selected.minCount + structureRand.nextInt(selected.maxCount - selected.minCount + 1);

                        // Find a random empty slot (like vanilla randomization)
                        int attempts = 0;
                        int slot = structureRand.nextInt(chestTE.getSizeInventory());
                        while (chestTE.getStackInSlot(slot) != null && attempts < 10) { // Limit attempts to avoid infinite loop
                            slot = structureRand.nextInt(chestTE.getSizeInventory());
                            attempts++;
                        }
                        if (chestTE.getStackInSlot(slot) == null) {
                            chestTE.setInventorySlotContents(slot, toAdd);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getBlockID(int index){
        if(index > paletteIDs.length){
            System.out.println("ID too big for the list");
            return paletteIDs[paletteIDs.length - 1];
        } else{
            return paletteIDs[index];
        }
    }

    private int getMeta(int index, int mappedBlockID){
        if(index > meta.length){
            System.out.println("meta too big for the list");
            return meta[meta.length - 1];
        }

        int valueToReturn = meta[index];
        int special = 0;
        if(valueToReturn < 0){
            // negative metadata calculates the meta as a random number from 0 to positive meta
            valueToReturn = this.structureRand.nextInt(-valueToReturn);
            return valueToReturn;
        }
        if(mappedBlockID == Block.oreIron.blockID){
            special = this.structureRand.nextInt(1,3);
        }
        if(mappedBlockID == BTWBlocks.soulforge.blockID){
            special = this.structureRand.nextInt(4) + 2;
        }

        if(special != 0){
            return special;
        }

        // no special behavior needed
        return meta[index];
    }

    private void place(World world, int blockID, int metadata, int localX, int localY, int localZ, StructureBoundingBox boundingBox) {
        this.placeBlockAtCurrentPosition(world, blockID, metadata, localX, localY, localZ, boundingBox);
    }

    public ObsidianSpike(Random random, int x, int z) {
        super(random, x, 63, z, 64, 64, 64);
    }

    private final Random structureRand = new Random();
}
