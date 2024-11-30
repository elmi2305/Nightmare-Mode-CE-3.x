package com.itlesports.nightmaremode.item;

import com.itlesports.nightmaremode.item.items.*;

public class NMItems {
    public static ItemRPG rpg;
    public static ItemAR rifle;
    public static ItemBandage bandage;
    public static ItemIronKnittingNeedles ironKnittingNeedles;
    public static ItemFlamethrower flamethrower;
    public static ItemWitchLocator witchLocator;

    static {
        rpg =(ItemRPG)(new ItemRPG(2309)).setTextureName("nmRPG");
        rifle = (ItemAR)(new ItemAR(2310)).setTextureName("nmRifle");
        bandage = (ItemBandage)(new ItemBandage(2311,0,0f,false)).setTextureName("nmBandage");
        ironKnittingNeedles = (ItemIronKnittingNeedles)(new ItemIronKnittingNeedles(2312)).setTextureName("nmNeedles");
//        flamethrower = (ItemFlamethrower)(new ItemFlamethrower(2313)).setTextureName("nmFlamethrower");
        witchLocator = (ItemWitchLocator)(new ItemWitchLocator(2314)).setTextureName("nmWitchDust");
    }
}
