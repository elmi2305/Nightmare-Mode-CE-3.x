package com.itlesports.nightmaremode.structure;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.StructureStart;
import net.minecraft.src.World;

import java.util.Random;

public class StructureNetherDesertTempleStart extends StructureStart {
    public StructureNetherDesertTempleStart() {
    }

    public StructureNetherDesertTempleStart(World world, Random random, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);
        this.components.add(new ComponentNetherDesertTemple(random, chunkX * 16, chunkZ * 16));
        this.updateBoundingBox();
    }

    @Override
    public void func_143017_b(NBTTagCompound tag) {
        if (this.components.size() != 1 || !(this.components.getFirst() instanceof ComponentNetherDesertTemple)) {
            return;
        }
        StructureBoundingBox box = ((ComponentNetherDesertTemple) this.components.getFirst()).getBoundingBox();
        if (box.maxX - box.minX != 148 || box.maxY - box.minY != 110
                || box.maxZ - box.minZ != 148) {
            return;
        }
        // older saves expanded the box, which also moved the temple's block origin.
        box.minX += 64;
        box.maxX -= 64;
        box.minY += 48;
        box.maxY -= 48;
        box.minZ += 64;
        box.maxZ -= 64;
        this.updateBoundingBox();
    }
}
