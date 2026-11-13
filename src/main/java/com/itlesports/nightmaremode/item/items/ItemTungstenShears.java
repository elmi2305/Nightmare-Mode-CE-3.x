package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.ItemShears;

public class ItemTungstenShears extends ItemShears implements INetherItem {
    public ItemTungstenShears(int id) {
        super(id);
        this.setMaxDamage(500);
    }
}
