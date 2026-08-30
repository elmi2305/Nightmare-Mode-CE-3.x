package com.itlesports.nightmaremode.structure;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import api.world.BlockPos;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.NMPostItems;
import com.itlesports.nightmaremode.underworld.poi.LootEntry;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntityMobSpawner;
import net.minecraft.src.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** A 54 x 54 sky landmark placed from structures/skyZiggurath.nbt at Y=200. */
public class SkyZiggurath extends NMStructure {
    public static final int MIN_CHUNKS_APART = 32;
    public static final int MAX_CHUNKS_APART = 48;
    private static final String[] SPAWNER_MOBS = {
            "NmSkyZiggurathWitherSkeleton",
            "NmSkyZiggurathJungleSpider",
            "NmSkyZiggurathDeadzonePigman",
            "NmSkyZiggurathShadowZombie"
    };
    private static PaletteEntry[] palette;

    public SkyZiggurath() {
    }

    public SkyZiggurath(Random random, int x, int z) {
        super(random, x, 200, z, 54, 30, 54);
        // Keep the NBT template aligned with the world's positive X/Z axes.
        this.coordBaseMode = 0;
        this.shouldGenerateAir = true;
    }

    @Override
    protected String getStructurePath() {
        return "structures/skyZiggurath.nbt";
    }

    @Override
    protected PaletteEntry[] getPalette() {
        if (palette == null) {
            palette = createPalette();
        }
        return palette;
    }

    // Kept in lockstep with Tier3VillagerPost's palette, including its state indexes.
    private static PaletteEntry[] createPalette() {
        PaletteEntry[] entries = new PaletteEntry[31];
        entries[0] = block(0, 0);
        entries[1] = block(Block.dirt.blockID, 0);
        entries[2] = block(Block.stone.blockID, 0);
        entries[3] = block(Block.stoneBrick.blockID, 8);
        entries[4] = block(Block.mobSpawner.blockID, 0);
        entries[5] = block(BTWBlocks.stoneBrickSlab.blockID, 2);
        entries[6] = block(BTWBlocks.stoneBrickSlab.blockID, 10);
        entries[7] = block(Block.skull.blockID, 4);
        entries[8] = block(Block.web.blockID, 0);
        entries[9] = block(Block.redstoneWire.blockID, 0);
        entries[10] = lootContainer(3);
        entries[11] = lootContainer(4);
        entries[12] = block(Block.stoneBrick.blockID, 10);
        entries[13] = lootContainer(4);
        entries[14] = block(Block.netherrack.blockID, 0);
        entries[15] = block(Block.fire.blockID, 0);
        entries[16] = block(BTWBlocks.deepStrataStoneBrickStairs.blockID, 1);
        entries[17] = block(BTWBlocks.deepStrataStoneBrickStairs.blockID, 0);
        entries[18] = block(BTWBlocks.deepStrataStoneBrickStairs.blockID, 3);
        entries[19] = block(BTWBlocks.deepStrataStoneBrickStairs.blockID, 7);
        entries[20] = block(Block.fenceIron.blockID, 0);
        entries[21] = block(Block.anvil.blockID, 0);
        entries[22] = block(Block.skull.blockID, 1);
        entries[23] = block(Block.obsidian.blockID, 0);
        entries[24] = block(BTWBlocks.deepStrataStoneBrickStairs.blockID, 2);
        entries[25] = block(BTWBlocks.deepStrataStoneBrickStairs.blockID, 4);
        entries[26] = lootContainer(2);
        entries[27] = lootContainer(5);
        entries[28] = lootContainer(2);
        entries[29] = block(Block.oreDiamond.blockID, 0);
        entries[30] = block(Block.oreGold.blockID, 0);
        return entries;
    }

    @Override
    protected int getLootContainerBlockID() {
        return NMBlocks.bloodChest.blockID;
    }

    @Override
    protected int getLootRollCount(BlockPos position, Random random) {
        return random.nextInt(5) + 5;
    }

    @Override
    protected void configureSpawner(World world, BlockPos position, TileEntityMobSpawner spawner, Random random) {
        spawner.getSpawnerLogic().setMobID(SPAWNER_MOBS[random.nextInt(SPAWNER_MOBS.length)]);
    }

    @Override
    protected List<LootEntry> getLootPool() {
        return Arrays.asList(
                // Common supplies: make the expedition materially worthwhile without trivializing it.
                new LootEntry(new ItemStack(BTWItems.steelNugget), 24, 4, 12),
                new LootEntry(new ItemStack(BTWItems.soulforgedSteelIngot), 10, 1, 3),
                new LootEntry(new ItemStack(NMItems.bloodOrb), 16, 2, 6),
                new LootEntry(new ItemStack(NMItems.bloodIngot), 10, 1, 2),
                new LootEntry(new ItemStack(Item.appleGold), 10, 1, 2),

                // The sky is a post-Wither reward source for the materials used to reach it.
                new LootEntry(new ItemStack(NMItems.deadzoneShard), 16, 4, 10),
                new LootEntry(new ItemStack(NMItems.coresteelIngot), 7, 1, 2),
                new LootEntry(new ItemStack(NMItems.deadzoneAlloyIngot), 6, 1, 2),
                new LootEntry(new ItemStack(NMItems.deadzoneAlloyPlate), 3, 1, 1),

                // Jackpot rolls stay scarce across the structure's several chests.
                new LootEntry(new ItemStack(Item.appleGold, 1, 1), 1, 1, 1),
                new LootEntry(new ItemStack(NMPostItems.bloodMoonBottle), 2, 1, 1)
        );
    }
}
