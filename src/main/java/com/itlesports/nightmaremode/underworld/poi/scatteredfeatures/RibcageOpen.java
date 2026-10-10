package com.itlesports.nightmaremode.underworld.poi.scatteredfeatures;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.NMStructure;
import com.itlesports.nightmaremode.underworld.poi.scatteredfeatures.utils.StructurePalette;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.World;

import java.util.Random;

public class RibcageOpen extends NMStructure {

    private final StructurePalette palette = new StructurePalette().map(1, BTWBlocks.aestheticOpaque.blockID, 15);

    public RibcageOpen() {} // required

    public RibcageOpen(Random random, int x, int z) {
        super(random, x, 70, z, 51,43,42);
    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
        String path = "structures/ribcageOpen.nbt";
        placeFromNBT(world, box, path, palette);
        return true;
    }
}
