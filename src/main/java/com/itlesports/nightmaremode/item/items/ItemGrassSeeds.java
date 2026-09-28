package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.interfaces.FoodStatsExt;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemGrassSeeds extends ItemFood {
    public ItemGrassSeeds(int id) {
        super(id, 0, 0.0F, false);
        this.setAlwaysEdible();
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        ItemStack result = super.onEaten(stack, world, player);
        ((FoodStatsExt)player.getFoodStats()).nightmareMode$reduceExhaustion(0.5F);
        return result;
    }
}
