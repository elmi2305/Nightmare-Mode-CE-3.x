package com.itlesports.nightmaremode.structure;

import net.minecraft.src.StructureStart;
import net.minecraft.src.World;

import java.util.Random;

public class StructureSkyZiggurathStart extends StructureStart {
    public StructureSkyZiggurathStart() {
    }

    public StructureSkyZiggurathStart(World world, Random random, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);
        this.components.add(new SkyZiggurath(random, chunkX * 16, chunkZ * 16));
        this.updateBoundingBox();
    }
}
