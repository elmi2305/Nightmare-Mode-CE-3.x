package com.itlesports.nightmaremode.util;

import api.achievement.AchievementTab;
import api.entity.mob.villager.TradeItem;
import api.entity.mob.villager.TradeProvider;
import btw.crafting.manager.SoulforgeCraftingManager;
import btw.crafting.manager.KilnCraftingManager;
import btw.crafting.recipe.RecipeManager;
import btw.crafting.manager.CauldronCraftingManager;
import btw.crafting.manager.CrucibleStokedCraftingManager;
import btw.crafting.manager.MillStoneCraftingManager;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.BTWTags;
import com.itlesports.nightmaremode.achievements.AchievementExt;
import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.crafting.manager.BrewingStandRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.CisternRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.MiscRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.WashingRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.HammerRecipeList;
import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.NMPostItems;
import com.itlesports.nightmaremode.item.NMTags;
import com.itlesports.nightmaremode.entity.EntityTier1NetherVillager;
import com.itlesports.nightmaremode.entity.EntityTier2NetherVillager;
import com.itlesports.nightmaremode.entity.EntityTier3NetherVillager;
import api.item.tag.TagInstance;
import api.item.tag.TagOrStack;
import api.item.tag.Tag;
import com.itlesports.nightmaremode.mixin.biomegen.BiomeGenBaseAccessor;
import com.itlesports.nightmaremode.skill.NMSkillNodes;
import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.function.Predicate;

public abstract class NMInitializer implements AchievementExt {
    private static void finishRecipes(String type){System.out.println("Finished initializing: [" + type + "]");}

    public static void initIFHYRecipes(){
        addCraftingRecipes();
        addWashingRecipes();
        addMiscRecipes();
        addHammerRecipes();
        addCampfireRecipes();
        addCrucibleRecipes();
        addCauldronRecipes();
        addCisternRecipes();
        addBrewingStandRecipes();
        addMillstoneRecipes();
        addOvenRecipes();
        addSoulforgeRecipes();
        addPistonPackingRecipes();
//        addBloodSawRecipes();
        addMultiplayerRecipes();
        addSkillLockedRecipes();
        finishRecipes("All Recipes");
    }


    public static void initMobSpawning(){



        finishRecipes("Mob Biome Spawning");
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

    private static void addMobToNether (Class mob, int i, int j, int k){
        addMobToBiome(mob, i, j, k, BiomeGenBase.hell);
    }
    private static void addMobToEnd(Class mob, int i, int j, int k){
        addMobToBiome(mob, i, j, k, BiomeGenBase.sky);
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



        finishRecipes("Achievement Edits");

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
        addNetherPostVillagerTrades();
        finishRecipes("All Trades");

    }

    public static void editExistingTrades(){

        finishRecipes("Trade Tweaks");

    }

    public static void miscInit(){
        NMFoodSpoilage.init();

        finishRecipes("Miscellaneous");

        finishRecipes("Config");

    }
    // trades begin here

    private static void buy(String name, int profession, int level, int id1, int meta, int count1, int count2, float w, boolean levelUp, int cost1, int cost2){
        TradeProvider.FinalStep step = TradeProvider.getBuilder().name(name).profession(profession).level(level).buy().item(id1, meta).itemCount(count1, count2).weight(w); // we have to add a variant that does emeraldCost. emeraldcost has to come after .item(), and takes 2 parameters (cost1, cost2) which are the min and max costs. additionally, .emeraldCost().itemCount() are not valid (cannot be used one after another)
        if(cost1 != 0 && cost2 != 0){
            ((TradeProvider.BuySellCountStep)(step)).emeraldCost(cost1, cost2);
        }
        if (levelUp) {
            step.addAsLevelUpTrade();
            return;
        }

        step.addToTradeList();
    }
    private static void sell(String name, int profession, int level, int id1, int meta, int c1, int c2, float w, boolean levelUp, int minCost, int maxCost){
        TradeProvider.FinalStep step = TradeProvider.getBuilder().name(name).profession(profession).level(level).sell().item(id1, meta).itemCount(c1, c2).weight(w);
        if(minCost != 0 && maxCost != 0){
            ((TradeProvider.BuySellCountStep)(step)).emeraldCost(minCost, maxCost);
        }
        if (levelUp) {
            step.addAsLevelUpTrade();
            return;
        }
        step.addToTradeList();
    }

    // Overloaded versions with defaults
    private static void buy(String name, int profession, int level, int id, int meta) {
        buy(name, profession, level, id, meta, 1, 1, 1.0f, false, 0, 0);
    }

    private static void buy(String name, int profession, int level, int id, int meta, int count1, int count2) {
        buy(name, profession, level, id, meta, count1, count2, 1.0f, false, 0, 0);
    }
    private static void buy(String name, int profession, int level, int id, int meta, int count1, int count2, int minCount, int maxCount) {
        buy(name, profession, level, id, meta, count1, count2, 1.0f, false, minCount, maxCount);
    }

    private static void buy(String name, int profession, int level, int id, int meta, int count1, int count2, float weight) {
        buy(name, profession, level, id, meta, count1, count2, weight, false, 0, 0);
    }

    private static void sell(String name, int profession, int level, int id, int meta, int count1, int count2) {
        sell(name, profession, level, id, meta, count1, count2, 1.0f, false,1 , 1);
    }

    private static void sell(String name, int profession, int level, int id, int meta, int count1, int count2, float weight) {
        sell(name, profession, level, id, meta, count1, count2, weight, false, 0, 0);
    }

    private static void convert(String name, int profession, int level, TradeItem firstInput, TradeItem secondInput, TradeItem output, float weight, boolean levelUp, boolean mandatory) {
        TradeProvider.ConvertSecondInputStep step = TradeProvider.getBuilder().name(name).profession(profession).level(level).convert().input(firstInput);
        if (secondInput != null && secondInput != TradeItem.EMPTY) {
            step = (TradeProvider.ConvertSecondInputStep) step.secondInput(secondInput);
        }
        ((TradeProvider.ConvertOutputStep) step).output(output).weight(weight);

        if (mandatory) {
            ((TradeProvider.FinalStep)step).mandatory();
        }
        if (levelUp) {
            ((TradeProvider.FinalStep)step).addAsLevelUpTrade();
        } else {
            ((TradeProvider.FinalStep)step).addToTradeList();
        }
    }

    // Overloaded versions with defaults
    private static void convert(String name, int profession, int level, TradeItem firstInput, TradeItem secondInput, TradeItem output) {
        convert(name, profession, level, firstInput, secondInput, output, 1.0f, false, false);
    }

    private static void convert(String name, int profession, int level, TradeItem firstInput, TradeItem secondInput, TradeItem output, float weight) {
        convert(name, profession, level, firstInput, secondInput, output, weight, false, false);
    }

    private static void convert(String name, int profession, int level, TradeItem firstInput, TradeItem secondInput, TradeItem output, boolean levelUp) {
        convert(name, profession, level, firstInput, secondInput, output, 1.0f, levelUp, false);
    }

    private static void convert(String name, int profession, int level, TradeItem firstInput, TradeItem secondInput, TradeItem output, boolean levelUp, boolean mandatory) {
        convert(name, profession, level, firstInput, secondInput, output, 1.0f, levelUp, mandatory);
    }

    private static void addFarmerTrades(){

        finishRecipes("Farmer Trades");

    }

    private static void addLibrarianTrades(){

        finishRecipes("Librarian Trades");
    }



    private static void addPriestTrades(){

        finishRecipes("Priest Trades");

    }


    private static void addBlacksmithTrades(){

        finishRecipes("Blacksmith Trades");
    }


    private static void addButcherTrades(){

        finishRecipes("Butcher Trades");

    }


    private static void addNightmareVillagerTrades(){
        finishRecipes("Nightmare Merchant Trades");

    }

    private static void addNetherPostVillagerTrades() {
        addTierOneNetherPostTrades();
        addTierTwoNetherPostTrades();
        addTierThreeNetherPostTrades();
        finishRecipes("Nether Post Villager Trades");
    }

    private static void addTierOneNetherPostTrades() {
        int profession = EntityTier1NetherVillager.PROFESSION_ID;

        buy("nmNetherTier1TungstenDust", profession, 1, NMItems.tungstenDust.itemID, 0, 8, 16, 1.0F);
        buy("nmNetherTier1TungstenChunk", profession, 1, NMItems.tungstenChunk.itemID, 0, 16, 32, 0.8F);
        buy("nmNetherTier1CrushedTungsten", profession, 1, NMItems.crushedTungsten.itemID, 0, 20, 48, 0.8F);
        buy("nmNetherTier1TungstenConcentrate", profession, 1, NMItems.tungstenConcentrate.itemID, 0, 16, 32, 0.7F);
        buy("nmNetherTier1Quartz", profession, 1, Item.netherQuartz.itemID, 0, 8, 32, 1.0F);
        buy("nmNetherTier1QuartzDust", profession, 1, NMItems.quartzDust.itemID, 0, 8, 12, 1.0F);
        buy("nmNetherTier1WorkbenchPart", profession, 1, NMItems.netherWorkbenchPart.itemID, 0, 1, 2, 0.8F);
        buy("nmNetherTier1SoulSand", profession, 1, Block.slowSand.blockID, 0, 48, 64, 1.0F);
        buy("nmNetherTier1Flint", profession, 1, Item.flint.itemID, 0, 12, 36, 1.0F);
        buy("nmNetherTier1FlintChip", profession, 1, NMItems.flintChip.itemID, 0, 32, 64, 1.0F);
        buy("nmNetherTier1SoulChip", profession, 1, NMItems.soulChip.itemID, 0, 10, 24, 1.0F);
        buy("nmNetherTier1SoulFlint", profession, 1, NMItems.soulFlint.itemID, 0, 2, 8, 0.7F);
        buy("nmNetherTier1PigHide", profession, 1, NMItems.pigHide.itemID, 0, 16, 32, 1.0F);
        buy("nmNetherTier1PighideString", profession, 1, NMItems.pighideString.itemID, 0, 8, 16, 0.8F);
        buy("nmNetherTier1GroundNetherrack", profession, 1, BTWItems.groundNetherrack.itemID, 0, 48, 64, 1.0F);
        buy("nmNetherTier1GhastTear", profession, 1, Item.ghastTear.itemID, 0, 12, 24, 0.5F);
        buy("nmNetherTier1CreeperOyster", profession, 1, BTWItems.creeperOysters.itemID, 0, 16, 32, 0.7F);
        buy("nmNetherTier1Nitre", profession, 1, BTWItems.nitre.itemID, 0, 48, 64, 0.8F);
        buy("nmNetherTier1Bone", profession, 1, Item.bone.itemID, 0, 48, 64, 1.0F);
        buy("nmNetherTier1Stick", profession, 1, NMItems.netherStick.itemID, 0, 16, 32, 1.0F);
        buy("nmNetherTier1Gravel", profession, 1, Block.gravel.blockID, 0, 48, 64, 1.0F);
        buy("nmNetherTier1Netherrack", profession, 1, Block.netherrack.blockID, 0, 48, 64, 1.0F);
        buy("nmNetherTier1BlazeRod", profession, 1, Item.blazeRod.itemID, 0, 2, 8, 0.6F);
        buy("nmNetherTier1RedMushroom", profession, 1, BTWItems.redMushroom.itemID, 0, 12, 16, 0.8F);
        buy("nmNetherTier1BrownMushroom", profession, 1, BTWItems.brownMushroom.itemID, 0, 12, 16, 0.8F);
        buy("nmNetherTier1MagmaCream", profession, 1, Item.magmaCream.itemID, 0, 2, 8, 0.7F);
        buy("nmNetherTier1GoldSword", profession, 1, Item.swordGold.itemID, Short.MAX_VALUE, 1, 1, 0.5F);
        buy("nmNetherTier1GoldNugget", profession, 1, Item.goldNugget.itemID, 0, 4, 8, 1.0F);
        buy("nmNetherTier1GoldIngot", profession, 1, Item.ingotGold.itemID, 0, 2, 4, 0.7F);
        buy("nmNetherTier1RottenFlesh", profession, 1, Item.rottenFlesh.itemID, 0, 24, 36, 1.0F);
        buy("nmNetherTier1FireCharge", profession, 1, Item.fireballCharge.itemID, 0, 8, 12, 0.8F);
        buy("nmNetherTier1GlowstoneDust", profession, 1, Item.glowstone.itemID, 0, 24, 48, 1.0F);
        buy("nmNetherTier1Glowstone", profession, 1, Block.glowStone.blockID, 0, 4, 8, 0.8F);
        buy("nmNetherTier1Wart", profession, 1, Item.netherStalkSeeds.itemID, 0, 16, 32, 1.0F);
        buy("nmNetherTier1Obsidian", profession, 1, Block.obsidian.blockID, 0, 8, 16, 0.6F);
        buy("nmNetherTier1Brick", profession, 1, Item.netherrackBrick.itemID, 0, 8, 16, 0.8F);
        buy("nmNetherTier1BrickBlock", profession, 1, Block.netherBrick.blockID, 0, 16, 32, 0.7F);
        buy("nmNetherTier1Saddle", profession, 1, Item.saddle.itemID, 0, 1, 2, 0.5F);
        buy("nmNetherTier1SilverScale", profession, 1, NMItems.searingSilverScale.itemID, 0, 4, 12, 0.8F);

        sell("nmNetherTier1Rails", profession, 1, Block.rail.blockID, 0, 12, 24, 1.2F, false, 2, 4);
        sell("nmNetherTier1Minecart", profession, 1, Item.minecartEmpty.itemID, 0, 1, 1, 0.7F, false, 5, 8);
        sell("nmNetherTier1Chest", profession, 1, BTWBlocks.chest.blockID, 0, 1, 2, 0.8F, false, 3, 6);
        sell("nmNetherTier1AzureSalt", profession, 1, NMItems.azureSalt.itemID, 0, 2, 6, 1.0F, false, 2, 4);
        sell("nmNetherTier1Redstone", profession, 2, Item.redstone.itemID, 0, 4, 8, 0.8F, false, 3, 6);
        sell("nmNetherTier1TungstenNugget", profession, 2, NMItems.tungstenNugget.itemID, 0, 2, 4, 0.7F, false, 4, 8);
        sell("nmNetherTier1PolishedShard", profession, 2, NMItems.polishedCrystalShard.itemID, 0, 1, 2, 0.7F, false, 5, 9);
        sell("nmNetherTier1BrewingStand", profession, 2, Block.brewingStand.blockID, 0, 1, 1, 0.5F, false, 12, 18);
        sell("nmNetherTier1Lapis", profession, 3, Item.dyePowder.itemID, 4, 2, 6, 0.7F, false, 4, 8);
        sell("nmNetherTier1PoweredRail", profession, 3, Block.railPowered.blockID, 0, 6, 12, 0.6F, false, 8, 14);
        sell("nmNetherTier1DetectorRail", profession, 3, Block.railDetector.blockID, 0, 6, 12, 0.6F, false, 8, 14);
        sell("nmNetherTier1TungstenIngot", profession, 3, NMItems.tungstenIngot.itemID, 0, 1, 1, 0.4F, false, 16, 24);
        sell("nmNetherTier1HighSpeedCart", profession, 4, NMItems.highSpeedMinecart.itemID, 0, 1, 1, 0.4F, false, 20, 32);
        sell("nmNetherTier1MinerDrill", profession, 2, NMBlocks.minerDrill.blockID, 0, 1, 1, 0.5F, false, 16, 24);

        buy("nmNetherTier1Level2", profession, 1, Block.blockGold.blockID, 0, 1, 1, 1.0F, true, 1, 1);
        buy("nmNetherTier1Level3", profession, 2, NMItems.redstoneCrystal.itemID, 0, 1, 1, 1.0F, true, 1, 1);
        convert("nmNetherTier1Level4Diamond", profession, 3,
                TradeItem.fromID(NMItems.redstoneCrystal.itemID, 4),
                TradeItem.fromIDAndMetadata(NMBlocks.netherProgressionGems.blockID, NMBlocks.META_PURPLE_GEM),
                TradeItem.fromID(Item.diamond.itemID), true, true);
        buy("nmNetherTier1Level5", profession, 4, Item.netherStar.itemID, 0, 1, 1, 1.0F, true, 1, 1);

        EntityVillager.defaultTradeByProfessionList.put(profession,
                TradeProvider.getBuilder().name("nmNetherTier1Default").profession(profession).level(1)
                        .buy().item(BTWItems.groundNetherrack.itemID).itemCount(48, 64).build());
    }

    private static void addTierTwoNetherPostTrades() {
        int profession = EntityTier2NetherVillager.PROFESSION_ID;


        buy("nmNetherTier2DenseCore", profession, 1, NMItems.denseNetherrackCore.itemID, 0, 8, 16, 1.2F);
        buy("nmNetherTier2RedstoneCrystal", profession, 1, NMItems.redstoneCrystal.itemID, 0, 4, 12, 0.9F);
        buy("nmNetherTier2AzureSlag", profession, 2, NMItems.azureSlag.itemID, 0, 4, 8, 0.8F);
        buy("nmNetherTier2LapisPrecipitate", profession, 2, NMItems.lapisPrecipitate.itemID, 0, 4, 8, 0.7F);
        buy("nmNetherTier2DeadzoneShard", profession, 3, NMItems.deadzoneShard.itemID, 0, 4, 10, 0.8F);

        sell("nmNetherTier2HempSeeds", profession, 1, BTWItems.hempSeeds.itemID, 0, 2, 4, 0.8F, false, 6, 10);
        sell("nmNetherTier2FertileNetherrack", profession, 1, NMBlocks.fertileNetherrack.blockID, 0, 4, 8, 0.8F, false, 8, 14);
        sell("nmNetherTier2Rope", profession, 2, BTWItems.rope.itemID, 0, 4, 8, 0.7F, false, 8, 14);
        sell("nmNetherTier2Axle", profession, 2, BTWBlocks.axle.blockID, 0, 2, 4, 0.6F, false, 10, 16);
        sell("nmNetherTier2Gearbox", profession, 2, BTWBlocks.gearBox.blockID, 0, 1, 1, 0.5F, false, 14, 20);
        sell("nmNetherTier2CisternInterface", profession, 2, NMBlocks.cisternInterface.blockID, 0, 1, 1, 0.6F, false, 12, 18);
        sell("nmNetherTier2ChunkLoader", profession, 3, NMBlocks.chunkLoader.blockID, 0, 1, 1, 0.35F, false, 24, 36);
        sell("nmNetherTier2BrewingStand", profession, 3, Block.brewingStand.blockID, 0, 1, 1, 0.4F, false, 12, 18);

        buy("nmNetherTier2Level2", profession, 1, NMBlocks.netherProgressionGems.blockID,
                NMBlocks.META_RED_GEM, 1, 1, 1.0F, true, 1, 1);
        convert("nmNetherTier2Level3Drill", profession, 2,
                TradeItem.fromID(NMBlocks.minerDrill.blockID),
                TradeItem.fromIDAndMetadata(NMBlocks.netherProgressionGems.blockID, NMBlocks.META_PURPLE_GEM),
                TradeItem.fromID(NMBlocks.minerDrillTier2.blockID), true, true);
        buy("nmNetherTier2Level4", profession, 3, NMItems.deadzoneShard.itemID, 0, 16, 16, 1.0F, true, 1, 1);
        convert("nmNetherTier2Level5Invocation", profession, 4,
                TradeItem.fromID(BTWItems.soulforgedSteelIngot.itemID, 4),
                TradeItem.fromIDAndMetadata(NMBlocks.netherProgressionGems.blockID, NMBlocks.META_BLACK_GEM),
                TradeItem.fromID(NMItems.invocationFragment.itemID), true, true);

        EntityVillager.defaultTradeByProfessionList.put(profession,
                TradeProvider.getBuilder().name("nmNetherTier2Default").profession(profession).level(1)
                        .buy().item(NMItems.denseNetherrackCore.itemID).itemCount(8, 16).build());
    }

    private static void addTierThreeNetherPostTrades() {
        int profession = EntityTier3NetherVillager.PROFESSION_ID;
//
//        buy("nmNetherTier3Level1", profession, 1, NMBlocks.netherProgressionGems.blockID,
//                NMBlocks.META_PURPLE_GEM, 1, 1, 1.0F, true, 1, 1);
        buy("nmNetherTier3DeadzoneShard", profession, 1, NMItems.deadzoneShard.itemID, 0, 8, 20, 1.2F);
        buy("nmNetherTier3RefinedRedstone", profession, 1, NMItems.refinedRedstone.itemID, 0, 8, 16, 0.8F);
        buy("nmNetherTier3AzureSlag", profession, 2, NMItems.azureSlag.itemID, 0, 8, 16, 0.8F);
        buy("nmNetherTier3Steel", profession, 3, BTWItems.soulforgedSteelIngot.itemID, 0, 4, 12, 0.7F);

        sell("nmNetherTier3Diamond", profession, 1, Item.diamond.itemID, 0, 1, 1, 0.35F, false, 24, 36);
        sell("nmNetherTier3Crucible", profession, 2, BTWBlocks.crucible.blockID, 0, 1, 1, 0.4F, false, 24, 36);
        sell("nmNetherTier3ChunkLoader", profession, 2, NMBlocks.chunkLoader.blockID, 0, 1, 1, 0.5F, false, 20, 30);
        sell("nmNetherTier3SteelIngot", profession, 3, BTWItems.soulforgedSteelIngot.itemID, 0, 1, 2, 0.5F, false, 18, 30);

        buy("nmNetherTier3Level2", profession, 1, NMItems.deadzoneShard.itemID, 0, 12, 12, 1.0F, true, 1, 1);
        convert("nmNetherTier3Level3Drill", profession, 2,
                TradeItem.fromID(NMBlocks.minerDrillTier2.blockID),
                TradeItem.fromIDAndMetadata(NMBlocks.netherProgressionGems.blockID, NMBlocks.META_BLACK_GEM),
                TradeItem.fromID(NMBlocks.minerDrillTier3.blockID), true, true);
        buy("nmNetherTier3Level4", profession, 3, BTWItems.soulforgedSteelIngot.itemID, 0, 16, 16, 1.0F, true, 1, 1);
        convert("nmNetherTier3Level5EndAccord", profession, 4,
                TradeItem.fromID(NMItems.deadzoneShard.itemID, 32),
                TradeItem.fromID(Item.diamond.itemID, 4),
                TradeItem.fromID(NMItems.endAccordFragment.itemID), true, true);

        EntityVillager.defaultTradeByProfessionList.put(profession,
                TradeProvider.getBuilder().name("nmNetherTier3Default").profession(profession).level(1)
                        .buy().item(NMItems.deadzoneShard.itemID).itemCount(8, 20).build());
    }


    private static void addCrucibleRecipes(){
        CrucibleStokedCraftingManager crucible = CrucibleStokedCraftingManager.getInstance();


        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot), new ItemStack[]{
                new ItemStack(BTWItems.diamondIngot),
                new ItemStack(Item.netherQuartz, 4),
                new ItemStack(NMItems.denseNetherrackCore),
                new ItemStack(NMItems.nickelHeatComponent),
                new ItemStack(NMItems.precisionCrystalGear)
        });


        crucible.removeRecipe(new ItemStack(BTWItems.soulforgedSteelIngot, 1), new ItemStack[]{
                new ItemStack(Item.ingotIron), new ItemStack(BTWItems.coalDust),
                new ItemStack(BTWItems.soulUrn), new ItemStack(BTWItems.soulFlux)
        });
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.soulforgedSteelIngot), new ItemStack[]{
                new ItemStack(Item.ingotIron),
                new ItemStack(BTWItems.coalDust, 2),
                new ItemStack(BTWItems.soulUrn),
                new ItemStack(BTWItems.enderSlag),
                new ItemStack(NMItems.denseNetherrackCore),
                new ItemStack(NMItems.nickelHeatComponent),
                new ItemStack(NMItems.lithiumHeatCompound)
        });


        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.bloodIngot), new ItemStack[]{
                new ItemStack(NMItems.refinedDiamondIngot),
                new ItemStack(NMItems.bloodOrb, 4),
                new ItemStack(NMItems.deadzoneShard),
                new ItemStack(NMItems.nickelBinding),
                new ItemStack(NMItems.lithiumHeatCompound),
                new ItemStack(NMItems.precisionCrystalGear)
        });


        finishRecipes("Crucible Recipes");

    }
    private static void addCauldronRecipes(){
        // BTW has separate tannin-strength variants (and a pre-cut shortcut). IFHY instead
        // requires the hide to be washed and worked after scouring before its final bark and
        // dung tanning bath.
        CauldronCraftingManager cauldron = CauldronCraftingManager.getInstance();
        int[] barkCounts = {8, 5, 3, 2};
        Tag[] barkTags = {
                BTWTags.lowTanninBarks,
                BTWTags.mediumTanninBarks,
                BTWTags.highTanninBarks,
                BTWTags.veryHighTanninBarks
        };
        for (int index = 0; index < barkCounts.length; ++index) {
            TagOrStack[] regularInputs = {
                    new ItemStack(BTWItems.dung),
                    new ItemStack(BTWItems.scouredLeather),
                    TagInstance.of(barkTags[index], barkCounts[index])
            };
            cauldron.removeRecipe(new ItemStack(BTWItems.tannedLeather), regularInputs);

            TagOrStack[] oldCutInputs = {
                    new ItemStack(BTWItems.dung),
                    new ItemStack(BTWItems.cutScouredLeather, 2),
                    TagInstance.of(barkTags[index], barkCounts[index])
            };
            cauldron.removeRecipe(new ItemStack(BTWItems.cutTannedLeather, 2), oldCutInputs);

            TagOrStack[] workedCutInputs = {
                    new ItemStack(BTWItems.dung),
                    new ItemStack(NMItems.workedScouredLeather),
                    TagInstance.of(barkTags[index], barkCounts[index])
            };
            cauldron.addRecipe(new ItemStack(BTWItems.cutTannedLeather, 2), workedCutInputs);
        }
        finishRecipes("Cauldron Recipes");

    }

    private static void addCisternRecipes(){
        CisternRecipeManager manager = CisternRecipeManager.instance;

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(BTWItems.goldOrePile, 2), new ItemStack(BTWItems.coalDust)},
                CisternTileEntity.FLUID_WATER, 1, 4, 360,
                new ItemStack[]{new ItemStack(Item.goldNugget)})
                .addRandomOutput(new ItemStack(NMItems.refinementWaste), 0.20F)
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.uncleanedCrystalShard)},
                CisternTileEntity.FLUID_WATER, 0, 1, 120,
                new ItemStack[]{new ItemStack(NMItems.cleanCrystalShard, 1, 79)}));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.crushedNickelRock)},
                CisternTileEntity.FLUID_WATER, 0, 1, 180,
                new ItemStack[]{new ItemStack(NMItems.washedNickelConcentrate)})
                .addRandomOutput(new ItemStack(NMItems.refinementWaste), 0.25F)
                .setResultingFluid(CisternTileEntity.FLUID_SLURRY));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.rawLithium)},
                CisternTileEntity.FLUID_WATER, 0, 2, 160,
                new ItemStack[0])
                .setResultingFluid(CisternTileEntity.FLUID_BRINE));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.hammeredLithium)},
                CisternTileEntity.FLUID_WATER, 0, 1, 140,
                new ItemStack[]{new ItemStack(NMItems.washedLithium)}));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.crackedDiamondBearingRock)},
                CisternTileEntity.FLUID_WATER, 0, 2, 240,
                new ItemStack[]{new ItemStack(NMItems.washedDiamondGrit)})
                .addRandomOutput(new ItemStack(NMItems.refinementWaste), 0.35F)
                .setResultingFluid(CisternTileEntity.FLUID_SLURRY));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.washedDiamondGrit), new ItemStack(NMItems.lithiumStabilizer)},
                CisternTileEntity.FLUID_BRINE, 1, 3, 300,
                new ItemStack[]{new ItemStack(NMItems.stabilizedDiamondSlurry)})
                .addRandomOutput(new ItemStack(NMItems.refinementWaste), 0.25F));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.stabilizedDiamondSlurry), new ItemStack(NMItems.polishedCrystalShard)},
                CisternTileEntity.FLUID_SLURRY, 2, 6, 420,
                new ItemStack[]{new ItemStack(NMItems.seededDiamondMatrix)})
                .addRandomOutput(new ItemStack(NMItems.failedDiamondRefinement), 0.12F)
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.nickelBoundDiamondMatrix), new ItemStack(NMItems.lithiumHeatCompound)},
                CisternTileEntity.FLUID_BRINE, 3, 8, 600,
                new ItemStack[]{new ItemStack(NMItems.diamondBearingMaterial)})
                .addRandomOutput(new ItemStack(NMItems.refinementWaste), 0.35F)
                .addRandomOutput(new ItemStack(NMItems.failedDiamondRefinement), 0.08F)
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{
                        new ItemStack(Item.diamond),
                        new ItemStack(Item.ingotIron),
                        new ItemStack(NMItems.nickelBinding),
                        new ItemStack(NMItems.lithiumStabilizer)
                },
                CisternTileEntity.FLUID_SLURRY, 2, 12, 720,
                new ItemStack[]{new ItemStack(BTWItems.diamondIngot)})
                .addRandomOutput(new ItemStack(NMItems.refinementWaste), 0.20F)
                .setConsumesFluid());

        // Failed batches remain expensive to reclaim.  These loops make refinement waste a
        // planning problem instead of a harmless dead item while consuming the specialty ores
        // again at a higher heat and stir requirement.
        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.refinementWaste, 2), new ItemStack(NMItems.lithiumSalt), new ItemStack(NMItems.polishedCrystalShard)},
                CisternTileEntity.FLUID_BRINE, 2, 7, 540,
                new ItemStack[]{new ItemStack(NMItems.washedDiamondGrit)})
                .addRandomOutput(new ItemStack(NMItems.refinementWaste), 0.20F)
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.failedDiamondRefinement), new ItemStack(NMItems.nickelBinding), new ItemStack(NMItems.lithiumStabilizer)},
                CisternTileEntity.FLUID_BRINE, 3, 10, 720,
                new ItemStack[]{new ItemStack(NMItems.diamondBearingMaterial)})
                .addRandomOutput(new ItemStack(NMItems.refinementWaste), 0.30F)
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(BTWItems.hemp)},
                CisternTileEntity.FLUID_BRINE, 0, 2, 240,
                new ItemStack[]{new ItemStack(NMItems.rettedHemp)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(BTWItems.scouredLeather)},
                CisternTileEntity.FLUID_WATER, 0, 1, 180,
                new ItemStack[]{new ItemStack(NMItems.washedScouredLeather)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.obsidianPowder), new ItemStack(Item.magmaCream)},
                CisternTileEntity.FLUID_LAVA, 3, 0, 200,
                new ItemStack[]{new ItemStack(NMItems.obsidianPaste)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(BTWItems.nitre, 3), new ItemStack(Item.netherQuartz)},
                CisternTileEntity.FLUID_LAVA, 3, 10, 400,
                new ItemStack[]{new ItemStack(NMItems.redstoneCrystal)})
                .addRandomOutput(new ItemStack(NMItems.redstoneCrystal), 0.12F)
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{
                        new ItemStack(Item.redstone, 4),
                        new ItemStack(NMItems.quartzDust, 2),
                        new ItemStack(BTWItems.soulSandPile),
                        new ItemStack(BTWItems.brimstone),
                        new ItemStack(NMItems.azureSalt)
                },
                CisternTileEntity.FLUID_LAVA, 3, 12, 600,
                new ItemStack[]{new ItemStack(NMItems.azureSlag)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{
                        new ItemStack(Item.redstone, 4),
                        new ItemStack(NMItems.quartzDust, 2),
                        new ItemStack(BTWItems.soulSandPile),
                        new ItemStack(BTWItems.brimstone),
                        new ItemStack(NMItems.searingSilverScale, 2)
                },
                CisternTileEntity.FLUID_LAVA, 3, 12, 600,
                new ItemStack[]{new ItemStack(NMItems.azureSlag)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.washedAzureSediment), new ItemStack(NMItems.aquamarine)},
                CisternTileEntity.FLUID_WATER, 0, 8, 480,
                new ItemStack[]{new ItemStack(NMItems.lapisPrecipitate)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{
                        new ItemStack(Block.netherrack, 4),
                        new ItemStack(NMItems.moistureFertilizer),
                        new ItemStack(NMItems.potassiumFertilizer)
                },
                CisternTileEntity.FLUID_WATER, 0, 4, 360,
                new ItemStack[]{new ItemStack(NMBlocks.fertileNetherrack, 4)})
                .setConsumesFluid());

        finishRecipes("Cistern Recipes");
    }

    private static void addBrewingStandRecipes(){
        // brewing stand recipes are registered here as they are added
        BrewingStandRecipeManager manager = BrewingStandRecipeManager.instance;

        manager.addRecipe(new ItemStack(Item.beefCooked), new ItemStack(Item.magmaCream), new ItemStack(Item.stick));
        finishRecipes("Brewing Stand Recipes");
    }

    private static void addWashingRecipes() {
        WashingRecipeManager manager = WashingRecipeManager.instance;
        manager.addWaterRecipe(
                new ItemStack(NMPostItems.washedIronMix),
                new ItemStack(NMPostItems.stompedCrushedIronStoneMix),
                4000);
        manager.addWaterRecipe(
                new ItemStack(NMItems.washedPith),
                new ItemStack(NMItems.reedStem),
                4000);
        manager.addWaterRecipe(
                new ItemStack(NMItems.washedSugarCane),
                new ItemStack(Item.reed),
                4000);
        manager.addWaterRecipe(
                new ItemStack(NMItems.washedHemp),
                new ItemStack(NMItems.rettedHemp),
                4000);
        manager.addWaterRecipe(
                new ItemStack(NMItems.washedAzureSediment),
                new ItemStack(NMItems.crushedAzureStone),
                5000);
        manager.addRainRecipe(
                NMBlocks.blockWashedIronLayer,
                NMBlocks.blockCrushedIronLayer,
                40,
                4);
        finishRecipes("Washing Recipes");
    }

    private static void addMiscRecipes() {
        MiscRecipeManager.instance.addRecipe(
                new ItemStack(NMPostItems.stompedCrushedIronStoneMix),
                new ItemStack(BTWItems.ironOreChunk),
                "When jumped on");    // visual for EMI
        MiscRecipeManager.instance.addBlockRecipe(
                NMBlocks.blockCrushedIronLayer,
                BTWBlocks.ironOreChunk,
                "When jumped on");
        MiscRecipeManager.instance.addRecipe(
                new ItemStack(BTWItems.netherBrick),
                new ItemStack(BTWItems.unfiredNetherBrick),
                "Next to Lava");

        MiscRecipeManager.instance.addRecipe(
                new ItemStack(NMItems.driedPlantFiber),
                new ItemStack(NMItems.plantFiber),
                "Dry for 120s");



        finishRecipes("Miscellaneous Recipes");
    }

    private static void addOvenRecipes(){
        KilnCraftingManager.instance.removeRecipe(new ItemStack[]{new ItemStack(Item.goldNugget)},
                BTWBlocks.goldOreChunk, new int[]{Short.MAX_VALUE}, (byte) 8);
        KilnCraftingManager.instance.removeRecipe(new ItemStack[]{new ItemStack(Item.ingotGold)},
                BTWBlocks.goldOreChunkStorage, new int[]{Short.MAX_VALUE}, (byte) 8);
        KilnCraftingManager.instance.removeRecipe(new ItemStack[]{new ItemStack(Item.goldNugget)},
                Block.oreGold, new int[]{Short.MAX_VALUE}, (byte) 8);
        FurnaceRecipes.smelting().getSmeltingList().remove(BTWItems.ironOreChunk.itemID);
        FurnaceRecipes.smelting().getSmeltingList().remove(BTWItems.goldOreChunk.itemID);
        FurnaceRecipes.smelting().getSmeltingList().remove(Block.oreDiamond.blockID);
        FurnaceRecipes.smelting().addSmelting(NMPostItems.washedIronMix.itemID, new ItemStack(NMItems.ironBloom), 0.0f, 3);
        FurnaceRecipes.smelting().addSmelting(NMItems.washedNickelConcentrate.itemID, new ItemStack(NMItems.roastedNickelConcentrate), 0.0f, 2);
        FurnaceRecipes.smelting().addSmelting(NMItems.roastedNickelConcentrate.itemID, new ItemStack(NMItems.nickelIngot), 0.4f, 3);
        FurnaceRecipes.smelting().addSmelting(NMItems.washedLithium.itemID, new ItemStack(NMItems.refinedLithium), 0.2f, 1);
        FurnaceRecipes.smelting().addSmelting(NMItems.diamondBearingMaterial.itemID, new ItemStack(Item.diamond), 1.0f, 4);

        FurnaceRecipes.smelting().addSmelting(NMItems.debonedRawFish.itemID, new ItemStack(Item.fishCooked), 0.0f);
        FurnaceRecipes.smelting().addSmelting(NMItems.wetFusedPlantSheet.itemID, new ItemStack(NMItems.plantSheet), 0.0f);
        FurnaceRecipes.smelting().addSmelting(NMItems.tungstenConcentrate.itemID, new ItemStack(NMItems.brittleTungstenCake), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.pureTungstenChunk.itemID, new ItemStack(NMItems.tungstenNugget), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.obsidianPaste.itemID, new ItemStack(NMItems.obsidianBrick), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.washedHemp.itemID, new ItemStack(NMItems.driedHemp), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.azureSlag.itemID, new ItemStack(NMItems.brittleAzureCake), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.lapisPrecipitate.itemID, new ItemStack(NMItems.brittleAzureCake), 0.0F);

        finishRecipes("Oven Recipes");

    }
    private static void addSoulforgeRecipes(){
        SoulforgeCraftingManager soulforge = SoulforgeCraftingManager.getInstance();

        soulforge.removeRecipe(new ItemStack(BTWItems.steelSword), new Object[]{"#", "#", "#", "X", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft});
        soulforge.removeRecipe(new ItemStack(BTWItems.steelShovel), new Object[]{"#", "X", "X", "X", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft});
        soulforge.removeRecipe(new ItemStack(BTWItems.steelPickaxe), new Object[]{"###", " X ", " X ", " X ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft});
        soulforge.removeRecipe(new ItemStack(BTWItems.mattock), new Object[]{" ###", "# X ", "  X ", "  X ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft});
        soulforge.removeRecipe(new ItemStack(BTWItems.steelHoe), new Object[]{"##", " X", " X", " X", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft});
        soulforge.removeRecipe(new ItemStack(BTWItems.battleaxe), new Object[]{"###", "#X#", " X ", " X ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft});
        soulforge.removeRecipe(new ItemStack(BTWItems.steelAxe), new Object[]{"# ", "#X", " X", " X", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft});
        soulforge.removeRecipe(new ItemStack(BTWItems.plateHelmet), new Object[]{"####", "#  #", "#  #", " XX ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.steelArmorPlate});
        soulforge.removeRecipe(new ItemStack(BTWItems.plateBreastplate), new Object[]{"X  X", "####", "####", "####", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.steelArmorPlate});
        soulforge.removeRecipe(new ItemStack(BTWItems.plateLeggings), new Object[]{"####", "X##X", "X  X", "X  X", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.steelArmorPlate});
        soulforge.removeRecipe(new ItemStack(BTWItems.plateBoots), new Object[]{" ## ", " ## ", "#XX#", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.steelArmorPlate});
        soulforge.removeRecipe(new ItemStack(BTWBlocks.dormandSoulforge), new Object[]{"####", " #  ", " #  ", "####", Character.valueOf('#'), Item.ingotGold});

        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.steelSword), new Object[]{" N# ", " C# ", " P# ", " LX ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), TagInstance.of(BTWTags.highQualityToolHandles), Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.steelShovel), new Object[]{" CP ", " N# ", " L# ", "  X ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), TagInstance.of(BTWTags.highQualityToolHandles), Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.steelPickaxe), new Object[]{"####", "NPC ", " LX ", " PX ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), TagInstance.of(BTWTags.highQualityToolHandles), Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('C'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.mattock), new Object[]{"####", "#NPC", "  LX", "  PX", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), TagInstance.of(BTWTags.highQualityToolHandles), Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('C'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.steelHoe), new Object[]{"##NP", "  C ", " LX ", " PX ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), TagInstance.of(BTWTags.highQualityToolHandles), Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('C'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.battleaxe), new Object[]{"####", "#X#N", "PCXL", "  X ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), TagInstance.of(BTWTags.highQualityToolHandles), Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('C'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.steelAxe), new Object[]{"##NP", "#CXL", "  X ", "  X ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), TagInstance.of(BTWTags.highQualityToolHandles), Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('C'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.plateHelmet), new Object[]{"####", "#NN#", "#CC#", "PLLP", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('C'), NMItems.crystalLens, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.plateBreastplate), new Object[]{"PNNP", "####", "#LL#", "#CC#", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('L'), NMItems.lithiumHeatCompound, Character.valueOf('C'), NMItems.precisionCrystalGear});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.plateLeggings), new Object[]{"####", "PNNP", "#LL#", "#  #", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.plateBoots), new Object[]{"PNNP", "#LL#", "#CC#", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound, Character.valueOf('C'), NMItems.deadzoneShard});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWBlocks.dormandSoulforge), new Object[]{"GNNG", "GDCG", "GPLG", "GGGG", Character.valueOf('G'), Item.ingotGold, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('D'), NMItems.refinedDiamondIngot, Character.valueOf('C'), NMItems.denseNetherrackCore, Character.valueOf('P'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});

        finishRecipes("Soulforge Recipes");

    }
    private static void addCampfireRecipes(){
        RecipeManager.addCampfireRecipe(NMItems.cupOfSap.itemID, new ItemStack(NMItems.thickenedSap));
        RecipeManager.addCampfireRecipe(NMItems.debonedRawFish.itemID, new ItemStack(Item.fishCooked));

        finishRecipes("Campfire Recipes");

    }

    private static void addMillstoneRecipes(){
        // Remove the cut-hide bypass: every leather product begins with this one scouring pass.
        MillStoneCraftingManager millstone = MillStoneCraftingManager.getInstance();
        millstone.removeRecipe(new ItemStack(BTWItems.scouredLeather), new ItemStack(Item.leather));
        millstone.removeRecipe(new ItemStack(BTWItems.cutScouredLeather), new ItemStack(BTWItems.cutLeather));
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.scouredLeather), new ItemStack(Item.leather));
        millstone.removeRecipe(new ItemStack(BTWItems.hempFibers, 4), new ItemStack(BTWItems.hemp));


        finishRecipes("Millstone Recipes");

    }

    private static void addCraftingRecipes(){
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.twigSharpening, 1, 199), new ItemStack[]{new ItemStack(NMItems.twig), new ItemStack(NMItems.twig), new ItemStack(NMItems.twig), new ItemStack(NMItems.twig)});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.sharpTwigBarkWrapping, 1, 49), new Object[]{new ItemStack(NMItems.sharpTwig), BTWTags.barks, BTWTags.barks, BTWTags.barks});

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.bowDrill), new Object[]{new ItemStack(Item.stick), new ItemStack(Item.stick), BTWTags.strings});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.bowDrill), new Object[]{new ItemStack(Item.stick), new ItemStack(Item.stick), new ItemStack(Item.stick), BTWTags.strings});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.idleLooseOven), new Object[]{"XX", "XX", Character.valueOf('X'), BTWBlocks.looseBrickSlab});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.paper, 3), new Object[]{"###", Character.valueOf('#'), Item.reed});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.wickerWeaving, 1, 299), new Object[]{"##", "##", Character.valueOf('#'), Item.reed});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.unlitCampfire), new Object[]{"XX", "XX", Character.valueOf('X'), Item.stick});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.sharpStone), new Object[]{BTWTags.looseRocks});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.diamondIngot), new Object[]{new ItemStack(Item.ingotIron), new ItemStack(Item.diamond), new ItemStack(BTWItems.creeperOysters)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.clay), new Object[]{BTWItems.clayPile, BTWItems.clayPile});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.unfiredCrudeBrick), new Object[]{Item.clay});

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeStone), new Object[]{"XXX", "S# ", " # ", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWTags.looseRocks, Character.valueOf('S'), BTWTags.strings});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeStone), new Object[]{"XXX", " # ", " # ", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWTags.looseRocks});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.axeStone), new Object[]{BTWTags.lowQualityToolHandles, BTWTags.looseRocks, BTWTags.looseRocks, BTWTags.strings});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.axeStone), new Object[]{BTWTags.lowQualityToolHandles, BTWTags.looseRocks, BTWTags.looseRocks});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.shovelStone), new Object[]{BTWTags.lowQualityToolHandles, BTWTags.looseRocks, BTWTags.strings});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.shovelStone), new Object[]{BTWTags.lowQualityToolHandles, BTWTags.looseRocks});

        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.scrapedBark), new Object[]{BTWTags.barks, new ItemStack(BTWItems.sharpStone, 1, Short.MAX_VALUE)});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.crudeStringCrafting, 1, NMItems.crudeStringCrafting.getMaxDamage() - 1), new Object[]{NMItems.driedPlantFiber,NMItems.driedPlantFiber,NMItems.driedPlantFiber, BTWTags.flowers});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.primitiveGlue), new Object[]{NMItems.thickenedSap, BTWItems.coalDust});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.woodCupCrafting, 1, NMItems.woodCupCrafting.getMaxDamage() - 1), new Object[]{new ItemStack(NMItems.woodClump, 1, Short.MAX_VALUE), new ItemStack(BTWItems.pointyStick, 1, Short.MAX_VALUE)});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.reedPeeling, 1, NMItems.reedPeeling.getMaxDamage() - 1), new Object[]{Item.reed});
        RecipeManager.addRecipe(new ItemStack(Item.paper), new Object[]{"###", Character.valueOf('#'), NMItems.plantSheet});
        RecipeManager.addRecipe(new ItemStack(BTWItems.wickerWeaving, 1, 299), new Object[]{"###", "###", "###", Character.valueOf('#'), NMItems.washedSugarCane});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.pileOfSticks), new Object[]{Item.stick, Item.stick, Item.stick, Item.stick});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.soulFlint), new Object[]{NMItems.soulChip, NMItems.soulChip, NMItems.soulChip, NMItems.soulChip});
        RecipeManager.addShapelessRecipe(
                new ItemStack(NMItems.pighideStringCrafting, 1, NMItems.pighideStringCrafting.getMaxDamage() - 1),
                new Object[]{NMItems.pigHide, new ItemStack(NMItems.soulFlint, 1, 0)});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.netherWorkbenchPart), new Object[]{BTWItems.groundNetherrack, BTWItems.soulSandPile, NMItems.tungstenDust, NMItems.quartzDust});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.netherWorkbench), new Object[]{"##", "##", Character.valueOf('#'), NMItems.netherWorkbenchPart});
        RecipeManager.addRecipe(new ItemStack(NMItems.netherrackChunk), new Object[]{"###", "###", "###", Character.valueOf('#'), BTWItems.groundNetherrack});
        RecipeManager.addRecipe(new ItemStack(NMItems.netherrackPickaxe), new Object[]{"CCC", "TST", " S ", Character.valueOf('C'), NMItems.netherrackChunk, Character.valueOf('T'), NMItems.pighideString, Character.valueOf('S'), NMItems.netherStick});
        RecipeManager.addRecipe(new ItemStack(NMItems.netherFishingRod), new Object[]{"  S", " SB", "S T", Character.valueOf('S'), NMItems.netherStick, Character.valueOf('B'), NMItems.boneShard, Character.valueOf('T'), NMItems.pighideString});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.netherSludge), new Object[]{BTWItems.groundNetherrack, BTWItems.soulSandPile, NMItems.ashClump, BTWItems.gravelPile});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.hellforge), new Object[]{"##", "##", Character.valueOf('#'), BTWBlocks.looseNetherBrickSlab});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.netherrackAnvil), new Object[]{"###", " # ", "###", Character.valueOf('#'), Block.netherrack});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.obsidianMillstone), new Object[]{"BBB", "BGB", "BBB", Character.valueOf('B'), NMItems.obsidianBrick, Character.valueOf('G'), BTWItems.gear});
        RecipeManager.addRecipe(new ItemStack(Block.netherrack), new Object[]{"###", "#S#", "###", Character.valueOf('#'), NMItems.netherrackChunk, Character.valueOf('S'), BTWItems.netherSludge});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.netherrackChunk, 4), new Object[]{new ItemStack(Block.netherrack, 1, 0)});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.netherrackHammer), new Object[]{Block.netherrack, NMItems.netherStick, NMItems.pighideString});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.tungstenChunk), new Object[]{NMItems.tungstenDust, NMItems.tungstenDust});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.refinedRedstone), new Object[]{NMItems.redstoneCrystal, NMItems.polishedCrystalShard});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.hydraulicLens), new Object[]{NMItems.aquamarine, NMItems.polishedCrystalShard, Block.glass});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.fluidGauge), new Object[]{NMItems.aquamarine, NMItems.nickelPlate, NMItems.hydraulicLens});
        RecipeManager.addRecipe(new ItemStack(BTWItems.redstoneEye, 2), new Object[]{"###", "GGG", " R ", Character.valueOf('#'), NMItems.aquamarine, Character.valueOf('G'), Item.goldNugget, Character.valueOf('R'), Item.redstone});

        RecipeManager.addRecipe(new ItemStack(Block.netherrack, 1, 2), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(Block.netherrack, 1, 0)});
        RecipeManager.addRecipe(new ItemStack(Block.netherrack, 1, 3), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(Block.netherrack, 1, 2)});
        RecipeManager.addRecipe(new ItemStack(Block.netherrack, 1, 4), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(Block.netherrack, 1, 3)});

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.rail, 12), new Object[]{"X X", "XSX", "X X", Character.valueOf('X'), BTWItems.ironNugget, Character.valueOf('S'), Item.stick});
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.railPowered, 6), new Object[]{"X X", "XSX", "XRX", Character.valueOf('X'), BTWItems.ironNugget, Character.valueOf('S'), Item.stick, Character.valueOf('R'), BTWItems.redstoneLatch});
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.railDetector, 6), new Object[]{"X X", "X#X", "XRX", Character.valueOf('X'), BTWItems.ironNugget, Character.valueOf('R'), Item.redstone, Character.valueOf('#'), BTWTags.stonePressurePlates});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.minecartEmpty), new Object[]{"# #", "###", Character.valueOf('#'), Item.ingotIron});
        RecipeManager.addRecipe(new ItemStack(Block.rail, 12), new Object[]{"X X", "XSX", "X X", Character.valueOf('X'), NMTags.ironTungstenNuggets, Character.valueOf('S'), Item.stick});
        RecipeManager.addRecipe(new ItemStack(Block.railPowered, 6), new Object[]{"X X", "XSX", "XRX", Character.valueOf('X'), NMTags.ironTungstenNuggets, Character.valueOf('S'), Item.stick, Character.valueOf('R'), BTWItems.redstoneLatch});
        RecipeManager.addRecipe(new ItemStack(Block.railDetector, 6), new Object[]{"XFX", "X#X", "XRX", Character.valueOf('X'), NMTags.ironTungstenNuggets, Character.valueOf('F'), NMTags.netherSignalBinders, Character.valueOf('R'), Item.redstone, Character.valueOf('#'), BTWTags.stonePressurePlates});
        RecipeManager.addRecipe(new ItemStack(Block.railActivator, 6), new Object[]{"XSX", "X#X", "XSX", Character.valueOf('X'), NMTags.ironTungstenNuggets, Character.valueOf('#'), Block.torchRedstoneActive, Character.valueOf('S'), Item.stick});
        RecipeManager.addRecipe(new ItemStack(Item.minecartEmpty), new Object[]{"# #", "###", Character.valueOf('#'), NMTags.ironTungstenIngots});
        RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.minerDrill), new Object[]{Block.netherrack, NMItems.tungstenIngot, Item.redstone});
        RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.cisternInterface), new Object[]{NMItems.tungstenIngot, NMItems.tungstenIngot, Item.redstone, Block.netherBrick});
        RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.cisternStirrer), new Object[]{NMBlocks.cisternInterface, BTWBlocks.gearBox, BTWBlocks.axle, Item.redstone});
        RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.cisternDrain), new Object[]{NMBlocks.cisternInterface, NMItems.tungstenIngot, Item.redstone});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.chunkLoader), new Object[]{"OTO", "TRT", "OTO", Character.valueOf('O'), NMItems.obsidianBrick, Character.valueOf('T'), NMItems.tungstenIngot, Character.valueOf('R'), NMItems.refinedRedstone});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.invocationSeal), new Object[]{NMItems.invocationFragment, NMItems.invocationFragment, NMItems.invocationFragment, NMItems.invocationFragment});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.endAccord), new Object[]{NMItems.endAccordFragment, NMItems.endAccordFragment, NMItems.endAccordFragment, NMItems.endAccordFragment});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.tungstenConcentrate), new Object[]{NMItems.crushedTungsten, Item.netherQuartz});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.pureTungstenChunk), new Object[]{NMItems.tungstenPowder, NMItems.tungstenPowder});
        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenIngot), new Object[]{"###", "###", "###", Character.valueOf('#'), NMItems.tungstenNugget});
        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenBucket), new Object[]{"# #", " # ", Character.valueOf('#'), NMItems.tungstenIngot});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.cistern), new Object[]{"ISI", "ISI", "III", Character.valueOf('I'), NMItems.tungstenIngot, Character.valueOf('S'), BTWItems.netherSludge});
        RecipeManager.addRecipe(new ItemStack(Block.obsidian, 1, 0), new Object[]{"BBB", "BSB", "BBB", Character.valueOf('B'), NMItems.obsidianBrick, Character.valueOf('S'), BTWItems.netherSludge});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.stoneKnife), new Object[]{new ItemStack(BTWItems.sharpStone, 1, Short.MAX_VALUE), Item.stick, NMTags.knifeStrings});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.ironKnife), new Object[]{Item.ingotIron, Item.stick, NMTags.knifeStrings});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.diamondKnife), new Object[]{BTWItems.diamondIngot, Item.stick, NMTags.knifeStrings});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.goldKnife), new Object[]{Item.ingotGold, Item.stick, NMTags.knifeStrings});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.tungstenKnife), new Object[]{NMItems.tungstenIngot, Item.stick, NMTags.knifeStrings});
        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenPickaxe), new Object[]{"III", " S ", " S ", Character.valueOf('I'), NMItems.tungstenIngot, Character.valueOf('S'), NMItems.netherStick});
        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenShovel), new Object[]{" I ", " S ", " S ", Character.valueOf('I'), NMItems.tungstenIngot, Character.valueOf('S'), NMItems.netherStick});
        RecipeManager.addRecipe(new ItemStack(NMItems.ironScythe), new Object[]{" II", "IS ", " S ", Character.valueOf('I'), Item.ingotIron, Character.valueOf('S'), Item.stick});
        RecipeManager.addRecipe(new ItemStack(NMItems.diamondScythe), new Object[]{" II", "IS ", " S ", Character.valueOf('I'), BTWItems.diamondIngot, Character.valueOf('S'), Item.stick});
        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenScythe), new Object[]{" II", "IS ", " S ", Character.valueOf('I'), NMItems.tungstenIngot, Character.valueOf('S'), NMItems.netherStick});
        RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.chuteHopper), new Object[]{BTWBlocks.hopper, Item.redstone});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.highSpeedMinecart), new Object[]{Item.minecartEmpty, NMItems.tungstenNugget});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.highSpeedChestMinecart), new Object[]{Item.minecartCrate, NMItems.tungstenNugget});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.drill), new Object[]{new ItemStack(BTWItems.pointyStick, 1, Short.MAX_VALUE), Item.stick, NMItems.primitiveGlue, BTWItems.sawDust});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.shovelWood), new Object[]{BTWTags.logs, Item.stick, NMItems.primitiveGlue});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.woodHammer), new Object[]{BTWTags.logs, BTWTags.logs, Item.stick, NMItems.primitiveGlue});

        RecipeManager.addRecipe(new ItemStack(Item.pickaxeStone), new Object[]{"CCC", "TSG", " S ", Character.valueOf('C'), BTWTags.cobblestones, Character.valueOf('T'), Item.silk, Character.valueOf('S'), Item.stick, Character.valueOf('G'), NMItems.primitiveGlue});
        RecipeManager.addRecipe(new ItemStack(Item.axeStone), new Object[]{"CCG", "CTS", " S ", Character.valueOf('C'), BTWTags.cobblestones, Character.valueOf('T'), Item.silk, Character.valueOf('S'), Item.stick, Character.valueOf('G'), NMItems.primitiveGlue});
        RecipeManager.addRecipe(new ItemStack(Item.shovelStone), new Object[]{" C ", "TGS", " S ", Character.valueOf('C'), BTWTags.cobblestones, Character.valueOf('T'), Item.silk, Character.valueOf('S'), Item.stick, Character.valueOf('G'), NMItems.primitiveGlue});

        RecipeManager.addShapelessRecipe(new ItemStack(Item.clay), new Object[]{BTWItems.clayPile, BTWItems.clayPile, BTWItems.clayPile, BTWItems.clayPile});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.flint), new Object[]{NMItems.flintChip, NMItems.flintChip, NMItems.flintChip, NMItems.flintChip});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.unshapedWetClayBrick, 1, NMItems.unshapedWetClayBrick.getMaxDamage() - 1), new Object[]{Item.clay, BTWItems.gravelPile, BTWItems.dirtPile, BTWItems.sandPile});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.cistern), new Object[]{"I I", "I I", "III", Character.valueOf('I'), Item.ingotIron});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.stoneAnvil), new Object[]{"SSS", " S ", "SSS", Character.valueOf('S'), BTWTags.cobblestones});
        NMFoodSpoilage.addSnowRefreshRecipes();

        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.lithiumSalt, 2), new Object[]{new ItemStack(NMItems.refinedLithium), new ItemStack(Item.sugar)});
        RecipeManager.addRecipe(new ItemStack(NMItems.lithiumHeatCompound), new Object[]{"NLN", "LCL", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.refinedLithium, Character.valueOf('C'), Block.sand});

        RecipeManager.addRecipe(new ItemStack(NMItems.nickelBinding, 2), new Object[]{"NN", " S", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('S'), Item.silk});
        RecipeManager.addRecipe(new ItemStack(NMItems.oxygenTank), new Object[]{" N ", "NIN", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('I'), Item.ingotIron, Character.valueOf('L'), Item.leather});

        RecipeManager.addRecipe(new ItemStack(NMItems.precisionCrystalGear), new Object[]{" C ", "CNC", " C ", Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('N'), NMItems.nickelMachinePart});

        RecipeManager.addRecipe(new ItemStack(NMItems.seededDiamondMatrix), new Object[]{" C ", "CDC", " C ", Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('D'), NMItems.stabilizedDiamondSlurry});
        RecipeManager.addRecipe(new ItemStack(NMItems.nickelBoundDiamondMatrix), new Object[]{" N ", "NDN", " S ", Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('D'), NMItems.seededDiamondMatrix, Character.valueOf('S'), NMItems.lithiumStabilizer});


        for (Item rawFish : NMItems.getRawFish()) {
            if (rawFish != NMItems.debonedRawFish) {
                RecipeManager.addShapelessRecipe(new ItemStack(NMItems.fishFlesh, 1, 99), new Object[]{new ItemStack(rawFish, 1, Short.MAX_VALUE)});
            }
        }


        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.moistureFertilizer, 4), new Object[]{Item.bucketWater, BTWItems.dirtPile});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.potassiumFertilizer, 4), new Object[]{NMItems.ash, BTWItems.dirtPile});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.acidityFertilizer, 4), new Object[]{Item.fermentedSpiderEye, BTWItems.dirtPile});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.porosityFertilizer, 4), new Object[]{BTWItems.sandPile, BTWItems.gravelPile});


        finishRecipes("Crafting Recipes");

    }

    private static void addSkillLockedRecipes(){
        SkillLockedCrafting.requireSkills(
                RecipeManager.addShapelessRecipe(
                        new ItemStack(NMItems.skillBook),
                        new Object[]{new ItemStack(Item.leather), new ItemStack(Item.dyePowder, 1, 0), new ItemStack(Item.stick), new ItemStack(Item.silk)}),
                NMSkillNodes.DANDELION_NOTES_I,
                NMSkillNodes.DANDELION_NOTES_II,
                NMSkillNodes.FLINT_CHIP_NOTES,
                NMSkillNodes.ROTTEN_FLESH_NOTES,
                NMSkillNodes.SUGAR_CANE_NOTES,
                NMSkillNodes.POPPY_NOTES,
                NMSkillNodes.BIOME_FIELD_NOTES);

        SkillLockedCrafting.requireSkill(
                RecipeManager.addShapelessRecipe(
                        new ItemStack(NMItems.stringCrafting, 1, NMItems.stringCrafting.getMaxDamage() - 1),
                        new Object[]{NMItems.crudeString, NMItems.spiderSilk, NMItems.primitiveGlue}),
                NMSkillNodes.SPIDER_SILK_STRING);

        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(
                        new ItemStack(BTWItems.bowDrill),
                        new Object[]{"ST", "SD", Character.valueOf('S'), Item.stick, Character.valueOf('T'), BTWTags.strings, Character.valueOf('D'), NMItems.drill}),
                NMSkillNodes.BURNING_TORCH_BOW_DRILL);

        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(
                        new ItemStack(BTWBlocks.unlitCampfire),
                        new Object[]{"##", "##", Character.valueOf('#'), NMItems.pileOfSticks}),
                NMSkillNodes.SAWDUST_CAMPFIRE);

        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(
                        new ItemStack(BTWBlocks.idleLooseOven),
                        new Object[]{"##", "##", Character.valueOf('#'), NMItems.ovenPart}),
                NMSkillNodes.PORK_OVEN_PATTERN);

        RecipeManager.removeVanillaRecipe(
                new ItemStack(BTWItems.woodenClub),
                new Object[]{"X", "X", Character.valueOf('X'), Item.stick});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(
                        new ItemStack(BTWItems.woodenClub),
                        new Object[]{"X", "X", Character.valueOf('X'), Item.stick}),
                NMSkillNodes.STICK_CLUB_PATTERNS,
                NMSkillNodes.MOB_CLUB_PATTERNS,
                NMSkillNodes.LOG_TWIGS);

        RecipeManager.removeVanillaRecipe(
                new ItemStack(BTWItems.boneClub),
                new Object[]{"X", "X", Character.valueOf('X'), Item.bone});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(
                        new ItemStack(BTWItems.boneClub),
                        new Object[]{"X", "X", Character.valueOf('X'), Item.bone}),
                NMSkillNodes.STICK_CLUB_PATTERNS,
                NMSkillNodes.MOB_CLUB_PATTERNS,
                NMSkillNodes.BONE_HEMP);

        RecipeManager.removeVanillaRecipe(
                new ItemStack(BTWBlocks.dirtSlab, 4),
                new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.dirt)});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(
                        new ItemStack(BTWBlocks.dirtSlab, 4),
                        new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.dirt)}),
                NMSkillNodes.JUMP_CUT_SLABS);

        RecipeManager.removeVanillaShapelessRecipe(
                new ItemStack(BTWBlocks.sandAndGravelSlab, 1, 0),
                new Object[]{new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile)});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addShapelessRecipe(
                        new ItemStack(BTWBlocks.sandAndGravelSlab, 1, 0),
                        new Object[]{new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile)}),
                NMSkillNodes.JUMP_CUT_SLABS);
        RecipeManager.removeVanillaRecipe(
                new ItemStack(BTWBlocks.sandAndGravelSlab, 4, 0),
                new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.gravel)});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(
                        new ItemStack(BTWBlocks.sandAndGravelSlab, 4, 0),
                        new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.gravel)}),
                NMSkillNodes.JUMP_CUT_SLABS);

        RecipeManager.removeVanillaShapelessRecipe(
                new ItemStack(BTWBlocks.sandAndGravelSlab, 1, 1),
                new Object[]{new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile)});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addShapelessRecipe(
                        new ItemStack(BTWBlocks.sandAndGravelSlab, 1, 1),
                        new Object[]{new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile)}),
                NMSkillNodes.JUMP_CUT_SLABS);
        RecipeManager.removeVanillaRecipe(
                new ItemStack(BTWBlocks.sandAndGravelSlab, 4, 1),
                new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.sand)});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(
                        new ItemStack(BTWBlocks.sandAndGravelSlab, 4, 1),
                        new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.sand)}),
                NMSkillNodes.JUMP_CUT_SLABS);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.handCrank), new Object[]{"  Y", " Y ", "#X#", Character.valueOf('#'), BTWTags.stoneBrickItems, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), Item.stick});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(BTWBlocks.handCrank), new Object[]{" G ", "SGS", "###", Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('S'), Item.stick, Character.valueOf('#'), BTWTags.stoneBrickItems}),
                NMSkillNodes.MECHANICAL_APPRENTICESHIP);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.millstone), new Object[]{"YYY", "YYY", "YXY", Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), BTWTags.stoneBrickItems});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(BTWBlocks.millstone), new Object[]{"SGS", "SSS", "SGS", Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('S'), BTWTags.stoneBrickItems}),
                NMSkillNodes.MECHANICAL_APPRENTICESHIP);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.windMill), new Object[]{" # ", "# #", " # ", Character.valueOf('#'), BTWItems.windMillBlade});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.windMill), new Object[]{" # ", "# #", " # ", Character.valueOf('#'), BTWItems.windMillBlade}),
                NMSkillNodes.MECHANICAL_APPRENTICESHIP, NMSkillNodes.WIND_ENGINEERING);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.saw), new Object[]{"YYY", "XZX", "#X#", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), Item.ingotIron, Character.valueOf('Z'), BTWItems.belt});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.saw), new Object[]{"IGI", "GBG", "SIS", Character.valueOf('I'), Item.ingotIron, Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('B'), BTWItems.belt, Character.valueOf('S'), BTWTags.highEfficiencyWoodSidings}),
                NMSkillNodes.MECHANICAL_APPRENTICESHIP, NMSkillNodes.WIND_ENGINEERING, NMSkillNodes.LEATHER_HANDIN);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.gearBox), new Object[]{"#X#", "XYX", "#X#", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), BTWBlocks.axle});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.gearBox), new Object[]{"SGS", "GAG", "SGS", Character.valueOf('S'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('A'), BTWBlocks.axle}),
                NMSkillNodes.MECHANICAL_APPRENTICESHIP, NMSkillNodes.WIND_ENGINEERING);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.waterWheel), new Object[]{"###", "# #", "###", Character.valueOf('#'), BTWItems.woodenBlade});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.waterWheel), new Object[]{"BPB", "B B", "BNB", Character.valueOf('B'), BTWItems.woodenBlade, Character.valueOf('P'), NMItems.precisionCrystalGear, Character.valueOf('N'), NMItems.nickelMachinePart}),
                NMSkillNodes.WIND_ENGINEERING, NMSkillNodes.PRECISION_MECHANICS, NMSkillNodes.NICKEL_MACHINE_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pocketSundial), new Object[]{" # ", "#X#", " # ", Character.valueOf('#'), Item.goldNugget, Character.valueOf('X'), Item.netherQuartz});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.pocketSundial), new Object[]{"GCG", "GQG", "GPG", Character.valueOf('G'), Item.goldNugget, Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('Q'), Item.netherQuartz, Character.valueOf('P'), NMItems.precisionCrystalGear}),
                NMSkillNodes.GOLD_ASSAYING, NMSkillNodes.PRECISION_MECHANICS, NMSkillNodes.DIAMOND_CRYSTALS);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.redstoneLatch), new Object[]{"ggg", " r ", Character.valueOf('g'), Item.goldNugget, Character.valueOf('r'), Item.redstone});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.redstoneLatch), new Object[]{"GCG", "GRG", "GNG", Character.valueOf('G'), Item.goldNugget, Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('R'), NMItems.refinedRedstone, Character.valueOf('N'), NMItems.nickelPlate}),
                NMSkillNodes.GOLD_ASSAYING, NMSkillNodes.SIGNAL_ENGINEERING, NMSkillNodes.CALIBRATED_CISTERN);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.redstoneClutch), new Object[]{"#X#", "XYX", "#X#", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), BTWItems.redstoneLatch});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.redstoneClutch), new Object[]{"SPS", "GLG", "SNS", Character.valueOf('S'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('P'), NMItems.precisionCrystalGear, Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('L'), BTWItems.redstoneLatch, Character.valueOf('N'), NMItems.nickelMachinePart}),
                NMSkillNodes.SIGNAL_ENGINEERING, NMSkillNodes.PRECISION_MECHANICS, NMSkillNodes.NICKEL_MACHINE_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.redstoneRepeater), new Object[]{"#X#", "III", Character.valueOf('#'), Block.torchRedstoneActive, Character.valueOf('X'), Item.pocketSundial, Character.valueOf('I'), BTWTags.stoneBrickItems});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.redstoneRepeater), new Object[]{"TRT", "PCP", "SNS", Character.valueOf('T'), Block.torchRedstoneActive, Character.valueOf('R'), NMItems.refinedRedstone, Character.valueOf('P'), NMItems.polishedCrystalShard, Character.valueOf('C'), Item.pocketSundial, Character.valueOf('S'), BTWTags.stoneBrickItems, Character.valueOf('N'), NMItems.nickelPlate}),
                NMSkillNodes.SIGNAL_ENGINEERING, NMSkillNodes.GOLD_ASSAYING, NMSkillNodes.CRYSTAL_LENS_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.comparator), new Object[]{" R ", "RER", "SSS", Character.valueOf('E'), BTWItems.redstoneEye, Character.valueOf('S'), BTWTags.stoneBrickItems, Character.valueOf('R'), Block.torchRedstoneActive});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.comparator), new Object[]{"TRT", "LEL", "SNS", Character.valueOf('T'), Block.torchRedstoneActive, Character.valueOf('R'), NMItems.refinedRedstone, Character.valueOf('L'), NMItems.crystalLens, Character.valueOf('E'), BTWItems.redstoneEye, Character.valueOf('S'), BTWTags.stoneBrickItems, Character.valueOf('N'), NMItems.nickelMachinePart}),
                NMSkillNodes.SIGNAL_ENGINEERING, NMSkillNodes.CALIBRATED_CISTERN, NMSkillNodes.PRECISION_MECHANICS);

        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(
                        new ItemStack(NMItems.flintAxeCrafting, 1, NMItems.flintAxeCrafting.getMaxDamage() - 1),
                        new Object[]{Item.flint, Item.flint, Item.stick, Item.silk}),
                NMSkillNodes.FLINT_TOOLMAKING);

        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.nickelHeatComponent), new Object[]{
                " N ", "NLN", " N ", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.lithiumHeatCompound), new Object[]{"NLN", "LCL", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.refinedLithium, Character.valueOf('C'), Block.sand});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.lithiumHeatCompound), new Object[]{"NLN", "LCL", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.refinedLithium, Character.valueOf('C'), Block.sand}),
                NMSkillNodes.LITHIUM_STABILIZER_RECIPE, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.coal), new Object[]{new ItemStack(BTWItems.coalDust), new ItemStack(BTWItems.coalDust)});
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(Item.coal), new Object[]{new ItemStack(BTWItems.coalDust), new ItemStack(BTWItems.coalDust)}),
                NMSkillNodes.COAL_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.ingotIron), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(BTWItems.ironNugget)});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.ingotIron), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(BTWItems.ironNugget)}),
                NMSkillNodes.IRON_BLOOM_RECIPE, NMSkillNodes.IRON_HELMET_PROGRESS, NMSkillNodes.IRON_CHEST_PROGRESS,
                NMSkillNodes.IRON_LEGS_PROGRESS, NMSkillNodes.IRON_BOOTS_PROGRESS);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.bedroll), new Object[]{BTWTags.knitWools, BTWTags.knitWools, BTWTags.strings});
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.bedroll), new Object[]{BTWTags.knitWools, BTWTags.knitWools, BTWTags.strings}),
                NMSkillNodes.BEDROLL_RECIPE);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.chickenFeed), new Object[]{new ItemStack(Item.dyePowder, 1, 15), BTWTags.seeds});
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.chickenFeed), new Object[]{new ItemStack(Item.dyePowder, 1, 15), BTWTags.seeds}),
                NMSkillNodes.CHICKEN_FEED_RECIPE);

        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(NMItems.lithiumSalt, 3), new Object[]{new ItemStack(NMItems.refinedLithium), new ItemStack(Item.reed)}),
                NMSkillNodes.BETTER_LITHIUM_SALT);
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(Item.cake), new Object[]{"AAA", "BEB", "CCC", Character.valueOf('A'), Item.bucketMilk, Character.valueOf('B'), Item.sugar, Character.valueOf('C'), Item.wheat, Character.valueOf('E'), Item.egg}),
                NMSkillNodes.CAKE_RECIPE);
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.straw), new Object[]{new ItemStack(NMItems.plantFiber)}),
                NMSkillNodes.FIBER_TO_STRAW);
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.oxygenMask), new Object[]{"NGN", "L L", Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('G'), Block.glass, Character.valueOf('L'), Item.leather}),
                NMSkillNodes.OXYGEN_MASK_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.oxygenTank), new Object[]{" N ", "NIN", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('I'), Item.ingotIron, Character.valueOf('L'), Item.leather});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.oxygenTank), new Object[]{" N ", "NIN", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('I'), Item.ingotIron, Character.valueOf('L'), Item.leather}),
                NMSkillNodes.OXYGEN_MASK_RECIPE, NMSkillNodes.NICKEL_HEAT_RECIPE);

        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.nickelMachinePart), new Object[]{
                " N ", "NIN", " R ", Character.valueOf('N'), NMItems.nickelIngot, Character.valueOf('I'), Item.ingotIron, Character.valueOf('R'), Item.redstone}),
                NMSkillNodes.NICKEL_MACHINE_RECIPE);
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.crystalLens), new Object[]{" G ", "GCG", " G ", Character.valueOf('G'), Block.glass, Character.valueOf('C'), NMItems.polishedCrystalShard}),
                NMSkillNodes.CRYSTAL_LENS_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.precisionCrystalGear), new Object[]{" C ", "CNC", " C ", Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('N'), NMItems.nickelMachinePart});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.precisionCrystalGear), new Object[]{" C ", "CNC", " C ", Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('N'), NMItems.nickelMachinePart}),
                NMSkillNodes.CRYSTAL_LENS_RECIPE, NMSkillNodes.NICKEL_MACHINE_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.seededDiamondMatrix), new Object[]{" C ", "CDC", " C ", Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('D'), NMItems.stabilizedDiamondSlurry});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.seededDiamondMatrix), new Object[]{" C ", "CDC", " C ", Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('D'), NMItems.stabilizedDiamondSlurry}),
                NMSkillNodes.DIAMOND_CRYSTALS, NMSkillNodes.LITHIUM_STABILIZER_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.nickelBoundDiamondMatrix), new Object[]{" N ", "NDN", " S ", Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('D'), NMItems.seededDiamondMatrix, Character.valueOf('S'), NMItems.lithiumStabilizer});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.nickelBoundDiamondMatrix), new Object[]{" N ", "NDN", " S ", Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('D'), NMItems.seededDiamondMatrix, Character.valueOf('S'), NMItems.lithiumStabilizer}),
                NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMItems.hydraulicLens), new Object[]{NMItems.aquamarine, NMItems.polishedCrystalShard, Block.glass});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMItems.hydraulicLens), new Object[]{NMItems.aquamarine, NMItems.polishedCrystalShard, NMItems.polishedCrystalShard, Block.glass, NMItems.refinedLithium}),
                NMSkillNodes.CRYSTAL_LENS_RECIPE, NMSkillNodes.LITHIUM_STABILIZER_RECIPE);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMItems.fluidGauge), new Object[]{NMItems.aquamarine, NMItems.nickelPlate, NMItems.hydraulicLens});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMItems.fluidGauge), new Object[]{NMItems.aquamarine, NMItems.nickelPlate, NMItems.hydraulicLens, NMItems.lithiumHeatCompound, NMItems.crystalLens}),
                NMSkillNodes.CALIBRATED_CISTERN, NMSkillNodes.NICKEL_MACHINE_RECIPE);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMBlocks.minerDrill), new Object[]{Block.netherrack, NMItems.tungstenIngot, Item.redstone});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.minerDrill), new Object[]{Block.netherrack, NMItems.tungstenIngot, Item.redstone, NMItems.nickelMachinePart, NMItems.lithiumHeatCompound}),
                NMSkillNodes.DENSE_CORE_METALLURGY, NMSkillNodes.NICKEL_MACHINE_RECIPE);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMBlocks.cisternInterface), new Object[]{NMItems.tungstenIngot, NMItems.tungstenIngot, Item.redstone, Block.netherBrick});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.cisternInterface), new Object[]{NMItems.tungstenIngot, NMItems.tungstenIngot, Item.redstone, Block.netherBrick, NMItems.nickelMachinePart, NMItems.crystalLens}),
                NMSkillNodes.CALIBRATED_CISTERN, NMSkillNodes.NICKEL_MACHINE_RECIPE);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMBlocks.cisternStirrer), new Object[]{NMBlocks.cisternInterface, BTWBlocks.gearBox, BTWBlocks.axle, Item.redstone});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.cisternStirrer), new Object[]{NMBlocks.cisternInterface, BTWBlocks.gearBox, BTWBlocks.axle, Item.redstone, NMItems.nickelHeatComponent, NMItems.precisionCrystalGear}),
                NMSkillNodes.CALIBRATED_CISTERN, NMSkillNodes.DIAMOND_PRECISION_GEAR);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMBlocks.cisternDrain), new Object[]{NMBlocks.cisternInterface, NMItems.tungstenIngot, Item.redstone});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.cisternDrain), new Object[]{NMBlocks.cisternInterface, NMItems.tungstenIngot, Item.redstone, NMItems.fluidGauge, NMItems.nickelBinding}),
                NMSkillNodes.CALIBRATED_CISTERN, NMSkillNodes.DENSE_CORE_METALLURGY);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.chunkLoader), new Object[]{"OTO", "TRT", "OTO", Character.valueOf('O'), NMItems.obsidianBrick, Character.valueOf('T'), NMItems.tungstenIngot, Character.valueOf('R'), NMItems.refinedRedstone});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMBlocks.chunkLoader), new Object[]{"OTO", "TRT", "OPO", Character.valueOf('O'), NMItems.obsidianBrick, Character.valueOf('T'), NMItems.tungstenIngot, Character.valueOf('R'), NMItems.refinedRedstone, Character.valueOf('P'), NMItems.precisionCrystalGear}),
                NMSkillNodes.CALIBRATED_CISTERN, NMSkillNodes.DENSE_CORE_METALLURGY);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.soulforgedSteelIngot), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(BTWItems.steelNugget)});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(BTWItems.soulforgedSteelIngot), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(BTWItems.steelNugget)}),
                NMSkillNodes.DENSE_CORE_METALLURGY);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.steelArmorPlate), new Object[]{"#X#", " Y ", Character.valueOf('#'), BTWItems.leatherStrap, Character.valueOf('X'), BTWItems.soulforgedSteelIngot, Character.valueOf('Y'), BTWItems.padding});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.steelArmorPlate), new Object[]{"#X#", "NYL", Character.valueOf('#'), BTWItems.leatherStrap, Character.valueOf('X'), BTWItems.soulforgedSteelIngot, Character.valueOf('Y'), BTWItems.padding, Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.DENSE_CORE_METALLURGY, NMSkillNodes.NICKEL_HEAT_RECIPE, NMSkillNodes.SOULFORGED_ARMORY);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodHelmet), new Object[]{"###", "# #", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodChestplate), new Object[]{"# #", "###", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodLeggings), new Object[]{"###", "# #", "# #", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodBoots), new Object[]{"# #", "# #", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodSword), new Object[]{" # ", "###", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodPickaxe), new Object[]{"###", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodAxe), new Object[]{"#  ", "#X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodShovel), new Object[]{" # ", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodHoe), new Object[]{"#X ", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});

        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodHelmet), new Object[]{"BIB", "NCN", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('I'), NMItems.nickelBinding, Character.valueOf('N'), NMItems.lithiumStabilizer, Character.valueOf('C'), NMItems.crystalLens}),
                NMSkillNodes.BLOOD_ARMORY, NMSkillNodes.CALIBRATED_CISTERN, NMSkillNodes.DIAMOND_TOOLMAKING);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodChestplate), new Object[]{"N N", "BIB", "BLB", Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('I'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.BLOOD_ARMORY, NMSkillNodes.CALIBRATED_CISTERN, NMSkillNodes.DIAMOND_TOOLMAKING);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodLeggings), new Object[]{"BIB", "N N", "L L", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('I'), NMItems.precisionCrystalGear, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('L'), NMItems.lithiumStabilizer}),
                NMSkillNodes.BLOOD_ARMORY, NMSkillNodes.CALIBRATED_CISTERN, NMSkillNodes.DIAMOND_TOOLMAKING);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodBoots), new Object[]{"B B", "NLN", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.BLOOD_ARMORY, NMSkillNodes.CALIBRATED_CISTERN, NMSkillNodes.DIAMOND_TOOLMAKING);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodSword), new Object[]{" B ", "CBC", " NH", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('H'), Item.blazeRod}),
                NMSkillNodes.BLOOD_ARMORY, NMSkillNodes.NICKEL_HEAT_RECIPE, NMSkillNodes.DIAMOND_TOOLMAKING);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodPickaxe), new Object[]{"BBB", "NPH", " LH", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumHeatCompound, Character.valueOf('H'), Item.blazeRod}),
                NMSkillNodes.BLOOD_ARMORY, NMSkillNodes.NICKEL_HEAT_RECIPE, NMSkillNodes.DIAMOND_TOOLMAKING);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodAxe), new Object[]{"BBN", "BPH", " LH", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumStabilizer, Character.valueOf('H'), Item.blazeRod}),
                NMSkillNodes.BLOOD_ARMORY, NMSkillNodes.NICKEL_HEAT_RECIPE, NMSkillNodes.DIAMOND_TOOLMAKING);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodShovel), new Object[]{" B ", "NPH", " LH", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('P'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumStabilizer, Character.valueOf('H'), Item.blazeRod}),
                NMSkillNodes.BLOOD_ARMORY, NMSkillNodes.NICKEL_HEAT_RECIPE, NMSkillNodes.DIAMOND_TOOLMAKING);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodHoe), new Object[]{"BBN", " PH", " LH", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('P'), NMItems.precisionCrystalGear, Character.valueOf('L'), NMItems.lithiumStabilizer, Character.valueOf('H'), Item.blazeRod}),
                NMSkillNodes.BLOOD_ARMORY, NMSkillNodes.NICKEL_HEAT_RECIPE, NMSkillNodes.DIAMOND_TOOLMAKING);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.bloodChest), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(BTWBlocks.chest)});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMBlocks.bloodChest), new Object[]{"OBO", "NCN", "PLP", Character.valueOf('O'), NMItems.bloodOrb, Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('C'), BTWBlocks.chest, Character.valueOf('L'), NMItems.lithiumStabilizer, Character.valueOf('P'), NMItems.precisionCrystalGear}),
                NMSkillNodes.BLOOD_STORAGE, NMSkillNodes.BLOOD_ARMORY, NMSkillNodes.CALIBRATED_CISTERN);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.steelLocker), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.steelBunch), Character.valueOf('X'), new ItemStack(NMBlocks.bloodChest)});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMBlocks.steelLocker), new Object[]{"SDS", "LBL", "SPS", Character.valueOf('S'), NMItems.steelBunch, Character.valueOf('D'), NMItems.deadzoneShard, Character.valueOf('L'), NMItems.lithiumHeatCompound, Character.valueOf('B'), NMBlocks.bloodChest, Character.valueOf('P'), NMItems.precisionCrystalGear}),
                NMSkillNodes.STEEL_LOGISTICS, NMSkillNodes.BLOOD_STORAGE, NMSkillNodes.DEADZONE_FOUNDRY, NMSkillNodes.SOULFORGED_ARMORY);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.blockAsphalt, 8), new Object[]{"XXX", "XYX", "XXX", Character.valueOf('X'), NMBlocks.blockRoad, Character.valueOf('Y'), BTWItems.soulUrn});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMBlocks.blockAsphalt, 4), new Object[]{"RXR", "RUR", "RNR", Character.valueOf('R'), NMBlocks.blockRoad, Character.valueOf('X'), NMItems.lithiumHeatCompound, Character.valueOf('U'), BTWItems.soulUrn, Character.valueOf('N'), NMItems.nickelHeatComponent}),
                NMSkillNodes.ROAD_ENGINEERING, NMSkillNodes.THERMAL_ENGINEERING, NMSkillNodes.PRECISION_MECHANICS);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), Item.ingotIron});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), BTWItems.ironNugget});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), Item.ingotIron}),
                NMSkillNodes.ITEM_FRAMES);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XYX", "###", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), Item.book, Character.valueOf('Y'), Item.enchantedBook});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XYX", "###", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), Item.book, Character.valueOf('Y'), Item.enchantedBook}),
                NMSkillNodes.BOOKSHELF_RECIPE);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.writableBook), new Object[]{Item.paper, Item.paper, Item.paper, BTWTags.rawLeathers, new ItemStack(Item.dyePowder, 1, 0), Item.feather});
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(Item.writableBook), new Object[]{Item.paper, Item.paper, Item.paper, BTWTags.rawLeathers, new ItemStack(Item.dyePowder, 1, 0), Item.feather}),
                NMSkillNodes.BOOK_QUILL_RECIPE);

        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.lithiumStabilizer), new Object[]{" C ", "LCL", " C ", Character.valueOf('L'), NMItems.lithiumSalt, Character.valueOf('C'), Item.clay}),
                NMSkillNodes.LITHIUM_STABILIZER_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.dynamite, 2), new Object[]{"PF", "PN", "PS", Character.valueOf('P'), Item.paper, Character.valueOf('F'), BTWItems.fuse, Character.valueOf('N'), BTWItems.blastingOil, Character.valueOf('S'), BTWTags.sawdusts});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.dynamite), new Object[]{"PFC", "PON", "PSL", Character.valueOf('P'), Item.paper, Character.valueOf('F'), BTWItems.fuse, Character.valueOf('C'), NMItems.polishedCrystalShard, Character.valueOf('O'), BTWItems.blastingOil, Character.valueOf('N'), BTWItems.nitre, Character.valueOf('S'), BTWTags.sawdusts, Character.valueOf('L'), NMItems.lithiumSalt}),
                NMSkillNodes.EXPLOSIVES_ENGINEERING, NMSkillNodes.POWDER_KEG_RECIPE, NMSkillNodes.DIAMOND_CRYSTALS);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.tnt), new Object[]{"GFG", "GBG", "GGG", Character.valueOf('B'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 11), Character.valueOf('G'), Item.gunpowder, Character.valueOf('F'), BTWItems.fuse});
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.tnt), new Object[]{"GFG", "GBG", "NGN", Character.valueOf('B'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 11), Character.valueOf('G'), Item.gunpowder, Character.valueOf('N'), BTWItems.nitre, Character.valueOf('F'), BTWItems.fuse});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Block.tnt), new Object[]{"GFG", "DBD", "NGN", Character.valueOf('B'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 11), Character.valueOf('G'), Item.gunpowder, Character.valueOf('D'), BTWItems.dynamite, Character.valueOf('N'), BTWItems.nitre, Character.valueOf('F'), BTWItems.fuse}),
                NMSkillNodes.EXPLOSIVES_ENGINEERING, NMSkillNodes.POWDER_KEG_RECIPE, NMSkillNodes.DIAMOND_CRYSTALS);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.infernalEnchanter), new Object[]{"CBC", "SES", "SSS", Character.valueOf('S'), BTWItems.soulforgedSteelIngot, Character.valueOf('C'), new ItemStack(BTWItems.candle, 1, 0), Character.valueOf('E'), Block.enchantmentTable, Character.valueOf('B'), Item.bone});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.infernalEnchanter), new Object[]{"CDC", "SES", "NPN", Character.valueOf('C'), NMItems.crystalLens, Character.valueOf('D'), NMItems.refinedDiamondIngot, Character.valueOf('S'), BTWItems.soulforgedSteelIngot, Character.valueOf('E'), Block.enchantmentTable, Character.valueOf('N'), NMItems.deadzoneShard, Character.valueOf('P'), NMItems.precisionCrystalGear}),
                NMSkillNodes.INFERNAL_SCHOLARSHIP, NMSkillNodes.SOULFORGED_ARMORY, NMSkillNodes.DEADZONE_FOUNDRY, NMSkillNodes.ENCHANT_MANUSCRIPTS_10);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.helmetLeather), new Object[]{"###", "# #", Character.valueOf('#'), BTWTags.rawLeathers});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.plateLeather), new Object[]{"# #", "###", "###", Character.valueOf('#'), BTWTags.rawLeathers});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.legsLeather), new Object[]{"###", "# #", "# #", Character.valueOf('#'), BTWTags.rawLeathers});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.bootsLeather), new Object[]{"# #", "# #", Character.valueOf('#'), BTWTags.rawLeathers});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.helmetLeather), new Object[]{"###", "# #", Character.valueOf('#'), BTWTags.rawLeathers}), NMSkillNodes.LEATHER_BREEDING, NMSkillNodes.LEATHER_HANDIN);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.plateLeather), new Object[]{"# #", "###", "###", Character.valueOf('#'), BTWTags.rawLeathers}), NMSkillNodes.LEATHER_BREEDING, NMSkillNodes.LEATHER_HANDIN);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.legsLeather), new Object[]{"###", "# #", "# #", Character.valueOf('#'), BTWTags.rawLeathers}), NMSkillNodes.LEATHER_BREEDING, NMSkillNodes.LEATHER_HANDIN);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.bootsLeather), new Object[]{"# #", "# #", Character.valueOf('#'), BTWTags.rawLeathers}), NMSkillNodes.LEATHER_BREEDING, NMSkillNodes.LEATHER_HANDIN);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.shovelIron), new Object[]{"X", "#", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), Item.ingotIron});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(Item.shovelIron), new Object[]{"X", "#", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), Item.ingotIron}), NMSkillNodes.IRON_SHOVEL_RECIPE);
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.swordIron), new Object[]{"X", "X", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), Item.ingotIron});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(Item.swordIron), new Object[]{"X", "X", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), Item.ingotIron}),
                NMSkillNodes.IRON_SWORD_RECIPE,
                NMSkillNodes.BONE_CLUB_SWORD_PATTERN,
                NMSkillNodes.WOOD_CLUB_SWORD_PATTERN);

        // diamond alloying is handled by the cistern. finished diamond equipment then needs
        // the same precision and thermal materials that made the ingots possible.
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.swordDiamond), new Object[]{"X", "X", "#", Character.valueOf('#'), Item.stick, Character.valueOf('X'), NMItems.refinedDiamondIngot});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeDiamond), new Object[]{"XXX", " # ", " # ", Character.valueOf('#'), Item.stick, Character.valueOf('X'), NMItems.refinedDiamondIngot});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.shovelDiamond), new Object[]{"X", "#", "#", Character.valueOf('#'), Item.stick, Character.valueOf('X'), NMItems.refinedDiamondIngot});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.axeDiamond), new Object[]{"X ", "X#", " #", Character.valueOf('#'), Item.stick, Character.valueOf('X'), NMItems.refinedDiamondIngot});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.diamondChisel), new Object[]{"X", Character.valueOf('X'), NMItems.refinedDiamondIngot});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.diamondShears), new Object[]{"X ", " X", Character.valueOf('X'), NMItems.refinedDiamondIngot});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.helmetDiamond), new Object[]{"###", "# #", "   ", Character.valueOf('#'), new ItemStack(NMItems.refinedDiamondIngot)});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.plateDiamond), new Object[]{"# #", "###", "###", Character.valueOf('#'), new ItemStack(NMItems.refinedDiamondIngot)});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.legsDiamond), new Object[]{"###", "# #", "# #", Character.valueOf('#'), new ItemStack(NMItems.refinedDiamondIngot)});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.bootsDiamond), new Object[]{"X X", "X X", Character.valueOf('X'), NMItems.refinedDiamondIngot});

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.diamondArmorPlate), new Object[]{"#X#", " Y ", Character.valueOf('#'), BTWItems.leatherStrap, Character.valueOf('X'), BTWItems.diamondIngot, Character.valueOf('Y'), BTWItems.padding});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.diamondArmorPlate), new Object[]{
                        "NXN", "PYL",
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('X'), BTWItems.diamondIngot,
                        Character.valueOf('P'), BTWItems.padding,
                        Character.valueOf('Y'), NMItems.crystalLens,
                        Character.valueOf('L'), NMItems.lithiumStabilizer}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeDiamond), new Object[]{"XXX", " # ", " # ", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.pickaxeDiamond), new Object[]{
                        "III", "GLH", " H ",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('G'), NMItems.precisionCrystalGear,
                        Character.valueOf('L'), NMItems.lithiumHeatCompound,
                        Character.valueOf('H'), BTWTags.lowQualityToolHandles}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.diamondChisel), new Object[]{"X", Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.diamondChisel), new Object[]{
                        " C", "NI",
                        Character.valueOf('C'), NMItems.polishedCrystalShard,
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('I'), BTWItems.diamondIngot}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.diamondShears), new Object[]{"X ", " X", Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.diamondShears), new Object[]{
                        "IP", " I",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('P'), NMItems.nickelPlate}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeDiamond), new Object[]{"X#", " #", " #", Character.valueOf('#'), TagInstance.of(BTWTags.lowQualityToolHandles), Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.hoeDiamond), new Object[]{
                        "IL ", "GH ", " H ",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('L'), NMItems.lithiumStabilizer,
                        Character.valueOf('G'), NMItems.nickelBinding,
                        Character.valueOf('H'), BTWTags.lowQualityToolHandles}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.swordDiamond), new Object[]{"X", "X", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.swordDiamond), new Object[]{
                        " I ", "CIC", " H ",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('C'), NMItems.polishedCrystalShard,
                        Character.valueOf('H'), BTWTags.lowQualityToolHandles}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.shovelDiamond), new Object[]{"X", "#", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.shovelDiamond), new Object[]{
                        " I ", "CGH", " H ",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('C'), NMItems.polishedCrystalShard,
                        Character.valueOf('G'), NMItems.nickelBinding,
                        Character.valueOf('H'), BTWTags.lowQualityToolHandles}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.axeDiamond), new Object[]{"X ", "X#", " #", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.axeDiamond), new Object[]{
                        "II ", "IGH", " LH",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('G'), NMItems.nickelBinding,
                        Character.valueOf('L'), NMItems.lithiumStabilizer,
                        Character.valueOf('H'), BTWTags.lowQualityToolHandles}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.helmetDiamond), new Object[]{"XXX", "XYX", Character.valueOf('X'), BTWItems.diamondIngot, Character.valueOf('Y'), BTWItems.diamondArmorPlate});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.helmetDiamond), new Object[]{
                        "IXI", "NYN",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('X'), BTWItems.diamondArmorPlate,
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('Y'), NMItems.crystalLens}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.plateDiamond), new Object[]{"Y Y", "XXX", "XXX", Character.valueOf('X'), BTWItems.diamondIngot, Character.valueOf('Y'), BTWItems.diamondArmorPlate});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.plateDiamond), new Object[]{
                        "N N", "IXI", "ILI",
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('X'), BTWItems.diamondArmorPlate,
                        Character.valueOf('L'), NMItems.lithiumStabilizer}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.legsDiamond), new Object[]{"XXX", "Y Y", "Y Y", Character.valueOf('X'), BTWItems.diamondIngot, Character.valueOf('Y'), BTWItems.diamondArmorPlate});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.legsDiamond), new Object[]{
                        "IXI", "N N", "L L",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('X'), BTWItems.diamondArmorPlate,
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('L'), NMItems.lithiumStabilizer}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.bootsDiamond), new Object[]{"X X", "X X", Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.bootsDiamond), new Object[]{
                        "I I", "NLN",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('L'), NMItems.lithiumStabilizer}),
                NMSkillNodes.DIAMOND_TOOLMAKING, NMSkillNodes.DIAMOND_PRECISION_GEAR, NMSkillNodes.NICKEL_HEAT_RECIPE);

        // manual cistern work produces the components for this tier. none of these gates
        // depends on the mechanical blocks being replaced here.
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.turntable), new Object[]{"###", "ZXZ", "ZYZ", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('X'), Item.pocketSundial, Character.valueOf('Y'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Z'), BTWTags.stoneBrickItems});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.turntable), new Object[]{
                        "SPS", "ZGZ", "LCL",
                        Character.valueOf('S'), BTWTags.woodenSidings,
                        Character.valueOf('P'), NMItems.precisionCrystalGear,
                        Character.valueOf('Z'), BTWTags.stoneBrickItems,
                        Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE),
                        Character.valueOf('L'), NMItems.lithiumStabilizer,
                        Character.valueOf('C'), NMItems.crystalLens}),
                NMSkillNodes.PRECISION_MECHANICS, NMSkillNodes.NICKEL_MACHINE_RECIPE, NMSkillNodes.CRYSTAL_LENS_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.hopper), new Object[]{"# #", "XYX", " Z ", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), BTWTags.woodenPressurePlates, Character.valueOf('Z'), BTWTags.woodenCorners});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.hopper), new Object[]{
                        "S S", "PNP", " C ",
                        Character.valueOf('S'), BTWTags.woodenSidings,
                        Character.valueOf('P'), NMItems.nickelPlate,
                        Character.valueOf('N'), NMItems.nickelMachinePart,
                        Character.valueOf('C'), NMItems.crystalLens}),
                NMSkillNodes.PRECISION_MECHANICS, NMSkillNodes.NICKEL_MACHINE_RECIPE, NMSkillNodes.CRYSTAL_LENS_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.pulley), new Object[]{"#Y#", "XZX", "#Y#", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), Item.ingotIron, Character.valueOf('Z'), BTWItems.redstoneLatch});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.pulley), new Object[]{
                        "SNS", "GRG", "SLS",
                        Character.valueOf('S'), BTWTags.highEfficiencyWoodSidings,
                        Character.valueOf('N'), NMItems.nickelMachinePart,
                        Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE),
                        Character.valueOf('R'), BTWItems.redstoneLatch,
                        Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.PRECISION_MECHANICS, NMSkillNodes.NICKEL_MACHINE_RECIPE, NMSkillNodes.CISTERN_USE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.pistonBase), new Object[]{"#I#", "XYX", "XZX", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), Item.ingotIron, Character.valueOf('X'), BTWTags.stoneBrickItems, Character.valueOf('Y'), BTWItems.soulUrn, Character.valueOf('Z'), BTWItems.redstoneLatch});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Block.pistonBase), new Object[]{
                        "SNS", "XUX", "LPL",
                        Character.valueOf('S'), BTWTags.woodenSidings,
                        Character.valueOf('N'), NMItems.nickelMachinePart,
                        Character.valueOf('X'), BTWTags.stoneBrickItems,
                        Character.valueOf('U'), BTWItems.soulUrn,
                        Character.valueOf('L'), NMItems.lithiumHeatCompound,
                        Character.valueOf('P'), NMItems.precisionCrystalGear}),
                NMSkillNodes.PRECISION_MECHANICS, NMSkillNodes.NICKEL_MACHINE_RECIPE, NMSkillNodes.DIAMOND_PRECISION_GEAR);

        // nickel and lithium are consumed by the machines that create and survive stoked heat.
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.bellows), new Object[]{"###", "XXX", "YZY", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('X'), BTWTags.tannedLeathers, Character.valueOf('Y'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Z'), BTWItems.belt});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.bellows), new Object[]{
                        "NBN", "LHL", "GSG",
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('B'), BTWItems.belt,
                        Character.valueOf('L'), BTWTags.tannedLeathers,
                        Character.valueOf('H'), NMItems.nickelHeatComponent,
                        Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE),
                        Character.valueOf('S'), BTWTags.woodenSidings}),
                NMSkillNodes.THERMAL_ENGINEERING, NMSkillNodes.NICKEL_HEAT_RECIPE, NMSkillNodes.LITHIUM_STABILIZER_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.hibachi), new Object[]{"XXX", "#Z#", "#Y#", Character.valueOf('#'), BTWTags.stoneBrickItems, Character.valueOf('X'), BTWItems.concentratedHellfire, Character.valueOf('Y'), Item.redstone, Character.valueOf('Z'), BTWItems.element});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.hibachi), new Object[]{
                        "HHH", "SES", "NLN",
                        Character.valueOf('H'), BTWItems.concentratedHellfire,
                        Character.valueOf('S'), BTWTags.stoneBrickItems,
                        Character.valueOf('E'), BTWItems.element,
                        Character.valueOf('N'), NMItems.nickelHeatComponent,
                        Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.THERMAL_ENGINEERING, NMSkillNodes.NICKEL_HEAT_RECIPE, NMSkillNodes.LITHIUM_STABILIZER_RECIPE);

        // the soulforge now consumes the products of every late material branch. its skill
        // parents require a Wither kill, refined-diamond crucible work, and Tier 3 mining.
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWBlocks.soulforge), new Object[]{new ItemStack(Item.netherStar), new ItemStack(BTWItems.soulFlux), new ItemStack(BTWBlocks.dormandSoulforge)});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.soulforge), new Object[]{
                        Item.netherStar,
                        BTWItems.soulFlux,
                        BTWBlocks.dormandSoulforge,
                        NMItems.refinedDiamondIngot,
                        NMItems.nickelHeatComponent,
                        NMItems.lithiumHeatCompound,
                        NMItems.precisionCrystalGear,
                        NMItems.deadzoneShard,
                        NMItems.denseNetherrackCore}),
                NMSkillNodes.SOULFORGE_ENGINEERING, NMSkillNodes.DEADZONE_FOUNDRY,
                NMSkillNodes.WITHER_KILL_LOOT, NMSkillNodes.DENSE_CORE_METALLURGY);

        finishRecipes("Skill Locked Recipes");

    }

    private static void addHammerRecipes(){
        HammerRecipeList.addRecipes();
    }
    private static void addMultiplayerRecipes(){
        if(MinecraftServer.getIsServer()){


            finishRecipes("Multiplayer Exclusive Recipes");
        }
    }

    private static void addPistonPackingRecipes() {

        finishRecipes("Piston Packing Recipes");

    }


}
