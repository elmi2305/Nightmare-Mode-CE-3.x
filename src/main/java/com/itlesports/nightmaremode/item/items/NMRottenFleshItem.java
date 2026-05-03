package com.itlesports.nightmaremode.item.items;

import btw.item.items.RottenFleshItem;
import com.itlesports.nightmaremode.util.NMFields;

public class NMRottenFleshItem extends RottenFleshItem {
    public NMRottenFleshItem(int iItemID) {
        super(iItemID);
    }

    public String getModId() {
        return NMFields.modID;
    }
}
