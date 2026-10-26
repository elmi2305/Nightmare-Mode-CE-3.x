package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.EnumToolMaterial;

public class ItemTungstenHammer extends ItemHammer implements INetherItem {
    public ItemTungstenHammer(int id) {
        super(id, EnumToolMaterial.EMERALD);
        this.setMaxDamage(750);
    }
}
