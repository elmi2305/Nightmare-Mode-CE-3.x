package com.itlesports.nightmaremode.item.items;

import api.item.items.ArmorItemMod;
import com.itlesports.nightmaremode.util.underworld.UnderworldToolTier;
import net.minecraft.src.EnumArmorMaterial;

public class ItemUnderworldArmor extends ArmorItemMod implements IUnderworldSanityArmor {
    private final UnderworldToolTier tier;
    private final double sanityReduction;

    public ItemUnderworldArmor(int id, UnderworldToolTier tier, int armorType, int weight,
                               int durability, double knockbackResistance, double sanityReduction) {
        super(id, EnumArmorMaterial.DIAMOND, 3, armorType, weight, knockbackResistance);
        this.tier = tier;
        this.sanityReduction = sanityReduction;
        this.setMaxDamage(durability);
        this.setBuoyant();
        this.setInfernalMaxNumEnchants(4);
    }

    @Override
    public double getSanityPressureReduction() {
        return sanityReduction;
    }

    public UnderworldToolTier getUnderworldTier() {
        return tier;
    }

    @Override public String getModId() { return "nightmare"; }
    @Override public String getWornTexturePrefix() { return tier == UnderworldToolTier.TUNGSTEN ? "nmTungstenArmor" : "nmTitaniumArmor"; }
    @Override public String getWornTextureDirectory() { return "nightmare:textures/armor/"; }
}
