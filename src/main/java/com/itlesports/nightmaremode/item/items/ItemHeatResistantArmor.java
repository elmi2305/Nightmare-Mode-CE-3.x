package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.IHeatResistantArmor;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;

public class ItemHeatResistantArmor extends ItemAlloyArmor implements IHeatResistantArmor, INetherItem {
    private final float fireTimeReduction;

    public ItemHeatResistantArmor(int id, int armorType, int protection, int weight, int maxUses,
                                  int enchantability, double knockbackResistance, int repairItemID,
                                  String wornTexturePrefix, String setBonusKey, float fireTimeReduction) {
        super(id, armorType, protection, weight, maxUses, enchantability, knockbackResistance,
                repairItemID, wornTexturePrefix, setBonusKey);
        this.fireTimeReduction = fireTimeReduction;
    }

    @Override
    public float getFireTimeReduction() {
        return this.fireTimeReduction;
    }
}
