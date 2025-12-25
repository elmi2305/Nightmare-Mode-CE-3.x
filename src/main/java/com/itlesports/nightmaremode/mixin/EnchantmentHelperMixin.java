package com.itlesports.nightmaremode.mixin;

import api.world.WorldUtils;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @ModifyConstant(method = "calcItemStackEnchantability", constant = @Constant(intValue = 15))
    private static int increaseEnchantability(int constant){
        return 30;
    }

    @Inject(method = "calcItemStackEnchantability", at = @At("TAIL"),cancellable = true)
    private static void manageItemEnchantabilityScore(Random rand, int iTableSlotNum, int iNumBookShelves, ItemStack stack, CallbackInfoReturnable<Integer> cir){
        if (WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
            int a = cir.getReturnValue();
            if(iTableSlotNum == 0){a+=1;}
            if(a>7 && iTableSlotNum == 1){a = 7;}
            if(a<5 && iTableSlotNum == 1){a = 4;}
            cir.setReturnValue((a << 1) + iNumBookShelves % 2);
        } else if(WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()){
            int a = cir.getReturnValue();
            if(iTableSlotNum == 0){a+=1;}
            if(a>8 && iTableSlotNum == 1){a = 8;}
            if(a<5 && iTableSlotNum == 1){a = 5;}
            cir.setReturnValue(MathHelper.floor_double(((a << 1) + iNumBookShelves % 2) / 1.5));
        }
    }
}