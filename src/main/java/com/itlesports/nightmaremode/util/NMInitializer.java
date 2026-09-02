package com.itlesports.nightmaremode.util;

import api.achievement.AchievementTab;
import api.entity.mob.villager.TradeItem;
import api.entity.mob.villager.TradeProvider;
import api.util.color.Color;
import btw.crafting.manager.*;
import btw.crafting.recipe.types.SawRecipe;
import btw.crafting.recipe.RecipeManager;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.item.BTWTags;
import btw.item.blockitems.WoodMouldingDecorativeStubBlockItem;
import btw.item.blockitems.WoodSidingDecorativeStubBlockItem;
import com.itlesports.nightmaremode.achievements.AchievementExt;
import com.itlesports.nightmaremode.block.tileEntities.CisternTileEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.crafting.manager.BrewingStandRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.CisternRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.EnderAssemblerRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.MiscRecipeManager;
import com.itlesports.nightmaremode.crafting.manager.WashingRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.HammerRecipeList;
import com.itlesports.nightmaremode.crafting.recipe.types.CisternRecipe;
import com.itlesports.nightmaremode.crafting.recipe.types.QuestToolRepairRecipe;
import com.itlesports.nightmaremode.crafting.recipe.types.FishingRodUpgradeRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.NMPostItems;
import com.itlesports.nightmaremode.item.NMTags;
import com.itlesports.nightmaremode.entity.EntityTier1NetherVillager;
import com.itlesports.nightmaremode.entity.EntityTier2NetherVillager;
import com.itlesports.nightmaremode.entity.EntityTier3NetherVillager;
import com.itlesports.nightmaremode.entity.EntityFishermanVillager;
import api.item.tag.TagInstance;
import api.item.tag.TagOrStack;
import api.item.tag.Tag;
import com.itlesports.nightmaremode.mixin.biomegen.BiomeGenBaseAccessor;
import com.itlesports.nightmaremode.skill.NMSkillNodes;
import com.itlesports.nightmaremode.skill.SkillNode;
import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import com.itlesports.nightmaremode.skill.SkillRecipeGates;
import com.itlesports.nightmaremode.tradetweaks.TradeTweaks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.function.Predicate;

public abstract class NMInitializer implements AchievementExt {
    private static IRecipe automationEssenceRecipe;
    private static IRecipe agrarianEssenceRecipe;
    private static IRecipe infernalEssenceRecipe;
    private static IRecipe artisanEssenceRecipe;
    private static IRecipe ultimateEyeOfEnderRecipe;

    private static void finishRecipes(String type){System.out.println("Finished initializing: [" + type + "]");}

    public static void initIFHYRecipes(){
        validateUltimateItemRegistrations();
        addCraftingRecipes();
        validateUltimateCraftingRecipeRegistrations();
        addWashingRecipes();
        addMiscRecipes();
        addHammerRecipes();
        normalizeWoodSawOutputs();
        addCampfireRecipes();
        addCrucibleRecipes();
        addCauldronRecipes();
        addCisternRecipes();
        addBrewingStandRecipes();
        addMillstoneRecipes();
        addTurntableRecipes();
        addEnderAssemblerRecipes();
        addOvenRecipes();
        addSoulforgeRecipes();
        addPistonPackingRecipes();
//        addBloodSawRecipes();
        addMultiplayerRecipes();
        addSkillLockedRecipes();
        finishRecipes("All Recipes");
    }

    private static void validateUltimateItemRegistrations() {
        validateItemRegistration("Librarian's Ender Treatise", NMItems.librarianEnderTreatise);
        validateItemRegistration("Automation Essence", NMItems.automationEssence);
        validateItemRegistration("Agrarian Essence", NMItems.husbandryEssence);
        validateItemRegistration("Infernal Essence", NMItems.infernalEssence);
        validateItemRegistration("Artisan Essence", NMItems.artisanEssence);
        validateItemRegistration("Fishing Essence", NMItems.fishingEssence);
        validateItemRegistration("Iron Fishing Rod", NMItems.ironFishingPole);
        validateItemRegistration("Baited Iron Fishing Rod", NMItems.ironFishingPoleBaited);
        validateItemRegistration("Diamond Fishing Rod", NMItems.diamondFishingPole);
        validateItemRegistration("Baited Diamond Fishing Rod", NMItems.diamondFishingPoleBaited);
        validateItemRegistration("Steel Fishing Rod", NMItems.steelFishingPole);
        validateItemRegistration("Baited Steel Fishing Rod", NMItems.steelFishingPoleBaited);
    }

    private static void validateItemRegistration(String name, Item expected) {
        Item registered = Item.itemsList[expected.itemID];
        if (registered != expected) {
            String occupant = registered == null
                    ? "null"
                    : registered.getClass().getName() + " (" + registered.getUnlocalizedName() + ")";
            throw new IllegalStateException(
                    name + " lost item registry slot " + expected.itemID + " to " + occupant);
        }
    }

    private static void validateUltimateCraftingRecipeRegistrations() {
        validateShapelessRecipeRegistration("Automation Essence", automationEssenceRecipe, NMItems.automationEssence);
        validateShapelessRecipeRegistration("Agrarian Essence", agrarianEssenceRecipe, NMItems.husbandryEssence);
        validateShapelessRecipeRegistration("Infernal Essence", infernalEssenceRecipe, NMItems.infernalEssence);

        if (artisanEssenceRecipe == null
                || !CraftingManager.getInstance().getRecipeList().contains(artisanEssenceRecipe)
                || artisanEssenceRecipe.getRecipeOutput().getItem() != NMItems.artisanEssence) {
            throw new IllegalStateException("Artisan Essence recipe was not registered with the expected output");
        }
    }

    private static void validateShapelessRecipeRegistration(String name, IRecipe recipe, Item expectedOutput) {
        if (!(recipe instanceof ShapelessRecipes)
                || !CraftingManager.getInstance().getRecipeList().contains(recipe)
                || recipe.getRecipeOutput().getItem() != expectedOutput) {
            throw new IllegalStateException(name + " recipe was not registered with the expected output");
        }

        ShapelessRecipes shapeless = (ShapelessRecipes) recipe;
        InventoryCrafting inventory = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int slot = 0; slot < shapeless.getRecipeItems().size(); ++slot) {
            Object ingredient = shapeless.getRecipeItems().get(slot);
            if (!(ingredient instanceof ItemStack)) {
                throw new IllegalStateException(name + " validation does not support ingredient " + ingredient);
            }
            inventory.setInventorySlotContents(slot, ((ItemStack) ingredient).copy());
        }

        if (!shapeless.matches(inventory, null)) {
            throw new IllegalStateException(name + " recipe was registered but cannot match its own ingredients");
        }
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
        addFishermanTrades();
        addNightmareVillagerTrades();
        addNetherPostVillagerTrades();
        finishRecipes("All Trades");

    }

    public static void editExistingTrades(){
        // Farmer
        tweakInput(34, 46, "btw:buy_loose_dirt");
        tweakInput(28, 40, "btw:buy_logs", "btw:buy_logs_variant_oak");
        tweakInput(30, 43, "btw:buy_logs_variant_spruce", "btw:buy_logs_variant_birch");
        tweakInput(32, 45, "btw:buy_logs_variant_jungle", "btw:buy_brown_wool");
        tweakInput(36, 50, "btw:buy_bone_meal", "btw:buy_sugar");
        tweakInput(22, 34, "btw:buy_cocoa_beans");
        tweakInput(10, 16, "btw:buy_brown_mushrooms", "btw:buy_eggs");
        tweakInput(38, 54, "btw:buy_hemp_seeds");
        tweakInput(24, 38, "btw:buy_glass_panes");
        tweakInput(4, 6, "btw:buy_water_wheel");
        tweakInput(14, 22, "btw:sell_apple", "btw:sell_sugar_cane_roots", "btw:sell_bread", "btw:sell_egg_foods", "btw:sell_desserts", "btw:sell_mycelium");
        tweakInput(10, 16, "btw:buy_chocolate");
        tweakInput(10, 16, "btw:buy_melons");
        tweakInput(9, 15, "btw:buy_pumpkins", "btw:buy_stump_remover");
        tweakInput(42, 56, "btw:buy_soap", "btw:buy_light_block", "btw:buy_stake_and_string");
        tweakInput(18, 28, "btw:buy_planters");
        tweakInput(40, 58, "btw:sell_looting_scroll");

        // Librarian
        tweakInput(34, 48, "btw:buy_paper", "btw:buy_ink");
        tweakInput(30, 44, "btw:buy_feathers");
        tweakInput(36, 52, "btw:buy_redstone", "btw:buy_redstone_latch");
        tweakInput(12, 20, "btw:buy_piston", "btw:buy_turntable");
        tweakInput(16, 25, "btw:sell_advanced_redstone", "btw:sell_bookshelf");
        tweakInput(10, 16, "btw:buy_nether_wart");
        tweakInput(38, 54, "btw:buy_glowstone");
        tweakInput(40, 56, "btw:buy_nitre", "btw:buy_witch_warts");
        tweakInput(10, 16, "btw:buy_spider_eyes");
        tweakInput(28, 42, "btw:buy_mysterious_glands", "btw:buy_fermented_spider_eyes");
        tweakInput(24, 36, "btw:buy_ghast_tears", "btw:buy_magma_cream", "btw:buy_blaze_powder");
        tweakInput(32, 46, "btw:buy_brimstone", "btw:buy_blood_wood_saplings");
        tweakInput(6, 10, "btw:buy_nether_groth_spores");
        tweakInput(42, 58, "btw:sell_power_scroll");

        // Priest
        tweakInput(36, 50, "btw:buy_hemp", "btw:buy_cactus", "btw:buy_paintings");
        tweakInput(10, 16, "btw:buy_red_mushrooms");
        tweakSecondary(18, 27,
                "btw:enchant_tools_variant_iron_sword", "btw:enchant_tools_variant_iron_axe", "btw:enchant_tools_variant_iron_pickaxe",
                "btw:enchant_tools_variant_diamond_sword", "btw:enchant_tools_variant_diamond_axe", "btw:enchant_tools_variant_diamond_pickaxe",
                "btw:enchant_armor_variant_iron_helmet", "btw:enchant_armor_variant_iron_chestplate", "btw:enchant_armor_variant_iron_leggings", "btw:enchant_armor_variant_iron_boots",
                "btw:enchant_armor_variant_diamond_helmet", "btw:enchant_armor_variant_diamond_chestplate", "btw:enchant_armor_variant_diamond_leggings", "btw:enchant_armor_variant_diamond_boots",
                "btw:convert_infused_skull_level_up", "btw:convert_infused_skull");
        tweakInput(12, 19, "btw:buy_vessel_of_the_dragon");
        tweakInput(28, 42, "btw:buy_mob_heads", "btw:buy_mob_heads_variant_skeleton", "btw:buy_mob_heads_variant_zombie", "btw:buy_mob_heads_variant_creeper");
        tweakInput(34, 48, "btw:buy_bone_block", "btw:buy_rotten_flesh_block");
        tweakInput(30, 44, "btw:buy_candles", "btw:buy_candles_variant_black", "btw:buy_candles_variant_white", "btw:buy_candles_variant_red", "btw:buy_candles_variant_yellow", "btw:buy_candles_variant_blue", "btw:buy_candles_variant_green");
        tweakInput(22, 34, "btw:buy_soul_urn");
        tweakInput(38, 52, "btw:buy_canvas");
        tweakSecondary(44, 60, "btw:sell_fortune_scroll");

        // Blacksmith
        tweakInput(38, 52, "btw:buy_coal", "btw:buy_birch_logs", "btw:buy_iron_nuggets");
        tweakInput(10, 17, "btw:buy_hibachi", "btw:buy_iron_ingot", "btw:buy_crucible");
        tweakInput(34, 48, "btw:buy_gold_nuggets", "btw:buy_charcoal");
        tweakInput(16, 24, "btw:sell_iron_equipment", "btw:sell_chain_armor", "btw:sell_diamond_equipment", "btw:sell_steel_tools");
        tweakInput(30, 42, "btw:buy_nethercoal", "btw:buy_diamonds");
        tweakInput(10, 16, "btw:buy_creeper_oysters");
        tweakInput(24, 36, "btw:buy_soul_urns_blacksmith");
        tweakInput(36, 50, "btw:buy_padding", "btw:buy_straps", "btw:buy_hafts");
        tweakInput(28, 40, "btw:buy_mining_charges", "btw:buy_steel_ingots", "btw:buy_soul_flux");
        tweakInput(46, 62, "btw:sell_unbreaking_scroll");

        // Butcher
        tweakInput(40, 56, "btw:buy_arrows", "btw:buy_flour", "btw:buy_dung", "btw:buy_spruce_bark", "btw:buy_leather");
        tweakInput(6, 10, "btw:buy_fishing_rod", "btw:buy_saddle", "btw:buy_composite_bow", "btw:buy_battleaxe");
        tweakInput(18, 28, "btw:sell_meat", "btw:sell_mid_tier_foods", "btw:sell_dinners", "btw:sell_hearty_stew", "btw:sell_tanned_leather_armor");
        tweakInput(10, 16, "btw:buy_potatoes", "btw:buy_carrots", "btw:buy_wolf_chops", "btw:buy_liver", "btw:buy_mystery_meat");
        tweakInput(42, 58, "btw:sell_tanned_leather", "btw:buy_breeding_harness", "btw:buy_dirty_chopping_block", "btw:buy_companion_cube");
        tweakInput(30, 44, "btw:buy_screw", "btw:buy_dynamite", "btw:buy_broadhead_arrows", "btw:buy_lightning_rod_and_soap");
        tweakInput(20, 31, "btw:convert_runed_skull");
        tweakInput(48, 63, "btw:sell_sharpness_scroll");

        // Restore normal emerald payouts where the previous tweaks accidentally
        // turned a one-to-four-emerald reward into a full stack.
        TradeTweaks.setOutputCount("btw:buy_brown_wool", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_iron_hoe", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_shears", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_flint_and_steel", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_brewing_stand", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_enchanting_table", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_infernal_enchanter", 4, 4);
        TradeTweaks.setOutputCount("btw:buy_oven", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_anvil_level_up", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_hibachi", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_iron_ingot", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_bellows", 2, 2);
        TradeTweaks.setOutputCount("btw:buy_diamonds", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_crucible", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_steel_ingots", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_shears_butcher", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_bow", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_cauldron", 1, 1);
        TradeTweaks.setOutputCount("btw:buy_saw", 2, 2);
        TradeTweaks.setOutputCount("btw:buy_screw", 1, 1);

        finishRecipes("Trade Tweaks");

    }

    public static void miscInit(){
        NMFoodSpoilage.init();

        finishRecipes("Miscellaneous");

        finishRecipes("Config");

    }
    // trades begin here

    private static void tweakInput(int min, int max, String... tradeNames) {
        for (String tradeName : tradeNames) {
            TradeTweaks.setInputCount(tradeName, min, max);
        }
    }

    private static void tweakSecondary(int min, int max, String... tradeNames) {
        for (String tradeName : tradeNames) {
            TradeTweaks.setSecondaryInputCount(tradeName, min, max);
        }
    }

    private static void buy(String name, int profession, int level, int id1, int meta, int count1, int count2, float w, boolean levelUp, int cost1, int cost2){
        if (name.startsWith("ifhy:")) {
            validateTradeStackLimit(name, id1, count2);
        }
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
        if (name.startsWith("ifhy:")) {
            validateTradeStackLimit(name, id1, c2);
            if (maxCost > Item.emerald.getItemStackLimit()) {
                throw new IllegalArgumentException("Trade " + name + " requests too many emeralds: " + maxCost);
            }
        }
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
        if (name.startsWith("ifhy:")) {
            validateTradeStackLimit(name, firstInput);
            if (secondInput != null && secondInput != TradeItem.EMPTY) {
                validateTradeStackLimit(name, secondInput);
            }
            validateTradeStackLimit(name, output);
        }
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
        removeOldFarmerTrades();

        buy("btw:buy_brown_mushrooms", 0, 2, BTWItems.brownMushroom.itemID, 0, 22, 34);
        buy("ifhy:farmer_millstone_level_up", 0, 2, BTWBlocks.millstone.blockID, 0, 10, 16, 1.0F, true, 0, 0);
        TradeProvider.getBuilder().name("btw:sell_looting_scroll").profession(0).level(5)
                .arcaneScroll().scrollEnchant(Enchantment.looting).secondaryEmeraldCost(42, 58).mandatory().addToTradeList();

        // Plant products and their slow IFHY processing chain.
        buy("ifhy:farmer_hemp", 0, 1, BTWItems.hemp.itemID, 0, 34, 48);
        buy("ifhy:farmer_hemp_fibers", 0, 1, BTWItems.hempFibers.itemID, 0, 38, 52);
        buy("ifhy:farmer_plant_fiber", 0, 1, NMItems.plantFiber.itemID, 0, 42, 58);
        buy("ifhy:farmer_dried_plant_fiber", 0, 1, NMItems.driedPlantFiber.itemID, 0, 44, 60);
        buy("ifhy:farmer_retted_hemp", 0, 2, NMItems.rettedHemp.itemID, 0, 30, 43);
        buy("ifhy:farmer_washed_hemp", 0, 2, NMItems.washedHemp.itemID, 0, 32, 46);
        buy("ifhy:farmer_dried_hemp", 0, 2, NMItems.driedHemp.itemID, 0, 36, 50);
        buy("ifhy:farmer_straw", 0, 2, BTWItems.straw.itemID, 0, 40, 56);
        buy("ifhy:farmer_thatch", 0, 3, BTWBlocks.thatch.blockID, 0, 26, 38);
        buy("ifhy:farmer_chicken_feed", 0, 3, BTWItems.chickenFeed.itemID, 0, 34, 47);

        // Husbandry products, including the leather wet-processing chain.
        buy("ifhy:farmer_raw_pork", 0, 2, Item.porkRaw.itemID, 0, 10, 16);
        buy("ifhy:farmer_raw_beef", 0, 2, Item.beefRaw.itemID, 0, 9, 15);
        buy("ifhy:farmer_raw_chicken", 0, 2, Item.chickenRaw.itemID, 0, 12, 16);
        buy("ifhy:farmer_feathers", 0, 2, Item.feather.itemID, 0, 39, 54);
        buy("ifhy:farmer_dung", 0, 3, BTWItems.dung.itemID, 0, 46, 61);
        buy("ifhy:farmer_scoured_leather", 0, 3, BTWItems.scouredLeather.itemID, 0, 30, 44);
        buy("ifhy:farmer_washed_scoured_leather", 0, 3, NMItems.washedScouredLeather.itemID, 0, 32, 45);
        buy("ifhy:farmer_worked_scoured_leather", 0, 3, NMItems.workedScouredLeather.itemID, 0, 34, 48);
        buy("ifhy:farmer_tanned_leather", 0, 4, BTWItems.tannedLeather.itemID, 0, 28, 41);
        buy("ifhy:farmer_leather_straps", 0, 4, BTWItems.leatherStrap.itemID, 0, 40, 55);

        // Soil management, farm blocks, and every terrain-extractor variant.
        buy("ifhy:farmer_moisture_fertilizer", 0, 4, NMItems.moistureFertilizer.itemID, 0, 36, 50);
        buy("ifhy:farmer_potassium_fertilizer", 0, 4, NMItems.potassiumFertilizer.itemID, 0, 38, 53);
        buy("ifhy:farmer_acidity_fertilizer", 0, 4, NMItems.acidityFertilizer.itemID, 0, 34, 49);
        buy("ifhy:farmer_porosity_fertilizer", 0, 4, NMItems.porosityFertilizer.itemID, 0, 40, 56);
        buy("ifhy:farmer_fertile_netherrack", 0, 4, NMBlocks.fertileNetherrack.blockID, 0, 24, 35);
        buy("ifhy:farmer_terrain_extractor_potassium", 0, 5, NMBlocks.terrainExtractor.blockID, 0, 18, 28);
        buy("ifhy:farmer_terrain_extractor_nitrogen", 0, 5, NMBlocks.terrainExtractor.blockID, 1, 20, 31);
        buy("ifhy:farmer_terrain_extractor_moisture", 0, 5, NMBlocks.terrainExtractor.blockID, 2, 22, 34);
        buy("ifhy:farmer_terrain_extractor_porosity", 0, 5, NMBlocks.terrainExtractor.blockID, 3, 19, 29);
        buy("ifhy:farmer_terrain_extractor_acidity", 0, 5, NMBlocks.terrainExtractor.blockID, 4, 21, 32);

        // Barters give a farmer a few useful recovery paths without implying that
        // the input is physically transformed into the output.
        convert("ifhy:farmer_soil_to_hemp_seeds", 0, 3,
                TradeItem.fromID(NMItems.soilSample.itemID),
                TradeItem.fromID(Item.emerald.itemID, 3, 5),
                TradeItem.fromID(BTWItems.hempSeeds.itemID, 6, 12));
        convert("ifhy:farmer_soil_to_fertilizer", 0, 4,
                TradeItem.fromID(NMItems.soilSample.itemID),
                TradeItem.fromID(NMItems.driedPlantFiber.itemID, 34, 48),
                TradeItem.fromID(NMItems.moistureFertilizer.itemID, 4, 8));
        convert("ifhy:farmer_thatch_to_wheat", 0, 4,
                TradeItem.fromID(BTWBlocks.thatch.blockID, 16, 24),
                TradeItem.fromID(Item.emerald.itemID, 4, 7),
                TradeItem.fromID(Item.wheat.itemID, 10, 18));

        finishRecipes("Farmer Trades");

    }

    private static void validateTradeStackLimit(String tradeName, TradeItem item) {
        validateTradeStackLimit(tradeName, item.id(), item.maxCount());
    }

    private static void validateTradeStackLimit(String tradeName, int itemId, int maximumCount) {
        Item item = Item.itemsList[itemId];
        if (item == null) {
            throw new IllegalStateException("Trade " + tradeName + " uses an unregistered item id " + itemId);
        }
        if (maximumCount > item.getItemStackLimit()) {
            throw new IllegalArgumentException("Trade " + tradeName + " requests " + maximumCount + " "
                    + item.getUnlocalizedName() + " but its stack limit is " + item.getItemStackLimit());
        }
    }

    private static void removeOldFarmerTrades() {
        EntityVillager.removeLevelUpTrade(0, 2);
        EntityVillager.removeCustomTrade(0, TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(1)
                .sell().item(Block.grass.blockID).itemCount(2, 4).weight(0.3F).build());
        EntityVillager.removeCustomTrade(0, TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(1)
                .convert().input(TradeItem.fromIDAndMetadata(Block.tallGrass.blockID, 1, 2, 4))
                .secondInput(TradeItem.fromID(Item.emerald.itemID, 1, 2))
                .output(TradeItem.fromID(BTWItems.hempSeeds.itemID, 2, 6)).weight(0.3F).build());
        EntityVillager.removeCustomTrade(0, TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(2)
                .buy().item(Item.shears.itemID).itemCount(1, 1).weight(0.4F).build());
        EntityVillager.removeCustomTrade(0, TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(3)
                .buy().item(BTWItems.redMushroom.itemID).itemCount(2, 5).weight(1.2F).build());
        EntityVillager.removeCustomTrade(0, TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(3)
                .buy().item(Item.bucketWater.itemID).itemCount(1, 1).build());
        EntityVillager.removeCustomTrade(0, TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(4)
                .buy().item(BTWItems.chowder.itemID).itemCount(1, 2).build());
        EntityVillager.removeCustomTrade(0, TradeProvider.getBuilder().name("nmFarmer0").profession(0).level(5)
                .convert().input(TradeItem.fromID(Item.paper.itemID))
                .secondInput(TradeItem.fromID(NMItems.bloodOrb.itemID, 8, 16))
                .output(TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("efficiency")))
                .mandatory().build());
    }

    private static void addLibrarianTrades(){
        convert(
                "ifhy:librarian_ender_treatise",
                1,
                5,
                TradeItem.fromID(BTWItems.corpseEye.itemID),
                TradeItem.fromID(NMItems.bloodOrb.itemID, 4, 10),
                TradeItem.fromID(NMItems.librarianEnderTreatise.itemID),
                1.0F,
                false,
                true);

        buy("ifhy:librarian_reed_stems", 1, 1, NMItems.reedStem.itemID, 0, 39, 54);
        buy("ifhy:librarian_washed_pith", 1, 1, NMItems.washedPith.itemID, 0, 35, 49);
        buy("ifhy:librarian_wet_plant_sheets", 1, 1, NMItems.wetFusedPlantSheet.itemID, 0, 28, 42);
        buy("ifhy:librarian_plant_sheets", 1, 1, NMItems.plantSheet.itemID, 0, 33, 47);
        buy("ifhy:librarian_washed_sugar_cane", 1, 1, NMItems.washedSugarCane.itemID, 0, 43, 57);
        buy("ifhy:librarian_books", 1, 2, Item.book.itemID, 0, 19, 29);
        buy("ifhy:librarian_writable_book", 1, 2, Item.writableBook.itemID, 0, 1, 1);
        buy("ifhy:librarian_bookshelves", 1, 2, Block.bookShelf.blockID, 0, 6, 10);
        buy("ifhy:librarian_repeaters", 1, 2, Item.redstoneRepeater.itemID, 0, 24, 36);
        buy("ifhy:librarian_hellfire_dust", 1, 3, BTWItems.hellfireDust.itemID, 0, 31, 45);
        buy("ifhy:librarian_dispensers", 1, 4, BTWBlocks.blockDispenser.blockID, 0, 11, 17);
        buy("ifhy:librarian_buddy_blocks", 1, 4, BTWBlocks.buddyBlock.blockID, 0, 9, 15);
        buy("ifhy:librarian_detector_blocks", 1, 4, BTWBlocks.detectorBlock.blockID, 0, 8, 13);
        sell("ifhy:librarian_soul_flux", 1, 4, BTWItems.soulFlux.itemID, 0, 2, 4, 1.0F, false, 17, 25);
        convert("ifhy:librarian_blast_scroll", 1, 4,
                TradeItem.fromID(Item.paper.itemID, 32, 46),
                TradeItem.fromID(NMItems.bloodOrb.itemID, 13, 20),
                TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("blast")));

        finishRecipes("Librarian Trades");
    }



    private static void addPriestTrades(){

        buy("ifhy:priest_bone_shards", 2, 1, NMItems.boneShard.itemID, 0, 40, 56);
        buy("ifhy:priest_ash", 2, 1, NMItems.ash.itemID, 0, 34, 49);
        buy("ifhy:priest_blood_orbs", 2, 2, NMItems.bloodOrb.itemID, 0, 30, 44);
        buy("ifhy:priest_witch_warts", 2, 2, BTWItems.witchWart.itemID, 0, 38, 54);
        buy("ifhy:priest_spider_eyes", 2, 3, Item.spiderEye.itemID, 0, 12, 16);
        buy("ifhy:priest_blaze_powder", 2, 3, Item.blazePowder.itemID, 0, 32, 46);
        buy("ifhy:priest_gunpowder", 2, 3, Item.gunpowder.itemID, 0, 46, 62);
        buy("ifhy:priest_soul_sand_piles", 2, 4, BTWItems.soulSandPile.itemID, 0, 36, 52);
        buy("ifhy:priest_soul_chips", 2, 4, NMItems.soulChip.itemID, 0, 28, 41);
        buy("ifhy:priest_rotten_arrows", 2, 4, BTWItems.rottenArrow.itemID, 0, 40, 55);
        convert("ifhy:priest_wart_to_nether_wart", 2, 2,
                TradeItem.fromID(BTWItems.witchWart.itemID, 36, 50),
                TradeItem.fromID(Item.emerald.itemID, 4, 7),
                TradeItem.fromID(Item.netherStalkSeeds.itemID, 2, 5));
        convert("ifhy:priest_blood_orb_to_potion", 2, 3,
                TradeItem.fromID(NMItems.bloodOrb.itemID, 32, 46),
                TradeItem.fromID(Item.emerald.itemID, 6, 10),
                TradeItem.fromIDAndMetadata(Item.potion.itemID, 0));
        convert("ifhy:priest_soul_chip_to_urn", 2, 4,
                TradeItem.fromID(NMItems.soulChip.itemID, 28, 40),
                TradeItem.fromID(Item.emerald.itemID, 8, 12),
                TradeItem.fromID(BTWItems.soulUrn.itemID, 2, 4));
        convert("ifhy:priest_bone_shard_to_candle", 2, 4,
                TradeItem.fromID(NMItems.boneShard.itemID, 38, 52),
                TradeItem.fromID(Item.emerald.itemID, 10, 14),
                TradeItem.fromID(BTWItems.candle.itemID, 2, 4));
        sell("ifhy:priest_brewing_stand", 2, 4, Item.brewingStand.itemID, 0, 1, 2, 1.0F, false, 12, 18);

        finishRecipes("Priest Trades");

    }


    private static void addBlacksmithTrades(){

        buy("ifhy:blacksmith_iron_bloom", 3, 1, NMItems.ironBloom.itemID, 0, 26, 38);
        buy("ifhy:blacksmith_nickel_raw_rock", 3, 1, NMItems.nickelRawRock.itemID, 0, 34, 48);
        buy("ifhy:blacksmith_nickel_crushed", 3, 2, NMItems.nickelCrushedRock.itemID, 0, 38, 53);
        buy("ifhy:blacksmith_nickel_washed", 3, 2, NMItems.nickelWashedConcentrate.itemID, 0, 30, 43);
        buy("ifhy:blacksmith_nickel_roasted", 3, 2, NMItems.nickelRoastedConcentrate.itemID, 0, 26, 37);
        buy("ifhy:blacksmith_lithium_raw", 3, 2, NMItems.lithiumRaw.itemID, 0, 36, 50);
        buy("ifhy:blacksmith_lithium_hammered", 3, 2, NMItems.lithiumHammered.itemID, 0, 32, 45);
        buy("ifhy:blacksmith_lithium_washed", 3, 2, NMItems.lithiumWashed.itemID, 0, 28, 40);
        buy("ifhy:blacksmith_lithium_refined", 3, 2, NMItems.lithiumRefined.itemID, 0, 24, 35);
        buy("ifhy:blacksmith_diamond_rock", 3, 3, NMItems.diamondBearingRock.itemID, 0, 20, 30);
        buy("ifhy:blacksmith_cracked_diamond_rock", 3, 3, NMItems.crackedDiamondBearingRock.itemID, 0, 24, 36);
        buy("ifhy:blacksmith_diamond_grit", 3, 3, NMItems.washedDiamondGrit.itemID, 0, 30, 42);
        buy("ifhy:blacksmith_diamond_slurry", 3, 3, NMItems.stabilizedDiamondSlurry.itemID, 0, 22, 33);
        buy("ifhy:blacksmith_seeded_matrix", 3, 3, NMItems.seededDiamondMatrix.itemID, 0, 18, 27);
        buy("ifhy:blacksmith_nickel_matrix", 3, 3, NMItems.nickelBoundDiamondMatrix.itemID, 0, 16, 24);
        buy("ifhy:blacksmith_carbon_mix", 3, 4, NMItems.carbonRichIronMix.itemID, 0, 32, 46);
        buy("ifhy:blacksmith_carburized_bloom", 3, 4, NMItems.carburizedIronBloom.itemID, 0, 24, 35);
        buy("ifhy:blacksmith_carbon_nuggets", 3, 4, NMItems.carbonIronNugget.itemID, 0, 40, 56);
        buy("ifhy:blacksmith_carbon_ingots", 3, 4, NMItems.carbonIronIngot.itemID, 0, 26, 38);
        buy("ifhy:blacksmith_carbon_plates", 3, 4, NMItems.carbonIronPlate.itemID, 0, 22, 32);
        buy("ifhy:blacksmith_lithium_iron_blanks", 3, 4, NMItems.lithiumTreatedIronBlank.itemID, 0, 24, 34);
        buy("ifhy:blacksmith_reinforced_ingots", 3, 4, NMItems.reinforcedIronIngot.itemID, 0, 20, 29);
        buy("ifhy:blacksmith_reinforced_plates", 3, 4, NMItems.reinforcedIronPlate.itemID, 0, 18, 26);
        buy("ifhy:blacksmith_raw_mercury", 3, 4, NMItems.rawMercuryCrystal.itemID, 0, 30, 42);
        buy("ifhy:blacksmith_mercury_powder", 3, 4, NMItems.mercuryPowder.itemID, 0, 34, 48);
        buy("ifhy:blacksmith_mercury_concentrate", 3, 4, NMItems.washedMercuryConcentrate.itemID, 0, 28, 39);
        buy("ifhy:blacksmith_mercury_amalgam", 3, 4, NMItems.mercuryAmalgam.itemID, 0, 22, 31);
        buy("ifhy:blacksmith_tungsten_chunk", 3, 5, NMItems.tungstenChunk.itemID, 0, 22, 33);
        buy("ifhy:blacksmith_crushed_tungsten", 3, 5, NMItems.crushedTungsten.itemID, 0, 26, 38);
        buy("ifhy:blacksmith_tungsten_concentrate", 3, 5, NMItems.tungstenConcentrate.itemID, 0, 24, 35);
        buy("ifhy:blacksmith_brittle_tungsten", 3, 5, NMItems.brittleTungstenCake.itemID, 0, 18, 28);
        buy("ifhy:blacksmith_tungsten_powder", 3, 5, NMItems.tungstenPowder.itemID, 0, 30, 44);
        buy("ifhy:blacksmith_pure_tungsten", 3, 5, NMItems.pureTungstenChunk.itemID, 0, 16, 24);
        buy("ifhy:blacksmith_tungsten_nuggets", 3, 5, NMItems.tungstenNugget.itemID, 0, 38, 54);
        buy("ifhy:blacksmith_tungsten_ingots", 3, 5, NMItems.tungstenIngot.itemID, 0, 20, 30);
        buy("ifhy:blacksmith_saturated_coresteel", 3, 5, NMItems.saturatedCoresteelCharge.itemID, 0, 14, 22);
        buy("ifhy:blacksmith_cooled_coresteel", 3, 5, NMItems.cooledCoresteelCharge.itemID, 0, 12, 19);
        buy("ifhy:blacksmith_coresteel_ingots", 3, 5, NMItems.coresteelIngot.itemID, 0, 18, 27);
        buy("ifhy:blacksmith_coresteel_plates", 3, 5, NMItems.coresteelPlate.itemID, 0, 16, 24);

        finishRecipes("Blacksmith Trades");
    }


    private static void addButcherTrades(){

        buy("ifhy:butcher_raw_mutton", 4, 1, BTWItems.rawMutton.itemID, 0, 10, 16);
        buy("ifhy:butcher_raw_cheval", 4, 1, BTWItems.rawCheval.itemID, 0, 9, 15);
        buy("ifhy:butcher_raw_eggs", 4, 1, Item.egg.itemID, 0, 12, 16);
        buy("ifhy:butcher_wool", 4, 1, BTWItems.wool.itemID, 0, 42, 58);
        buy("ifhy:butcher_tallow", 4, 2, BTWItems.tallow.itemID, 0, 34, 47);
        buy("ifhy:butcher_cured_meat", 4, 2, BTWItems.curedMeat.itemID, 0, 10, 16);
        buy("ifhy:butcher_raw_fish", 4, 2, Item.fishRaw.itemID, 0, 12, 16);
        buy("ifhy:butcher_deboned_fish", 4, 2, NMItems.debonedRawFish.itemID, 0, 10, 16);
        buy("ifhy:butcher_fish_flesh", 4, 3, NMItems.fishFlesh.itemID, 0, 1, 1);
        buy("ifhy:butcher_calamari", 4, 3, NMItems.calamari.itemID, 0, 10, 16);
        buy("ifhy:butcher_raw_wolf_chops", 4, 3, BTWItems.rawWolfChop.itemID, 0, 10, 16);
        buy("ifhy:butcher_raw_liver", 4, 3, BTWItems.rawLiver.itemID, 0, 10, 16);
        buy("ifhy:butcher_raw_mystery_meat", 4, 4, BTWItems.rawMysteryMeat.itemID, 0, 10, 16);
        convert("ifhy:butcher_mutton_to_cooked", 4, 2,
                TradeItem.fromID(BTWItems.rawMutton.itemID, 12, 16),
                TradeItem.fromID(Item.emerald.itemID, 6, 10),
                TradeItem.fromID(BTWItems.cookedMutton.itemID, 4, 8));
        convert("ifhy:butcher_eggs_to_feed", 4, 2,
                TradeItem.fromID(Item.egg.itemID, 12, 16),
                TradeItem.fromID(Item.emerald.itemID, 4, 7),
                TradeItem.fromID(BTWItems.chickenFeed.itemID, 3, 6));
        convert("ifhy:butcher_tallow_to_soap", 4, 3,
                TradeItem.fromID(BTWItems.tallow.itemID, 28, 40),
                TradeItem.fromID(Item.emerald.itemID, 8, 12),
                TradeItem.fromID(BTWItems.soap.itemID, 4, 8));
        convert("ifhy:butcher_fish_to_hooks", 4, 3,
                TradeItem.fromID(Item.fishRaw.itemID, 12, 16),
                TradeItem.fromID(Item.emerald.itemID, 5, 9),
                TradeItem.fromID(BTWItems.boneFishHook.itemID, 2, 4));

        finishRecipes("Butcher Trades");

    }

    private static void addFishermanTrades() {
        final int profession = EntityFishermanVillager.PROFESSION_ID;

        // The base pool is intentionally mundane: a fisherman converts supplies and
        // catches into the emeralds needed for his useful stock.
        buy("ifhy:fisherman_string", profession, 1, Item.silk.itemID, 0, 20, 32);
        buy("ifhy:fisherman_bone_hooks", profession, 1, BTWItems.boneFishHook.itemID, 0, 8, 16);
        buy("ifhy:fisherman_raw_fish", profession, 1, Item.fishRaw.itemID, 0, 12, 16);
        buy("ifhy:fisherman_mackerel", profession, 1, NMItems.mackerel.itemID, 0, 12, 16);
        buy("ifhy:fisherman_cod", profession, 1, NMItems.cod.itemID, 0, 12, 16);
        buy("ifhy:fisherman_calamari", profession, 2, NMItems.calamari.itemID, 0, 8, 16);
        buy("ifhy:fisherman_bass", profession, 2, NMItems.bass.itemID, 0, 10, 16);
        buy("ifhy:fisherman_trout", profession, 2, NMItems.trout.itemID, 0, 10, 16);
        buy("ifhy:fisherman_carp", profession, 2, NMItems.carp.itemID, 0, 8, 14);
        buy("ifhy:fisherman_salmon", profession, 3, NMItems.salmon.itemID, 0, 8, 14);
        buy("ifhy:fisherman_fish_flesh", profession, 3, NMItems.fishFlesh.itemID, 0, 1, 1);
        buy("ifhy:fisherman_swordfish", profession, 3, NMItems.swordfish.itemID, 0, 1, 2);
        buy("ifhy:fisherman_golden_carp", profession, 4, NMItems.goldenCarp.itemID, 0, 1, 2);
        buy("ifhy:fisherman_lavafish", profession, 4, NMItems.lavafish.itemID, 0, 8, 16);

        // Upgrades and rods are conversions, not clean emerald purchases. This keeps
        // the fisherman tied to fishing supplies instead of becoming an emerald sink.
        convert("ifhy:fisherman_bell_upgrade", profession, 1,
                TradeItem.fromID(BTWItems.boneFishHook.itemID, 4, 8),
                TradeItem.fromID(Item.emerald.itemID, 4, 6),
                TradeItem.fromID(NMItems.fishingBellUpgrade.itemID));
        convert("ifhy:fisherman_iron_rod", profession, 2,
                TradeItem.fromID(Item.ingotIron.itemID, 8),
                TradeItem.fromID(Item.emerald.itemID, 10, 14),
                TradeItem.fromID(NMItems.ironFishingPole.itemID));
        convert("ifhy:fisherman_lure_upgrade", profession, 2,
                TradeItem.fromID(Item.silk.itemID, 12, 16),
                TradeItem.fromID(Item.emerald.itemID, 7, 10),
                TradeItem.fromID(NMItems.fishingLureUpgrade.itemID));
        convert("ifhy:fisherman_auto_reel", profession, 3,
                TradeItem.fromID(Item.bone.itemID, 12, 16),
                TradeItem.fromID(Item.emerald.itemID, 12, 16),
                TradeItem.fromID(NMItems.fishingAutoReelUpgrade.itemID));
        convert("ifhy:fisherman_diamond_rod", profession, 3,
                TradeItem.fromID(BTWItems.diamondIngot.itemID, 8),
                TradeItem.fromID(Item.emerald.itemID, 18, 24),
                TradeItem.fromID(NMItems.diamondFishingPole.itemID));
        convert("ifhy:fisherman_rare_lure", profession, 4,
                TradeItem.fromID(BTWItems.batWing.itemID, 1, 2),
                TradeItem.fromID(Item.emerald.itemID, 18, 24),
                TradeItem.fromID(NMItems.rareFishLureUpgrade.itemID));
        convert("ifhy:fisherman_steel_rod", profession, 4,
                TradeItem.fromID(BTWItems.soulforgedSteelIngot.itemID, 8),
                TradeItem.fromID(Item.emerald.itemID, 26, 32),
                TradeItem.fromID(NMItems.steelFishingPole.itemID));

        // Disposable bait and basic rod supplies occupy the otherwise low-value slots.
        // Every offer consumes a catch as well as emeralds, so the fisherman cannot turn
        // an emerald surplus into unrestricted valuable stock.
        convert("ifhy:fisherman_rotten_flesh_bait", profession, 1,
                TradeItem.fromID(Item.fishRaw.itemID, 8, 12),
                TradeItem.fromID(Item.emerald.itemID, 1, 2),
                TradeItem.fromID(Item.rottenFlesh.itemID, 8, 16));
        convert("ifhy:fisherman_fish_to_sticks", profession, 1,
                TradeItem.fromID(Item.fishRaw.itemID, 6, 10),
                TradeItem.fromID(Item.emerald.itemID, 1, 2),
                TradeItem.fromID(Item.stick.itemID, 8, 16));
        convert("ifhy:fisherman_fish_to_string", profession, 1,
                TradeItem.fromID(Item.fishRaw.itemID, 8, 12),
                TradeItem.fromID(Item.emerald.itemID, 2, 3),
                TradeItem.fromID(Item.silk.itemID, 4, 8));
        convert("ifhy:fisherman_fish_to_bones", profession, 1,
                TradeItem.fromID(Item.fishRaw.itemID, 8, 12),
                TradeItem.fromID(Item.emerald.itemID, 2, 3),
                TradeItem.fromID(Item.bone.itemID, 4, 8));
        convert("ifhy:fisherman_creeper_oyster_bait", profession, 2,
                TradeItem.fromID(NMItems.mackerel.itemID, 8, 12),
                TradeItem.fromID(Item.emerald.itemID, 4, 6),
                TradeItem.fromID(BTWItems.creeperOysters.itemID, 2, 4));
        convert("ifhy:fisherman_bat_wing_bait", profession, 3,
                TradeItem.fromID(NMItems.salmon.itemID, 4, 6),
                TradeItem.fromID(Item.emerald.itemID, 8, 12),
                TradeItem.fromID(BTWItems.batWing.itemID));
        convert("ifhy:fisherman_spider_eye_bait", profession, 4,
                TradeItem.fromID(NMItems.goldenCarp.itemID),
                TradeItem.fromID(Item.emerald.itemID, 12, 16),
                TradeItem.fromID(Item.spiderEye.itemID, 2, 4));
        TradeProvider.getBuilder().name("ifhy:fisherman_fishing_essence").profession(profession).level(5)
                .sell().item(NMItems.fishingEssence.itemID).emeraldCost(32, 32).mandatory().addToTradeList();

        buy("ifhy:fisherman_level_two", profession, 1, Item.fishingRod.itemID, 0, 1, 1, 1.0F, true, 0, 0);
        buy("ifhy:fisherman_level_three", profession, 2, Item.fishRaw.itemID, 0, 16, 16, 1.0F, true, 0, 0);
        buy("ifhy:fisherman_level_four", profession, 3, NMItems.swordfish.itemID, 0, 2, 2, 1.0F, true, 0, 0);
        buy("ifhy:fisherman_level_five", profession, 4, NMItems.goldenCarp.itemID, 0, 4, 4, 1.0F, true, 0, 0);
        TradeProvider.getBuilder().name("ifhy:fisherman_lavafish_final").profession(profession).level(5)
                .buy().item(NMItems.lavafish.itemID).itemCount(16, 16).mandatory().addToTradeList();

        EntityVillager.defaultTradeByProfessionList.put(profession,
                TradeProvider.getBuilder().name("ifhy:fisherman_default").profession(profession).level(1)
                        .buy().item(Item.silk.itemID).itemCount(20, 32).build());
        finishRecipes("Fisherman Trades");
    }

    private static void addNightmareVillagerTrades(){
        final int profession = 5;

        // Rank one accepts both Overworld Eclipse drops and the first resources brought
        // back from the outer End. No single farm can carry the whole reputation chain.
        buy("nmEclipseMerchantDarksun", profession, 1, NMItems.darksunFragment.itemID, 0, 4, 8, 1.2F);
        buy("nmEclipseMerchantEnderDust", profession, 1, NMItems.enderDust.itemID, 0, 8, 16, 1.1F);
        buy("nmEclipseMerchantPaleRoot", profession, 1, NMItems.paleRoot.itemID, 0, 12, 24, 1.0F);
        buy("nmEclipseMerchantMercury", profession, 1, NMItems.rawMercuryCrystal.itemID, 0, 12, 24, 1.0F);
        buy("nmEclipseMerchantCharredFlesh", profession, 1, NMItems.charredFlesh.itemID, 0, 4, 8, 0.8F);
        buy("nmEclipseMerchantSilver", profession, 1, NMItems.silverLump.itemID, 0, 4, 8, 0.8F);
        sell("nmEclipseMerchantSoulFlux", profession, 1, BTWItems.soulFlux.itemID, 0, 2, 4, 0.7F, false, 8, 14);

        buy("nmEclipseMerchantShell", profession, 2, NMItems.enderShell.itemID, 0, 6, 12, 1.2F);
        buy("nmEclipseMerchantWashedMercury", profession, 2, NMItems.washedMercuryConcentrate.itemID, 0, 4, 8, 1.0F);
        buy("nmEclipseMerchantRootResin", profession, 2, NMItems.paleRootResin.itemID, 0, 2, 5, 0.9F);
        buy("nmEclipseMerchantVoidMembrane", profession, 2, NMItems.voidMembrane.itemID, 0, 1, 2, 0.7F);
        buy("nmEclipseMerchantGhastTentacle", profession, 2, NMItems.ghastTentacle.itemID, 0, 1, 3, 0.7F);
        sell("nmEclipseMerchantRails", profession, 2, Block.rail.blockID, 0, 16, 32, 0.8F, false, 5, 10);

        buy("nmEclipseMerchantAmalgam", profession, 3, NMItems.mercuryAmalgam.itemID, 0, 2, 4, 1.2F);
        buy("nmEclipseMerchantPhaseCharge", profession, 3, NMItems.phaseSteelCharge.itemID, 0, 1, 2, 0.9F);
        buy("nmEclipseMerchantCreeperTear", profession, 3, NMItems.creeperTear.itemID, 0, 1, 2, 0.8F);
        buy("nmEclipseMerchantElementalRod", profession, 3, NMItems.elementalRod.itemID, 0, 1, 2, 0.7F);
        sell("nmEclipseMerchantNameTag", profession, 3, Item.nameTag.itemID, 0, 1, 2, 0.5F, false, 8, 16);

        buy("nmEclipseMerchantPhaseSteel", profession, 4, NMItems.phaseSteelIngot.itemID, 0, 2, 4, 1.2F);
        buy("nmEclipseMerchantMechanism", profession, 4, NMItems.enderMechanism.itemID, 0, 1, 1, 0.8F);
        buy("nmEclipseMerchantWaterRod", profession, 4, NMItems.waterRod.itemID, 0, 1, 2, 0.7F);
        buy("nmEclipseMerchantShadowRod", profession, 4, NMItems.shadowRod.itemID, 0, 1, 2, 0.7F);
        buy("nmEclipseMerchantEnderMechanismFinal", profession, 5, NMItems.enderMechanism.itemID, 0, 1, 2, 1.1F);
        buy("nmEclipseMerchantDarksunFinal", profession, 5, NMItems.darksunFragment.itemID, 0, 16, 32, 1.0F);
        TradeProvider.getBuilder().name("nmEclipseMerchantBloodBone1").profession(profession).level(5)
                .sell().item(NMBlocks.bloodBones.blockID).emeraldCost(24, 24).mandatory().addToTradeList();
        TradeProvider.getBuilder().name("nmEclipseMerchantBloodBone2").profession(profession).level(5)
                .sell().item(NMBlocks.bloodBones.blockID).emeraldCost(28, 28).mandatory().addToTradeList();
        TradeProvider.getBuilder().name("nmEclipseMerchantBloodBone3").profession(profession).level(5)
                .sell().item(NMBlocks.bloodBones.blockID).emeraldCost(32, 32).mandatory().addToTradeList();
        TradeProvider.getBuilder().name("nmEclipseMerchantBloodBone4").profession(profession).level(5)
                .sell().item(NMBlocks.bloodBones.blockID).emeraldCost(36, 36).mandatory().addToTradeList();

        TradeProvider.getBuilder().name("nmEclipseMerchantRank2").profession(profession).level(1)
                .buy().item(NMItems.enderCrystal.itemID).itemCount(16, 16).mandatory().addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmEclipseMerchantRank3").profession(profession).level(2)
                .buy().item(NMItems.mercuryAmalgam.itemID).itemCount(8, 8).mandatory().addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmEclipseMerchantRank4").profession(profession).level(3)
                .buy().item(NMItems.phaseSteelIngot.itemID).itemCount(8, 8).mandatory().addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmEclipseMerchantRank5").profession(profession).level(4)
                .buy().item(NMItems.enderMechanism.itemID).itemCount(4, 4).mandatory().addAsLevelUpTrade();

        EntityVillager.defaultTradeByProfessionList.put(profession,
                TradeProvider.getBuilder().name("nmEclipseMerchantDefault").profession(profession).level(1)
                        .buy().item(NMItems.enderDust.itemID).itemCount(8, 16).build());
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
        sell("nmNetherTier1PolishedShard", profession, 2, NMItems.crystalPolishedShard.itemID, 0, 1, 2, 0.7F, false, 5, 9);
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

        crucible.removeRecipe(new ItemStack(BTWItems.soulforgedSteelIngot, 1), (TagOrStack[])new ItemStack[]{new ItemStack(BTWItems.steelNugget, 9)});



        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot), new ItemStack[]{
                new ItemStack(BTWItems.diamondIngot),
                new ItemStack(Item.netherQuartz, 4),
                new ItemStack(NMItems.denseNetherrackCore),
                new ItemStack(NMItems.nickelHeatComponent),
                new ItemStack(NMItems.crystalPrecisionGear)
        });


        crucible.removeRecipe(new ItemStack(BTWItems.soulforgedSteelIngot, 1), new ItemStack[]{
                new ItemStack(Item.ingotIron), new ItemStack(BTWItems.coalDust),
                new ItemStack(BTWItems.soulUrn), new ItemStack(BTWItems.soulFlux)
        });
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.soulforgedSteelIngot), new ItemStack[]{
                new ItemStack(Item.ingotIron),
                new ItemStack(BTWItems.coalDust, 2),
                new ItemStack(BTWItems.soulUrn),
                new ItemStack(BTWItems.soulFlux, 2),
                new ItemStack(NMItems.denseNetherrackCore),
                new ItemStack(NMItems.nickelHeatComponent),
                new ItemStack(BTWItems.steelNugget, 12),
                new ItemStack(NMItems.lithiumHeatCompound)
        });


        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.bloodIngot), new ItemStack[]{
                new ItemStack(NMItems.refinedDiamondIngot),
                new ItemStack(NMItems.bloodOrb, 8),
                new ItemStack(NMItems.deadzoneShard, 4),
                new ItemStack(NMItems.nickelBinding, 4),
                new ItemStack(NMItems.lithiumHeatCompound, 2),
                new ItemStack(NMItems.crystalPrecisionGear)
        });

        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.diamondStick), new ItemStack[]{
                new ItemStack(NMItems.refinedDiamondIngot),
                new ItemStack(NMItems.ironStick)
        });

        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.verdantIngot), new ItemStack[]{
                new ItemStack(NMItems.washedEmeraldPowder, 4),
                new ItemStack(Item.goldNugget)
        });

        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.darkIngot), new ItemStack[]{
                new ItemStack(BTWItems.soulforgedSteelIngot), new ItemStack(NMItems.coresteelIngot, 3),
                new ItemStack(BTWItems.soulFlux, 2), new ItemStack(BTWItems.coalDust, 16),
                new ItemStack(Item.ingotIron, 4), new ItemStack(NMItems.bloodOrb, 8)
        });

        // The fired ceramic liner is a reusable crucible insert, not a consumable shortcut.
        RecipeManager.addStokedCrucibleRecipe(
                new ItemStack[]{new ItemStack(NMItems.phaseSteelIngot, 2), new ItemStack(NMItems.firedCrucibleLiner)},
                new ItemStack[]{new ItemStack(NMItems.phaseSteelCharge), new ItemStack(NMItems.firedCrucibleLiner),
                        new ItemStack(BTWItems.soulFlux, 2)});


        finishRecipes("Crucible Recipes");

    }
    private static void addCauldronRecipes(){
        // BTW has separate tannin-strength variants (and a pre-cut shortcut). IFHY instead
        // requires the hide to be washed and worked after scouring before its final bark and
        // dung tanning bath.
        CauldronCraftingManager cauldron = CauldronCraftingManager.getInstance();
        CauldronStokedCraftingManager cauldronStoked = CauldronStokedCraftingManager.getInstance();
        removeLegacyGlueRecipes(cauldronStoked);
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

        // Nether hides can be fully tanned without importing bark: a blood-moon blood orb
        // and quartz dust replace the overworld tannin bath after the normal scouring work.
        cauldron.addRecipe(new ItemStack(BTWItems.tannedLeather), new TagOrStack[]{
                new ItemStack(NMItems.workedScouredLeather),
                new ItemStack(NMItems.bloodOrb),
                new ItemStack(NMItems.quartzDust, 2)
        });
        finishRecipes("Cauldron Recipes");

    }

    private static void removeLegacyGlueRecipes(CauldronStokedCraftingManager cauldron) {
        cauldron.removeRecipe(
                new ItemStack[]{new ItemStack(BTWItems.chocolate, 2), new ItemStack(Item.bucketEmpty)},
                new TagOrStack[]{new ItemStack(Item.dyePowder, 1, 3), new ItemStack(Item.sugar), new ItemStack(Item.bucketMilk)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue), new TagOrStack[]{TagInstance.of(BTWTags.wholeLeathers)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue), new TagOrStack[]{TagInstance.of(BTWTags.cutLeathers, 2)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue, 2), new TagOrStack[]{new ItemStack(Item.helmetLeather, 1, Short.MAX_VALUE)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue, 4), new TagOrStack[]{new ItemStack(Item.plateLeather, 1, Short.MAX_VALUE)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue, 3), new TagOrStack[]{new ItemStack(Item.legsLeather, 1, Short.MAX_VALUE)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue, 2), new TagOrStack[]{new ItemStack(Item.bootsLeather, 1, Short.MAX_VALUE)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue, 2), new TagOrStack[]{new ItemStack(Item.saddle)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue, 3), new TagOrStack[]{new ItemStack(BTWItems.breedingHarness, 1, Short.MAX_VALUE)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue), new TagOrStack[]{TagInstance.of(BTWTags.books, 2)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue, 2), new TagOrStack[]{new ItemStack(BTWItems.tannedLeatherHelmet, 1, Short.MAX_VALUE)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue, 4), new TagOrStack[]{new ItemStack(BTWItems.tannedLeatherChest, 1, Short.MAX_VALUE)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue, 3), new TagOrStack[]{new ItemStack(BTWItems.tannedLeatherLeggings, 1, Short.MAX_VALUE)});
        cauldron.removeRecipe(new ItemStack(BTWItems.glue, 2), new TagOrStack[]{new ItemStack(BTWItems.tannedLeatherBoots, 1, Short.MAX_VALUE)});
    }

    private static void addCisternRecipes(){
        CisternRecipeManager manager = CisternRecipeManager.instance;

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(Item.ingotIron), new ItemStack(NMItems.lithiumStabilizer), new ItemStack(NMItems.lithiumStabilizer)},
                CisternTileEntity.FLUID_BRINE, 2, 24, 700,
                new ItemStack[]{new ItemStack(NMItems.lithiumTreatedIronBlank, 2)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(Item.dyePowder, 4, Color.BLUE.colorID), new ItemStack(Item.clay)},
                CisternTileEntity.FLUID_WATER, 1, 8, 360,
                new ItemStack[]{new ItemStack(NMItems.azureSlip)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.lithiumHeatCompound, 16), new ItemStack(Block.glass, 8),
                        new ItemStack(BTWItems.diamondPile, 4)},
                CisternTileEntity.FLUID_WATER, 3, 20, 240,
                new ItemStack[]{new ItemStack(NMItems.refractoryPaste)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.quartzDust, 2), new ItemStack(NMItems.lithiumHeatCompound, 3)},
                CisternTileEntity.FLUID_LAVA, 3, 20, 240,
                new ItemStack[]{new ItemStack(NMItems.refractoryPaste)}));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.emeraldGrit, 4)},
                CisternTileEntity.FLUID_WATER, 0, 2, 240,
                new ItemStack[]{new ItemStack(NMItems.washedEmeraldPowder)}));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.quartzDust, 4), new ItemStack(BTWItems.netherSludge)},
                CisternTileEntity.FLUID_LAVA, 3, 6, 300,
                new ItemStack[]{new ItemStack(NMItems.moltenQuartzCompound)})
                .setConsumesFluid());

        // Nether lava washing replaces the unavailable water-and-brine hemp route.
        // The washed hemp still has to be fired and milled into fibres.
        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(BTWItems.hemp)},
                CisternTileEntity.FLUID_LAVA, 2, 18, 360,
                new ItemStack[]{new ItemStack(NMItems.washedHemp)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(BTWItems.hemp)},
                CisternTileEntity.FLUID_WATER, 2, 4, 360,
                new ItemStack[]{new ItemStack(NMItems.washedHemp)}));

        // A lava cistern can also make the ordinary kiln masonry required by BTW machines.
        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(BTWBlocks.looseNetherBrick)},
                CisternTileEntity.FLUID_LAVA, 3, 12, 480,
                new ItemStack[]{new ItemStack(Item.brick, 4)}));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(BTWItems.scouredLeather)},
                CisternTileEntity.FLUID_LAVA, 2, 24, 360,
                new ItemStack[]{new ItemStack(NMItems.washedScouredLeather)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(Item.redstone, 4), new ItemStack(Item.ingotIron)},
                CisternTileEntity.FLUID_BRINE, 1, 6, 300,
                new ItemStack[]{new ItemStack(NMItems.signalConductiveCharge)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.refinedRedstone), new ItemStack(Item.ingotIron)},
                CisternTileEntity.FLUID_BRINE, 1, 6, 300,
                new ItemStack[]{new ItemStack(NMItems.signalConductiveCharge, 2)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.obsidianBrick), new ItemStack(NMItems.tungstenNugget)},
                CisternTileEntity.FLUID_LAVA, 3, 8, 300,
                new ItemStack[]{new ItemStack(NMItems.blackglassCharge)}));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.denseNetherrackCore, 8), new ItemStack(NMItems.tungstenIngot), new ItemStack(BTWItems.hellfireDust, 4)},
                CisternTileEntity.FLUID_LAVA, 3, 48, 600,
                new ItemStack[]{new ItemStack(NMItems.saturatedCoresteelCharge)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.crystalLatticeCharge)},
                CisternTileEntity.FLUID_BRINE, 2, 6, 360,
                new ItemStack[]{new ItemStack(NMItems.setCrystalLattice)})
                .setConsumesFluid());
        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.endstonePowder), new ItemStack(NMItems.paleRootResin)},
                CisternTileEntity.FLUID_ACIDIC_WASH, 3, 92, 780,
                new ItemStack[]{new ItemStack(NMItems.endstoneClay)}));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(BTWItems.goldOrePile, 2), new ItemStack(BTWItems.coalDust)},
                CisternTileEntity.FLUID_WATER, 1, 4, 360,
                new ItemStack[]{new ItemStack(Item.goldNugget)})
                .addRandomOutput(new ItemStack(NMItems.refinementWaste), 0.20F)
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.crystalUncleanedShard)},
                CisternTileEntity.FLUID_WATER, 0, 1, 120,
                new ItemStack[]{new ItemStack(NMItems.crystalCleanShard, 1, 79)}));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.nickelCrushedRock)},
                CisternTileEntity.FLUID_WATER, 0, 1, 180,
                new ItemStack[]{new ItemStack(NMItems.nickelWashedConcentrate)})
                .addRandomOutput(new ItemStack(NMItems.refinementWaste), 0.25F)
                .setResultingFluid(CisternTileEntity.FLUID_SLURRY));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.lithiumRaw)},
                CisternTileEntity.FLUID_WATER, 0, 2, 160,
                new ItemStack[0])
                .setResultingFluid(CisternTileEntity.FLUID_BRINE));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.lithiumHammered)},
                CisternTileEntity.FLUID_WATER, 0, 1, 140,
                new ItemStack[]{new ItemStack(NMItems.lithiumWashed)}));

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
                new ItemStack[]{new ItemStack(NMItems.stabilizedDiamondSlurry), new ItemStack(NMItems.crystalPolishedShard)},
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

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.mercuryPowder, 4)},
                CisternTileEntity.FLUID_WATER, 1, 6, 360,
                new ItemStack[]{new ItemStack(NMItems.washedMercuryConcentrate, 2)})
                .setResultingFluid(CisternTileEntity.FLUID_SLURRY));

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.washedMercuryConcentrate, 2),
                        new ItemStack(Item.goldNugget), new ItemStack(NMItems.nickelBinding)},
                CisternTileEntity.FLUID_ACIDIC_WASH, 2, 8, 480,
                new ItemStack[]{new ItemStack(NMItems.mercuryAmalgam)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{new ItemStack(NMItems.paleRootPulp, 2), new ItemStack(NMItems.enderDust)},
                CisternTileEntity.FLUID_BRINE, 1, 4, 300,
                new ItemStack[]{new ItemStack(NMItems.paleRootResin)})
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
                new ItemStack[]{
                        new ItemStack(NMItems.primitiveGlue),
                        new ItemStack(BTWItems.cutScouredLeather, 2),
                        new ItemStack(Item.dyePowder, 4, 15),
                        new ItemStack(NMItems.dyeBlend)
                },
                CisternTileEntity.FLUID_WATER, 1, 6, 420,
                new ItemStack[]{new ItemStack(NMItems.glueSlurry)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{
                        new ItemStack(NMItems.hammeredStoneBrick),
                        new ItemStack(BTWItems.clayPile, 2),
                        new ItemStack(BTWItems.sandPile),
                        new ItemStack(NMItems.dyeBlend)
                },
                CisternTileEntity.FLUID_WATER, 1, 5, 360,
                new ItemStack[]{new ItemStack(NMItems.mortaredStoneBrick)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{
                        new ItemStack(BTWBlocks.ladder),
                        new ItemStack(BTWItems.stoneBrick, 4),
                        new ItemStack(BTWItems.glue),
                        new ItemStack(NMItems.dyeBlend)
                },
                CisternTileEntity.FLUID_WATER, 1, 4, 300,
                new ItemStack[]{new ItemStack(NMBlocks.stoneLadder)})
                .setConsumesFluid());

        manager.addRecipe(new CisternRecipe(
                new ItemStack[]{
                        new ItemStack(NMItems.crystalPowder, 2),
                        new ItemStack(BTWItems.sandPile, 4),
                        new ItemStack(BTWItems.potash),
                        new ItemStack(NMItems.dyeBlend)
                },
                CisternTileEntity.FLUID_WATER, 2, 8, 480,
                new ItemStack[]{new ItemStack(NMItems.glassBatch, 2)})
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
        manager.addWaterRecipe(
                new ItemStack(NMItems.cooledCoresteelCharge),
                new ItemStack(NMItems.saturatedCoresteelCharge),
                300);
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
        MiscRecipeManager.instance.addRecipe(
                new ItemStack(NMItems.cooledCoresteelCharge),
                new ItemStack(NMItems.saturatedCoresteelCharge),
                "Place touching water for 15s");



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
        FurnaceRecipes.smelting().addSmelting(NMItems.carbonRichIronMix.itemID, new ItemStack(NMItems.carburizedIronBloom), 0.0F, 3);
        FurnaceRecipes.smelting().addSmelting(NMItems.lithiumTreatedIronBlank.itemID, new ItemStack(NMItems.reinforcedIronIngot), 0.2F, 3);
        FurnaceRecipes.smelting().addSmelting(NMItems.wetGasket.itemID, new ItemStack(NMItems.waxedGasket), 0.0F, 1);
        FurnaceRecipes.smelting().addSmelting(NMItems.wetRefractoryCloth.itemID, new ItemStack(NMItems.refractoryCloth), 0.0F, 2);
        FurnaceRecipes.smelting().addSmelting(NMItems.moltenQuartzCompound.itemID, new ItemStack(NMItems.quartzglassIngot), 0.4F, 3);
        FurnaceRecipes.smelting().addSmelting(NMItems.blackglassCharge.itemID, new ItemStack(NMItems.blackglassIngot), 0.5F, 4);
        FurnaceRecipes.smelting().addSmelting(NMItems.cooledCoresteelCharge.itemID, new ItemStack(NMItems.coresteelIngot), 0.6F, 4);
        FurnaceRecipes.smelting().addSmelting(NMItems.signalConductiveCharge.itemID, new ItemStack(NMItems.signalAlloyIngot), 0.1F, 2);
        FurnaceRecipes.smelting().addSmelting(NMItems.azureSlip.itemID, new ItemStack(NMItems.azureCeramicIngot), 0.1F, 2);
        FurnaceRecipes.smelting().addSmelting(NMItems.setCrystalLattice.itemID, new ItemStack(NMItems.prismaticIngot), 0.2F, 2);
        FurnaceRecipes.smelting().addSmelting(NMItems.endstoneClay.itemID, new ItemStack(NMItems.endstoneIngot), 0.3F, 3);
        FurnaceRecipes.smelting().addSmelting(NMItems.nickelWashedConcentrate.itemID, new ItemStack(NMItems.nickelRoastedConcentrate), 0.0f, 2);
        FurnaceRecipes.smelting().addSmelting(NMItems.nickelRoastedConcentrate.itemID, new ItemStack(NMItems.nickelIngot), 0.4f, 3);
        FurnaceRecipes.smelting().addSmelting(NMItems.lithiumWashed.itemID, new ItemStack(NMItems.lithiumRefined), 0.2f, 1);
        FurnaceRecipes.smelting().addSmelting(NMItems.diamondBearingMaterial.itemID, new ItemStack(Item.diamond), 1.0f, 4);

        FurnaceRecipes.smelting().addSmelting(NMItems.debonedRawFish.itemID, new ItemStack(Item.fishCooked), 0.0f);
        FurnaceRecipes.smelting().addSmelting(NMItems.wetFusedPlantSheet.itemID, new ItemStack(NMItems.plantSheet), 0.0f);
        FurnaceRecipes.smelting().addSmelting(NMItems.tungstenConcentrate.itemID, new ItemStack(NMItems.brittleTungstenCake), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.pureTungstenChunk.itemID, new ItemStack(NMItems.tungstenNugget), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.obsidianPaste.itemID, new ItemStack(NMItems.obsidianBrick), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.washedHemp.itemID, new ItemStack(NMItems.driedHemp), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.azureSlag.itemID, new ItemStack(NMItems.brittleAzureCake), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.lapisPrecipitate.itemID, new ItemStack(NMItems.brittleAzureCake), 0.0F);
        FurnaceRecipes.smelting().addSmelting(NMItems.pressedGlueCake.itemID, new ItemStack(BTWItems.glue), 0.0F, 2);
        FurnaceRecipes.smelting().addSmelting(NMItems.mortaredStoneBrick.itemID, new ItemStack(BTWItems.stoneBrick), 0.0F, 2);
        FurnaceRecipes.smelting().addSmelting(NMItems.glassBatch.itemID, new ItemStack(Block.glass), 0.0F, 3);
        FurnaceRecipes.smelting().addSmelting(NMItems.unbakedChocolateCake.itemID, new ItemStack(NMItems.chocolateCake), 0.0F);

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

        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.steelSword), new Object[]{" N# ", " C# ", " P# ", " LX ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.steelShovel), new Object[]{" CP ", " N# ", " L# ", "  X ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft, Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.steelPickaxe), new Object[]{"####", "NPC ", " LX ", " PX ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('C'), NMItems.crystalPrecisionGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.mattock), new Object[]{"####", "#NPC", "  LX", "  PX", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('C'), NMItems.crystalPrecisionGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.steelHoe), new Object[]{"##NP", "  C ", " LX ", " PX ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('C'), NMItems.crystalPrecisionGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.battleaxe), new Object[]{"####", "#X#N", "PCXL", "  X ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('C'), NMItems.crystalPrecisionGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.steelAxe), new Object[]{"##NP", "#CXL", "  X ", "  X ", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('X'), BTWItems.haft, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('C'), NMItems.crystalPrecisionGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.plateHelmet), new Object[]{"####", "#NN#", "#CC#", "PLLP", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('C'), NMItems.crystalLens, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.plateBreastplate), new Object[]{"PNNP", "####", "#LL#", "#CC#", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('L'), NMItems.lithiumHeatCompound, Character.valueOf('C'), NMItems.crystalPrecisionGear});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.plateLeggings), new Object[]{"####", "PNNP", "#LL#", "#  #", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('L'), NMItems.lithiumHeatCompound});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWItems.plateBoots), new Object[]{"PNNP", "#LL#", "#CC#", Character.valueOf('#'), BTWItems.soulforgedSteelIngot, Character.valueOf('P'), BTWItems.steelArmorPlate, Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound, Character.valueOf('C'), NMItems.deadzoneShard});
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWBlocks.dormandSoulforge), new Object[]{"GNNG", "GDCG", "GPLG", "GGGG", Character.valueOf('G'), Item.ingotGold, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('D'), NMItems.refinedDiamondIngot, Character.valueOf('C'), NMItems.denseNetherrackCore, Character.valueOf('P'), NMItems.crystalPrecisionGear, Character.valueOf('L'), NMItems.lithiumHeatCompound});

        RecipeManager.addSoulforgeRecipe(new ItemStack(NMBlocks.enderAssembler), new Object[]{
                "PLLP", "NGGN", "GCCG", "PSSP",
                Character.valueOf('P'), BTWItems.steelArmorPlate,
                Character.valueOf('L'), NMItems.firedCrucibleLiner,
                Character.valueOf('N'), NMItems.nickelMachinePart,
                Character.valueOf('G'), new ItemStack(NMBlocks.netherProgressionGems, 1, NMBlocks.META_PURPLE_GEM),
                Character.valueOf('C'), NMItems.crystalPrecisionGear,
                Character.valueOf('S'), BTWItems.soulforgedSteelIngot});

        RecipeManager.addSoulforgeRecipe(new ItemStack(NMBlocks.minerDrillTier4), new Object[]{
                "BEPB", "GM3G", "RSAR", "DCDD",
                Character.valueOf('B'), NMItems.bloodIngot,
                Character.valueOf('E'), Item.eyeOfEnder,
                Character.valueOf('P'), NMItems.phaseSteelPlate,
                Character.valueOf('G'), NMItems.crystalPrecisionGear,
                Character.valueOf('M'), NMItems.enderMechanism,
                Character.valueOf('3'), NMBlocks.minerDrillTier3,
                Character.valueOf('R'), NMItems.refinedRedstone,
                Character.valueOf('S'), BTWItems.soulforgedSteelIngot,
                Character.valueOf('A'), NMItems.mercuryAmalgam,
                Character.valueOf('D'), Item.diamond,
                Character.valueOf('C'), new ItemStack(NMBlocks.netherProgressionGems, 1, NMBlocks.META_BLACK_GEM)});

        RecipeManager.addSoulforgeRecipe(new ItemStack(NMItems.enderSword), new Object[]{"  I ", " II ", " II ", " HM ", Character.valueOf('I'), NMItems.phaseSteelIngot, Character.valueOf('M'), NMItems.enderMechanism, Character.valueOf('H'), BTWItems.haft});
        RecipeManager.addSoulforgeRecipe(new ItemStack(NMItems.enderPickaxe), new Object[]{"IIII", " MHI", "  H ", "  H ", Character.valueOf('I'), NMItems.phaseSteelIngot, Character.valueOf('M'), NMItems.enderMechanism, Character.valueOf('H'), BTWItems.haft});
        RecipeManager.addSoulforgeRecipe(new ItemStack(NMItems.enderAxe), new Object[]{"II", "IM", " H", " H", Character.valueOf('I'), NMItems.phaseSteelIngot, Character.valueOf('M'), NMItems.enderMechanism, Character.valueOf('H'), BTWItems.haft});
        RecipeManager.addSoulforgeRecipe(new ItemStack(NMItems.enderShovel), new Object[]{"IPI ", "IMI ", " H  ", " H  ", Character.valueOf('I'), NMItems.phaseSteelIngot, Character.valueOf('P'), NMItems.phaseSteelPlate, Character.valueOf('M'), NMItems.enderMechanism, Character.valueOf('H'), BTWItems.haft});
        RecipeManager.addSoulforgeRecipe(new ItemStack(NMItems.enderHoe), new Object[]{"IIP ", " M  ", " H  ", " H  ", Character.valueOf('I'), NMItems.phaseSteelIngot, Character.valueOf('P'), NMItems.phaseSteelPlate, Character.valueOf('M'), NMItems.enderMechanism, Character.valueOf('H'), BTWItems.haft});
        RecipeManager.addSoulforgeRecipe(new ItemStack(NMItems.enderHelmet), new Object[]{"PPPP", "I  I", "M  M", "    ", Character.valueOf('P'), NMItems.phaseSteelPlate, Character.valueOf('I'), NMItems.phaseSteelIngot, Character.valueOf('M'), NMItems.enderMechanism});
        RecipeManager.addSoulforgeRecipe(new ItemStack(NMItems.enderChestplate), new Object[]{"P  P", "IIII", "IMMI", "PPPP", Character.valueOf('P'), NMItems.phaseSteelPlate, Character.valueOf('I'), NMItems.phaseSteelIngot, Character.valueOf('M'), NMItems.enderMechanism});
        RecipeManager.addSoulforgeRecipe(new ItemStack(NMItems.enderLeggings), new Object[]{"PPPP", "IMMI", "I  I", "I  I", Character.valueOf('P'), NMItems.phaseSteelPlate, Character.valueOf('I'), NMItems.phaseSteelIngot, Character.valueOf('M'), NMItems.enderMechanism});
        RecipeManager.addSoulforgeRecipe(new ItemStack(NMItems.enderBoots), new Object[]{"P  P", "I  I", "PMMP", "P  P", Character.valueOf('P'), NMItems.phaseSteelPlate, Character.valueOf('I'), NMItems.phaseSteelIngot, Character.valueOf('M'), NMItems.enderMechanism});

        int recipeIndex = soulforge.getRecipeList().size();
        RecipeManager.addShapelessSoulforgeRecipe(
                new ItemStack(Item.eyeOfEnder),
                new Object[]{
                      NMItems.automationEssence,
                      NMItems.husbandryEssence,
                      NMItems.infernalEssence,
                      NMItems.artisanEssence,
                      NMItems.fishingEssence,
                      NMItems.refinedDiamondIngot,
                      NMItems.deadzoneShard,
                      BTWItems.soulforgedSteelIngot,
                      NMItems.tungstenIngot,
                      NMItems.bloodIngot,
                      NMItems.lithiumHeatCompound,
                      NMItems.crystalPrecisionGear,
                      NMItems.endAccord,
                      Item.netherStar,
                      BTWItems.verticalWindMill,
                      BTWItems.ocularOfEnder});
        ultimateEyeOfEnderRecipe = (IRecipe)soulforge.getRecipeList().get(recipeIndex);
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
        RecipeManager.addMillStoneRecipe(new ItemStack(Item.leather), new ItemStack(NMItems.pigHide));
        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.scouredLeather), new ItemStack(Item.leather));
        millstone.removeRecipe(new ItemStack(BTWItems.hempFibers, 4), new ItemStack(BTWItems.hemp));
        RecipeManager.addMillStoneRecipe(new ItemStack(NMItems.crystalPowder, 2), new ItemStack(NMItems.crystalPolishedShard));
        RecipeManager.addMillStoneRecipe(new ItemStack(NMItems.mercuryPowder, 2), new ItemStack(NMItems.rawMercuryCrystal));
        RecipeManager.addMillStoneRecipe(new ItemStack(NMItems.enderDust, 2), new ItemStack(NMItems.enderCrystal));
        RecipeManager.addMillStoneRecipe(new ItemStack(NMItems.enderShellPowder, 2), new ItemStack(NMItems.enderShell));
        RecipeManager.addMillStoneRecipe(new ItemStack(NMItems.paleRootPulp, 2), new ItemStack(NMItems.paleRoot));
        RecipeManager.addMillStoneRecipe(new ItemStack(NMItems.emeraldGrit, 4), new ItemStack(NMItems.crackedEmerald));
        RecipeManager.addMillStoneRecipe(new ItemStack(NMItems.endstonePowder, 2), new ItemStack(Block.whiteStone));


        finishRecipes("Millstone Recipes");

    }

    private static void addCraftingRecipes(){
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.carbonRichIronMix), new Object[]{
                NMPostItems.washedIronMix, BTWItems.coalDust, BTWItems.coalDust, BTWItems.coalDust});
        RecipeManager.addRecipe(new ItemStack(BTWItems.mail, 4), new Object[]{
                "SNS",
                "NIN",
                "SNS",
                Character.valueOf('I'), Item.ingotIron,
                Character.valueOf('S'), BTWTags.strings,
                Character.valueOf('N'), BTWItems.ironNugget
        });

        RecipeManager.addRecipe(new ItemStack(BTWItems.mail, 4), new Object[]{
                "SNS",
                "NIN",
                "SNS",
                Character.valueOf('I'), Item.ingotIron,
                Character.valueOf('N'), BTWTags.strings,
                Character.valueOf('S'), BTWItems.ironNugget
        });
        RecipeManager.addRecipe(new ItemStack(NMItems.carbonIronIngot), new Object[]{
                "NNN", "NYN", "NNN", Character.valueOf('N'), NMItems.carbonIronNugget, Character.valueOf('Y'), BTWItems.stoneBrick});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.wetGasket), new Object[]{
                BTWItems.tannedLeather, NMItems.thickenedSap});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.wetRefractoryCloth), new Object[]{
                NMItems.refractoryPaste, BTWItems.fabric});
        RecipeManager.addRecipe(new ItemStack(NMItems.pressureRegulator), new Object[]{
                "NLN", "LCL", " R ",
                Character.valueOf('N'), NMItems.nickelMachinePart,
                Character.valueOf('L'), NMItems.lithiumRefined,
                Character.valueOf('R'), NMItems.refinedRedstone,
                Character.valueOf('C'), NMItems.crystalLens});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.thermalLaminate, 2), new Object[]{
                NMItems.nickelHeatComponent, NMItems.refractoryCloth, NMItems.lithiumHeatCompound});

        RecipeManager.addRecipe(new ItemStack(NMItems.carbonIronHelmet), new Object[]{
                "PPP", "ISI",
                Character.valueOf('P'), NMItems.carbonIronPlate,
                Character.valueOf('S'), BTWItems.leatherStrap,
                Character.valueOf('I'), NMItems.carbonIronIngot});
        RecipeManager.addRecipe(new ItemStack(NMItems.carbonIronChestplate), new Object[]{
                "P P", "IBI", "PPP",
                Character.valueOf('P'), NMItems.carbonIronPlate,
                Character.valueOf('I'), NMItems.carbonIronIngot,
                Character.valueOf('B'), BTWItems.padding});
        RecipeManager.addRecipe(new ItemStack(NMItems.carbonIronLeggings), new Object[]{
                "PIP", "PBP", "S S",
                Character.valueOf('P'), NMItems.carbonIronPlate,
                Character.valueOf('I'), NMItems.carbonIronIngot,
                Character.valueOf('S'), BTWItems.leatherStrap,
                Character.valueOf('B'), BTWItems.belt});
        RecipeManager.addRecipe(new ItemStack(NMItems.carbonIronBoots), new Object[]{
                "P P", "I I", "S S",
                Character.valueOf('P'), NMItems.carbonIronPlate,
                Character.valueOf('I'), NMItems.carbonIronIngot,
                Character.valueOf('S'), BTWItems.leatherStrap});
        RecipeManager.addShapelessRecipe(NMItems.carbonIronHelmet.createWaxedStack(), new Object[]{
                new ItemStack(NMItems.carbonIronHelmet, 1, Short.MAX_VALUE), BTWItems.tallow, BTWItems.tallow, BTWItems.tallow});
        RecipeManager.addRecipe(NMItems.carbonIronChestplate.createWaxedStack(), new Object[]{
                "AT", Character.valueOf('A'), new ItemStack(NMItems.carbonIronChestplate, 1, Short.MAX_VALUE), Character.valueOf('T'), BTWItems.tallow});
        RecipeManager.addRecipe(NMItems.carbonIronLeggings.createWaxedStack(), new Object[]{
                "AT", Character.valueOf('A'), new ItemStack(NMItems.carbonIronLeggings, 1, Short.MAX_VALUE), Character.valueOf('T'), BTWItems.tallow});
        RecipeManager.addRecipe(NMItems.carbonIronBoots.createWaxedStack(), new Object[]{
                "AT", Character.valueOf('A'), new ItemStack(NMItems.carbonIronBoots, 1, Short.MAX_VALUE), Character.valueOf('T'), BTWItems.tallow});

        RecipeManager.addRecipe(new ItemStack(NMItems.reinforcedIronHelmet), new Object[]{
                "PPP", "PCP", "S S",
                Character.valueOf('P'), NMItems.reinforcedIronPlate,
                Character.valueOf('S'), BTWItems.leatherStrap,
                Character.valueOf('C'), new ItemStack(Item.helmetIron, 1, Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(NMItems.reinforcedIronChestplate), new Object[]{
                "PCP", "PPP", "PTP",
                Character.valueOf('P'), NMItems.reinforcedIronPlate,
                Character.valueOf('T'), BTWItems.leatherStrap,
                Character.valueOf('C'), new ItemStack(Item.plateIron, 1, Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(NMItems.reinforcedIronLeggings), new Object[]{
                "PPP", "PCP", "P P",
                Character.valueOf('P'), NMItems.reinforcedIronPlate,
                Character.valueOf('C'), new ItemStack(Item.legsIron, 1, Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(NMItems.reinforcedIronBoots), new Object[]{
                "PCP", "P P",
                Character.valueOf('P'), NMItems.reinforcedIronPlate,
                Character.valueOf('C'), new ItemStack(Item.bootsIron, 1, Short.MAX_VALUE)});

        RecipeManager.addRecipe(new ItemStack(NMItems.nickelWorkLeggings), new Object[]{
                "PPP", "PBP", "N N",
                Character.valueOf('P'), NMItems.nickelPlate,
                Character.valueOf('B'), BTWItems.belt,
                Character.valueOf('N'), BTWItems.padding});
        RecipeManager.addRecipe(new ItemStack(NMItems.nickelWorkBoots), new Object[]{
                "P P", "S S",
                Character.valueOf('P'), NMItems.nickelPlate,
                Character.valueOf('S'), BTWItems.leatherStrap});

        RecipeManager.addRecipe(new ItemStack(NMItems.thermalChestLining), new Object[]{
                "LBP", "PBL",
                Character.valueOf('L'), NMItems.thermalLaminate,
                Character.valueOf('B'), NMItems.nickelBinding,
                Character.valueOf('P'), BTWItems.padding});
        RecipeManager.addRecipe(new ItemStack(NMItems.heatResistantHelmet), new Object[]{
                "PCP", "NDN", " L ",
                Character.valueOf('P'), NMItems.nickelPlate,
                Character.valueOf('C'), NMItems.crystalLens,
                Character.valueOf('N'), NMItems.nickelBinding,
                Character.valueOf('D'), new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE),
                Character.valueOf('L'), NMItems.thermalLaminate});
        RecipeManager.addRecipe(new ItemStack(NMItems.heatResistantChestplate), new Object[]{
                "PDP", "PLP", "PPP",
                Character.valueOf('P'), NMItems.nickelPlate,
                Character.valueOf('D'), new ItemStack(Item.plateDiamond, 1, Short.MAX_VALUE),
                Character.valueOf('L'), NMItems.thermalChestLining});
        RecipeManager.addRecipe(new ItemStack(NMItems.heatResistantLeggings), new Object[]{
                "PWP", "PDP", "LBL",
                Character.valueOf('P'), NMItems.nickelPlate,
                Character.valueOf('D'), new ItemStack(Item.legsDiamond, 1, Short.MAX_VALUE),
                Character.valueOf('L'), NMItems.thermalLaminate,
                Character.valueOf('W'), new ItemStack(NMItems.nickelWorkLeggings, 1, Short.MAX_VALUE),
                Character.valueOf('B'), BTWItems.belt});
        RecipeManager.addRecipe(new ItemStack(NMItems.heatResistantBoots), new Object[]{
                "PDP", "PWP", "SLS",
                Character.valueOf('P'), NMItems.nickelPlate,
                Character.valueOf('D'), new ItemStack(Item.bootsDiamond, 1, Short.MAX_VALUE),
                Character.valueOf('L'), NMItems.thermalLaminate,
                Character.valueOf('W'), new ItemStack(NMItems.nickelWorkBoots, 1, Short.MAX_VALUE),
                Character.valueOf('S'), BTWItems.leatherStrap});

        RecipeManager.addRecipe(new ItemStack(NMItems.divingMask), new Object[]{
                "QFQ", "NON", "GCG",
                Character.valueOf('Q'), NMItems.quartzglassPlate,
                Character.valueOf('G'), NMItems.waxedGasket,
                Character.valueOf('N'), NMItems.nickelBinding,
                Character.valueOf('O'), new ItemStack(NMItems.oxygenMask, 1, Short.MAX_VALUE),
                Character.valueOf('C'), NMItems.crystalLens,
                Character.valueOf('F'), NMItems.refractoryCloth});
        RecipeManager.addRecipe(new ItemStack(NMItems.tankReinforcementCradle), new Object[]{
                "TNT", "S S", "TNT",
                Character.valueOf('T'), NMItems.tungstenPlate,
                Character.valueOf('N'), NMItems.nickelPlate,
                Character.valueOf('S'), BTWItems.leatherStrap});
        RecipeManager.addRecipe(new ItemStack(NMItems.divingTank), new Object[]{
                "GOG", "RCR", " L ",
                Character.valueOf('G'), NMItems.waxedGasket,
                Character.valueOf('O'), new ItemStack(NMItems.oxygenTank, 1, Short.MAX_VALUE),
                Character.valueOf('R'), NMItems.pressureRegulator,
                Character.valueOf('C'), NMItems.tankReinforcementCradle,
                Character.valueOf('L'), NMItems.lithiumHeatCompound});

        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.unstableDeadzoneCharge), new Object[]{
                NMItems.coresteelIngot, NMItems.deadzoneShard, NMItems.deadzoneShard, BTWItems.soulFlux});
        RecipeManager.addSoulforgeRecipe(new ItemStack(NMItems.deadzoneAlloyIngot), new Object[]{
                "USSU", "DBBD", "DIID", "UYYU",
                Character.valueOf('U'), NMItems.unstableDeadzoneCharge,
                Character.valueOf('B'), NMItems.bloodIngot,
                Character.valueOf('D'), NMItems.deadzoneShard,
                Character.valueOf('I'), NMItems.blackglassIngot,
                Character.valueOf('Y'), NMItems.reinforcedIronIngot,
                Character.valueOf('S'), BTWItems.soulforgedSteelIngot});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.solarCloth), new Object[]{
                NMItems.deadzoneShard, NMItems.refinedRedstone, NMItems.refractoryCloth});

        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenHelmet), new Object[]{
                "PPP", "IFI", " S ",
                Character.valueOf('P'), NMItems.tungstenPlate,
                Character.valueOf('S'), NMItems.pighideString,
                Character.valueOf('I'), NMItems.tungstenIngot,
                Character.valueOf('F'), NMItems.refractoryCloth});
        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenChestplate), new Object[]{
                "PSP", "IFI", "PPP",
                Character.valueOf('P'), NMItems.tungstenPlate,
                Character.valueOf('S'), NMItems.pighideString,
                Character.valueOf('I'), NMItems.tungstenIngot,
                Character.valueOf('F'), NMItems.refractoryCloth});
        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenLeggings), new Object[]{
                "PIP", "PFP", "PSP",
                Character.valueOf('P'), NMItems.tungstenPlate,
                Character.valueOf('I'), NMItems.tungstenIngot,
                Character.valueOf('S'), NMItems.pighideString,
                Character.valueOf('F'), NMItems.refractoryCloth});
        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenBoots), new Object[]{
                "P P", "IFI", "S S",
                Character.valueOf('P'), NMItems.tungstenPlate,
                Character.valueOf('I'), NMItems.tungstenIngot,
                Character.valueOf('F'), NMItems.refractoryCloth,
                Character.valueOf('S'), NMItems.pighideString});

        RecipeManager.addRecipe(new ItemStack(NMItems.coresteelHelmet), new Object[]{
                "PCP", "PTP",
                Character.valueOf('P'), NMItems.coresteelPlate,
                Character.valueOf('C'), new ItemStack(NMItems.tungstenHelmet, 1, Short.MAX_VALUE),
                Character.valueOf('T'), NMItems.refractoryCloth});
        RecipeManager.addRecipe(new ItemStack(NMItems.coresteelChestplate), new Object[]{
                "PTP", "PCP", "PPP",
                Character.valueOf('P'), NMItems.coresteelPlate,
                Character.valueOf('C'), new ItemStack(NMItems.tungstenChestplate, 1, Short.MAX_VALUE),
                Character.valueOf('T'), NMItems.refractoryCloth});
        RecipeManager.addRecipe(new ItemStack(NMItems.coresteelLeggings), new Object[]{
                "PTP", "PCP", "PBP",
                Character.valueOf('P'), NMItems.coresteelPlate,
                Character.valueOf('C'), new ItemStack(NMItems.tungstenLeggings, 1, Short.MAX_VALUE),
                Character.valueOf('T'), NMItems.refractoryCloth,
                Character.valueOf('B'), NMItems.nickelBinding});
        RecipeManager.addRecipe(new ItemStack(NMItems.coresteelBoots), new Object[]{
                "PCP", "PBP",
                Character.valueOf('P'), NMItems.coresteelPlate,
                Character.valueOf('C'), new ItemStack(NMItems.tungstenBoots, 1, Short.MAX_VALUE),
                Character.valueOf('T'), NMItems.refractoryCloth,
                Character.valueOf('B'), NMItems.nickelBinding});

        RecipeManager.addRecipe(new ItemStack(NMItems.deadzoneHelmet), new Object[]{
                "PPP", "PDP","BSC",
                Character.valueOf('P'), NMItems.deadzoneAlloyPlate,
                Character.valueOf('D'), new ItemStack(NMItems.coresteelHelmet, 1, Short.MAX_VALUE),
                Character.valueOf('B'), NMItems.nickelBinding,
                Character.valueOf('S'), NMItems.deadzoneShard,
                Character.valueOf('C'), NMItems.crystalLens});
        RecipeManager.addRecipe(new ItemStack(NMItems.deadzoneChestplate), new Object[]{
                "PDP", "PPP", "PCP",
                Character.valueOf('P'), NMItems.deadzoneAlloyPlate,
                Character.valueOf('D'), new ItemStack(NMItems.coresteelChestplate, 1, Short.MAX_VALUE),
                Character.valueOf('C'), NMItems.crystalPrecisionGear});
        RecipeManager.addRecipe(new ItemStack(NMItems.deadzoneLeggings), new Object[]{
                "PSP", "PDP", "PBP",
                Character.valueOf('P'), NMItems.deadzoneAlloyPlate,
                Character.valueOf('D'), new ItemStack(NMItems.coresteelLeggings, 1, Short.MAX_VALUE),
                Character.valueOf('S'), NMItems.deadzoneShard,
                Character.valueOf('B'), NMItems.nickelBinding});
        RecipeManager.addRecipe(new ItemStack(NMItems.deadzoneBoots), new Object[]{
                "PDP", "PBP", "S S",
                Character.valueOf('P'), NMItems.deadzoneAlloyPlate,
                Character.valueOf('D'), new ItemStack(NMItems.coresteelBoots, 1, Short.MAX_VALUE),
                Character.valueOf('S'), NMItems.deadzoneShard,
                Character.valueOf('B'), NMItems.nickelBinding});

        RecipeManager.addRecipe(new ItemStack(NMItems.sunHelmet), new Object[]{
                "DBD", "BHB", "FLF",
                Character.valueOf('D'), NMItems.deadzoneAlloyPlate,
                Character.valueOf('B'), NMItems.blackglassPlate,
                Character.valueOf('H'), new ItemStack(NMItems.heatResistantHelmet, 1, Short.MAX_VALUE),
                Character.valueOf('F'), NMItems.refractoryCloth,
                Character.valueOf('L'), BTWItems.belt});
        RecipeManager.addRecipe(new ItemStack(NMItems.sunChestplate), new Object[]{
                "DHD", "VFV", "DBD",
                Character.valueOf('D'), NMItems.deadzoneAlloyPlate,
                Character.valueOf('H'), new ItemStack(NMItems.heatResistantChestplate, 1, Short.MAX_VALUE),
                Character.valueOf('V'), NMItems.verdantPlate,
                Character.valueOf('F'), NMItems.refractoryCloth,
                Character.valueOf('B'), BTWItems.belt});
        RecipeManager.addRecipe(new ItemStack(NMItems.sunLeggings), new Object[]{
                "DQD", "FHF", "DBD",
                Character.valueOf('D'), NMItems.deadzoneAlloyPlate,
                Character.valueOf('H'), new ItemStack(NMItems.heatResistantLeggings, 1, Short.MAX_VALUE),
                Character.valueOf('Q'), NMItems.quartzglassPlate,
                Character.valueOf('F'), NMItems.refractoryCloth,
                Character.valueOf('B'), BTWItems.belt});
        RecipeManager.addRecipe(new ItemStack(NMItems.sunBoots), new Object[]{
                "CHC", "TGT", "CGC",
                Character.valueOf('C'), NMItems.coresteelPlate,
                Character.valueOf('H'), new ItemStack(NMItems.heatResistantBoots, 1, Short.MAX_VALUE),
                Character.valueOf('T'), NMItems.tungstenPlate,
                Character.valueOf('G'), NMItems.waxedGasket});
        RecipeManager.addRecipe(new ItemStack(NMItems.sunVisor), new Object[]{
                "DSD", "QVQ", "BHB",
                Character.valueOf('D'), NMItems.deadzoneAlloyPlate,
                Character.valueOf('B'), NMItems.blackglassPlate,
                Character.valueOf('Q'), NMItems.quartzglassPlate,
                Character.valueOf('V'), new ItemStack(NMItems.divingMask, 1, Short.MAX_VALUE),
                Character.valueOf('S'), NMItems.solarCloth,
                Character.valueOf('H'), new ItemStack(NMItems.sunHelmet, 1, Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(NMItems.sunReservoir), new Object[]{
                "CDC", "RSR", "DHD",
                Character.valueOf('C'), NMItems.coresteelPlate,
                Character.valueOf('D'), NMItems.denseNetherrackCore,
                Character.valueOf('R'), NMItems.pressureRegulator,
                Character.valueOf('S'), new ItemStack(NMItems.divingTank, 1, Short.MAX_VALUE),
                Character.valueOf('H'), new ItemStack(NMItems.sunChestplate, 1, Short.MAX_VALUE)});

        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(NMItems.stoneStick, 4), new Object[]{
                        "C", "C", Character.valueOf('C'), new ItemStack(Block.cobblestone, 1, Short.MAX_VALUE)}),
                NMSkillNodes.MINE_STONE_1000, NMSkillNodes.BRING_LOOSE_STONE_64);

        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(NMItems.ironStick, 8), new Object[]{
                        "I", "I", Character.valueOf('I'), Item.ingotIron}),
                NMSkillNodes.BRING_IRON_INGOT_16, NMSkillNodes.BRING_DIAMOND_BEARING_ROCK_64,
                NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.KILL_MOB_250);

        SkillLockedCrafting.requireSkill(
                RecipeManager.addShapelessRecipe(new ItemStack(NMItems.dyeBlend), new Object[]{
                        new ItemStack(Item.dyePowder, 1, 0), new ItemStack(Item.dyePowder, 1, 1),
                        new ItemStack(Item.dyePowder, 1, 2), new ItemStack(Item.dyePowder, 1, 3),
                        new ItemStack(Item.dyePowder, 1, 4), new ItemStack(Item.dyePowder, 1, 5),
                        new ItemStack(Item.dyePowder, 1, 6), new ItemStack(Item.dyePowder, 1, 7),
                        new ItemStack(Item.dyePowder, 1, 15)}),
                NMSkillNodes.BRING_DYE_64);

        SkillLockedCrafting.requireSkills(
                RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.chocolate, 2), new Object[]{
                        new ItemStack(Item.dyePowder, 1, 3), Item.sugar, Item.bucketMilk,
                        NMItems.dyeBlend, BTWItems.flour}),
                NMSkillNodes.BRING_COCOA_POWDER_256, NMSkillNodes.BRING_DYE_BLEND_16,
                NMSkillNodes.BRING_FLOUR_32);

        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(new ItemStack(NMItems.ironBrick, 8), new Object[]{
                        "SSS", "SIS", "SSS",
                        Character.valueOf('S'), BTWItems.stoneBrick,
                        Character.valueOf('I'), Item.ingotIron}),
                NMSkillNodes.BRING_STONE_BRICK_64);

        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(new ItemStack(NMItems.diamondBrick, 8), new Object[]{
                        "III", "IDI", "III",
                        Character.valueOf('I'), NMItems.ironBrick,
                        Character.valueOf('D'), Item.diamond}),
                NMSkillNodes.BRING_IRON_BRICK_64);

        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(NMBlocks.stationRail, 4), new Object[]{
                        "IGI", "DCP", "INI",
                        Character.valueOf('I'), NMItems.ironBrick,
                        Character.valueOf('G'), Item.ingotGold,
                        Character.valueOf('D'), Block.railDetector,
                        Character.valueOf('C'), Item.comparator,
                        Character.valueOf('P'), Block.railPowered,
                        Character.valueOf('N'), NMItems.nickelPlate}),
                NMSkillNodes.BRING_IRON_BRICK_64, NMSkillNodes.BRING_GOLD_INGOT_16,
                NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_COMPARATOR_8);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.ladder, 2), new Object[]{
                "#S#", "###", "#S#", Character.valueOf('#'), Item.stick, Character.valueOf('S'), BTWTags.strings});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(BTWBlocks.ladder, 2), new Object[]{
                        "MGM", "RIR", "MSM",
                        Character.valueOf('M'), BTWTags.woodenMouldings,
                        Character.valueOf('G'), BTWItems.glue,
                        Character.valueOf('R'), BTWItems.rope,
                        Character.valueOf('I'), BTWItems.ironNugget,
                        Character.valueOf('S'), BTWItems.screw}),
                NMSkillNodes.JUMP_1000, NMSkillNodes.BRING_SLAB_1000, NMSkillNodes.BRING_SAW);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.stoneLadder, 3), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWBlocks.looseCobblestone, Character.valueOf('S'), Item.silk});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.stoneLadder, 3), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWBlocks.looseCobblestone, Character.valueOf('S'), BTWItems.hempFibers});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.stoneLadder, 3), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWBlocks.looseCobblestone, Character.valueOf('S'), BTWItems.sinew});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.ironLadder, 4), new Object[]{"#S#", "###", "#S#", Character.valueOf('#'), BTWItems.ironNugget, Character.valueOf('S'), BTWItems.hempFibers});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(NMBlocks.ironLadder, 2), new Object[]{
                        "IBI", "NSN", "IGI",
                        Character.valueOf('I'), NMItems.ironBrick,
                        Character.valueOf('B'), BTWItems.belt,
                        Character.valueOf('N'), NMItems.nickelPlate,
                        Character.valueOf('S'), NMBlocks.stoneLadder,
                        Character.valueOf('G'), BTWItems.glue}),
                NMSkillNodes.BRING_STONE_LADDER_64, NMSkillNodes.BRING_IRON_BRICK_64,
                NMSkillNodes.BRING_NICKEL_PLATE_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.haft), new Object[]{
                "Y", "X", "#", Character.valueOf('#'), BTWTags.woodenMouldings,
                Character.valueOf('X'), BTWItems.glue, Character.valueOf('Y'), BTWItems.leatherStrap});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(BTWItems.haft), new Object[]{
                        "LNL", "GDG", "SBS",
                        Character.valueOf('L'), BTWItems.leatherStrap,
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('G'), BTWItems.glue,
                        Character.valueOf('D'), NMItems.diamondStick,
                        Character.valueOf('S'), NMItems.lithiumStabilizer,
                        Character.valueOf('B'), NMItems.diamondBrick}),
                NMSkillNodes.BRING_DIAMOND_STICK_16, NMSkillNodes.BRING_DIAMOND_BRICK_4,
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4,
                NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_RAW_LITHIUM_64,
                NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.KILL_WITHER);

        Block[] woodStairs = new Block[]{
                Block.stairsWoodOak, Block.stairsWoodSpruce, Block.stairsWoodBirch,
                Block.stairsWoodJungle, BTWBlocks.bloodWoodStairs};
        for (int woodType = 0; woodType < woodStairs.length; ++woodType) {
            RecipeManager.removeVanillaRecipe(new ItemStack(woodStairs[woodType], 6), new Object[]{
                    "#  ", "## ", "###", Character.valueOf('#'), new ItemStack(Block.planks, 1, woodType)});
            RecipeManager.removeVanillaRecipe(new ItemStack(Block.woodSingleSlab, 6, woodType), new Object[]{
                    "###", Character.valueOf('#'), new ItemStack(Block.planks, 1, woodType)});
            RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Block.planks, 1, woodType), new Object[]{
                    new ItemStack(BTWItems.woodSidingStubID, 1, woodType),
                    new ItemStack(BTWItems.woodSidingStubID, 1, woodType)});
            RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Block.planks, 1, woodType), new Object[]{
                    new ItemStack(Block.woodSingleSlab, 1, woodType),
                    new ItemStack(Block.woodSingleSlab, 1, woodType)});
            RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.woodSidingStubID, 1, woodType), new Object[]{
                    "#", "#", Character.valueOf('#'), new ItemStack(BTWItems.woodMouldingStubID, 1, woodType)});
            RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.woodMouldingStubID, 1, woodType), new Object[]{
                    new ItemStack(BTWItems.woodCornerStubID, 1, woodType),
                    new ItemStack(BTWItems.woodCornerStubID, 1, woodType)});
            RecipeManager.removeVanillaRecipe(
                    new ItemStack(BTWItems.woodMouldingDecorativeStubID, 6,
                            WoodMouldingDecorativeStubBlockItem.getItemDamageForType(woodType, 1)),
                    new Object[]{" S ", "###", "###",
                            Character.valueOf('#'), new ItemStack(Block.planks, 1, woodType),
                            Character.valueOf('S'), new ItemStack(BTWItems.woodSidingStubID, 1, woodType)});
            RecipeManager.removeVanillaRecipe(
                    new ItemStack(BTWItems.woodSidingDecorativeStubID, 6,
                            WoodSidingDecorativeStubBlockItem.getItemDamageForType(woodType, 1)),
                    new Object[]{"###", "###",
                            Character.valueOf('#'), new ItemStack(Block.planks, 1, woodType)});

            SkillLockedCrafting.requireSkills(
                    RecipeManager.addRecipe(new ItemStack(woodStairs[woodType], 4), new Object[]{
                            "P  ", "PG ", "PPS",
                            Character.valueOf('P'), new ItemStack(Block.planks, 1, woodType),
                            Character.valueOf('G'), BTWItems.glue,
                            Character.valueOf('S'), BTWItems.screw}),
                    NMSkillNodes.BRING_GLUE_16, NMSkillNodes.BRING_SCREW_16, NMSkillNodes.BRING_SAW);
            SkillLockedCrafting.requireSkills(
                    RecipeManager.addRecipe(new ItemStack(Block.woodSingleSlab, 4, woodType), new Object[]{
                            "PPP", "GSG",
                            Character.valueOf('P'), new ItemStack(Block.planks, 1, woodType),
                            Character.valueOf('G'), BTWItems.glue,
                            Character.valueOf('S'), BTWItems.screw}),
                    NMSkillNodes.BRING_GLUE_16, NMSkillNodes.BRING_SCREW_16, NMSkillNodes.BRING_SAW);
            SkillLockedCrafting.requireSkills(
                    RecipeManager.addRecipe(
                            new ItemStack(BTWItems.woodMouldingDecorativeStubID, 4,
                                    WoodMouldingDecorativeStubBlockItem.getItemDamageForType(woodType, 1)),
                            new Object[]{"GSG", "PMP", "PPP",
                                    Character.valueOf('P'), new ItemStack(Block.planks, 1, woodType),
                                    Character.valueOf('M'), new ItemStack(BTWItems.woodSidingStubID, 1, woodType),
                                    Character.valueOf('G'), BTWItems.glue,
                                    Character.valueOf('S'), BTWItems.screw}),
                    NMSkillNodes.BRING_GLUE_16, NMSkillNodes.BRING_SCREW_16, NMSkillNodes.BRING_SAW);
            SkillLockedCrafting.requireSkills(
                    RecipeManager.addRecipe(
                            new ItemStack(BTWItems.woodSidingDecorativeStubID, 4,
                                    WoodSidingDecorativeStubBlockItem.getItemDamageForType(woodType, 1)),
                            new Object[]{"PGP", "PSP", "PGP",
                                    Character.valueOf('P'), new ItemStack(Block.planks, 1, woodType),
                                    Character.valueOf('G'), BTWItems.glue,
                                    Character.valueOf('S'), BTWItems.screw}),
                    NMSkillNodes.BRING_GLUE_16, NMSkillNodes.BRING_SCREW_16, NMSkillNodes.BRING_SAW);
        }

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.hamper), new Object[]{
                "S#S", "#P#", "###", Character.valueOf('#'), BTWItems.wickerPane,
                Character.valueOf('P'), BTWTags.planks, Character.valueOf('S'), BTWItems.rope});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(BTWBlocks.hamper), new Object[]{
                        "RWR", "WGW", "SPS",
                        Character.valueOf('R'), BTWItems.rope,
                        Character.valueOf('W'), BTWItems.wickerPane,
                        Character.valueOf('G'), BTWItems.glue,
                        Character.valueOf('P'), BTWTags.planks,
                        Character.valueOf('S'), BTWItems.screw}),
                NMSkillNodes.BRING_GLUE_16, NMSkillNodes.BRING_SCREW_16,
                NMSkillNodes.BRING_WICKER_PANE_16);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.fenceGate), new Object[]{
                "#W#", "#W#", Character.valueOf('#'), Item.stick,
                Character.valueOf('W'), BTWTags.planks});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(Block.fenceGate), new Object[]{
                        "IPI", "GPG", "ISI",
                        Character.valueOf('I'), BTWItems.ironNugget,
                        Character.valueOf('P'), BTWTags.planks,
                        Character.valueOf('G'), BTWItems.glue,
                        Character.valueOf('S'), BTWItems.screw}),
                NMSkillNodes.BRING_GLUE_16, NMSkillNodes.BRING_SCREW_16,
                NMSkillNodes.BRING_IRON_NUGGET_32);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.trapdoor), new Object[]{
                "WW#", "WW#", Character.valueOf('#'), Item.stick,
                Character.valueOf('W'), BTWTags.planks});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(Block.trapdoor), new Object[]{
                        "PIP", "GSG", "PIP",
                        Character.valueOf('P'), BTWTags.planks,
                        Character.valueOf('I'), BTWItems.ironNugget,
                        Character.valueOf('G'), BTWItems.glue,
                        Character.valueOf('S'), BTWItems.screw}),
                NMSkillNodes.BRING_GLUE_16, NMSkillNodes.BRING_SCREW_16,
                NMSkillNodes.BRING_IRON_NUGGET_32);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.stick, 2), new Object[]{BTWTags.planks});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.twigSharpening, 1, 199), new ItemStack[]{new ItemStack(NMItems.twig), new ItemStack(NMItems.twig), new ItemStack(NMItems.twig), new ItemStack(NMItems.flintChip)});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.sharpTwigBarkWrapping, 1, 49), new Object[]{new ItemStack(NMItems.sharpTwig), BTWTags.barks, BTWTags.barks, BTWTags.barks});

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.bowDrill), new Object[]{new ItemStack(Item.stick), new ItemStack(Item.stick), BTWTags.strings});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.bowDrill), new Object[]{new ItemStack(Item.stick), new ItemStack(Item.stick), new ItemStack(Item.stick), BTWTags.strings});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.bow), new Object[]{" TS", "T S", " TS", Character.valueOf('S'), BTWTags.fineStrings, Character.valueOf('T'), Item.stick});
//        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.arrow, 2), new Object[]{new ItemStack(Item.feather), new ItemStack(Item.stick), BTWTags.strings, new ItemStack(Item.flint)});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.cauldron), new Object[]{"# #", "# #", "###", Character.valueOf('#'), Item.ingotIron});
//        RecipeManager.removeVanillaShapelessRecipe(
//                new ItemStack(Item.eyeOfEnder),
//                new Object[]{Item.enderPearl, Item.blazePowder});
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
        RecipeManager.addRecipe(new ItemStack(Item.bow), new Object[]{" TS", "T S", " TS", Character.valueOf('S'), NMTags.netherCompatibleStrings, Character.valueOf('T'), NMTags.netherCompatibleSticks});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.arrow, 4), new Object[]{NMItems.soulFlint, NMItems.pighideString, NMItems.tungstenNugget});
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
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.refinedRedstone), new Object[]{NMItems.redstoneCrystal, NMItems.crystalPolishedShard});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.hydraulicLens), new Object[]{NMItems.aquamarine, NMItems.crystalPolishedShard, Block.glass});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.fluidGauge), new Object[]{NMItems.aquamarine, NMItems.nickelPlate, NMItems.hydraulicLens});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(BTWItems.redstoneEye, 2), new Object[]{
                        "AGA", "DRD", "NLN",
                        Character.valueOf('A'), NMItems.aquamarine,
                        Character.valueOf('G'), Item.ingotGold,
                        Character.valueOf('D'), NMItems.dyeBlend,
                        Character.valueOf('R'), NMItems.refinedRedstone,
                        Character.valueOf('N'), NMItems.nickelPlate,
                        Character.valueOf('L'), NMItems.crystalLens}),
                NMSkillNodes.BRING_AQUAMARINE_16, NMSkillNodes.BRING_REDSTONE_BLOCK_16,
                NMSkillNodes.BRING_DYE_BLEND_16, NMSkillNodes.BRING_NICKEL_PLATE_4);

        RecipeManager.addRecipe(new ItemStack(Block.netherrack, 1, 2), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(Block.netherrack, 1, 0)});
        RecipeManager.addRecipe(new ItemStack(Block.netherrack, 1, 3), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(Block.netherrack, 1, 2)});
        RecipeManager.addRecipe(new ItemStack(Block.netherrack, 1, 4), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(Block.netherrack, 1, 3)});

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.rail, 12), new Object[]{"X X", "XSX", "X X", Character.valueOf('X'), BTWItems.ironNugget, Character.valueOf('S'), Item.stick});
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.railPowered, 6), new Object[]{"X X", "XSX", "XRX", Character.valueOf('X'), BTWItems.ironNugget, Character.valueOf('S'), Item.stick, Character.valueOf('R'), BTWItems.redstoneLatch});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.woodenDetectorRail, 6), new Object[]{"X X", "X#X", "XRX", Character.valueOf('X'), BTWItems.ironNugget, Character.valueOf('R'), Item.redstone, Character.valueOf('#'), BTWTags.woodenPressurePlates});
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.railDetector, 6), new Object[]{"X X", "X#X", "XRX", Character.valueOf('X'), BTWItems.ironNugget, Character.valueOf('R'), Item.redstone, Character.valueOf('#'), BTWTags.stonePressurePlates});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.steelDetectorRail, 6), new Object[]{"X X", "X#X", "XRX", Character.valueOf('X'), BTWItems.ironNugget, Character.valueOf('R'), Item.redstone, Character.valueOf('#'), BTWTags.metalPressurePlates});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.minecartEmpty), new Object[]{"# #", "###", Character.valueOf('#'), Item.ingotIron});
        RecipeManager.addRecipe(new ItemStack(Block.rail, 12), new Object[]{"X X", "XSX", "X X", Character.valueOf('X'), NMTags.ironTungstenNuggets, Character.valueOf('S'), NMTags.netherCompatibleSticks});
        RecipeManager.addRecipe(new ItemStack(Block.railPowered, 6), new Object[]{"X X", "XSX", "XRX", Character.valueOf('X'), NMTags.ironTungstenNuggets, Character.valueOf('S'), NMTags.netherCompatibleSticks, Character.valueOf('R'), BTWItems.redstoneLatch});
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.woodenDetectorRail, 6), new Object[]{"X X", "X#X", "XRX", Character.valueOf('X'), NMTags.ironTungstenNuggets, Character.valueOf('R'), Item.redstone, Character.valueOf('#'), BTWTags.woodenPressurePlates});
        RecipeManager.addRecipe(new ItemStack(Block.railDetector, 6), new Object[]{"XFX", "X#X", "XRX", Character.valueOf('X'), NMTags.ironTungstenNuggets, Character.valueOf('F'), NMTags.netherSignalBinders, Character.valueOf('R'), Item.redstone, Character.valueOf('#'), NMTags.netherRailPressurePlates});
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.steelDetectorRail, 6), new Object[]{"X X", "X#X", "XRX", Character.valueOf('X'), NMTags.ironTungstenNuggets, Character.valueOf('R'), Item.redstone, Character.valueOf('#'), BTWTags.metalPressurePlates});
        RecipeManager.addRecipe(new ItemStack(Block.railActivator, 6), new Object[]{"XSX", "X#X", "XSX", Character.valueOf('X'), NMTags.ironTungstenNuggets, Character.valueOf('#'), Block.torchRedstoneActive, Character.valueOf('S'), NMTags.netherCompatibleSticks});
        RecipeManager.addRecipe(new ItemStack(Item.minecartEmpty), new Object[]{"# #", "###", Character.valueOf('#'), NMTags.ironTungstenIngots});
        RecipeManager.addRecipe(new ItemStack(Block.rail, 16), new Object[]{"P P", "PSP", "P P", Character.valueOf('P'), NMItems.carbonIronPlate, Character.valueOf('S'), NMTags.netherCompatibleSticks});
        RecipeManager.addRecipe(new ItemStack(Block.railPowered, 10), new Object[]{"P P", "PSP", "PRP", Character.valueOf('P'), NMItems.reinforcedIronPlate, Character.valueOf('S'), NMTags.netherCompatibleSticks, Character.valueOf('R'), BTWItems.redstoneLatch});
        RecipeManager.addRecipe(new ItemStack(Item.redstoneRepeater, 2), new Object[]{"TPT", "SRS", "CNC", Character.valueOf('T'), Block.torchRedstoneActive, Character.valueOf('P'), NMItems.signalAlloyPlate, Character.valueOf('S'), BTWTags.stoneBrickItems, Character.valueOf('R'), NMItems.refinedRedstone, Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('N'), NMItems.nickelPlate});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(NMBlocks.minerDrill), new Object[]{
                        "TRT", "NGN", "DLD",
                        Character.valueOf('T'), NMItems.tungstenIngot,
                        Character.valueOf('R'), NMItems.refinedRedstone,
                        Character.valueOf('N'), NMItems.nickelPlate,
                        Character.valueOf('G'), NMItems.crystalPrecisionGear,
                        Character.valueOf('D'), NMItems.diamondStick,
                        Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_NICKEL_PLATE_4,
                NMSkillNodes.BRING_DIAMOND_STICK_16, NMSkillNodes.BRING_RAW_LITHIUM_64);
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(NMBlocks.cisternInterface), new Object[]{
                        "TNT", "RFR", "BAB",
                        Character.valueOf('T'), NMItems.tungstenIngot,
                        Character.valueOf('N'), NMItems.nickelPlate,
                        Character.valueOf('R'), NMItems.refinedRedstone,
                        Character.valueOf('F'), NMItems.fluidGauge,
                        Character.valueOf('B'), Block.netherBrick,
                        Character.valueOf('A'), NMItems.aquamarine}),
                NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_AQUAMARINE_16,
                NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_DYE_BLEND_16);
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(NMBlocks.cisternStirrer), new Object[]{
                        "AGA", "SIS", "NRN",
                        Character.valueOf('A'), BTWBlocks.axle,
                        Character.valueOf('G'), BTWBlocks.gearBox,
                        Character.valueOf('S'), BTWItems.screw,
                        Character.valueOf('I'), NMBlocks.cisternInterface,
                        Character.valueOf('N'), NMItems.nickelPlate,
                        Character.valueOf('R'), NMItems.refinedRedstone}),
                NMSkillNodes.BRING_SCREW_16, NMSkillNodes.BRING_GEAR_64,
                NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_NICKEL_PLATE_4);
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(NMBlocks.cisternDrain), new Object[]{
                        "NIN", "TRT", "SGS",
                        Character.valueOf('N'), NMItems.nickelPlate,
                        Character.valueOf('I'), NMBlocks.cisternInterface,
                        Character.valueOf('T'), NMItems.tungstenIngot,
                        Character.valueOf('R'), NMItems.refinedRedstone,
                        Character.valueOf('S'), BTWItems.screw,
                        Character.valueOf('G'), NMItems.glueSlurry}),
                NMSkillNodes.BRING_SCREW_16, NMSkillNodes.BRING_REDSTONE_BLOCK_16,
                NMSkillNodes.BRING_GLUE_SLURRY_16, NMSkillNodes.BRING_NICKEL_PLATE_4);
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(NMBlocks.chunkLoader), new Object[]{
                        "OTO", "TRT", "ODO",
                        Character.valueOf('O'), NMItems.obsidianBrick,
                        Character.valueOf('T'), NMItems.tungstenIngot,
                        Character.valueOf('R'), NMItems.refinedRedstone,
                        Character.valueOf('D'), NMItems.diamondBrick}),
                NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_DIAMOND_BRICK_4,
                NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16, NMSkillNodes.KILL_WITHER);
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.invocationSeal), new Object[]{NMItems.invocationFragment, NMItems.invocationFragment, NMItems.invocationFragment, NMItems.invocationFragment});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.endAccord), new Object[]{NMItems.endAccordFragment, NMItems.endAccordFragment, NMItems.endAccordFragment, NMItems.endAccordFragment});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.tungstenConcentrate), new Object[]{NMItems.crushedTungsten, Item.netherQuartz});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.pureTungstenChunk), new Object[]{NMItems.tungstenPowder, NMItems.tungstenPowder});
        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenIngot), new Object[]{"###", "###", "###", Character.valueOf('#'), NMItems.tungstenNugget});
        RecipeManager.addRecipe(new ItemStack(NMItems.tungstenBucket), new Object[]{"# #", " # ", Character.valueOf('#'), NMItems.tungstenIngot});
        // Item.cauldron deliberately places the IFHY cistern; BTWBlocks.cauldron remains the
        // separate cooking vessel used by cauldron recipes.
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.cauldron), new Object[]{"ISI", "ISI", "III", Character.valueOf('I'), NMItems.tungstenIngot, Character.valueOf('S'), BTWItems.netherSludge}), NMSkillNodes.BRING_TUNGSTEN_INGOT_8);
//        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.cauldron), new Object[]{"#Y#", "#X#", "###", Character.valueOf('#'), Item.ingotIron, Character.valueOf('X'), Item.bucketWater, Character.valueOf('Y'), Item.bone});
//        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWBlocks.cauldron), new Object[]{Item.cauldron, Item.bucketWater, Item.bone});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.cauldron), new Object[]{
                        "TBT", "TLT", "TTT",
                        Character.valueOf('T'), NMItems.tungstenIngot,
                        Character.valueOf('B'), Item.bone,
                        Character.valueOf('L'), NMItems.tungstenLavaBucket}),
                NMSkillNodes.BRING_TUNGSTEN_INGOT_8);
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
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(NMBlocks.chuteHopper), new Object[]{
                        "NSN", "HRH", "IGI",
                        Character.valueOf('N'), NMItems.nickelPlate,
                        Character.valueOf('S'), BTWItems.screw,
                        Character.valueOf('H'), BTWBlocks.hopper,
                        Character.valueOf('R'), NMItems.refinedRedstone,
                        Character.valueOf('I'), NMItems.ironBrick,
                        Character.valueOf('G'), BTWItems.glue}),
                NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_IRON_BRICK_64,
                NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_SCREW_16);
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.minecartPowered), new Object[]{"A", "B", Character.valueOf('A'), Block.furnaceIdle, Character.valueOf('B'), Item.minecartEmpty});
        RecipeManager.addRecipe(new ItemStack(Item.minecartPowered), new Object[]{"F", "C", Character.valueOf('F'), NMTags.netherCartFurnaces, Character.valueOf('C'), Item.minecartEmpty});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.highSpeedMinecart), new Object[]{Item.minecartEmpty, NMTags.highSpeedCartReinforcements});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.highSpeedChestMinecart), new Object[]{Item.minecartCrate, NMTags.highSpeedCartReinforcements});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.highSpeedFurnaceMinecart), new Object[]{Item.minecartPowered, NMTags.highSpeedCartReinforcements});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.drill), new Object[]{new ItemStack(BTWItems.pointyStick, 1, Short.MAX_VALUE), Item.stick, NMItems.crudeString, BTWItems.sawDust});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.stoneLeafRake), new Object[]{BTWTags.looseCobblestones, Item.stick, Item.stick, Item.stick});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.ironLeafRake), new Object[]{Item.ingotIron, Item.stick, Item.stick, Item.stick});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.diamondLeafRake), new Object[]{BTWItems.diamondIngot, Item.stick, Item.stick, Item.stick});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.shovelWood), new Object[]{BTWTags.logs, Item.stick, NMItems.primitiveGlue});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.woodHammer), new Object[]{BTWTags.logs, BTWTags.logs, Item.stick, NMItems.crudeString});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.bone), new Object[]{NMItems.boneShard,NMItems.boneShard,NMItems.boneShard,NMItems.boneShard});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.stoneHammer), new Object[]{BTWTags.looseCobblestones,BTWTags.looseCobblestones, Item.stick, Item.silk});

        RecipeManager.addRecipe(new ItemStack(NMItems.stoneHammer), new Object[]{
                "CCC", "CSC", "LSL",
                Character.valueOf('C'), BTWTags.looseCobblestones,
                Character.valueOf('S'), Item.stick,
                Character.valueOf('L'), Item.clay});

        RecipeManager.addRecipe(new ItemStack(Item.pickaxeStone), new Object[]{"CCC", "TSG", "LSL", Character.valueOf('C'), BTWTags.looseCobblestones, Character.valueOf('T'), Item.silk, Character.valueOf('S'), Item.stick, Character.valueOf('G'), NMItems.primitiveGlue, Character.valueOf('L'), Item.clay});
        RecipeManager.addRecipe(new ItemStack(Item.axeStone), new Object[]{"CCG", "CST", "LSL", Character.valueOf('C'), BTWTags.looseCobblestones, Character.valueOf('T'), Item.silk, Character.valueOf('S'), Item.stick, Character.valueOf('G'), NMItems.primitiveGlue, Character.valueOf('L'), Item.clay});
        RecipeManager.addRecipe(new ItemStack(Item.shovelStone), new Object[]{"LCL", "TSG", "LSL", Character.valueOf('C'), BTWTags.looseCobblestones, Character.valueOf('T'), Item.silk, Character.valueOf('S'), Item.stick, Character.valueOf('G'), NMItems.primitiveGlue, Character.valueOf('L'), Item.clay});

        RecipeManager.addShapelessRecipe(new ItemStack(Item.clay), new Object[]{BTWItems.clayPile, BTWItems.clayPile, BTWItems.clayPile, BTWItems.clayPile});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.flint), new Object[]{NMItems.flintChip, NMItems.flintChip, NMItems.flintChip, NMItems.flintChip});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.snowball), new Object[]{NMItems.snowPile, NMItems.snowPile, NMItems.snowPile, NMItems.snowPile});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.unshapedWetClayBrick, 1, NMItems.unshapedWetClayBrick.getMaxDamage() - 1), new Object[]{Item.clay, BTWItems.gravelPile, BTWItems.dirtPile, BTWItems.sandPile});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.cauldron), new Object[]{"I I", "I I", "III", Character.valueOf('I'), Item.ingotIron}), NMSkillNodes.BRING_IRON_INGOT_16);
        RecipeManager.addRecipe(new ItemStack(NMBlocks.stoneAnvil), new Object[]{"SSS", " S ", "SSS", Character.valueOf('S'), BTWTags.looseCobblestones});
        NMFoodSpoilage.addSnowRefreshRecipes();

        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.lithiumSalt, 2), new Object[]{new ItemStack(NMItems.lithiumRefined), new ItemStack(Item.sugar)});
        RecipeManager.addRecipe(new ItemStack(NMItems.lithiumHeatCompound), new Object[]{"NLN", "LCL", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.lithiumRefined, Character.valueOf('C'), NMItems.potassiumCrystal});

        RecipeManager.addRecipe(new ItemStack(NMItems.nickelBinding, 2), new Object[]{"NN", " S", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('S'), Item.silk});
        RecipeManager.addRecipe(new ItemStack(NMItems.oxygenTank), new Object[]{" N ", "NIN", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('I'), Item.ingotIron, Character.valueOf('L'), Item.leather});

        RecipeManager.addRecipe(new ItemStack(NMItems.crystalPrecisionGear), new Object[]{" C ", "CNC", " C ", Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('N'), NMItems.nickelMachinePart});

        RecipeManager.addRecipe(new ItemStack(NMItems.seededDiamondMatrix), new Object[]{" C ", "CDC", " C ", Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('D'), NMItems.stabilizedDiamondSlurry});
        RecipeManager.addRecipe(new ItemStack(NMItems.nickelBoundDiamondMatrix), new Object[]{" N ", "NDN", " S ", Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('D'), NMItems.seededDiamondMatrix, Character.valueOf('S'), NMItems.lithiumStabilizer});


        for (Item rawFish : NMItems.getRawFish()) {
            if (rawFish != NMItems.debonedRawFish) {
                RecipeManager.addShapelessRecipe(new ItemStack(NMItems.fishFlesh, 1, 99), new Object[]{new ItemStack(rawFish, 1, Short.MAX_VALUE)});
            }
        }
        CraftingManager.getInstance().getRecipeList().add(new FishingRodUpgradeRecipe(
                "fishing_bell_upgrade", NMItems.fishingBellUpgrade, "IfhyFishingBell"));
        CraftingManager.getInstance().getRecipeList().add(new FishingRodUpgradeRecipe(
                "fishing_lure_upgrade", NMItems.fishingLureUpgrade, "IfhyFishingLure"));
        CraftingManager.getInstance().getRecipeList().add(new FishingRodUpgradeRecipe(
                "fishing_auto_reel_upgrade", NMItems.fishingAutoReelUpgrade, "IfhyFishingAutoReel"));
        CraftingManager.getInstance().getRecipeList().add(new FishingRodUpgradeRecipe(
                "rare_fish_lure_upgrade", NMItems.rareFishLureUpgrade, "IfhyRareFishLure"));


        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.moistureFertilizer, 4), new Object[]{Item.bucketWater, BTWItems.dirtPile});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.potassiumFertilizer, 4), new Object[]{NMItems.potassiumCrystal, BTWItems.dirtPile});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.acidityFertilizer, 4), new Object[]{NMItems.acidCrystal, BTWItems.dirtPile});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.porosityFertilizer, 4), new Object[]{NMItems.porosityAggregate, BTWItems.dirtPile});
        RecipeManager.addShapelessRecipe(new ItemStack(Item.dyePowder, 4, 15), new Object[]{NMItems.nitrogenCrystal});
        RecipeManager.addShapelessRecipe(new ItemStack(NMItems.glassBatch, 2), new Object[]{NMItems.acidCrystal, NMItems.porosityAggregate, BTWItems.sandPile});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addShapelessRecipe(new ItemStack(Item.glassBottle), new Object[]{NMItems.soilSample}),
                NMSkillNodes.CRAFT_BOOK_64);

        Item[] extractorKeys = {Item.redstone, Item.dyePowder, Item.bucketEmpty, Item.flint, Item.fermentedSpiderEye};
        int[] extractorMetadata = {0, 15, 0, 0, 0};
        for (int type = 0; type < 5; ++type) {
            SkillLockedCrafting.requireSkills(
                    RecipeManager.addRecipe(new ItemStack(NMBlocks.terrainExtractor, 1, type), new Object[]{
                            "IRI", "GKG", "III",
                            Character.valueOf('I'), Item.ingotIron,
                            Character.valueOf('R'), Item.redstone,
                            Character.valueOf('G'), BTWItems.gear,
                            Character.valueOf('K'), new ItemStack(extractorKeys[type], 1, extractorMetadata[type])}),
                    NMSkillNodes.CRAFT_BOOK_64, NMSkillNodes.BRING_REDSTONE_16, NMSkillNodes.BRING_GEAR_64);
        }

        CraftingManager.getInstance().getRecipeList().add(new QuestToolRepairRecipe(
                "repair_farmers_hoe", NMItems.brokenHoeFragment, BTWItems.ironNugget, -1, NMItems.farmersFavoriteHoe));
        CraftingManager.getInstance().getRecipeList().add(new QuestToolRepairRecipe(
                "repair_blacksmith_pickaxe", NMItems.brokenPickaxeFragment, Item.diamond, -1, NMItems.blacksmithFavoritePickaxe));


        automationEssenceRecipe = RecipeManager.addShapelessRecipe(
                new ItemStack(NMItems.automationEssence),
                new Object[]{
                        BTWItems.gear,
                        BTWItems.redstoneLatch,
                        BTWItems.screw,
                        BTWBlocks.axle,
                        BTWBlocks.screwPump,
                        NMItems.crystalPrecisionGear,
                        BTWItems.belt,
                        Item.minecartEmpty,
                        BTWBlocks.detectorBlock
                });

        agrarianEssenceRecipe = RecipeManager.addShapelessRecipe(
                new ItemStack(NMItems.husbandryEssence),
                new Object[]{
                        BTWItems.hemp,
                        BTWItems.fabric,
                        BTWItems.tannedLeather,
                        BTWItems.dung,
                        BTWItems.flour,
                        BTWItems.rawEgg,
                        BTWItems.wheat,
                        Item.egg,
                        Block.hay
                });

        infernalEssenceRecipe = RecipeManager.addShapelessRecipe(
                new ItemStack(NMItems.infernalEssence),
                new Object[]{
                        BTWItems.hellfireDust,
                        BTWItems.concentratedHellfire,
                        BTWItems.soulUrn,
                        NMItems.denseNetherrackCore,
                        Item.enderPearl,
                        BTWItems.ocularOfEnder,
                        Item.netherStar,
                        new ItemStack(BTWItems.ancientProphecy, 1, Short.MAX_VALUE),
                        NMItems.endAccord});

        artisanEssenceRecipe = RecipeManager.addRecipe(
                new ItemStack(NMItems.artisanEssence),
                new Object[]{
                        "BCS",
                        "WGR",
                        "LAP",
                        Character.valueOf('B'), Item.brick,
                        Character.valueOf('C'), BTWItems.candle,
                        Character.valueOf('S'), BTWItems.soap,
                        Character.valueOf('W'), BTWItems.wickerPane,
                        Character.valueOf('G'), Block.glass,
                        Character.valueOf('R'), Item.record13,
                        Character.valueOf('L'), NMItems.crystalLens,
                        Character.valueOf('A'), Item.compass,
                        Character.valueOf('P'), NMItems.crystalPolishedShard});

        RecipeManager.addRecipe(new ItemStack(NMBlocks.enderCeramic, 1, 0), new Object[]{
                "CEC", "MPM", "CEC",
                Character.valueOf('C'), Item.clay,
                Character.valueOf('E'), NMItems.enderShellPowder,
                Character.valueOf('M'), NMItems.mercuryPowder,
                Character.valueOf('P'), BTWItems.enderSlag});

        addDeferredArmorRecipes();

        finishRecipes("Crafting Recipes");

    }

    /** Deferred sidegrades stay here so their chassis costs remain legible beside one another. */
    private static void addDeferredArmorRecipes() {
        RecipeManager.addRecipe(new ItemStack(NMItems.signalHelmet), new Object[]{
                "PPP",
                "PCP",
                "RER",
                Character.valueOf('P'), NMItems.signalAlloyPlate,
                Character.valueOf('R'), NMItems.redstoneCrystal,
                Character.valueOf('C'), new ItemStack(Item.helmetChain, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.refinedRedstone});
        RecipeManager.addRecipe(new ItemStack(NMItems.signalChestplate), new Object[]{
                "PEP",
                "PCP",
                "PPP",
                Character.valueOf('P'), NMItems.signalAlloyPlate,
                Character.valueOf('C'), new ItemStack(Item.plateChain, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.nickelBinding});
        RecipeManager.addRecipe(new ItemStack(NMItems.signalLeggings), new Object[]{
                "PPP",
                "PCP",
                "PEP",
                Character.valueOf('P'), NMItems.signalAlloyPlate,
                Character.valueOf('C'), new ItemStack(Item.legsChain, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.nickelBinding});
        RecipeManager.addRecipe(new ItemStack(NMItems.signalBoots), new Object[]{
                "PCP",
                "PEP",
                Character.valueOf('P'), NMItems.signalAlloyPlate,
                Character.valueOf('C'), new ItemStack(Item.bootsChain, 1, Short.MAX_VALUE),
                Character.valueOf('E'), Item.redstone});

        RecipeManager.addRecipe(new ItemStack(NMItems.azureHelmet), new Object[]{
                "PPP",
                "PCP",
                "AEA",
                Character.valueOf('P'), NMItems.azureCeramicPlate,
                Character.valueOf('A'), NMItems.aquamarine,
                Character.valueOf('C'), new ItemStack(Item.helmetGold, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.crystalLens});
        RecipeManager.addRecipe(new ItemStack(NMItems.azureChestplate), new Object[]{
                "PEP",
                "PCP",
                "PPP",
                Character.valueOf('P'), NMItems.azureCeramicPlate,
                Character.valueOf('C'), new ItemStack(Item.plateGold, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.aquamarine});
        RecipeManager.addRecipe(new ItemStack(NMItems.azureLeggings), new Object[]{
                "PPP",
                "PCP",
                "PEP",
                Character.valueOf('P'), NMItems.azureCeramicPlate,
                Character.valueOf('C'), new ItemStack(Item.legsGold, 1, Short.MAX_VALUE),
                Character.valueOf('E'), BTWItems.padding});
        RecipeManager.addRecipe(new ItemStack(NMItems.azureBoots), new Object[]{
                "PCP",
                "PEP",
                Character.valueOf('P'), NMItems.azureCeramicPlate,
                Character.valueOf('C'), new ItemStack(Item.bootsGold, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.nickelBinding});

        RecipeManager.addRecipe(new ItemStack(NMItems.crystalLatticeCharge), new Object[]{
                " C ",
                "CPC",
                " C ",
                Character.valueOf('C'), NMItems.crystalPolishedShard,
                Character.valueOf('P'), NMItems.nickelPlate});

        RecipeManager.addRecipe(new ItemStack(NMItems.prismaticHelmet), new Object[]{
                "PPP",
                "PCP",
                " E ",
                Character.valueOf('P'), NMItems.prismaticPlate,
                Character.valueOf('C'), new ItemStack(Item.helmetChain, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.crystalLens});
        RecipeManager.addRecipe(new ItemStack(NMItems.prismaticChestplate), new Object[]{
                "PEP",
                "PCP",
                "PPP",
                Character.valueOf('P'), NMItems.prismaticPlate,
                Character.valueOf('C'), new ItemStack(Item.plateChain, 1, Short.MAX_VALUE),
                Character.valueOf('E'), BTWItems.padding});
        RecipeManager.addRecipe(new ItemStack(NMItems.prismaticLeggings), new Object[]{
                "PPP",
                "PCP",
                "PEP",
                Character.valueOf('P'), NMItems.prismaticPlate,
                Character.valueOf('C'), new ItemStack(Item.legsChain, 1, Short.MAX_VALUE),
                Character.valueOf('E'), BTWItems.padding});
        RecipeManager.addRecipe(new ItemStack(NMItems.prismaticBoots), new Object[]{
                "PCP",
                "PEP",
                Character.valueOf('P'), NMItems.prismaticPlate,
                Character.valueOf('C'), new ItemStack(Item.bootsChain, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.nickelBinding});

        RecipeManager.addRecipe(new ItemStack(NMItems.verdantHelmet), new Object[]{
                "PPP",
                "PCP",
                " E ",
                Character.valueOf('P'), NMItems.verdantPlate,
                Character.valueOf('C'), new ItemStack(Item.helmetGold, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.crystalLens});
        RecipeManager.addRecipe(new ItemStack(NMItems.verdantChestplate), new Object[]{
                "PEP",
                "PCP",
                "PPP",
                Character.valueOf('P'), NMItems.verdantPlate,
                Character.valueOf('C'), new ItemStack(Item.plateGold, 1, Short.MAX_VALUE),
                Character.valueOf('E'), Item.emerald});
        RecipeManager.addRecipe(new ItemStack(NMItems.verdantLeggings), new Object[]{
                "PPP",
                "PCP",
                "PEP",
                Character.valueOf('P'), NMItems.verdantPlate,
                Character.valueOf('C'), new ItemStack(Item.legsGold, 1, Short.MAX_VALUE),
                Character.valueOf('E'), BTWItems.padding});
        RecipeManager.addRecipe(new ItemStack(NMItems.verdantBoots), new Object[]{
                "PCP",
                "PEP",
                Character.valueOf('P'), NMItems.verdantPlate,
                Character.valueOf('C'), new ItemStack(Item.bootsGold, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.nickelBinding});

        RecipeManager.addRecipe(new ItemStack(NMItems.blackglassHelmet), new Object[]{
                "PPP",
                "PCP",
                " E ",
                Character.valueOf('P'), NMItems.blackglassPlate,
                Character.valueOf('C'), new ItemStack(Item.helmetIron, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.nickelBinding});
        RecipeManager.addRecipe(new ItemStack(NMItems.blackglassChestplate), new Object[]{
                "PEP",
                "PCP",
                "PPP",
                Character.valueOf('P'), NMItems.blackglassPlate,
                Character.valueOf('C'), new ItemStack(Item.plateIron, 1, Short.MAX_VALUE),
                Character.valueOf('E'), BTWItems.padding});
        RecipeManager.addRecipe(new ItemStack(NMItems.blackglassLeggings), new Object[]{
                "PPP",
                "PCP",
                "PEP",
                Character.valueOf('P'), NMItems.blackglassPlate,
                Character.valueOf('C'), new ItemStack(Item.legsIron, 1, Short.MAX_VALUE),
                Character.valueOf('E'), BTWItems.padding});
        RecipeManager.addRecipe(new ItemStack(NMItems.blackglassBoots), new Object[]{
                "PCP",
                "PEP",

                Character.valueOf('P'), NMItems.blackglassPlate,
                Character.valueOf('C'), new ItemStack(Item.bootsIron, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.nickelBinding});
        RecipeManager.addRecipe(new ItemStack(NMItems.quartzglassHelmet), new Object[]{
                "PPP",
                "PCP",
                " E ",
                Character.valueOf('P'), NMItems.quartzglassPlate,
                Character.valueOf('C'), new ItemStack(NMItems.glassHelmet, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.refractoryCloth});
        RecipeManager.addRecipe(new ItemStack(NMItems.quartzglassChestplate), new Object[]{
                "PEP",
                "PCP",
                "PPP",
                Character.valueOf('P'), NMItems.quartzglassPlate,
                Character.valueOf('C'), new ItemStack(NMItems.glassChestplate, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.refractoryCloth});
        RecipeManager.addRecipe(new ItemStack(NMItems.quartzglassLeggings), new Object[]{
                "PPP",
                "PCP",
                "PEP",
                Character.valueOf('P'), NMItems.quartzglassPlate,
                Character.valueOf('C'), new ItemStack(NMItems.glassLeggings, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.refractoryCloth});
        RecipeManager.addRecipe(new ItemStack(NMItems.quartzglassBoots), new Object[]{
                "PCP",
                "PEP",

                Character.valueOf('P'), NMItems.quartzglassPlate,
                Character.valueOf('C'), new ItemStack(NMItems.glassBoots, 1, Short.MAX_VALUE),
                Character.valueOf('E'), NMItems.refractoryCloth});

        RecipeManager.addRecipe(new ItemStack(NMItems.refinedPrismaHelmet), new Object[]{
                "PBP", "DRG", "FHF",
                Character.valueOf('P'), NMItems.prismaticPlate, Character.valueOf('B'), new ItemStack(NMItems.bloodHelmet, 1, Short.MAX_VALUE),
                Character.valueOf('D'), NMItems.dyeBlend, Character.valueOf('R'), new ItemStack(NMItems.prismaticHelmet, 1, Short.MAX_VALUE),
                Character.valueOf('G'), NMItems.crystalPrecisionGear,
                Character.valueOf('H'), NMItems.lithiumHeatCompound, Character.valueOf('F'), Item.blazePowder});
        RecipeManager.addRecipe(new ItemStack(NMItems.refinedPrismaChestplate), new Object[]{
                "PBP", "DRG", "PHP",
                Character.valueOf('P'), NMItems.prismaticPlate, Character.valueOf('B'), new ItemStack(NMItems.bloodChestplate, 1, Short.MAX_VALUE),
                Character.valueOf('D'), NMItems.dyeBlend, Character.valueOf('R'), new ItemStack(NMItems.prismaticChestplate, 1, Short.MAX_VALUE),
                Character.valueOf('G'), NMItems.crystalPrecisionGear, Character.valueOf('H'), NMItems.lithiumHeatCompound});
        RecipeManager.addRecipe(new ItemStack(NMItems.refinedPrismaLeggings), new Object[]{
                "PBP", "DRG", "PHP",
                Character.valueOf('P'), NMItems.prismaticPlate, Character.valueOf('B'), new ItemStack(NMItems.bloodLeggings, 1, Short.MAX_VALUE),
                Character.valueOf('D'), NMItems.dyeBlend, Character.valueOf('R'), new ItemStack(NMItems.prismaticLeggings, 1, Short.MAX_VALUE),
                Character.valueOf('G'), NMItems.crystalPrecisionGear,
                Character.valueOf('H'), NMItems.lithiumHeatCompound});
        RecipeManager.addRecipe(new ItemStack(NMItems.refinedPrismaBoots), new Object[]{
                "PBP", "DRG", "FHF",
                Character.valueOf('P'), NMItems.prismaticPlate, Character.valueOf('B'), new ItemStack(NMItems.bloodBoots, 1, Short.MAX_VALUE),
                Character.valueOf('D'), NMItems.dyeBlend, Character.valueOf('R'), new ItemStack(NMItems.prismaticBoots, 1, Short.MAX_VALUE),
                Character.valueOf('G'), NMItems.crystalPrecisionGear,
                Character.valueOf('H'), NMItems.lithiumHeatCompound, Character.valueOf('F'), Item.blazePowder});

        RecipeManager.addRecipe(new ItemStack(NMItems.glassHelmet), new Object[]{
                "GSG", "XGX", "G G",
                Character.valueOf('G'), Block.glass, Character.valueOf('S'), BTWItems.leatherStrap, Character.valueOf('X'), NMItems.primitiveGlue});
        RecipeManager.addRecipe(new ItemStack(NMItems.glassChestplate), new Object[]{
                "G G", "GXG", "SXS",
                Character.valueOf('G'), Block.glass, Character.valueOf('S'), BTWItems.leatherStrap, Character.valueOf('X'), NMItems.primitiveGlue});
        RecipeManager.addRecipe(new ItemStack(NMItems.glassLeggings), new Object[]{
                "GSG", "GXG", "G G",
                Character.valueOf('G'), Block.glass, Character.valueOf('S'), BTWItems.leatherStrap, Character.valueOf('X'), NMItems.primitiveGlue});
        RecipeManager.addRecipe(new ItemStack(NMItems.glassBoots), new Object[]{
                "G G", "XSX",
                Character.valueOf('G'), Block.glass, Character.valueOf('S'), BTWItems.leatherStrap, Character.valueOf('X'), NMItems.primitiveGlue});

        RecipeManager.addRecipe(new ItemStack(NMItems.darkHelmet), new Object[]{
                "DDD", "DZD", "SCG",
                Character.valueOf('D'), NMItems.darkIngot, Character.valueOf('Z'), new ItemStack(NMItems.deadzoneHelmet, 1, Short.MAX_VALUE),
                Character.valueOf('S'), new ItemStack(BTWItems.plateHelmet, 1, Short.MAX_VALUE), Character.valueOf('C'), NMItems.deadzoneShard,
                Character.valueOf('G'), NMItems.crystalPrecisionGear});
        RecipeManager.addRecipe(new ItemStack(NMItems.darkChestplate), new Object[]{
                "DZD", "DSD", "DDD",
                Character.valueOf('D'), NMItems.darkIngot, Character.valueOf('Z'), new ItemStack(NMItems.deadzoneChestplate, 1, Short.MAX_VALUE),
                Character.valueOf('S'), new ItemStack(BTWItems.plateBreastplate, 1, Short.MAX_VALUE), Character.valueOf('C'), NMItems.deadzoneShard,});
        RecipeManager.addRecipe(new ItemStack(NMItems.darkLeggings), new Object[]{
                "DDD", "GZG", "DCD",
                Character.valueOf('D'), NMItems.darkIngot, Character.valueOf('Z'), new ItemStack(NMItems.deadzoneLeggings, 1, Short.MAX_VALUE),
                Character.valueOf('S'), new ItemStack(BTWItems.plateLeggings, 1, Short.MAX_VALUE), Character.valueOf('C'), NMItems.deadzoneShard,
                Character.valueOf('G'), NMItems.crystalPrecisionGear});
        RecipeManager.addRecipe(new ItemStack(NMItems.darkBoots), new Object[]{
                "DZD", "DSD","GCG",
                Character.valueOf('D'), NMItems.darkIngot, Character.valueOf('Z'), new ItemStack(NMItems.deadzoneBoots, 1, Short.MAX_VALUE),
                Character.valueOf('S'), new ItemStack(BTWItems.plateBoots, 1, Short.MAX_VALUE), Character.valueOf('C'), NMItems.deadzoneShard,
                Character.valueOf('G'), NMItems.crystalPrecisionGear});
    }

    private static void addSkillLockedRecipes(){
        RecipeManager.addShapelessRecipe(
                new ItemStack(NMItems.skillBook),
                new Object[]{new ItemStack(Item.leather), new ItemStack(Item.dyePowder, 1, 0), new ItemStack(NMItems.twig), new ItemStack(NMItems.driedPlantFiber)});


        SkillLockedCrafting.requireSkill(
                RecipeManager.addShapelessRecipe(
                        new ItemStack(NMItems.stringCrafting, 1, NMItems.stringCrafting.getMaxDamage() - 1),
                        new Object[]{NMItems.crudeString, NMItems.spiderSilk, NMItems.primitiveGlue}),
                NMSkillNodes.BRING_SPIDER_SILK_2);

        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(
                        new ItemStack(BTWItems.bowDrill),
                        new Object[]{"ST", "SD", Character.valueOf('S'), Item.stick, Character.valueOf('T'), BTWTags.strings, Character.valueOf('D'), NMItems.drill}),
                NMSkillNodes.BRING_BURNING_CRUDE_TORCH, NMSkillNodes.BRING_DRILL_1, NMSkillNodes.BRING_FLINT_4);

        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(
                        new ItemStack(BTWBlocks.unlitCampfire),
                        new Object[]{"##", "##", Character.valueOf('#'), NMItems.pileOfSticks}),
                NMSkillNodes.BRING_SAWDUST_16);

        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(
                        new ItemStack(BTWBlocks.idleLooseOven),
                        new Object[]{"##", "##", Character.valueOf('#'), NMItems.ovenPart}),
                NMSkillNodes.BRING_RAW_PORKCHOP_16, NMSkillNodes.BRING_CLAY_BALL_32, NMSkillNodes.KILL_MOB_16, NMSkillNodes.TAME_ANIMAL_1, NMSkillNodes.BRING_DRILL_1);

        RecipeManager.removeVanillaRecipe(
                new ItemStack(BTWItems.woodenClub),
                new Object[]{"X", "X", Character.valueOf('X'), Item.stick});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(
                        new ItemStack(BTWItems.woodenClub),
                        new Object[]{"XY", "X", Character.valueOf('X'), Item.stick, Character.valueOf('Y'), Item.silk}),
                NMSkillNodes.BRING_STICK_16,
                NMSkillNodes.KILL_MOB_16,
                NMSkillNodes.BRING_LOG_64);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.boneCarving, 1, 599), new Object[]{new ItemStack(Item.bone)});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.boneCarving, 1, 599), new Object[]{new ItemStack(Item.bone)}),
                NMSkillNodes.BRING_BONE_CLUB_4);

        SkillLockedCrafting.requireSkills(
                RecipeManager.addShapelessRecipe(new ItemStack(Item.flintAndSteel), new Object[]{new ItemStack(Item.flint), new ItemStack(Item.flint), new ItemStack(BTWItems.blastingOil), NMTags.ironTungstenIngots}),
                NMSkillNodes.BRING_NETHER_WART_64, NMSkillNodes.CRAFT_CAULDRON, NMSkillNodes.CRAFT_UNIQUE_RECIPE_OUTPUT_64);



        RecipeManager.removeVanillaRecipe(
                new ItemStack(BTWItems.boneClub),
                new Object[]{"X", "X", Character.valueOf('X'), Item.bone});
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(
                        new ItemStack(BTWItems.boneClub),
                        new Object[]{"X", "X", Character.valueOf('X'), Item.bone}),
                NMSkillNodes.BRING_STICK_16,
                NMSkillNodes.KILL_MOB_16,
                NMSkillNodes.BRING_BONE_128);

        RecipeManager.removeVanillaRecipe(
                new ItemStack(BTWBlocks.dirtSlab, 4),
                new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.dirt)});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(
                        new ItemStack(BTWBlocks.dirtSlab, 4),
                        new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.dirt)}),
                NMSkillNodes.JUMP_1000);

        RecipeManager.removeVanillaShapelessRecipe(
                new ItemStack(BTWBlocks.sandAndGravelSlab, 1, 0),
                new Object[]{new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile)});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addShapelessRecipe(
                        new ItemStack(BTWBlocks.sandAndGravelSlab, 1, 0),
                        new Object[]{new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile), new ItemStack(BTWItems.gravelPile)}),
                NMSkillNodes.JUMP_1000);
        RecipeManager.removeVanillaRecipe(
                new ItemStack(BTWBlocks.sandAndGravelSlab, 4, 0),
                new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.gravel)});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(
                        new ItemStack(BTWBlocks.sandAndGravelSlab, 4, 0),
                        new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.gravel)}),
                NMSkillNodes.JUMP_1000);

        RecipeManager.removeVanillaShapelessRecipe(
                new ItemStack(BTWBlocks.sandAndGravelSlab, 1, 1),
                new Object[]{new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile)});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addShapelessRecipe(
                        new ItemStack(BTWBlocks.sandAndGravelSlab, 1, 1),
                        new Object[]{new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile), new ItemStack(BTWItems.sandPile)}),
                NMSkillNodes.JUMP_1000);
        RecipeManager.removeVanillaRecipe(
                new ItemStack(BTWBlocks.sandAndGravelSlab, 4, 1),
                new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.sand)});
        SkillLockedCrafting.requireSkill(
                RecipeManager.addRecipe(
                        new ItemStack(BTWBlocks.sandAndGravelSlab, 4, 1),
                        new Object[]{"##", Character.valueOf('#'), new ItemStack(Block.sand)}),
                NMSkillNodes.JUMP_1000);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.handCrank), new Object[]{"  Y", " Y ", "#X#", Character.valueOf('#'), BTWTags.stoneBrickItems, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), Item.stick});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(BTWBlocks.handCrank), new Object[]{" G ", "SGS", "###", Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('S'), NMTags.netherCompatibleSticks, Character.valueOf('#'), NMTags.netherKilnMasonry}),
                NMSkillNodes.BRING_WOODEN_GEAR_12);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.millstone), new Object[]{"YYY", "YYY", "YXY", Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), BTWTags.stoneBrickItems});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(BTWBlocks.millstone), new Object[]{"SGS", "SSS", "SGS", Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('S'), NMTags.netherKilnMasonry}),
                NMSkillNodes.BRING_WOODEN_GEAR_12);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.windMill), new Object[]{" # ", "# #", " # ", Character.valueOf('#'), BTWItems.windMillBlade});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.windMill), new Object[]{" # ", "# #", " # ", Character.valueOf('#'), BTWItems.windMillBlade}),
                NMSkillNodes.BRING_WOODEN_GEAR_12, NMSkillNodes.BRING_WINDMILL_BLADE_8);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.saw), new Object[]{"YYY", "XZX", "#X#", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), Item.ingotIron, Character.valueOf('Z'), BTWItems.belt});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.saw), new Object[]{"III", "GBG", "SPS", Character.valueOf('I'), NMTags.ironTungstenIngots, Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('B'), BTWItems.belt, Character.valueOf('S'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('P'), NMItems.nickelMachinePart}),
                NMSkillNodes.BRING_WOODEN_GEAR_12, NMSkillNodes.BRING_LEATHER_16);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.gearBox), new Object[]{"#X#", "XYX", "#X#", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), BTWBlocks.axle});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.gearBox), new Object[]{"SGS", "GAG", "SGS", Character.valueOf('S'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('A'), BTWBlocks.axle}),
                NMSkillNodes.BRING_WOODEN_GEAR_12);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.waterWheel), new Object[]{"###", "# #", "###", Character.valueOf('#'), BTWItems.woodenBlade});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.waterWheel), new Object[]{"BPB", "B B", "BNB", Character.valueOf('B'), BTWItems.woodenBlade, Character.valueOf('P'), NMItems.crystalPrecisionGear, Character.valueOf('N'), NMItems.nickelMachinePart}),
                NMSkillNodes.BRING_WINDMILL_BLADE_8, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2, NMSkillNodes.BRING_REDSTONE_256);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pocketSundial), new Object[]{" # ", "#X#", " # ", Character.valueOf('#'), Item.goldNugget, Character.valueOf('X'), Item.netherQuartz});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.pocketSundial), new Object[]{"GCG", "GQG", "GPG", Character.valueOf('G'), Item.goldNugget, Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('Q'), Item.netherQuartz, Character.valueOf('P'), NMItems.crystalPrecisionGear}),
                NMSkillNodes.BRING_GOLD_ORE_PILE_32, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.redstoneLatch), new Object[]{"ggg", " r ", Character.valueOf('g'), Item.goldNugget, Character.valueOf('r'), Item.redstone});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.redstoneLatch), new Object[]{"GCG", "GRG", "GNG", Character.valueOf('G'), Item.goldNugget, Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('R'), NMItems.refinedRedstone, Character.valueOf('N'), NMItems.nickelPlate}),
                NMSkillNodes.BRING_GOLD_ORE_PILE_32, NMSkillNodes.BRING_REFINED_REDSTONE_16, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.redstoneClutch), new Object[]{"#X#", "XYX", "#X#", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), BTWItems.redstoneLatch});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.redstoneClutch), new Object[]{"SPS", "GLG", "SNS", Character.valueOf('S'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('P'), NMItems.crystalPrecisionGear, Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('L'), BTWItems.redstoneLatch, Character.valueOf('N'), NMItems.nickelMachinePart}),
                NMSkillNodes.BRING_REFINED_REDSTONE_16, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2, NMSkillNodes.BRING_REDSTONE_256);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.redstoneRepeater), new Object[]{"#X#", "III", Character.valueOf('#'), Block.torchRedstoneActive, Character.valueOf('X'), Item.pocketSundial, Character.valueOf('I'), BTWTags.stoneBrickItems});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.redstoneRepeater), new Object[]{"TRT", "PCP", "SNS", Character.valueOf('T'), Block.torchRedstoneActive, Character.valueOf('R'), NMItems.refinedRedstone, Character.valueOf('P'), NMItems.crystalPolishedShard, Character.valueOf('C'), Item.pocketSundial, Character.valueOf('S'), BTWTags.stoneBrickItems, Character.valueOf('N'), NMItems.nickelPlate}),
                NMSkillNodes.BRING_REFINED_REDSTONE_16, NMSkillNodes.BRING_GOLD_ORE_PILE_32, NMSkillNodes.BRING_GLASS_64);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.comparator), new Object[]{" R ", "RER", "SSS", Character.valueOf('E'), BTWItems.redstoneEye, Character.valueOf('S'), BTWTags.stoneBrickItems, Character.valueOf('R'), Block.torchRedstoneActive});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.comparator), new Object[]{"TRT", "LEL", "SNS", Character.valueOf('T'), Block.torchRedstoneActive, Character.valueOf('R'), NMItems.refinedRedstone, Character.valueOf('L'), NMItems.crystalLens, Character.valueOf('E'), BTWItems.redstoneEye, Character.valueOf('S'), BTWTags.stoneBrickItems, Character.valueOf('N'), NMItems.nickelMachinePart}),
                NMSkillNodes.BRING_REFINED_REDSTONE_16, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.redstoneEye, 2), new Object[]{"###", "GGG", " R ", Character.valueOf('#'), new ItemStack(Item.dyePowder, 1, 4), Character.valueOf('G'), Item.goldNugget, Character.valueOf('R'), Item.redstone});

        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(
                        new ItemStack(NMItems.flintAxeCrafting, 1, NMItems.flintAxeCrafting.getMaxDamage() - 1),
                        new Object[]{Item.flint, Item.flint, Item.stick, NMItems.crudeString}),
                NMSkillNodes.BRING_FLINT_4);

        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.nickelHeatComponent), new Object[]{
                " N ", "NLN", " N ", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.BRING_NICKEL_PLATE_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.lithiumHeatCompound), new Object[]{"NLN", "LCL", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.lithiumRefined, Character.valueOf('C'), Block.sand});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.lithiumHeatCompound), new Object[]{"NLN", "LCL", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.lithiumRefined, Character.valueOf('C'), Block.sand}),
                NMSkillNodes.CRAFT_CAULDRON, NMSkillNodes.BRING_NICKEL_PLATE_4);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.coal), new Object[]{new ItemStack(BTWItems.coalDust), new ItemStack(BTWItems.coalDust)});
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(Item.coal), new Object[]{new ItemStack(BTWItems.coalDust), new ItemStack(BTWItems.coalDust)}),
                NMSkillNodes.BRING_COAL_DUST_64);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.ingotIron), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(BTWItems.ironNugget)});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.ingotIron), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(BTWItems.ironNugget), Character.valueOf('X'), new ItemStack(NMItems.hammeredStoneBrick)}),
                NMSkillNodes.BRING_IRON_BLOOM_8, NMSkillNodes.BRING_IRON_HELMET, NMSkillNodes.BRING_IRON_CHESTPLATE,
                NMSkillNodes.BRING_IRON_LEGGINGS, NMSkillNodes.BRING_IRON_BOOTS, NMSkillNodes.BRING_IRON_NUGGET_32,
                NMSkillNodes.BRING_IRON_SWORD);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.bedroll), new Object[]{BTWTags.knitWools, BTWTags.knitWools, BTWTags.strings});
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.bedroll), new Object[]{BTWTags.knitWools,BTWTags.knitWools,BTWTags.knitWools,BTWTags.knitWools,BTWTags.knitWools, BTWTags.knitWools, BTWTags.strings, BTWItems.padding, BTWItems.padding}),
                NMSkillNodes.BRING_WOOL_128);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.chickenFeed), new Object[]{new ItemStack(Item.dyePowder, 1, 15), BTWTags.seeds});
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.chickenFeed), new Object[]{new ItemStack(Item.dyePowder, 1, 15), BTWTags.seeds}),
                NMSkillNodes.BRING_FEATHER_64);

        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(NMItems.lithiumSalt, 3), new Object[]{new ItemStack(NMItems.lithiumRefined), new ItemStack(Item.reed)}),
                NMSkillNodes.BRING_SUGAR_CANE_256);
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(Item.cake), new Object[]{"AAA", "BEB", "CCC", Character.valueOf('A'), Item.bucketMilk, Character.valueOf('B'), Item.sugar, Character.valueOf('C'), Item.wheat, Character.valueOf('E'), Item.egg}),
                NMSkillNodes.MILK_COW_100);
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.straw), new Object[]{new ItemStack(NMItems.plantFiber)}),
                NMSkillNodes.BRING_PLANT_FIBER_1024);
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.oxygenMask), new Object[]{"NGN", "L L", Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('G'), Block.glass, Character.valueOf('L'), Item.leather}),
                NMSkillNodes.BRING_DRIED_PLANT_FIBER_300);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.oxygenTank), new Object[]{" N ", "NIN", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('I'), Item.ingotIron, Character.valueOf('L'), Item.leather});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.oxygenTank), new Object[]{" N ", "NIN", "NLN", Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('I'), Item.ingotIron, Character.valueOf('L'), Item.leather}),
                NMSkillNodes.BRING_DRIED_PLANT_FIBER_300, NMSkillNodes.BRING_NICKEL_PLATE_4);

        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.nickelMachinePart), new Object[]{
                " N ", "NIN", " R ",
                        Character.valueOf('N'), NMItems.nickelIngot,
                        Character.valueOf('I'), NMTags.ironTungstenIngots,
                        Character.valueOf('R'), Item.redstone}),
                NMSkillNodes.BRING_REDSTONE_256);
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.crystalLens), new Object[]{" G ", "GCG", " G ", Character.valueOf('G'), Block.glass, Character.valueOf('C'), NMItems.crystalPolishedShard}),
                NMSkillNodes.BRING_GLASS_64);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.crystalPrecisionGear), new Object[]{" C ", "CNC", " C ", Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('N'), NMItems.nickelMachinePart});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.crystalPrecisionGear), new Object[]{" C ", "CNC", " C ", Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('N'), NMItems.nickelMachinePart}),
                NMSkillNodes.BRING_GLASS_64, NMSkillNodes.BRING_REDSTONE_256);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.seededDiamondMatrix), new Object[]{" C ", "CDC", " C ", Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('D'), NMItems.stabilizedDiamondSlurry});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.seededDiamondMatrix), new Object[]{" C ", "CDC", " C ", Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('D'), NMItems.stabilizedDiamondSlurry}),
                NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.CRAFT_CAULDRON);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.nickelBoundDiamondMatrix), new Object[]{" N ", "NDN", " S ", Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('D'), NMItems.seededDiamondMatrix, Character.valueOf('S'), NMItems.lithiumStabilizer});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.nickelBoundDiamondMatrix), new Object[]{" N ", "NDN", " S ", Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('D'), NMItems.seededDiamondMatrix, Character.valueOf('S'), NMItems.lithiumStabilizer}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMItems.hydraulicLens), new Object[]{NMItems.aquamarine, NMItems.crystalPolishedShard, Block.glass});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMItems.hydraulicLens), new Object[]{NMItems.aquamarine, NMItems.crystalPolishedShard, NMItems.crystalPolishedShard, Block.glass, NMItems.lithiumRefined}),
                NMSkillNodes.BRING_GLASS_64, NMSkillNodes.CRAFT_CAULDRON);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMItems.fluidGauge), new Object[]{NMItems.aquamarine, NMItems.nickelPlate, NMItems.hydraulicLens});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMItems.fluidGauge), new Object[]{NMItems.aquamarine, NMItems.nickelPlate, NMItems.hydraulicLens, NMItems.lithiumHeatCompound, NMItems.crystalLens}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4, NMSkillNodes.BRING_REDSTONE_256);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMBlocks.minerDrill), new Object[]{Block.netherrack, NMItems.tungstenIngot, Item.redstone});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.minerDrill), new Object[]{Block.netherrack, NMItems.tungstenIngot, Item.redstone, NMItems.nickelMachinePart, NMItems.lithiumHeatCompound}),
                NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16, NMSkillNodes.BRING_REDSTONE_256);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMBlocks.cisternInterface), new Object[]{NMItems.tungstenIngot, NMItems.tungstenIngot, Item.redstone, Block.netherBrick});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.cisternInterface), new Object[]{NMItems.tungstenIngot, NMItems.tungstenIngot, Item.redstone, Block.netherBrick, NMItems.nickelMachinePart, NMItems.crystalLens}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4, NMSkillNodes.BRING_REDSTONE_256);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMBlocks.cisternStirrer), new Object[]{NMBlocks.cisternInterface, BTWBlocks.gearBox, BTWBlocks.axle, Item.redstone});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.cisternStirrer), new Object[]{NMBlocks.cisternInterface, BTWBlocks.gearBox, BTWBlocks.axle, Item.redstone, NMItems.nickelHeatComponent, NMItems.crystalPrecisionGear}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(NMBlocks.cisternDrain), new Object[]{NMBlocks.cisternInterface, NMItems.tungstenIngot, Item.redstone});
        SkillLockedCrafting.requireSkills(RecipeManager.addShapelessRecipe(new ItemStack(NMBlocks.cisternDrain), new Object[]{NMBlocks.cisternInterface, NMItems.tungstenIngot, Item.redstone, NMItems.fluidGauge, NMItems.nickelBinding}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4, NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.chunkLoader), new Object[]{"OTO", "TRT", "OTO", Character.valueOf('O'), NMItems.obsidianBrick, Character.valueOf('T'), NMItems.tungstenIngot, Character.valueOf('R'), NMItems.refinedRedstone});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMBlocks.chunkLoader), new Object[]{"OTO", "TRT", "OPO", Character.valueOf('O'), NMItems.obsidianBrick, Character.valueOf('T'), NMItems.tungstenIngot, Character.valueOf('R'), NMItems.refinedRedstone, Character.valueOf('P'), NMItems.crystalPrecisionGear}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4, NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.soulforgedSteelIngot), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(BTWItems.steelNugget)});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(BTWItems.soulforgedSteelIngot), new Object[]{"###", "###", "###", Character.valueOf('#'), new ItemStack(BTWItems.steelNugget)}),
                NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.steelArmorPlate), new Object[]{"#X#", " Y ", Character.valueOf('#'), BTWItems.leatherStrap, Character.valueOf('X'), BTWItems.soulforgedSteelIngot, Character.valueOf('Y'), BTWItems.padding});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.steelArmorPlate), new Object[]{"#X#", "NYL", Character.valueOf('#'), BTWItems.leatherStrap, Character.valueOf('X'), BTWItems.soulforgedSteelIngot, Character.valueOf('Y'), BTWItems.padding, Character.valueOf('N'), NMItems.nickelPlate, Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16, NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_8);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodSword), new Object[]{" # ", "###", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodPickaxe), new Object[]{"###", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodAxe), new Object[]{"#  ", "#X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodShovel), new Object[]{" # ", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});
        RecipeManager.removeVanillaRecipe(new ItemStack(NMItems.bloodHoe), new Object[]{"#X ", " X ", " X ", Character.valueOf('#'), new ItemStack(NMItems.bloodIngot), Character.valueOf('X'), new ItemStack(Item.blazeRod)});

        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodHelmet), new Object[]{"BCB", "BNB", "L L", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('C'), NMItems.crystalLens, Character.valueOf('L'), NMItems.lithiumStabilizer, Character.valueOf('N'), NMItems.nickelBinding}),
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4, NMSkillNodes.BRING_DIAMOND_INGOT_2);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodChestplate), new Object[]{"BNB", "BGB", "BHB", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('G'), NMItems.crystalPrecisionGear, Character.valueOf('H'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4, NMSkillNodes.BRING_DIAMOND_INGOT_2);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodLeggings), new Object[]{"BBB", "BGB", "LNL", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('G'), NMItems.crystalPrecisionGear, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('L'), NMItems.lithiumStabilizer}),
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4, NMSkillNodes.BRING_DIAMOND_INGOT_2);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodBoots), new Object[]{"B B", "BHB", "N N", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('H'), NMItems.lithiumHeatCompound, Character.valueOf('N'), NMItems.nickelBinding}),
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4, NMSkillNodes.BRING_DIAMOND_INGOT_2);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodSword), new Object[]{" B ", "CBC", "NH ", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('H'), Item.blazeRod}),
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_DIAMOND_INGOT_2);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodPickaxe), new Object[]{"BBB", "NHP", "LH ", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), NMItems.crystalPrecisionGear, Character.valueOf('L'), NMItems.lithiumHeatCompound, Character.valueOf('H'), Item.blazeRod}),
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_DIAMOND_INGOT_2);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodAxe), new Object[]{"BBN", "BHP", "LH ", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelHeatComponent, Character.valueOf('P'), NMItems.crystalPrecisionGear, Character.valueOf('L'), NMItems.lithiumStabilizer, Character.valueOf('H'), Item.blazeRod}),
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_DIAMOND_INGOT_2);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodShovel), new Object[]{" B ", "NHP", "LH ", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('P'), NMItems.crystalPrecisionGear, Character.valueOf('L'), NMItems.lithiumStabilizer, Character.valueOf('H'), Item.blazeRod}),
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_DIAMOND_INGOT_2);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMItems.bloodHoe), new Object[]{"BBN", "PH ", "LH ", Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('P'), NMItems.crystalPrecisionGear, Character.valueOf('L'), NMItems.lithiumStabilizer, Character.valueOf('H'), Item.blazeRod}),
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_DIAMOND_INGOT_2);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.bloodChest), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.bloodOrb), Character.valueOf('X'), new ItemStack(BTWBlocks.chest)});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMBlocks.bloodChest), new Object[]{"OBO", "NCN", "PLP", Character.valueOf('O'), NMItems.bloodOrb, Character.valueOf('B'), NMItems.bloodIngot, Character.valueOf('N'), NMItems.nickelBinding, Character.valueOf('C'), BTWBlocks.chest, Character.valueOf('L'), NMItems.lithiumStabilizer, Character.valueOf('P'), NMItems.crystalPrecisionGear}),
                NMSkillNodes.BRING_BLOOD_ORB_128_II, NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.steelLocker), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.steelBunch), Character.valueOf('X'), new ItemStack(NMBlocks.bloodChest)});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMBlocks.steelLocker), new Object[]{"SDS", "LBL", "SPS", Character.valueOf('S'), NMItems.steelBunch, Character.valueOf('D'), NMItems.deadzoneShard, Character.valueOf('L'), NMItems.lithiumHeatCompound, Character.valueOf('B'), NMBlocks.bloodChest, Character.valueOf('P'), NMItems.crystalPrecisionGear}),
                NMSkillNodes.BRING_STEEL_BUNCH_8, NMSkillNodes.BRING_BLOOD_ORB_128_II, NMSkillNodes.BRING_DEADZONE_SHARD_64, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_8);

        RecipeManager.removeVanillaRecipe(new ItemStack(NMBlocks.blockAsphalt, 8), new Object[]{"XXX", "XYX", "XXX", Character.valueOf('X'), NMBlocks.blockRoad, Character.valueOf('Y'), BTWItems.soulUrn});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMBlocks.blockAsphalt, 4), new Object[]{"RXR", "RUR", "RNR", Character.valueOf('R'), NMBlocks.blockRoad, Character.valueOf('X'), NMItems.lithiumHeatCompound, Character.valueOf('U'), BTWItems.soulUrn, Character.valueOf('N'), NMItems.nickelHeatComponent}),
                NMSkillNodes.BRING_ROAD_BLOCK_64, NMSkillNodes.BRING_HEAT_RESISTANT_NICKEL_COMPONENT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), Item.ingotIron});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), BTWItems.ironNugget});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), NMTags.ironTungstenIngots}),
                NMSkillNodes.BRING_ITEM_FRAME_27);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XYX", "###", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), Item.book, Character.valueOf('Y'), Item.enchantedBook});
        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XYX", "###", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), Item.book, Character.valueOf('Y'), Item.enchantedBook}),
                NMSkillNodes.BRING_WRITTEN_BOOK_3);

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.writableBook), new Object[]{Item.paper, Item.paper, Item.paper, BTWTags.rawLeathers, new ItemStack(Item.dyePowder, 1, 0), Item.feather});
        SkillLockedCrafting.requireSkill(RecipeManager.addShapelessRecipe(new ItemStack(Item.writableBook), new Object[]{Item.paper, Item.paper, Item.paper, BTWTags.rawLeathers, new ItemStack(Item.dyePowder, 1, 0), Item.feather}),
                NMSkillNodes.BRING_PAPER_64);

        SkillLockedCrafting.requireSkill(RecipeManager.addRecipe(new ItemStack(NMItems.lithiumStabilizer), new Object[]{" C ", "LCL", " C ", Character.valueOf('L'), NMItems.lithiumSalt, Character.valueOf('C'), Item.clay}),
                NMSkillNodes.CRAFT_CAULDRON);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.dynamite, 2), new Object[]{"PF", "PN", "PS", Character.valueOf('P'), Item.paper, Character.valueOf('F'), BTWItems.fuse, Character.valueOf('N'), BTWItems.blastingOil, Character.valueOf('S'), BTWTags.sawdusts});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.dynamite), new Object[]{"PFC", "PON", "PSL", Character.valueOf('P'), Item.paper, Character.valueOf('F'), BTWItems.fuse, Character.valueOf('C'), NMItems.crystalPolishedShard, Character.valueOf('O'), BTWItems.blastingOil, Character.valueOf('N'), BTWItems.nitre, Character.valueOf('S'), BTWTags.sawdusts, Character.valueOf('L'), NMItems.lithiumSalt}),
                NMSkillNodes.BRING_GUNPOWDER_256, NMSkillNodes.BRING_GUNPOWDER_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.tnt), new Object[]{"GFG", "GBG", "GGG", Character.valueOf('B'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 11), Character.valueOf('G'), Item.gunpowder, Character.valueOf('F'), BTWItems.fuse});
        RecipeManager.removeVanillaRecipe(new ItemStack(Block.tnt), new Object[]{"GFG", "GBG", "NGN", Character.valueOf('B'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 11), Character.valueOf('G'), Item.gunpowder, Character.valueOf('N'), BTWItems.nitre, Character.valueOf('F'), BTWItems.fuse});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Block.tnt), new Object[]{"GFG", "DBD", "NGN", Character.valueOf('B'), new ItemStack(BTWBlocks.aestheticOpaque, 1, 11), Character.valueOf('G'), Item.gunpowder, Character.valueOf('D'), BTWItems.dynamite, Character.valueOf('N'), BTWItems.nitre, Character.valueOf('F'), BTWItems.fuse}),
                NMSkillNodes.BRING_GUNPOWDER_256, NMSkillNodes.BRING_GUNPOWDER_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.infernalEnchanter), new Object[]{"CBC", "SES", "SSS", Character.valueOf('S'), BTWItems.soulforgedSteelIngot, Character.valueOf('C'), new ItemStack(BTWItems.candle, 1, 0), Character.valueOf('E'), Block.enchantmentTable, Character.valueOf('B'), Item.bone});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.infernalEnchanter), new Object[]{"CAC", "SES", "NPN", Character.valueOf('C'), NMItems.crystalLens, Character.valueOf('A'), NMItems.azureCeramicPlate, Character.valueOf('S'), BTWItems.soulforgedSteelIngot, Character.valueOf('E'), Block.enchantmentTable, Character.valueOf('N'), NMItems.deadzoneShard, Character.valueOf('P'), NMItems.crystalPrecisionGear}),
                NMSkillNodes.BRING_ANCIENT_MANUSCRIPT_16, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_8, NMSkillNodes.BRING_DEADZONE_SHARD_64, NMSkillNodes.BRING_ANCIENT_MANUSCRIPT_10);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.helmetLeather), new Object[]{"###", "# #", Character.valueOf('#'), BTWTags.rawLeathers});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.plateLeather), new Object[]{"# #", "###", "###", Character.valueOf('#'), BTWTags.rawLeathers});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.legsLeather), new Object[]{"###", "# #", "# #", Character.valueOf('#'), BTWTags.rawLeathers});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.bootsLeather), new Object[]{"# #", "# #", Character.valueOf('#'), BTWTags.rawLeathers});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.helmetLeather), new Object[]{"###", "# #", Character.valueOf('#'), BTWTags.rawLeathers}), NMSkillNodes.TAME_ANIMAL_8, NMSkillNodes.BRING_LEATHER_16);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.plateLeather), new Object[]{"# #", "###", "###", Character.valueOf('#'), BTWTags.rawLeathers}), NMSkillNodes.TAME_ANIMAL_8, NMSkillNodes.BRING_LEATHER_16);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.legsLeather), new Object[]{"###", "# #", "# #", Character.valueOf('#'), BTWTags.rawLeathers}), NMSkillNodes.TAME_ANIMAL_8, NMSkillNodes.BRING_LEATHER_16);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.bootsLeather), new Object[]{"# #", "# #", Character.valueOf('#'), BTWTags.rawLeathers}), NMSkillNodes.TAME_ANIMAL_8, NMSkillNodes.BRING_LEATHER_16);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeIron), new Object[]{"XXX", " # ", " # ", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), Item.ingotIron});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.axeIron), new Object[]{"X ", "X#", " #", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), Item.ingotIron});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.shovelIron), new Object[]{"X", "#", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), Item.ingotIron});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeIron), new Object[]{"X#", " #", " #", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), Item.ingotIron});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.swordIron), new Object[]{"X", "X", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), Item.ingotIron});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.pickaxeIron), new Object[]{
                "XXX", "C#C", "N#N",
                        Character.valueOf('#'), NMItems.stoneStick,
                        Character.valueOf('C'), Item.clay,
                        Character.valueOf('N'), NMItems.primitiveGlue,
                        Character.valueOf('X'), Item.ingotIron}),
                NMSkillNodes.BRING_STONE_STICK_64, NMSkillNodes.BRING_IRON_INGOT_16,
                NMSkillNodes.KILL_MOB_250);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.axeIron), new Object[]{
                "XXN", "X#L", "G#L",
                        Character.valueOf('#'), NMItems.stoneStick,
                        Character.valueOf('G'), BTWItems.glue,
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('L'), NMItems.lithiumStabilizer,
                        Character.valueOf('X'), Item.ingotIron}),
                NMSkillNodes.BRING_STONE_STICK_64, NMSkillNodes.BRING_IRON_INGOT_16, NMSkillNodes.MINE_BLOCK_1000);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.shovelIron), new Object[]{
                "CXC", "B#B", "S#S",
                        Character.valueOf('#'), NMItems.stoneStick,
                        Character.valueOf('C'), Item.clay,
                        Character.valueOf('B'), NMItems.nickelBinding,
                        Character.valueOf('S'), NMItems.lithiumStabilizer,
                        Character.valueOf('X'), Item.ingotIron}),
                NMSkillNodes.BRING_IRON_SHOVEL, NMSkillNodes.BRING_STONE_STICK_64,
                NMSkillNodes.BRING_AQUAMARINE_16, NMSkillNodes.BRING_STONE_BRICK_32,
                NMSkillNodes.BRING_IRON_INGOT_16, NMSkillNodes.BRING_LITHIUM_SALT_16);
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.hoeIron), new Object[]{
                "X#G", "S#S", "S#S",
                        Character.valueOf('#'), NMItems.stoneStick,
                        Character.valueOf('S'), NMItems.lithiumStabilizer,
                        Character.valueOf('G'), BTWItems.glue,
                        Character.valueOf('X'), Item.ingotIron}),
                NMSkillNodes.BRING_STONE_STICK_64, NMSkillNodes.BRING_IRON_INGOT_16, NMSkillNodes.BRING_LITHIUM_SALT_16);
        SkillLockedCrafting.requireSkills(
                RecipeManager.addRecipe(new ItemStack(Item.swordIron), new Object[]{
                        "X", "X", "#",
                        Character.valueOf('#'), NMItems.stoneStick,
                        Character.valueOf('X'), Item.ingotIron}),
                NMSkillNodes.BRING_IRON_SWORD,
                NMSkillNodes.BRING_BONE_CLUB_4,
                NMSkillNodes.BRING_WOODEN_CLUB_4,
                NMSkillNodes.BRING_STONE_STICK_64,
                NMSkillNodes.KILL_MOB_250);

        RecipeManager.addRecipe(new ItemStack(Item.boat, 1), new Object[]{"#P#", "###", "S S",
                Character.valueOf('P'), Item.shovelWood,
                Character.valueOf('S'), BTWItems.screw,
                Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings});

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
                        "NXN", "PYL", "Z Z",
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('X'), BTWItems.diamondIngot,
                        Character.valueOf('P'), BTWItems.padding,
                        Character.valueOf('Z'), BTWItems.belt,
                        Character.valueOf('Y'), NMItems.crystalLens,
                        Character.valueOf('L'), NMItems.lithiumStabilizer}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.KILL_MOB_250, NMSkillNodes.BRING_IRON_ANVIL);

        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMBlocks.diamondAnvil), new Object[]{
                        "NXN", " Y ", "NNN",
                        Character.valueOf('N'), NMItems.refinedDiamondIngot,
                        Character.valueOf('Y'), NMBlocks.stoneAnvil,
                        Character.valueOf('X'), BTWBlocks.diamondIngot}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.KILL_MOB_250, NMSkillNodes.SMELT_IRON_NUGGET_128);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.pickaxeDiamond), new Object[]{"XXX", " # ", " # ", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.pickaxeDiamond), new Object[]{
                        "III", "GHL", " H ",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('G'), NMItems.crystalPrecisionGear,
                        Character.valueOf('L'), NMItems.lithiumHeatCompound,
                        Character.valueOf('H'), NMItems.ironStick}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4,
                NMSkillNodes.BRING_IRON_STICK_64, NMSkillNodes.MINE_DIAMOND_ORE_100, NMSkillNodes.BRING_DIAMOND_BRICK_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.diamondChisel), new Object[]{"X", Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.diamondChisel), new Object[]{
                        "AC", "NI",
                        Character.valueOf('C'), NMItems.crystalPolishedShard,
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('A'), NMItems.lithiumRefined,
                        Character.valueOf('I'), BTWItems.diamondIngot}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.diamondShears), new Object[]{"X ", " X", Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWItems.diamondShears), new Object[]{
                        "IP", " I",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('P'), NMItems.nickelPlate}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeDiamond), new Object[]{"X#", " #", " #", Character.valueOf('#'), TagInstance.of(BTWTags.lowQualityToolHandles), Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.hoeDiamond), new Object[]{
                        "IHL", "GH ", " H ",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('L'), NMItems.lithiumStabilizer,
                        Character.valueOf('G'), NMItems.nickelBinding,
                        Character.valueOf('H'), NMItems.ironStick}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4,
                NMSkillNodes.BRING_IRON_STICK_64, NMSkillNodes.MINE_DIAMOND_ORE_100, NMSkillNodes.BRING_DIAMOND_BRICK_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.swordDiamond), new Object[]{"X", "X", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.swordDiamond), new Object[]{
                        " I ", "CIC", " H ",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('C'), NMItems.crystalPolishedShard,
                        Character.valueOf('H'), NMItems.ironStick}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4,
                NMSkillNodes.BRING_IRON_STICK_64, NMSkillNodes.MINE_DIAMOND_ORE_100, NMSkillNodes.BRING_DIAMOND_BRICK_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.shovelDiamond), new Object[]{"X", "#", "#", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.shovelDiamond), new Object[]{
                        " I ", "CHG", " H ",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('C'), NMItems.crystalPolishedShard,
                        Character.valueOf('G'), NMItems.nickelBinding,
                        Character.valueOf('H'), NMItems.ironStick}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4,
                NMSkillNodes.BRING_IRON_STICK_64, NMSkillNodes.MINE_DIAMOND_ORE_100, NMSkillNodes.BRING_DIAMOND_BRICK_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.axeDiamond), new Object[]{"X ", "X#", " #", Character.valueOf('#'), BTWTags.lowQualityToolHandles, Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.axeDiamond), new Object[]{
                        "IIV", "IHG", " HL",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('G'), NMItems.nickelBinding,
                        Character.valueOf('V'), BTWItems.glue,
                        Character.valueOf('L'), NMItems.lithiumStabilizer,
                        Character.valueOf('H'), NMItems.ironStick}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4,
                NMSkillNodes.BRING_IRON_STICK_64, NMSkillNodes.MINE_DIAMOND_ORE_100, NMSkillNodes.BRING_DIAMOND_BRICK_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.helmetDiamond), new Object[]{"XXX", "XYX", Character.valueOf('X'), BTWItems.diamondIngot, Character.valueOf('Y'), BTWItems.diamondArmorPlate});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.helmetDiamond), new Object[]{
                        "IXI", "IYI","N N",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('X'), BTWItems.diamondArmorPlate,
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('Y'), NMItems.crystalLens}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.plateDiamond), new Object[]{"Y Y", "XXX", "XXX", Character.valueOf('X'), BTWItems.diamondIngot, Character.valueOf('Y'), BTWItems.diamondArmorPlate});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.plateDiamond), new Object[]{
                        "NLN", "IXI", "III",
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('X'), BTWItems.diamondArmorPlate,
                        Character.valueOf('L'), NMItems.lithiumStabilizer}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.legsDiamond), new Object[]{"XXX", "Y Y", "Y Y", Character.valueOf('X'), BTWItems.diamondIngot, Character.valueOf('Y'), BTWItems.diamondArmorPlate});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.legsDiamond), new Object[]{
                        "IXI", "NLN", "ILI",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('X'), BTWItems.diamondArmorPlate,
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('L'), NMItems.lithiumStabilizer}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4);

        RecipeManager.removeVanillaRecipe(new ItemStack(Item.bootsDiamond), new Object[]{"X X", "X X", Character.valueOf('X'), BTWItems.diamondIngot});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Item.bootsDiamond), new Object[]{
                        "I I", "ILI", "N N",
                        Character.valueOf('I'), BTWItems.diamondIngot,
                        Character.valueOf('N'), NMItems.nickelBinding,
                        Character.valueOf('L'), NMItems.lithiumStabilizer}),
                NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR, NMSkillNodes.BRING_NICKEL_PLATE_4);

        // manual cistern work produces the components for this tier. none of these gates
        // depends on the mechanical blocks being replaced here.
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.turntable), new Object[]{"###", "ZXZ", "ZYZ", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('X'), Item.pocketSundial, Character.valueOf('Y'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Z'), BTWTags.stoneBrickItems});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.turntable), new Object[]{
                        "SPS", "ZGZ", "LCL",
                        Character.valueOf('S'), BTWTags.woodenSidings,
                        Character.valueOf('P'), NMItems.crystalPrecisionGear,
                        Character.valueOf('Z'), NMTags.netherKilnMasonry,
                        Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE),
                        Character.valueOf('L'), NMItems.lithiumStabilizer,
                        Character.valueOf('C'), NMItems.crystalLens}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2, NMSkillNodes.BRING_REDSTONE_256, NMSkillNodes.BRING_GLASS_64);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.hopper), new Object[]{"# #", "XYX", " Z ", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), BTWTags.woodenPressurePlates, Character.valueOf('Z'), BTWTags.woodenCorners});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.hopper), new Object[]{
                        "S S", "PNP", " C ",
                        Character.valueOf('S'), BTWTags.woodenSidings,
                        Character.valueOf('P'), NMItems.nickelPlate,
                        Character.valueOf('N'), NMItems.nickelMachinePart,
                        Character.valueOf('C'), NMItems.crystalLens}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2, NMSkillNodes.BRING_REDSTONE_256, NMSkillNodes.BRING_GLASS_64);


        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.hopper), new Object[]{
                        "S S", "PNP", " C ",
                        Character.valueOf('S'), BTWTags.woodenSidings,
                        Character.valueOf('P'), NMItems.tungstenPlate,
                        Character.valueOf('N'), NMItems.nickelMachinePart,
                        Character.valueOf('C'), NMItems.crystalLens}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2, NMSkillNodes.BRING_REDSTONE_256, NMSkillNodes.BRING_GLASS_64, NMSkillNodes.BRING_TUNGSTEN_INGOT_8);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.pulley), new Object[]{"#Y#", "XZX", "#Y#", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE), Character.valueOf('Y'), Item.ingotIron, Character.valueOf('Z'), BTWItems.redstoneLatch});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.pulley), new Object[]{
                        "SNS", "GRG", "SLS",
                        Character.valueOf('S'), BTWTags.highEfficiencyWoodSidings,
                        Character.valueOf('N'), NMItems.nickelMachinePart,
                        Character.valueOf('G'), new ItemStack(BTWItems.gear, 1, Short.MAX_VALUE),
                        Character.valueOf('R'), BTWItems.redstoneLatch,
                        Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2, NMSkillNodes.BRING_REDSTONE_256, NMSkillNodes.BRING_REDSTONE_16);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.pistonBase), new Object[]{"#I#", "XYX", "XZX", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), Item.ingotIron, Character.valueOf('X'), BTWTags.stoneBrickItems, Character.valueOf('Y'), BTWItems.soulUrn, Character.valueOf('Z'), BTWItems.redstoneLatch});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Block.pistonBase), new Object[]{
                        "SNS", "XUX", "LPL",
                        Character.valueOf('S'), BTWTags.woodenSidings,
                        Character.valueOf('N'), NMItems.nickelMachinePart,
                        Character.valueOf('X'), BTWTags.stoneBrickItems,
                        Character.valueOf('U'), BTWItems.soulUrn,
                        Character.valueOf('L'), NMItems.lithiumHeatCompound,
                        Character.valueOf('P'), NMItems.crystalPrecisionGear}),
                NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2, NMSkillNodes.BRING_REDSTONE_256, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR);

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
                NMSkillNodes.BRING_HEAT_RESISTANT_NICKEL_COMPONENT_2, NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.CRAFT_CAULDRON);

        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.hibachi), new Object[]{"XXX", "#Z#", "#Y#", Character.valueOf('#'), BTWTags.stoneBrickItems, Character.valueOf('X'), BTWItems.concentratedHellfire, Character.valueOf('Y'), Item.redstone, Character.valueOf('Z'), BTWItems.element});
        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(BTWBlocks.hibachi), new Object[]{
                        "HHH", "SES", "NLN",
                        Character.valueOf('H'), BTWItems.concentratedHellfire,
                        Character.valueOf('S'), NMTags.netherKilnMasonry,
                        Character.valueOf('E'), BTWItems.element,
                        Character.valueOf('N'), NMItems.nickelHeatComponent,
                        Character.valueOf('L'), NMItems.lithiumHeatCompound}),
                NMSkillNodes.BRING_HEAT_RESISTANT_NICKEL_COMPONENT_2, NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.CRAFT_CAULDRON);

        RecipeManager.removeVanillaRecipe(new ItemStack(Block.anvil, 1), new Object[]{"iii", " i ", "iii", Character.valueOf('i'), Item.ingotIron});

        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(Block.anvil), new Object[]{
                        "ABA", " A ", "AAA",
                        Character.valueOf('A'), BTWItems.soulforgedSteelIngot,
                        Character.valueOf('B'), BTWBlocks.soulforgedSteelBlock}),
                NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_BROADHEAD_ARROWHEAD_16,
                NMSkillNodes.BRING_DIAMOND_16, NMSkillNodes.BRING_REFINED_DIAMOND_INGOT_AFTER_WITHER,
                NMSkillNodes.CRAFT_UNIQUE_RECIPE_OUTPUT_256, NMSkillNodes.CRAFT_CAULDRON,
                NMSkillNodes.BRING_WINDMILL_4, NMSkillNodes.BRING_STEEL_HAMMER,
                NMSkillNodes.KILL_MOB_1000, NMSkillNodes.KILL_ENDERMAN_50,
                NMSkillNodes.MINE_STRATA_ONE_COBBLESTONE_3000, NMSkillNodes.KILL_WITHER,
                NMSkillNodes.BRING_IRON_INGOT_16, NMSkillNodes.SMELT_IRON_NUGGET_128,
                NMSkillNodes.CRAFT_CAULDRON, NMSkillNodes.BRING_ANCIENT_MANUSCRIPT_16
        );


        SkillLockedCrafting.requireSkills(RecipeManager.addRecipe(new ItemStack(NMBlocks.ironAnvil), new Object[]{
                        "ABA", " A ", "AAA",
                        Character.valueOf('A'), Item.ingotIron,
                        Character.valueOf('B'), Block.blockIron}),
                NMSkillNodes.BRING_DIAMOND_16, NMSkillNodes.BRING_DIAMOND_HAMMER,
                NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16, NMSkillNodes.CRAFT_CAULDRON,
                NMSkillNodes.BRING_WOODEN_BLADE_16, NMSkillNodes.CRAFT_UNIQUE_RECIPE_OUTPUT_64,
                NMSkillNodes.KILL_MOB_250, NMSkillNodes.BRING_ENCHANTMENT_TABLE,
                NMSkillNodes.MINE_STONE_1000, NMSkillNodes.KILL_WITCH_4
        );


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
                        NMItems.crystalPrecisionGear,
                        NMItems.deadzoneShard,
                        NMItems.denseNetherrackCore}),
                NMSkillNodes.BRING_REFINED_DIAMOND_INGOT_AFTER_WITHER, NMSkillNodes.BRING_DEADZONE_SHARD_64,
                NMSkillNodes.KILL_WITHER, NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16);
        addUltimateEnderRecipes();

        applyExpandedSkillRecipeGates();

        finishRecipes("Skill Locked Recipes");

    }

    private static void addUltimateEnderRecipes() {
        SkillLockedCrafting.requireSkills(
                automationEssenceRecipe,
                NMSkillNodes.BRING_GEAR_64,
                NMSkillNodes.BRING_SCREW_16,
                NMSkillNodes.BRING_SCREW_PUMP_4,
                NMSkillNodes.BRING_REDSTONE_LATCH_16,
                NMSkillNodes.COMPLETE_IRON_AGE_ACHIEVEMENTS);

//        SkillLockedCrafting.requireSkills(
//                agrarianEssenceRecipe,
//                NMSkillNodes.BRING_HEMP_32,
//                NMSkillNodes.BRING_FABRIC_16,
//                NMSkillNodes.BRING_TANNED_LEATHER_16,
//                NMSkillNodes.BRING_DUNG_16,
//                NMSkillNodes.BRING_FLOUR_32,
//                NMSkillNodes.BRING_RAW_EGG_16,
//                NMSkillNodes.BRING_FRESH_PUMPKIN_16);

//        SkillLockedCrafting.requireSkills(
//                infernalEssenceRecipe,
//                NMSkillNodes.BRING_HELLFIRE_DUST_32,
//                NMSkillNodes.BRING_SOUL_URN_16,
//                NMSkillNodes.BRING_ENDER_PEARL_16,
//                NMSkillNodes.BRING_OCULAR_OF_ENDER_8,
//                NMSkillNodes.BRING_ANCIENT_MANUSCRIPT_16,
//                NMSkillNodes.WITHER_KILL_LOOT,
//                NMSkillNodes.VISIT_UNIQUE_BIOME_10);

        SkillLockedCrafting.requireSkills(
                artisanEssenceRecipe,
                NMSkillNodes.BRING_BRICK_32,
                NMSkillNodes.BRING_CANDLE_16,
                NMSkillNodes.BRING_SOAP_16,
                NMSkillNodes.BRING_WICKER_PANE_16,
                NMSkillNodes.BRING_COMPASS_8,
                NMSkillNodes.BRING_MUSIC_RECORD_16,
                NMSkillNodes.BRING_GLASS_64);

        SkillLockedCrafting.requireSkills(
                ultimateEyeOfEnderRecipe,
                NMSkillNodes.BRING_LIBRARIAN_ENDER_TREATISE, NMSkillNodes.BRING_SCREW_PUMP_4, NMSkillNodes.COMPLETE_IRON_AGE_ACHIEVEMENTS, NMSkillNodes.KILL_HOSTILE_MOB_10000, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_SOUL_URN_16,
                NMSkillNodes.BRING_ENDER_PEARL_16, NMSkillNodes.BRING_OCULAR_OF_ENDER_8, NMSkillNodes.BRING_WINDMILL_4, NMSkillNodes.BRING_MUSIC_RECORD_16, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_STEEL_ARMOR_PLATE_16,
                NMSkillNodes.BRING_REDSTONE_EYE_16, NMSkillNodes.BRING_COMPARATOR_8, NMSkillNodes.BRING_REFINED_DIAMOND_INGOT_AFTER_WITHER, NMSkillNodes.BRING_TUNGSTEN_INGOT_8, NMSkillNodes.BRING_QUARTZGLASS_PLATE_8, NMSkillNodes.BRING_NICKEL_PLATE_4,
                NMSkillNodes.BRING_CARBON_IRON_PLATE_8, NMSkillNodes.BRING_REINFORCED_IRON_PLATE_64, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_8, NMSkillNodes.BRING_ANCIENT_MANUSCRIPT_16, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_REFINED_LITHIUM_256,
                NMSkillNodes.BRING_BOTTLE_OF_ENCHANTING_64, NMSkillNodes.BRING_DEADZONE_SHARD_512, NMSkillNodes.BRING_CORESTEEL_PLATE_8, NMSkillNodes.KILL_SKELETON_1000, NMSkillNodes.BRING_DEADZONE_ALLOY_PLATE_8, NMSkillNodes.BRING_SIGNAL_ALLOY_PLATE_8,
                NMSkillNodes.BRING_PRISMATIC_INGOT_8, NMSkillNodes.BRING_REFINED_PRISMA_ARMOR, NMSkillNodes.BRING_BLOOD_ARMOR_SET, NMSkillNodes.BRING_STEEL_ARMOR_SET, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2,
                NMSkillNodes.BRING_HEAT_RESISTANT_NICKEL_COMPONENT_2, NMSkillNodes.BRING_REFINED_REDSTONE_16, NMSkillNodes.BRING_GUNPOWDER_256, NMSkillNodes.BRING_DARK_INGOT_8, NMSkillNodes.BRING_GLASS_64, NMSkillNodes.BRING_BLACKGLASS_PLATE_4,
                NMSkillNodes.BRING_BLACKSTONE_64, NMSkillNodes.BRING_OBSIDIAN_BRICK_16, NMSkillNodes.BRING_WINDMILL_BLADE_8, NMSkillNodes.BRING_GOLD_ORE_PILE_32, NMSkillNodes.BRING_DIAMOND_INGOT_2, NMSkillNodes.KILL_WITHER,
                NMSkillNodes.TAME_ANIMAL_8, NMSkillNodes.MINE_CLAY_BLOCK_1500, NMSkillNodes.MINE_NICKEL_ORE_500, NMSkillNodes.MINE_STRATA_ONE_COBBLESTONE_3000, NMSkillNodes.KILL_ZOMBIE_1000, NMSkillNodes.KILL_WITCH_30,
                NMSkillNodes.BRING_BLOOD_ORB_128_II, NMSkillNodes.BRING_GIMP_ARMOR_SET, NMSkillNodes.BRING_WOODEN_BLADE_16, NMSkillNodes.BRING_HEMP_32, NMSkillNodes.BRING_PLANT_FIBER_1024, NMSkillNodes.BRING_NETHERRACK_TIER_ONE_1024,
                NMSkillNodes.BRING_NETHERRACK_TIER_TWO_1024, NMSkillNodes.BRING_NETHERRACK_TIER_THREE_1024, NMSkillNodes.BRING_ENDER_PEARL_16, NMSkillNodes.BRING_BOOK_128, NMSkillNodes.BRING_RARE_FISH_32, NMSkillNodes.BRING_DRIED_PLANT_FIBER_300,
                NMSkillNodes.VISIT_UNIQUE_BIOME_10, NMSkillNodes.CRAFT_UNIQUE_RECIPE_OUTPUT_256);
    }

    private static void applyExpandedSkillRecipeGates() {
        // Hand-crafting and workbench recipes have an owning player, so these gates may use
        // either personal or world skills. Repeated calls deliberately merge into AND gates.
        SkillRecipeGates.crafting(BTWItems.pointyStick.itemID, NMSkillNodes.BRING_STICK_4);
//        SkillRecipeGates.crafting(BTWItems.sharpStone.itemID, NMSkillNodes.BRING_STICK_4, NMSkillNodes.BRING_LOOSE_STONE_2);
        SkillRecipeGates.crafting(BTWItems.firePlough.itemID, NMSkillNodes.BRING_STICK_4, NMSkillNodes.BRING_SHARP_STONE_4);
        SkillRecipeGates.crafting(NMItems.drill.itemID, NMSkillNodes.BRING_STICK_4);
        SkillRecipeGates.crafting(BTWBlocks.looseCobblestone.blockID, 0, NMSkillNodes.BRING_LOOSE_STONE_64);
        SkillRecipeGates.crafting(BTWBlocks.looseCobblestoneSlab.blockID, 0, NMSkillNodes.BRING_LOOSE_STONE_2, NMSkillNodes.BRING_STICK_4);
        SkillRecipeGates.crafting(BTWBlocks.looseCobblestone.blockID, 4, NMSkillNodes.BRING_STRATA_TWO_LOOSE_STONE_128);
        SkillRecipeGates.crafting(BTWBlocks.looseCobblestoneSlab.blockID, 4, NMSkillNodes.BRING_STRATA_TWO_LOOSE_STONE_128);
        SkillRecipeGates.crafting(BTWBlocks.looseCobblestone.blockID, 8, NMSkillNodes.BRING_STRATA_THREE_LOOSE_STONE_256);
        SkillRecipeGates.crafting(BTWBlocks.looseCobblestoneSlab.blockID, 8, NMSkillNodes.BRING_STRATA_THREE_LOOSE_STONE_256);
        SkillRecipeGates.crafting(BTWBlocks.oakBarkBox.blockID, NMSkillNodes.BRING_BARK_16);
        SkillRecipeGates.crafting(BTWBlocks.spruceBarkBox.blockID, NMSkillNodes.BRING_BARK_16);
        SkillRecipeGates.crafting(BTWBlocks.birchBarkBox.blockID, NMSkillNodes.BRING_BARK_16);
        SkillRecipeGates.crafting(BTWBlocks.jungleBarkBox.blockID, NMSkillNodes.BRING_BARK_16);
        SkillRecipeGates.crafting(BTWBlocks.looseDirtSlab.blockID, NMSkillNodes.JUMP_1000);

        SkillRecipeGates.crafting(Item.arrow.itemID, NMSkillNodes.BRING_FLINT_64, NMSkillNodes.BRING_FEATHER_32, NMSkillNodes.BRING_STRING_32);
        SkillRecipeGates.crafting(Item.bow.itemID, NMSkillNodes.BRING_ARROW_64);
        SkillRecipeGates.crafting(BTWItems.compositeBow.itemID, NMSkillNodes.BRING_BOW_36, NMSkillNodes.BRING_BONE_16, NMSkillNodes.BRING_GLUE_16, NMSkillNodes.BRING_SINEW_16, NMSkillNodes.FIRE_ARROW_256);
        SkillRecipeGates.crafting(BTWItems.broadheadArrow.itemID, NMSkillNodes.BRING_BROADHEAD_ARROWHEAD_16);
        SkillRecipeGates.crafting(Item.fishingRod.itemID, NMSkillNodes.BRING_BONE_FISH_HOOK_8);
        SkillRecipeGates.crafting(BTWItems.baitedFishingRod.itemID, NMSkillNodes.BRING_BAT_WING_16);
        SkillRecipeGates.crafting(BTWItems.stake.itemID, NMSkillNodes.BRING_SILK_16);

        SkillRecipeGates.crafting(BTWItems.rope.itemID, NMSkillNodes.BRING_HEMP_FIBER_32);
        SkillRecipeGates.crafting(BTWBlocks.aestheticOpaque.blockID, 6, NMSkillNodes.BRING_ROPE_8);
        SkillRecipeGates.crafting(Item.nameTag.itemID, NMSkillNodes.BRING_ROPE_8);
        SkillRecipeGates.crafting(BTWBlocks.gearBox.blockID, NMSkillNodes.BRING_ROPE_8, NMSkillNodes.BRING_GEAR_64, NMSkillNodes.BRING_PADDING_16);
        SkillRecipeGates.crafting(BTWItems.fabric.itemID, NMSkillNodes.BRING_HEMP_32, NMSkillNodes.BRING_HEMP_FIBER_32);
        SkillRecipeGates.crafting(NMItems.bandage.itemID, NMSkillNodes.BRING_BEDROLL);
        SkillRecipeGates.crafting(BTWItems.padding.itemID, NMSkillNodes.BRING_FABRIC_16);
        SkillRecipeGates.crafting(BTWItems.windMillBlade.itemID, NMSkillNodes.BRING_FABRIC_16, NMSkillNodes.BRING_WOOL_128, NMSkillNodes.BRING_WOOL_KNIT_16);
        SkillRecipeGates.crafting(BTWBlocks.axle.blockID, NMSkillNodes.BRING_FABRIC_16, NMSkillNodes.BRING_GEAR_64, NMSkillNodes.CRAFT_CAULDRON, NMSkillNodes.BRING_HEMP_32, NMSkillNodes.BRING_WINDMILL_BLADE_8);
        SkillRecipeGates.crafting(Item.bed.itemID, NMSkillNodes.BRING_FABRIC_16, NMSkillNodes.BRING_BELT_8, NMSkillNodes.BRING_PADDING_16, NMSkillNodes.BRING_POPPY_16, NMSkillNodes.BRING_BEDROLL);
        SkillRecipeGates.crafting(BTWItems.belt.itemID, NMSkillNodes.BRING_LEATHER_STRAP_16);
        SkillRecipeGates.crafting(BTWItems.haft.itemID, NMSkillNodes.BRING_LEATHER_STRAP_16, NMSkillNodes.BRING_GLUE_16);
        SkillRecipeGates.crafting(BTWItems.breedingHarness.itemID, NMSkillNodes.BRING_LEATHER_STRAP_16, NMSkillNodes.TAME_ANIMAL_8);
        SkillRecipeGates.crafting(BTWBlocks.loom.blockID, NMSkillNodes.BRING_BELT_8);
        SkillRecipeGates.crafting(BTWItems.woodenBlade.itemID, 
                NMSkillNodes.BRING_GLUE_16, NMSkillNodes.BRING_GLUE_SLURRY_16,
                NMSkillNodes.BRING_IRON_INGOT_16, NMSkillNodes.BRING_WINDMILL_4,
                NMSkillNodes.CRAFT_UNIQUE_RECIPE_OUTPUT_256, NMSkillNodes.BRING_NETHER_WART_64);
        SkillRecipeGates.crafting(BTWItems.waterWheel.itemID, NMSkillNodes.BRING_WOODEN_BLADE_16);
        SkillRecipeGates.crafting(BTWBlocks.screwPump.blockID, NMSkillNodes.BRING_GEAR_64, NMSkillNodes.BRING_SCREW_16);
        SkillRecipeGates.crafting(BTWBlocks.hibachi.blockID, NMSkillNodes.BRING_GEAR_64);
        SkillRecipeGates.crafting(BTWBlocks.bellows.blockID, NMSkillNodes.BRING_GEAR_64);
        SkillRecipeGates.crafting(BTWItems.windMill.itemID, NMSkillNodes.BRING_WINDMILL_BLADE_8);
        SkillRecipeGates.crafting(BTWItems.verticalWindMill.itemID, NMSkillNodes.BRING_WINDMILL_4);

        SkillRecipeGates.crafting(BTWItems.knittingNeedles.itemID, NMSkillNodes.BRING_WOOL_16, NMSkillNodes.BRING_STICK_16);
        SkillRecipeGates.crafting(BTWItems.woolKnit.itemID, NMSkillNodes.BRING_KNITTING_NEEDLE_4);
        SkillRecipeGates.crafting(BTWItems.woolHelmet.itemID, NMSkillNodes.BRING_WOOL_KNIT_16);
        SkillRecipeGates.crafting(BTWItems.woolChest.itemID, NMSkillNodes.BRING_WOOL_KNIT_16);
        SkillRecipeGates.crafting(BTWItems.woolLeggings.itemID, NMSkillNodes.BRING_WOOL_KNIT_16);
        SkillRecipeGates.crafting(BTWItems.woolBoots.itemID, NMSkillNodes.BRING_WOOL_KNIT_16);
        SkillRecipeGates.crafting(BTWItems.paddedHelmet.itemID, NMSkillNodes.BRING_PADDING_16);
        SkillRecipeGates.crafting(BTWItems.paddedChest.itemID, NMSkillNodes.BRING_PADDING_16);
        SkillRecipeGates.crafting(BTWItems.paddedLeggings.itemID, NMSkillNodes.BRING_PADDING_16);
        SkillRecipeGates.crafting(BTWItems.paddedBoots.itemID, NMSkillNodes.BRING_PADDING_16);
        SkillRecipeGates.crafting(BTWItems.paddedHelmet.itemID, NMSkillNodes.BRING_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(BTWItems.paddedChest.itemID, NMSkillNodes.BRING_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(BTWItems.paddedLeggings.itemID, NMSkillNodes.BRING_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(BTWItems.paddedBoots.itemID, NMSkillNodes.BRING_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(Item.helmetChain.itemID, NMSkillNodes.BRING_MAIL_16);
        SkillRecipeGates.crafting(Item.plateChain.itemID, NMSkillNodes.BRING_MAIL_16);
        SkillRecipeGates.crafting(Item.legsChain.itemID, NMSkillNodes.BRING_MAIL_16);
        SkillRecipeGates.crafting(Item.bootsChain.itemID, NMSkillNodes.BRING_MAIL_16);
        SkillRecipeGates.crafting(BTWItems.tannedLeatherHelmet.itemID, NMSkillNodes.BRING_TANNED_LEATHER_16);
        SkillRecipeGates.crafting(BTWItems.tannedLeatherChest.itemID, NMSkillNodes.BRING_TANNED_LEATHER_16);
        SkillRecipeGates.crafting(BTWItems.tannedLeatherLeggings.itemID, NMSkillNodes.BRING_TANNED_LEATHER_16);
        SkillRecipeGates.crafting(BTWItems.tannedLeatherBoots.itemID, NMSkillNodes.BRING_TANNED_LEATHER_16);
        SkillRecipeGates.crafting(BTWItems.gimpHelmet.itemID, NMSkillNodes.BRING_TANNED_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(BTWItems.gimpChest.itemID, NMSkillNodes.BRING_TANNED_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(BTWItems.gimpLeggings.itemID, NMSkillNodes.BRING_TANNED_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(BTWItems.gimpBoots.itemID, NMSkillNodes.BRING_TANNED_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(BTWItems.gimpHelmet.itemID, NMSkillNodes.BRING_PADDED_ARMOR_SET);
        SkillRecipeGates.crafting(BTWItems.gimpChest.itemID, NMSkillNodes.BRING_PADDED_ARMOR_SET);
        SkillRecipeGates.crafting(BTWItems.gimpLeggings.itemID, NMSkillNodes.BRING_PADDED_ARMOR_SET);
        SkillRecipeGates.crafting(BTWItems.gimpBoots.itemID, NMSkillNodes.BRING_PADDED_ARMOR_SET);
        SkillRecipeGates.crafting(BTWBlocks.aestheticVegetation.blockID, 0, NMSkillNodes.BRING_VINE_256);
        SkillRecipeGates.crafting(Item.helmetChain.itemID, NMSkillNodes.BRING_GIMP_ARMOR_SET);
        SkillRecipeGates.crafting(Item.plateChain.itemID, NMSkillNodes.BRING_GIMP_ARMOR_SET);
        SkillRecipeGates.crafting(Item.legsChain.itemID, NMSkillNodes.BRING_GIMP_ARMOR_SET);
        SkillRecipeGates.crafting(Item.bootsChain.itemID, NMSkillNodes.BRING_GIMP_ARMOR_SET);
        SkillRecipeGates.crafting(Item.helmetIron.itemID, NMSkillNodes.BRING_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(Item.plateIron.itemID, NMSkillNodes.BRING_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(Item.legsIron.itemID, NMSkillNodes.BRING_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(Item.bootsIron.itemID, NMSkillNodes.BRING_LEATHER_ARMOR_SET);
        SkillRecipeGates.crafting(Item.helmetGold.itemID, NMSkillNodes.BRING_IRON_ARMOR_SET);
        SkillRecipeGates.crafting(Item.plateGold.itemID, NMSkillNodes.BRING_IRON_ARMOR_SET);
        SkillRecipeGates.crafting(Item.legsGold.itemID, NMSkillNodes.BRING_IRON_ARMOR_SET);
        SkillRecipeGates.crafting(Item.bootsGold.itemID, NMSkillNodes.BRING_IRON_ARMOR_SET);

        SkillRecipeGates.crafting(BTWBlocks.wickerBlock.blockID, NMSkillNodes.BRING_WICKER_PANE_16);
        SkillRecipeGates.crafting(BTWBlocks.wickerSlab.blockID, NMSkillNodes.BRING_WICKER_PANE_16);
        SkillRecipeGates.crafting(BTWBlocks.wickerPane.blockID,
                NMSkillNodes.BRING_SUGAR_CANE_16, NMSkillNodes.BRING_KNITTING_NEEDLE_4);
        SkillRecipeGates.crafting(BTWBlocks.thatch.blockID, NMSkillNodes.BRING_STRAW_32);
        SkillRecipeGates.crafting(Item.clay.itemID, NMSkillNodes.BRING_CLAY_PILE_16);

        SkillRecipeGates.crafting(Block.brick.blockID, NMSkillNodes.BRING_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.looseBrick.blockID, NMSkillNodes.BRING_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.looseBrickSlab.blockID, NMSkillNodes.BRING_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.looseBrickStairs.blockID, NMSkillNodes.BRING_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.brickSidingAndCorner.blockID, NMSkillNodes.BRING_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.brickMouldingAndDecorative.blockID, NMSkillNodes.BRING_BRICK_32);
        SkillRecipeGates.crafting(Block.netherBrick.blockID, NMSkillNodes.BRING_NETHER_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.looseNetherBrick.blockID, NMSkillNodes.BRING_NETHER_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.looseNetherBrickSlab.blockID, NMSkillNodes.BRING_NETHER_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.looseNetherBrickStairs.blockID, NMSkillNodes.BRING_NETHER_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.netherBrickSidingAndCorner.blockID, NMSkillNodes.BRING_NETHER_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.netherBrickMouldingAndDecorative.blockID, NMSkillNodes.BRING_NETHER_BRICK_32);
        SkillRecipeGates.crafting(Block.stoneBrick.blockID, NMSkillNodes.BRING_STONE_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.looseStoneBrick.blockID, NMSkillNodes.BRING_STONE_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.looseStoneBrickSlab.blockID, NMSkillNodes.BRING_STONE_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.looseStoneBrickStairs.blockID, NMSkillNodes.BRING_STONE_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.stoneBrickSidingAndCorner.blockID, NMSkillNodes.BRING_STONE_BRICK_32);
        SkillRecipeGates.crafting(BTWBlocks.stoneBrickMouldingAndDecorative.blockID, NMSkillNodes.BRING_STONE_BRICK_32);

        SkillRecipeGates.crafting(Item.compass.itemID, NMSkillNodes.BRING_IRON_NUGGET_32);
        SkillRecipeGates.crafting(BTWItems.screw.itemID, NMSkillNodes.BRING_IRON_NUGGET_32);
        SkillRecipeGates.crafting(Block.rail.blockID, NMSkillNodes.BRING_IRON_NUGGET_32, NMSkillNodes.BRING_IRON_BRICK_64);
        SkillRecipeGates.crafting(BTWBlocks.ironSpike.blockID, NMSkillNodes.BRING_IRON_NUGGET_32);
        SkillRecipeGates.crafting(Block.railDetector.blockID, NMSkillNodes.BRING_IRON_NUGGET_32, NMSkillNodes.BRING_COMPARATOR_8, NMSkillNodes.BRING_IRON_BRICK_64, NMSkillNodes.BRING_REDSTONE_BLOCK_16);
        SkillRecipeGates.crafting(Block.railPowered.blockID, NMSkillNodes.BRING_IRON_BRICK_64, NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_GOLD_INGOT_16);
        SkillRecipeGates.crafting(Block.railActivator.blockID, NMSkillNodes.BRING_IRON_BRICK_64, NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_DYE_BLEND_16);
        SkillRecipeGates.crafting(NMBlocks.stationRail.blockID, NMSkillNodes.BRING_IRON_BRICK_64, NMSkillNodes.BRING_COMPARATOR_8, NMSkillNodes.BRING_REDSTONE_BLOCK_16);

        SkillRecipeGates.crafting(BTWBlocks.anchor.blockID, NMSkillNodes.BRING_IRON_INGOT_16);
        SkillRecipeGates.crafting(Item.pickaxeIron.itemID, NMSkillNodes.BRING_IRON_INGOT_16);
        SkillRecipeGates.crafting(Item.axeIron.itemID, NMSkillNodes.BRING_IRON_INGOT_16);
        SkillRecipeGates.crafting(Item.shovelIron.itemID, NMSkillNodes.BRING_IRON_INGOT_16);
        SkillRecipeGates.crafting(Item.hoeIron.itemID, NMSkillNodes.BRING_IRON_INGOT_16);
        SkillRecipeGates.crafting(Item.swordIron.itemID, NMSkillNodes.BRING_IRON_INGOT_16);
        SkillRecipeGates.crafting(BTWItems.redstoneLatch.itemID, NMSkillNodes.BRING_GOLD_NUGGET_32);
        SkillRecipeGates.crafting(BTWItems.ocularOfEnder.itemID, NMSkillNodes.BRING_GOLD_NUGGET_32, NMSkillNodes.BRING_ENDER_PEARL_16);
        SkillRecipeGates.crafting(Item.pocketSundial.itemID, NMSkillNodes.BRING_GOLD_NUGGET_32);
        SkillRecipeGates.crafting(NMItems.crystalLens.itemID, NMSkillNodes.BRING_GOLD_INGOT_16, NMSkillNodes.BRING_DIAMOND_8);
        SkillRecipeGates.crafting(NMItems.crystalPrecisionGear.itemID, NMSkillNodes.BRING_CRYSTAL_POWDER_32);
        SkillRecipeGates.crafting(BTWBlocks.lightningRod.blockID, NMSkillNodes.BRING_GOLD_INGOT_16);
        SkillRecipeGates.crafting(BTWItems.diamondIngot.itemID, NMSkillNodes.BRING_DIAMOND_8, NMSkillNodes.BRING_CREEPER_OYSTER_16);
        SkillRecipeGates.crafting(BTWItems.stumpRemover.itemID, NMSkillNodes.BRING_CREEPER_OYSTER_16);
        SkillRecipeGates.crafting(BTWItems.diamondArmorPlate.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_GOLD_ARMOR_SET);
        SkillRecipeGates.crafting(BTWBlocks.diamondIngot.blockID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4);
        SkillRecipeGates.crafting(Item.pickaxeDiamond.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_IRON_STICK_64);
        SkillRecipeGates.crafting(Item.axeDiamond.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_IRON_STICK_64);
        SkillRecipeGates.crafting(Item.shovelDiamond.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_IRON_STICK_64);
        SkillRecipeGates.crafting(Item.hoeDiamond.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_IRON_STICK_64);
        SkillRecipeGates.crafting(Item.swordDiamond.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_IRON_STICK_64);
        SkillRecipeGates.crafting(Item.helmetDiamond.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_GOLD_ARMOR_SET);
        SkillRecipeGates.crafting(Item.plateDiamond.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_GOLD_ARMOR_SET);
        SkillRecipeGates.crafting(Item.legsDiamond.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_GOLD_ARMOR_SET);
        SkillRecipeGates.crafting(Item.bootsDiamond.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_GOLD_ARMOR_SET);
        SkillRecipeGates.crafting(NMItems.bloodHelmet.itemID, NMSkillNodes.BRING_REFINED_PRISMA_ARMOR, NMSkillNodes.BRING_BLOOD_INGOT_16);
        SkillRecipeGates.crafting(NMItems.bloodChestplate.itemID, NMSkillNodes.BRING_REFINED_PRISMA_ARMOR, NMSkillNodes.BRING_BLOOD_INGOT_16);
        SkillRecipeGates.crafting(NMItems.bloodLeggings.itemID, NMSkillNodes.BRING_REFINED_PRISMA_ARMOR, NMSkillNodes.BRING_BLOOD_INGOT_16);
        SkillRecipeGates.crafting(NMItems.bloodBoots.itemID, NMSkillNodes.BRING_REFINED_PRISMA_ARMOR, NMSkillNodes.BRING_BLOOD_INGOT_16);
        SkillRecipeGates.crafting(NMItems.carbonIronHelmet.itemID, NMSkillNodes.BRING_CARBON_RICH_IRON_MIX_16);
        SkillRecipeGates.crafting(NMItems.carbonIronChestplate.itemID, NMSkillNodes.BRING_CARBON_RICH_IRON_MIX_16);
        SkillRecipeGates.crafting(NMItems.carbonIronLeggings.itemID, NMSkillNodes.BRING_CARBON_RICH_IRON_MIX_16);
        SkillRecipeGates.crafting(NMItems.carbonIronBoots.itemID, NMSkillNodes.BRING_CARBON_RICH_IRON_MIX_16);
        SkillRecipeGates.crafting(NMItems.reinforcedIronHelmet.itemID, NMSkillNodes.BRING_REINFORCED_IRON_INGOT_32);
        SkillRecipeGates.crafting(NMItems.reinforcedIronChestplate.itemID, NMSkillNodes.BRING_REINFORCED_IRON_INGOT_32);
        SkillRecipeGates.crafting(NMItems.reinforcedIronLeggings.itemID, NMSkillNodes.BRING_REINFORCED_IRON_INGOT_32);
        SkillRecipeGates.crafting(NMItems.reinforcedIronBoots.itemID, NMSkillNodes.BRING_REINFORCED_IRON_INGOT_32);
        gateArmorSet(NMItems.carbonIronHelmet, NMItems.carbonIronChestplate, NMItems.carbonIronLeggings, NMItems.carbonIronBoots,
                NMSkillNodes.BRING_CARBON_IRON_PLATE_8);
        gateArmorSet(NMItems.reinforcedIronHelmet, NMItems.reinforcedIronChestplate, NMItems.reinforcedIronLeggings, NMItems.reinforcedIronBoots,
                NMSkillNodes.BRING_REINFORCED_IRON_PLATE_64);
        SkillRecipeGates.crafting(BTWBlocks.infernalEnchanter.blockID, NMSkillNodes.BRING_AZURE_CERAMIC_PLATE_8);
        SkillRecipeGates.crafting(NMItems.heatResistantHelmet.itemID, NMSkillNodes.BRING_THERMAL_LAMINATE_4);
        SkillRecipeGates.crafting(NMItems.heatResistantChestplate.itemID, NMSkillNodes.BRING_THERMAL_LAMINATE_4);
        SkillRecipeGates.crafting(NMItems.heatResistantLeggings.itemID, NMSkillNodes.BRING_THERMAL_LAMINATE_4);
        SkillRecipeGates.crafting(NMItems.heatResistantBoots.itemID, NMSkillNodes.BRING_THERMAL_LAMINATE_4);
        SkillRecipeGates.crafting(NMItems.divingMask.itemID, NMSkillNodes.BRING_PRESSURE_REGULATOR_2);
        SkillRecipeGates.crafting(NMItems.divingTank.itemID, NMSkillNodes.BRING_PRESSURE_REGULATOR_2);
        SkillRecipeGates.crafting(NMItems.tungstenHelmet.itemID, NMSkillNodes.BRING_TUNGSTEN_PLATE_8);
        SkillRecipeGates.crafting(NMItems.tungstenChestplate.itemID, NMSkillNodes.BRING_TUNGSTEN_PLATE_8);
        SkillRecipeGates.crafting(NMItems.tungstenLeggings.itemID, NMSkillNodes.BRING_TUNGSTEN_PLATE_8);
        SkillRecipeGates.crafting(NMItems.tungstenBoots.itemID, NMSkillNodes.BRING_TUNGSTEN_PLATE_8);
        SkillRecipeGates.crafting(NMItems.coresteelHelmet.itemID, NMSkillNodes.BRING_CORESTEEL_INGOT_8);
        SkillRecipeGates.crafting(NMItems.coresteelChestplate.itemID, NMSkillNodes.BRING_CORESTEEL_INGOT_8);
        SkillRecipeGates.crafting(NMItems.coresteelLeggings.itemID, NMSkillNodes.BRING_CORESTEEL_INGOT_8);
        SkillRecipeGates.crafting(NMItems.coresteelBoots.itemID, NMSkillNodes.BRING_CORESTEEL_INGOT_8);
        SkillRecipeGates.crafting(NMItems.unstableDeadzoneCharge.itemID, NMSkillNodes.BRING_DEADZONE_SHARD_64);
        SkillRecipeGates.soulforge(NMItems.deadzoneAlloyIngot.itemID,
                NMSkillNodes.BRING_UNSTABLE_DEADZONE_CHARGE_4, NMSkillNodes.KILL_WITHER, NMSkillNodes.BRING_BLOOD_ARMOR_SET,
                NMSkillNodes.BRING_DIAMOND_ARMOR_SET, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16);
        SkillRecipeGates.crafting(NMItems.deadzoneHelmet.itemID, NMSkillNodes.BRING_UNSTABLE_DEADZONE_CHARGE_4);
        SkillRecipeGates.crafting(NMItems.deadzoneChestplate.itemID, NMSkillNodes.BRING_UNSTABLE_DEADZONE_CHARGE_4);
        SkillRecipeGates.crafting(NMItems.deadzoneLeggings.itemID, NMSkillNodes.BRING_UNSTABLE_DEADZONE_CHARGE_4);
        SkillRecipeGates.crafting(NMItems.deadzoneBoots.itemID, NMSkillNodes.BRING_UNSTABLE_DEADZONE_CHARGE_4);
        SkillRecipeGates.crafting(NMItems.solarCloth.itemID, NMSkillNodes.BRING_UNSTABLE_DEADZONE_CHARGE_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.crafting(NMItems.sunHelmet.itemID, NMSkillNodes.BRING_SOLAR_CLOTH_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.crafting(NMItems.sunChestplate.itemID, NMSkillNodes.BRING_SOLAR_CLOTH_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.crafting(NMItems.sunLeggings.itemID, NMSkillNodes.BRING_SOLAR_CLOTH_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.crafting(NMItems.sunBoots.itemID, NMSkillNodes.BRING_SOLAR_CLOTH_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.crafting(NMItems.sunVisor.itemID, NMSkillNodes.BRING_SOLAR_CLOTH_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.crafting(NMItems.sunReservoir.itemID, NMSkillNodes.BRING_SOLAR_CLOTH_4);
        gateArmorSet(NMItems.signalHelmet, NMItems.signalChestplate, NMItems.signalLeggings, NMItems.signalBoots,
                NMSkillNodes.BRING_SIGNAL_ALLOY_INGOT_8);
        gateArmorSet(NMItems.signalHelmet, NMItems.signalChestplate, NMItems.signalLeggings, NMItems.signalBoots,
                NMSkillNodes.BRING_SIGNAL_ALLOY_PLATE_8);
        gateArmorSet(NMItems.azureHelmet, NMItems.azureChestplate, NMItems.azureLeggings, NMItems.azureBoots,
                NMSkillNodes.BRING_AZURE_CERAMIC_INGOT_8);
        gateArmorSet(NMItems.azureHelmet, NMItems.azureChestplate, NMItems.azureLeggings, NMItems.azureBoots,
                NMSkillNodes.BRING_AZURE_CERAMIC_PLATE_8);
        gateArmorSet(NMItems.prismaticHelmet, NMItems.prismaticChestplate, NMItems.prismaticLeggings, NMItems.prismaticBoots,
                NMSkillNodes.BRING_PRISMATIC_INGOT_8);
        gateArmorSet(NMItems.prismaticHelmet, NMItems.prismaticChestplate, NMItems.prismaticLeggings, NMItems.prismaticBoots,
                NMSkillNodes.BRING_PRISMATIC_PLATE_8);
        gateArmorSet(NMItems.refinedPrismaHelmet, NMItems.refinedPrismaChestplate, NMItems.refinedPrismaLeggings, NMItems.refinedPrismaBoots,
                NMSkillNodes.BRING_REFINED_PRISMA_ARMOR);
        gateArmorSet(NMItems.refinedPrismaHelmet, NMItems.refinedPrismaChestplate, NMItems.refinedPrismaLeggings, NMItems.refinedPrismaBoots,
                NMSkillNodes.BRING_PRISMATIC_PLATE_8);
        gateArmorSet(NMItems.verdantHelmet, NMItems.verdantChestplate, NMItems.verdantLeggings, NMItems.verdantBoots,
                NMSkillNodes.BRING_VERDANT_PLATE_4);
        gateArmorSet(NMItems.glassHelmet, NMItems.glassChestplate, NMItems.glassLeggings, NMItems.glassBoots,
                NMSkillNodes.BRING_GLASS_ARMOR);
        gateArmorSet(NMItems.blackglassHelmet, NMItems.blackglassChestplate, NMItems.blackglassLeggings, NMItems.blackglassBoots,
                NMSkillNodes.BRING_BLACKGLASS_PLATE_4);
        gateArmorSet(NMItems.quartzglassHelmet, NMItems.quartzglassChestplate, NMItems.quartzglassLeggings, NMItems.quartzglassBoots,
                NMSkillNodes.BRING_QUARTZGLASS_INGOT_8);
        gateArmorSet(NMItems.quartzglassHelmet, NMItems.quartzglassChestplate, NMItems.quartzglassLeggings, NMItems.quartzglassBoots,
                NMSkillNodes.BRING_QUARTZGLASS_PLATE_8);
        SkillRecipeGates.crafting(NMItems.divingMask.itemID, NMSkillNodes.BRING_QUARTZGLASS_PLATE_8);
        gateArmorSet(NMItems.coresteelHelmet, NMItems.coresteelChestplate, NMItems.coresteelLeggings, NMItems.coresteelBoots,
                NMSkillNodes.BRING_CORESTEEL_PLATE_8);
        gateArmorSet(NMItems.deadzoneHelmet, NMItems.deadzoneChestplate, NMItems.deadzoneLeggings, NMItems.deadzoneBoots,
                NMSkillNodes.BRING_DEADZONE_ALLOY_PLATE_8);
            SkillRecipeGates.crafting(NMItems.solarCloth.itemID, NMSkillNodes.BRING_DEADZONE_ALLOY_PLATE_8);
        gateArmorSet(NMItems.darkHelmet, NMItems.darkChestplate, NMItems.darkLeggings, NMItems.darkBoots,
                NMSkillNodes.BRING_DARK_INGOT_8);

        SkillRecipeGates.crafting(Block.pistonBase.blockID, NMSkillNodes.BRING_REDSTONE_LATCH_16, NMSkillNodes.BRING_SOUL_URN_16, NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_DYE_BLEND_16);
        SkillRecipeGates.crafting(Block.music.blockID, NMSkillNodes.BRING_REDSTONE_LATCH_16);
        SkillRecipeGates.crafting(Item.comparator.itemID, NMSkillNodes.BRING_REDSTONE_EYE_16, NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_DYE_BLEND_16);
        SkillRecipeGates.crafting(BTWItems.corpseEye.itemID, NMSkillNodes.BRING_SOUL_URN_16);
        SkillRecipeGates.crafting(BTWItems.goldenDung.itemID, NMSkillNodes.BRING_DUNG_16);
        SkillRecipeGates.crafting(BTWItems.enderSpectacles.itemID, NMSkillNodes.BRING_OCULAR_OF_ENDER_8);
        SkillRecipeGates.crafting(Item.redstoneRepeater.itemID, NMSkillNodes.BRING_POCKET_SUNDIAL_8, NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_DYE_BLEND_16);
        SkillRecipeGates.crafting(Item.map.itemID, NMSkillNodes.BRING_COMPASS_8, NMSkillNodes.VISIT_UNIQUE_BIOME_4);
        SkillRecipeGates.crafting(Item.minecartEmpty.itemID, NMSkillNodes.BRING_RAIL_32);
        SkillRecipeGates.crafting(Item.minecartCrate.itemID, NMSkillNodes.BRING_MINECART_8);
        SkillRecipeGates.crafting(BTWBlocks.woodenDoor.blockID, NMSkillNodes.BRING_WOODEN_SIDING_32);
        SkillRecipeGates.crafting(Block.trapdoor.blockID, NMSkillNodes.BRING_WOODEN_SIDING_32);
        SkillRecipeGates.crafting(Item.bowlEmpty.itemID, NMSkillNodes.BRING_WOODEN_SIDING_32);
        SkillRecipeGates.crafting(Item.boat.itemID, NMSkillNodes.BRING_WOODEN_SIDING_32);
        SkillRecipeGates.crafting(Item.sign.itemID, NMSkillNodes.BRING_WOODEN_SIDING_32);

        SkillRecipeGates.crafting(BTWItems.dynamite.itemID, NMSkillNodes.BRING_FUSE_16, NMSkillNodes.BRING_BLASTING_OIL_16);
        SkillRecipeGates.crafting(Block.tnt.blockID, NMSkillNodes.BRING_FUSE_16);
        SkillRecipeGates.crafting(BTWItems.candle.itemID, NMSkillNodes.BRING_TALLOW_16);
        SkillRecipeGates.crafting(BTWBlocks.infernalEnchanter.blockID, NMSkillNodes.BRING_CANDLE_16);
        SkillRecipeGates.crafting(BTWItems.breadDough.itemID, NMSkillNodes.BRING_FLOUR_32);
        SkillRecipeGates.crafting(BTWItems.rawMushroomOmelet.itemID, NMSkillNodes.BRING_RAW_EGG_16, NMSkillNodes.BRING_BROWN_MUSHROOM_32);
        SkillRecipeGates.crafting(BTWItems.rawScrambledEggs.itemID, NMSkillNodes.BRING_RAW_EGG_16);
        SkillRecipeGates.crafting(BTWItems.unbakedPumpkinPie.itemID, NMSkillNodes.BRING_RAW_EGG_16, NMSkillNodes.BRING_PUMPKIN_16);
        SkillRecipeGates.crafting(Item.cake.itemID, NMSkillNodes.BRING_RAW_EGG_16);
        SkillRecipeGates.crafting(BTWItems.unbakedCookies.itemID, NMSkillNodes.BRING_CHOCOLATE_16);
        SkillRecipeGates.crafting(BTWBlocks.carvedPumpkin.blockID, NMSkillNodes.BRING_PUMPKIN_16);
        SkillRecipeGates.crafting(Block.pumpkinLantern.blockID, NMSkillNodes.BRING_PUMPKIN_16);
        SkillRecipeGates.crafting(Item.bowlSoup.itemID, NMSkillNodes.BRING_BROWN_MUSHROOM_32);
        SkillRecipeGates.crafting(BTWItems.rawKebab.itemID, NMSkillNodes.BRING_BROWN_MUSHROOM_32, NMSkillNodes.BRING_RAW_MUTTON_16);

        SkillRecipeGates.crafting(Block.workbench.blockID, NMSkillNodes.CRAFT_UNIQUE_RECIPE_OUTPUT_64);
        SkillRecipeGates.crafting(Item.leash.itemID, NMSkillNodes.TAME_ANIMAL_8);
        SkillRecipeGates.crafting(Item.paper.itemID, NMSkillNodes.BRING_SUGAR_CANE);
        SkillRecipeGates.crafting(BTWBlocks.finiteUnlitTorch.blockID, NMSkillNodes.BRING_FLINT_CHIP, NMSkillNodes.BRING_STICK_4, NMSkillNodes.BRING_GRAVEL_PILE_32, NMSkillNodes.BRING_DRILL_1);

        // Soulforge recipes are also player-owned through SlotCrafting.
        SkillRecipeGates.soulforge(BTWItems.tuningFork.itemID, NMSkillNodes.BRING_MUSIC_RECORD_16);
        SkillRecipeGates.soulforge(BTWItems.mail.itemID, NMSkillNodes.BRING_IRON_NUGGET_32);
        SkillRecipeGates.soulforge(BTWItems.steelSword.itemID, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_DIAMOND_STICK_16, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWItems.steelShovel.itemID, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_DIAMOND_STICK_16, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWItems.steelPickaxe.itemID, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_DIAMOND_STICK_16, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWItems.mattock.itemID, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_DIAMOND_STICK_16, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWItems.steelHoe.itemID, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_DIAMOND_STICK_16, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWItems.battleaxe.itemID, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_DIAMOND_STICK_16, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWItems.steelAxe.itemID, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_DIAMOND_STICK_16, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWItems.plateHelmet.itemID, NMSkillNodes.BRING_STEEL_ARMOR_PLATE_16, NMSkillNodes.BRING_DIAMOND_ARMOR_SET, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWItems.plateBreastplate.itemID, NMSkillNodes.BRING_STEEL_ARMOR_PLATE_16, NMSkillNodes.BRING_DIAMOND_ARMOR_SET, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWItems.plateLeggings.itemID, NMSkillNodes.BRING_STEEL_ARMOR_PLATE_16, NMSkillNodes.BRING_DIAMOND_ARMOR_SET, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWItems.plateBoots.itemID, NMSkillNodes.BRING_STEEL_ARMOR_PLATE_16, NMSkillNodes.BRING_DIAMOND_ARMOR_SET, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWBlocks.dormandSoulforge.blockID, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16, NMSkillNodes.BRING_DIAMOND_STICK_16, NMSkillNodes.BRING_DIAMOND_BRICK_4, NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4, NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_AQUAMARINE_64, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.soulforge(BTWBlocks.detectorBlock.blockID, NMSkillNodes.BRING_STEEL_PRESSURE_PLATE_8);

        SkillRecipeGates.crafting(NMItems.stoneHammer.itemID, NMSkillNodes.BRING_STONE_BRICK_32, NMSkillNodes.BRING_AQUAMARINE_16);

        SkillRecipeGates.crafting(Block.netherrack.blockID, 2, NMSkillNodes.BRING_NETHERRACK_TIER_ONE_64);
        SkillRecipeGates.crafting(Block.netherrack.blockID, 3, NMSkillNodes.BRING_NETHERRACK_TIER_TWO_64);
        SkillRecipeGates.crafting(Block.netherrack.blockID, 4, NMSkillNodes.BRING_NETHERRACK_TIER_THREE_64);
        SkillRecipeGates.crafting(NMItems.netherrackChunk.itemID,
                NMSkillNodes.BRING_NETHER_WORKBENCH_PART_4, NMSkillNodes.BRING_QUARTZ_DUST_32);
        SkillRecipeGates.crafting(NMItems.netherStick.itemID, NMSkillNodes.BRING_NETHERRACK_TIER_ONE_64);
        SkillRecipeGates.crafting(NMItems.netherWorkbenchPart.itemID,
                NMSkillNodes.BRING_QUARTZ_DUST_32, NMSkillNodes.BRING_TUNGSTEN_CHUNK_16);
        SkillRecipeGates.crafting(NMBlocks.netherWorkbench.blockID,
                NMSkillNodes.BRING_NETHER_WORKBENCH_PART_4, NMSkillNodes.BRING_QUARTZ_DUST_32);
        SkillRecipeGates.crafting(NMBlocks.netherrackAnvil.blockID, NMSkillNodes.BRING_NETHERRACK_TIER_ONE_64);
        SkillRecipeGates.crafting(NMItems.netherrackHammer.itemID,
                NMSkillNodes.BRING_NETHER_STICK_16, NMSkillNodes.BRING_NETHERRACK_TIER_ONE_64);
        SkillRecipeGates.crafting(NMItems.netherrackPickaxe.itemID,
                NMSkillNodes.BRING_NETHER_STICK_16, NMSkillNodes.BRING_NETHERRACK_CHUNK_16,
                NMSkillNodes.BRING_PIGHIDE_STRING_16);
        SkillRecipeGates.crafting(NMItems.netherFishingRod.itemID,
                NMSkillNodes.BRING_NETHER_STICK_16, NMSkillNodes.BRING_BONE_SHARD_16,
                NMSkillNodes.BRING_PIGHIDE_STRING_16);

        SkillRecipeGates.crafting(NMItems.tungstenChunk.itemID, NMSkillNodes.BRING_TUNGSTEN_DUST_32);
        SkillRecipeGates.crafting(NMItems.tungstenConcentrate.itemID,
                NMSkillNodes.BRING_CRUSHED_TUNGSTEN_16, NMSkillNodes.BRING_QUARTZ_16);
        SkillRecipeGates.crafting(NMItems.pureTungstenChunk.itemID, NMSkillNodes.BRING_TUNGSTEN_POWDER_32);
        SkillRecipeGates.crafting(NMItems.tungstenIngot.itemID, NMSkillNodes.BRING_TUNGSTEN_NUGGET_32);
        SkillRecipeGates.crafting(NMItems.tungstenBucket.itemID, NMSkillNodes.BRING_TUNGSTEN_INGOT_8);
        SkillRecipeGates.crafting(NMItems.tungstenPickaxe.itemID,
                NMSkillNodes.BRING_TUNGSTEN_INGOT_8, NMSkillNodes.BRING_NETHER_STICK_16);
        SkillRecipeGates.crafting(NMItems.tungstenShovel.itemID,
                NMSkillNodes.BRING_TUNGSTEN_INGOT_8, NMSkillNodes.BRING_NETHER_STICK_16);
        SkillRecipeGates.crafting(NMItems.tungstenKnife.itemID,
                NMSkillNodes.BRING_TUNGSTEN_INGOT_8, NMSkillNodes.BRING_NETHER_STICK_16);
        SkillRecipeGates.crafting(NMItems.tungstenScythe.itemID,
                NMSkillNodes.BRING_TUNGSTEN_INGOT_8, NMSkillNodes.BRING_NETHER_STICK_16);
        SkillRecipeGates.crafting(NMBlocks.obsidianMillstone.blockID,
                NMSkillNodes.BRING_OBSIDIAN_BRICK_16, NMSkillNodes.BRING_NETHERRACK_CHUNK_16);
        SkillRecipeGates.crafting(NMBlocks.minerDrill.blockID,
                NMSkillNodes.BRING_TUNGSTEN_INGOT_8, NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16);
        SkillRecipeGates.crafting(NMBlocks.cisternInterface.blockID,
                NMSkillNodes.BRING_TUNGSTEN_INGOT_8, NMSkillNodes.BRING_AZURE_SALT_16);
        SkillRecipeGates.crafting(NMBlocks.chunkLoader.blockID,
                NMSkillNodes.BRING_NETHERRACK_TIER_THREE_256, NMSkillNodes.BRING_DEADZONE_SHARD_64);

        SkillRecipeGates.crafting(NMItems.stoneKnife.itemID, NMSkillNodes.BRING_SHARP_STONE_4);
        SkillRecipeGates.crafting(NMItems.ironKnife.itemID, NMSkillNodes.BRING_IRON_INGOT_16);
        SkillRecipeGates.crafting(NMItems.diamondKnife.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8);
        SkillRecipeGates.crafting(NMItems.goldKnife.itemID, NMSkillNodes.BRING_GOLD_INGOT_16);
        SkillRecipeGates.crafting(NMItems.ironScythe.itemID, NMSkillNodes.BRING_IRON_INGOT_16);
        SkillRecipeGates.crafting(NMItems.diamondScythe.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8);

        SkillRecipeGates.crafting(NMItems.potassiumFertilizer.itemID, NMSkillNodes.BRING_POTASSIUM_CRYSTAL_16);
        SkillRecipeGates.crafting(NMItems.acidityFertilizer.itemID, NMSkillNodes.BRING_ACID_CRYSTAL_16);
        SkillRecipeGates.crafting(NMItems.porosityFertilizer.itemID, NMSkillNodes.BRING_POROSITY_AGGREGATE_16);
        SkillRecipeGates.crafting(Item.dyePowder.itemID, 15, NMSkillNodes.BRING_NITROGEN_CRYSTAL_16);

        SkillRecipeGates.crafting(NMItems.twigSharpening.itemID, NMSkillNodes.BRING_GRAVEL_PILE_32);
        SkillRecipeGates.crafting(NMItems.sharpTwigBarkWrapping.itemID, NMSkillNodes.BRING_BARK_16);
        SkillRecipeGates.crafting(NMItems.scrapedBark.itemID, NMSkillNodes.BRING_SHARP_STONE_4);
        SkillRecipeGates.crafting(NMItems.crudeStringCrafting.itemID, NMSkillNodes.BRING_DRIED_PLANT_FIBER_64);
        SkillRecipeGates.crafting(NMItems.primitiveGlue.itemID, NMSkillNodes.BRING_COAL_DUST_32);
        SkillRecipeGates.crafting(NMItems.woodCupCrafting.itemID, NMSkillNodes.BRING_SHARP_STONE_4);
        SkillRecipeGates.crafting(NMItems.reedPeeling.itemID, NMSkillNodes.BRING_SUGAR_CANE);
        SkillRecipeGates.crafting(NMItems.pileOfSticks.itemID, NMSkillNodes.BRING_STICK_4);
        SkillRecipeGates.crafting(NMItems.soulFlint.itemID, NMSkillNodes.BRING_SOUL_CHIP_16);
        SkillRecipeGates.crafting(NMItems.pighideStringCrafting.itemID, NMSkillNodes.BRING_PIG_HIDE_16);
        SkillRecipeGates.crafting(BTWItems.netherSludge.itemID,
                NMSkillNodes.BRING_GROUND_NETHERRACK_32, NMSkillNodes.BRING_ASH_CLUMP_16);
        SkillRecipeGates.crafting(NMBlocks.hellforge.blockID,
                NMSkillNodes.BRING_NETHER_BRICK_32, NMSkillNodes.BRING_NETHERRACK_TIER_ONE_64);
        SkillRecipeGates.crafting(Block.obsidian.blockID, NMSkillNodes.BRING_OBSIDIAN_SHARD_16);
        SkillRecipeGates.crafting(NMItems.refinedRedstone.itemID,
                NMSkillNodes.BRING_REDSTONE_BLOCK_16, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4);
        SkillRecipeGates.crafting(NMItems.hydraulicLens.itemID,
                NMSkillNodes.BRING_AQUAMARINE_16, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4);
        SkillRecipeGates.crafting(NMItems.fluidGauge.itemID,
                NMSkillNodes.BRING_AQUAMARINE_16, NMSkillNodes.BRING_NICKEL_PLATE_4);
        SkillRecipeGates.crafting(NMItems.invocationSeal.itemID,
                NMSkillNodes.BRING_GHAST_TEAR_16, NMSkillNodes.BRING_INVOCATION_FRAGMENT_4);
        SkillRecipeGates.crafting(NMItems.endAccord.itemID,
                NMSkillNodes.BRING_END_ACCORD_FRAGMENT_4, NMSkillNodes.KILL_WITHER);
        SkillRecipeGates.crafting(NMBlocks.cisternStirrer.blockID,
                NMSkillNodes.BRING_TUNGSTEN_INGOT_8, NMSkillNodes.BRING_GEAR_64);
        SkillRecipeGates.crafting(NMBlocks.cisternDrain.blockID,
                NMSkillNodes.BRING_TUNGSTEN_INGOT_8, NMSkillNodes.BRING_GLUE_SLURRY_16);
        SkillRecipeGates.crafting(NMBlocks.chuteHopper.blockID,
                NMSkillNodes.BRING_IRON_BRICK_64, NMSkillNodes.BRING_NICKEL_PLATE_4);
        SkillRecipeGates.crafting(NMItems.highSpeedMinecart.itemID,
                NMSkillNodes.BRING_MINECART_8, NMSkillNodes.BRING_TUNGSTEN_INGOT_8);
        SkillRecipeGates.crafting(NMItems.highSpeedChestMinecart.itemID,
                NMSkillNodes.BRING_MINECART_8, NMSkillNodes.BRING_TUNGSTEN_INGOT_8);
        SkillRecipeGates.crafting(NMItems.highSpeedFurnaceMinecart.itemID,
                NMSkillNodes.BRING_MINECART_8, NMSkillNodes.BRING_TUNGSTEN_INGOT_8);
        SkillRecipeGates.crafting(Item.shovelWood.itemID, NMSkillNodes.BRING_STICK_4);
        SkillRecipeGates.crafting(NMItems.woodHammer.itemID, NMSkillNodes.BRING_STICK_4);
        SkillRecipeGates.crafting(Item.pickaxeStone.itemID, NMSkillNodes.BRING_LOOSE_STONE_64);
        SkillRecipeGates.crafting(Item.axeStone.itemID, NMSkillNodes.BRING_LOOSE_STONE_64);
        SkillRecipeGates.crafting(Item.shovelStone.itemID, NMSkillNodes.BRING_LOOSE_STONE_64);
        SkillRecipeGates.crafting(Item.flint.itemID, NMSkillNodes.BRING_FLINT_CHIP);
        SkillRecipeGates.crafting(NMItems.unshapedWetClayBrick.itemID, NMSkillNodes.BRING_CLAY_64);
        SkillRecipeGates.crafting(NMBlocks.stoneAnvil.blockID,
                NMSkillNodes.BRING_LOOSE_STONE_64, NMSkillNodes.BRING_STONE_BRICK_32);
        SkillRecipeGates.crafting(NMItems.lithiumSalt.itemID, NMSkillNodes.BRING_RAW_LITHIUM_64);
        SkillRecipeGates.crafting(NMItems.lithiumHeatCompound.itemID,
                NMSkillNodes.BRING_RAW_LITHIUM_64, NMSkillNodes.BRING_NICKEL_PLATE_4,
                NMSkillNodes.BRING_POTASSIUM_CRYSTAL_16);
        SkillRecipeGates.crafting(NMItems.nickelBinding.itemID, NMSkillNodes.BRING_NICKEL_PLATE_4);
        SkillRecipeGates.crafting(NMItems.oxygenTank.itemID,
                NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_IRON_INGOT_16);
        SkillRecipeGates.crafting(NMItems.seededDiamondMatrix.itemID,
                NMSkillNodes.BRING_DIAMOND_BEARING_ROCK_64, NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4);
        SkillRecipeGates.crafting(NMItems.nickelBoundDiamondMatrix.itemID,
                NMSkillNodes.BRING_NICKEL_PLATE_4, NMSkillNodes.BRING_RAW_LITHIUM_64);
        SkillRecipeGates.crafting(NMItems.fishFlesh.itemID, NMSkillNodes.CATCH_FISH_50);
        SkillRecipeGates.crafting(NMItems.moistureFertilizer.itemID, NMSkillNodes.CRAFT_BOOK_64);
        SkillRecipeGates.crafting(NMItems.glassBatch.itemID,
                NMSkillNodes.BRING_ACID_CRYSTAL_16, NMSkillNodes.BRING_POROSITY_AGGREGATE_16);

        // old initializer crafting outputs are registered before this pass, so the same
        // output gates replace their formerly unrestricted behavior without duplicating recipes.
        SkillRecipeGates.crafting(Block.bookShelf.blockID, NMSkillNodes.CRAFT_BOOK_64);
        SkillRecipeGates.crafting(BTWBlocks.creeperOysterBlock.blockID, NMSkillNodes.BRING_CREEPER_OYSTER_64);
        SkillRecipeGates.crafting(BTWBlocks.rottenFleshBlock.blockID, NMSkillNodes.BRING_ROTTEN_FLESH_BLOCK_64);
        SkillRecipeGates.crafting(BTWBlocks.spiderEyeBlock.blockID, NMSkillNodes.BRING_SPIDER_EYE_64);
        SkillRecipeGates.crafting(BTWItems.gear.itemID, NMSkillNodes.BRING_STICK_16);
        SkillRecipeGates.crafting(BTWItems.steelNugget.itemID, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_8);
        SkillRecipeGates.crafting(NMItems.eclipseBow.itemID, NMSkillNodes.FIRE_ARROW_256);
        SkillRecipeGates.crafting(NMItems.ironKnittingNeedles.itemID,
                NMSkillNodes.BRING_IRON_NUGGET_32, NMSkillNodes.BRING_WOOL_16);
        SkillRecipeGates.crafting(NMItems.magicArrow.itemID,
                NMSkillNodes.FIRE_ARROW_256, NMSkillNodes.BRING_BROADHEAD_ARROWHEAD_16);
        SkillRecipeGates.crafting(NMItems.steelBunch.itemID, NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_8);
        SkillRecipeGates.crafting(NMItems.elementalRod.itemID,
                NMSkillNodes.BRING_BLAZE_ROD_16, NMSkillNodes.BRING_GHAST_TEAR_16);
        SkillRecipeGates.crafting(NMBlocks.blockRoad.blockID, NMSkillNodes.BRING_GRAVEL_64);
        SkillRecipeGates.crafting(NMBlocks.blockAsphalt.blockID, NMSkillNodes.BRING_ROAD_BLOCK_64);
        SkillRecipeGates.crafting(NMBlocks.asphaltLayer.blockID, NMSkillNodes.BRING_ROAD_BLOCK_64);
        SkillRecipeGates.crafting(NMBlocks.steelLocker.blockID, NMSkillNodes.BRING_STEEL_BUNCH_8);
        SkillRecipeGates.crafting(NMItems.bloodSword.itemID, NMSkillNodes.BRING_BLOOD_INGOT_16);
        SkillRecipeGates.crafting(NMItems.bloodPickaxe.itemID, NMSkillNodes.BRING_BLOOD_INGOT_16);
        SkillRecipeGates.crafting(NMItems.bloodAxe.itemID, NMSkillNodes.BRING_BLOOD_INGOT_16);
        SkillRecipeGates.crafting(NMItems.bloodShovel.itemID, NMSkillNodes.BRING_BLOOD_INGOT_16);
        SkillRecipeGates.crafting(NMItems.bloodHoe.itemID, NMSkillNodes.BRING_BLOOD_INGOT_16);
        SkillRecipeGates.crafting(NMBlocks.bloodChest.blockID, NMSkillNodes.BRING_BLOOD_ORB_64);
        SkillRecipeGates.crafting(BTWBlocks.aestheticOpaque.blockID, 15, NMSkillNodes.BRING_BONE_128);
        SkillRecipeGates.crafting(BTWBlocks.chest.blockID, NMSkillNodes.BRING_WOODEN_SIDING_32);
        SkillRecipeGates.crafting(BTWBlocks.planter.blockID, NMSkillNodes.BRING_CLAY_BLOCK_32);
        SkillRecipeGates.crafting(BTWItems.curedMeat.itemID, NMSkillNodes.COOK_FOOD_200);
        SkillRecipeGates.crafting(BTWItems.rawMysteryMeat.itemID, NMSkillNodes.BRING_BLOOD_ORB_64);
        SkillRecipeGates.crafting(BTWItems.tastySandwich.itemID, NMSkillNodes.COOK_FOOD_200);
        SkillRecipeGates.crafting(NMItems.dungApple.itemID, NMSkillNodes.BRING_DUNG_16);
        SkillRecipeGates.crafting(NMItems.ironFishingPole.itemID,
                NMSkillNodes.BRING_IRON_INGOT_16, NMSkillNodes.BRING_BONE_FISH_HOOK_8);
        SkillRecipeGates.crafting(NMBlocks.stoneLadder.blockID, NMSkillNodes.BRING_LADDER_64);
        SkillRecipeGates.crafting(NMBlocks.ironLadder.blockID, NMSkillNodes.BRING_STONE_LADDER_64);
        SkillRecipeGates.crafting(NMBlocks.bloodSaw.blockID,
                NMSkillNodes.BRING_BLOOD_INGOT_16, NMSkillNodes.BRING_SAW);
        SkillRecipeGates.crafting(NMPostItems.bloodMoonBottle.itemID, NMSkillNodes.BRING_BLOOD_ORB_64);
        SkillRecipeGates.crafting(Item.appleGold.itemID, 0, NMSkillNodes.BRING_GOLD_NUGGET_32);
        SkillRecipeGates.crafting(Item.appleGold.itemID, 1, NMSkillNodes.BRING_GOLD_INGOT_16);
        SkillRecipeGates.crafting(Item.goldenCarrot.itemID, NMSkillNodes.BRING_GOLD_INGOT_16);
        SkillRecipeGates.crafting(BTWItems.bowDrill.itemID,
                NMSkillNodes.BRING_STICK_4, NMSkillNodes.BRING_STRING_32);
        SkillRecipeGates.crafting(Item.flintAndSteel.itemID,
                NMSkillNodes.BRING_FLINT_4, NMSkillNodes.BRING_IRON_NUGGET_32);
        SkillRecipeGates.crafting(BTWItems.diamondChisel.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8);
        SkillRecipeGates.crafting(BTWItems.diamondShears.itemID, NMSkillNodes.BRING_DIAMOND_INGOT_8);
        SkillRecipeGates.crafting(BTWItems.creeperOysters.itemID, NMSkillNodes.BRING_CREEPER_OYSTER_16);
        SkillRecipeGates.crafting(BTWItems.wickerPane.itemID,
                NMSkillNodes.BRING_SUGAR_CANE_16, NMSkillNodes.BRING_KNITTING_NEEDLE_4);
        SkillRecipeGates.crafting(Item.book.itemID, NMSkillNodes.BRING_PAPER_64);
        SkillRecipeGates.crafting(Item.silk.itemID, NMSkillNodes.BRING_KNITTING_NEEDLE_4);
        SkillRecipeGates.crafting(Item.spiderEye.itemID, NMSkillNodes.KILL_SPIDER_100);

        // Cauldrons, crucibles, millstones, and cisterns have no initiating player: hoppers
        // can start their recipes after every player has left. Personal skill nodes must not
        // be attached to those managers. Their locking support is reserved for deliberate
        // world-reward nodes; no personal node is promoted to a world reward to fit it.

        SkillRecipeGates.crafting(NMBlocks.enderCeramic.blockID, NMSkillNodes.BRING_EYE_OF_ENDER_ECLIPSE);
        SkillRecipeGates.soulforge(NMBlocks.enderAssembler.blockID, NMSkillNodes.BRING_EYE_OF_ENDER_ECLIPSE);
        SkillRecipeGates.soulforge(NMBlocks.minerDrillTier4.blockID, NMSkillNodes.BRING_PHASE_STEEL_8);
        SkillRecipeGates.soulforge(NMItems.enderSword.itemID, NMSkillNodes.BRING_PHASE_STEEL_8);
        SkillRecipeGates.soulforge(NMItems.enderPickaxe.itemID, NMSkillNodes.BRING_PHASE_STEEL_8);
        SkillRecipeGates.soulforge(NMItems.enderAxe.itemID, NMSkillNodes.BRING_PHASE_STEEL_8);
        SkillRecipeGates.soulforge(NMItems.enderShovel.itemID, NMSkillNodes.BRING_PHASE_STEEL_8);
        SkillRecipeGates.soulforge(NMItems.enderHoe.itemID, NMSkillNodes.BRING_PHASE_STEEL_8);
        SkillRecipeGates.soulforge(NMItems.enderHelmet.itemID, NMSkillNodes.BRING_PHASE_STEEL_8);
        SkillRecipeGates.soulforge(NMItems.enderChestplate.itemID, NMSkillNodes.BRING_PHASE_STEEL_8);
        SkillRecipeGates.soulforge(NMItems.enderLeggings.itemID, NMSkillNodes.BRING_PHASE_STEEL_8);
        SkillRecipeGates.soulforge(NMItems.enderBoots.itemID, NMSkillNodes.BRING_PHASE_STEEL_8);

        finishRecipes("Skill Gates");
    }

    private static void addHammerRecipes(){
        HammerRecipeList.addRecipes();
    }

    private static void addTurntableRecipes() {
        RecipeManager.addTurntableRecipe(NMBlocks.enderCeramic, 1, NMBlocks.enderCeramic, 0, 8);
        RecipeManager.addKilnRecipe(new ItemStack(NMItems.firedCrucibleLiner), NMBlocks.enderCeramic, 1, (byte)8);
    }

    private static void addEnderAssemblerRecipes() {
        EnderAssemblerRecipeManager manager = EnderAssemblerRecipeManager.instance;
        manager.addRecipe(new ItemStack(NMItems.phaseSteelCharge), 300,
                new ItemStack(BTWItems.soulforgedSteelIngot, 1), new ItemStack(NMItems.mercuryAmalgam, 8),
                new ItemStack(NMItems.enderDust, 4), new ItemStack(NMItems.enderShellPowder, 1),
                new ItemStack(NMItems.paleRootResin, 4), new ItemStack(NMItems.darksunFragment, 3));
        manager.addRecipe(new ItemStack(NMItems.enderMechanism), 700,
                new ItemStack(NMItems.phaseSteelPlate, 4), new ItemStack(NMItems.crystalPrecisionGear),
                new ItemStack(NMItems.enderCrystal, 8), new ItemStack(NMItems.nickelMachinePart, 2),
                new ItemStack(NMBlocks.netherProgressionGems, 1, NMBlocks.META_PURPLE_GEM),
                new ItemStack(NMItems.endAccordFragment), new ItemStack(NMItems.sealedQuicksilverPlate));
        manager.addRecipe(new ItemStack(NMItems.sealedQuicksilverIngot), 300,
                new ItemStack(NMItems.mercuryAmalgam), new ItemStack(NMItems.tungstenNugget, 4),
                new ItemStack(NMItems.waxedGasket), new ItemStack(NMItems.enderDust, 2),
                new ItemStack(Item.ingotIron));
        addEndArmorRecipes(manager);
    }

    private static void addEndArmorRecipes(EnderAssemblerRecipeManager manager) {
        addEndArmorRecipe(manager, NMItems.quicksilverHelmet, NMItems.sealedQuicksilverPlate, 3, NMItems.nickelBinding, NMItems.waxedGasket);
        addEndArmorRecipe(manager, NMItems.quicksilverChestplate, NMItems.sealedQuicksilverPlate, 5, NMItems.nickelBinding, BTWItems.fabric);
        addEndArmorRecipe(manager, NMItems.quicksilverLeggings, NMItems.sealedQuicksilverPlate, 4, NMItems.waxedGasket, BTWItems.fabric);
        addEndArmorRecipe(manager, NMItems.quicksilverBoots, NMItems.sealedQuicksilverPlate, 2, NMItems.nickelBinding, NMItems.waxedGasket);
        addEndArmorRecipe(manager, NMItems.anchorHelmet, NMItems.endstonePlate, 3, NMItems.phaseSteelIngot, NMItems.paleRootResin);
        addEndArmorRecipe(manager, NMItems.anchorChestplate, NMItems.endstonePlate, 5, NMItems.phaseSteelIngot, BTWItems.fabric);
        addEndArmorRecipe(manager, NMItems.anchorLeggings, NMItems.endstonePlate, 4, NMItems.tungstenIngot, NMItems.paleRootResin);
        addEndArmorRecipe(manager, NMItems.anchorBoots, NMItems.endstonePlate, 2, NMItems.tungstenIngot, BTWItems.fabric);
    }

    private static void addEndArmorRecipe(EnderAssemblerRecipeManager manager, Item output, Item plate,
                                           int plateCount, Item structure, Item seal) {
        manager.addRecipe(new ItemStack(output), 450, new ItemStack(plate, plateCount),
                new ItemStack(structure, 2), new ItemStack(seal, 2));
    }

    private static void gateArmorSet(Item helmet, Item chestplate, Item leggings, Item boots, SkillNode skill) {
        SkillRecipeGates.crafting(helmet.itemID, skill);
        SkillRecipeGates.crafting(chestplate.itemID, skill);
        SkillRecipeGates.crafting(leggings.itemID, skill);
        SkillRecipeGates.crafting(boots.itemID, skill);
    }

    private static void normalizeWoodSawOutputs() {
        for (SawRecipe recipe : SawCraftingManager.instance.getRecipes()) {
            if (recipe.getInputblock().blockMaterial.materialMapColor.colorIndex != Material.wood.materialMapColor.colorIndex) {
                continue;
            }
            for (ItemStack output : recipe.getOutput()) {
                output.stackSize = 1;
            }
        }
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
