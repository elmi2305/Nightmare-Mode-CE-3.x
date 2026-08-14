package com.itlesports.nightmaremode.item.items;

import api.item.items.ArmorItemMod;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.EnumArmorMaterial;
import net.minecraft.src.ItemStack;

public class ItemEnderArmor extends ArmorItemMod {
    public ItemEnderArmor(int id, int armorType, int weight) {
        super(id, EnumArmorMaterial.DIAMOND, 5, armorType, weight, 0.08D);
        this.setMaxDamage(3200);
        this.setInfernalMaxEnchantmentCost(50);
        this.setInfernalMaxNumEnchants(4);
    }

    @Override public String getModId() { return "nightmare"; }
    @Override public String getWornTexturePrefix() { return "ifhyEnderArmor"; }
    @Override public String getWornTextureDirectory() { return "nightmare:textures/armor/"; }
    @Override public boolean getIsRepairable(ItemStack armor, ItemStack material) { return material != null && material.getItem() == NMItems.phaseSteelIngot; }
}
