package com.itlesports.nightmaremode.mixin;

import btw.item.items.ArmorItemSpecial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ArmorItemSpecial.class)
public class ArmorItemSpecialMixin {
    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 12),remap = false)
    private int extendDurabilityOfSpectacles(int constant){
        return 300;
    }
}
