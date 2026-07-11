package com.itlesports.nightmaremode.block.blocks.templates;

import api.item.items.PlaceAsBlockItem;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import com.itlesports.nightmaremode.util.NMFields;

public class NMPlaceAsBlockItem extends PlaceAsBlockItem {
    private int washedItemID;

    public NMPlaceAsBlockItem(int iItemID, int iBlockID) {
        super(iItemID, iBlockID);
    }
    public NMPlaceAsBlockItem setWashable(int washID){
        this.washedItemID = washID;
        return this;
    }
    public int getWashResult(){
        return this.washedItemID;
    }
    @Override
    public String getModId() {
        return NMFields.modID;
    }
}
