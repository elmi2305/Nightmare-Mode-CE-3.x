package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.items.template.NMFoodItem;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;

public class ItemLavafish extends NMFoodItem implements INetherItem {
    public ItemLavafish(int id) {
        super(id, 6, 0.25F, false, "ifhyLavafish", false);
    }
}
