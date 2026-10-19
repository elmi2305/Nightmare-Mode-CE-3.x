package com.itlesports.nightmaremode.util;

import btw.crafting.manager.CampfireCraftingManager;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

import java.util.HashMap;
import java.util.Map;

public final class NMCookedFoodTracking {
    private NMCookedFoodTracking() {}

    public static boolean isFoodOutput(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemFood
                && stack.getItem() != BTWItems.burnedMeat && stack.getItem() != BTWItems.foulFood
                && stack.getItem() != NMItems.burnedChocolateCake;
    }

    public static void creditOvenPickup(EntityPlayer player, ItemStack output) {
        if (isFoodOutput(output)) SkillHandler.incrementFoodCooked(player, output.stackSize);
    }

    public static void creditCampfirePickup(EntityPlayer player, ItemStack output) {
        if (!isFoodOutput(output)) return;
        for (Object value : CampfireCraftingManager.instance.getRecipeMap().values()) {
            if (value instanceof ItemStack recipeOutput && recipeOutput.itemID == output.itemID) {
                // a campfire holds one item; inventory insertion may already have consumed its stack size.
                SkillHandler.incrementFoodCooked(player, 1);
                return;
            }
        }
    }

    public static int countNewFood(ItemStack[] before, ItemStack[] after) {
        Map<Integer, Integer> totals = new HashMap<>();
        for (ItemStack stack : before) {
            if (isFoodOutput(stack)) totals.merge(stack.itemID, -Math.max(0, stack.stackSize), Integer::sum);
        }
        for (ItemStack stack : after) {
            if (isFoodOutput(stack)) totals.merge(stack.itemID, Math.max(0, stack.stackSize), Integer::sum);
        }
        int count = 0;
        for (int difference : totals.values()) count += Math.max(0, difference);
        return count;
    }

    public static void creditCauldronCycle(World world, int x, int y, int z, ItemStack[] before, ItemStack[] after) {
        if (world == null || world.isRemote) return;
        int count = countNewFood(before, after);
        if (count == 0) return;
        for (Object value : world.playerEntities) {
            if (value instanceof EntityPlayer player && player.isEntityAlive()
                    && player.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D) {
                SkillHandler.incrementFoodCooked(player, count);
            }
        }
    }
}
