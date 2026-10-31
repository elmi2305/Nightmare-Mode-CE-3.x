package com.itlesports.nightmaremode.util;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.WeightedRandomChestContent;

public final class FortressSupplyLoot {
    private FortressSupplyLoot() {}

    public static WeightedRandomChestContent[] add(WeightedRandomChestContent[] loot) {
        return WeightedRandomChestContent.func_92080_a(loot, new WeightedRandomChestContent[]{
                new WeightedRandomChestContent(NMItems.tungstenNugget.itemID, 0, 1, 3, 1),
                new WeightedRandomChestContent(NMItems.tungstenChunk.itemID, 0, 2, 5, 5),
                new WeightedRandomChestContent(NMItems.tungstenDust.itemID, 0, 2, 4, 4),
                new WeightedRandomChestContent(BTWItems.diamondPile.itemID, 0, 1, 1, 1)
        });
    }
}
