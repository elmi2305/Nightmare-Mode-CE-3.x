package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.NMPostItems;
import com.itlesports.nightmaremode.underworld.poi.LootEntry;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.MushroomStructurePalette;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.StructurePalette;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

import java.util.Random;

public class BigMushroom extends NMStructure {
    private static final byte BASE_HEIGHT = 50;
    private final StructurePalette palette = MushroomStructurePalette.create();

    public BigMushroom() {}

    public BigMushroom(Random random, int x, int z) {
        super(random, x, BASE_HEIGHT, z, 64, 64, 64);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        if (lootPool.isEmpty()) configureLoot();
        verticalLootThreshold = BASE_HEIGHT + 30;
        placeFromNBT(world, box, "structures/mushroom.nbt", palette);
        return true;
    }

    private void configureLoot() {
        lootPool.add(new LootEntry(new ItemStack(NMItems.mycelialCore), 4, 1, 1));
        lootPool.add(new LootEntry(new ItemStack(NMItems.clarityDraught), 1, 1, 1));
        lootPool.add(new LootEntry(new ItemStack(NMItems.bloodOrb), 10, 3, 13));
        lootPool.add(new LootEntry(new ItemStack(Item.appleRed), 10, 1, 2));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.brownMushroom), 25, 6, 20));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.redMushroom), 25, 10, 12));
        lootPool.add(new LootEntry(new ItemStack(Item.potion, 1, 8197), 15, 1, 3)); // healing 1
        lootPool.add(new LootEntry(new ItemStack(Item.potion, 1, 16421), 3, 0, 2)); // splash healing 2
        lootPool.add(new LootEntry(new ItemStack(NMItems.bloodIngot), 18, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(NMItems.refinedDiamondIngot), 24, 1, 2));
        lootPool.add(new LootEntry(new ItemStack(Item.diamond), 5, 1, 2));
        lootPool.add(new LootEntry(new ItemStack(NMItems.friedCalamari), 15, 1, 4));
        lootPool.add(new LootEntry(new ItemStack(NMItems.darksunFragment), 15, 1, 3));
        lootPool.add(new LootEntry(new ItemStack(NMPostItems.bloodMoonBottle), 2, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(NMItems.bloodBoots), 1, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(NMItems.bloodShovel), 2, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.steelNugget), 10, 1, 4));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.soulforgedSteelIngot), 2, 0, 2));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("looting")), 1, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("sharp")), 1, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("smite")), 1, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("efficiency")), 3, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("unbreaking")), 3, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("fortune")), 1, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("infinity")), 1, 0, 1));
        lootPool.add(new LootEntry(new ItemStack(Item.enderPearl), 8, 1, 2));
    }

    public void placeBlockAtCurrentPositionUnused(World world, int blockID, int meta, int x, int y, int z, StructureBoundingBox box) {
        placeBlockAtCurrentPosition(world, blockID, meta, x, y, z, box);
    }
}
