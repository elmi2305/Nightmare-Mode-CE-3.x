package com.itlesports.nightmaremode.util.elements;

import net.minecraft.src.Item;

public class FishingCatch {
    public final Item item;
    public final int weight;
    public final boolean rare;

    public FishingCatch(Item item, int weight, boolean rare) {
        this.item = item;
        this.weight = weight;
        this.rare = rare;
    }
}
