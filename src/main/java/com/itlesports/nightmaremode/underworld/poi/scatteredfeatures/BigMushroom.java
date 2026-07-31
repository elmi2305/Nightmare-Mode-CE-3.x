package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import api.world.BlockPos;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.NMPostItems;
import com.itlesports.nightmaremode.underworld.poi.LootEntry;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BigMushroom extends NMStructure {
    private static final int BASE_HEIGHT = 50;
    private static final String STRUCTURE_PATH = "structures/mushroom.nbt";

    private static final PaletteEntry[] PALETTE = createPalette();

    public BigMushroom() {}

    public BigMushroom(Random random, int x, int z) {
        super(random, x, BASE_HEIGHT, z, 64, 64, 64);
    }

    @Override
    protected String getStructurePath() {
        return STRUCTURE_PATH;
    }

    @Override
    protected PaletteEntry[] getPalette() {
        return PALETTE;
    }

    private static PaletteEntry[] createPalette() {
        PaletteEntry[] palette = new PaletteEntry[69];
        palette[1] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_CAP_YELLOW);
        palette[2] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_CAP_WHITE);
        palette[3] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_WALL_YELLOW);
        palette[4] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_STEM);
        palette[5] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_STEM);
        palette[6] = block(Block.hay.blockID, 0);
        palette[7] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_FLOOR_PARTIAL_YELLOW);
        palette[8] = block(NMBlocks.underFlowerDirts.blockID, NMBlocks.META_FLOWER_GRASS);
        palette[9] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_STEM);
        palette[10] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_STEM);
        palette[11] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_TOP_FLOOR_YELLOW);
        palette[12] = block(BTWBlocks.unlitCampfire.blockID, 0);
        palette[13] = block(NMBlocks.yellowFlowerRoots.blockID, 0);
        palette[14] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_WALL_PURPLE);
        palette[15] = block(Block.mobSpawner.blockID, 0);
        palette[16] = block(NMBlocks.mushInnards.blockID, NMBlocks.META_MUSH_INNARDS_BREAKABLE);
        palette[17] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_STEM);
        palette[18] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_FLOOR_PARTIAL_PURPLE);
        palette[19] = block(BTWBlocks.saw.blockID, randomMetadata(6));
        palette[20] = block(Block.pistonStickyBase.blockID, randomMetadata(6));
        palette[21] = block(Block.bookShelf.blockID, 0);
        palette[22] = block(BTWBlocks.hopper.blockID, 0);
        palette[23] = block(Block.chest.blockID, randomMetadata(6));
        palette[24] = block(Block.web.blockID, 0);
        palette[25] = block(BTWBlocks.pulley.blockID, 0);
        palette[26] = block(BTWBlocks.aestheticOpaque.blockID, 11);
        palette[27] = block(BTWBlocks.gearBox.blockID, randomMetadata(6));
        palette[28] = block(Block.flowerPot.blockID, randomMetadata(3));
        palette[29] = block(NMBlocks.mushBlocks.blockID, NMBlocks.META_MUSH_TOP_FLOOR_PURPLE);
        palette[30] = block(Block.fence.blockID, 0);
        palette[31] = block(Block.waterStill.blockID, 0);
        palette[32] = block(Block.cloth.blockID, 7);
        palette[33] = block(BTWBlocks.blockDispenser.blockID, randomMetadata(6));
        palette[34] = block(Block.music.blockID, 0);
        palette[35] = block(Block.blockNetherQuartz.blockID, 0);
        palette[36] = block(random -> BTWBlocks.coloredCandle[random.nextInt(BTWBlocks.coloredCandle.length)].blockID, randomMetadata(4));
        palette[37] = block(BTWBlocks.quartzSidingAndCorner.blockID, 12);
        palette[38] = block(Block.jukebox.blockID, 0);
        palette[39] = block(Block.tnt.blockID, 0);
        palette[40] = block(BTWBlocks.dragonVessel.blockID, 0);
        palette[41] = lootContainer(randomMetadata(6));
        palette[42] = block(Block.mobSpawner.blockID, randomMetadata(64));
        palette[43] = block(Block.pistonBase.blockID, randomMetadata(6));
        palette[44] = block(Block.blockIron.blockID, 0);
        palette[45] = block(Block.blockGold.blockID, 0);
        palette[46] = block(Block.blockDiamond.blockID, 0);
        palette[47] = block(BTWBlocks.planter.blockID, 8);
        palette[48] = block(Block.netherStalk.blockID, 0);
        palette[49] = block(BTWBlocks.turntable.blockID, 0);
        palette[50] = block(Block.stoneSingleSlab.blockID, 0);
        palette[51] = block(BTWBlocks.quartzMouldingAndDecorative.blockID, 8);
        palette[52] = block(Block.brewingStand.blockID, 0);
        palette[53] = block(Block.cauldron.blockID, randomMetadata(4));
        palette[54] = block(NMBlocks.mushInnards.blockID, NMBlocks.META_MUSH_INNARDS_EXPLOSIVE);
        palette[55] = block(NMBlocks.mushBookshelf.blockID, 0);
        palette[56] = block(Block.enchantmentTable.blockID, 0);
        palette[57] = block(BTWBlocks.soulforge.blockID, (random, blockID) -> random.nextInt(4) + 2);
        palette[58] = block(Block.brick.blockID, 0);
        palette[59] = block(random -> BTWBlocks.coloredCandle[random.nextInt(BTWBlocks.coloredCandle.length)].blockID, randomMetadata(4));
        palette[60] = block(BTWBlocks.idleOven.blockID, 4);
        palette[61] = block(BTWBlocks.vase.blockID, randomMetadata(16));
        palette[62] = block(NMBlocks.disenchantmentTable.blockID, 0);
        palette[63] = block(BTWBlocks.planter.blockID, 0);
        palette[64] = block(Block.stoneSingleSlab.blockID, 4);
        palette[65] = block(Block.oreIron.blockID, (random, blockID) -> random.nextInt(2) + 1);
        palette[66] = block(Block.anvil.blockID, 0);
        palette[67] = block(BTWBlocks.workStump.blockID, 0);
        palette[68] = block(Block.rail.blockID, 0);
        return palette;
    }

    @Override
    protected int getLootContainerBlockID() {
        return NMBlocks.bloodChest.blockID;
    }

    @Override
    protected int getLootRollCount(BlockPos position, Random random) {
        int rolls = super.getLootRollCount(position, random);
        return position.y > BASE_HEIGHT + 30 ? rolls + 10 : rolls;
    }

    @Override
    protected List<LootEntry> getLootPool() {
        return Arrays.asList(
                new LootEntry(new ItemStack(NMItems.bloodOrb), 10, 3, 13),
                new LootEntry(new ItemStack(Item.appleRed), 10, 1, 2),
                new LootEntry(new ItemStack(BTWItems.brownMushroom), 25, 6, 20),
                new LootEntry(new ItemStack(BTWItems.redMushroom), 25, 10, 12),
                new LootEntry(new ItemStack(Item.potion, 1, 8197), 15, 1, 3),
                new LootEntry(new ItemStack(Item.potion, 1, 16421), 3, 0, 2),
                new LootEntry(new ItemStack(NMItems.bloodIngot), 18, 0, 1),
                new LootEntry(new ItemStack(NMItems.refinedDiamondIngot), 24, 1, 2),
                new LootEntry(new ItemStack(Item.diamond), 5, 1, 2),
                new LootEntry(new ItemStack(NMItems.friedCalamari), 15, 1, 4),
                new LootEntry(new ItemStack(NMItems.darksunFragment), 15, 1, 3),
                new LootEntry(new ItemStack(NMPostItems.bloodMoonBottle), 2, 0, 1),
                new LootEntry(new ItemStack(NMItems.bloodBoots), 1, 0, 1),
                new LootEntry(new ItemStack(NMItems.bloodShovel), 2, 0, 1),
                new LootEntry(new ItemStack(BTWItems.steelNugget), 10, 1, 4),
                new LootEntry(new ItemStack(BTWItems.soulforgedSteelIngot), 2, 0, 2),
                new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("looting")), 1, 0, 1),
                new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("sharp")), 1, 0, 1),
                new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("smite")), 1, 0, 1),
                new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("efficiency")), 3, 0, 1),
                new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("unbreaking")), 3, 0, 1),
                new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("fortune")), 1, 0, 1),
                new LootEntry(new ItemStack(BTWItems.arcaneScroll, 1, NMUtils.getScrollMetadata("infinity")), 1, 0, 1),
                new LootEntry(new ItemStack(BTWItems.soulFlux), 5, 1, 4),
                new LootEntry(new ItemStack(Item.enderPearl), 8, 1, 2)
        );
    }
}
