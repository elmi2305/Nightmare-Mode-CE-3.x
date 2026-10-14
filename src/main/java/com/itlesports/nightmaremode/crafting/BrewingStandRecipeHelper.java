package com.itlesports.nightmaremode.crafting;

import com.itlesports.nightmaremode.crafting.manager.BrewingStandRecipeManager;
import com.itlesports.nightmaremode.util.MachineSpeedBonuses;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityBrewingStand;

public final class BrewingStandRecipeHelper {
    private BrewingStandRecipeHelper() {
    }

    public static int getBrewTime(TileEntityBrewingStand brewingStand) {
        ItemStack[] bottleSlots = getBottleSlots(brewingStand);
        float multiplier = BrewingStandRecipeManager.instance.getBatchTimeMultiplier(
                brewingStand.getStackInSlot(3), bottleSlots);
        return getBrewTime(brewingStand, multiplier <= 0.0F ? 1.0F : multiplier);
    }

    public static int getBrewTime(TileEntityBrewingStand brewingStand, float multiplier) {
        float speedBonus = MachineSpeedBonuses.get(brewingStand);
        return Math.max(1, Math.round(12000.0F * multiplier / (1.0F + speedBonus)));
    }

    public static ItemStack[] getBottleSlots(TileEntityBrewingStand brewingStand) {
        return new ItemStack[]{
                brewingStand.getStackInSlot(0),
                brewingStand.getStackInSlot(1),
                brewingStand.getStackInSlot(2)
        };
    }
}
