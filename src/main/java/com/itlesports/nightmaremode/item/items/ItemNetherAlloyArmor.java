package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.INetherItem;

public class ItemNetherAlloyArmor extends ItemAlloyArmor implements INetherItem {
    public ItemNetherAlloyArmor(int id, int armorType, int protection, int weight, int maxUses,
                                int enchantability, double knockbackResistance, int repairItemID,
                                String wornTexturePrefix, String setBonusKey) {
        super(id, armorType, protection, weight, maxUses, enchantability, knockbackResistance,
                repairItemID, wornTexturePrefix, setBonusKey);
    }
}
