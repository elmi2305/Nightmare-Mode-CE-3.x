package com.itlesports.nightmaremode.structure;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import net.minecraft.src.Block;

import java.util.Random;

/** A 54 x 54 sky landmark placed from structures/skyZiggurath.nbt at Y=200. */
public class SkyZiggurath extends NMStructure {
    public static final int MIN_CHUNKS_APART = 8;
    public static final int MAX_CHUNKS_APART = 32;
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
        PaletteEntry[] entries = new PaletteEntry[21];
        entries[0] = block(0, 0);
        entries[1] = block(0, 0);
        entries[2] = block(Block.blockNetherQuartz.blockID, 0);
        entries[3] = block(Block.netherFence.blockID, 0);
        entries[4] = block(Block.netherBrick.blockID, 0);
        entries[5] = block(Block.glowStone.blockID, 0);
        entries[6] = block(BTWBlocks.quartzMouldingAndDecorative.blockID, 12);
        entries[7] = block(Block.obsidian.blockID, 0);
        entries[8] = block(Block.endPortalFrame.blockID, 0);
        entries[9] = block(Block.stoneSingleSlab.blockID, 0);
        entries[10] = block(Block.netherrack.blockID, 0);
        entries[11] = block(Block.enchantmentTable.blockID, 0);
        entries[12] = block(Block.beacon.blockID, 0);
        entries[13] = block(Block.oreDiamond.blockID, 0);
        entries[14] = block(Block.wood.blockID, 0);
        entries[15] = block(Block.chest.blockID, 0);
        entries[16] = block(Block.oreCoal.blockID, 0);
        entries[17] = block(Block.hay.blockID, 0);
        entries[18] = block(Block.oreEmerald.blockID, 0);
        return entries;
    }
}
