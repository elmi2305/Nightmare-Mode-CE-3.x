package com.itlesports.nightmaremode.structure;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.StructureStart;
import net.minecraft.src.World;

import java.util.Random;

public class StructureNetherVillagerPostStart extends StructureStart {
    public StructureNetherVillagerPostStart() {}

    public StructureNetherVillagerPostStart(World world, Random random, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);
        int x = chunkX * 16;
        int z = chunkZ * 16;
        int tier = NetherTierHelper.getTier(world, x + 8, z + 8);
        if (tier == 1) {
            this.components.add(new Tier1VillagerPost(random, x, z));
        } else if (tier == 2) {
            this.components.add(new Tier2VillagerPost(random, x, z));
        } else if (tier == 3) {
            this.components.add(new Tier3VillagerPost(random, x, z));
        }
        this.updateBoundingBox();
    }
}
