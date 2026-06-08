package com.itlesports.nightmaremode.item;

import api.item.tag.Tag;
import btw.item.BTWItems;
import net.minecraft.src.Item;
import net.minecraft.src.ResourceLocation;


public class NMTags {
    public static final Tag sandwichMeats = Tag.of(loc("sandwich_meats"), Item.beefCooked, Item.chickenCooked, Item.porkCooked, BTWItems.cookedMutton, BTWItems.cookedCheval, BTWItems.cookedWolfChop, BTWItems.cookedMysteryMeat, BTWItems.hamAndEggs);
    public static final Tag eclipseDrops = Tag.of(loc("eclipse_drops"),
            NMItems.darksunFragment,
            NMItems.magicFeather,
            NMItems.bloodMilk,
            NMItems.creeperChop,
            NMItems.voidSack,
            NMItems.charredFlesh,
            NMItems.spiderFangs,
            NMItems.fireRod,
            NMItems.waterRod,
            NMItems.sulfur,
            NMItems.creeperTear,
            NMItems.silverLump,
            NMItems.witheredBone,
            NMItems.voidMembrane,
            NMItems.decayedFlesh,
            NMItems.ghastTentacle,
            NMItems.elementalRod,
            NMItems.shadowRod,
            NMItems.speedCoil
    );

    private static ResourceLocation loc(String name) {
        return new ResourceLocation("nightmare", name);
    }


    public static void initTags(){}
}
