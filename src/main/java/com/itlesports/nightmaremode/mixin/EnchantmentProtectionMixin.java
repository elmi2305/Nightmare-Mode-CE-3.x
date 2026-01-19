package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentProtection.class)
public class EnchantmentProtectionMixin {
    @Inject(method = "getMaxLevel", at = @At("HEAD"),cancellable = true)
    private void limitProtectionEnchantmentsToLevelThree(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(3);
    }

    @Inject(method = "canApplyTogether", at = @At("HEAD"),cancellable = true)
    private void cannotApplyProtectionsTogether(Enchantment par1, CallbackInfoReturnable<Boolean> cir){
        if(par1 instanceof EnchantmentProtection){
            cir.setReturnValue(false);
        }
    }


    @Inject(method = "getFireTimeForEntity", at = @At("RETURN"), cancellable = true)
    private static void makeChainArmorFireResistant(Entity entity, int i, CallbackInfoReturnable<Integer> cir){
        int chain;
        if(entity instanceof EntityLivingBase e && (chain = getChainArmorWornCount(e)) != 0){
            double multiplier = 0.15f * (Math.log(chain * chain + 1) * 1.5d); // I love adding unnecessary complexity to random formulas. I promise it actually works really well
            i -= MathHelper.floor_float((float) ((float)i * multiplier));
            cir.setReturnValue(i);
        }
    }

    @Unique
    private static int getChainArmorWornCount(EntityLivingBase e){
        int j = 0;
        for(int i = 1; i < 5; i++){
            ItemStack tempStack;

            if((tempStack = e.getCurrentItemOrArmor(i)) != null && tempStack.getItem() instanceof ItemArmor item){
                if(item.getArmorMaterial() == EnumArmorMaterial.CHAIN){
                    j++;
                }
            }
        }
        return j;
    }
}
