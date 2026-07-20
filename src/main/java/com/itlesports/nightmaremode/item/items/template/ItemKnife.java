package com.itlesports.nightmaremode.item.items.template;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

public class ItemKnife extends NMItem {
    public static final int TIER_FISTS = 0;
    public static final int TIER_STONE = 1;
    public static final int TIER_IRON = 2;
    public static final int TIER_DIAMOND = 3;

    private final int processingTicks;
    private final int harvestTier;

    public ItemKnife(int id, int processingTicks, int harvestTier, int durability) {
        super(id);
        this.processingTicks = processingTicks;
        this.harvestTier = harvestTier;
        this.setMaxDamage(durability);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabTools);
    }

    public int getProcessingTicks() {
        return this.processingTicks;
    }

    public int getHarvestTier() {
        return this.harvestTier;
    }

    public static ItemKnife fromStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemKnife knife ? knife : null;
    }
}
