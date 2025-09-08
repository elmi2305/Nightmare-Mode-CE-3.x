package com.itlesports.nightmaremode.item.items.bloodItems;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class ItemBloodOrb extends Item {
    public ItemBloodOrb(int par1) {
        super(par1);
        this.setBellowsBlowDistance(1);
        this.setFilterableProperties(8);
        this.setUnlocalizedName("nmBloodOrb");
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    public String getModId() {
        return "nightmare_mode";
    }

}
