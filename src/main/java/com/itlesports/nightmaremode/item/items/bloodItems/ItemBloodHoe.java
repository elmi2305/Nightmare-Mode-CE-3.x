package com.itlesports.nightmaremode.item.items.bloodItems;

import api.item.items.HoeItem;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;

public class ItemBloodHoe extends HoeItem implements IBloodTool{
    public ItemBloodHoe(int i, EnumToolMaterial enumToolMaterial, int iMaxUses) {
        super(i, enumToolMaterial);
        this.setMaxDamage(iMaxUses);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setBuoyant();
        this.setInfernalMaxNumEnchants(4);
        this.setUnlocalizedName("nmBloodHoe");
    }

    public String getModId() {
        return "nightmare";
    }

}
