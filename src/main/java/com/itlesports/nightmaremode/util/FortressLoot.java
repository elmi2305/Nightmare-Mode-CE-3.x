package com.itlesports.nightmaremode.util;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.worldgen.NetherTierHelper;
import net.minecraft.src.StructureBoundingBox;
import net.minecraft.src.WeightedRandomChestContent;
import net.minecraft.src.World;

public final class FortressLoot {
    private FortressLoot() {}

    public static WeightedRandomChestContent[] addSupplies(WeightedRandomChestContent[] loot) {
        return WeightedRandomChestContent.func_92080_a(loot, new WeightedRandomChestContent[]{
                new WeightedRandomChestContent(NMItems.tungstenNugget.itemID, 0, 1, 3, 1),
                new WeightedRandomChestContent(NMItems.tungstenChunk.itemID, 0, 2, 5, 5),
                new WeightedRandomChestContent(NMItems.tungstenDust.itemID, 0, 2, 4, 4),
                new WeightedRandomChestContent(BTWItems.diamondPile.itemID, 0, 1, 1, 1)
        });
    }

    public static WeightedRandomChestContent[] add(WeightedRandomChestContent[] loot, World world,
                                                     StructureBoundingBox bounds) {
        loot = addSupplies(loot);
        int x = (bounds.minX + bounds.maxX) / 2;
        int z = (bounds.minZ + bounds.maxZ) / 2;
        if (NetherTierHelper.getDistanceFromSpawn(world, x, z) <= 1000.0D) return loot;
        return WeightedRandomChestContent.func_92080_a(loot,
                new WeightedRandomChestContent[]{new WeightedRandomChestContent(JourneyJournals.create(4), 1, 1, 2)});
    }
}
