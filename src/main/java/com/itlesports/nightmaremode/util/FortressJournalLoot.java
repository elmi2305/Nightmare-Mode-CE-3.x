package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.WeightedRandomChestContent;
import net.minecraft.src.World;

public final class FortressJournalLoot {
    private FortressJournalLoot() {}

    public static WeightedRandomChestContent[] add(WeightedRandomChestContent[] loot, World world,
                                                     StructureBoundingBox bounds) {
        int x = (bounds.minX + bounds.maxX) / 2;
        int z = (bounds.minZ + bounds.maxZ) / 2;
        if (NetherTierHelper.getDistanceFromSpawn(world, x, z) <= 1000.0D) return loot;
        return WeightedRandomChestContent.func_92080_a(loot,
                new WeightedRandomChestContent[]{new WeightedRandomChestContent(JourneyJournals.create(4), 1, 1, 2)});
    }
}
