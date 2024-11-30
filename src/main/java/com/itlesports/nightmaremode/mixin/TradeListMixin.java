package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.entity.mob.villager.trade.*;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
        args.set(1, 20); // feather 16-24
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
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 7),remap = false)
    private static void lowerLibrarianTrades7(Args args) {
        args.set(0, 12);
        args.set(1, 24); // nitre 32 - 48
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
        args.set(0, 3);
        args.set(1, 5); // witch wart 4-6
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
    // skipped 15 blaze powder 8-12
    @ModifyArgs(method = "addLibrarianTrades",
            at = @At(value = "INVOKE",
                    target = "Lbtw/entity/mob/villager/trade/TradeProvider$BuySellCountStep;itemCount(II)Lbtw/entity/mob/villager/trade/TradeProvider$FinalStep;",
                    ordinal = 16),remap = false)
    private static void lowerLibrarianTrades16(Args args) {
        args.set(0, 12);
        args.set(1, 24); // brimstone 24-32
    }



    @Inject(method = "addFarmerTrades", at = @At("TAIL"),remap = false)
    private static void customFarmerTrades(CallbackInfo ci){
        TradeProvider.getBuilder().profession(0).level(1).buy().item(Block.grass.blockID).itemCount(4,10).addToTradeList();
        TradeProvider.getBuilder().profession(0).level(1).convert().input(TradeItem.fromIDAndMetadata(Block.tallGrass.blockID,1,10,20)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,3)).output(TradeItem.fromID(BTWItems.hempSeeds.itemID,2,6)).addToTradeList();
        TradeProvider.getBuilder().profession(0).level(2).buy().item(Item.shears.itemID).buySellSingle().weight(0.5f).addToTradeList();
        TradeProvider.getBuilder().profession(0).level(3).buy().item(BTWItems.redMushroom.itemID).itemCount(4, 8).addToTradeList();
        TradeProvider.getBuilder().profession(0).level(3).buy().item(Item.bucketWater.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().profession(0).level(4).buy().item(BTWItems.chowder.itemID).itemCount(2, 4).addToTradeList();
    }

    @Inject(method = "addLibrarianTrades", at = @At("TAIL"),remap = false)
    private static void customLibrarianTrades(CallbackInfo ci){
        EntityVillager.removeCustomTrade(1,TradeProvider.getBuilder().profession(1).level(1).buy().item(Item.paper.itemID).itemCount(24, 32).build());
        EntityVillager.removeCustomTrade(1,TradeProvider.getBuilder().profession(1).level(2).variants().addTradeVariant(TradeProvider.getBuilder().profession(1).level(2).convert().input(TradeItem.fromID(BTWItems.redstoneEye.itemID, 2)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.detectorBlock.blockID)).build()).addTradeVariant(TradeProvider.getBuilder().profession(1).level(2).convert().input(TradeItem.fromID(BTWItems.redstoneEye.itemID, 4)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.buddyBlock.blockID)).build()).addTradeVariant(TradeProvider.getBuilder().profession(1).level(2).convert().input(TradeItem.fromID(Block.cobblestoneMossy.blockID, 6)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.blockDispenser.blockID)).build()).finishVariants().mandatory().build());


        TradeProvider.getBuilder().profession(1).level(1).buy().item(NMItems.ironKnittingNeedles.itemID).emeraldCost(2,3).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(2).buy().item(Block.bookShelf.blockID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(2).buy().item(Item.book.itemID).itemCount(3,6).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(2).buy().item(Item.redstoneRepeater.itemID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(3).buy().item(BTWItems.hellfireDust.itemID).itemCount(16,24).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(3).buy().item(Item.glassBottle.itemID).itemCount(16,24).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(3).convert().input(TradeItem.fromIDAndMetadata(BTWItems.wool.itemID,15,4,8)).conversionCost(1, 2).output(TradeItem.fromID(NMItems.bandage.itemID,1,2)).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(4).buy().item(BTWBlocks.blockDispenser.blockID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(4).buy().item(BTWBlocks.buddyBlock.blockID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(4).buy().item(BTWBlocks.detectorBlock.blockID).itemCount(1,3).addToTradeList();
    }



    @Inject(method = "addPriestTrades", at = @At("TAIL"),remap = false)
    private static void customPriestTrades(CallbackInfo ci){
        TradeProvider.getBuilder().profession(2).level(2).buy().item(Item.netherStalkSeeds.itemID).itemCount(12,16).addToTradeList();
        TradeProvider.getBuilder().profession(2).level(3).buy().item(BTWItems.nitre.itemID).itemCount(16,32).addToTradeList();
        TradeProvider.getBuilder().profession(2).level(3).convert().input(TradeItem.fromID(Item.potion.itemID)).secondInput(TradeItem.fromID(Item.emerald.itemID,2,6)).output(TradeItem.fromIDAndMetadata(Item.potion.itemID,16453,2)).addToTradeList();
        TradeProvider.getBuilder().profession(2).level(4).convert().input(TradeItem.fromID(Item.appleGold.itemID)).secondInput(TradeItem.fromID(BTWItems.soulFlux.itemID,4,8)).output(TradeItem.fromIDAndMetadata(Item.appleGold.itemID,1)).mandatory().addToTradeList();
        TradeProvider.getBuilder().profession(2).level(4).buy().item(BTWItems.soulFlux.itemID).itemCount(3,6).weight(0.5f).addToTradeList();
    }


    @Inject(method = "addBlacksmithTrades", at = @At("TAIL"),remap = false)
    private static void customBlacksmithTrades(CallbackInfo ci){
        TradeProvider.getBuilder().profession(3).level(1).buy().item(Item.pickaxeStone.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().profession(3).level(2).sell().item(NMItems.bandage.itemID).itemCount(2,2).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(2).buy().item(Item.redstone.itemID).itemCount(32,64).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(2).buy().item(Item.flintAndSteel.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).convert().input(TradeItem.fromIDAndMetadata(BTWBlocks.aestheticOpaque.blockID, 7,4,8)).secondInput(TradeItem.EMPTY).output(TradeItem.fromID(Item.emerald.itemID,1)).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).buy().item(BTWItems.diamondArmorPlate.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).buy().item(BTWItems.padding.itemID).itemCount(6,10).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).sell().item(Item.appleGold.itemID).emeraldCost(8,16).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).convert().input(TradeItem.fromID(Item.potion.itemID)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,3)).output(TradeItem.fromIDAndMetadata(Item.potion.itemID,8201)).addToTradeList();
    }

    @Inject(method = "addButcherTrades", at = @At("TAIL"),remap = false)
    private static void customButcherTrades(CallbackInfo ci){
        TradeProvider.getBuilder().profession(4).level(1).buy().item(Item.leash.itemID).itemCount(8,12).addToTradeList();
        TradeProvider.getBuilder().profession(4).level(2).buy().item(Item.swordIron.itemID).buySellSingle().weight(0.5f).addToTradeList();
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
