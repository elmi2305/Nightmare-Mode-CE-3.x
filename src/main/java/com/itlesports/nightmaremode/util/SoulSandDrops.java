package com.itlesports.nightmaremode.util;

import api.item.items.ShovelItem;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.ItemStack;

public final class SoulSandDrops {
    private SoulSandDrops() {}

    public static float chipChance(ItemStack tool) {
        if (tool == null) return 0.08F;
        if (tool.getItem() == NMItems.tungstenShovel) return 0.50F;
        if (!(tool.getItem() instanceof ShovelItem shovel)) return 0.08F;
        return switch (shovel.toolMaterial.getHarvestLevel()) {
            case 0 -> 0.08F;
            case 1 -> 0.15F;
            case 2 -> 0.20F;
            case 3 -> 0.35F;
            default -> 0.40F;
        };
    }
}
