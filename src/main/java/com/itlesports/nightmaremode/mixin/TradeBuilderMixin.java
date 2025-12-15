package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.villager.trade.TradeProvider;
import btw.entity.mob.villager.trade.VillagerTrade;
import com.itlesports.nightmaremode.tradetweaks.ApplyAction;
import com.itlesports.nightmaremode.NMInitializer;
import com.itlesports.nightmaremode.tradetweaks.TradeTweaks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TradeProvider.TradeBuilder.class)
public class TradeBuilderMixin {
    static{
        NMInitializer.editExistingTrades();
    }

    @Inject(method = "build", at = @At("HEAD"), cancellable = true, remap = false)
    private void onAddToTradeList(CallbackInfoReturnable<VillagerTrade> cir) {
        ApplyAction action = TradeTweaks.applyEditIfPresent(this);
        if (action == ApplyAction.DROP) {
            // removed the trade
            cir.cancel();
        } else if (action == ApplyAction.REPLACED) {
            // this trade was replaced so we aren't adding it again
            cir.cancel();
        }
    }
}
