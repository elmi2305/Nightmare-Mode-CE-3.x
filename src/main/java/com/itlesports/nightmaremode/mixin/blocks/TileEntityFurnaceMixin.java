package com.itlesports.nightmaremode.mixin.blocks;

import btw.item.BTWItems;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntityFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(TileEntityFurnace.class)
public class TileEntityFurnaceMixin {
    @Inject(method = "getItemBurnTime", at = @At("RETURN"), cancellable = true)
    private void economicalEasyFuel(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (com.itlesports.nightmaremode.world.BalanceProfile.isEasy()) cir.setReturnValue(cir.getReturnValueI() * 2);
    }

    @Inject(method = "getCookTimeForCurrentItem", at = @At("RETURN"), cancellable = true)
    private void fasterEasySmelting(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(com.itlesports.nightmaremode.util.EasyBalance.processingTicks(cir.getReturnValueI()));
    }
}
