package com.itlesports.nightmaremode.item.items.bloodItems;

import btw.item.items.ArmorItem;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EnumArmorMaterial;

public class ItemBloodArmor extends ArmorItem {
    public ItemBloodArmor(int iItemID, int iArmorType, int iWeight, int iMaxUses, double iKnockbackResistance) {
        super(iItemID, EnumArmorMaterial.DIAMOND, 3, iArmorType, iWeight, iKnockbackResistance);
        this.setInfernalMaxEnchantmentCost(50);
        this.setInfernalMaxNumEnchants(4);
        this.setMaxDamage(iMaxUses);
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setBuoyant();
        this.setUnlocalizedName("nmBloodChestplate");
    }
}
