package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.item.items.template.NMFoodItem;
import com.itlesports.nightmaremode.util.NMSanityUtils;
import net.minecraft.src.*;

import static btw.community.nightmaremode.NightmareMode.SANITY_ITEM_COOLDOWN;

public class ItemSanityConsumable extends NMFoodItem {
    private final double restoredPercent;

    public ItemSanityConsumable(int id, String name, double restoredPercent) {
        super(id, 1, 0.0F, false, name, false);
        this.restoredPercent = restoredPercent;
        this.setAlwaysEdible();
        this.setMaxStackSize(4);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.getTotalWorldTime() >= player.getData(SANITY_ITEM_COOLDOWN)) {
            player.setItemInUse(stack, getMaxItemUseDuration(stack));
        } else player.onCantConsume();
        return stack;
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            NMSanityUtils.restore(player, NMSanityUtils.getCapacity(player) * restoredPercent);
            player.setData(SANITY_ITEM_COOLDOWN, world.getTotalWorldTime() + 600L);
        }
    }
}
