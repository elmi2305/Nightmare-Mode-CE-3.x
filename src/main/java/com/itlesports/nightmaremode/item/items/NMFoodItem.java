package com.itlesports.nightmaremode.item.items;

import btw.item.items.FoodItem;

public class NMFoodItem extends FoodItem {

    public NMFoodItem(int iItemID, int iHungerHealed, float fSaturationModifier, boolean bWolfMeat, String sItemName, boolean bZombiesConsume) {
        super(iItemID, iHungerHealed, fSaturationModifier, bWolfMeat, sItemName, bZombiesConsume);
    }

    public String getModId() {
        return "nightmare_mode";
    }
}