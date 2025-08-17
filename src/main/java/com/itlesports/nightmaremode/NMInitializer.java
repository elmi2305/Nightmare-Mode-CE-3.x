package com.itlesports.nightmaremode;

import btw.block.BTWBlocks;
import btw.crafting.manager.CrucibleStokedCraftingManager;
import btw.crafting.recipe.RecipeManager;
import btw.entity.mob.villager.trade.TradeItem;
import btw.entity.mob.villager.trade.TradeProvider;
import btw.item.BTWItems;
import btw.util.color.Color;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;


import java.util.HashMap;

public class NMInitializer {

    public static void initNightmareRecipes(){
        addCraftingRecipes();
        addCampfireRecipes();
        addCrucibleRecipes();
        addCauldronRecipes();
        addMillstoneRecipes();
        addOvenRecipes();
        addSoulforgeRecipes();
    }
    public static void initNightmareTrades(){
        addFarmerTrades();
        addButcherTrades();
        addPriestTrades();
        addLibrarianTrades();
        addBlacksmithTrades();
        addNightmareVillagerTrades();
    }

    public static void miscInit(){
        BTWItems.plateBoots.setMaxDamage(729);
        BTWItems.plateLeggings.setMaxDamage(729);
        BTWItems.plateBreastplate.setMaxDamage(729);
        BTWItems.plateHelmet.setMaxDamage(729);
    }


    private static void addFarmerTrades(){
        EntityVillager.removeLevelUpTrade(0,2);
        EntityVillager.removeCustomTrade(0, TradeProvider.getBuilder().profession(0).level(5).arcaneScroll().scrollEnchant(Enchantment.looting).secondaryEmeraldCost(12, 16).mandatory().build());

        TradeProvider.getBuilder().profession(0).level(1).sell().item(Block.grass.blockID).itemCount(2,4).weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().profession(0).level(1).convert().input(TradeItem.fromIDAndMetadata(Block.tallGrass.blockID,1,8,16)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,2)).output(TradeItem.fromID(BTWItems.hempSeeds.itemID,2,6)).weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().profession(0).level(2).buy().item(BTWBlocks.millstone.blockID).emeraldCost(2, 2).addAsLevelUpTrade();
        TradeProvider.getBuilder().profession(0).level(2).buy().item(Item.shears.itemID).buySellSingle().weight(0.4f).addToTradeList();
        TradeProvider.getBuilder().profession(0).level(3).buy().item(BTWItems.redMushroom.itemID).itemCount(4, 8).weight(1.2f).addToTradeList();
        TradeProvider.getBuilder().profession(0).level(3).buy().item(Item.bucketWater.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().profession(0).level(4).buy().item(BTWItems.chowder.itemID).itemCount(2, 4).addToTradeList();
        TradeProvider.getBuilder().profession(0).level(5).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,8,16)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("efficiency"))).mandatory().addToTradeList();
    }

    private static void addLibrarianTrades(){
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().profession(1).level(1).buy().item(Item.paper.itemID).itemCount(24, 32).build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().profession(1).level(2).variants().addTradeVariant(TradeProvider.getBuilder().profession(1).level(2).convert().input(TradeItem.fromID(BTWItems.redstoneEye.itemID, 2)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.detectorBlock.blockID)).build()).addTradeVariant(TradeProvider.getBuilder().profession(1).level(2).convert().input(TradeItem.fromID(BTWItems.redstoneEye.itemID, 4)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.buddyBlock.blockID)).build()).addTradeVariant(TradeProvider.getBuilder().profession(1).level(2).convert().input(TradeItem.fromID(Block.cobblestoneMossy.blockID, 6)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.blockDispenser.blockID)).build()).finishVariants().mandatory().build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().profession(1).level(5).convert().input(TradeItem.fromID(Item.enderPearl.itemID)).conversionCost(6, 8).output(TradeItem.fromID(Item.eyeOfEnder.itemID)).mandatory().build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().profession(1).level(5).arcaneScroll().scrollEnchant(Enchantment.power).secondaryEmeraldCost(16, 24).mandatory().build());

        TradeProvider.getBuilder().profession(1).level(1).buy().item(NMItems.ironKnittingNeedles.itemID).emeraldCost(2,3).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(2).buy().item(Block.bookShelf.blockID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(2).buy().item(Item.book.itemID).itemCount(3,6).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(2).buy().item(Item.redstoneRepeater.itemID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(3).buy().item(BTWItems.hellfireDust.itemID).itemCount(16,24).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(3).buy().item(Item.glassBottle.itemID).itemCount(16,24).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(3).convert().input(TradeItem.fromIDAndMetadata(BTWItems.wool.itemID,15,4,6)).conversionCost(1, 2).output(TradeItem.fromID(NMItems.bandage.itemID,1,2)).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(4).buy().item(BTWBlocks.blockDispenser.blockID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(4).buy().item(BTWBlocks.buddyBlock.blockID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(4).buy().item(BTWBlocks.detectorBlock.blockID).itemCount(1,3).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(4).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,12,24)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("blast"))).weight(1.2f).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(4).sell().item(BTWItems.soulFlux.itemID).itemCount(2,4).weight(1.2f).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(5).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,24,32)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("power"))).addToTradeList();
        TradeProvider.getBuilder().profession(1).level(5).convert().input(TradeItem.fromID(BTWItems.corpseEye.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,4,10)).output(TradeItem.fromID(Item.eyeOfEnder.itemID)).mandatory().addToTradeList();
    }



    private static void addPriestTrades(){
        EntityVillager.removeCustomTrade(2, TradeProvider.getBuilder().profession(2).level(5).arcaneScroll().scrollEnchant(Enchantment.fortune).secondaryEmeraldCost(24, 32).mandatory().build());
        EntityVillager.removeLevelUpTrade(2,4);

        TradeProvider.getBuilder().profession(2).level(2).buy().item(Item.netherStalkSeeds.itemID).itemCount(6,12).addToTradeList();
        TradeProvider.getBuilder().profession(2).level(3).buy().item(BTWItems.nitre.itemID).itemCount(16,32).addToTradeList();
        TradeProvider.getBuilder().profession(2).level(3).sell().item(Block.enchantmentTable.blockID).emeraldCost(6,10).weight(0.35f).addToTradeList();
        TradeProvider.getBuilder().profession(2).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,32,64)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("fortune"))).weight(0.1f).addToTradeList();
        TradeProvider.getBuilder().profession(2).level(3).convert().input(TradeItem.fromID(Item.potion.itemID)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,3)).output(TradeItem.fromIDAndMetadata(Item.potion.itemID,16453,2)).addToTradeList();
        TradeProvider.getBuilder().profession(2).level(4).convert().input(TradeItem.fromID(Item.appleGold.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,12,24)).output(TradeItem.fromIDAndMetadata(Item.appleGold.itemID,1)).mandatory().addToTradeList();

        TradeProvider.getBuilder().profession(2).level(4).convert().input(TradeItem.fromIDAndMetadata(BTWBlocks.aestheticVegetation.blockID, 2, 3)).secondInput(TradeItem.EMPTY).output(TradeItem.fromID(Block.enchantmentTable.blockID)).addAsLevelUpTrade();
//        TradeProvider.getBuilder().profession(2).level(4).buy().item().emeraldCost(4, 4).addAsLevelUpTrade();
        TradeProvider.getBuilder().profession(2).level(5).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,16,26)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("prot"))).addToTradeList();
        TradeProvider.getBuilder().profession(2).level(5).convert().input(TradeItem.fromID(NMItems.rifle.itemID)).secondInput(TradeItem.fromID(NMItems.rpg.itemID)).output(TradeItem.fromID(Block.dragonEgg.blockID)).mandatory().addToTradeList();

    }


    private static void addBlacksmithTrades(){
        EntityVillager.removeCustomTrade(3, TradeProvider.getBuilder().profession(3).level(5).arcaneScroll().scrollEnchant(Enchantment.unbreaking).secondaryEmeraldCost(16, 24).mandatory().build());

        TradeProvider.getBuilder().profession(3).level(1).buy().item(Item.pickaxeStone.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().profession(3).level(2).sell().item(NMItems.bandage.itemID).itemCount(2,2).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(2).buy().item(Item.redstone.itemID).itemCount(32,64).weight(0.8f).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(2).buy().item(Item.flintAndSteel.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).convert().input(TradeItem.fromIDAndMetadata(BTWBlocks.aestheticOpaque.blockID, 7,4,8)).secondInput(TradeItem.EMPTY).output(TradeItem.fromID(Item.emerald.itemID,1)).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).buy().item(BTWItems.diamondArmorPlate.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,24,32)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("looting"))).weight(0.9f).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).buy().item(BTWItems.padding.itemID).itemCount(6,10).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).sell().item(Item.appleGold.itemID).emeraldCost(8,16).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(3).convert().input(TradeItem.fromID(Item.potion.itemID)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,3)).output(TradeItem.fromIDAndMetadata(Item.potion.itemID,8201)).addToTradeList();
        TradeProvider.getBuilder().profession(3).level(4).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,12,18)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("unbreaking"))).weight(1.0f).addToTradeList();
    }


    private static void addButcherTrades(){
        EntityVillager.removeCustomTrade(4, TradeProvider.getBuilder().profession(4).level(5).arcaneScroll().scrollEnchant(Enchantment.sharpness).secondaryEmeraldCost(16, 24).mandatory().build());

        TradeProvider.getBuilder().profession(4).level(1).buy().item(Item.leash.itemID).itemCount(6,10).addToTradeList();
        TradeProvider.getBuilder().profession(4).level(2).buy().item(Item.swordIron.itemID).buySellSingle().weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().profession(4).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,6,12)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("thorns"))).addToTradeList();
        TradeProvider.getBuilder().profession(4).level(4).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,24,32)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("feather"))).weight(2.0f).addToTradeList();
    }


    @Unique
    private static int getScrollMetadata(String input){
        HashMap<String, Integer> dictionary = new HashMap<>();
        dictionary.put("prot",0);
        dictionary.put("fire prot",1);
        dictionary.put("feather",2);
        dictionary.put("blast",3);
        dictionary.put("proj prot",4);
        dictionary.put("resp",5);
        dictionary.put("aqua",6);
        dictionary.put("thorns",7);
        dictionary.put("sharp",16);
        dictionary.put("smite",17);
        dictionary.put("bane",18);
        dictionary.put("knockback",19);
        dictionary.put("fire aspect",20);
        dictionary.put("looting",21);
        dictionary.put("efficiency",32);
        dictionary.put("silk",33);
        dictionary.put("unbreaking",34);
        dictionary.put("fortune",35);
        dictionary.put("power",48);
        dictionary.put("punch",49);
        dictionary.put("flame",50);
        dictionary.put("infinity",51);

        return dictionary.get(input);
    }

    private static void addNightmareVillagerTrades(){
        // Level 1 Trades
        TradeProvider.getBuilder().profession(5).level(1).buy().item(Item.rottenFlesh.itemID).itemCount(8, 16).defaultTrade().weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(1).buy().item(Item.dyePowder.itemID, Color.BLACK.colorID).itemCount(12, 18).weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(1).buy().item(NMItems.magicFeather.itemID).itemCount(1, 2).weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(1).buy().item(NMItems.bloodMilk.itemID).buySellSingle().weight(0.2f).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(1).buy().item(Item.enderPearl.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().profession(5).level(1).buy().item(NMItems.fireRod.itemID).itemCount(1, 3).weight(0.5f).addToTradeList();

        // Level 2 Trades
        TradeProvider.getBuilder().profession(5).level(2).convert().input(TradeItem.fromIDAndMetadata(Item.potion.itemID, 8229, 1,2)).secondInput(TradeItem.EMPTY).output(TradeItem.fromID(Item.emerald.itemID)).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(2).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.darksunFragment.itemID,4,8)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("infinity"))).weight(0.1f).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(2).buy().item(NMItems.decayedFlesh.itemID).itemCount(4, 6).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(2).buy().item(NMItems.silverLump.itemID).itemCount(2, 3).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(2).sell().item(NMItems.dungApple.itemID).buySellSingle().weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(2).buy().item(NMItems.creeperTear.itemID).itemCount(1, 1).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(2).buy().item(NMItems.shadowRod.itemID).itemCount(1, 2).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(2).sell().item(BTWItems.soulFlux.itemID).itemCount(4, 8).addToTradeList();


        // Level 3 Trades
        TradeProvider.getBuilder().profession(5).level(3).sell().item(Item.blazeRod.itemID).emeraldCost(2, 4).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(3).sell().item(Item.nameTag.itemID).emeraldCost(2, 4).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(3).buy().item(NMItems.spiderFangs.itemID).itemCount(2, 4).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(3).buy().item(NMItems.sulfur.itemID).itemCount(3, 6).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(3).buy().item(NMItems.charredFlesh.itemID).itemCount(1, 2).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(3).sell().item(Item.magmaCream.itemID).itemCount(6, 8).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(3).buy().item(NMItems.greg.itemID).itemCount(1, 1).weight(0.5f).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.darksunFragment.itemID,4,8)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("sharp"))).weight(0.55f).addToTradeList();

        // Level 4 Trades
        TradeProvider.getBuilder().profession(5).level(4).sell().item(Item.ghastTear.itemID).itemCount(4, 6).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.waterRod.itemID).itemCount(1, 2).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.voidSack.itemID).itemCount(1,3).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.creeperChop.itemID).itemCount(1, 1).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.elementalRod.itemID).itemCount(1, 1).weight(0.6f).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.voidMembrane.itemID).itemCount(1, 1).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.darksunFragment.itemID).itemCount(1, 1).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(4).buy().item(Item.eyeOfEnder.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.creeperTear.itemID).itemCount(1, 1).weight(0.2f).addToTradeList();

        // Level 5 Trades
        TradeProvider.getBuilder().profession(5).level(5).sell().item(Item.enderPearl.itemID).itemCount(32, 64).mandatory().addToTradeList();
        TradeProvider.getBuilder().profession(5).level(5).sell().item(NMItems.rifle.itemID).buySellSingle().mandatory().addToTradeList();
        TradeProvider.getBuilder().profession(5).level(5).sell().item(NMItems.rpg.itemID).buySellSingle().mandatory().addToTradeList();
        TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.potion.itemID,16421,64)).mandatory().addToTradeList();

        TradeProvider.getBuilder().profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.waterStill.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.lavaStill.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.fire.blockID).itemCount(1, 64).build())
                .finishVariants().mandatory().addToTradeList();

        TradeProvider.getBuilder().profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.bedrock.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.portal.blockID).itemCount(6, 6).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.endPortal.blockID).itemCount(9, 9).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.endPortalFrame.blockID).itemCount(12, 12).build())
                .finishVariants().mandatory().addToTradeList();

        TradeProvider.getBuilder().profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.mobSpawner.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.dragonEgg.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(Block.workbench.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).sell().item(BTWBlocks.axlePowerSource.blockID).buySellSingle().build())
                .finishVariants().mandatory().addToTradeList();

        TradeProvider.getBuilder().profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 50, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 51, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 52, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 53, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 54, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 55, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 56, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 57, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 58, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 59, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 60, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 61, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 62, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 63, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 64, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 65, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 66, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 90, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 91, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 92, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 93, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 94, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 95, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 96, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 97, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 98, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 99, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 100, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 238, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 240, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 600, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 601, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 602, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 603, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 604, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2301, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2302, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2303, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2306, 3, 6)).build())
                .finishVariants().mandatory().addToTradeList();



        // Level up Trades
        TradeProvider.getBuilder().profession(5).level(1).buy().item(Block.dragonEgg.blockID).itemCount(1,1).addAsLevelUpTrade();
        TradeProvider.getBuilder().profession(5).level(2).buy().item(NMItems.voidMembrane.itemID).itemCount(3,3).addAsLevelUpTrade();
        TradeProvider.getBuilder().profession(5).level(3).buy().item(NMItems.darksunFragment.itemID).itemCount(16,16).addAsLevelUpTrade();
        TradeProvider.getBuilder().profession(5).level(4).buy().item(NMItems.starOfTheBloodGod.itemID).itemCount(1,1).addAsLevelUpTrade();


        TradeProvider.getBuilder().profession(5).level(2).sell().item(NMBlocks.bloodBones.blockID).buySellSingle().weight( 0.05f).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(3).sell().item(NMBlocks.bloodBones.blockID).buySellSingle().weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().profession(5).level(4).sell().item(NMBlocks.bloodBones.blockID).buySellSingle().weight(1.2f).addToTradeList();
    }


    private static void addCrucibleRecipes(){
        // remove vanilla helmet recipe because it returns 6 ingots instead of 5
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 6), new ItemStack[]{new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 5), new ItemStack[]{new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE)});

//        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.ironNugget, 6), new ItemStack[]{new ItemStack(BTWItems.metalFragment)});
//        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.steelNugget, 6), new ItemStack[]{new ItemStack(BTWItems.metalFragment)});

        // replace soul flux with ender slag in SFS ingot recipe, to force SFS mining
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.soulforgedSteelIngot, 1), new ItemStack[]{new ItemStack(Item.ingotIron, 1), new ItemStack(BTWItems.coalDust, 1), new ItemStack(BTWItems.soulUrn, 1), new ItemStack(BTWItems.soulFlux, 1)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.soulforgedSteelIngot, 1), new ItemStack[]{new ItemStack(Item.ingotIron, 1), new ItemStack(BTWItems.coalDust, 1), new ItemStack(BTWItems.soulUrn, 1), new ItemStack(BTWItems.enderSlag, 1)});
        // done replacing

        // remove all gold recipes from crucible
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.ingotGold, 2), new ItemStack[]{new ItemStack(Item.pickaxeGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 12), new ItemStack[]{new ItemStack(Item.axeGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 12), new ItemStack[]{new ItemStack(Item.swordGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 6), new ItemStack[]{new ItemStack(Item.hoeGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 6), new ItemStack[]{new ItemStack(Item.shovelGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 30), new ItemStack[]{new ItemStack(Item.helmetGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 48), new ItemStack[]{new ItemStack(Item.plateGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 42), new ItemStack[]{new ItemStack(Item.legsGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 24), new ItemStack[]{new ItemStack(Item.bootsGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(Item.pocketSundial)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.ingotGold, 9), new ItemStack[]{new ItemStack(Block.blockGold)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 5), new ItemStack[]{new ItemStack(BTWItems.ocularOfEnder)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 11), new ItemStack[]{new ItemStack(BTWItems.enderSpectacles, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 6), new ItemStack[]{new ItemStack(BTWItems.goldenDung)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 2), new ItemStack[]{new ItemStack(BTWItems.redstoneLatch)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 2), new ItemStack[]{new ItemStack(BTWBlocks.redstoneClutch)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 2), new ItemStack[]{new ItemStack(Block.music)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 60), new ItemStack[]{new ItemStack(BTWBlocks.dormandSoulforge)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 8), new ItemStack[]{new ItemStack(BTWBlocks.lightningRod)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.ingotGold, 4), new ItemStack[]{new ItemStack(Item.horseArmorGold)});
        // done removing all gold recipes from crucible


        // add my own gold recipes
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotGold, 1), new ItemStack[]{new ItemStack(Item.pickaxeGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 6), new ItemStack[]{new ItemStack(Item.axeGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 6), new ItemStack[]{new ItemStack(Item.swordGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(Item.hoeGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(Item.shovelGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 15), new ItemStack[]{new ItemStack(Item.helmetGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 24), new ItemStack[]{new ItemStack(Item.plateGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 21), new ItemStack[]{new ItemStack(Item.legsGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 12), new ItemStack[]{new ItemStack(Item.bootsGold, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 2), new ItemStack[]{new ItemStack(Item.pocketSundial)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotGold, 9), new ItemStack[]{new ItemStack(Block.blockGold)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(BTWItems.ocularOfEnder)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 5), new ItemStack[]{new ItemStack(BTWItems.enderSpectacles, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(BTWItems.goldenDung)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 1), new ItemStack[]{new ItemStack(BTWItems.redstoneLatch)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 1), new ItemStack[]{new ItemStack(BTWBlocks.redstoneClutch)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 1), new ItemStack[]{new ItemStack(Block.music)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 30), new ItemStack[]{new ItemStack(BTWBlocks.dormandSoulforge)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 4), new ItemStack[]{new ItemStack(BTWBlocks.lightningRod)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotGold, 2), new ItemStack[]{new ItemStack(Item.horseArmorGold)});
        // done adding




        // add blood armor and tool recipes
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 5), new ItemStack[]{new ItemStack(NMItems.bloodHelmet, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 8), new ItemStack[]{new ItemStack(NMItems.bloodChestplate, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 7), new ItemStack[]{new ItemStack(NMItems.bloodLeggings, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 4), new ItemStack[]{new ItemStack(NMItems.bloodBoots, 1, Short.MAX_VALUE)});

        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 3), new ItemStack[]{new ItemStack(NMItems.bloodPickaxe, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 4), new ItemStack[]{new ItemStack(NMItems.bloodSword, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 2), new ItemStack[]{new ItemStack(NMItems.bloodAxe, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 1), new ItemStack[]{new ItemStack(NMItems.bloodShovel, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 1), new ItemStack[]{new ItemStack(NMItems.bloodHoe, 1, Short.MAX_VALUE)});
        // done adding

        // add other crucible tools and blocks
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 2), new ItemStack[]{new ItemStack(NMItems.ironKnittingNeedles, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 4), new ItemStack[]{new ItemStack(NMItems.ironFishingPole, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotIron, 1), new ItemStack[]{new ItemStack(NMBlocks.ironLadder, 4, Short.MAX_VALUE)});
    }
    private static void addCauldronRecipes(){
        RecipeManager.addCauldronRecipe(new ItemStack(Item.potato, 1), new ItemStack[]{new ItemStack(BTWItems.straw, 8)});
        RecipeManager.addCauldronRecipe(new ItemStack(Item.clay, 8), new ItemStack[]{new ItemStack(BTWItems.netherSludge, 8)});
        RecipeManager.addCauldronRecipe(new ItemStack(NMItems.friedCalamari), new ItemStack[]{new ItemStack(NMItems.calamariRoast), new ItemStack(Item.bowlEmpty)});
        RecipeManager.addStokedCauldronRecipe(new ItemStack(BTWItems.netherSludge, 4), new ItemStack[]{new ItemStack(BTWItems.netherBrick, 8)});
    }

    private static void addOvenRecipes(){
        FurnaceRecipes.smelting().addSmelting(NMItems.calamari.itemID, new ItemStack(NMItems.calamariRoast), 0.0f);
    }
    private static void addSoulforgeRecipes(){
        RecipeManager.removeSoulforgeRecipe(new ItemStack(BTWItems.canvas), new Object[]{"MMMM", "MFFM", "MFFM", "MMMM", Character.valueOf('F'), BTWItems.fabric, Character.valueOf('M'), new ItemStack(BTWItems.woodMouldingStubID, 1, Short.MAX_VALUE)});
        RecipeManager.addSoulforgeRecipe(new ItemStack(Block.cobblestoneMossy, 4),new Object[]{"####", "#XX#", "#XX#", "####", Character.valueOf('#'), Block.vine, Character.valueOf('X'), BTWBlocks.looseCobblestone});
    }
    private static void addCampfireRecipes(){
        RecipeManager.addCampfireRecipe(NMItems.calamari.itemID, new ItemStack(NMItems.calamariRoast));
    }
    private static void addMillstoneRecipes(){
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.carrotSeeds), new ItemStack(BTWItems.hempSeeds));
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.wheatSeeds), new ItemStack(BTWItems.carrotSeeds));
        RecipeManager.addMillStoneRecipe(new ItemStack(NMItems.witchLocator,4), new ItemStack(BTWItems.witchWart));
        RecipeManager.addMillStoneRecipe(new ItemStack[]{new ItemStack(NMItems.rifle), new ItemStack(NMItems.rpg)}, new ItemStack[]{new ItemStack(Block.dragonEgg)});
    }

    private static void addCraftingRecipes(){
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.planter, 1), new Object[]{"# #", "# #", "###", Character.valueOf('#'), Item.brick});
        RecipeManager.addRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XXX", "###", Character.valueOf('#'), new ItemStack(BTWItems.woodSidingStubID, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.book,1,Short.MAX_VALUE)});
        // add gapple and carrot recipes
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.appleGold, 1, 0), new Object[]{"###", "#X#", "###", '#', Item.ingotGold, 'X', Item.appleRed});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.appleGold, 1, 1), new Object[]{"###", "#X#", "###", '#', Block.blockGold, 'X', Item.appleRed});
        RecipeManager.addRecipe(new ItemStack(Item.appleGold,1,1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(Item.ingotGold, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(Item.appleGold), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(Item.goldNugget, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(NMItems.dungApple), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(BTWItems.dung, 1), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.goldenCarrot, 1, 0), new Object[]{"###", "#X#", "###", Character.valueOf('#'), Item.goldNugget, Character.valueOf('X'), BTWItems.carrot});
        RecipeManager.addRecipe(new ItemStack(Item.goldenCarrot, 1, 0), new Object[]{" # ", "#X#", " # ", Character.valueOf('#'), Item.goldNugget, Character.valueOf('X'), BTWItems.carrot});
        // done with gapples and carrots

        // fishing recipes
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.fishingRod), new Object[]{new ItemStack(Item.stick), new ItemStack(Item.silk), new ItemStack(Item.silk), new ItemStack(BTWItems.ironNugget)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.fishingRod), new Object[]{new ItemStack(Item.stick), new ItemStack(BTWItems.sinew), new ItemStack(BTWItems.sinew), new ItemStack(BTWItems.ironNugget)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.fishingRod), new Object[]{new ItemStack(Item.stick), new ItemStack(BTWItems.sinew), new ItemStack(BTWItems.sinew), new ItemStack(BTWItems.boneFishHook)});
        RecipeManager.addRecipe(new ItemStack(NMItems.ironFishingPole,1), new Object[]{"  #", " #X", "Y #", Character.valueOf('#'), BTWItems.ironNugget, Character.valueOf('X'), BTWItems.rope, Character.valueOf('Y'), Item.ingotIron});
        // fishing recipes added

        // add misc recipes
        RecipeManager.addRecipe(new ItemStack(NMItems.eclipseBow,1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.darksunFragment, 1), Character.valueOf('X'), new ItemStack(BTWItems.compositeBow)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wickerPane, 8), new Object[]{new ItemStack(BTWBlocks.hamper)});
        RecipeManager.addRecipe(new ItemStack(BTWItems.canvas,1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(BTWItems.woodMouldingStubID, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(BTWItems.fabric)});
        RecipeManager.addRecipe(new ItemStack(NMItems.ironKnittingNeedles,1), new Object[]{"# #", "# #", "#X#", Character.valueOf('#'), BTWItems.ironNugget, Character.valueOf('X'), Item.silk});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wickerPane,1), new Object[]{new ItemStack(NMItems.ironKnittingNeedles,1,Short.MAX_VALUE),Item.reed,Item.reed,Item.reed,Item.reed});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.silk,1), new Object[]{new ItemStack(NMItems.ironKnittingNeedles,1,Short.MAX_VALUE),BTWItems.tangledWeb});
        for (int i = 0; i < 16; i++) {
            RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.woolKnit,1, i), new Object[]{new ItemStack(NMItems.ironKnittingNeedles,1,Short.MAX_VALUE),new ItemStack(BTWItems.wool, 1, i),new ItemStack(BTWItems.wool, 1, i)});
        }
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.bandage,2), new Object[]{BTWItems.wickerPane, new ItemStack(BTWItems.wool, 1, Short.MAX_VALUE), new ItemStack(BTWItems.wool, 1, Short.MAX_VALUE), Item.silk});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.bandage,2), new Object[]{new ItemStack(BTWItems.woolKnit, 1, Short.MAX_VALUE), new ItemStack(BTWItems.wool, 1, Short.MAX_VALUE), new ItemStack(BTWItems.wool, 1, Short.MAX_VALUE), Item.silk});

        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.steelBunch,1), new Object[]{new ItemStack(BTWItems.steelNugget),new ItemStack(BTWItems.steelNugget),new ItemStack(BTWItems.steelNugget,4),new ItemStack(BTWItems.steelNugget)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.steelNugget, 4), new Object[]{new ItemStack(NMItems.steelBunch)});

        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.magicArrow, 4), new Object[]{new ItemStack(NMItems.magicFeather), new ItemStack(Item.stick), new ItemStack(BTWItems.soulFlux), new ItemStack(BTWItems.broadheadArrowHead)});
        // done adding misc recipes

        // fish sandwich
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2), new Object[]{new ItemStack(Item.bread), new ItemStack(Item.fishCooked)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 1), new Object[]{new ItemStack(Item.bread), new ItemStack(Item.fishCooked)});
        // fish sandwich end

        // remove sinew recipes, add custom ones
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, 600), new Object[]{new ItemStack(Item.beefCooked), new ItemStack(Item.beefCooked), new ItemStack(BTWItems.sharpStone)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, 600), new Object[]{new ItemStack(BTWItems.cookedWolfChop), new ItemStack(BTWItems.cookedWolfChop), new ItemStack(BTWItems.sharpStone)});

        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, 600), new Object[]{new ItemStack(Item.beefCooked), new ItemStack(BTWItems.sharpStone)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, 600), new Object[]{new ItemStack(Item.porkCooked), new ItemStack(BTWItems.sharpStone)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, 600), new Object[]{new ItemStack(BTWItems.cookedMutton), new ItemStack(BTWItems.sharpStone)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, 600), new Object[]{new ItemStack(BTWItems.cookedWolfChop), new ItemStack(BTWItems.sharpStone)});
        // done with sinew

        // add blood recipes
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodIngot), new Object[]{" # ", "#X#", " # ", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(BTWItems.diamondIngot)});

        RecipeManager.addRecipe(new ItemStack(NMItems.bloodHelmet), new Object[]{"###", "# #", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodChestplate), new Object[]{"# #", "###", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodLeggings), new Object[]{"###", "# #", "# #", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodBoots), new Object[]{"# #", "# #",  Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});

        RecipeManager.addRecipe(new ItemStack(NMItems.bloodSword), new Object[]{" # ", "###", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.stick)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodPickaxe), new Object[]{"###", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.stick)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodAxe), new Object[]{"#  ", "#X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.stick)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodShovel), new Object[]{" # ", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.stick)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodHoe), new Object[]{"#X ", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.stick)});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.bloodChest), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(BTWBlocks.chest)});

        RecipeManager.addRecipe(new ItemStack(BTWItems.rawMysteryMeat), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(Item.beefRaw)});
        RecipeManager.addRecipe(new ItemStack(BTWItems.rawMysteryMeat), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(BTWItems.rawCheval)});
        RecipeManager.addRecipe(new ItemStack(BTWItems.rawMysteryMeat), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(Item.porkRaw)});
        // done adding blood recipes

        // add blaze rod recipes
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.elementalRod, 1), new Object[]{NMItems.shadowRod, Item.blazeRod, NMItems.fireRod, NMItems.waterRod});
        // done adding blaze rod recipes

        RecipeManager.addRecipe(new ItemStack(Item.book), new Object[]{"###", "XXX", Character.valueOf('#'), BTWItems.cutScouredLeather, Character.valueOf('X'), new ItemStack(Item.paper, 1, Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(Item.book), new Object[]{"###", "XXX", Character.valueOf('#'), BTWItems.cutTannedLeather, Character.valueOf('X'), new ItemStack(Item.paper, 1, Short.MAX_VALUE)});

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.gunpowder), new Object[]{new ItemStack(BTWItems.nitre), new ItemStack(BTWItems.brimstone), new ItemStack(BTWItems.coalDust)});

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.woolHelmet), new Object[]{"##", Character.valueOf('#'), new ItemStack(BTWItems.woolKnit, 1, Short.MAX_VALUE)});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.woolChest), new Object[]{"##", "##", Character.valueOf('#'), new ItemStack(BTWItems.woolKnit, 1, Short.MAX_VALUE)});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.woolLeggings), new Object[]{"##", "# ", Character.valueOf('#'), new ItemStack(BTWItems.woolKnit, 1, Short.MAX_VALUE)});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.woolLeggings), new Object[]{"# ", "##", Character.valueOf('#'), new ItemStack(BTWItems.woolKnit, 1, Short.MAX_VALUE)});

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.helmetDiamond), new Object[]{"XXX", "XYX", 'X', BTWItems.diamondIngot, 'Y', BTWItems.diamondArmorPlate});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.plateDiamond), new Object[]{"Y Y", "XXX", "XXX", 'X', BTWItems.diamondIngot, 'Y', BTWItems.diamondArmorPlate});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.legsDiamond), new Object[]{"XXX", "Y Y", "Y Y", 'X', BTWItems.diamondIngot, 'Y', BTWItems.diamondArmorPlate});

        RecipeManager.addRecipe(new ItemStack(Item.helmetDiamond), new Object[]{"###", "# #", "   ", Character.valueOf('#'), new ItemStack(BTWItems.diamondIngot)});
        RecipeManager.addRecipe(new ItemStack(Item.plateDiamond), new Object[]{"# #", "###", "###", Character.valueOf('#'), new ItemStack(BTWItems.diamondIngot)});
        RecipeManager.addRecipe(new ItemStack(Item.legsDiamond), new Object[]{"###", "# #", "# #", Character.valueOf('#'), new ItemStack(BTWItems.diamondIngot)});

        // road
        RecipeManager.addRecipe(new ItemStack(NMBlocks.blockRoad, 2), new Object[]{"XY", "YX", 'X', Block.gravel, 'Y', BTWBlocks.looseCobblestone});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.blockAsphalt, 8), new Object[]{"XXX", "XYX", "XXX", 'X', NMBlocks.blockRoad, 'Y', BTWItems.soulUrn});
        // ladders
        RecipeManager.addRecipe(new ItemStack(NMBlocks.stoneLadder, 3), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWBlocks.looseCobblestone, Character.valueOf('S'), Item.silk});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.stoneLadder, 3), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWBlocks.looseCobblestone, Character.valueOf('S'), BTWItems.hempFibers});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.stoneLadder, 3), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWBlocks.looseCobblestone, Character.valueOf('S'), BTWItems.sinew});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.ironLadder, 4), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWItems.ironNugget, Character.valueOf('S'), BTWItems.hempFibers});
    }
}
