package com.itlesports.nightmaremode.item.items;

import btw.item.items.FishingRodItemBaited;

public class ItemIronFishingPole extends FishingRodItemBaited {
    public ItemIronFishingPole(int iItemID) {
        super(iItemID);
        this.setMaxDamage(200);
        this.setTextureName("baited_nmIronFishingPole");
        this.setUnlocalizedName("nmIronFishingPole");
    }
}
