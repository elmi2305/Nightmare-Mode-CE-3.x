package com.itlesports.nightmaremode.structure;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.util.hardcorespawn.HardcoreSpawnUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.util.KnowledgeBookLoot;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;

import java.util.Random;

/**
 * Nether desert-temple layout. Copy vanilla addComponentParts() here when the
 * Nether variant's block palette is ready.
 */
public class ComponentNetherDesertTemple extends ComponentScatteredFeature {
    private boolean[] field_74940_h = new boolean[4];
    private static final WeightedRandomChestContent[] lootListArray = new WeightedRandomChestContent[]{

//          new WeightedRandomChestContent(Item.helmetGold.itemID, metadata, minChanceToGen, maxChanceToGen, itemWeightHigherMeansMoreLikely),
            new WeightedRandomChestContent(Item.helmetGold.itemID, 0, 1, 1, 5),
            new WeightedRandomChestContent(Item.plateGold.itemID, 0, 1, 1, 2),
            new WeightedRandomChestContent(Item.legsGold.itemID, 0, 1, 1, 5),
            // these  hampers are the only renewable bootstrap source of Nether wood.
            new WeightedRandomChestContent(NMItems.netherStick.itemID, 0, 4, 13, 20),
            new WeightedRandomChestContent(Block.planks.blockID, 4, 4, 13, 20),
            new WeightedRandomChestContent(Item.bootsGold.itemID, 0, 1, 1, 2),
            new WeightedRandomChestContent(Item.swordGold.itemID, 0, 1, 1, 5),
            new WeightedRandomChestContent(Item.axeGold.itemID, 0, 1, 1, 5),
            new WeightedRandomChestContent(NMItems.tungstenChunk.itemID, 0, 0, 4, 5),
            new WeightedRandomChestContent(BTWItems.groundNetherrack.itemID, 0, 0, 16, 10),
            new WeightedRandomChestContent(Item.netherQuartz.itemID, 0, 0, 3, 10),
            new WeightedRandomChestContent(Item.emerald.itemID, 0, 1, 5, 15),
            new WeightedRandomChestContent(NMItems.boneShard.itemID, 0, 8, 12, 20),
            new WeightedRandomChestContent(Item.rottenFlesh.itemID, 0, 3, 7, 11),
            new WeightedRandomChestContent(Item.skull.itemID, 0, 1, 1, 5),
            new WeightedRandomChestContent(BTWBlocks.aestheticVegetation.blockID, 2, 1, 1, 2), // blood sapling
            new WeightedRandomChestContent(BTWItems.soulUrn.itemID, 0, 1, 1, 2),
            new WeightedRandomChestContent(Item.horseArmorGold.itemID, 0, 1, 1, 1)
        };
    private static final WeightedRandomChestContent[] lootedLootListArray = new WeightedRandomChestContent[]{
            new WeightedRandomChestContent(Item.bone.itemID, 0, 4, 6, 20),
            new WeightedRandomChestContent(Item.rottenFlesh.itemID, 0, 3, 7, 11),
            new WeightedRandomChestContent(Item.skull.itemID, 0, 1, 1, 5)
    };

    public ComponentNetherDesertTemple() {
    }

    public ComponentNetherDesertTemple(Random random, int x, int z) {
        super(random, x, 64, z, 21, 15, 21);
        this.expandSpawnBounds();
    }

    private void expandSpawnBounds() {
        this.boundingBox.minX -= 64;
        this.boundingBox.maxX += 64;
        this.boundingBox.minY -= 48;
        this.boundingBox.maxY += 48;
        this.boundingBox.minZ -= 64;
        this.boundingBox.maxZ += 64;
    }

    @Override
    protected void func_143012_a(NBTTagCompound tag) {
        super.func_143012_a(tag);
        for (int i = 0; i < field_74940_h.length; i++) {
            tag.setBoolean("hasPlacedChest" + i, field_74940_h[i]);
        }
    }

    @Override
    protected void func_143011_b(NBTTagCompound tag) {
        super.func_143011_b(tag);
        for (int i = 0; i < field_74940_h.length; i++) {
            field_74940_h[i] = tag.getBoolean("hasPlacedChest" + i);
        }
    }

    @Override
    public boolean addComponentParts(World world, Random generatorRand, StructureBoundingBox boundingBox) {
        int var10;
        int var5;
        int var4;
        if (world.getWorldInfo().getTerrainType() == WorldType.FLAT && !this.func_74935_a(world, boundingBox, 0)) {
            return false;
        }
        boolean bIsLooted = HardcoreSpawnUtils.isInLootedTempleRadius(world, boundingBox.getCenterX(), boundingBox.getCenterZ());

        int sandstoneBlockID = Block.netherBrick.blockID;
        int stairBlockId = Block.stairsNetherBrick.blockID;
        int stoneSingleSlabID = 0;
        int obsidianID = Block.obsidian.blockID;
        int pressurePlate = Block.pressurePlateStone.blockID;
        int tntBlockId = Block.tnt.blockID;


        this.fillWithBlocks(world, boundingBox, 0, -4, 0, this.scatteredFeatureSizeX - 1, 0, this.scatteredFeatureSizeZ - 1, sandstoneBlockID, sandstoneBlockID, false);
        for (var4 = 1; var4 <= 9; ++var4) {
            this.fillWithBlocks(world, boundingBox, var4, var4, var4, this.scatteredFeatureSizeX - 1 - var4, var4, this.scatteredFeatureSizeZ - 1 - var4, sandstoneBlockID, sandstoneBlockID, false);
            this.fillWithBlocks(world, boundingBox, var4 + 1, var4, var4 + 1, this.scatteredFeatureSizeX - 2 - var4, var4, this.scatteredFeatureSizeZ - 2 - var4, 0, 0, false);
        }
        for (var4 = 0; var4 < this.scatteredFeatureSizeX; ++var4) {
            for (var5 = 0; var5 < this.scatteredFeatureSizeZ; ++var5) {
                this.fillCurrentPositionBlocksDownwards(world, sandstoneBlockID, 0, var4, -5, var5, boundingBox);
            }
        }



        var4 = this.getMetadataWithOffset(stairBlockId, 3);
        var5 = this.getMetadataWithOffset(stairBlockId, 2);
        int var6 = this.getMetadataWithOffset(stairBlockId, 0);
        int var7 = this.getMetadataWithOffset(stairBlockId, 1);
        this.fillWithBlocks(world, boundingBox, 0, 0, 0, 4, 9, 4, sandstoneBlockID, 0, false);
        this.fillWithBlocks(world, boundingBox, 1, 10, 1, 3, 10, 3, sandstoneBlockID, sandstoneBlockID, false);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var4, 2, 10, 0, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var5, 2, 10, 4, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var6, 0, 10, 2, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var7, 4, 10, 2, boundingBox);
        this.fillWithBlocks(world, boundingBox, this.scatteredFeatureSizeX - 5, 0, 0, this.scatteredFeatureSizeX - 1, 9, 4, sandstoneBlockID, 0, false);
        this.fillWithBlocks(world, boundingBox, this.scatteredFeatureSizeX - 4, 10, 1, this.scatteredFeatureSizeX - 2, 10, 3, sandstoneBlockID, sandstoneBlockID, false);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var4, this.scatteredFeatureSizeX - 3, 10, 0, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var5, this.scatteredFeatureSizeX - 3, 10, 4, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var6, this.scatteredFeatureSizeX - 5, 10, 2, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var7, this.scatteredFeatureSizeX - 1, 10, 2, boundingBox);
        this.fillWithBlocks(world, boundingBox, 8, 0, 0, 12, 4, 4, sandstoneBlockID, 0, false);
        this.fillWithBlocks(world, boundingBox, 9, 1, 0, 11, 3, 4, 0, 0, false);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 9, 1, 1, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 9, 2, 1, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 9, 3, 1, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 10, 3, 1, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 11, 3, 1, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 11, 2, 1, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 11, 1, 1, boundingBox);
        this.fillWithBlocks(world, boundingBox, 4, 1, 1, 8, 3, 3, sandstoneBlockID, 0, false);
        this.fillWithBlocks(world, boundingBox, 4, 1, 2, 8, 2, 2, 0, 0, false);
        this.fillWithBlocks(world, boundingBox, 12, 1, 1, 16, 3, 3, sandstoneBlockID, 0, false);
        this.fillWithBlocks(world, boundingBox, 12, 1, 2, 16, 2, 2, 0, 0, false);
        this.fillWithBlocks(world, boundingBox, 5, 4, 5, this.scatteredFeatureSizeX - 6, 4, this.scatteredFeatureSizeZ - 6, sandstoneBlockID, sandstoneBlockID, false);
        this.fillWithBlocks(world, boundingBox, 9, 4, 9, 11, 4, 11, 0, 0, false);
        this.fillWithMetadataBlocks(world, boundingBox, 8, 1, 8, 8, 3, 8, sandstoneBlockID, 2, sandstoneBlockID, 2, false);
        this.fillWithMetadataBlocks(world, boundingBox, 12, 1, 8, 12, 3, 8, sandstoneBlockID, 2, sandstoneBlockID, 2, false);
        this.fillWithMetadataBlocks(world, boundingBox, 8, 1, 12, 8, 3, 12, sandstoneBlockID, 2, sandstoneBlockID, 2, false);
        this.fillWithMetadataBlocks(world, boundingBox, 12, 1, 12, 12, 3, 12, sandstoneBlockID, 2, sandstoneBlockID, 2, false);
        this.fillWithBlocks(world, boundingBox, 1, 1, 5, 4, 4, 11, sandstoneBlockID, sandstoneBlockID, false);
        this.fillWithBlocks(world, boundingBox, this.scatteredFeatureSizeX - 5, 1, 5, this.scatteredFeatureSizeX - 2, 4, 11, sandstoneBlockID, sandstoneBlockID, false);
        this.fillWithBlocks(world, boundingBox, 6, 7, 9, 6, 7, 11, sandstoneBlockID, sandstoneBlockID, false);
        this.fillWithBlocks(world, boundingBox, this.scatteredFeatureSizeX - 7, 7, 9, this.scatteredFeatureSizeX - 7, 7, 11, sandstoneBlockID, sandstoneBlockID, false);
        this.fillWithMetadataBlocks(world, boundingBox, 5, 5, 9, 5, 7, 11, sandstoneBlockID, 2, sandstoneBlockID, 2, false);
        this.fillWithMetadataBlocks(world, boundingBox, this.scatteredFeatureSizeX - 6, 5, 9, this.scatteredFeatureSizeX - 6, 7, 11, sandstoneBlockID, 2, sandstoneBlockID, 2, false);
        this.placeBlockAtCurrentPosition(world, 0, 0, 5, 5, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, 5, 6, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, 6, 6, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, this.scatteredFeatureSizeX - 6, 5, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, this.scatteredFeatureSizeX - 6, 6, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, this.scatteredFeatureSizeX - 7, 6, 10, boundingBox);
        this.fillWithBlocks(world, boundingBox, 2, 4, 4, 2, 6, 4, 0, 0, false);
        this.fillWithBlocks(world, boundingBox, this.scatteredFeatureSizeX - 3, 4, 4, this.scatteredFeatureSizeX - 3, 6, 4, 0, 0, false);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var4, 2, 4, 5, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var4, 2, 3, 4, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var4, this.scatteredFeatureSizeX - 3, 4, 5, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var4, this.scatteredFeatureSizeX - 3, 3, 4, boundingBox);
        this.fillWithBlocks(world, boundingBox, 1, 1, 3, 2, 2, 3, sandstoneBlockID, sandstoneBlockID, false);
        this.fillWithBlocks(world, boundingBox, this.scatteredFeatureSizeX - 3, 1, 3, this.scatteredFeatureSizeX - 2, 2, 3, sandstoneBlockID, sandstoneBlockID, false);
        this.placeBlockAtCurrentPosition(world, stairBlockId, 0, 1, 1, 2, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, 0, this.scatteredFeatureSizeX - 2, 1, 2, boundingBox);
        this.placeBlockAtCurrentPosition(world, stoneSingleSlabID, 1, 1, 2, 2, boundingBox);
        this.placeBlockAtCurrentPosition(world, stoneSingleSlabID, 1, this.scatteredFeatureSizeX - 2, 2, 2, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var7, 2, 1, 2, boundingBox);
        this.placeBlockAtCurrentPosition(world, stairBlockId, var6, this.scatteredFeatureSizeX - 3, 1, 2, boundingBox);
        this.fillWithBlocks(world, boundingBox, 4, 3, 5, 4, 3, 18, sandstoneBlockID, sandstoneBlockID, false);
        this.fillWithBlocks(world, boundingBox, this.scatteredFeatureSizeX - 5, 3, 5, this.scatteredFeatureSizeX - 5, 3, 17, sandstoneBlockID, sandstoneBlockID, false);
        this.fillWithBlocks(world, boundingBox, 3, 1, 5, 4, 2, 16, 0, 0, false);
        this.fillWithBlocks(world, boundingBox, this.scatteredFeatureSizeX - 6, 1, 5, this.scatteredFeatureSizeX - 5, 2, 16, 0, 0, false);
        for (var10 = 5; var10 <= 17; var10 += 2) {
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 4, 1, var10, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, 4, 2, var10, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, this.scatteredFeatureSizeX - 5, 1, var10, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, this.scatteredFeatureSizeX - 5, 2, var10, boundingBox);
        }
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 10, 0, 7, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 10, 0, 8, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 9, 0, 9, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 11, 0, 9, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 8, 0, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 12, 0, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 7, 0, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 13, 0, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 9, 0, 11, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 11, 0, 11, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 10, 0, 12, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 10, 0, 13, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 10, 0, 10, boundingBox);
        for (var10 = 0; var10 <= this.scatteredFeatureSizeX - 1; var10 += this.scatteredFeatureSizeX - 1) {
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10, 2, 1, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 2, 2, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10, 2, 3, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10, 3, 1, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 3, 2, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10, 3, 3, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 4, 1, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, var10, 4, 2, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 4, 3, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10, 5, 1, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 5, 2, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10, 5, 3, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 6, 1, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, var10, 6, 2, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 6, 3, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 7, 1, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 7, 2, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 7, 3, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10, 8, 1, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10, 8, 2, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10, 8, 3, boundingBox);
        }
        for (var10 = 2; var10 <= this.scatteredFeatureSizeX - 3; var10 += this.scatteredFeatureSizeX - 3 - 2) {
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10 - 1, 2, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 2, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10 + 1, 2, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10 - 1, 3, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 3, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10 + 1, 3, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10 - 1, 4, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, var10, 4, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10 + 1, 4, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10 - 1, 5, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 5, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10 + 1, 5, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10 - 1, 6, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, var10, 6, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10 + 1, 6, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10 - 1, 7, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10, 7, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, obsidianID, 0, var10 + 1, 7, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10 - 1, 8, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10, 8, 0, boundingBox);
            this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, var10 + 1, 8, 0, boundingBox);
        }
        this.fillWithMetadataBlocks(world, boundingBox, 8, 4, 0, 12, 6, 0, sandstoneBlockID, 2, sandstoneBlockID, 2, false);
        this.placeBlockAtCurrentPosition(world, 0, 0, 8, 6, 0, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, 12, 6, 0, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 9, 5, 0, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, 10, 5, 0, boundingBox);
        this.placeBlockAtCurrentPosition(world, obsidianID, 0, 11, 5, 0, boundingBox);
        this.fillWithMetadataBlocks(world, boundingBox, 8, -14, 8, 12, -11, 12, sandstoneBlockID, 2, sandstoneBlockID, 2, false);
        this.fillWithMetadataBlocks(world, boundingBox, 8, -10, 8, 12, -10, 12, sandstoneBlockID, 1, sandstoneBlockID, 1, false);
        this.fillWithMetadataBlocks(world, boundingBox, 8, -9, 8, 12, -9, 12, sandstoneBlockID, 2, sandstoneBlockID, 2, false);
        this.fillWithBlocks(world, boundingBox, 8, -8, 8, 12, -1, 12, sandstoneBlockID, sandstoneBlockID, false);
        this.fillWithBlocks(world, boundingBox, 9, -11, 9, 11, -1, 11, 0, 0, false);

        this.placeBlockAtCurrentPosition(world, pressurePlate, 0, 10, -11, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, pressurePlate, 0, 11, -11, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, pressurePlate, 0, 11, -11, 11, boundingBox);
        this.placeBlockAtCurrentPosition(world, pressurePlate, 0, 11, -11, 9, boundingBox);

        this.placeBlockAtCurrentPosition(world, pressurePlate, 0, 10, -11, 9, boundingBox);
        this.placeBlockAtCurrentPosition(world, pressurePlate, 0, 10, -11, 11, boundingBox);

        this.placeBlockAtCurrentPosition(world, pressurePlate, 0, 9, -11, 9, boundingBox);
        this.placeBlockAtCurrentPosition(world, pressurePlate, 0, 9, -11, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, pressurePlate, 0, 9, -11, 11, boundingBox);

        this.fillWithBlocks(world, boundingBox, 9, -13, 9, 11, -13, 11, tntBlockId, 0, false);
        this.placeBlockAtCurrentPosition(world, 0, 0, 8, -11, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, 8, -10, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, 7, -10, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 7, -11, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, 12, -11, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, 12, -10, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, 13, -10, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 13, -11, 10, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, 10, -11, 8, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, 10, -10, 8, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, 10, -10, 7, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 10, -11, 7, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, 10, -11, 12, boundingBox);
        this.placeBlockAtCurrentPosition(world, 0, 0, 10, -10, 12, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 1, 10, -10, 13, boundingBox);
        this.placeBlockAtCurrentPosition(world, sandstoneBlockID, 2, 10, -11, 13, boundingBox);
        for (var10 = 0; var10 < 4; ++var10) {
            if (this.field_74940_h[var10]) continue;
            int iXOffset = Direction.offsetX[var10] * 2;
            int iZOffset = Direction.offsetZ[var10] * 2;
            WeightedRandomChestContent[] lootList = lootListArray;
            int iNumItems = 2 + generatorRand.nextInt(5);
            if (bIsLooted) {
                lootList = lootedLootListArray;
                iNumItems /= 2;
            }
            WeightedRandomChestContent[] moddedLootList = WeightedRandomChestContent.func_92080_a(lootList, Item.enchantedBook.func_92114_b(generatorRand));
            this.field_74940_h[var10] = this.generateStructureContainerContents(world, boundingBox, generatorRand, 10 + iXOffset, -11, 10 + iZOffset, BTWBlocks.hamper.blockID, Direction.directionToFacing[var10], moddedLootList, iNumItems, true);
        }
        if (bIsLooted) {
            this.fillWithBlocks(world, boundingBox, 9, 0, 9, 10, 0, 10, 0, 0, false);
            this.fillWithBlocks(world, boundingBox, 9, -13, 9, 11, -11, 11, 0, 0, false);
            int iLadderFacing = this.getMetadataWithOffset(Block.ladder.blockID, 5);
            int iLadderMetadata = BTWBlocks.ladder.setFacing(0, iLadderFacing);
            for (int iTempY = -13; iTempY <= 0; ++iTempY) {
                this.placeBlockAtCurrentPosition(world, BTWBlocks.ladder.blockID, iLadderMetadata, 9, iTempY, 9, boundingBox);
            }
        } else {
            this.placeBlockAtCurrentPosition(world, Block.enchantmentTable.blockID, 0, 10, 1, 10, boundingBox);
        }

        for (int direction = 0; direction < 4; ++direction) {
            int x = this.getXWithOffset(10 + Direction.offsetX[direction] * 2, 10 + Direction.offsetZ[direction] * 2);
            int y = this.getYWithOffset(-11);
            int z = this.getZWithOffset(10 + Direction.offsetX[direction] * 2, 10 + Direction.offsetZ[direction] * 2);
            TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
            if (tileEntity instanceof IInventory inventory) {
                KnowledgeBookLoot.addBookIfRolled(inventory, generatorRand, NMFields.KNOWLEDGE_BOOKS_OCEAN_TEMPLE, 3);
            }
        }
        return true;
    }

}
