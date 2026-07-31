package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import java.util.Random;

public class RibcageClosed extends NMStructure {
    private static final PaletteEntry[] PALETTE = new PaletteEntry[]{
            null,
            block(BTWBlocks.aestheticOpaque.blockID, 15)
    };

    public RibcageClosed() {} // required

    public RibcageClosed(Random random, int x, int z) {

        super(random, x, 70, z, 67,48,67);

    }

    @Override
    protected String getStructurePath() {
        return "structures/ribcageClosed.nbt";
    }

    @Override
    protected PaletteEntry[] getPalette() {
        return PALETTE;
    }
}
