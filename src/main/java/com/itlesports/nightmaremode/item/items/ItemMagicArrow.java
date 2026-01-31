package com.itlesports.nightmaremode.item.items;

import btw.item.items.ArrowItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.ItemStack;

public class ItemMagicArrow extends ArrowItem {
    public ItemMagicArrow(int iItemID) {
        super(iItemID);
    }

    public String getModId() {
        return "nightmare";
    }

    @Override
    @Environment(value= EnvType.CLIENT)
    public boolean hasEffect(ItemStack itemStack) {
        return true;
    }
}
