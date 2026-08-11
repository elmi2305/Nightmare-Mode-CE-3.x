package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.items.template.NMItem;

public class ItemQuestFragment extends NMItem {
    public ItemQuestFragment(int id, int repairsRequired) {
        super(id);
        this.setMaxStackSize(1);
        this.setMaxDamage(repairsRequired);
    }
}
