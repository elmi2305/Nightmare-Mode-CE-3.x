package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.StructurePalette;
import net.minecraft.src.*;

import java.util.Random;

public class RibcageClosed extends NMStructure {
    private final StructurePalette palette = new StructurePalette().map(1, BTWBlocks.aestheticOpaque.blockID, 15);

    public RibcageClosed() {} // required

    public RibcageClosed(Random random, int x, int z) {
        super(random, x, 70, z, 67,48,67);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        String path = "structures/ribcageClosed.nbt";
        placeFromNBT(world, box, path, palette);
        return true;
    }

}
