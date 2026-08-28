package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.IHeatResistantArmor;

public class ItemSunDivingGear extends ItemDivingGear implements IHeatResistantArmor {
    private final float fireTimeReduction;

    public ItemSunDivingGear(int id, int armorType, int protection, int weight, int maxUses,
                             int enchantability, double knockbackResistance, int repairItemID,
                             float oxygenDrainReduction, int airCapacity, String wornTexturePrefix,
                             String setBonusKey, float fireTimeReduction) {
        super(id, armorType, protection, weight, maxUses, enchantability, knockbackResistance,
                repairItemID, oxygenDrainReduction, airCapacity, wornTexturePrefix, setBonusKey);
        this.fireTimeReduction = fireTimeReduction;
    }

    @Override
    public float getFireTimeReduction() {
        return this.fireTimeReduction;
    }
}
