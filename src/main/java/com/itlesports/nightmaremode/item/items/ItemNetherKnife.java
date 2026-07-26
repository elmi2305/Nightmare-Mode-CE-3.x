package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.items.template.ItemKnife;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;

public class ItemNetherKnife extends ItemKnife implements INetherItem {
    public ItemNetherKnife(int id, int processingTicks, int harvestTier, int durability) {
        super(id, processingTicks, harvestTier, durability);
    }
}
