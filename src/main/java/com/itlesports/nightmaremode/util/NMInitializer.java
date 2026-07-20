package com.itlesports.nightmaremode.util;

import api.achievement.AchievementTab;
import api.entity.mob.villager.TradeItem;
import api.entity.mob.villager.TradeProvider;
import btw.crafting.recipe.RecipeManager;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.BTWTags;
import com.itlesports.nightmaremode.achievements.AchievementExt;
import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.crafting.manager.CisternRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.MiscRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.WashingRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.HammerRecipeList;
import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.NMPostItems;
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
        addHammerWorldInteractionRecipes();
        addCampfireRecipes();
        addCrucibleRecipes();
        addCauldronRecipes();
        addCisternRecipes();
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


    private static void addCrucibleRecipes(){


        finishRecipes("Crucible Recipes");

    }
    private static void addCauldronRecipes(){


        finishRecipes("Cauldron Recipes");

    }

    private static void addCisternRecipes(){
        CisternRecipeManager manager = CisternRecipeManager.instance;

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

        finishRecipes("Cistern Recipes");
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



        finishRecipes("Miscellaneous Recipes");
    }

    private static void addOvenRecipes(){
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

        finishRecipes("Oven Recipes");

    }
    private static void addSoulforgeRecipes(){
        // packed blocks

        finishRecipes("Soulforge Recipes");

    }
    private static void addCampfireRecipes(){
        RecipeManager.addCampfireRecipe(NMItems.cupOfSap.itemID, new ItemStack(NMItems.thickenedSap));
        RecipeManager.addCampfireRecipe(NMItems.debonedRawFish.itemID, new ItemStack(Item.fishCooked));

        finishRecipes("Campfire Recipes");

    }

    private static void addMillstoneRecipes(){


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
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.clay), new Object[]{BTWItems.clayPile, BTWItems.clayPile});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.unfiredCrudeBrick), new Object[]{Item.clay});

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeStone), new Object[]{"XXX", "S# ", " # ", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWTags.looseRocks, Character.valueOf('S'), BTWTags.strings});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeStone), new Object[]{"XXX", " # ", " # ", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWTags.looseRocks});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.axeStone), new Object[]{BTWTags.lowQualityToolHandles, BTWTags.looseRocks, BTWTags.looseRocks, BTWTags.strings});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.axeStone), new Object[]{BTWTags.lowQualityToolHandles, BTWTags.looseRocks, BTWTags.looseRocks});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.shovelStone), new Object[]{BTWTags.lowQualityToolHandles, BTWTags.looseRocks, BTWTags.strings});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.shovelStone), new Object[]{BTWTags.lowQualityToolHandles, BTWTags.looseRocks});

        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.scrapedBark), new Object[]{BTWTags.barks, new ItemStack(BTWItems.sharpStone, 1, Short.MAX_VALUE)});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.crudeStringCrafting, 1, NMItems.crudeStringCrafting.getMaxDamage() - 1), new Object[]{NMItems.driedPlantFiber, BTWTags.flowers});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.primitiveGlue), new Object[]{NMItems.thickenedSap, BTWItems.coalDust});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.stringCrafting, 1, NMItems.stringCrafting.getMaxDamage() - 1), new Object[]{NMItems.crudeString, NMItems.spiderSilk, NMItems.primitiveGlue});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.woodCupCrafting, 1, NMItems.woodCupCrafting.getMaxDamage() - 1), new Object[]{new ItemStack(NMItems.woodClump, 1, Short.MAX_VALUE), new ItemStack(BTWItems.pointyStick, 1, Short.MAX_VALUE)});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.reedPeeling, 1, NMItems.reedPeeling.getMaxDamage() - 1), new Object[]{Item.reed});
        RecipeManager.addRecipe(new ItemStack(Item.paper), new Object[]{"###", Character.valueOf('#'), NMItems.plantSheet});
        RecipeManager.addRecipe(new ItemStack(BTWItems.wickerWeaving, 1, 299), new Object[]{"###", "###", "###", Character.valueOf('#'), NMItems.washedSugarCane});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.pileOfSticks), new Object[]{Item.stick, Item.stick, Item.stick, Item.stick});
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.unlitCampfire), new Object[]{"##", "##", Character.valueOf('#'), NMItems.pileOfSticks});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.drill), new Object[]{new ItemStack(BTWItems.pointyStick, 1, Short.MAX_VALUE), Item.stick, NMItems.primitiveGlue, BTWItems.sawDust});
        RecipeManager.addRecipe(new ItemStack(BTWItems.bowDrill), new Object[]{"ST", "SD", Character.valueOf('S'), Item.stick, Character.valueOf('T'), BTWTags.strings, Character.valueOf('D'), NMItems.drill});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.shovelWood), new Object[]{BTWTags.logs, Item.stick, NMItems.primitiveGlue});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.woodHammer), new Object[]{BTWTags.logs, BTWTags.logs, Item.stick, NMItems.primitiveGlue});

        RecipeManager.addRecipe(new ItemStack(Item.pickaxeStone), new Object[]{"CCC", "TSG", " S ", Character.valueOf('C'), BTWTags.cobblestones, Character.valueOf('T'), Item.silk, Character.valueOf('S'), Item.stick, Character.valueOf('G'), NMItems.primitiveGlue});
        RecipeManager.addRecipe(new ItemStack(Item.axeStone), new Object[]{"CCG", "CTS", " S ", Character.valueOf('C'), BTWTags.cobblestones, Character.valueOf('T'), Item.silk, Character.valueOf('S'), Item.stick, Character.valueOf('G'), NMItems.primitiveGlue});
        RecipeManager.addRecipe(new ItemStack(Item.shovelStone), new Object[]{" C ", "TGS", " S ", Character.valueOf('C'), BTWTags.cobblestones, Character.valueOf('T'), Item.silk, Character.valueOf('S'), Item.stick, Character.valueOf('G'), NMItems.primitiveGlue});

        RecipeManager.addShapelessRecipe(new ItemStack(Item.clay), new Object[]{BTWItems.clayPile, BTWItems.clayPile, BTWItems.clayPile, BTWItems.clayPile});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.unshapedWetClayBrick, 1, NMItems.unshapedWetClayBrick.getMaxDamage() - 1), new Object[]{Item.clay, BTWItems.gravelPile, BTWItems.dirtPile, BTWItems.sandPile});
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.idleLooseOven), new Object[]{"##", "##", Character.valueOf('#'), NMItems.ovenPart});
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


        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.skillBook), new Object[]{new ItemStack(Item.leather), new ItemStack(Item.dyePowder, 1, 0),new ItemStack(Item.stick),new ItemStack(Item.silk)});


        finishRecipes("Crafting Recipes");

    }

    private static void addSkillLockedRecipes(){
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(
                        new ItemStack(NMItems.flintAxeCrafting, 1, NMItems.flintAxeCrafting.getMaxDamage() - 1),
                        new Object[]{Item.flint, Item.flint, Item.stick, Item.silk}),
                NMSkillNodes.FLINT_TOOLMAKING);

        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.nickelHeatComponent), new Object[]{
                " N ", "NLN", " N ", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.NICKEL_HEAT_RECIPE);

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

        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.nickelMachinePart), new Object[]{
                " N ", "NIN", " R ", Character.valueOf('N'), NMItems.nickelIngot, Character.valueOf('I'), Item.ingotIron, Character.valueOf('R'), Item.redstone}),
                NMSkillNodes.NICKEL_MACHINE_RECIPE);
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.crystalLens), new Object[]{" G ", "GCG", " G ", Character.valueOf('G'), Block.glass, Character.valueOf('C'), NMItems.polishedCrystalShard}),
                NMSkillNodes.CRYSTAL_LENS_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), Item.ingotIron});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), Item.ingotIron}),
                NMSkillNodes.CHEST_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XYX", "###", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), Item.book, Character.valueOf('Y'), Item.enchantedBook});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XYX", "###", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), Item.book, Character.valueOf('Y'), Item.enchantedBook}),
                NMSkillNodes.BOOKSHELF_RECIPE);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.writableBook), new Object[]{Item.paper, Item.paper, Item.paper, BTWTags.rawLeathers, new ItemStack(Item.dyePowder, 1, 0), Item.feather});
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(Item.writableBook), new Object[]{Item.paper, Item.paper, Item.paper, BTWTags.rawLeathers, new ItemStack(Item.dyePowder, 1, 0), Item.feather}),
                NMSkillNodes.BOOK_QUILL_RECIPE);

        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.lithiumStabilizer), new Object[]{" C ", "LCL", " C ", Character.valueOf('L'), NMItems.lithiumSalt, Character.valueOf('C'), Item.clay}),
                NMSkillNodes.LITHIUM_STABILIZER_RECIPE);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.tnt), new Object[]{"GFG", "GBG", "GGG", Character.valueOf('B'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 11), Character.valueOf('G'), Item.gunpowder, Character.valueOf('F'), BTWItems.fuse});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(Block.tnt), new Object[]{"GFG", "GBG", "GGG", Character.valueOf('B'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 11), Character.valueOf('G'), Item.gunpowder, Character.valueOf('F'), BTWItems.fuse}),
                NMSkillNodes.POWDER_KEG_RECIPE);

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
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(Item.swordIron), new Object[]{"X", "X", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), Item.ingotIron}), NMSkillNodes.IRON_SWORD_RECIPE);

        finishRecipes("Skill Locked Recipes");

    }

    private static void addHammerWorldInteractionRecipes(){
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
