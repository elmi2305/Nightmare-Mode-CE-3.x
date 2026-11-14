package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.entity.EntityNetherPostVillager;
import com.itlesports.nightmaremode.mixin.entity.EntityVillagerAccessor;
import net.minecraft.src.*;

public final class VillagerTradeReroll {
    private VillagerTradeReroll() {}

    public static boolean useApple(EntityVillager villager, EntityPlayer player) {
        ItemStack held = player.getHeldItem();
        if (held == null || held.getItem() != NMItems.emeraldApple) return false;
        if (villager.worldObj.isRemote) return true;
        if (villager.isChild() || !villager.isEntityAlive() || villager.isTrading()
                || villager.getCurrentTradeLevel() < 1) return true;

        MerchantRecipeList previous = villager.getRecipes(player);
        int count = previous == null || previous.isEmpty() ? villager.getCurrentMaxNumTrades() : previous.size();
        MerchantRecipeList refreshed = new MerchantRecipeList();
        EntityVillagerAccessor accessor = (EntityVillagerAccessor)villager;
        accessor.nightmareMode$setBuyingList(refreshed);
        try {
            if (villager instanceof EntityNetherPostVillager post) post.addGuaranteedTrades();
            // adding one offer at a time lets the normal generator reject duplicates already chosen.
            for (int attempt = 0; attempt < count * 3 && refreshed.size() < count; ++attempt) {
                accessor.nightmareMode$generateTrades(1);
            }
        } catch (RuntimeException exception) {
            accessor.nightmareMode$setBuyingList(previous);
            throw exception;
        }
        if (refreshed.isEmpty()) {
            accessor.nightmareMode$setBuyingList(previous);
            return true;
        }
        if (!player.capabilities.isCreativeMode && --held.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        }
        villager.worldObj.playSoundAtEntity(villager, "random.eat", 0.8F, 1.0F);
        return true;
    }
}
