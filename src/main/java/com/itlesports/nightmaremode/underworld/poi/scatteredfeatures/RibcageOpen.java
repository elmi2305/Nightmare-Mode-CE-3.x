package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import java.util.Random;

public class RibcageOpen extends NMStructure {
    private static final PaletteEntry[] PALETTE = new PaletteEntry[]{
            null,
            block(BTWBlocks.aestheticOpaque.blockID, 15)
    };

    public RibcageOpen() {} // required

    public RibcageOpen(Random random, int x, int z) {

        super(random, x, 70, z, 51,43,42);

    }

    @Override
    protected String getStructurePath() {
        return "structures/ribcageOpen.nbt";
    }

    @Override
    protected PaletteEntry[] getPalette() {
        return PALETTE;
    }
}
