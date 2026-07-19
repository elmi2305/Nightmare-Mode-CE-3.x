package com.itlesports.nightmaremode.item.items;

import api.item.items.AxeItem;
import net.minecraft.src.EnumToolMaterial;

public class ItemFlintAxe extends AxeItem {
    public ItemFlintAxe(int itemID) {
        super(itemID, EnumToolMaterial.STONE);
        this.setMaxDamage(1);
    }
}
