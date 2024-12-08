package com.itlesports.nightmaremode.item.items.bloodItems;

import btw.item.items.PickaxeItem;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumToolMaterial;

public class ItemBloodPickaxe extends PickaxeItem {
    public ItemBloodPickaxe(int i, EnumToolMaterial enumToolMaterial) {
        super(i, enumToolMaterial);
        this.maxStackSize = 1;
        this.setDamageVsEntity(3);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setBuoyant();
        this.setUnlocalizedName("nmBloodPickaxe");
        this.efficiencyOnProperMaterial = 6.0f;
    }
}
