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

public class BigMushroom extends ComponentScatteredFeature {
    public BigMushroom() {} // required

    public BigMushroom(Random random, int x, int z) {
        super(random, x, BASE_HEIGHT, z, 64,64,64);
    }
    private final Random structureRand = new Random();
    private static final byte BASE_HEIGHT = 63;

    private static final int[] paletteIDs = new int[]{
            Block.jukebox.blockID, // in the storage room
            NMBlocks.mushBlocks.blockID, // yellow cap
            NMBlocks.mushBlocks.blockID, // white cap
            NMBlocks.mushBlocks.blockID, // yellow wall
            NMBlocks.mushBlocks.blockID, // base of the plant
            NMBlocks.mushBlocks.blockID, // inner mushiness
            Block.hay.blockID, // in the store room
            NMBlocks.mushBlocks.blockID, // partial yellow floor
            NMBlocks.underFlowerDirts.blockID, // flower grass
            NMBlocks.mushBlocks.blockID, // underside gills
            NMBlocks.mushBlocks.blockID, // the ceiling of the top part which used to be netherrack
            NMBlocks.mushBlocks.blockID, // top floor, yellow
            BTWBlocks.unlitCampfire.blockID, // actual
            NMBlocks.yellowFlowerRoots.blockID, // actual
            NMBlocks.mushBlocks.blockID, // purple wall
            Block.mobSpawner.blockID, // ambient in rooms
            NMBlocks.mushInnards.blockID, // breakable innards
            NMBlocks.mushBlocks.blockID, // simply the wall on the top part between sections
            NMBlocks.mushBlocks.blockID, // partial floor purple
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
            NMBlocks.mushBlocks.blockID, // top floor purple
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
            NMBlocks.mushInnards.blockID, // explosive innards
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
            NMBlocks.META_MUSH_CAP_YELLOW,
            NMBlocks.META_MUSH_CAP_WHITE,
            NMBlocks.META_MUSH_WALL_YELLOW,
            NMBlocks.META_MUSH_STEM,
            NMBlocks.META_MUSH_STEM,
            0,
            NMBlocks.META_MUSH_FLOOR_PARTIAL_YELLOW,
            NMBlocks.META_FLOWER_GRASS,
            NMBlocks.META_MUSH_STEM,
            NMBlocks.META_MUSH_STEM,
            NMBlocks.META_MUSH_TOP_FLOOR_YELLOW,
            0,
            0,
            NMBlocks.META_MUSH_WALL_PURPLE,
            0, // Block.mobSpawner.blockID,
            NMBlocks.META_MUSH_INNARDS_BREAKABLE,
            NMBlocks.META_MUSH_STEM, // wall top room
            NMBlocks.META_MUSH_FLOOR_PARTIAL_PURPLE,
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
            NMBlocks.META_MUSH_TOP_FLOOR_PURPLE,
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
            NMBlocks.META_MUSH_INNARDS_EXPLOSIVE,
            0,
            0,
            0, // BTWBlocks.soulforge.blockID, // storage
            0,
            -4, // BTWBlocks.plainCandle.blockID,
            4   , // BTWBlocks.idleOven.blockID, // needs facing meta
            -16, // BTWBlocks.vase.blockID, // forge
            0,
            0,
            4, // Block.stoneSingleSlab.blockID, // needs meta 4
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

            // calculate total weight once (for efficiency)
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

                // clear the chest first if needed (vanilla dungeons start empty)
                for (int slot = 0; slot < chestTE.getSizeInventory(); slot++) {
                    chestTE.setInventorySlotContents(slot, null);
                }

                int numItems = structureRand.nextInt(7) + 4;
                if(bp.y > (BASE_HEIGHT + 30)){
                    // this is just the upper 2 chests
                    numItems += 10;
                }

                for (int itemIndex = 0; itemIndex < numItems; itemIndex++) {
                    if (totalWeight <= 0) break; // No loot defined

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
                        toAdd.stackSize = selected.minCount + structureRand.nextInt(selected.maxCount - selected.minCount + 1);

                        // find a random empty slot (like vanilla randomization)
                        int attempts = 0;
                        int slot = structureRand.nextInt(chestTE.getSizeInventory());
                        while (chestTE.getStackInSlot(slot) != null && attempts < 10) {
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
            // negative metadata calculates the meta as a random number from 0 to abs(meta)
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


    public void placeBlockAtCurrentPositionUnused(World world, int blockID, int meta, int x, int y, int z, StructureBoundingBox par7StructureBoundingBox) {
        int zPos = this.getZWithOffset(x, z);
        int yPos = this.getYWithOffset(y);
        int xPos = this.getXWithOffset(x, z);
        if (par7StructureBoundingBox.isVecInside(xPos, yPos, zPos)) {
            world.setBlock(xPos, yPos, zPos, blockID, meta, 2);
        }
    }

}