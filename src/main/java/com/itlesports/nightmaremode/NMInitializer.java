package com.itlesports.nightmaremode;

import btw.achievement.AchievementTab;
import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.crafting.manager.CauldronCraftingManager;
import btw.crafting.manager.CrucibleStokedCraftingManager;
import btw.crafting.manager.MillStoneCraftingManager;
import btw.crafting.manager.PistonPackingCraftingManager;
import btw.crafting.recipe.RecipeManager;
import btw.entity.mob.villager.trade.TradeItem;
import btw.entity.mob.villager.trade.TradeProvider;
import btw.item.BTWItems;
import btw.item.tag.BTWTags;
import btw.item.tag.TagInstance;
import btw.item.tag.TagOrStack;
import btw.util.color.Color;
import com.itlesports.nightmaremode.achievements.AchievementExt;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.*;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.NMPostItems;
import com.itlesports.nightmaremode.mixin.AchievementAccessor;
import com.itlesports.nightmaremode.mixin.BiomeGenBaseAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Unique;


import java.util.HashMap;
import java.util.Set;
import java.util.function.Predicate;

import static btw.achievement.BTWAchievements.*;
import static com.itlesports.nightmaremode.achievements.NMAchievements.*;

public abstract class NMInitializer implements AchievementExt {

    public static void initNightmareRecipes(){
        addCraftingRecipes();
        addCampfireRecipes();
        addCrucibleRecipes();
        addCauldronRecipes();
        addMillstoneRecipes();
        addOvenRecipes();
        addSoulforgeRecipes();
        addPistonPackingRecipes();
    }
    public static void runItemPostInit(){
        NMPostItems.runPostInit();
    }
    public static void initMobSpawning(){
        addMobToMushroomIslands(EntityGhast.class, 1, 1, 1);
        addMobToMushroomIslands(EntityFauxVillager.class, 2, 1, 1);

        if (NightmareMode.magicMonsters) {
            clearAllLandBiomes();
            addMobToAllLandBiomes(EntityWitch.class, 3, 1, 2);
            clearAllWaterBiomes();
        }
    }

    private static void addMobToMushroomIslands(Class mob, int i, int j, int k){
        addMobToBiome(mob, i, j, k, BiomeGenBase.mushroomIslandShore);
        addMobToBiome(mob, i, j, k, BiomeGenBase.mushroomIsland);
    }
    private static void clearAllLandBiomes(){
        clearBiome(BiomeGenBase.plains);
        clearBiome(BiomeGenBase.desert);
        clearBiome(BiomeGenBase.extremeHills);
        clearBiome(BiomeGenBase.forest);
        clearBiome(BiomeGenBase.taiga);
        clearBiome(BiomeGenBase.swampland);
        clearBiome(BiomeGenBase.icePlains);
        clearBiome(BiomeGenBase.iceMountains);
        clearBiome(BiomeGenBase.beach);
        clearBiome(BiomeGenBase.desertHills);
        clearBiome(BiomeGenBase.forestHills);
        clearBiome(BiomeGenBase.taigaHills);
        clearBiome(BiomeGenBase.extremeHillsEdge);
        clearBiome(BiomeGenBase.jungle);
        clearBiome(BiomeGenBase.jungleHills);
    }
    private static void clearAllWaterBiomes(){
        clearWaterBiome(BiomeGenBase.ocean);
        clearWaterBiome(BiomeGenBase.river);
        clearWaterBiome(BiomeGenBase.frozenOcean);
        clearWaterBiome(BiomeGenBase.frozenRiver);
    }

    private static void clearBiome(BiomeGenBase b){
        ((BiomeGenBaseAccessor)(b)).nightmareMode$getSpawnableMonsterList().clear();
    }
    private static void clearWaterBiome(BiomeGenBase b){
        ((BiomeGenBaseAccessor)(b)).nightmareMode$getSpawnableWaterCreatureList().clear();
    }
    private static void addMobToAllBiomes(Class mob, int i, int j, int k){
        addMobToAllLandBiomes(mob,i,j,k);
        addMobToAllWaterBiomes(mob,i,j,k);
    }
    private static void addMobToAllLandBiomes(Class mob, int i, int j, int k){

        addMobToBiome(mob, i, j, k, BiomeGenBase.plains);
        addMobToBiome(mob, i, j, k, BiomeGenBase.desert);
        addMobToBiome(mob, i, j, k, BiomeGenBase.extremeHills);
        addMobToBiome(mob, i, j, k, BiomeGenBase.forest);
        addMobToBiome(mob, i, j, k, BiomeGenBase.taiga);
        addMobToBiome(mob, i, j, k, BiomeGenBase.swampland);
        addMobToBiome(mob, i, j, k, BiomeGenBase.icePlains);
        addMobToBiome(mob, i, j, k, BiomeGenBase.iceMountains);
        addMobToBiome(mob, i, j, k, BiomeGenBase.beach);
        addMobToBiome(mob, i, j, k, BiomeGenBase.desertHills);
        addMobToBiome(mob, i, j, k, BiomeGenBase.forestHills);
        addMobToBiome(mob, i, j, k, BiomeGenBase.taigaHills);
        addMobToBiome(mob, i, j, k, BiomeGenBase.extremeHillsEdge);
        addMobToBiome(mob, i, j, k, BiomeGenBase.jungle);
        addMobToBiome(mob, i, j, k, BiomeGenBase.jungleHills);
    }
    private static void addMobToAllWaterBiomes(Class mob, int i, int j, int k){
        addMobToBiome(mob, i, j, k, BiomeGenBase.ocean);
        addMobToBiome(mob, i, j, k, BiomeGenBase.river);
        addMobToBiome(mob, i, j, k, BiomeGenBase.frozenOcean);
        addMobToBiome(mob, i, j, k, BiomeGenBase.frozenRiver);
    }


    private static void addMobToBiome(Class mob, int i, int j, int k, BiomeGenBase b){
        ((BiomeGenBaseAccessor)b).nightmareMode$getSpawnableMonsterList().add(new SpawnListEntry(mob, i, j, k));
    }
    private static void addWaterMobToBiome(Class mob, int i, int j, int k, BiomeGenBase b){
        ((BiomeGenBaseAccessor)b).nightmareMode$getSpawnableWaterCreatureList().add(new SpawnListEntry(mob, i, j, k));
    }

    public static void manipulateAchievements(){
        addParent(FIND_SHAFT, NMAchievements.MORNING_SECOND_DAY);
        addParent(FIND_REEDS, NMAchievements.MORNING_SECOND_DAY);
        addParent(FIND_BONES, NMAchievements.MORNING_SECOND_DAY);

        addParent(COOK_FOOD, NMAchievements.KILL_ANIMAL);


        setHidden(CRAFT_HAMPER,true);

        kill(EQUIP_WOOL_ARMOR);

        move(FIND_REEDS,-1,0);
        move(CRAFT_BASKET,-2,0);
        move(CRAFT_KNITTING_NEEDLES,-1,0);
        move(CRAFT_WOOL_KNIT,-1,0);
        move(MINE_DIAMOND_ORE, 0, 1);


        // wicker stuff for snap 6
        move(FIND_REEDS, 0, -20);
        move(CRAFT_WICKER, -1, -20);
        move(CRAFT_BASKET, -1, -12);

        removeParent(CRAFT_BASKET,GRIND_HEMP_FIBERS);
        move(CRAFT_BEDROLL, 0, -1);

        // the hemp purge

        switchTab(GRIND_HEMP_FIBERS, TAB_IRON_AGE);
        switchTab(HARVEST_HEMP, TAB_IRON_AGE);
        switchTab(CRAFT_MILLSTONE, TAB_IRON_AGE);
        switchTab(CRAFT_HAND_CRANK, TAB_IRON_AGE);
        switchTab(FIND_HEMP_SEEDS, TAB_IRON_AGE);
        switchTab(CRAFT_GEAR, TAB_IRON_AGE);
        removeParent(CRAFT_GEAR, FIND_LOGS);
        removeParent(FIND_HEMP_SEEDS, CRAFT_STONE_HOE);
        kill(CRAFT_STONE_HOE);

        // move everything that was moved down back up
        move(CRAFT_STONE_SHOVEL, -2, 0);
        move(CRAFT_WET_BRICKS, -2, 0);
        move(DRY_BRICKS, -2, 0);
        move(CRAFT_OVEN, -2, 0);
        move(SMELT_IRON, -2, 0);
        move(CRAFT_IRON_CHISEL, -2, 0);
        move(MAKE_WORK_STUMP, -2, 0);
        move(FIND_STONE_BRICK, -2, 0);
        move(CRAFT_STONE_PICKAXE, -2, 0);
        move(CRAFT_IRON_INGOT, -2, 0);
        move(MINE_DIAMOND_ORE, -2, 0);
        move(MINE_REDSTONE_ORE, -2, 0);
        move(CRAFT_COMPASS, -2, 0);
        move(CRAFT_BOW_DRILL, 4, 0);
        // done moving up



        move(CRAFT_KNITTING_NEEDLES, 3, -2);
        move(CRAFT_WOOL_KNIT, 3, -2);

        move(CRAFT_KNITTING_NEEDLES, -1, 2);
        move(CRAFT_WOOL_KNIT, -1, 2);
        move(CRAFT_BEDROLL, 2, 1);
        kill(CRAFT_BARK_BOX);

                            // 2ND TAB - TAB_IRON_AGE
        move(CRAFT_CAULDRON, 0, -2);
        setCondition(FIND_OBSIDIAN, itemStack -> ((ItemStack)itemStack).itemID == NMBlocks.crudeObsidian.blockID);

        // hemp arc

        move(FIND_HEMP_SEEDS, -1, -14);
        addParent(FIND_HEMP_SEEDS, CRAFT_IRON_HOE);
        move(HARVEST_HEMP, 1, -15);
        move(CRAFT_GEAR, -2, -12);
        addParent(CRAFT_GEAR, CRAFT_PLANKS);
        move(CRAFT_MILLSTONE, -1, -13);
        move(CRAFT_HAND_CRANK, -3, -13);
        move(GRIND_HEMP_FIBERS, 1, -14);

        removeParent(CRAFT_SAIL, CRAFT_PLANKS);
        addParent(CRAFT_SAIL, CRAFT_FABRIC);
        destroyParents(CRAFT_BASKET);
        kill(CRAFT_BASKET);
        // hemp arc over

        move(USE_EMERALD_PILE, -3, -2);

        move(CRAFT_HAND_CRANK, 2,0);
        move(LOCATE_FORTRESS_WITH_PILE, 0, -2);
            // shift everything 1 block to the right
        move(FIND_BLAZE_ROD, 0, 2);
        move(CRAFT_HIBACHI, 0, 1);
        move(STOKE_FIRE, 0, 1);
        move(SPIN_POTTERY, 0, 1);
        move(CRAFT_CRUCIBLE, 0, 1);
        move(CRAFT_WATER_WHEEL, 0, 1);
        move(CRAFT_DYNAMITE, 0, 1);
        move(REDNECK_FISHING, 0, 1);
        move(MAKE_HELLFIRE_DUST, 0, 2);
        move(CRAFT_NETHER_COAL, -1, 2);
        addParent(MAKE_HELLFIRE_DUST, GET_GROUND_NETHERRACK);

        kill(EXPLODED_WITH_BLASTING_OIL);
        removeParent(EQUIP_DIAMOND_ARMOR, CRAFT_DIAMOND_INGOT);
        addParent(EQUIP_DIAMOND_ARMOR, CRAFT_REFINED_DIAMOND);




        // 3RD TAB - TAB_AUTOMATION
        kill(TOSS_THE_MILK);
        move(CONVERT_SOULFORGE, 0, -2);
        addParent(CONVERT_EYES_OF_ENDER, NMAchievements.CRAFT_CORPSE_EYE);
        move(FIND_DORMANT_SOULFORGE, 0, -2);

        // 4TH TAB - TAB_END_GAME
        removeParent(EQUIP_STEEL_ARMOR, CRAFT_STEEL);
        removeParent(CRAFT_STEEL_COMBO_TOOL, CRAFT_STEEL);
        removeParent(CRAFT_INFERNAL_ENCHANTER, CRAFT_STEEL);
        removeParent(USE_INFERNAL_ENCHANTER, CRAFT_INFERNAL_ENCHANTER);
        removeParent(MAX_INFERNAL_ENCHANT, USE_INFERNAL_ENCHANTER);
        removeParent(MAX_STEEL_BEACON, CRAFT_STEEL);
        removeParent(STEEL_BEACON_RESPAWN_ACROSS_DIMENSIONS, MAX_STEEL_BEACON);
        removeParent(CRAFT_STEEL, KILLED_DRAGON);
        kill(CRAFT_STEEL_COMBO_TOOL);
        kill(CRAFT_STEEL);

        // moving steel armor quest to automation
        switchTab(EQUIP_STEEL_ARMOR, TAB_AUTOMATION);
        move(EQUIP_STEEL_ARMOR, -2 ,12);
        addParent(EQUIP_STEEL_ARMOR, CRAFT_STEEL_INGOT);
        // done, back to endgame
        removeParent(GET_BOOTSIES, MAX_STEEL_BEACON);
        kill(GET_BOOTSIES);


        kill(CRAFT_INFERNAL_ENCHANTER);

        kill(USE_INFERNAL_ENCHANTER);
        kill(MAX_INFERNAL_ENCHANT);
        kill(STEEL_BEACON_RESPAWN_ACROSS_DIMENSIONS);

        move(MAX_STEEL_BEACON, 4, 6);

        setIcon(MAX_STEEL_BEACON, BTWBlocks.soulforgedSteelBlock);
    }

    private static void switchTab(Achievement acObj, AchievementTab tab){
        acObj.tab.achievementList.remove(acObj);
        acObj.tab = tab;
        tab.achievementList.add(acObj);
    }
    private static void setCondition(Achievement acObj, Predicate predicate){
        ((AchievementExt) acObj).nightmareMode$setPredicate(predicate);
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

        boolean isServer = MinecraftServer.getIsServer();
        if (!isServer) {
            BTWItems.emeraldPile.setItemRightClickCooldown( BTWItems.emeraldPile.getItemRightClickCooldown() / 6);
            BTWItems.soulSandPile.setItemRightClickCooldown( BTWItems.soulSandPile.getItemRightClickCooldown() / 6);
            NMItems.witchLocator.setItemRightClickCooldown( NMItems.witchLocator.getItemRightClickCooldown() / 6);
            NMItems.templeLocator.setItemRightClickCooldown( NMItems.templeLocator.getItemRightClickCooldown() / 6);
        }

    }


    private static void addFarmerTrades(){
        EntityVillager.removeLevelUpTrade(0,2);
        EntityVillager.removeCustomTrade(0,TradeProvider.getBuilder().name("btw:sell_looting_scroll").profession(0).level(5).arcaneScroll().scrollEnchant(Enchantment.looting).secondaryEmeraldCost(48, 64).mandatory().build());

        TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(1).sell().item(Block.grass.blockID).itemCount(2,4).weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(1).convert().input(TradeItem.fromIDAndMetadata(Block.tallGrass.blockID,1,4,8)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,2)).output(TradeItem.fromID(BTWItems.hempSeeds.itemID,2,6)).weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(2).buy().item(BTWBlocks.millstone.blockID).emeraldCost(2, 2).addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(2).buy().item(Item.shears.itemID).buySellSingle().weight(0.4f).addToTradeList();
        TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(3).buy().item(BTWItems.redMushroom.itemID).itemCount(2, 5).weight(1.2f).addToTradeList();
        TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(3).buy().item(Item.bucketWater.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(4).buy().item(BTWItems.chowder.itemID).itemCount(1, 2).addToTradeList();
        TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(5).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,8,16)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("efficiency"))).mandatory().addToTradeList();
    }

    private static void addLibrarianTrades(){
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().name("nmLibrarian0").profession(1).level(1).buy().item(Item.paper.itemID).itemCount(24, 32).build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().name("nmLibrarian0").profession(1).level(2).variants().addTradeVariant(TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(2).convert().input(TradeItem.fromID(BTWItems.redstoneEye.itemID, 2)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.detectorBlock.blockID)).build()).addTradeVariant(TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(2).convert().input(TradeItem.fromID(BTWItems.redstoneEye.itemID, 4)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.buddyBlock.blockID)).build()).addTradeVariant(TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(2).convert().input(TradeItem.fromID(Block.cobblestoneMossy.blockID, 6)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.blockDispenser.blockID)).build()).finishVariants().mandatory().build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().name("nmLibrarian0").profession(1).level(5).convert().input(TradeItem.fromID(Item.enderPearl.itemID)).conversionCost(6, 8).output(TradeItem.fromID(Item.eyeOfEnder.itemID)).mandatory().build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().name("btw:sell_power_scroll").profession(1).level(5).arcaneScroll().scrollEnchant(Enchantment.power).secondaryEmeraldCost(48, 64).mandatory().build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().name("btw:buy_bat_wings").profession(1).level(3).buy().item(BTWItems.batWing.itemID).itemCount(8, 12).build());


        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(1).buy().item(NMItems.ironKnittingNeedles.itemID).emeraldCost(2,3).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(2).buy().item(Block.bookShelf.blockID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(2).buy().item(Item.book.itemID).itemCount(3,6).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(2).buy().item(Item.redstoneRepeater.itemID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(3).buy().item(BTWItems.hellfireDust.itemID).itemCount(16,24).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(3).buy().item(Item.glassBottle.itemID).itemCount(16,24).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(3).convert().input(TradeItem.fromIDAndMetadata(BTWItems.wool.itemID,15,2,4)).conversionCost(1, 2).output(TradeItem.fromID(NMItems.bandage.itemID,1,2)).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(4).buy().item(BTWBlocks.blockDispenser.blockID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(4).buy().item(BTWBlocks.buddyBlock.blockID).itemCount(1,2).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(4).buy().item(BTWBlocks.detectorBlock.blockID).itemCount(1,3).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(4).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,12,24)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("blast"))).weight(1.2f).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(4).sell().item(BTWItems.soulFlux.itemID).itemCount(2,4).weight(1.2f).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(5).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,24,32)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("power"))).addToTradeList();
        TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(5).convert().input(TradeItem.fromID(BTWItems.corpseEye.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,4,10)).output(TradeItem.fromID(Item.eyeOfEnder.itemID)).mandatory().addToTradeList();
    }



    private static void addPriestTrades(){
        EntityVillager.removeCustomTrade(2, TradeProvider.getBuilder().name("btw:sell_fortune_scroll").profession(2).level(5).arcaneScroll().scrollEnchant(Enchantment.fortune).secondaryEmeraldCost(48, 64).mandatory().build());
        EntityVillager.removeLevelUpTrade(2,4);


        TradeProvider.getBuilder().name("nmPriest0").profession(2).level(2).buy().item(Item.netherStalkSeeds.itemID).itemCount(4,8).addToTradeList();
        TradeProvider.getBuilder().name("nmPriest0").profession(2).level(3).buy().item(BTWItems.nitre.itemID).itemCount(8,16).addToTradeList();
        TradeProvider.getBuilder().name("nmPriest0").profession(2).level(3).sell().item(Block.enchantmentTable.blockID).emeraldCost(6,10).weight(0.35f).addToTradeList();
        TradeProvider.getBuilder().name("nmPriest0").profession(2).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,32,64)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("fortune"))).weight(0.1f).addToTradeList();
        TradeProvider.getBuilder().name("nmPriest0").profession(2).level(3).convert().input(TradeItem.fromID(Item.potion.itemID)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,3)).output(TradeItem.fromIDAndMetadata(Item.potion.itemID,16453,2)).addToTradeList();
        TradeProvider.getBuilder().name("nmPriest0").profession(2).level(4).convert().input(TradeItem.fromID(Item.appleGold.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,10,18)).output(TradeItem.fromIDAndMetadata(Item.appleGold.itemID,1)).mandatory().addToTradeList();

        TradeProvider.getBuilder().name("nmPriest0").profession(2).level(4).convert().input(TradeItem.fromIDAndMetadata(BTWBlocks.aestheticVegetation.blockID, 2, 3)).secondInput(TradeItem.EMPTY).output(TradeItem.fromID(Block.enchantmentTable.blockID)).addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmPriest0").profession(2).level(5).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,16,24)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("prot"))).addToTradeList();
        TradeProvider.getBuilder().name("nmPriest0").profession(2).level(5).convert().input(TradeItem.fromID(NMItems.rifle.itemID)).secondInput(TradeItem.fromID(NMItems.rpg.itemID)).output(TradeItem.fromID(Block.dragonEgg.blockID)).mandatory().addToTradeList();

    }


    private static void addBlacksmithTrades(){
        EntityVillager.removeCustomTrade(3, TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(5).arcaneScroll().scrollEnchant(Enchantment.unbreaking).secondaryEmeraldCost(16, 24).mandatory().build());

        TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(1).buy().item(Item.pickaxeStone.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(2).sell().item(NMItems.bandage.itemID).itemCount(2,2).addToTradeList();
        TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(2).buy().item(Item.redstone.itemID).itemCount(32,64).weight(0.8f).addToTradeList();
        TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(2).buy().item(Item.flintAndSteel.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(3).convert().input(TradeItem.fromIDAndMetadata(BTWBlocks.aestheticOpaque.blockID, 7,4,8)).secondInput(TradeItem.EMPTY).output(TradeItem.fromID(Item.emerald.itemID,1)).addToTradeList();
        TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(3).buy().item(BTWItems.diamondArmorPlate.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,24,32)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("looting"))).weight(0.9f).addToTradeList();
        TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(3).sell().item(Item.appleGold.itemID).emeraldCost(8,16).addToTradeList();
        TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(3).convert().input(TradeItem.fromID(Item.potion.itemID)).secondInput(TradeItem.fromID(Item.emerald.itemID,1,3)).output(TradeItem.fromIDAndMetadata(Item.potion.itemID,8201)).addToTradeList();
        TradeProvider.getBuilder().name("nmBlacksmith0").profession(3).level(4).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,12,18)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("unbreaking"))).weight(1.0f).addToTradeList();
    }


    private static void addButcherTrades(){
        EntityVillager.removeCustomTrade(4, TradeProvider.getBuilder().name("btw:sell_sharpness_scroll").profession(4).level(5).arcaneScroll().scrollEnchant(Enchantment.sharpness).secondaryEmeraldCost(48, 64).mandatory().build());

        TradeProvider.getBuilder().name("nmButcher0").profession(4).level(1).buy().item(Item.leash.itemID).itemCount(6,10).addToTradeList();
        TradeProvider.getBuilder().name("nmButcher0").profession(4).level(2).buy().item(Item.swordIron.itemID).buySellSingle().weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().name("nmButcher0").profession(4).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,6,12)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("thorns"))).addToTradeList();
        TradeProvider.getBuilder().name("nmButcher0").profession(4).level(4).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID,24,32)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("feather"))).weight(2.0f).addToTradeList();
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
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(1).buy().item(Item.rottenFlesh.itemID).itemCount(8, 16).defaultTrade().weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(1).buy().item(Item.dyePowder.itemID, Color.BLACK.colorID).itemCount(12, 18).weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(1).buy().item(NMItems.magicFeather.itemID).itemCount(1, 2).weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(1).buy().item(NMItems.bloodMilk.itemID).buySellSingle().weight(0.2f).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(1).buy().item(Item.enderPearl.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(1).buy().item(NMItems.fireRod.itemID).itemCount(1, 3).weight(0.5f).addToTradeList();

        // Level 2 Trades
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(2).convert().input(TradeItem.fromIDAndMetadata(Item.potion.itemID, 8229, 1,2)).secondInput(TradeItem.EMPTY).output(TradeItem.fromID(Item.emerald.itemID)).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(2).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.darksunFragment.itemID,4,8)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("infinity"))).weight(0.1f).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(2).buy().item(NMItems.decayedFlesh.itemID).itemCount(4, 6).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(2).buy().item(NMItems.silverLump.itemID).itemCount(2, 3).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(2).sell().item(NMItems.dungApple.itemID).buySellSingle().weight(0.3f).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(2).buy().item(NMItems.creeperTear.itemID).itemCount(1, 1).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(2).buy().item(NMItems.shadowRod.itemID).itemCount(1, 2).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(2).sell().item(BTWItems.soulFlux.itemID).itemCount(4, 8).addToTradeList();


        // Level 3 Trades
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(3).sell().item(Item.blazeRod.itemID).emeraldCost(2, 4).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(3).sell().item(Item.nameTag.itemID).emeraldCost(2, 4).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(3).buy().item(NMItems.spiderFangs.itemID).itemCount(2, 4).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(3).buy().item(NMItems.sulfur.itemID).itemCount(3, 6).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(3).buy().item(NMItems.charredFlesh.itemID).itemCount(1, 2).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(3).sell().item(Item.magmaCream.itemID).itemCount(6, 8).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(3).buy().item(NMItems.speedCoil.itemID).itemCount(1, 1).weight(0.5f).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(3).convert().input(TradeItem.fromID(Item.paper.itemID)).secondInput(TradeItem.fromID(NMItems.darksunFragment.itemID,4,8)).output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID,getScrollMetadata("sharp"))).weight(0.55f).addToTradeList();

        // Level 4 Trades
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).sell().item(Item.ghastTear.itemID).itemCount(4, 6).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).buy().item(NMItems.waterRod.itemID).itemCount(1, 2).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).buy().item(NMItems.voidSack.itemID).itemCount(1,3).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).buy().item(NMItems.creeperChop.itemID).itemCount(1, 1).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).buy().item(NMItems.elementalRod.itemID).itemCount(1, 1).weight(0.6f).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).buy().item(NMItems.voidMembrane.itemID).itemCount(1, 1).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).buy().item(NMItems.darksunFragment.itemID).itemCount(1, 1).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).buy().item(Item.eyeOfEnder.itemID).buySellSingle().addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).buy().item(NMItems.creeperTear.itemID).itemCount(1, 1).weight(0.2f).addToTradeList();

        // Level 5 Trades
        TradeProvider.getBuilder()
                .name("nmMerchant0")
                .profession(5)
                .level(5)
                .variants()
                .addTradeVariant(
                        TradeProvider.getBuilder()
                                .name("nmMerchant0")
                                .profession(5)
                                .level(5)
                                .sell()
                                .item(Item.enderPearl.itemID)
                                .itemCount(32, 64)
                                .build()
                )
                .addTradeVariant(
                        TradeProvider.getBuilder()
                                .name("nmMerchant0")
                                .profession(5)
                                .level(5)
                                .sell()
                                .item(NMItems.rifle.itemID)
                                .buySellSingle()
                                .build()
                )
                .addTradeVariant(
                        TradeProvider.getBuilder()
                                .name("nmMerchant0")
                                .profession(5)
                                .level(5)
                                .sell()
                                .item(NMItems.rpg.itemID)
                                .buySellSingle()
                                .build()
                )
                .addTradeVariant(
                        TradeProvider.getBuilder()
                                .name("nmMerchant0")
                                .profession(5)
                                .level(5)
                                .convert()
                                .input(TradeItem.fromID(Item.emerald.itemID))
                                .secondInput(TradeItem.EMPTY)
                                .output(TradeItem.fromIDAndMetadata(Item.potion.itemID, 16421, 64))
                                .build()
                )
                .finishVariants()
                .mandatory()
                .addToTradeList();

        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(NMPostItems.timeBottle.itemID).buySellSingle().mandatory().addToTradeList();

        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(Block.waterStill.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(Block.lavaStill.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(Block.fire.blockID).itemCount(1, 64).build())
                .finishVariants().mandatory().addToTradeList();

        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(Block.bedrock.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(Block.portal.blockID).itemCount(6, 6).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(Block.endPortal.blockID).itemCount(9, 9).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(Block.endPortalFrame.blockID).itemCount(12, 12).build())
                .finishVariants().mandatory().addToTradeList();

        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(Block.mobSpawner.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(Block.dragonEgg.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(Block.workbench.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).sell().item(BTWBlocks.axlePowerSource.blockID).buySellSingle().build())
                .finishVariants().mandatory().addToTradeList();

        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 50, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 51, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 52, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 53, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 54, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 55, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 56, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 57, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 58, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 59, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 60, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 61, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 62, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 63, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 64, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 65, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 66, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 90, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 91, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 92, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 93, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 94, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 95, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 96, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 97, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 98, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 99, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 100, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 238, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 240, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 600, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 601, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 602, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 603, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 604, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2301, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2302, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2303, 3, 6)).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(5).convert().input(TradeItem.fromID(Item.emerald.itemID)).secondInput(TradeItem.EMPTY).output(TradeItem.fromIDAndMetadata(Item.monsterPlacer.itemID, 2306, 3, 6)).build())
                .finishVariants().mandatory().addToTradeList();



        // Level up Trades
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(1).buy().item(Block.dragonEgg.blockID).itemCount(1,1).addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(2).buy().item(NMItems.voidMembrane.itemID).itemCount(3,3).addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(3).buy().item(NMItems.darksunFragment.itemID).itemCount(16,16).addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).buy().item(NMItems.starOfTheBloodGod.itemID).itemCount(1,1).addAsLevelUpTrade();


        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(3).sell().item(NMBlocks.bloodBones.blockID).buySellSingle().weight(0.02f).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant0").profession(5).level(4).sell().item(NMBlocks.bloodBones.blockID).buySellSingle().weight(1.2f).addToTradeList();
    }


    private static void addCrucibleRecipes(){
        // refined diamond
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot), new ItemStack[]{new ItemStack(BTWItems.diamondIngot), new ItemStack(Item.netherQuartz, 4)});

        // replace soul flux with ender slag in SFS ingot recipe, to force SFS mining
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.soulforgedSteelIngot, 1), new ItemStack[]{new ItemStack(Item.ingotIron, 1), new ItemStack(BTWItems.coalDust, 1), new ItemStack(BTWItems.soulUrn, 1), new ItemStack(BTWItems.soulFlux, 1)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.soulforgedSteelIngot, 1), new ItemStack[]{new ItemStack(Item.ingotIron, 1), new ItemStack(BTWItems.coalDust, 1), new ItemStack(BTWItems.soulUrn, 1), new ItemStack(BTWItems.enderSlag, 1)});
        // done replacing

        // remove all gold recipes from crucible
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.ingotGold, 2), (TagOrStack[])new ItemStack[]{new ItemStack(Item.pickaxeGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 12), (TagOrStack[])new ItemStack[]{new ItemStack(Item.axeGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 12), (TagOrStack[])new ItemStack[]{new ItemStack(Item.swordGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 6), (TagOrStack[])new ItemStack[]{new ItemStack(Item.hoeGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 6), (TagOrStack[])new ItemStack[]{new ItemStack(Item.shovelGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 30), (TagOrStack[])new ItemStack[]{new ItemStack(Item.helmetGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 48), (TagOrStack[])new ItemStack[]{new ItemStack(Item.plateGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 42), (TagOrStack[])new ItemStack[]{new ItemStack(Item.legsGold, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Item.goldNugget, 24), (TagOrStack[])new ItemStack[]{new ItemStack(Item.bootsGold, 1, Short.MAX_VALUE)});
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

        // add gold recipes for golden apples and carrots
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(Item.appleGold)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotGold, 3), new ItemStack[]{new ItemStack(Item.appleGold, 1, 1)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 2), new ItemStack[]{new ItemStack(Item.goldenCarrot)});
        // done with apples

        // chest
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.ironNugget, 6), (TagOrStack[])new ItemStack[]{new ItemStack(BTWBlocks.chest)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget), (TagOrStack[])new ItemStack[]{new ItemStack(BTWBlocks.chest)});
        // done with chest




        // add blood armor and tool recipes
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 5), new ItemStack[]{new ItemStack(NMItems.bloodHelmet, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 8), new ItemStack[]{new ItemStack(NMItems.bloodChestplate, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 7), new ItemStack[]{new ItemStack(NMItems.bloodLeggings, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 4), new ItemStack[]{new ItemStack(NMItems.bloodBoots, 1, Short.MAX_VALUE)});

        RecipeManager.addStokedCrucibleRecipe(new ItemStack[]{new ItemStack(NMItems.refinedDiamondIngot, 3), new ItemStack(Item.blazeRod, 2)}, new ItemStack[]{new ItemStack(NMItems.bloodPickaxe, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack[]{new ItemStack(NMItems.refinedDiamondIngot, 4), new ItemStack(Item.blazeRod, 1)}, new ItemStack[]{new ItemStack(NMItems.bloodSword, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack[]{new ItemStack(NMItems.refinedDiamondIngot, 2), new ItemStack(Item.blazeRod, 2)}, new ItemStack[]{new ItemStack(NMItems.bloodAxe, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack[]{new ItemStack(NMItems.refinedDiamondIngot, 1), new ItemStack(Item.blazeRod, 2)}, new ItemStack[]{new ItemStack(NMItems.bloodShovel, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack[]{new ItemStack(NMItems.refinedDiamondIngot, 1), new ItemStack(Item.blazeRod, 3)}, new ItemStack[]{new ItemStack(NMItems.bloodHoe, 1, Short.MAX_VALUE)});
        // done adding

        // add other crucible tools and blocks
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 2), new ItemStack[]{new ItemStack(NMItems.ironKnittingNeedles, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 4), new ItemStack[]{new ItemStack(NMItems.ironFishingPole, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 7), new ItemStack[]{new ItemStack(NMBlocks.ironLadder, 4, Short.MAX_VALUE)});

        // obsidian post-wither
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Block.obsidian), new ItemStack[]{new ItemStack(BTWItems.steelNugget), new ItemStack(Item.clay),new ItemStack(NMBlocks.crudeObsidian)});


        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 6), new ItemStack[]{new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 8), new ItemStack[]{new ItemStack(Item.plateDiamond, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 7), new ItemStack[]{new ItemStack(Item.legsDiamond, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 4), new ItemStack[]{new ItemStack(Item.bootsDiamond, 1, Short.MAX_VALUE)});

        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 5), (TagOrStack[])new ItemStack[]{new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 8), (TagOrStack[])new ItemStack[]{new ItemStack(Item.plateDiamond, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 7), (TagOrStack[])new ItemStack[]{new ItemStack(Item.legsDiamond, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 4), (TagOrStack[])new ItemStack[]{new ItemStack(Item.bootsDiamond, 1, Short.MAX_VALUE)});

    }
    private static void addCauldronRecipes(){
        RecipeManager.addCauldronRecipe(new ItemStack(Item.potato, 1), new ItemStack[]{new ItemStack(BTWItems.straw, 8)});
        RecipeManager.addCauldronRecipe(new ItemStack(Item.clay, 8), new ItemStack[]{new ItemStack(BTWItems.netherSludge, 8)});
        RecipeManager.addCauldronRecipe(new ItemStack(NMItems.friedCalamari), new ItemStack[]{new ItemStack(NMItems.calamariRoast), new ItemStack(Item.bowlEmpty)});
        RecipeManager.addCauldronRecipe(new ItemStack(Item.blazeRod), new ItemStack[]{new ItemStack(Item.blazePowder, 2), new ItemStack(Item.stick)});

        // blood sapling and groth nerf: instead of costing 8 urns, they only cost 1
        CauldronCraftingManager.getInstance().removeRecipe((new ItemStack(BTWBlocks.aestheticVegetation, 1, 2)), (TagOrStack[])new ItemStack[]{new ItemStack(BTWBlocks.oakSapling), new ItemStack(BTWBlocks.spruceSapling), new ItemStack(BTWBlocks.birchSapling), new ItemStack(BTWBlocks.jungleSapling), new ItemStack(BTWItems.soulUrn, 8), new ItemStack(Item.netherStalkSeeds)});
        RecipeManager.addCauldronRecipe(new ItemStack(BTWBlocks.aestheticVegetation, 1, 2), (TagOrStack[])new ItemStack[]{new ItemStack(BTWBlocks.oakSapling), new ItemStack(BTWBlocks.spruceSapling), new ItemStack(BTWBlocks.birchSapling), new ItemStack(BTWBlocks.jungleSapling), new ItemStack(BTWItems.soulUrn), new ItemStack(Item.netherStalkSeeds)});

        CauldronCraftingManager.getInstance().removeRecipe(new ItemStack[]{new ItemStack(BTWBlocks.looseDirt), new ItemStack(BTWItems.netherGrothSpores)}, (TagOrStack[])new ItemStack[]{new ItemStack(Block.mycelium), new ItemStack(BTWItems.brownMushroom), new ItemStack(BTWItems.redMushroom), new ItemStack(BTWItems.soulUrn, 8), new ItemStack(BTWItems.dung), new ItemStack(Item.netherStalkSeeds)});
        RecipeManager.addCauldronRecipe(new ItemStack(BTWItems.netherGrothSpores), (TagOrStack[])new ItemStack[]{new ItemStack(Block.mycelium), new ItemStack(BTWItems.brownMushroom), new ItemStack(BTWItems.redMushroom), new ItemStack(BTWItems.soulUrn), new ItemStack(BTWItems.dung), new ItemStack(Item.netherStalkSeeds)});



        RecipeManager.addStokedCauldronRecipe(new ItemStack(BTWItems.netherSludge, 4), new ItemStack[]{new ItemStack(BTWItems.netherBrick, 8)});

        RecipeManager.addStokedCauldronRecipe(new ItemStack(NMItems.templeLocator, 2), new ItemStack[]{new ItemStack(BTWItems.sandPile, 64), new ItemStack(NMItems.obsidianShard, 4), new ItemStack(Item.ingotGold)});

        CauldronCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.heartyStew, 5), new TagOrStack[]{new ItemStack(BTWItems.boiledPotato), new ItemStack(BTWItems.cookedCarrot), new ItemStack(BTWItems.brownMushroom, 3), new ItemStack(BTWItems.flour), TagInstance.of(BTWTags.heartyMeats), new ItemStack(Item.bowlEmpty, 5)});
        RecipeManager.addCauldronRecipe(new ItemStack(BTWItems.heartyStew, 5), new TagOrStack[]{new ItemStack(BTWItems.boiledPotato), new ItemStack(BTWItems.cookedCarrot), new ItemStack(BTWItems.brownMushroom, 3), new ItemStack(BTWItems.flour), TagInstance.of(BTWTags.cookedMeats), new ItemStack(Item.bowlEmpty, 5)});

    }

    private static void addOvenRecipes(){
        FurnaceRecipes.smelting().addSmelting(NMItems.calamari.itemID, new ItemStack(NMItems.calamariRoast), 0.0f);
    }
    private static void addSoulforgeRecipes(){
        RecipeManager.removeSoulforgeRecipe(new ItemStack(BTWItems.canvas), new Object[]{"MMMM", "MFFM", "MFFM", "MMMM", Character.valueOf('F'), BTWItems.fabric, Character.valueOf('M'), new ItemStack(BTWItems.woodMouldingStubID, 1, Short.MAX_VALUE)});
        RecipeManager.addSoulforgeRecipe(new ItemStack(Block.cobblestoneMossy, 4),new Object[]{"####", "#XX#", "#XX#", "####", Character.valueOf('#'), Block.vine, Character.valueOf('X'), BTWBlocks.looseCobblestone});
        // packed blocks
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWBlocks.aestheticEarth.blockID, 4, 6),new Object[]{"####", "####", "####", "####", Character.valueOf('#'), BTWBlocks.looseDirt});
        RecipeManager.addSoulforgeRecipe(new ItemStack(Block.sandStone, 4),new Object[]{"####", "####", "####", "####", Character.valueOf('#'), Block.sand});


    }
    private static void addCampfireRecipes(){
        RecipeManager.addCampfireRecipe(NMItems.calamari.itemID, new ItemStack(NMItems.calamariRoast));
    }
    private static void addMillstoneRecipes(){
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.carrotSeeds), new ItemStack(BTWItems.hempSeeds));
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.wheatSeeds), new ItemStack(BTWItems.carrotSeeds));
        RecipeManager.addMillStoneRecipe(new ItemStack(NMItems.witchLocator,4), new ItemStack(BTWItems.witchWart));

        // improve netherrack grinding rates
        MillStoneCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.groundNetherrack), new ItemStack(Block.netherrack));
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.groundNetherrack, 8), new ItemStack(Block.netherrack));

    }

    private static void addCraftingRecipes(){
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.planter, 1), new Object[]{"# #", "# #", "###", Character.valueOf('#'), Item.brick});
        RecipeManager.addRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XXX", "###", Character.valueOf('#'), new ItemStack(BTWItems.woodSidingStubID, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.book,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.steelLocker), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.steelBunch), Character.valueOf('X'), new ItemStack(NMBlocks.bloodChest)});
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
        RecipeManager.addRecipe(new ItemStack(Item.fishingRod,1), new Object[]{
                "  #",
                " S#",
                "S Y", Character.valueOf('#'), Item.silk, Character.valueOf('Y'), BTWItems.boneFishHook,Character.valueOf('S'), Item.stick});

        RecipeManager.addRecipe(new ItemStack(Item.fishingRod,1), new Object[]{
                "  #",
                " S#",
                "S Y", Character.valueOf('#'), Item.silk, Character.valueOf('Y'), BTWItems.ironNugget,Character.valueOf('S'), Item.stick});

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.fishingRod), new Object[]{new ItemStack(Item.stick), BTWTags.strings, BTWTags.strings, BTWTags.fishingHooks});

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
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodIngot), new Object[]{" # ", "#X#", " # ", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(NMItems.refinedDiamondIngot)});

        RecipeManager.addRecipe(new ItemStack(NMItems.bloodHelmet), new Object[]{"###", "# #", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodChestplate), new Object[]{"# #", "###", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodLeggings), new Object[]{"###", "# #", "# #", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodBoots), new Object[]{"# #", "# #",  Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});

        RecipeManager.addRecipe(new ItemStack(NMItems.bloodSword), new Object[]{" # ", "###", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodPickaxe), new Object[]{"###", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodAxe), new Object[]{"#  ", "#X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodShovel), new Object[]{" # ", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.addRecipe(new ItemStack(NMItems.bloodHoe), new Object[]{"#X ", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
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
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.bootsDiamond), new Object[]{"X X", "X X", Character.valueOf('X'), BTWItems.diamondIngot});

        RecipeManager.addRecipe(new ItemStack(Item.helmetDiamond), new Object[]{"###", "# #", "   ", Character.valueOf('#'), new ItemStack(NMItems.refinedDiamondIngot)});
        RecipeManager.addRecipe(new ItemStack(Item.plateDiamond), new Object[]{"# #", "###", "###", Character.valueOf('#'), new ItemStack(NMItems.refinedDiamondIngot)});
        RecipeManager.addRecipe(new ItemStack(Item.legsDiamond), new Object[]{"###", "# #", "# #", Character.valueOf('#'), new ItemStack(NMItems.refinedDiamondIngot)});
        RecipeManager.addRecipe(new ItemStack(Item.bootsDiamond), new Object[]{"X X", "X X", Character.valueOf('X'), NMItems.refinedDiamondIngot});

        // road
        RecipeManager.addRecipe(new ItemStack(NMBlocks.blockRoad, 2), new Object[]{"XY", "YX", 'X', Block.gravel, 'Y', BTWBlocks.looseCobblestone});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.blockAsphalt, 8), new Object[]{"XXX", "XYX", "XXX", 'X', NMBlocks.blockRoad, 'Y', BTWItems.soulUrn});
        // ladders
        RecipeManager.addRecipe(new ItemStack(NMBlocks.stoneLadder, 3), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWBlocks.looseCobblestone, Character.valueOf('S'), Item.silk});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.stoneLadder, 3), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWBlocks.looseCobblestone, Character.valueOf('S'), BTWItems.hempFibers});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.stoneLadder, 3), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWBlocks.looseCobblestone, Character.valueOf('S'), BTWItems.sinew});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.ironLadder, 4), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWItems.ironNugget, Character.valueOf('S'), BTWItems.hempFibers});

        // bark boxes
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWBlocks.oakBarkBox), new Object[]{new ItemStack(BTWItems.bark, 1, 0), new ItemStack(BTWItems.bark, 1, 0), new ItemStack(BTWItems.bark, 1, 0), new ItemStack(Item.silk, 1, 0)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWBlocks.spruceBarkBox), new Object[]{new ItemStack(BTWItems.bark, 1, 1), new ItemStack(BTWItems.bark, 1, 1), new ItemStack(BTWItems.bark, 1, 1), new ItemStack(Item.silk, 1, 0)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWBlocks.birchBarkBox), new Object[]{new ItemStack(BTWItems.bark, 1, 2), new ItemStack(BTWItems.bark, 1, 2), new ItemStack(BTWItems.bark, 1, 2), new ItemStack(Item.silk, 1, 0)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWBlocks.jungleBarkBox), new Object[]{new ItemStack(BTWItems.bark, 1, 3), new ItemStack(BTWItems.bark, 1, 3), new ItemStack(BTWItems.bark, 1, 3), new ItemStack(Item.silk, 1, 0)});



        // new wicker basket
        RecipeManager.addRecipe(new ItemStack(NMBlocks.customWickerBasket), new Object[]{"##", "##", Character.valueOf('#'), BTWItems.wickerPane});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wickerPane, 4), new Object[]{NMBlocks.customWickerBasket});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.wickerBasket), new Object[]{"SS", "##", "##", Character.valueOf('#'), BTWItems.wickerPane, Character.valueOf('S'), BTWItems.hempFibers});
        // hide all baskets from EMI
        BTWBlocks.wickerBasket.hideFromEMI();
        BTWBlocks.oakBarkBox.hideFromEMI();
        BTWBlocks.spruceBarkBox.hideFromEMI();
        BTWBlocks.birchBarkBox.hideFromEMI();
        BTWBlocks.jungleBarkBox.hideFromEMI();
        // done hiding

        // hamper stuff
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.hamper), new Object[]{"S#S", "#P#", "###", Character.valueOf('#'), BTWItems.wickerPane, Character.valueOf('P'), BTWTags.planks, Character.valueOf('S'), BTWItems.rope});
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.hamper), new Object[]{"###", "#P#", "###", Character.valueOf('#'), BTWItems.wickerPane, Character.valueOf('P'), Block.planks});

        // add compressed block recipes
        RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.creeperOysterBlock, 1), new Object[]{new ItemStack(BTWItems.creeperOysters), new ItemStack(BTWItems.creeperOysters), new ItemStack(BTWItems.creeperOysters), new ItemStack(BTWItems.creeperOysters), new ItemStack(BTWItems.creeperOysters), new ItemStack(BTWItems.creeperOysters), new ItemStack(BTWItems.creeperOysters), new ItemStack(BTWItems.creeperOysters), new ItemStack(BTWItems.creeperOysters)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.rottenFleshBlock, 1), new Object[]{new ItemStack(Item.rottenFlesh), new ItemStack(Item.rottenFlesh),new ItemStack(Item.rottenFlesh),new ItemStack(Item.rottenFlesh),new ItemStack(Item.rottenFlesh),new ItemStack(Item.rottenFlesh),new ItemStack(Item.rottenFlesh),new ItemStack(Item.rottenFlesh),new ItemStack(Item.rottenFlesh),});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.spiderEyeBlock, 1), new Object[]{new ItemStack(Item.spiderEye), new ItemStack(Item.spiderEye),new ItemStack(Item.spiderEye),new ItemStack(Item.spiderEye),new ItemStack(Item.spiderEye),new ItemStack(Item.spiderEye),new ItemStack(Item.spiderEye),new ItemStack(Item.spiderEye),new ItemStack(Item.spiderEye),});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 1, 15), new Object[]{new ItemStack(Item.bone), new ItemStack(Item.bone),new ItemStack(Item.bone),new ItemStack(Item.bone),new ItemStack(Item.bone),new ItemStack(Item.bone),new ItemStack(Item.bone),new ItemStack(Item.bone),new ItemStack(Item.bone),});

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.creeperOysters, 16), new Object[]{new ItemStack(BTWBlocks.creeperOysterBlock)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.creeperOysters, 8), new Object[]{new ItemStack(BTWBlocks.creeperOysterSlab)});

        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.creeperOysters, 9), new Object[]{new ItemStack(BTWBlocks.creeperOysterBlock)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.creeperOysters, 4), new Object[]{new ItemStack(BTWBlocks.creeperOysterSlab)});

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.spiderEye, 16), new Object[]{new ItemStack(BTWBlocks.spiderEyeBlock)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.spiderEye, 8), new Object[]{new ItemStack(BTWBlocks.spiderEyeSlab)});

        RecipeManager.addShapelessRecipe(new ItemStack(Item.spiderEye, 9), new Object[]{new ItemStack(BTWBlocks.spiderEyeBlock)});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.spiderEye, 4), new Object[]{new ItemStack(BTWBlocks.spiderEyeSlab)});


        // gear stuff
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.gear, 4), new Object[]{" X ", "X#X", " X ", Character.valueOf('#'), Block.wood, Character.valueOf('X'), Item.stick});
        RecipeManager.addRecipe(new ItemStack(BTWItems.gear, 2), new Object[]{" X ", "X#X", " X ", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.stick});
        // stone hoe
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeStone), new Object[]{" #X", " #S", " # ", Character.valueOf('#'), Item.stick, Character.valueOf('X'), new ItemStack(BTWItems.stone, 1, Short.MAX_VALUE), Character.valueOf('S'), Item.silk});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeStone), new Object[]{"X# ", "S# ", " # ", Character.valueOf('#'), Item.stick, Character.valueOf('X'), new ItemStack(BTWItems.stone, 1, Short.MAX_VALUE), Character.valueOf('S'), Item.silk});
        // sail recipe
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.windMillBlade), new Object[]{"###", "###", "XXX", Character.valueOf('#'), BTWItems.fabric, Character.valueOf('X'), BTWTags.woodMouldings});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.windMillBlade), new Object[]{"###", "###", "XXX", Character.valueOf('#'), BTWItems.fabric, Character.valueOf('X'), BTWTags.planks});

        RecipeManager.addRecipe(new ItemStack(BTWItems.windMillBlade), new Object[]{"###", "XXX", Character.valueOf('#'), BTWItems.fabric, Character.valueOf('X'), BTWTags.woodMouldings});
        RecipeManager.addRecipe(new ItemStack(BTWItems.windMillBlade), new Object[]{"###", "XXX", Character.valueOf('#'), BTWItems.fabric, Character.valueOf('X'), BTWTags.planks});

        // hellforge
        RecipeManager.addRecipe(new ItemStack(NMBlocks.hellforge), new Object[]{
                "NNN",
                "NBN",
                "NHN",
                Character.valueOf('H'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 3),
                Character.valueOf('B'), Item.blazePowder,
                Character.valueOf('N'), BTWBlocks.looseNetherBrick
        });

        // change powder keg recipe for sealed nether
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.tnt, 1), new Object[]{"GFG", "GBG", "GGG", Character.valueOf('B'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 11), Character.valueOf('G'), Item.gunpowder, Character.valueOf('F'), BTWItems.fuse});
        RecipeManager.addRecipe(new ItemStack(Block.tnt, 1), new Object[]{
                "GFG",
                "GBG",
                "NGN",
                Character.valueOf('B'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 11),
                Character.valueOf('G'), Item.gunpowder,
                Character.valueOf('N'), BTWItems.nitre,
                Character.valueOf('F'), BTWItems.fuse});

        // change barrel recipe for powder keg recipe for sealed nether
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 2, 11), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(BTWItems.woodMouldingStubID, 1, Short.MAX_VALUE), Character.valueOf('X'), BTWItems.glue});
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.aestheticOpaque, 1, 11), new Object[]{"###", "# #", "###", Character.valueOf('#'), new ItemStack(BTWItems.woodMouldingStubID, 1, Short.MAX_VALUE)});

        // add ground netherrack -> netherrack 1:1 conversion
        RecipeManager.addShapelessRecipe(new ItemStack(Block.netherrack, 1), new Object[]{new ItemStack(BTWItems.groundNetherrack), new ItemStack(BTWItems.groundNetherrack), new ItemStack(BTWItems.groundNetherrack), new ItemStack(BTWItems.groundNetherrack), new ItemStack(BTWItems.groundNetherrack), new ItemStack(BTWItems.groundNetherrack), new ItemStack(BTWItems.groundNetherrack), new ItemStack(BTWItems.groundNetherrack)});

        // add obsidian recipes
        RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.crudeObsidian, 1), new Object[]{new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard)});


        // make ender chest not stupid
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.enderChest), new Object[]{
                "OOO",
                "OEO",
                "OOO",
                Character.valueOf('O'), Block.obsidian,
                Character.valueOf('E'), Item.eyeOfEnder
        });
        RecipeManager.addRecipe(new ItemStack(Block.enderChest), new Object[]{
                "OEO",
                "OBO",
                "OOO",
                Character.valueOf('O'), NMBlocks.crudeObsidian,
                Character.valueOf('B'), NMBlocks.bloodChest,
                Character.valueOf('E'), Item.enderPearl
        });

        // asphalt layer
        RecipeManager.addRecipe(new ItemStack(NMBlocks.asphaltLayer, 6), new Object[]{
                "AAA",
                Character.valueOf('A'), NMBlocks.blockAsphalt,
        });


        // alternate recipes for all diamond items that involve refined ingots
        RecipeManager.addRecipe(new ItemStack(Item.swordDiamond), new Object[]{"X", "X", "#", Character.valueOf('#'), Item.stick, Character.valueOf('X'), NMItems.refinedDiamondIngot}).hideFromEMI();
        RecipeManager.addRecipe(new ItemStack(Item.pickaxeDiamond), new Object[]{"XXX", " # ", " # ", Character.valueOf('#'), Item.stick, Character.valueOf('X'), NMItems.refinedDiamondIngot}).hideFromEMI();
        RecipeManager.addRecipe(new ItemStack(Item.shovelDiamond), new Object[]{"X", "#", "#", Character.valueOf('#'), Item.stick, Character.valueOf('X'), NMItems.refinedDiamondIngot}).hideFromEMI();
        RecipeManager.addRecipe(new ItemStack(Item.axeDiamond), new Object[]{"X ", "X#", " #", Character.valueOf('#'), Item.stick, Character.valueOf('X'), NMItems.refinedDiamondIngot}).hideFromEMI();
        RecipeManager.addRecipe(new ItemStack(BTWItems.diamondChisel), new Object[]{"X", Character.valueOf('X'), NMItems.refinedDiamondIngot}).hideFromEMI();
        RecipeManager.addRecipe(new ItemStack(BTWItems.diamondShears), new Object[]{"X ", " X", Character.valueOf('X'), NMItems.refinedDiamondIngot}).hideFromEMI();
        // done refining

        // chest
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodSidings, Character.valueOf('I'), Item.ingotIron});
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodSidings, Character.valueOf('I'), BTWItems.ironNugget});
        // done chest

        // blood saw
        RecipeManager.addRecipe(new ItemStack(NMBlocks.bloodSaw), new Object[]{
                "DDD",
                "WLW",
                "BHB",
                Character.valueOf('B'), NMItems.bloodIngot,
                Character.valueOf('H'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 3), // HFD BLOCK
                Character.valueOf('D'), NMItems.refinedDiamondIngot,
                Character.valueOf('W'), new ItemStack(BTWItems.woodSidingStubID, 1, 4),
                Character.valueOf('L'), BTWItems.redstoneLatch
        });
        // blood saw, blood conquered




        // crimson bottle
        RecipeManager.addRecipe(new ItemStack(NMPostItems.bloodMoonBottle), new Object[]{
                "HSH",
                "BEB",
                "HBH",
                Character.valueOf('B'), NMItems.bloodOrb,
                Character.valueOf('S'), BTWItems.soulFlux,
                Character.valueOf('E'), Item.expBottle,
                Character.valueOf('H'), BTWItems.hellfireDust
        });
    }

    private static void addPistonPackingRecipes() {
        // oysters
        PistonPackingCraftingManager.instance.removeRecipe((Block)BTWBlocks.creeperOysterBlock, 0 , (TagOrStack[])new ItemStack[]{new ItemStack(BTWItems.creeperOysters, 16)});
        RecipeManager.addPistonPackingRecipe((Block)BTWBlocks.creeperOysterBlock, new ItemStack(BTWItems.creeperOysters, 9));

        // spider eyes
        PistonPackingCraftingManager.instance.removeRecipe((Block)BTWBlocks.spiderEyeBlock, 0 , (TagOrStack[])new ItemStack[]{new ItemStack(Item.spiderEye, 16)});
        RecipeManager.addPistonPackingRecipe((Block)BTWBlocks.spiderEyeBlock, new ItemStack(Item.spiderEye, 9));
    }










    // ACHIEVEMENT HELPERS

    private static void addParent(Achievement acObj, Achievement achievementToAdd){
        ((AchievementExt) acObj).nightmareMode$appendParent(achievementToAdd);
    }
    private static void setHidden(Achievement acObj, boolean hidden){
        acObj.isHidden = hidden;
    }
//    private static void kill(Achievement acObj){
//        acObj.tab.achievementList.remove(acObj);
//    }

    private static void kill(Achievement acObj) {
        StatList.allStats.remove(acObj);
        StatList.oneShotStats.remove(acObj.statId);

        if (acObj.tab != null) {
            acObj.tab.achievementList.remove(acObj);
        }


        Set<Achievement<?>> set = AchievementList.achievementsByEventType.get(acObj.eventType);
        if (set != null) {
            set.remove(acObj);
            if (set.isEmpty()) {
                AchievementList.achievementsByEventType.remove(acObj.eventType);
            }
        }
        acObj.statGuid = null;
    }

    private static void setDisplay(Achievement acObj, int row, int column){
        ((AchievementExt) acObj).nightmareMode$setDisplay(row, column);
    }
    private static void move(Achievement acObj, int down, int right){
        setDisplay(acObj,acObj.displayRow + down, acObj.displayColumn + right);
    }
    private static void removeParent(Achievement myAchievement, Achievement parentToRemove) {
        AchievementExt ext = (AchievementExt) (Object) myAchievement;
        Achievement[] current = ((AchievementAccessor)myAchievement).getParents();
        Achievement[] updated = ext.nightmareMode$removeParent(current, parentToRemove);
        ((AchievementAccessor)myAchievement).setParentAchievements(updated);
    }
    private static void destroyParents(Achievement myAchievement){
        ((AchievementAccessor)myAchievement).setParentAchievements(new Achievement[0]);
    }

    private static void setIcon(Achievement acObj, Block block){
        AchievementExt ext = (AchievementExt) (Object) acObj;
        ext.nightmareMode$setIcon(new ItemStack(block));
    }
    private static void setIcon(Achievement acObj, Item item){
        AchievementExt ext = (AchievementExt) (Object) acObj;
        ext.nightmareMode$setIcon(new ItemStack(item));
    }
    private static void setIcon(Achievement acObj, ItemStack stack){
        AchievementExt ext = (AchievementExt) (Object) acObj;
        ext.nightmareMode$setIcon(stack);
    }

}
