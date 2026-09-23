package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.world.SandboxRules;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.SlotMerchantResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotMerchantResult.class)
public abstract class SlotMerchantResultMixin {
    @Inject(method = "onPickupFromSlot", at = @At("HEAD"))
    private void nightmareMode$recordTrade(EntityPlayer player, ItemStack stack, CallbackInfo ci) {
        SandboxRules.recordAcquisition(player, stack);
    }
}
