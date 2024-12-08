package com.itlesports.nightmaremode.item.items.bloodItems;

import btw.item.items.ShovelItem;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;

public class ItemBloodShovel extends ShovelItem {
    public ItemBloodShovel(int iItemID, EnumToolMaterial material, int iMaxUses) {
        super(iItemID, material, iMaxUses);
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setBuoyant();
        this.setUnlocalizedName("nmBloodShovel");
    }
}
