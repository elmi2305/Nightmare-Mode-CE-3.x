package com.itlesports.nightmaremode;

import btw.entity.mob.villager.trade.VillagerTrade;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.MerchantRecipe;
import net.minecraft.src.MerchantRecipeList;
import net.minecraft.src.World;

import java.util.HashSet;

public class NightmareVillager extends EntityVillager {
    public NightmareVillager(World par1World) {
        super(par1World, 5);
    }
    @Override
    public int getProfessionFromClass() {
        return 5;
    }

    @Override
    protected void checkForProfessionTrades(MerchantRecipeList recipeList, int availableTrades) {
        HashSet<VillagerTrade> tradeList = new HashSet<VillagerTrade>();
        for (VillagerTrade entry : tradeByProfessionList.get(this.getProfessionFromClass())) {
            if (entry.level > this.getCurrentTradeLevel() || entry.isMandatory() || !entry.canBeAdded(this)) continue;
            tradeList.add(entry);
        }
        int maxAttempts = 50;
        for (int currentAttempts = 0; availableTrades > 0 && currentAttempts < maxAttempts; ++currentAttempts) {
            VillagerTrade trade = this.getRandomTradeFromAdjustedWeight(tradeList);
            if (this.doesRecipeListAlreadyContainRecipe(trade)) continue;
            MerchantRecipe recipe = trade.generateRecipe(this.rand);
            recipeList.add(recipe);
            --availableTrades;
        }
    }
}
