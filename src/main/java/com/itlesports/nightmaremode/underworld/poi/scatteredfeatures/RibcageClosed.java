package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import net.minecraft.src.*;

import java.util.Random;

public class RibcageClosed extends NMStructure {
    public RibcageClosed() {} // required

    public RibcageClosed(Random random, int x, int z) {

        super(random, x, 70, z, 67,48,67);

        paletteIDs = new int[]{
                0,
                BTWBlocks.aestheticOpaque.blockID
        };
        meta = new int[]{
                0,
                15
        };
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        String path = "structures/ribcageClosed.nbt";
        placeFromNBT(world, box, path, paletteIDs);
        return true;
    }

}
