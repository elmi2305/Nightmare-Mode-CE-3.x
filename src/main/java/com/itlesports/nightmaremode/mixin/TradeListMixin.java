package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.villager.trade.TradeList;
import btw.entity.mob.villager.trade.TradeProvider;
import btw.item.BTWItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TradeList.class)
public class TradeListMixin {
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeItem;fromID(III)Lbtw/entity/mob/villager/trade/TradeItem;"),remap = false)
    private static void lowerTrades24(Args args) {
        args.set(1, 1);
        args.set(2, 2); // both planter trades
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;emeraldCost(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
            ordinal = 6),remap = false)
    private static void lowerTrades25(Args args) {
        args.set(0, 2);
        args.set(1, 3); // mycelium 12-16 emerald cost
    }
}
