package com.itlesports.nightmaremode.item.items.bloodItems;

import api.item.items.ArmorItemMod;
import net.minecraft.src.EnumArmorMaterial;

public class ItemBloodArmor extends ArmorItemMod implements IBloodTool{
    public ItemBloodArmor(int iItemID, EnumArmorMaterial material, int iArmorType, int iWeight, int iMaxUses, double iKnockbackResistance) {
        super(iItemID, material, 3, iArmorType, iWeight, iKnockbackResistance);
        this.setInfernalMaxEnchantmentCost(50);
        this.setInfernalMaxNumEnchants(3);
        this.setMaxDamage(iMaxUses);
        this.setBuoyant();
        this.setUnlocalizedName("nmBloodArmor");
    }

    public String getModId() {
        return "nightmare";
    }

    @Override
    public String getWornTexturePrefix() {
        return "bloodArmor";
    }

    @Override
    public int getItemEnchantability() {
        return 28;
    }


    @Override
    public String getWornTextureDirectory() {
        return "textures/armor/";
    }
}
