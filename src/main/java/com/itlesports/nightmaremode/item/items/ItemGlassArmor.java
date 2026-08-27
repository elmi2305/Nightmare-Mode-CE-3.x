package com.itlesports.nightmaremode.item.items;

/** Marker type used to apply the deliberately painful glass-suit break behaviour. */
public class ItemGlassArmor extends ItemAlloyArmor {
    public ItemGlassArmor(int id, int armorType, int protection, int weight, int maxUses,
                          int enchantability, double knockbackResistance, int repairItemID,
                          String wornTexturePrefix, String setBonusKey) {
        super(id, armorType, protection, weight, maxUses, enchantability, knockbackResistance,
                repairItemID, wornTexturePrefix, setBonusKey);
    }
}
