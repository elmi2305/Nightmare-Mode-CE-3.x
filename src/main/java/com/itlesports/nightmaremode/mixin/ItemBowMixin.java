package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.ItemBow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemBow.class)
public class ItemBowMixin {
    @ModifyConstant(method = "applyBowEnchantmentsToArrow", constant = @Constant(doubleValue = 0.5))
    private double reducePowerDefaultScaling(double constant){
        return 0.15d;
    }
}
