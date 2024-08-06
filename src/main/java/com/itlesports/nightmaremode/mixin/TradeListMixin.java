package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.entity.mob.villager.trade.TradeList;
import btw.item.BTWItems;
import net.minecraft.src.ItemStack;
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
        args.set(0, 6);
        args.set(1, 12); // wool 16 - 24
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 6),remap = false)
    private static void lowerTrades6(Args args) {
        args.set(0, 12);
        args.set(1, 20); // bone meal 32-48
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 7),remap = false)
    private static void lowerTrades7(Args args) {
        args.set(0, 6);
        args.set(1, 12); // flour 24 - 32
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
        args.set(0, 4);
        args.set(1, 8); // pumpkin 8-12
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 18),remap = false)
    private static void lowerTrades18(Args args) {
        args.set(0, 1);
        args.set(1, 2); // bread 4-6
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
        args.set(1, 2); // light block 2-4
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 22),remap = false)
    private static void lowerTrades22(Args args) {
        args.set(0, 2);
        args.set(1, 4); // stump remover 4-8
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
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 24),remap = false)
    private static void lowerTrades24(Args args) {
        args.set(0, 1);
        args.set(1, 2); // pumpkin pie 2-2
    }
    @ModifyArgs(method = "addFarmerTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 25),remap = false)
    private static void lowerTrades25(Args args) {
        args.set(0, 2);
        args.set(1, 6); // planter with soil 8-8
    }


    // LIBRARIAN


    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                                  ordinal = 0),remap = false)
    private static void lowerLibrarianTrades0(Args args) {
        args.set(0, 10);
        args.set(1, 20); // paper 24-32
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 1),remap = false)
    private static void lowerLibrarianTrades1(Args args) {
        args.set(0, 6);
        args.set(1, 9); // ink sac 24-32
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 2),remap = false)
    private static void lowerLibrarianTrades2(Args args) {
        args.set(0, 6);
        args.set(1, 9); // feather 16-24
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
        args.set(0, 10);
        args.set(1, 20); // nether wart 16-24
    }
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 6),remap = false)
    private static void lowerLibrarianTrades6(Args args) {
        args.set(0, 8);
        args.set(1, 16); // glowstone 16-24
    }
    // skipped 7 nitre 32-48
    // skipped 8 bat wing 4-8
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 9),remap = false)
    private static void lowerLibrarianTrades9(Args args) {
        args.set(0, 1);
        args.set(1, 4); // spider eye 4-8
    }
    // skipped 10 witch wart 4-6
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
    // skipped 15 blaze powder 8-12
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 16),remap = false)
    private static void lowerLibrarianTrades16(Args args) {
        args.set(0, 12);
        args.set(1, 24); // brimstone 24-32
    }


    // PRIEST
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 0),remap = false)
    private static void priest0LowerTrades(Args args) {
        args.set(0, 5);
        args.set(1, 10); // hemp 16-24
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 1),remap = false)
    private static void priest1LowerTrades(Args args) {
        args.set(0, 3);
        args.set(1, 6); // red mush 10-16
    }
    @ModifyArgs(method = "addPriestTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 2),remap = false)
    private static void priest2LowerTrades(Args args) {
        args.set(0, 10);
        args.set(1, 22); // cactus 32-48
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
        args.set(0, 24);
        args.set(1, 36); // iron nugget 32-48
    }
    // 3 skipped, nethercoal 12-20
    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 4),remap = false)
    private static void BSmith4LowerTrades(Args args) {
        args.set(0, 24);
        args.set(1, 36); // iron nugget 32-48
    }
    // 5 skipped, oysters 12-16
    @ModifyArgs(method = "addBlacksmithTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 6),remap = false)
    private static void BSmith6LowerTrades(Args args) {
        args.set(0, 6);
        args.set(1, 18); // gold nugget 18-27
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
        args.set(0, 6);
        args.set(1, 16); // arrow 16-24
    }
    @ModifyArgs(method = "addButcherTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 7),remap = false)
    private static void Butcher7LowerTrades(Args args) {
        args.set(0, 2);
        args.set(1, 5); // dung 10-16
    }
    @ModifyArgs(method = "addButcherTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 8),remap = false)
    private static void Butcher8LowerTrades(Args args) {
        args.set(0, 16);
        args.set(1, 32); // bark 48-64
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
}
