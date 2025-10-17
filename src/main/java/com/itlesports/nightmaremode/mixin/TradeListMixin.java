package com.itlesports.nightmaremode.mixin;

import btw.entity.mob.villager.trade.TradeList;
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
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 0),remap = false)
    private static void lowerTrades0(Args args) {
        args.set(0, 16);
        args.set(1, 32); // dirt 48 - 64
    }
    // wood start
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 1),remap = false)
    private static void lowerTrades1(Args args) {
        args.set(0, 12);
        args.set(1, 24);
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 2),remap = false)
    private static void lowerTrades2(Args args) {
        args.set(0, 12);
        args.set(1, 24);
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 3),remap = false)
    private static void lowerTrades3(Args args) {
        args.set(0, 12);
        args.set(1, 24);
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 4),remap = false)
    private static void lowerTrades4(Args args) {
        args.set(0, 12);
        args.set(1, 24);
    }
    // wood end
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 5),remap = false)
    private static void lowerTrades5(Args args) {
        args.set(0, 4);
        args.set(1, 6); // wool 16 - 24
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 6),remap = false)
    private static void lowerTrades6(Args args) {
        args.set(0, 6);
        args.set(1, 12); // bone meal 32-48
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 7),remap = false)
    private static void lowerTrades7(Args args) {
        args.set(0, 6);
        args.set(1, 12); // sugar 10 - 20
    }
    // 8 skipped, sugar 10-20
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 9),remap = false)
    private static void lowerTrades9(Args args) {
        args.set(0, 4);
        args.set(1, 9); // cocoa beans 10 - 16
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 10),remap = false)
    private static void lowerTrades10(Args args) {
        args.set(0, 3);
        args.set(1, 6); // brown mushy 10 - 16
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 11),remap = false)
    private static void lowerTrades11(Args args) {
        args.set(0, 4);
        args.set(1, 8); // hemp seed 24 - 32
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 12),remap = false)
    private static void lowerTrades12(Args args) {
        args.set(0, 3);
        args.set(1, 6); // egg 8-12
    }
    // 13 skipped, glass pane 16-32
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 14),remap = false)
    private static void lowerTrades14(Args args) {
        args.set(0, 2);
        args.set(1, 8); // wheat 8-16
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 15),remap = false)
    private static void lowerTrades15(Args args) {
        args.set(0, 1);
        args.set(1, 2); // apple 2-4
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 16),remap = false)
    private static void lowerTrades16(Args args) {
        args.set(0, 4);
        args.set(1, 8); // melon 8-12
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 17),remap = false)
    private static void lowerTrades17(Args args) {
        args.set(0, 1);
        args.set(1, 3); // pumpkin 8-12
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 18),remap = false)
    private static void lowerTrades18(Args args) {
        args.set(0, 1);
        args.set(1, 2); // melon
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 19),remap = false)
    private static void lowerTrades19(Args args) {
        args.set(0, 1);
        args.set(1, 3); // mushy stew 4-5
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 20),remap = false)
    private static void lowerTrades20(Args args) {
        args.set(0, 1);
        args.set(1, 2); // scrambled egg 4-5
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 21),remap = false)
    private static void lowerTrades21(Args args) {
        args.set(0, 1);
        args.set(1, 2); // light block 2-4 & stump remover somehow they're both 1-2 lol
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 22),remap = false)
    private static void lowerTrades22(Args args) {
        args.set(0, 2);
        args.set(1, 4); // Chowder 4-8
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 23),remap = false)
    private static void lowerTrades23(Args args) {
        args.set(0, 3);
        args.set(1, 6); // cookie 8-10
    }
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



    // LIBRARIAN


    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                                  ordinal = 0),remap = false)
    private static void lowerLibrarianTrades0(Args args) {
        args.set(0, 4);
        args.set(1, 8); // paper 24-32
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 1),remap = false)
    private static void lowerLibrarianTrades1(Args args) {
        args.set(0, 4);
        args.set(1, 6); // ink sac 24-32
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 2),remap = false)
    private static void lowerLibrarianTrades2(Args args) {
        args.set(0, 3);
        args.set(1, 5); // feather 16-24
    }

    // 4 skipped - redstone 32-48
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 4),remap = false)
    private static void lowerLibrarianTrades4(Args args) {
        args.set(0, 1);
        args.set(1, 2); // latch 4-6
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 5),remap = false)
    private static void lowerLibrarianTrades5(Args args) {
        args.set(0, 3);
        args.set(1, 8); // nether wart 16-24
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 6),remap = false)
    private static void lowerLibrarianTrades6(Args args) {
        args.set(0, 8);
        args.set(1, 16); // glowstone 16-24
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 7),remap = false)
    private static void lowerLibrarianTrades7(Args args) {
        args.set(0, 6);
        args.set(1, 12); // nitre 32 - 48
    }
    // skipped 8 bat wing 4-8
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 9),remap = false)
    private static void lowerLibrarianTrades9(Args args) {
        args.set(0, 2);
        args.set(1, 4); // spider eye 4-8
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 10),remap = false)
    private static void lowerLibrarianTrades10(Args args) {
        args.set(0, 1);
        args.set(1, 3); // witch wart 4-6
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 11),remap = false)
    private static void lowerLibrarianTrades11(Args args) {
        args.set(0, 2);
        args.set(1, 6); // gland 12-16
    }
    // skipped 12 venom sack 4-6
    // skipped 13 ghast tear 4-6
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 14),remap = false)
    private static void lowerLibrarianTrades14(Args args) {
        args.set(0, 2);
        args.set(1, 3); // magma cream 8-12
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 15),remap = false)
    private static void lowerLibrarianTrades15(Args args) {
        args.set(0, 2);
        args.set(1, 3); // blaze powder 32-48
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 16),remap = false)
    private static void lowerLibrarianTrades16(Args args) {
        args.set(0, 4);
        args.set(1, 8); // brimstone 24-32
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 17),remap = false)
    private static void lowerLibrarianTrades17(Args args) {
        args.set(0, 1);
        args.set(1, 1); // blood sapling 8-12
    }



    // PRIEST
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 0),remap = false)
    private static void priest0LowerTrades(Args args) {
        args.set(0, 3);
        args.set(1, 6); // hemp 16-24
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 1),remap = false)
    private static void priest1LowerTrades(Args args) {
        args.set(0, 2);
        args.set(1, 4); // red mush 10-16
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 2),remap = false)
    private static void priest2LowerTrades(Args args) {
        args.set(0, 3);
        args.set(1, 6); // cactus 32-48
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 3),remap = false)
    private static void priest3LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // mob head 6-12
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 4),remap = false)
    private static void priest4LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // mob head 6-12
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 5),remap = false)
    private static void priest5LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // mob head 6-12
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 6),remap = false)
    private static void priest6LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // mob head 6-12
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
            ordinal = 8),remap = false)
    private static void priest8LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // candle 12-16
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
            ordinal = 9),remap = false)
    private static void priest9LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // candle 12-16
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
            ordinal = 10),remap = false)
    private static void priest10LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // candle 12-16
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
            ordinal = 11),remap = false)
    private static void priest11LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // candle 12-16
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
            ordinal = 12),remap = false)
    private static void priest12LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // candle 12-16
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
            ordinal = 13),remap = false)
    private static void priest13LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // candle 12-16
    }



    @ModifyArg(method = "addPriestTrades", at = @At(value = "INVOKE", target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellItemStep;item(I)Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;",ordinal = 6),remap = false)
    private static int noVessel(int id){
        return BTWItems.ocularOfEnder.itemID;
    }


    // BLACKSMITH

    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 0),remap = false)
    private static void BSmith0LowerTrades(Args args) {
        args.set(0, 8);
        args.set(1, 12); // coal 16-24
    }
    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 1),remap = false)
    private static void BSmith1LowerTrades(Args args) {
        args.set(0, 16);
        args.set(1, 32); // wood 32-48
    }
    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 2),remap = false)
    private static void BSmith2LowerTrades(Args args) {
        args.set(0, 18);
        args.set(1, 28); // iron nugget 32-48
    }
    // 3 skipped, nethercoal 12-20
    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 4),remap = false)
    private static void BSmith4LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // hibachi 2-3
    }
    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 5),remap = false)
    private static void BSmith5LowerTrades(Args args) {
        args.set(0, 6);
        args.set(1, 12); // oysters 12-16
    }
    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 6),remap = false)
    private static void BSmith6LowerTrades(Args args) {
        args.set(0, 6);
        args.set(1, 14); // gold nugget 18-27
    }
    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 7),remap = false)
    private static void BSmith7LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 3); // diamond 2-3
    }
    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 8),remap = false)
    private static void BSmith8LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 2); // soul urn 2-3
    }
    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 9),remap = false)
    private static void BSmith9LowerTrades(Args args) {
        args.set(0, 1);
        args.set(1, 3); // haft 2-4
    }

    // BUTCHER
    @ModifyArgs(method = "addButcherTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 0),remap = false)
    private static void Butcher0LowerTrades(Args args) {
        args.set(0, 2);
        args.set(1, 5); // arrow 16-24
    }


    @ModifyArgs(method = "addButcherTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 7),remap = false)
    private static void Butcher7LowerTrades(Args args) {
        args.set(0, 2);
        args.set(1, 5); // flour 16-24
    }
    @ModifyArgs(method = "addButcherTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 8),remap = false)
    private static void Butcher8LowerTrades(Args args) {
        args.set(0, 2);
        args.set(1, 5); // dung 10-16
    }
    @ModifyArgs(method = "addButcherTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 9),remap = false)
    private static void Butcher9LowerTrades(Args args) {
        args.set(0, 12);
        args.set(1, 16); // bark 48-64
    }
    @ModifyArgs(method = "addButcherTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 15),remap = false)
    private static void Butcher15LowerTrades(Args args) {
        args.set(0, 2);
        args.set(1, 4); // carrot 10-16
    }
    @ModifyArgs(method = "addButcherTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 16),remap = false)
    private static void Butcher16LowerTrades(Args args) {
        args.set(0, 2);
        args.set(1, 4); // potato 10-16
    }
//
//    @Inject(method = "addFarmerTrades", at = @At("TAIL"),remap = false)
//    private static void customFarmerTrades(CallbackInfo ci){
//        EntityVillager.removeLevelUpTrade(0,2);
//        EntityVillager.removeCustomTrade(0,TradeProvider.getBuilder().profession(0).level(5).arcaneScroll().scrollEnchant(Enchantment.looting).secondaryEmeraldCost(12, 16).mandatory().build());
//
//        TradeProvider.getBuilder().profession(0).level(1).sell().item(Block.grass.blockID).itemCount(2,4).weight(0.3f).addToTradeList();
//        TradeProvider.getBuilder().profession(0).level(1).convert().input(TradeItem.fromIDAndMetadata(Block.tallGrass.blockID,1,8,16)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,2)).output(TradeItem.fromID(BTWItems.hempSeeds.itemID,2,6)).weight(0.3f).addToTradeList();
//        TradeProvider.getBuilder().profession(0).level(2).buy().item(BTWBlocks.millstone.blockID).emeraldCost(2, 2).addAsLevelUpTrade();
//        TradeProvider.getBuilder().profession(0).level(2).buy().item(Item.shears.itemID).buySellSingle().weight(0.4f).addToTradeList();
//        TradeProvider.getBuilder().profession(0).level(3).buy().item(BTWItems.redMushroom.itemID).itemCount(4, 8).weight(1.2f).addToTradeList();
//        TradeProvider.getBuilder().profession(0).level(3).buy().item(Item.bucketWater.itemID).buySellSingle().addToTradeList();
//        TradeProvider.getBuilder().profession(0).level(4).buy().item(BTWItems.chowder.itemID).itemCount(2, 4).addToTradeList();
//        TradeProvider.getBuilder().profession(0).level(5).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,8,16)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("efficiency"))).mandatory().addToTradeList();
//    }
//
//    @Inject(method = "addLibrarianTrades", at = @At("TAIL"),remap = false)
//    private static void customLibrarianTrades(CallbackInfo ci){
//        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().profession(1).level(1).buy().item(Item.paper.itemID).itemCount(24, 32).build());
//        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().profession(1).level(2).variants().addTradeVariant(TradeProvider.getBuilder().profession(1).level(2).convert().input(TradeItem.fromID(BTWItems.redstoneEye.itemID, 2)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.detectorBlock.blockID)).build()).addTradeVariant(TradeProvider.getBuilder().profession(1).level(2).convert().input(TradeItem.fromID(BTWItems.redstoneEye.itemID, 4)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.buddyBlock.blockID)).build()).addTradeVariant(TradeProvider.getBuilder().profession(1).level(2).convert().input(TradeItem.fromID(Block.cobblestoneMossy.blockID, 6)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.blockDispenser.blockID)).build()).finishVariants().mandatory().build());
//        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().profession(1).level(5).convert().input(TradeItem.fromID(Item.enderPearl.itemID)).conversionCost(6, 8).output(TradeItem.fromID(Item.eyeOfEnder.itemID)).mandatory().build());
//        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().profession(1).level(5).arcaneScroll().scrollEnchant(Enchantment.power).secondaryEmeraldCost(16, 24).mandatory().build());
//
//        TradeProvider.getBuilder().profession(1).level(1).buy().item(NMItems.ironKnittingNeedles.itemID).emeraldCost(2,3).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(2).buy().item(Block.bookShelf.blockID).itemCount(1,2).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(2).buy().item(Item.book.itemID).itemCount(3,6).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(2).buy().item(Item.redstoneRepeater.itemID).itemCount(1,2).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(3).buy().item(BTWItems.hellfireDust.itemID).itemCount(16,24).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(3).buy().item(Item.glassBottle.itemID).itemCount(16,24).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(3).convert().input(TradeItem.fromIDAndMetadata(BTWItems.wool.itemID,15,4,6)).conversionCost(1, 2).output(TradeItem.fromID(NMItems.bandage.itemID,1,2)).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(4).buy().item(BTWBlocks.blockDispenser.blockID).itemCount(1,2).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(4).buy().item(BTWBlocks.buddyBlock.blockID).itemCount(1,2).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(4).buy().item(BTWBlocks.detectorBlock.blockID).itemCount(1,3).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(4).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,12,24)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("blast"))).weight(0.1f).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(4).sell().item(BTWItems.soulFlux.itemID).itemCount(2,4).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(5).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,24,32)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("power"))).addToTradeList();
//        TradeProvider.getBuilder().profession(1).level(5).convert().input(TradeItem.fromID(BTWItems.corpseEye.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,4,10)).output(TradeItem.fromID(Item.eyeOfEnder.itemID)).mandatory().addToTradeList();
//    }
//
//
//
//    @Inject(method = "addPriestTrades", at = @At("TAIL"),remap = false)
//    private static void customPriestTrades(CallbackInfo ci){
//        EntityVillager.removeCustomTrade(2, TradeProvider.getBuilder().profession(2).level(5).arcaneScroll().scrollEnchant(Enchantment.fortune).secondaryEmeraldCost(24, 32).mandatory().build());
//
//        TradeProvider.getBuilder().profession(2).level(2).buy().item(Item.netherStalkSeeds.itemID).itemCount(12,16).addToTradeList();
//        TradeProvider.getBuilder().profession(2).level(3).buy().item(BTWItems.nitre.itemID).itemCount(16,32).addToTradeList();
//        TradeProvider.getBuilder().profession(2).level(3).sell().item(Block.enchantmentTable.blockID).emeraldCost(6,10).weight(0.2f).addToTradeList();
//        TradeProvider.getBuilder().profession(2).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,32,64)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("fortune"))).weight(0.1f).addToTradeList();
//        TradeProvider.getBuilder().profession(2).level(3).convert().input(TradeItem.fromID(Item.potion.itemID)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,3)).output(TradeItem.fromIDAndMetadata(Item.potion.itemID,16453,2)).addToTradeList();
////        TradeProvider.getBuilder().profession(2).level(3).sell().item(Item.slimeBall.itemID).itemCount(2,4).weight(1.3f).addToTradeList();
//        TradeProvider.getBuilder().profession(2).level(4).convert().input(TradeItem.fromID(Item.appleGold.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,12,24)).output(TradeItem.fromIDAndMetadata(Item.appleGold.itemID,1)).mandatory().addToTradeList();
//        TradeProvider.getBuilder().profession(2).level(5).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,16,26)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("prot"))).addToTradeList();
//        TradeProvider.getBuilder().profession(2).level(5).convert().input(TradeItem.fromID(NMItems.rifle.itemID)).secondInput(TradeItem.fromID(NMItems.rpg.itemID)).output(TradeItem.fromID(Block.dragonEgg.blockID)).mandatory().addToTradeList();
//
//    }
//
//
//    @Inject(method = "addBlacksmithTrades", at = @At("TAIL"),remap = false)
//    private static void customBlacksmithTrades(CallbackInfo ci){
//        EntityVillager.removeCustomTrade(3, TradeProvider.getBuilder().profession(3).level(5).arcaneScroll().scrollEnchant(Enchantment.unbreaking).secondaryEmeraldCost(16, 24).mandatory().build());
//
//        TradeProvider.getBuilder().profession(3).level(1).buy().item(Item.pickaxeStone.itemID).buySellSingle().addToTradeList();
//        TradeProvider.getBuilder().profession(3).level(2).sell().item(NMItems.bandage.itemID).itemCount(2,2).addToTradeList();
//        TradeProvider.getBuilder().profession(3).level(2).buy().item(Item.redstone.itemID).itemCount(32,64).weight(0.8f).addToTradeList();
//        TradeProvider.getBuilder().profession(3).level(2).buy().item(Item.flintAndSteel.itemID).buySellSingle().addToTradeList();
//        TradeProvider.getBuilder().profession(3).level(3).convert().input(TradeItem.fromIDAndMetadata(BTWBlocks.aestheticOpaque.blockID, 7,4,8)).secondInput(TradeItem.EMPTY).output(TradeItem.fromID(Item.emerald.itemID,1)).addToTradeList();
//        TradeProvider.getBuilder().profession(3).level(3).buy().item(BTWItems.diamondArmorPlate.itemID).buySellSingle().addToTradeList();
//        TradeProvider.getBuilder().profession(3).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,24,32)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("looting"))).weight(0.1f).addToTradeList();
//        TradeProvider.getBuilder().profession(3).level(3).buy().item(BTWItems.padding.itemID).itemCount(6,10).addToTradeList();
//        TradeProvider.getBuilder().profession(3).level(3).sell().item(Item.appleGold.itemID).emeraldCost(8,16).addToTradeList();
//        TradeProvider.getBuilder().profession(3).level(3).convert().input(TradeItem.fromID(Item.potion.itemID)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,3)).output(TradeItem.fromIDAndMetadata(Item.potion.itemID,8201)).addToTradeList();
//        TradeProvider.getBuilder().profession(3).level(4).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,12,18)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("unbreaking"))).weight(0.6f).addToTradeList();
//    }
//
//
//    @Inject(method = "addButcherTrades", at = @At("TAIL"),remap = false)
//    private static void customButcherTrades(CallbackInfo ci){
//        EntityVillager.removeCustomTrade(4, TradeProvider.getBuilder().profession(4).level(5).arcaneScroll().scrollEnchant(Enchantment.sharpness).secondaryEmeraldCost(16, 24).mandatory().build());
//
//        TradeProvider.getBuilder().profession(4).level(1).buy().item(Item.leash.itemID).itemCount(6,10).addToTradeList();
//        TradeProvider.getBuilder().profession(4).level(2).buy().item(Item.swordIron.itemID).buySellSingle().weight(0.3f).addToTradeList();
//        TradeProvider.getBuilder().profession(4).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,6,12)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("thorns"))).addToTradeList();
//        TradeProvider.getBuilder().profession(4).level(4).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,24,32)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("feather"))).weight(0.1f).addToTradeList();
//    }
//
//
//    @Unique private static int getScrollMetadata(String input){
//        HashMap<String, Integer> dictionary = new HashMap<>();
//        dictionary.put("prot",0);
//        dictionary.put("fire prot",1);
//        dictionary.put("feather",2);
//        dictionary.put("blast",3);
//        dictionary.put("proj prot",4);
//        dictionary.put("resp",5);
//        dictionary.put("aqua",6);
//        dictionary.put("thorns",7);
//        dictionary.put("sharp",16);
//        dictionary.put("smite",17);
//        dictionary.put("bane",18);
//        dictionary.put("knockback",19);
//        dictionary.put("fire aspect",20);
//        dictionary.put("looting",21);
//        dictionary.put("efficiency",32);
//        dictionary.put("silk",33);
//        dictionary.put("unbreaking",34);
//        dictionary.put("fortune",35);
//        dictionary.put("power",48);
//        dictionary.put("punch",49);
//        dictionary.put("flame",50);
//        dictionary.put("infinity",51);
//
//        return dictionary.get(input);
//    }
//
//    @Inject(method = "addVillagerTrades", at = @At("TAIL"),remap = false)
//    private static void addNightmareVillagerTrades(CallbackInfo ci){
//        // Level 1 Trades
//        TradeProvider.getBuilder().profession(5).level(1).buy().item(Item.rottenFlesh.itemID).itemCount(8, 16).defaultTrade().weight(0.3f).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(1).buy().item(Item.dyePowder.itemID, Color.BLACK.colorID).itemCount(12, 18).weight(0.3f).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(1).buy().item(NMItems.magicFeather.itemID).itemCount(1, 2).weight(0.3f).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(1).buy().item(NMItems.bloodMilk.itemID).buySellSingle().weight(0.2f).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(1).buy().item(Item.enderPearl.itemID).buySellSingle().addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(1).buy().item(NMItems.fireRod.itemID).itemCount(1, 3).weight(0.5f).addToTradeList();
//
//        // Level 2 Trades
//        TradeProvider.getBuilder().profession(5).level(2).convert().input(TradeItem.fromIDAndMetadata(Item.potion.itemID, 8229, 1,2)).secondInput(TradeItem.EMPTY).output(TradeItem.fromID(Item.emerald.itemID)).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(2).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.darksunFragment.itemID,4,8)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("infinity"))).weight(0.1f).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(2).buy().item(NMItems.decayedFlesh.itemID).itemCount(4, 6).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(2).buy().item(NMItems.silverLump.itemID).itemCount(2, 3).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(2).sell().item(NMItems.dungApple.itemID).buySellSingle().weight(0.3f).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(2).buy().item(NMItems.creeperTear.itemID).itemCount(1, 1).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(2).buy().item(NMItems.shadowRod.itemID).itemCount(1, 2).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(2).sell().item(BTWItems.soulFlux.itemID).itemCount(4, 8).addToTradeList();
//
//
//        // Level 3 Trades
//        TradeProvider.getBuilder().profession(5).level(3).sell().item(Item.blazeRod.itemID).emeraldCost(2, 4).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(3).sell().item(Item.nameTag.itemID).emeraldCost(2, 4).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(3).buy().item(NMItems.spiderFangs.itemID).itemCount(2, 4).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(3).buy().item(NMItems.sulfur.itemID).itemCount(3, 6).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(3).buy().item(NMItems.charredFlesh.itemID).itemCount(1, 2).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(3).sell().item(Item.magmaCream.itemID).itemCount(6, 8).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(3).buy().item(NMItems.greg.itemID).itemCount(1, 1).weight(0.5f).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.darksunFragment.itemID,4,8)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("sharp"))).weight(0.15f).addToTradeList();
//
//        // Level 4 Trades
//        TradeProvider.getBuilder().profession(5).level(4).sell().item(Item.ghastTear.itemID).itemCount(4, 6).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.waterRod.itemID).itemCount(1, 2).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.voidSack.itemID).itemCount(1,3).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.creeperChop.itemID).itemCount(1, 1).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.elementalRod.itemID).itemCount(1, 1).weight(0.6f).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.voidMembrane.itemID).itemCount(1, 1).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.darksunFragment.itemID).itemCount(1, 1).addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(4).buy().item(Item.eyeOfEnder.itemID).buySellSingle().addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.creeperTear.itemID).itemCount(1, 1).weight(0.2f).addToTradeList();
//
//        // Level 5 Trades
//        TradeProvider.getBuilder().profession(5).level(5).sell().item(Item.enderPearl.itemID).itemCount(32, 64).mandatory().addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(5).sell().item(NMItems.rifle.itemID).buySellSingle().mandatory().addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(5).sell().item(NMItems.rpg.itemID).buySellSingle().mandatory().addToTradeList();
//        TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.potion.itemID,16421,64)).mandatory().addToTradeList();
//
//        TradeProvider.getBuilder().profession(5).level(5).variants()
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.waterStill.blockID).buySellSingle().build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.lavaStill.blockID).buySellSingle().build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.fire.blockID).itemCount(1, 64).build())
//                .finishVariants().mandatory().addToTradeList();
//
//        TradeProvider.getBuilder().profession(5).level(5).variants()
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.bedrock.blockID).buySellSingle().build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.portal.blockID).itemCount(6, 6).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.endPortal.blockID).itemCount(9, 9).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.endPortalFrame.blockID).itemCount(12, 12).build())
//                .finishVariants().mandatory().addToTradeList();
//
//        TradeProvider.getBuilder().profession(5).level(5).variants()
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.mobSpawner.blockID).buySellSingle().build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.dragonEgg.blockID).buySellSingle().build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.workbench.blockID).buySellSingle().build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(BTWBlocks.axlePowerSource.blockID).buySellSingle().build())
//                .finishVariants().mandatory().addToTradeList();
//
//        TradeProvider.getBuilder().profession(5).level(5).variants()
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 50, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 51, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 52, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 53, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 54, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 55, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 56, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 57, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 58, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 59, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 60, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 61, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 62, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 63, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 64, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 65, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 66, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 90, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 91, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 92, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 93, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 94, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 95, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 96, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 97, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 98, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 99, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 100, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 238, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 240, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 600, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 601, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 602, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 603, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 604, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2301, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2302, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2303, 3, 6)).build())
//                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2306, 3, 6)).build())
//                .finishVariants().mandatory().addToTradeList();
//
//
//
//        // Level up Trades
//        TradeProvider.getBuilder().profession(5).level(1).buy().item(Block.dragonEgg.blockID).itemCount(1,1).addAsLevelUpTrade();
//        TradeProvider.getBuilder().profession(5).level(2).buy().item(NMItems.voidMembrane.itemID).itemCount(3,3).addAsLevelUpTrade();
//        TradeProvider.getBuilder().profession(5).level(3).buy().item(NMItems.darksunFragment.itemID).itemCount(16,16).addAsLevelUpTrade();
//        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.starOfTheBloodGod.itemID).itemCount(1,1).addAsLevelUpTrade();
//    }
}
