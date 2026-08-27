package com.itlesports.nightmaremode.item.items;

import api.item.items.ArmorItemMod;
import net.minecraft.src.EnumArmorMaterial;

public class ItemOxygenGear extends ArmorItemMod {
    private final float oxygenDrainReduction;

    public ItemOxygenGear(int itemID, int armorType, int weight, int maxUses, float oxygenDrainReduction) {
        this(itemID, armorType, weight, maxUses, oxygenDrainReduction, 0.0D);
    }

    public ItemOxygenGear(int itemID, int armorType, int weight, int maxUses,
                          float oxygenDrainReduction, double knockbackResistance) {
        super(itemID, EnumArmorMaterial.IRON, 4, armorType, weight, knockbackResistance);
        this.oxygenDrainReduction = oxygenDrainReduction;
        this.setMaxDamage(maxUses);
        this.setCreativeTab(net.minecraft.src.CreativeTabs.tabCombat);
    }

    public float getOxygenDrainReduction() {
        return this.oxygenDrainReduction;
    }

    @Override
    public String getModId() {
        return "nightmare";
    }

    @Override
    public String getWornTexturePrefix() {
        return "oxygenGear";
    }

    @Override
    public String getWornTextureDirectory() {
        return "nightmare:textures/armor/";
    }
}
