package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BTWItems.class)
public class BTWItemsMixin {
    @ModifyArg(method = "instantiateModItems", at = @At(value = "INVOKE", target = "Lbtw/item/items/ArmorItemSteel;<init>(III)V", ordinal = 0), index = 2)
    private static int steelHelmet(int w){
        return 1;
    }
    @ModifyArg(method = "instantiateModItems", at = @At(value = "INVOKE", target = "Lbtw/item/items/ArmorItemSteel;<init>(III)V", ordinal = 1), index = 2)
    private static int steelChest(int w){
        return 4;
    }
    @ModifyArg(method = "instantiateModItems", at = @At(value = "INVOKE", target = "Lbtw/item/items/ArmorItemSteel;<init>(III)V", ordinal = 2), index = 2)
    private static int steelLegs(int w){
        return 3;
    }
    @ModifyArg(method = "instantiateModItems", at = @At(value = "INVOKE", target = "Lbtw/item/items/ArmorItemSteel;<init>(III)V", ordinal = 3), index = 2)
    private static int steelBoots(int w){
        return 1;
    }
}
