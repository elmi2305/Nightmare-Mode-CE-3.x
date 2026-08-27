package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.ArmorSetHelper;
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

        if(par1 instanceof EnchantmentProtection && par1 != Enchantment.featherFalling){ // isn't feather falling
            cir.setReturnValue(false);
        }
    }


    @Inject(method = "getFireTimeForEntity", at = @At("RETURN"), cancellable = true)
    private static void applyHeatResistantArmor(Entity entity, int fireTicks, CallbackInfoReturnable<Integer> cir){
        int adjustedTicks = cir.getReturnValue();
        if(entity instanceof EntityLivingBase wearer){
            boolean changed = false;
            int chainPieces = getChainArmorWornCount(wearer);
            if (chainPieces > 0) {
                double multiplier = 0.15F * (Math.log(chainPieces * chainPieces + 1) * 1.5D);
                adjustedTicks -= MathHelper.floor_float((float)(adjustedTicks * multiplier));
                changed = true;
            }
            float reduction = ArmorSetHelper.getFireTimeReduction(wearer);
            reduction = Math.min(0.8F, reduction + ArmorSetHelper.getAdditionalFireTimeReduction(wearer));
            if (reduction > 0.0F) {
                adjustedTicks = MathHelper.ceiling_float_int(adjustedTicks * (1.0F - reduction));
                changed = true;
            }
            if (changed) cir.setReturnValue(Math.max(1, adjustedTicks));
        }
    }

    @Unique
    private static int getChainArmorWornCount(EntityLivingBase wearer){
        int count = 0;
        for(int slot = 1; slot < 5; ++slot){
            ItemStack stack = wearer.getCurrentItemOrArmor(slot);
            if(stack != null && stack.getItem() instanceof ItemArmor armor
                    && armor.getArmorMaterial() == EnumArmorMaterial.CHAIN){
                ++count;
            }
        }
        return count;
    }
}
