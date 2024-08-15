package com.itlesports.nightmaremode.mixin;

import btw.world.util.WorldUtils;
import net.minecraft.src.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyConstant(method = "calcItemStackEnchantability", constant = @Constant(intValue = 15))
    private static int increaseEnchantability(int constant){
        if(WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()){return 30;}
        return 20;
    }
}
