package com.itlesports.nightmaremode.item.items.bloodItems;

import btw.item.items.HoeItem;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;

public class ItemBloodHoe extends HoeItem {
    public ItemBloodHoe(int i, EnumToolMaterial enumToolMaterial, int iMaxUses) {
        super(i, enumToolMaterial);
        this.setMaxDamage(iMaxUses);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setBuoyant();
        this.setUnlocalizedName("nmBloodHoe");
    }
}
