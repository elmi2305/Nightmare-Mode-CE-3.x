package com.itlesports.nightmaremode.mixin;

import api.entity.mob.villager.EnchantmentVillagerTrade;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = EnchantmentVillagerTrade.class, remap = false)
public class EnchantmentVillagerTradeMixin {
    @ModifyArg(method = "generateRecipe", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/EnchantmentHelper;addRandomEnchantment(Ljava/util/Random;Lnet/minecraft/src/ItemStack;I)Lnet/minecraft/src/ItemStack;", remap = true), index = 2)
    private int improvePriestEnchantments(int power) {
        return power + 3;
    }
}
