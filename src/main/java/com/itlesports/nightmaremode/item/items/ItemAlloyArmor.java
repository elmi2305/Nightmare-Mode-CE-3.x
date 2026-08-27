package com.itlesports.nightmaremode.item.items;

import api.item.items.ArmorItemMod;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumArmorMaterial;
import net.minecraft.src.I18n;
import net.minecraft.src.ItemStack;

import java.util.List;

/**
 * Parameterized IFHY armor backed by BTW's armor attributes. The vanilla material is only a
 * constructor requirement; protection, durability, weight, enchantability, and repair material
 * are supplied explicitly so adding an alloy does not require extending EnumArmorMaterial.
 */
public class ItemAlloyArmor extends ArmorItemMod {
    private final int enchantability;
    private final int repairItemID;
    private final String wornTexturePrefix;
    private final String setBonusKey;

    public ItemAlloyArmor(int id, int armorType, int protection, int weight, int maxUses,
                          int enchantability, double knockbackResistance, int repairItemID,
                          String wornTexturePrefix, String setBonusKey) {
        super(id, EnumArmorMaterial.IRON, 6, armorType, weight, knockbackResistance);
        this.damageReduceAmount = protection;
        this.setMaxDamage(maxUses);
        this.enchantability = enchantability;
        this.repairItemID = repairItemID;
        this.wornTexturePrefix = wornTexturePrefix;
        this.setBonusKey = setBonusKey;
        this.setInfernalMaxEnchantmentCost(50);
        this.setInfernalMaxNumEnchants(3);
    }

    @Override
    public int getItemEnchantability() {
        return this.enchantability;
    }

    @Override
    public boolean getIsRepairable(ItemStack armor, ItemStack material) {
        return material != null && material.itemID == this.repairItemID;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        if (this.setBonusKey != null && !this.setBonusKey.isEmpty()) {
            tooltip.add(I18n.getString(this.setBonusKey));
        }
    }

    @Override
    public String getModId() {
        return "nightmare";
    }

    @Override
    public String getWornTexturePrefix() {
        return this.wornTexturePrefix;
    }

    @Override
    public String getWornTextureDirectory() {
        return "nightmare:textures/armor/";
    }
}
