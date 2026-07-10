package com.itlesports.nightmaremode.util;

import api.achievement.AchievementTab;
import api.entity.mob.villager.TradeItem;
import api.entity.mob.villager.TradeProvider;
import api.item.tag.TagInstance;
import api.item.tag.TagOrStack;
import api.util.color.Color;
import btw.achievement.BTWAchievements;
import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.crafting.manager.CauldronCraftingManager;
import btw.crafting.manager.CrucibleStokedCraftingManager;
import btw.crafting.manager.MillStoneCraftingManager;
import btw.crafting.manager.PistonPackingCraftingManager;
import btw.crafting.recipe.RecipeManager;
import btw.item.BTWItems;
import btw.item.BTWTags;
import btw.util.BTWDamageSources;
import com.itlesports.nightmaremode.achievements.AchievementExt;
import com.itlesports.nightmaremode.achievements.NMAchievements;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.*;
import com.itlesports.nightmaremode.entity.creepers.*;
import com.itlesports.nightmaremode.entity.underworld.EntityWalker;
import com.itlesports.nightmaremode.entity.variants.*;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.NMTags;
import com.itlesports.nightmaremode.item.NMPostItems;
import com.itlesports.nightmaremode.mixin.interfaces.AchievementAccessor;
import com.itlesports.nightmaremode.mixin.biomegen.BiomeGenBaseAccessor;
import com.itlesports.nightmaremode.mixin.interfaces.ItemInvoker;
import com.itlesports.nightmaremode.mixin.interfaces.StatListAccess;
import com.itlesports.nightmaremode.tradetweaks.TradeTweaks;
import com.itlesports.nightmaremode.util.elements.BloodSawCraftingManager;
import com.itlesports.nightmaremode.util.elements.NMBeaconEffects;
import com.itlesports.nightmaremode.util.interfaces.DamageSourceExt;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

import java.util.Set;
import java.util.function.Predicate;

import static btw.achievement.BTWAchievements.*;
import static com.itlesports.nightmaremode.achievements.NMAchievements.*;

public abstract class NMInitializer implements AchievementExt {
    private static void finishRecipes(String type){System.out.println("Finished initializing: [" + type + "]");}

    public static void initNightmareRecipes(){
        addCraftingRecipes();
        addCampfireRecipes();
        addCrucibleRecipes();
        addCauldronRecipes();
        addMillstoneRecipes();
        addOvenRecipes();
        addSoulforgeRecipes();
        addPistonPackingRecipes();
//        addBloodSawRecipes();
        addMultiplayerRecipes();
        finishRecipes("All Recipes");
    }

    public static void initBeaconEffects(){
        NMBeaconEffects.initializeEffectsByBlockID();
    }

    public static void runItemPostInit(){
        NMPostItems.runPostInit();
        NMItems.addItemsToTags();
        finishRecipes("Item Post-Initialization");

    }
    public static void runDevModePostInit(){
        if(NightmareMode.devMode) return;
        // hides dev-build exclusive features & code from noobs

        NMItems.hideItems();
        NMBlocks.hideBlocks();
        finishRecipes("Dev mode Item/Block hiding");

    }
    public static void initMobSpawning(){
        addMobToMushroomIslands(EntityGhast.class, 1, 1, 1);
        addMobToMushroomIslands(EntityFauxVillager.class, 2, 1, 1);
        addMobToMushroomIslands(EntityMushWorm.class, 2, 1, 1);

        addMobToEnd(EntityCreeper.class, 1, 1, 3);

        addMobToNether(EntityCreeper.class, 15,1,1);
        addMobToNether(EntityFireCreeper.class, 40,1,1);
        addMobToNether(EntityBloodMoonSkeleton.class, 30,1,3);
        addMobToNether(EntityShadowZombie.class, 20,1,1);
        addMobToNether(EntityObsidianCreeper.class, 4,1,1);


        addMobToAllBiomes(EntityFireCreeper.class, 4, 1, 2);
        addMobToAllBiomes(EntityGhast.class, 1, 1, 1);
        addMobToAllBiomes(EntityFireSpider.class, 1, 1, 2);
        addMobToAllBiomes(EntityStoneZombie.class, 1, 1, 2);
        addMobToAllBiomes(EntityObsidianCreeper.class, 3, 1, 1);
        addMobToAllBiomes(EntityNitroCreeper.class, 1, 1, 2);
        addMobToAllBiomes(EntityBlackWidowSpider.class, 2, 1, 2);
        addMobToAllBiomes(EntityRadioactiveEnderman.class, 1, 1, 1);
        addMobToAllBiomes(EntityDungCreeper.class, 3, 1, 1);
        addMobToAllBiomes(EntityVoidCreeper.class, 2, 1, 1);
        addMobToAllBiomes(EntityGelCreeper.class, 3, 1, 1);
        addMobToAllBiomes(EntityGlitchCreeper.class, 1, 1, 1);
        addMobToAllBiomes(EntityLightningCreeper.class, 1, 1, 1);
        addMobToAllBiomes(EntityBloodZombie.class, 2, 1, 1);
        addMobToAllBiomes(EntityFauxVillager.class, 1, 1, 1);
        addMobToAllBiomes(EntityCreeperGhast.class, 1, 1, 2);
        addMobToAllBiomes(EntityWalker.class, 1, 1, 1);

        if (NightmareMode.magicMonsters != null && NightmareMode.magicMonsters) {
            clearAllLandBiomes();
            addMobToAllLandBiomes(EntityWitch.class, 3, 1, 2);
            addMobToNether(EntityWitch.class,40,1,2);
            addMobToEnd(EntityWitch.class,4,1,2);
            clearAllWaterBiomes();
        }
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
        addParent(FIND_SHAFT, NMAchievements.MORNING_SECOND_DAY);
        addParent(FIND_REEDS, NMAchievements.MORNING_SECOND_DAY);
        addParent(FIND_BONES, NMAchievements.MORNING_SECOND_DAY);

        addParent(COOK_FOOD, NMAchievements.KILL_ANIMAL);


        setHidden(CRAFT_HAMPER,true);
//        move(CRAFT_HAMPER, -1, 0);
//
//        move(BTWAchievements.CRAFT_PLANTER,);
//        removeParent(BTWAchievements.CRAFT_PLANTER, STOKE_FIRE);
//        removeParent(BTWAchievements.CRAFT_PLANTER, SPIN_POTTERY);
        kill(BTWAchievements.CRAFT_PLANTER);
        kill(USE_DIAMOND_PILE);
        kill(EQUIP_WOOL_ARMOR);

        move(FIND_REEDS,-1,0);
        move(CRAFT_BASKET,-2,0);
        move(CRAFT_KNITTING_NEEDLES,-1,0);
        move(CRAFT_WOOL_KNIT,-1,0);
        move(MINE_DIAMOND_ORE, 0, 1);

        move(PLACE_TOOL, 3, 0);
        move(MINE_DIAMOND_ORE, 0, -5);


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

        kill(EXPLODED_WITH_BLASTING_OIL);
        kill(MAKE_COMPANION_SLAB);
        move(POWER_THE_CAKE, -2, 0);
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
        // farmer
        TradeTweaks.setInputCount("btw:buy_loose_dirt", 12, 24);
        TradeTweaks.setInputCount("btw:buy_logs", 8,16);
            TradeTweaks.setInputCount("btw:buy_logs_variant_oak", 12,16);
            TradeTweaks.setInputCount("btw:buy_logs_variant_spruce", 8,16);
            TradeTweaks.setInputCount("btw:buy_logs_variant_birch", 12,16);
            TradeTweaks.setInputCount("btw:buy_logs_variant_jungle", 4,8);
        TradeTweaks.setInputCount("btw:buy_brown_wool", 2,4);
        TradeTweaks.setOutputCount("btw:buy_brown_wool", 2,3);
        TradeTweaks.setInputCount("btw:buy_bone_meal", 6,12);
        TradeTweaks.setOutputCount("btw:buy_iron_hoe", 1,2);
        TradeTweaks.setInputCount("btw:buy_sugar", 6,12);
        TradeTweaks.setInputCount("btw:buy_cocoa_beans", 4,8);
//        TradeTweaks.setInputCount("btw:buy_brown_mushrooms", 3,6);
//        TradeTweaks.dropTrade("btw:buy_brown_mushrooms");
        TradeTweaks.setInputCount("btw:buy_hemp_seeds", 4,8);
        TradeTweaks.setInputCount("btw:buy_eggs", 2,6);
        TradeTweaks.setInputCount("btw:buy_glass_panes", 16,16);
        TradeTweaks.setInputCount("btw:buy_milk_bucket", 1,1);
        TradeTweaks.setOutputCount("btw:sell_wheat", 4,8);

//        TradeTweaks.setInputCount("btw:sell_apple", 0,0);
//        TradeTweaks.setInputCount("btw:buy_millstone", 0,0);
        TradeTweaks.setInputCount("btw:sell_sugar_cane_roots", 1,1);
        TradeTweaks.setOutputCount("btw:sell_sugar_cane_roots", 2,3);
        TradeTweaks.setInputCount("btw:buy_melons", 2,4);
        TradeTweaks.setInputCount("btw:buy_pumpkins", 2,4);
//        TradeTweaks.setInputCount("btw:buy_chocolate", 0,0);
        TradeTweaks.setOutputCount("btw:buy_shears", 2,3);
//        TradeTweaks.setInputCount("btw:buy_flint_and_steel", 0,0);
        TradeTweaks.setOutputCount("btw:buy_flint_and_steel", 1,2);
//        TradeTweaks.setInputCount("btw:buy_soap", 0,0);
//        TradeTweaks.setInputCount("btw:sell_bread", 0,0);
//        TradeTweaks.setInputCount("btw:sell_egg_foods", 0,0);
//        TradeTweaks.setInputCount("btw:buy_water_wheel", 0,0);
//        TradeTweaks.setInputCount("btw:buy_cement_bucket", 0,0);
        TradeTweaks.setInputCount("btw:buy_light_block", 1,2);
        TradeTweaks.setInputCount("btw:buy_stump_remover", 1,2);
//        TradeTweaks.setInputCount("btw:sell_desserts", 1,3); // does nothing cause variant trade
//        TradeTweaks.setInputCount("btw:buy_stake_and_string", 1,1);
//        TradeTweaks.setInputCount("btw:buy_planters", 4,8);
//        TradeTweaks.setInputCount("btw:sell_mycelium", 0,0);
//        TradeTweaks.setInputCount("btw:sell_looting_scroll", 0,0);





        // librarian
        TradeTweaks.setInputCount("btw:buy_paper", 4,8);
        TradeTweaks.setInputCount("btw:buy_ink", 4,6);
        TradeTweaks.setInputCount("btw:buy_feathers", 4,6);
//        TradeTweaks.setInputCount("btw:buy_book_and_quill", 0,0);
//        TradeTweaks.setInputCount("btw:buy_ancient_manuscript", 0,0);
//        TradeTweaks.setInputCount("btw:buy_redstone", 0,0);
        TradeTweaks.setInputCount("btw:buy_redstone_latch", 1,2);
//        TradeTweaks.setInputCount("btw:buy_piston", 0,0);
//        TradeTweaks.setInputCount("btw:buy_turntable", 0,0);
        TradeTweaks.setOutputCount("btw:buy_brewing_stand", 4,4);
//        TradeTweaks.setInputCount("btw:sell_advanced_redstone", 0,0); // does nothing because it's a variant trade
        TradeTweaks.setInputCount("btw:buy_nether_wart", 4,8);
        TradeTweaks.setInputCount("btw:buy_glowstone", 8,16);
        TradeTweaks.setInputCount("btw:buy_nitre", 16,24);
        TradeTweaks.setInputCount("btw:buy_spider_eyes", 2,4);
//        TradeTweaks.setInputCount("btw:sell_bookshelf", 0,0);
        TradeTweaks.setInputCount("btw:buy_witch_warts", 1,2);
        TradeTweaks.setInputCount("btw:buy_mysterious_glands", 4,6);
        TradeTweaks.setInputCount("btw:buy_fermented_spider_eyes", 2,4);
        TradeTweaks.setInputCount("btw:buy_ghast_tears", 1,2);
        TradeTweaks.setInputCount("btw:buy_magma_cream", 1,2);
        TradeTweaks.setInputCount("btw:buy_blaze_powder", 2,3);
//        TradeTweaks.setInputCount("btw:buy_ender_spectacles", 0,0);
        TradeTweaks.setInputCount("btw:buy_brimstone", 4,8);
        TradeTweaks.setInputCount("btw:buy_blood_wood_saplings", 1,1);
        TradeTweaks.setInputCount("btw:buy_nether_groth_spores", 1,1);
//        TradeTweaks.setInputCount("btw:convert_eyes_of_ender", 0,0);
//        TradeTweaks.setInputCount("btw:sell_power_scroll", 0,0);








        // priest
        TradeTweaks.setInputCount("btw:buy_hemp", 3,6);
        TradeTweaks.setInputCount("btw:buy_red_mushrooms", 4,6);
        TradeTweaks.setInputCount("btw:buy_cactus", 3,6);
//        TradeTweaks.setInputCount("btw:buy_paintings", 0,0);
//        TradeTweaks.setInputCount("btw:buy_flint_and_steel", 0,0);
        TradeTweaks.setOutputCount("btw:buy_enchanting_table", 3,3);
//        TradeTweaks.setInputCount("btw:enchant_tools", 0,0);
//        TradeTweaks.setInputCount("btw:enchant_iron_armor", 0,0);
//        TradeTweaks.setInputCount("btw:buy_vessel_of_the_dragon", 0,0);
        TradeTweaks.setInputCount("btw:buy_mob_heads", 1,3);
            TradeTweaks.setInputCount("btw:buy_mob_heads_variant_skeleton", 2,4);
            TradeTweaks.setInputCount("btw:buy_mob_heads_variant_zombie", 2,4);
            TradeTweaks.setInputCount("btw:buy_mob_heads_variant_creeper", 1,2);
        TradeTweaks.setInputCount("btw:buy_bone_block", 1,3);
        TradeTweaks.setInputCount("btw:buy_rotten_flesh_block", 2,4);
//        TradeTweaks.setInputCount("btw:convert_infused_skull_level_up", 0,0);
        TradeTweaks.setInputCount("btw:buy_candles", 2,4);
            TradeTweaks.setInputCount("btw:buy_candles_variant_black", 2,4);
            TradeTweaks.setInputCount("btw:buy_candles_variant_white", 2,4);
            TradeTweaks.setInputCount("btw:buy_candles_variant_red", 2,4);
            TradeTweaks.setInputCount("btw:buy_candles_variant_yellow", 2,4);
            TradeTweaks.setInputCount("btw:buy_candles_variant_blue", 2,4);
            TradeTweaks.setInputCount("btw:buy_candles_variant_green", 2,4);
        TradeTweaks.setInputCount("btw:buy_soul_urn", 1,2);
        TradeTweaks.setInputCount("btw:buy_canvas", 1,1);
//        TradeTweaks.setOutputCount("btw:buy_infernal_enchanter", 0,0);
//        TradeTweaks.setInputCount("btw:convert_infused_skull", 0,0);
//        TradeTweaks.setInputCount("btw:sell_fortune_scroll", 0,0);


        // blacksmith
        TradeTweaks.setInputCount("btw:buy_coal", 8,12);
        TradeTweaks.setInputCount("btw:buy_birch_logs", 8,12);
        TradeTweaks.setInputCount("btw:buy_iron_nuggets", 6,10);
        TradeTweaks.setOutputCount("btw:buy_oven", 2,2);
        TradeTweaks.setOutputCount("btw:buy_anvil_level_up", 3,6);
        TradeTweaks.setInputCount("btw:buy_hibachi", 1,1);
        TradeTweaks.setOutputCount("btw:buy_hibachi", 1,2);
        TradeTweaks.setInputCount("btw:buy_gold_nuggets", 2,6);
        TradeTweaks.setInputCount("btw:buy_charcoal", 2,4);
        TradeTweaks.setInputCount("btw:buy_iron_ingot", 1,1);
        TradeTweaks.setOutputCount("btw:buy_iron_ingot", 2,3);
//        TradeTweaks.setInputCount("btw:sell_iron_equipment", 0,0);
        TradeTweaks.setOutputCount("btw:buy_bellows", 3,6);
        TradeTweaks.setInputCount("btw:buy_nethercoal", 6,12);
        TradeTweaks.setInputCount("btw:buy_creeper_oysters", 8,12);
        TradeTweaks.setInputCount("btw:buy_diamonds", 1,2);
        TradeTweaks.setOutputCount("btw:buy_diamonds", 2,2);
        TradeTweaks.setInputCount("btw:buy_padding", 2,4);
        TradeTweaks.setInputCount("btw:buy_straps", 6,8);
//        TradeTweaks.setInputCount("btw:sell_chain_armor", 0,0);
        TradeTweaks.setOutputCount("btw:buy_crucible", 4,4);
//        TradeTweaks.setInputCount("btw:buy_soul_urns_blacksmith", 0,0);
        TradeTweaks.setInputCount("btw:buy_hafts", 1,2);
        TradeTweaks.setInputCount("btw:buy_mining_charges", 1,2);
//        TradeTweaks.setInputCount("btw:sell_diamond_equipment", 0,0);
//        TradeTweaks.setInputCount("btw:buy_steel_ingots", 2,4);
        TradeTweaks.setOutputCount("btw:buy_steel_ingots", 4,4);
        TradeTweaks.setInputCount("btw:buy_soul_flux", 1,4);
//        TradeTweaks.setInputCount("btw:sell_steel_tools", 0,0);
//        TradeTweaks.setInputCount("btw:sell_unbreaking_scroll", 0,0);



        // butcher
        TradeTweaks.setInputCount("btw:buy_arrows", 3,6);
        TradeTweaks.setOutputCount("btw:buy_shears_butcher", 2,2);
//        TradeTweaks.setInputCount("btw:buy_fishing_rod", 0,0);
        TradeTweaks.setOutputCount("btw:buy_bow", 1,2);
//        TradeTweaks.setInputCount("btw:sell_meat", 0,0);
        TradeTweaks.setOutputCount("btw:buy_cauldron", 3,4);
        TradeTweaks.setInputCount("btw:buy_flour", 4,6);
        TradeTweaks.setInputCount("btw:buy_dung", 1,3);
        TradeTweaks.setInputCount("btw:buy_spruce_bark", 16,24);
        TradeTweaks.setInputCount("btw:buy_leather", 2,4);
//        TradeTweaks.setInputCount("btw:sell_mid_tier_foods", 0,0);
        TradeTweaks.setOutputCount("btw:buy_saw", 4,6);
        TradeTweaks.setInputCount("btw:buy_potatoes", 4,6);
        TradeTweaks.setInputCount("btw:buy_carrots", 3,4);
        TradeTweaks.setInputCount("btw:buy_wolf_chops", 1,2);
//        TradeTweaks.setInputCount("btw:buy_liver", 0,0);
//        TradeTweaks.setInputCount("btw:buy_saddle", 0,0);
//        TradeTweaks.setInputCount("btw:sell_dinners", 0,0);
//        TradeTweaks.setInputCount("btw:sell_tanned_leather", 0,0);
//        TradeTweaks.setInputCount("btw:buy_breeding_harness", 0,0);
        TradeTweaks.setInputCount("btw:buy_mystery_meat", 1,1);
        TradeTweaks.setInputCount("btw:buy_screw", 1,2);
        TradeTweaks.setOutputCount("btw:buy_screw", 3,4);
//        TradeTweaks.setInputCount("btw:buy_composite_bow", 0,0);
//        TradeTweaks.setInputCount("btw:sell_hearty_stew", 0,0);
//        TradeTweaks.setInputCount("btw:sell_tanned_leather_armor", 0,0);
//        TradeTweaks.setInputCount("btw:buy_dirty_chopping_block", 0,0);
//        TradeTweaks.setInputCount("btw:convert_runed_skull", 0,0);
//        TradeTweaks.setInputCount("btw:buy_dynamite", 0,0);
//        TradeTweaks.setInputCount("btw:buy_battleaxe", 0,0);
//        TradeTweaks.setInputCount("btw:buy_companion_cube", 0,0);
        TradeTweaks.setInputCount("btw:buy_broadhead_arrows", 2,4);
//        TradeTweaks.setInputCount("btw:buy_lightning_rod_and_soap", 0,0);
//        TradeTweaks.setInputCount("btw:sell_sharpness_scroll", 0,0);

        finishRecipes("Trade Tweaks");

    }

    public static void miscInit(){
        ((ItemInvoker)BTWItems.plateBoots).invokeSetMaxDamage(729);
        ((ItemInvoker)BTWItems.plateLeggings).invokeSetMaxDamage(729);
        ((ItemInvoker)BTWItems.plateBreastplate).invokeSetMaxDamage(729);
        ((ItemInvoker)BTWItems.plateHelmet).invokeSetMaxDamage(729);

        ((DamageSourceExt)(BTWDamageSources.damageSourceGloom)).nightmareMode$setHungerDrain(0.3f);
        ((DamageSourceExt)(BTWDamageSources.damageSourceSaw)).nightmareMode$setHungerDrain(0.5f);
        ((DamageSourceExt)(BTWDamageSources.damageSourceSaw)).nightmareMode$setUnblockable(true);

        ((DamageSourceExt)(DamageSource.fall)).nightmareMode$setHungerDrain(0.1f);
        ((DamageSourceExt)(DamageSource.generic)).nightmareMode$setHungerDrain(0.2f);
        ((DamageSourceExt)(DamageSource.onFire)).nightmareMode$setHungerDrain(0.02f);



        WorldGenReed.addBiomeToGenerator(BiomeGenBase.river);


        boolean isServer = MinecraftServer.getIsServer();
        if (!isServer) {
            BTWItems.emeraldPile.setItemRightClickCooldown( BTWItems.emeraldPile.getItemRightClickCooldown() / 6);
            BTWItems.diamondPile.setItemRightClickCooldown( BTWItems.emeraldPile.getItemRightClickCooldown() / 6);
            BTWItems.soulSandPile.setItemRightClickCooldown( BTWItems.soulSandPile.getItemRightClickCooldown() / 6);
            NMItems.witchLocator.setItemRightClickCooldown( NMItems.witchLocator.getItemRightClickCooldown() / 6);
            NMItems.templeLocator.setItemRightClickCooldown( NMItems.templeLocator.getItemRightClickCooldown() / 6);
        }
        finishRecipes("Miscellaneous");
        NMConfUtils.initConfigFile();
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
        EntityVillager.removeLevelUpTrade(0,2);
        EntityVillager.removeCustomTrade(0, TradeProvider.getBuilder().name("btw:sell_looting_scroll").profession(0).level(5).arcaneScroll().scrollEnchant(Enchantment.looting).secondaryEmeraldCost(48, 64).mandatory().build());
        EntityVillager.removeCustomTrade(0, TradeProvider.getBuilder().name("btw:buy_brown_mushrooms").profession(0).level(2).buy().item(BTWItems.brownMushroom.itemID).itemCount(10, 16).build());

        sell("nmFarmer0", 0, 1,Block.grass.blockID, 0,2, 4, 0.3f, false, 0,0);
        convert("nmFarmer0", 0, 1,
                TradeItem.fromIDAndMetadata(Block.tallGrass.blockID, 1, 2, 4),
                TradeItem.fromID(Item.emerald.itemID, 1, 2),
                TradeItem.fromID(BTWItems.hempSeeds.itemID, 2, 6),
                0.3f, false, false);
        buy("nmFarmer0", 0, 2, BTWBlocks.millstone.blockID, 0, 2, 2, 0, true, 2, 2);
        buy("nmFarmer0", 0, 2, Item.shears.itemID, 0, 1, 1, 0.4f);
        buy("nmFarmer0", 0, 3, BTWItems.redMushroom.itemID, 0, 2, 5, 1.2f);
        buy("nmFarmer0", 0, 3, Item.bucketWater.itemID, 0, 1, 1);
        buy("nmFarmer0", 0, 4, BTWItems.chowder.itemID, 0, 1, 2);
        convert("nmFarmer0", 0, 5,
                TradeItem.fromID(Item.paper.itemID),
                TradeItem.fromID(NMItems.bloodOrb.itemID, 8, 16),
                TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("efficiency")),
                false, true);

        finishRecipes("Farmer Trades");

    }

    private static void addLibrarianTrades(){
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().name("nmLibrarian0").profession(1).level(1).buy().item(Item.paper.itemID).itemCount(24, 32).build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().name("nmLibrarian0").profession(1).level(2).variants().addTradeVariant(TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(2).convert().input(TradeItem.fromID(BTWItems.redstoneEye.itemID, 2)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.detectorBlock.blockID)).build()).addTradeVariant(TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(2).convert().input(TradeItem.fromID(BTWItems.redstoneEye.itemID, 4)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.buddyBlock.blockID)).build()).addTradeVariant(TradeProvider.getBuilder().name("nmlibrarian0").profession(1).level(2).convert().input(TradeItem.fromID(Block.cobblestoneMossy.blockID, 6)).conversionCost(4, 6).output(TradeItem.fromID(BTWBlocks.blockDispenser.blockID)).build()).finishVariants().mandatory().build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().name("nmLibrarian0").profession(1).level(5).convert().input(TradeItem.fromID(Item.enderPearl.itemID)).conversionCost(6, 8).output(TradeItem.fromID(Item.eyeOfEnder.itemID)).mandatory().build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().name("btw:sell_power_scroll").profession(1).level(5).arcaneScroll().scrollEnchant(Enchantment.power).secondaryEmeraldCost(48, 64).mandatory().build());
        EntityVillager.removeCustomTrade(1, TradeProvider.getBuilder().name("btw:buy_bat_wings").profession(1).level(3).buy().item(BTWItems.batWing.itemID).itemCount(8, 12).build());

        buy("nmlibrarian0", 1, 1, NMItems.ironKnittingNeedles.itemID, 0, 1, 1, 2, 3);
        buy("nmlibrarian0", 1, 2, Block.bookShelf.blockID, 0, 1, 2);
        buy("nmlibrarian0", 1, 2, Item.book.itemID, 0, 3, 6);
        buy("nmlibrarian0", 1, 2, Item.redstoneRepeater.itemID, 0, 1, 2);
        buy("nmlibrarian0", 1, 3, BTWItems.hellfireDust.itemID, 0, 16, 24);
        convert("nmlibrarian0", 1, 3, TradeItem.fromIDAndMetadata(BTWItems.wool.itemID, 15, 2, 4),
                TradeItem.fromID(Item.emerald.itemID, 1, 2),
                TradeItem.fromID(NMItems.bandage.itemID, 1, 2));
        buy("nmlibrarian0", 1, 4, BTWBlocks.blockDispenser.blockID, 0, 1, 2);
        buy("nmlibrarian0", 1, 4, BTWBlocks.buddyBlock.blockID, 0, 1, 2);
        buy("nmlibrarian0", 1, 4, BTWBlocks.detectorBlock.blockID, 0, 1, 3);

        convert("nmlibrarian0", 1, 4, TradeItem.fromID(Item.paper.itemID),
                TradeItem.fromID(NMItems.bloodOrb.itemID, 12, 24), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("blast")), 1.2f);
        sell("nmlibrarian0", 1, 4, BTWItems.soulFlux.itemID, 0, 2, 4, 1.2f);
        convert("nmlibrarian0", 1, 5, TradeItem.fromID(Item.paper.itemID),
                TradeItem.fromID(NMItems.bloodOrb.itemID, 24, 32),
                TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("power")));
        convert("nmlibrarian0", 1, 5, TradeItem.fromID(BTWItems.corpseEye.itemID),
                TradeItem.fromID(NMItems.bloodOrb.itemID, 4, 10),
                TradeItem.fromID(Item.eyeOfEnder.itemID), 1.0f, false, true);

        finishRecipes("Librarian Trades");
    }



    private static void addPriestTrades(){
        EntityVillager.removeCustomTrade(2, TradeProvider.getBuilder().name("btw:sell_fortune_scroll").profession(2).level(5).arcaneScroll().scrollEnchant(Enchantment.fortune).secondaryEmeraldCost(48, 64).mandatory().build());
        EntityVillager.removeLevelUpTrade(2,2);


        buy("nmPriestWart", 2, 2, Item.netherStalkSeeds.itemID, 0, 4, 8);
        buy("nmPriestNitre", 2, 3, BTWItems.nitre.itemID, 0, 8, 16);
        sell("nmPriestEnchant", 2, 3, Block.enchantmentTable.blockID, 0, 1, 1, 0.35f, false, 10, 6);
        convert("nmPriestPunchScroll", 2, 3, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.bloodOrb.itemID, 8, 12), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("punch")), 0.1f);
        convert("nmPriestPaper", 2, 3, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.bloodOrb.itemID, 32, 64), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("fortune")), 0.1f);
        convert("nmPriestPotion", 2, 3, TradeItem.fromID(Item.potion.itemID), TradeItem.fromID(Item.emerald.itemID, 1, 3), TradeItem.fromIDAndMetadata(Item.potion.itemID, 16453, 2));
        convert("nmPriestGoldenApple", 2, 4, TradeItem.fromID(Item.appleGold.itemID), TradeItem.fromID(NMItems.bloodOrb.itemID, 10, 18), TradeItem.fromIDAndMetadata(Item.appleGold.itemID, 1), false, true);
        convert("nmPriestPaperAgain", 2, 5, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.bloodOrb.itemID, 16, 24), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("prot")));
        convert("nmPriestRifle", 2, 5, TradeItem.fromID(NMItems.rifle.itemID), TradeItem.fromID(NMItems.rpg.itemID), TradeItem.fromID(Block.dragonEgg.blockID), false, true);
        buy("nmPriestOcular", 2, 2, BTWItems.ocularOfEnder.itemID, 0, 1, 1, 1.0f, true, 2,2);

        finishRecipes("Priest Trades");

    }


    private static void addBlacksmithTrades(){
        EntityVillager.removeCustomTrade(3, TradeProvider.getBuilder().name("btw:sell_unbreaking_scroll").profession(3).level(5).arcaneScroll().scrollEnchant(Enchantment.unbreaking).secondaryEmeraldCost(48, 64).mandatory().build());

        buy("nmBlacksmith0", 3, 1, Item.pickaxeStone.itemID, 0);
        sell("nmBlacksmith0", 3, 2, NMItems.bandage.itemID, 0, 2, 2);
        buy("nmBlacksmith0", 3, 2, Item.redstone.itemID, 0, 32, 64, 0.8f);
        buy("nmBlacksmith0", 3, 2, Item.flintAndSteel.itemID, 0);
        convert("nmBlacksmith0", 3, 3, TradeItem.fromIDAndMetadata(BTWBlocks.aestheticOpaque.blockID, 7, 4, 8), TradeItem.EMPTY, TradeItem.fromID(Item.emerald.itemID, 1));
        buy("nmBlacksmith0", 3, 3, BTWItems.diamondArmorPlate.itemID, 0);
        convert("nmBlacksmith0", 3, 3, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.bloodOrb.itemID, 24, 32), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("looting")), 0.9f);
        sell("nmBlacksmith0", 3, 3, Item.appleGold.itemID, 0, 8, 16);
        convert("nmBlacksmith0", 3, 3, TradeItem.fromID(Item.potion.itemID), TradeItem.fromID(Item.emerald.itemID, 1, 3), TradeItem.fromIDAndMetadata(Item.potion.itemID, 8201));
        convert("nmBlacksmith0", 3, 4, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.bloodOrb.itemID, 12, 18), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("unbreaking")));
        finishRecipes("Blacksmith Trades");
    }


    private static void addButcherTrades(){
        EntityVillager.removeCustomTrade(4, TradeProvider.getBuilder().name("btw:sell_sharpness_scroll").profession(4).level(5).arcaneScroll().scrollEnchant(Enchantment.sharpness).secondaryEmeraldCost(48, 64).mandatory().build());
        EntityVillager.removeCustomTrade(4, TradeProvider.getBuilder().name("btw:buy_leash").profession(4).level(4).buy().item(Item.leash.itemID).buySellSingle().weight(0.25f).build());

        buy("nmButcher0", 4, 1, Item.leash.itemID, 0, 2, 6);
        buy("nmButcher0", 4, 2, Item.swordIron.itemID, 0, 1, 1, 0.3f);
        convert("nmButcher0", 4, 3, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.bloodOrb.itemID, 6, 12), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("thorns")));
        convert("nmButcher0", 4, 4, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.bloodOrb.itemID, 24, 32), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("feather")), 2.0f);

        finishRecipes("Butcher Trades");

    }


    private static void addNightmareVillagerTrades(){
        convert("nmMerchant0", 5, 1, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.bloodOrb.itemID,16,24), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("power")));
        buy("nmMerchant1", 5, 1, Item.rottenFlesh.itemID, 0, 8, 16, 0.3f);
        buy("nmMerchant2", 5, 1, Item.dyePowder.itemID, Color.BLACK.colorID, 12, 18, 0.3f);
        buy("nmMerchant3", 5, 1, NMItems.magicFeather.itemID, 0, 1, 2, 0.3f);
        buy("nmMerchant4", 5, 1, NMItems.bloodMilk.itemID, 0, 1, 1, 0.2f);
        buy("nmMerchant5", 5, 1, Item.enderPearl.itemID, 0);
        buy("nmMerchant6", 5, 1, NMItems.fireRod.itemID, 0, 1, 3, 0.5f);
        convert("nmMerchant7", 5, 2, TradeItem.fromIDAndMetadata(Item.potion.itemID, 8229, 1,2), TradeItem.EMPTY, TradeItem.fromID(Item.emerald.itemID));
        convert("nmMerchant8", 5, 2, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.darksunFragment.itemID,4,8), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("infinity")), 0.7f);
        buy("nmMerchant9", 5, 2, NMItems.decayedFlesh.itemID, 0, 4, 6);
        buy("nmMerchant10", 5, 2, NMItems.silverLump.itemID, 0, 2, 3);
        sell("nmMerchant11", 5, 2, NMItems.dungApple.itemID, 0, 1, 1, 0.3f);
        buy("nmMerchant12", 5, 2, NMItems.creeperTear.itemID, 0, 1, 1);
        buy("nmMerchant13", 5, 2, NMItems.shadowRod.itemID, 0, 1, 2);
        sell("nmMerchant14", 5, 2, BTWItems.soulFlux.itemID, 0, 4, 8);
        convert("nmMerchant20", 5, 2, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.darksunFragment.itemID,1,2), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("blast")), 0.55f);
        sell("nmMerchant15", 5, 3, Item.blazeRod.itemID, 0, 2, 4);
        sell("nmMerchant16", 5, 3, Item.nameTag.itemID, 0, 2, 4);
        buy("nmMerchant17", 5, 3, NMItems.spiderFangs.itemID, 0, 2, 4);
        buy("nmMerchant18", 5, 3, NMItems.ghastTentacle.itemID, 0, 1, 2);
        buy("nmMerchant19", 5, 3, NMItems.sulfur.itemID, 0, 3, 6);
        buy("nmMerchant20", 5, 3, NMItems.charredFlesh.itemID, 0, 1, 2);
        sell("nmMerchant20", 5, 3, Item.magmaCream.itemID, 0, 6, 8);
        buy("nmMerchant20", 5, 3, NMItems.speedCoil.itemID, 0, 1, 1, 0.5f);
        convert("nmMerchant20", 5, 3, TradeItem.fromID(Item.paper.itemID), TradeItem.fromID(NMItems.darksunFragment.itemID,4,8), TradeItem.fromIDAndMetadata(BTWItems.arcaneScroll.itemID, NMUtils.getScrollMetadata("sharp")), 0.55f);
        sell("nmMerchant21", 5, 4, Item.ghastTear.itemID, 0, 4, 6);
        buy("nmMerchant22", 5, 4, NMItems.waterRod.itemID, 0, 1, 2);
        buy("nmMerchant23", 5, 4, NMItems.voidSack.itemID, 0, 1, 3);
        buy("nmMerchant24", 5, 4, NMItems.creeperChop.itemID, 0, 1, 1);
        buy("nmMerchant25", 5, 3, NMItems.witheredBone.itemID, 0, 1, 2);
        buy("nmMerchant26", 5, 4, NMItems.elementalRod.itemID, 0, 1, 1, 0.6f);
        buy("nmMerchant27", 5, 4, NMItems.voidMembrane.itemID, 0, 1, 1);
        buy("nmMerchant28", 5, 4, NMItems.darksunFragment.itemID, 0, 1, 1);
        buy("nmMerchant29", 5, 4, Item.eyeOfEnder.itemID, 0);
        buy("nmMerchant30", 5, 4, NMItems.creeperTear.itemID, 0, 1, 1, 0.2f);

        // Level 5 Trades
        TradeProvider.getBuilder()
                .name("nmMerchant31")
                .profession(5)
                .level(5)
                .variants()
                .addTradeVariant(
                        TradeProvider.getBuilder()
                                .name("nmMerchant32")
                                .profession(5)
                                .level(5)
                                .sell()
                                .item(Item.enderPearl.itemID)
                                .itemCount(32, 64)
                                .build()
                )
                .addTradeVariant(
                        TradeProvider.getBuilder()
                                .name("nmMerchant33")
                                .profession(5)
                                .level(5)
                                .sell()
                                .item(NMItems.rifle.itemID)
                                .buySellSingle()
                                .build()
                )
                .addTradeVariant(
                        TradeProvider.getBuilder()
                                .name("nmMerchant34")
                                .profession(5)
                                .level(5)
                                .sell()
                                .item(NMItems.rpg.itemID)
                                .buySellSingle()
                                .build()
                )
                .addTradeVariant(
                        TradeProvider.getBuilder()
                                .name("nmMerchant35")
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

        TradeProvider.getBuilder().name("nmMerchant36").profession(5).level(5).sell().item(NMPostItems.timeBottle.itemID).buySellSingle().mandatory().addToTradeList();

        TradeProvider.getBuilder().name("nmMerchant37").profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant38").profession(5).level(5).sell().item(Block.waterStill.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant39").profession(5).level(5).sell().item(Block.lavaStill.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant40").profession(5).level(5).sell().item(Block.fire.blockID).itemCount(1, 64).build())
                .finishVariants().mandatory().addToTradeList();

        TradeProvider.getBuilder().name("nmMerchant41").profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant42").profession(5).level(5).sell().item(Block.bedrock.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant43").profession(5).level(5).sell().item(Block.portal.blockID).itemCount(6, 6).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant44").profession(5).level(5).sell().item(Block.endPortal.blockID).itemCount(9, 9).build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant45").profession(5).level(5).sell().item(Block.endPortalFrame.blockID).itemCount(12, 12).build())
                .finishVariants().mandatory().addToTradeList();

        TradeProvider.getBuilder().name("nmMerchant46").profession(5).level(5).variants()
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant47").profession(5).level(5).sell().item(Block.mobSpawner.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant48").profession(5).level(5).sell().item(Block.dragonEgg.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant49").profession(5).level(5).sell().item(BTWBlocks.workbench.blockID).buySellSingle().build())
                .addTradeVariant(TradeProvider.getBuilder().name("nmMerchant50").profession(5).level(5).sell().item(BTWBlocks.axlePowerSource.blockID).buySellSingle().build())
                .finishVariants().mandatory().addToTradeList();

        // Level up Trades
        TradeProvider.getBuilder().name("nmMerchant103").profession(5).level(1).buy().item(Block.dragonEgg.blockID).itemCount(1,1).addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmMerchant104").profession(5).level(2).buy().item(NMItems.voidMembrane.itemID).itemCount(3,3).addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmMerchant105").profession(5).level(3).buy().item(NMItems.darksunFragment.itemID).itemCount(16,16).addAsLevelUpTrade();
        TradeProvider.getBuilder().name("nmMerchant106").profession(5).level(4).buy().item(NMItems.starOfTheBloodGod.itemID).itemCount(1,1).addAsLevelUpTrade();


        TradeProvider.getBuilder().name("nmMerchant107").profession(5).level(3).sell().item(NMBlocks.bloodBones.blockID).buySellSingle().weight(0.02f).addToTradeList();
        TradeProvider.getBuilder().name("nmMerchant108").profession(5).level(4).sell().item(NMBlocks.bloodBones.blockID).buySellSingle().weight(1.2f).addToTradeList();
        finishRecipes("Nightmare Merchant Trades");

    }


    private static void addCrucibleRecipes(){
        // refined diamond
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot), new ItemStack[]{new ItemStack(BTWItems.diamondIngot), new ItemStack(Item.netherQuartz, 4)});

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
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotGold, 2), new ItemStack[]{new ItemStack(Item.horseArmorGold, 1, Short.MAX_VALUE)});
        // done adding

        // make other horse armors smeltable regardless of durability
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotIron, 4), (TagOrStack[])new ItemStack[]{new ItemStack(Item.horseArmorIron, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.diamondIngot, 6), (TagOrStack[])new ItemStack[]{new ItemStack(Item.horseArmorDiamond, 1, Short.MAX_VALUE)});

        // done horsing

        // add gold recipes for golden apples and carrots
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 3), new ItemStack[]{new ItemStack(Item.appleGold)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.ingotGold, 3), new ItemStack[]{new ItemStack(Item.appleGold, 1, 1)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Item.goldNugget, 2), new ItemStack[]{new ItemStack(Item.goldenCarrot)});
        // done with apples

        // chest
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.ironNugget, 6), new ItemStack[]{new ItemStack(BTWBlocks.chest)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget), new ItemStack[]{new ItemStack(BTWBlocks.chest)});
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
//        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 4), new ItemStack[]{new ItemStack(NMItems.ironFishingPole, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 7), new ItemStack[]{new ItemStack(NMBlocks.ironLadder, 4)});

        // obsidian post-wither
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Block.obsidian, 1, 0), new ItemStack[]{new ItemStack(BTWItems.steelNugget), new ItemStack(Item.clay),new ItemStack(Block.obsidian, 1, 1)});
        // done with obsidian

        // remove and re-add steel ingot recipe, so that the obsidian takes priority over it
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.soulforgedSteelIngot, 1), new ItemStack[]{new ItemStack(BTWItems.steelNugget, 9)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.soulforgedSteelIngot, 1), new ItemStack[]{new ItemStack(BTWItems.steelNugget, 9)});
        // done with steel


        // refined diamond
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 6), new ItemStack[]{new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 8), new ItemStack[]{new ItemStack(Item.plateDiamond, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 7), new ItemStack[]{new ItemStack(Item.legsDiamond, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondIngot, 4), new ItemStack[]{new ItemStack(Item.bootsDiamond, 1, Short.MAX_VALUE)});

        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 5), new ItemStack[]{new ItemStack(Item.helmetDiamond, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 8), new ItemStack[]{new ItemStack(Item.plateDiamond, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 7), new ItemStack[]{new ItemStack(Item.legsDiamond, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.refinedDiamondIngot, 4), new ItemStack[]{new ItemStack(Item.bootsDiamond, 1, Short.MAX_VALUE)});

        // done with refined diamond
        // blood chest and steel locker
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(NMItems.bloodOrb, 4), new ItemStack[]{new ItemStack(NMBlocks.bloodChest)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack[]{new ItemStack(NMItems.bloodOrb, 4), new ItemStack(BTWItems.steelNugget, 32)}, new ItemStack[]{new ItemStack(NMBlocks.steelLocker)});


        // chainmail
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.ironNugget, 2), new ItemStack[]{new ItemStack(BTWItems.mail)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 1), new ItemStack[]{new ItemStack(BTWItems.mail, 3)});

        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.ironNugget, 13), new ItemStack[]{new ItemStack(Item.helmetChain, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.ironNugget, 21), new ItemStack[]{new ItemStack(Item.plateChain, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.ironNugget, 19), new ItemStack[]{new ItemStack(Item.legsChain, 1, Short.MAX_VALUE)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.ironNugget, 11), new ItemStack[]{new ItemStack(Item.bootsChain, 1, Short.MAX_VALUE)});

        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 4), new ItemStack[]{new ItemStack(Item.helmetChain, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 6), new ItemStack[]{new ItemStack(Item.plateChain, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 5), new ItemStack[]{new ItemStack(Item.legsChain, 1, Short.MAX_VALUE)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 3), new ItemStack[]{new ItemStack(Item.bootsChain, 1, Short.MAX_VALUE)});


        // diamond pile
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.diamondPile, 8), new ItemStack[]{new ItemStack(Item.diamond, 1), new ItemStack(BTWItems.soulSandPile, 8)});
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.emeraldPile, 8), new ItemStack[]{new ItemStack(Item.emerald, 1), new ItemStack(BTWItems.soulSandPile, 8)});

        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.emeraldPile, 4), new ItemStack[]{new ItemStack(Item.emerald, 1), new ItemStack(BTWItems.soulSandPile, 8)});
        // diamond pile

        // glass
        CrucibleStokedCraftingManager.getInstance().removeRecipe(new ItemStack(Block.glass, 8), (TagOrStack[])new ItemStack[]{new ItemStack(Item.netherQuartz), new ItemStack(Block.sand, 16)});
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(Block.glass, 16), (TagOrStack[])new ItemStack[]{new ItemStack(Item.netherQuartz), new ItemStack(Block.sand, 16)});
        // glass
        // fns
        RecipeManager.addStokedCrucibleRecipe(new ItemStack(BTWItems.ironNugget, 2), (TagOrStack[])new ItemStack[]{new ItemStack(Item.flintAndSteel, 1,Short.MAX_VALUE)});
        // fns

        finishRecipes("Crucible Recipes");

    }
    private static void addCauldronRecipes(){
        RecipeManager.addCauldronRecipe(new ItemStack(Item.potato, 1), new ItemStack[]{new ItemStack(BTWItems.straw, 8)});
        RecipeManager.addCauldronRecipe(new ItemStack(Item.clay, 8), new ItemStack[]{new ItemStack(BTWItems.netherSludge, 8)});
        RecipeManager.addCauldronRecipe(new ItemStack(NMItems.friedCalamari), new ItemStack[]{new ItemStack(NMItems.calamariRoast), new ItemStack(Item.bowlEmpty)});
        RecipeManager.addCauldronRecipe(new ItemStack(Item.blazeRod), new ItemStack[]{new ItemStack(Item.blazePowder, 2), new ItemStack(Item.stick)});

        // blood sapling and groth nerf: instead of costing 8 urns, they only cost 1
        CauldronCraftingManager.getInstance().removeRecipe((new ItemStack(BTWBlocks.aestheticVegetation, 1, 2)), new ItemStack[]{new ItemStack(BTWBlocks.oakSapling), new ItemStack(BTWBlocks.spruceSapling), new ItemStack(BTWBlocks.birchSapling), new ItemStack(BTWBlocks.jungleSapling), new ItemStack(BTWItems.soulUrn, 8), new ItemStack(Item.netherStalkSeeds)});
        RecipeManager.addCauldronRecipe(new ItemStack(BTWBlocks.aestheticVegetation, 1, 2), new ItemStack[]{new ItemStack(BTWBlocks.oakSapling), new ItemStack(BTWBlocks.spruceSapling), new ItemStack(BTWBlocks.birchSapling), new ItemStack(BTWBlocks.jungleSapling), new ItemStack(BTWItems.soulUrn), new ItemStack(Item.netherStalkSeeds)});

        CauldronCraftingManager.getInstance().removeRecipe(new ItemStack[]{new ItemStack(BTWBlocks.looseDirt), new ItemStack(BTWItems.netherGrothSpores)}, new ItemStack[]{new ItemStack(Block.mycelium), new ItemStack(BTWItems.brownMushroom), new ItemStack(BTWItems.redMushroom), new ItemStack(BTWItems.soulUrn, 8), new ItemStack(BTWItems.dung), new ItemStack(Item.netherStalkSeeds)});
        RecipeManager.addCauldronRecipe(new ItemStack(BTWItems.netherGrothSpores), new ItemStack[]{new ItemStack(Block.mycelium), new ItemStack(BTWItems.brownMushroom), new ItemStack(BTWItems.redMushroom), new ItemStack(BTWItems.soulUrn), new ItemStack(BTWItems.dung), new ItemStack(Item.netherStalkSeeds)});



        RecipeManager.addStokedCauldronRecipe(new ItemStack(BTWItems.netherSludge, 4), new ItemStack[]{new ItemStack(BTWItems.netherBrick, 8)});

        RecipeManager.addStokedCauldronRecipe(new ItemStack(NMItems.templeLocator, 2), new ItemStack[]{new ItemStack(BTWItems.sandPile, 64), new ItemStack(NMItems.obsidianShard, 4), new ItemStack(Item.ingotGold)});

        CauldronCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.heartyStew, 5), new TagOrStack[]{TagInstance.of(BTWTags.cookedPotatoes), new ItemStack(BTWItems.cookedCarrot), new ItemStack(BTWItems.brownMushroom, 3), new ItemStack(BTWItems.flour), TagInstance.of(BTWTags.heartyMeats), new ItemStack(Item.bowlEmpty, 5)});
        RecipeManager.addCauldronRecipe(new ItemStack(BTWItems.heartyStew, 5), new TagOrStack[]{new ItemStack(BTWItems.boiledPotato), new ItemStack(BTWItems.cookedCarrot), new ItemStack(BTWItems.brownMushroom, 3), new ItemStack(BTWItems.flour), TagInstance.of(BTWTags.cookedMeats), new ItemStack(Item.bowlEmpty, 5)});

//        RecipeManager.addCauldronRecipe(new ItemStack(NMItems.refinedElement), new TagOrStack[]{new ItemStack(Item.blazePowder, 2), new ItemStack(BTWItems.soulUrn), new ItemStack(Item.redstone, 3), new ItemStack(BTWItems.steelNugget, 4)});
//        RecipeManager.addCauldronRecipe(new ItemStack(NMItems.refinedElement), new TagOrStack[]{new ItemStack(Item.blazePowder, 2), new ItemStack(BTWItems.soulUrn), new ItemStack(Item.redstone, 3), new ItemStack(NMItems.steelBunch)});


        finishRecipes("Cauldron Recipes");

    }

    private static void addOvenRecipes(){
        FurnaceRecipes.smelting().addSmelting(NMItems.calamari.itemID, new ItemStack(NMItems.calamariRoast), 0.0f);
        finishRecipes("Oven Recipes");

    }
    private static void addSoulforgeRecipes(){
        // packed blocks
        RecipeManager.addSoulforgeRecipe(new ItemStack(BTWBlocks.aestheticEarth.blockID, 4, 6),new Object[]{"####", "####", "####", "####", Character.valueOf('#'), BTWBlocks.looseDirt});
        RecipeManager.addSoulforgeRecipe(new ItemStack(Block.sandStone, 4),new Object[]{"####", "####", "####", "####", Character.valueOf('#'), Block.sand});

        finishRecipes("Soulforge Recipes");

    }
    private static void addCampfireRecipes(){
        RecipeManager.addCampfireRecipe(NMItems.calamari.itemID, new ItemStack(NMItems.calamariRoast));
        finishRecipes("Campfire Recipes");

    }
    private static void addMillstoneRecipes(){
//        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.carrotSeeds), new ItemStack(BTWItems.hempSeeds));
//        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.wheatSeeds), new ItemStack(BTWItems.carrotSeeds));
//        RecipeManager.addMillStoneRecipe(new ItemStack(NMItems.witchLocator,4), new ItemStack(BTWItems.witchWart));

        // improve netherrack grinding rates
//        MillStoneCraftingManager.getInstance().removeRecipe(new ItemStack(BTWItems.groundNetherrack), new ItemStack(Block.netherrack));
//        RecipeManager.addMillStoneRecipe(new ItemStack(BTWItems.groundNetherrack, 8), new ItemStack(Block.netherrack));
        finishRecipes("Millstone Recipes");

    }

    private static void addCraftingRecipes(){
//        RecipeManager.addRecipe(new ItemStack(BTWBlocks.planter, 1), new Object[]{"# #", "# #", "###", Character.valueOf('#'), Item.brick});
//        RecipeManager.addRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XXX", "###", Character.valueOf('#'), new ItemStack(BTWItems.woodSidingStubID, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.book,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(NMBlocks.steelLocker), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.steelBunch), Character.valueOf('X'), new ItemStack(NMBlocks.bloodChest)});
        // add gapple and carrot recipes
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.appleGold, 1, 0), new Object[]{"###", "#X#", "###", '#', Item.ingotGold, 'X', Item.appleRed});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.appleGold, 1, 1), new Object[]{"###", "#X#", "###", '#', Block.blockGold, 'X', Item.appleRed});
        RecipeManager.addRecipe(new ItemStack(Item.appleGold,1,1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(Item.ingotGold, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(Item.appleGold), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(Item.goldNugget, 1, Short.MAX_VALUE), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.addRecipe(new ItemStack(NMItems.dungApple), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(BTWItems.dung, 1), Character.valueOf('X'), new ItemStack(Item.appleRed,1,Short.MAX_VALUE)});
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.goldenCarrot, 1, 0), new Object[]{"###", "#X#", "###", Character.valueOf('#'), Item.goldNugget, Character.valueOf('X'), BTWItems.carrot});
        RecipeManager.addRecipe(new ItemStack(Item.goldenCarrot, 1, 0), new Object[]{"###", "#X#", "###", Character.valueOf('#'), Item.ingotGold, Character.valueOf('X'), BTWItems.carrot});
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

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.fishingRod), new Object[]{TagInstance.of(BTWTags.lowQualityToolHandles), BTWTags.fineStrings, BTWTags.fineStrings, BTWTags.fishingHooks});

//        RecipeManager.addRecipe(new ItemStack(NMItems.ironFishingPole,1), new Object[]{"  #", " #X", "Y #", Character.valueOf('#'), BTWItems.ironNugget, Character.valueOf('X'), BTWItems.rope, Character.valueOf('Y'), Item.ingotIron});
        // fishing recipes added

        // add misc recipes
        RecipeManager.addRecipe(new ItemStack(NMItems.eclipseBow,1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), new ItemStack(NMItems.darksunFragment, 1), Character.valueOf('X'), new ItemStack(BTWItems.compositeBow)});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.wickerPane, 8), new Object[]{new ItemStack(BTWBlocks.hamper)});
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

        // tasty sandwich
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2), new Object[]{new ItemStack(Item.bread), BTWTags.cookedMeats});

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2), new Object[]{new ItemStack(Item.bread), new ItemStack(BTWItems.hamAndEggs)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 3), new Object[]{new ItemStack(Item.bread), new ItemStack(Item.bread), new ItemStack(BTWItems.cookedKebab)});

        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.tastySandwich, 2), new Object[]{new ItemStack(Item.bread), NMTags.sandwichMeats});
        // tasty sandwich end

        // remove sinew recipes, add custom ones
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, 600), new Object[]{new ItemStack(Item.beefCooked), new ItemStack(Item.beefCooked), new ItemStack(BTWItems.sharpStone)});
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, 600), new Object[]{new ItemStack(BTWItems.cookedWolfChop), new ItemStack(BTWItems.cookedWolfChop), new ItemStack(BTWItems.sharpStone)});

        NMUtils.logTodo(); // add sinew recipes
//        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, 600), new Object[]{new ItemStack(Item.beefCooked), new ItemStack(BTWItems.sharpStone)});
//        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingBeef, 1, 600), new Object[]{new ItemStack(Item.porkCooked), new ItemStack(BTWItems.sharpStone)});
//        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, 600), new Object[]{new ItemStack(BTWItems.cookedMutton), new ItemStack(BTWItems.sharpStone)});
//        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.sinewExtractingWolf, 1, 600), new Object[]{new ItemStack(BTWItems.cookedWolfChop), new ItemStack(BTWItems.sharpStone)});
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

        NMUtils.logTodo(); // make books expensive
//        RecipeManager.addRecipe(new ItemStack(Item.book), new Object[]{"###", "XXX", Character.valueOf('#'), NMTags.bookLeather, Character.valueOf('X'), new ItemStack(Item.paper, 1, Short.MAX_VALUE)});

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
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.gear, 4), new Object[]{" X ", "X#X", " X ", Character.valueOf('#'), BTWTags.logs, Character.valueOf('X'), Item.stick});
        RecipeManager.addRecipe(new ItemStack(BTWItems.gear, 2), new Object[]{" X ", "X#X", " X ", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.stick});
        // stone hoe
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.hoeStone), new Object[]{"X#", "S#", " #", '#', Item.stick, 'X', BTWTags.looseRocks, 'S', BTWTags.strings});

        // sail recipe
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.windMillBlade), new Object[]{"###", "###", "XXX", Character.valueOf('#'), BTWItems.fabric, Character.valueOf('X'), BTWTags.woodenMouldings});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.windMillBlade), new Object[]{"###", "###", "XXX", Character.valueOf('#'), BTWItems.fabric, Character.valueOf('X'), BTWTags.planks});

        RecipeManager.addRecipe(new ItemStack(BTWItems.windMillBlade), new Object[]{"###", "XXX", Character.valueOf('#'), BTWItems.fabric, Character.valueOf('X'), BTWTags.woodenMouldings});
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
        RecipeManager.addShapelessRecipe(new ItemStack(Block.obsidian, 1, 1), new Object[]{new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard), new ItemStack(NMItems.obsidianShard)});


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
                Character.valueOf('O'), Block.obsidian,
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
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), Item.ingotIron});
        RecipeManager.addRecipe(new ItemStack(BTWBlocks.chest), new Object[]{"###", "#I#", "###", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('I'), BTWItems.ironNugget});
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
        NMUtils.logTodo(); // recipe for bm bottle
        RecipeManager.addRecipe(new ItemStack(NMPostItems.bloodMoonBottle), new Object[]{
                "HSH",
                "BEB",
                "HBH",
                Character.valueOf('B'), NMItems.bloodOrb,
                Character.valueOf('S'), BTWItems.soulFlux,
                Character.valueOf('E'), Item.expBottle,
                Character.valueOf('H'), BTWItems.hellfireDust
        });


        // new BD recipe:
//        RecipeManager.addRecipe(new ItemStack(BTWBlocks.blockDispenser), new Object[]{
//                "MVM",
//                "SLS",
//                "MRM",
//                Character.valueOf('M'), BTWTags.mossyCobblestones,
//                Character.valueOf('L'), BTWItems.redstoneLatch,
//                Character.valueOf('R'), Block.blockRedstone,
//                Character.valueOf('D'), Block.vine,
//                Character.valueOf('S'), BTWItems.soulUrn
//        });



        // dynamite
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.dynamite, 2), new Object[]{"PF", "PN", "PS", Character.valueOf('P'), Item.paper, Character.valueOf('F'), BTWItems.fuse, Character.valueOf('N'), BTWItems.blastingOil, Character.valueOf('S'), BTWItems.sawDust});
        RecipeManager.removeVanillaRecipe(new ItemStack(BTWItems.dynamite, 2), new Object[]{"PF", "PN", "PS", Character.valueOf('P'), Item.paper, Character.valueOf('F'), BTWItems.fuse, Character.valueOf('N'), BTWItems.blastingOil, Character.valueOf('S'), BTWItems.soulDust});

        RecipeManager.addRecipe(new ItemStack(BTWItems.dynamite, 4), new Object[]{"PF", "PN", "PS", Character.valueOf('P'), Item.paper, Character.valueOf('F'), BTWItems.fuse, Character.valueOf('N'), BTWItems.blastingOil, Character.valueOf('S'), BTWTags.sawdusts});
        // done with dynamite

        // book
//        RecipeManager.removeVanillaRecipe(new ItemStack(Block.bookShelf), new Object[]{"###", "XYX", "###", Character.valueOf('#'), BTWTags.highEfficiencyWoodSidings, Character.valueOf('X'), Item.book, Character.valueOf('Y'), Item.enchantedBook});
//        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.book), new Object[]{Item.paper, Item.paper, Item.paper, BTWTags.rawLeathers});
        // done with book


        // change map recipe
        RecipeManager.removeVanillaRecipe(new ItemStack(Item.emptyMap, 1), new Object[]{"#S#", "#X#", "#S#", Character.valueOf('#'), Item.paper, Character.valueOf('X'), Item.compass, Character.valueOf('S'), BTWItems.soulUrn});
        RecipeManager.addRecipe(new ItemStack(Item.emptyMap, 1), new Object[]{"###", "#X#", "###", Character.valueOf('#'), Item.paper, Character.valueOf('X'), Item.compass});
        // done changing map

        // calamari with nitre
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.curedMeat, 1), new Object[]{NMItems.calamari, BTWItems.nitre});
        // done with calamari

        // firestarters
        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(Item.flintAndSteel, 1), new Object[]{new ItemStack(BTWItems.ironNugget), new ItemStack(Item.flint)});
        RecipeManager.addRecipe(new ItemStack(Item.flintAndSteel, 1), new Object[]{"## ", "#  ", " SS", Character.valueOf('#'), BTWItems.ironNugget, Character.valueOf('S'), Item.flint});

        RecipeManager.removeVanillaShapelessRecipe(new ItemStack(BTWItems.bowDrill), new Object[]{new ItemStack(Item.stick), new ItemStack(Item.stick), BTWTags.strings});
        RecipeManager.addShapelessRecipe(new ItemStack(BTWItems.bowDrill), new Object[]{new ItemStack(Item.stick), new ItemStack(Item.stick), new ItemStack(Item.stick), BTWTags.strings});
        // done with firestarters

        // planters
        RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.planter), new Object[]{new ItemStack(BTWBlocks.planterWithSoil)}).hideFromEMI();
        RecipeManager.addShapelessRecipe(new ItemStack(BTWBlocks.planter), new Object[]{new ItemStack(BTWBlocks.planter, 1, 8)}).hideFromEMI();
        // done planter-ing

        // potion recycling
        RecipeManager.addShapelessRecipe(new ItemStack(Item.glassBottle), new ItemStack[]{new ItemStack(Item.potion, 1, Short.MAX_VALUE)});
        // done recycling

        finishRecipes("Crafting Recipes");

    }
    private static void addMultiplayerRecipes(){
        if(MinecraftServer.getIsServer()){
            // signs
            RecipeManager.removeVanillaRecipe(new ItemStack(Item.sign, 3), new Object[]{"#", "X", Character.valueOf('#'), BTWTags.woodenSidings, Character.valueOf('X'), BTWTags.woodenMouldings});
            RecipeManager.addRecipe(new ItemStack(Item.sign), new Object[]{"#", "X", Character.valueOf('#'), BTWTags.logs, Character.valueOf('X'), Item.stick}).setAsDefaultAfterAchievement(BTWAchievements.CRAFT_SAW);
            // done with signs


            finishRecipes("Multiplayer Exclusive Recipes");
        }
    }

    private static void addPistonPackingRecipes() {
        // oysters
        PistonPackingCraftingManager.instance.removeRecipe(BTWBlocks.creeperOysterBlock, 0 , new ItemStack[]{new ItemStack(BTWItems.creeperOysters, 16)});
        RecipeManager.addPistonPackingRecipe(BTWBlocks.creeperOysterBlock, new ItemStack(BTWItems.creeperOysters, 9));

        // spider eyes
        PistonPackingCraftingManager.instance.removeRecipe(BTWBlocks.spiderEyeBlock, 0 , new ItemStack[]{new ItemStack(Item.spiderEye, 16)});
        RecipeManager.addPistonPackingRecipe(BTWBlocks.spiderEyeBlock, new ItemStack(Item.spiderEye, 9));
        finishRecipes("Piston Packing Recipes");

    }

    private static void addBloodSawRecipes(){
//        BloodSawCraftingManager.instance.addRecipe(new ItemStack[]{new ItemStack(Block.planks, 4, 0), new ItemStack(BTWItems.sawDust, 2), new ItemStack(BTWItems.bark, 1, 0)}, Block.wood, new int[]{0, 4, 8, 12}).setAsDefaultAfterAchievement(BTWAchievements.CRAFT_SAW);


//        BloodSawCraftingManager.instance.addWoodSubBlockRecipes();

        // Stone variants
        BloodSawCraftingManager.instance.addSawSubBlockRecipes(Block.stone, 0,
                BTWBlocks.stoneSidingAndCorner,
                BTWBlocks.stoneMouldingAndDecorative,
                new ItemStack(BTWBlocks.stoneSlab, 1, 0));

        BloodSawCraftingManager.instance.addSawSubBlockRecipes(Block.stone, 1,
                BTWBlocks.midStrataStoneSidingAndCorner,
                BTWBlocks.midStrataStoneMouldingAndDecorative,
                new ItemStack(BTWBlocks.stoneSlab, 1, 1));

        BloodSawCraftingManager.instance.addSawSubBlockRecipes(Block.stone, 2,
                BTWBlocks.deepStrataStoneSidingAndCorner,
                BTWBlocks.deepStrataStoneMouldingAndDecorative,
                new ItemStack(BTWBlocks.stoneSlab, 1, 2));

        // Stone Brick variants
        BloodSawCraftingManager.instance.addSawSubBlockRecipes(Block.stoneBrick, 0,
                BTWBlocks.stoneBrickSidingAndCorner,
                BTWBlocks.stoneBrickMouldingAndDecorative,
                new ItemStack(BTWBlocks.stoneBrickSlab, 1, 0));

        BloodSawCraftingManager.instance.addSawSubBlockRecipes(Block.stoneBrick, 4,
                BTWBlocks.midStrataStoneBrickSidingAndCorner,
                BTWBlocks.midStrataStoneBrickMouldingAndDecorative,
                new ItemStack(BTWBlocks.stoneBrickSlab, 1, 1));

        BloodSawCraftingManager.instance.addSawSubBlockRecipes(Block.stoneBrick, 8,
                BTWBlocks.deepStrataStoneBrickSidingAndCorner,
                BTWBlocks.deepStrataStoneBrickMouldingAndDecorative,
                new ItemStack(BTWBlocks.stoneBrickSlab, 1, 2));

        BloodSawCraftingManager.instance.addSawSubBlockRecipes(BTWBlocks.aestheticOpaque, 9,
                BTWBlocks.whiteStoneSidingAndCorner,
                BTWBlocks.whiteStoneMouldingAndDecroative,
                new ItemStack(BTWBlocks.aestheticNonOpaque, 1, 10));

        BloodSawCraftingManager.instance.addSawSubBlockRecipes(Block.netherBrick, 0,
                BTWBlocks.netherBrickSidingAndCorner,
                BTWBlocks.netherBrickMouldingAndDecorative,
                new ItemStack(Block.stoneSingleSlab.blockID, 1, 6));

        BloodSawCraftingManager.instance.addSawSubBlockRecipes(Block.brick, 0,
                BTWBlocks.brickSidingAndCorner,
                BTWBlocks.brickMouldingAndDecorative,
                new ItemStack(Block.stoneSingleSlab.blockID, 1, 4));

        // Sandstone uses wildcard metadata
        BloodSawCraftingManager.instance.addSawSubBlockRecipes(Block.sandStone, Short.MAX_VALUE, 0,
                BTWBlocks.sandstoneSidingAndCorner,
                BTWBlocks.sandstoneMouldingAndDecorative,
                new ItemStack(Block.stoneSingleSlab.blockID, 1, 1));

        BloodSawCraftingManager.instance.addSawSubBlockRecipes(Block.blockNetherQuartz, 0,
                BTWBlocks.quartzSidingAndCorner,
                BTWBlocks.quartzMouldingAndDecorative,
                new ItemStack(Block.stoneSingleSlab.blockID, 1, 7));

        finishRecipes("Blood Saw Recipes");

    }










    // ACHIEVEMENT HELPERS

    private static void addParent(Achievement acObj, Achievement achievementToAdd){
        ((AchievementExt) acObj).nightmareMode$appendParent(achievementToAdd);
    }
    private static void setHidden(Achievement acObj, boolean hidden){
        acObj.isHidden = hidden;
    }

    private static void kill(Achievement acObj) {
        StatList.allStats.remove(acObj);
        StatListAccess.getOneShotStats().remove(acObj.statId);

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
        AchievementExt ext = (AchievementExt) myAchievement;
        Achievement[] current = ((AchievementAccessor)myAchievement).getParents();
        Achievement[] updated = ext.nightmareMode$removeParent(current, parentToRemove);
        ((AchievementAccessor)myAchievement).setParentAchievements(updated);
    }
    private static void destroyParents(Achievement myAchievement){
        ((AchievementAccessor)myAchievement).setParentAchievements(new Achievement[0]);
    }

    private static void setIcon(Achievement acObj, Block block){
        AchievementExt ext = (AchievementExt) acObj;
        ext.nightmareMode$setIcon(new ItemStack(block));
    }
    private static void setIcon(Achievement acObj, Item item){
        AchievementExt ext = (AchievementExt) acObj;
        ext.nightmareMode$setIcon(new ItemStack(item));
    }
    private static void setIcon(Achievement acObj, ItemStack stack){
        AchievementExt ext = (AchievementExt) acObj;
        ext.nightmareMode$setIcon(stack);
    }

}
