package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;

import java.util.Random;

public class RibcageOpen extends NMStructure {

    public RibcageOpen() {} // required

    public RibcageOpen(Random random, int x, int z) {

        super(random, x, 70, z, 51,43,42);

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
        String path = "structures/ribcageOpen.nbt";
        placeFromNBT(world, box, path, paletteIDs);
        return true;
    }
}
