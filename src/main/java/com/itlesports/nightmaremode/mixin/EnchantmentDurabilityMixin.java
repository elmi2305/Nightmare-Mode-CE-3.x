package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Enchantment;
import net.minecraft.src.EnchantmentDurability;
import net.minecraft.src.EnumEnchantmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentDurability.class)
public class EnchantmentDurabilityMixin extends Enchantment {
    protected EnchantmentDurabilityMixin(int par1, int par2, EnumEnchantmentType par3EnumEnchantmentType) {
        super(par1, par2, par3EnumEnchantmentType);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setApplicableToAll(int j, int par2, CallbackInfo ci){
        this.type = EnumEnchantmentType.all;
    }
}
