package com.itlesports.nightmaremode.mixin.entity;

import net.minecraft.src.EntityVillager;
import net.minecraft.src.MerchantRecipeList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityVillager.class)
public interface EntityVillagerAccessor {
    @Accessor("buyingList")
    void nightmareMode$setBuyingList(MerchantRecipeList recipes);

    @Invoker("checkForNewTrades")
    void nightmareMode$generateTrades(int count);
}
