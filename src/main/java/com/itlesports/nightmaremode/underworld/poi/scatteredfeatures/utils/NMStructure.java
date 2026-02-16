package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils;

import api.world.BlockPos;
import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
import com.itlesports.nightmaremode.underworld.poi.LootEntry;
import net.minecraft.src.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class NMStructure extends ComponentScatteredFeature {
    public NMStructure(){}
    public NMStructure(Random random, int x, int y, int z, int xSize, int ySize, int zSize) {
        super(random, x, y, z, xSize,ySize,zSize);
    }

    @Override
    public boolean addComponentParts(World var1, Random var2, StructureBoundingBox var3) {
        return false;
    }
    protected int[] paletteIDs = new int[256];
    protected int[] meta = new int[256];
    protected ArrayList<LootEntry> lootPool = new ArrayList<>();
    protected Random structureRand = new Random();

    protected int verticalLootThreshold = 256;


    public void placeFromNBT(World world, StructureBoundingBox box, String resourcePath, int[] blockIDs) {
        try {
            InputStream in = NightmareMode.class.getClassLoader().getResourceAsStream(resourcePath);

            if (in == null) {
                System.err.println("NBT resource not found: " + resourcePath);
                return;
            }

            NBTTagCompound root = CompressedStreamTools.readCompressed(in);
            in.close();

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
                if(bp.y > verticalLootThreshold){
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

    private int getBlockID(int index){
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
}
