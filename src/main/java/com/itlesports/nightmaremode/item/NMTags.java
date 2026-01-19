package com.itlesports.nightmaremode.item;

import api.item.tag.Tag;
import btw.item.BTWItems;
import net.minecraft.src.Item;
import net.minecraft.src.ResourceLocation;


public class NMTags {
    public static final Tag sandwichMeats = Tag.of(loc("sandwich_meats"), Item.beefCooked, Item.chickenCooked, Item.porkCooked, BTWItems.cookedMutton, BTWItems.cookedCheval, BTWItems.cookedWolfChop, BTWItems.cookedMysteryMeat, BTWItems.hamAndEggs);

    private static ResourceLocation loc(String name) {
        return new ResourceLocation("nightmare_mode", name);
    }


    public static void initTags(){}
}
