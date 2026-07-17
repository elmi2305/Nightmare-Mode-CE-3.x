package com.itlesports.nightmaremode.block.blocks.templates;

import api.item.items.PlaceAsBlockItem;
import com.itlesports.nightmaremode.util.NMFields;

public class NMPlaceAsBlockItem extends PlaceAsBlockItem {
    public NMPlaceAsBlockItem(int iItemID, int iBlockID) {
        super(iItemID, iBlockID);
    }
    @Override
    public String getModId() {
        return NMFields.modID;
    }
}
