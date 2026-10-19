package com.itlesports.nightmaremode.item.items;

import api.item.items.ArmorItemMod;
import net.minecraft.src.EnumArmorMaterial;

public class ItemOxygenGear extends ArmorItemMod {
    private final float oxygenDrainReduction;
    private final String wornTexturePrefix;

    public ItemOxygenGear(int itemID, int armorType, int weight, int maxUses, float oxygenDrainReduction) {
        this(itemID, armorType, weight, maxUses, oxygenDrainReduction, 0.0D, "oxygenGear");
    }

    public ItemOxygenGear(int itemID, int armorType, int weight, int maxUses,
                          float oxygenDrainReduction, double knockbackResistance) {
        this(itemID, armorType, weight, maxUses, oxygenDrainReduction, knockbackResistance, "oxygenGear");
    }

    public ItemOxygenGear(int itemID, int armorType, int weight, int maxUses,
                          float oxygenDrainReduction, double knockbackResistance, String wornTexturePrefix) {
        super(itemID, EnumArmorMaterial.IRON, 4, armorType, weight, knockbackResistance);
        this.oxygenDrainReduction = oxygenDrainReduction;
        this.wornTexturePrefix = wornTexturePrefix;
        this.setMaxDamage(maxUses);
        this.setCreativeTab(net.minecraft.src.CreativeTabs.tabCombat);
    }

    public float getOxygenDrainReduction() {
        return this.oxygenDrainReduction;
    }

    @Override
    public void addInformation(net.minecraft.src.ItemStack stack, net.minecraft.src.EntityPlayer player,
                               java.util.List tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(net.minecraft.src.EnumChatFormatting.BLUE + net.minecraft.src.I18n.getStringParams(
                "item.ifhyOxygenReduction", Math.round(this.oxygenDrainReduction * 100.0F)));
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
