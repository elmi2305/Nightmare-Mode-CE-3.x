package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.villager.TradeList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
@Mixin(TradeList.class)
public class TradeListMixin {
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lapi/entity/mob/villager/TradeItem;fromID(III)Lapi/entity/mob/villager/TradeItem;"),remap = false)
    private static void lowerTrades24(Args args) {
        args.set(1, 1);
        args.set(2, 2); // both planter trades
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lapi/entity/mob/villager/TradeProvider$BuySellCountStep;emeraldCost(II)Lapi/entity/mob/villager/TradeProvider$FinalStep;",
            ordinal = 6),remap = false
    )
    private static void lowerTrades25(Args args) {
        args.set(0, 2);
        args.set(1, 3); // mycelium 12-16 emerald cost
    }
}
