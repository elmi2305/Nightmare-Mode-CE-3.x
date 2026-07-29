package com.itlesports.nightmaremode.structure;

import net.minecraft.src.StructureStart;
import net.minecraft.src.World;

import java.util.Random;

public class StructureOceanDesertTempleStart extends StructureStart {
    public StructureOceanDesertTempleStart() {
    }

    public StructureOceanDesertTempleStart(World world, Random random, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);
        this.components.add(new ComponentOceanDesertTemple(random, chunkX * 16, chunkZ * 16));
        this.updateBoundingBox();
    }
}
