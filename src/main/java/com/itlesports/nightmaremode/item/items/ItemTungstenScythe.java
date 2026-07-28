package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import net.minecraft.src.EnumToolMaterial;

public class ItemTungstenScythe extends ItemScythe implements INetherItem {
    public ItemTungstenScythe(int id) {
        super(id, EnumToolMaterial.IRON, 4.0F);
    }
}
