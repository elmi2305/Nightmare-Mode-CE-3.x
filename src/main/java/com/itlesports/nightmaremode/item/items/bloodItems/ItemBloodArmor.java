package com.itlesports.nightmaremode.item.items.bloodItems;

import btw.item.items.ArmorItem;
import net.minecraft.src.EnumArmorMaterial;

public class ItemBloodArmor extends ArmorItem {
    public ItemBloodArmor(int iItemID, int iArmorType, int iWeight, int iMaxUses, double iKnockbackResistance) {
        super(iItemID, EnumArmorMaterial.IRON, 3, iArmorType, iWeight, iKnockbackResistance);
        this.setInfernalMaxEnchantmentCost(50);
        this.setInfernalMaxNumEnchants(3);
        this.setMaxDamage(iMaxUses);
        this.setBuoyant();
        this.setUnlocalizedName("nmBloodArmor");
    }
}
