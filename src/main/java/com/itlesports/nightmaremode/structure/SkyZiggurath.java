package com.itlesports.nightmaremode.structure;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import net.minecraft.src.Block;

import java.util.Random;

/** A 54 x 54 sky landmark placed from structures/skyZiggurath.nbt at Y=200. */
public class SkyZiggurath extends NMStructure {
    public static final int MIN_CHUNKS_APART = 32;
    public static final int MAX_CHUNKS_APART = 48;
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
        entries[4] = block(Block.brick.blockID, 0);
        entries[5] = block(BTWBlocks.stoneBrickSlab.blockID, 2);
        entries[6] = block(BTWBlocks.stoneBrickSlab.blockID, 10);
        entries[7] = block(Block.skull.blockID, 4);
        entries[8] = block(Block.web.blockID, 0);
        entries[9] = block(Block.redstoneWire.blockID, 0);
        entries[10] = block(BTWBlocks.chest.blockID, 3);
        entries[11] = block(BTWBlocks.chest.blockID, 4);
        entries[12] = block(Block.stoneBrick.blockID, 10);
        entries[13] = block(BTWBlocks.chest.blockID, 4);
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
        entries[26] = block(BTWBlocks.chest.blockID, 2);
        entries[27] = block(BTWBlocks.chest.blockID, 5);
        entries[28] = block(BTWBlocks.chest.blockID, 2);
        entries[29] = block(Block.oreDiamond.blockID, 0);
        entries[30] = block(Block.oreGold.blockID, 0);
        return entries;
    }
}
