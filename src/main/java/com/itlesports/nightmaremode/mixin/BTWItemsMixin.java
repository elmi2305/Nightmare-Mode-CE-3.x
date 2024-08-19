package com.itlesports.nightmaremode.mixin;

import btw.item.BTWItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BTWItems.class)
public class BTWItemsMixin {
    @ModifyConstant(method = "instantiateModItems", constant = @Constant(intValue = 10,ordinal = 0),remap = false)
    private static int decreaseSteelWeightHelmet(int constant){
        return 1;
    }
    @ModifyConstant(method = "instantiateModItems", constant = @Constant(intValue = 14,ordinal = 0),remap = false)
    private static int decreaseSteelWeightChest(int constant){
        return 4;
    }
    @ModifyConstant(method = "instantiateModItems", constant = @Constant(intValue = 12,ordinal = 0),remap = false)
    private static int decreaseSteelWeightLegs(int constant){
        return 3;
    }
    @ModifyConstant(method = "instantiateModItems", constant = @Constant(intValue = 8,ordinal = 3),remap = false)
    private static int decreaseSteelWeightBoots(int constant){
        return 1;
    }
    // total steel weight: 44 -> 9 (the player can swim and suffers little hunger penalty)
}
