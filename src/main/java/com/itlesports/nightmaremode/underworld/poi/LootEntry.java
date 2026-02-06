package com.itlesports.nightmaremode.underworld.poi;

import net.minecraft.src.ItemStack;

public class LootEntry {
    public ItemStack stack;
    public int weight;
    public int minCount;
    public int maxCount;

    public LootEntry(ItemStack stack, int weight, int minCount, int maxCount) {
        this.stack = stack;
        this.weight = weight;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }
}
