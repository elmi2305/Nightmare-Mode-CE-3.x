package com.itlesports.nightmaremode.item;

import com.itlesports.nightmaremode.item.items.ItemAR;
import com.itlesports.nightmaremode.item.items.ItemBandage;
import com.itlesports.nightmaremode.item.items.ItemRPG;

public class NMItems {
    public static ItemRPG rpg;
    public static ItemAR rifle;
    public static ItemBandage bandage;

    static {
        rpg =(ItemRPG)(new ItemRPG(2309)).setTextureName("nmRPG");
        rifle = (ItemAR)(new ItemAR(2310)).setTextureName("nmRifle");
        bandage = (ItemBandage)(new ItemBandage(2311,0,0f,false)).setTextureName("nmBandage");
    }
}
