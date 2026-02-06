package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import api.world.BlockPos;
import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityBloodChest;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.underworld.poi.LootEntry;
import net.minecraft.src.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class BigMushroom extends ComponentScatteredFeature {
    public BigMushroom() {} // required

    public BigMushroom(Random random, int x, int z) {
        super(random, x, 63, z, 64,64,64);
    }
    private final Random structureRand = new Random();


    private static final int[] paletteIDs = new int[]{
            Block.jukebox.blockID, // storage
            NMBlocks.mushroomCapYellow.blockID,
            NMBlocks.mushroomCapWhite.blockID,
            NMBlocks.mushroomWallYellow.blockID,
            NMBlocks.mushroomStem.blockID, // base of the plant
            NMBlocks.mushroomStem.blockID, // inner mushiness
            Block.hay.blockID,
            NMBlocks.mushroomFloorPartialYellow.blockID, // actual
            NMBlocks.flowerGrass.blockID,
            NMBlocks.mushroomStem.blockID, // underside gills
            NMBlocks.mushroomStem.blockID, // the ceiling of the top part which used to be netherrack
            NMBlocks.mushroomTopFloorYellow.blockID,
            BTWBlocks.unlitCampfire.blockID, // actual
            NMBlocks.yellowFlowerRoots.blockID, // actual
            NMBlocks.mushroomWallPurple.blockID, // actual
            Block.mobSpawner.blockID, // ambient in rooms
            NMBlocks.mushInnardsBreakable.blockID, // actual
            Block.blockIron.blockID, // simply the wall on the top part between sections
            NMBlocks.mushroomFloorPartialPurple.blockID, // actual
            BTWBlocks.saw.blockID,
            Block.pistonStickyBase.blockID, // storage ig
            Block.bookShelf.blockID, // storage
            BTWBlocks.hopper.blockID,
            Block.chest.blockID, // decor
            Block.web.blockID,
            BTWBlocks.pulley.blockID, // storage
            BTWBlocks.aestheticOpaque.blockID, // barrel storage
            BTWBlocks.gearBox.blockID, // storage
            Block.flowerPot.blockID, // pot in storage
            NMBlocks.mushroomTopFloorPurple.blockID, // actual
            Block.fence.blockID,
            Block.waterStill.blockID, // farm area
            Block.cloth.blockID,
            BTWBlocks.blockDispenser.blockID, // bd in storage
            Block.music.blockID, // storage
            Block.blockNetherQuartz.blockID, // table thing
            BTWBlocks.plainCandle.blockID, // enchanting area
            BTWBlocks.quartzSidingAndCorner.blockID, // bench
            Block.jukebox.blockID, // actual
            Block.tnt.blockID, // actual
            BTWBlocks.dragonVessel.blockID, // storage
            NMBlocks.bloodChest.blockID, // loot
            Block.mobSpawner.blockID, // spawner in the storage TODO
            Block.pistonBase.blockID, // storage
            Block.blockIron.blockID, // top reward
            Block.blockGold.blockID, // top reward
            Block.blockDiamond.blockID, // reward room top
            BTWBlocks.planter.blockID, // soul sand planter
            Block.netherStalk.blockID,
            BTWBlocks.turntable.blockID, // storage
            Block.stoneSingleSlab.blockID, // slab at the start
            BTWBlocks.quartzMouldingAndDecorative.blockID, // needs meta 8, it's the brewing stand table
            Block.brewingStand.blockID,
            Block.cauldron.blockID, // cauldron for the brewing stand, needs water meta chance
            NMBlocks.mushInnardsBreakableExplosive.blockID, // actual
            NMBlocks.mushBookshelf.blockID, // for enchanting
            Block.enchantmentTable.blockID, // enchanting
            BTWBlocks.soulforge.blockID, // storage
            Block.brick.blockID, // actual forge
            BTWBlocks.plainCandle.blockID, //
            BTWBlocks.idleOven.blockID, // needs facing meta
            BTWBlocks.vase.blockID, // forge
            NMBlocks.disenchantmentTable.blockID,
            BTWBlocks.planter.blockID, // empty planter
            Block.stoneSingleSlab.blockID, // actual
            Block.oreIron.blockID, // actual
            Block.anvil.blockID, // actual
            BTWBlocks.workStump.blockID, // stump in alchemy room
            Block.rail.blockID // in forge
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
            0,
            0, // Block.mobSpawner.blockID,
            0,
            0,
            0,
            -6, // BTWBlocks.saw.blockID,
            -6, // Block.pistonStickyBase.blockID,
            0,
            0,
            -6, //Block.chest.blockID,
            0,
            0,
            11, //BTWBlocks.aestheticOpaque.blockID, // barrel storage
            -6,// BTWBlocks.gearBox.blockID, // storage
            -3, // Block.flowerPot.blockID, // pot in storage
            0,
            0,
            0,
            7, // Block.cloth.blockID,
            -6, // BTWBlocks.blockDispenser.blockID, // bd in storage
            0,
            0, // Block.blockNetherQuartz.blockID, // table thing
            -4, // BTWBlocks.plainCandle.blockID, // enchanting area
            12, // BTWBlocks.quartzSidingAndCorner.blockID, // bench
            0,
            0,
            0,
            -6, // NMBlocks.bloodChest.blockID,
            -64, // Block.mobSpawner.blockID, // spawner in the storage TODO
            -6, // Block.pistonBase.blockID,
            0,
            0,
            0,
            8, // BTWBlocks.planter.blockID, // soul sand planter
            0,
            0,
            0,
            8, // BTWBlocks.quartzMouldingAndDecorative.blockID, // it's the brewing stand table
            0,
            -4, // Block.cauldron.blockID, // cauldron for the brewing stand, needs water meta chance
            0,
            0,
            0,
            0, // BTWBlocks.soulforge.blockID, // storage
            0,
            -4, // BTWBlocks.plainCandle.blockID,
            2   , // BTWBlocks.idleOven.blockID, // needs facing meta
            -16, // BTWBlocks.vase.blockID, // forge
            0,
            0,
            8, // Block.stoneSingleSlab.blockID, // needs meta 8
            0, // Block.oreIron.blockID, // random strata
            0,
            0,
            0,
    };



    // === MAIN MUSHROOM GENERATION ============================================
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        String path = "structures/mushroom.nbt";
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
            lootPool.add(new LootEntry(new ItemStack(Block.mushroomRed), 25, 6, 20));
            lootPool.add(new LootEntry(new ItemStack(Block.mushroomBrown), 25, 10, 12));
            lootPool.add(new LootEntry(new ItemStack(Item.potion, 1, 8197), 15, 1, 3)); // healing 1
            lootPool.add(new LootEntry(new ItemStack(Item.potion, 1, 16421), 3, 0, 2)); // splash healing 2
            lootPool.add(new LootEntry(new ItemStack(NMItems.bloodIngot), 30, 0, 1));
            lootPool.add(new LootEntry(new ItemStack(NMItems.refinedDiamondIngot), 24, 1, 2));
            lootPool.add(new LootEntry(new ItemStack(Item.diamond), 5, 1, 2));
            lootPool.add(new LootEntry(new ItemStack(NMItems.friedCalamari), 15, 1, 4));
            lootPool.add(new LootEntry(new ItemStack(NMItems.darksunFragment), 15, 1, 3));

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

//            for(BlockPos bp : spawnerLocations){
//                TileEntity te = world.getBlockTileEntity(bp.x, bp.y, bp.z);
//                if(te instanceof TileEntityMobSpawner spawner){
//                    spawner.getSpawnerLogic().setMobID("NightmareBloodZombie");
//                }
//            }


            for(BlockPos bp : lootLocations){
                TileEntity chest = world.getBlockTileEntity(bp.x, bp.y, bp.z);
                if(chest == null){
                    System.out.println("something went wrong, couldn't capture chest");
                    continue;
                }
                // add the loot
                if (!(chest instanceof TileEntityBloodChest)) {
                    System.out.println("BloodChest is not a TileEntityBloodChest - adjust casting if needed");
                    continue;
                }
                TileEntityBloodChest chestTE = (TileEntityBloodChest) chest;

                // Clear the chest first if needed (vanilla dungeons start empty)
                for (int slot = 0; slot < chestTE.getSizeInventory(); slot++) {
                    chestTE.setInventorySlotContents(slot, null);
                }

                int numItems = structureRand.nextInt(7) + 4;

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


    public void placeBlockAtCurrentPositiona(World world, int blockID, int meta, int x, int y, int z, StructureBoundingBox par7StructureBoundingBox) {
        int zPos = this.getZWithOffset(x, z);
        int yPos = this.getYWithOffset(y);
        int xPos = this.getXWithOffset(x, z);
        if (par7StructureBoundingBox.isVecInside(xPos, yPos, zPos)) {
            world.setBlock(xPos, yPos, zPos, blockID, meta, 2);
        }
    }

}