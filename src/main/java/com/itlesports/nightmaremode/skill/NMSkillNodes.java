package com.itlesports.nightmaremode.skill;

import api.achievement.AchievementHandler;
import btw.achievement.BTWAchievements;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

import static com.itlesports.nightmaremode.skill.SkillNodeProvider.getBuilder;

public final class NMSkillNodes {
    public static final SkillBranch MINING = new SkillBranch("Mining", Item.pickaxeIron);
    public static final SkillBranch HUSBANDRY = new SkillBranch("Husbandry", Item.wheat);
    public static final SkillBranch RITUAL = new SkillBranch("Ritual", Item.blazePowder);
    public static final SkillBranch KNOWLEDGE = new SkillBranch("Knowledge", Item.book);
    public static final SkillBranch COMBAT = new SkillBranch("Combat", Item.swordIron);

    // Mining
    public static final SkillNode CLAY_BULK = bring(
            "clay_bulk",
            "Clay Stockpile",
            Block.blockClay,
            0, 0,
            "Bring 32 clay blocks.",
            Block.blockClay.blockID, 0, false, 32,
            "Clay cooks 12,000 ticks faster.", SkillRewardActions.addClayCookTimeReduction(12000),
            MINING, false);

    public static final SkillNode CLAY_EXCAVATOR = counter(
            "clay_excavator",
            "Clay Excavator",
            Block.blockClay,
            -1, 1,
            "Mine 1,500 clay blocks.",
            (p, w) -> SkillHandler.getPlayerData(p).clayMined >= 1500,
            "Clay cooks another 12,000 ticks faster.", SkillRewardActions.addClayCookTimeReduction(12000),
            MINING, false,
            CLAY_BULK);

    public static final SkillNode STONE_MARATHON = counter(
            "stone_marathon",
            "Stone Marathon",
            Block.stone,
            0, -1,
            "Mine 1,000 stone of any strata.",
            (p, w) -> SkillHandler.getPlayerData(p).stoneMined >= 1000,
            "+5% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.05F),
            MINING, false);

    public static final SkillNode FLINT_TOOLMAKING = bring(
            "flint_toolmaking",
            "Flint Toolmaking",
            Item.flint,
            1, 3,
            "Bring 4 flint.",
            Item.flint.itemID, 0, false, 4,
            "Unlock the flint axe crafting recipe.", none(),
            MINING, false);

    public static final SkillNode IRON_SAMPLE = bring(
            "iron_sample",
            "Iron Sample",
            Item.ingotIron,
            1, 0,
            "Bring 1 iron ingot.",
            Item.ingotIron.itemID, 0, false, 1,
            "+5% global iron-pile chance and 1/4 wood-gravity progress.", combine(SkillRewardActions.addGlobalIronPileChance(0.05F), SkillRewardActions.addWoodGravityProgress()),
            MINING, true);

    public static final SkillNode IRON_PILE_CACHE = bring(
            "iron_pile_cache",
            "Pile Preference",
            BTWItems.ironOrePile,
            2, 0,
            "Bring 8 iron ore piles.",
            BTWItems.ironOrePile.itemID, 0, false, 8,
            "+5% iron-pile chance.", SkillRewardActions.addIronPileChance(0.05F),
            MINING, false,
            IRON_SAMPLE);

    public static final SkillNode LITHIUM_CRAFTING = bring(
            "lithium_crafting",
            "Lithium Tempering",
            NMItems.rawLithium,
            -1, 0,
            "Bring 64 raw lithium.",
            NMItems.rawLithium.itemID, 0, false, 64,
            "+5% average crafted-item durability.", SkillRewardActions.addCraftingDurability(0.05F),
            MINING, false);

    public static final SkillNode LITHIUM_DOUBLING = bring(
            "lithium_doubling",
            "Lithium Prospector",
            NMItems.refinedLithium,
            -2, 0,
            "Bring 256 refined lithium.",
            NMItems.refinedLithium.itemID, 0, false, 256,
            "Lithium ore drops twice as much raw lithium.", SkillRewardActions.doubleLithiumDrops(),
            MINING, false,
            LITHIUM_CRAFTING);

    public static final SkillNode BLACKSTONE_AUTHORITY = bring(
            "blackstone_authority",
            "Blackstone Authority",
            new ItemStack(Block.cobblestone, 1, 2),
            -1, -1,
            "Bring 64 blackstone (strata-three cobblestone).",
            Block.cobblestone.blockID, 2, true, 64,
            "Strata-three ore can be mined.", SkillRewardActions.unlockStrataThreeOre(),
            MINING, false,
            STONE_MARATHON);

    public static final SkillNode PERFECT_DIAMOND_YIELD = bring(
            "perfect_diamond_yield",
            "Perfect Diamond Yield",
            NMItems.refinedDiamondIngot,
            1, 2,
            "Bring 1 refined diamond ingot.",
            NMItems.refinedDiamondIngot.itemID, 0, false, 1,
            "Diamond ore always drops diamond-bearing rock.", SkillRewardActions.guaranteeDiamondRockDrop(),
            MINING, false);

    public static final SkillNode NICKEL_DUPLICATION = counter(
            "nickel_duplication",
            "Nickel Duplication",
            NMBlocks.nickelOre,
            -2, -2,
            "Mine 500 nickel ore.",
            (p, w) -> SkillHandler.getPlayerData(p).nickelOreMined >= 500,
            "Nickel ore gains a 5% chance to drop a second rock.", SkillRewardActions.addDoubleNickelRockChance(0.05F),
            MINING, false,
            BLACKSTONE_AUTHORITY);

    public static final SkillNode DIAMOND_LITHIUM = bring(
            "diamond_lithium",
            "Lithium Diamond Theory",
            NMItems.refinedLithium,
            0, 1,
            "Bring 1 refined lithium.",
            NMItems.refinedLithium.itemID, 0, false, 1,
            "+1/5 Diamond Extraction progress.", SkillRewardActions.addDiamondHarvestProgress(),
            MINING, false);

    public static final SkillNode NETHER_OBSIDIAN = bring(
            "nether_obsidian",
            "Crude Portal Theory",
            new ItemStack(Block.obsidian, 1, 1),
            -2, 1,
            "Bring 16 crude obsidian.",
            Block.obsidian.blockID, 1, true, 16,
            "+1/7 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            MINING, true);

    public static final SkillNode DIAMOND_CRYSTALS = bring(
            "diamond_crystals",
            "Crystal Diamond Theory",
            NMItems.polishedCrystalShard,
            1, 1,
            "Bring 4 polished crystal shards.",
            NMItems.polishedCrystalShard.itemID, 0, false, 4,
            "+1/5 Diamond Extraction progress.", SkillRewardActions.addDiamondHarvestProgress(),
            MINING, false);

    public static final SkillNode DIAMOND_ANVIL = bring(
            "diamond_anvil",
            "Anvil Diamond Theory",
            Block.anvil,
            2, 2,
            "Bring 1 iron anvil.",
            Block.anvil.blockID, 0, false, 1,
            "+1/5 Diamond Extraction progress.", SkillRewardActions.addDiamondHarvestProgress(),
            MINING, false);

    public static final SkillNode HAMMER_PRESERVATION = counter(
            "hammer_preservation",
            "Hammer Preservation",
            Block.cobblestone,
            1, -2,
            "Mine 3,000 strata-one cobblestone.",
            (p, w) -> SkillHandler.getPlayerData(p).strataOneCobblestoneMined >= 3000,
            "10% chance not to consume hammer durability.", SkillRewardActions.addHammerDurabilitySaveChance(0.10F),
            MINING, false,
            STONE_MARATHON);

    public static final SkillNode WASTE_EFFICIENCY = bring(
            "waste_efficiency",
            "Waste Efficiency",
            NMItems.refinementWaste,
            -3, -1,
            "Bring 32 refinement waste.",
            NMItems.refinementWaste.itemID, 0, false, 32,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F),
            MINING, false);

    public static final SkillNode FAILED_REFINEMENT = bring(
            "failed_refinement",
            "Failure Analysis",
            NMItems.failedDiamondRefinement,
            2, -1,
            "Bring 16 failed diamond refinement.",
            NMItems.failedDiamondRefinement.itemID, 0, false, 16,
            "+10% cistern processing speed.", SkillRewardActions.addCisternSpeed(0.10F),
            MINING, false);

    public static final SkillNode CAVE_OXYGEN = bring(
            "cave_oxygen",
            "Cave Breathing",
            NMItems.diamondBearingRock,
            3, -1,
            "Bring 64 diamond-bearing rock.",
            NMItems.diamondBearingRock.itemID, 0, false, 64,
            "10% less oxygen loss in caves.", SkillRewardActions.addOxygenLossReduction(0.10F),
            MINING, false);

    public static final SkillNode NICKEL_HEAT_RECIPE = bring(
            "nickel_heat_recipe",
            "Nickel Heatwork",
            NMItems.nickelPlate,
            -3, -2,
            "Bring 4 nickel plates.",
            NMItems.nickelPlate.itemID, 0, false, 4,
            "Unlock the Heat-Resistant Nickel Component recipe.", none(),
            MINING, false);

    public static final SkillNode COAL_RECIPE = bring(
            "coal_recipe",
            "Coal Reconstitution",
            BTWItems.coalDust,
            0, -2,
            "Bring 64 coal dust.",
            BTWItems.coalDust.itemID, 0, false, 64,
            "Unlock the coal recipe.", none(),
            MINING, false);

    public static final SkillNode LITHIUM_CISTERN = bring(
            "lithium_cistern",
            "Lithium Brine Control",
            NMItems.lithiumSalt,
            3, -2,
            "Bring 16 lithium salt.",
            NMItems.lithiumSalt.itemID, 0, false, 16,
            "+10% cistern processing speed.", SkillRewardActions.addCisternSpeed(0.10F),
            MINING, false,
            FAILED_REFINEMENT);

    public static final SkillNode IRON_BLOOM_RECIPE = bring(
            "iron_bloom_recipe",
            "Bloom Consolidation",
            NMItems.ironBloom,
            2, 1,
            "Bring 8 iron bloom items.",
            NMItems.ironBloom.itemID, 0, false, 8,
            "+1/5 iron-ingot recipe progress.", SkillRewardActions.addIronIngotRecipeProgress(),
            MINING, false);

    public static final SkillNode NETHER_DIAMOND_HAMMER = bring(
            "nether_diamond_hammer",
            "Portal Hammer",
            NMItems.diamondHammer,
            -3, 2,
            "Bring 1 diamond hammer.",
            NMItems.diamondHammer.itemID, 0, false, 1,
            "+1/7 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            MINING, true);

    public static final SkillNode CRYSTAL_POCKET_SKILL = bring(
            "crystal_pocket_skill",
            "Pocket Appraisal",
            NMItems.uncleanedCrystalShard,
            0, 2,
            "Bring 32 uncleaned crystal shards.",
            NMItems.uncleanedCrystalShard.itemID, 0, false, 32,
            "+10% crystal-pocket shard chance.", SkillRewardActions.addCrystalDropChance(0.10F),
            MINING, false);

    public static final SkillNode STEEL_HAMMER_DAMAGE = bring(
            "steel_hammer_damage",
            "Steel-Driven Violence",
            NMItems.steelHammer,
            2, 3,
            "Bring 1 steel hammer.",
            NMItems.steelHammer.itemID, 0, false, 1,
            "+5% melee damage.", SkillRewardActions.addMeleeDamage(0.05F),
            MINING, false);

    public static final SkillNode GRAVEL_SHOVEL = bring(
            "gravel_shovel",
            "Gravel Familiarity",
            Block.gravel,
            -1, -3,
            "Bring 64 gravel.",
            Block.gravel.blockID, 0, false, 64,
            "Shovels mine 5% faster.", SkillRewardActions.addShovelSpeed(0.05F),
            MINING, false);

    public static final SkillNode CLAY_SHOVEL = bring(
            "clay_shovel",
            "Clay Familiarity",
            Item.clay,
            0, -3,
            "Bring 64 clay.",
            Item.clay.itemID, 0, false, 64,
            "Shovels mine 5% faster.", SkillRewardActions.addShovelSpeed(0.05F),
            MINING, false,
            GRAVEL_SHOVEL);

    public static final SkillNode NICKEL_BLAZE = bring(
            "nickel_blaze",
            "Nickel Pyrology",
            NMItems.rawNickelRock,
            -3, -3,
            "Bring 64 nickel-bearing rock.",
            NMItems.rawNickelRock.itemID, 0, false, 64,
            "+5% blaze-rod drop chance.", SkillRewardActions.addBlazeRodDropChance(0.05F),
            MINING, false);


    // Husbandry
    public static final SkillNode GRASS_HARVEST = counter(
            "grass_harvest",
            "Grass Reaper",
            Block.tallGrass,
            0, 0,
            "Harvest 1,000 tall grass.",
            (p, w) -> SkillHandler.getPlayerData(p).tallGrassMined >= 1000,
            "+2% hemp-seed chance when hoeing grass.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false);

    public static final SkillNode DRIED_FIBER_HEMP = bring(
            "dried_fiber_hemp",
            "Fiber Seed Lore",
            NMItems.driedPlantFiber,
            -1, 1,
            "Bring 64 dried plant fibers.",
            NMItems.driedPlantFiber.itemID, 0, false, 64,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false,
            GRASS_HARVEST);

    public static final SkillNode SAPLING_PLANTER = counter(
            "sapling_planter",
            "Forest Planter",
            Block.sapling,
            0, -1,
            "Plant 100 saplings.",
            (p, w) -> SkillHandler.getPlayerData(p).saplingsPlanted >= 100,
            "+5% twig drop chance.", SkillRewardActions.addTwigDropChance(0.05F),
            HUSBANDRY, false);

    public static final SkillNode GRASS_BLOCK_HEMP = bring(
            "grass_block_hemp",
            "Sod Examination",
            Block.grass,
            1, 1,
            "Bring 1 grass block.",
            Block.grass.blockID, 0, false, 1,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false,
            GRASS_HARVEST);

    public static final SkillNode NETHER_TAMER = counter(
            "nether_tamer",
            "Dimensional Tamer",
            Item.leash,
            3, 1,
            "Tame 8 animals.",
            (p, w) -> SkillHandler.getPlayerData(p).animalsTamed >= 8,
            "+1/7 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            HUSBANDRY, true);

    public static final SkillNode XP_CAP_REMOVAL = counter(
            "xp_cap_removal",
            "Weed Transcendence",
            BTWItems.hempSeeds,
            -2, -1,
            "Remove weeds 500 times.",
            (p, w) -> SkillHandler.getPlayerData(p).weedsRemoved >= 500,
            "Experience level can exceed 30.", SkillRewardActions.unlockXpAboveThirty(),
            HUSBANDRY, false);

    public static final SkillNode RARE_FISHING = counter(
            "rare_fishing",
            "Rare Angler",
            Item.fishingRod,
            2, -1,
            "Catch 50 fish.",
            (p, w) -> SkillHandler.getPlayerData(p).fishCaught >= 50,
            "+5% rare-fish chance.", SkillRewardActions.addRareFishChance(0.05F),
            HUSBANDRY, false);

    public static final SkillNode COOKED_PRESERVATION = counter(
            "cooked_preservation",
            "Preserving Cook",
            Item.beefCooked,
            -1, -1,
            "Cook 200 food items.",
            (p, w) -> SkillHandler.getPlayerData(p).foodCooked >= 200,
            "Raw food globally spoils 5% slower and grants 1/4 wood-gravity progress.", combine(SkillRewardActions.slowFoodSpoilageGlobally(), SkillRewardActions.addWoodGravityProgress()),
            HUSBANDRY, true);

    public static final SkillNode DIRT_SHOVEL = counter(
            "dirt_shovel",
            "Dirt Familiarity",
            Block.dirt,
            1, 0,
            "Break 1,000 dirt.",
            (p, w) -> SkillHandler.getPlayerData(p).dirtMined >= 1000,
            "Shovels mine 5% faster.", SkillRewardActions.addShovelSpeed(0.05F),
            HUSBANDRY, false);

    public static final SkillNode DIRT_FIBER = counter(
            "dirt_fiber",
            "Soil Fiber Mastery",
            Block.dirt,
            2, 0,
            "Break 2,000 dirt.",
            (p, w) -> SkillHandler.getPlayerData(p).dirtMined >= 2000,
            "Tall grass always drops plant fiber.", SkillRewardActions.alwaysDropPlantFiberFromTallGrass(),
            HUSBANDRY, false,
            DIRT_SHOVEL);

    public static final SkillNode LOG_TWIGS = bring(
            "log_twigs",
            "Logged Branches",
            Block.wood,
            -1, -2,
            "Bring 64 logs.",
            Block.wood.blockID, 0, false, 64,
            "+5% twig drop chance.", SkillRewardActions.addTwigDropChance(0.05F),
            HUSBANDRY, false,
            SAPLING_PLANTER);

    public static final SkillNode CALAMARI_LOOT = bring(
            "calamari_loot",
            "Calamari Tribute",
            NMItems.calamari,
            3, -1,
            "Bring 16 calamari.",
            NMItems.calamari.itemID, 0, false, 16,
            "+5% mob drops.", SkillRewardActions.addMobLootChance(0.05F),
            HUSBANDRY, false);

    public static final SkillNode CROP_HEMP = counter(
            "crop_hemp",
            "Crop Rotation",
            Block.crops,
            0, 1,
            "Plant 200 crops.",
            (p, w) -> SkillHandler.getPlayerData(p).cropsPlanted >= 200,
            "+5% hemp-seed chance when hoeing grass.", SkillRewardActions.addHempSeedChance(0.05F),
            HUSBANDRY, false);

    public static final SkillNode BEDROLL_RECIPE = bring(
            "bedroll_recipe",
            "Portable Bedding",
            BTWItems.wool,
            -2, 1,
            "Bring 128 wool.",
            BTWItems.wool.itemID, 0, false, 128,
            "Unlock the bedroll recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode CHICKEN_FEED_RECIPE = bring(
            "chicken_feed_recipe",
            "Feathered Nutrition",
            Item.feather,
            -3, 1,
            "Bring 64 feathers.",
            Item.feather.itemID, 0, false, 64,
            "Unlock the chicken-feed recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode LEATHER_BREEDING = counter(
            "leather_breeding",
            "Breeder's Leather",
            Item.leather,
            -2, 0,
            "Breed 50 animals.",
            (p, w) -> SkillHandler.getPlayerData(p).animalsBred >= 50,
            "+1/2 leather-armor recipe progress.", SkillRewardActions.addLeatherArmorProgress(),
            HUSBANDRY, false);

    public static final SkillNode BETTER_LITHIUM_SALT = bring(
            "better_lithium_salt",
            "Sweet Lithium",
            Item.reed,
            1, 2,
            "Bring 256 sugar cane.",
            Item.reed.itemID, 0, false, 256,
            "Unlock a lithium-salt recipe yielding 3.", none(),
            HUSBANDRY, false);

    public static final SkillNode CAKE_RECIPE = counter(
            "cake_recipe",
            "Dairy Patissier",
            Item.bucketMilk,
            2, 2,
            "Milk cows 100 times.",
            (p, w) -> SkillHandler.getPlayerData(p).cowsMilked >= 100,
            "Unlock the cake recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode PUMPKIN_FIBER = bring(
            "pumpkin_fiber",
            "Pumpkin Mulch",
            Block.pumpkin,
            -1, 2,
            "Bring 64 pumpkins.",
            Block.pumpkin.blockID, 0, false, 64,
            "+10% tall-grass plant-fiber chance.", SkillRewardActions.addTallGrassPlantFiberChance(0.10F),
            HUSBANDRY, false);

    public static final SkillNode FIBER_TO_STRAW = bring(
            "fiber_to_straw",
            "Fiber Compression",
            NMItems.plantFiber,
            -2, 2,
            "Bring 1,024 plant fibers.",
            NMItems.plantFiber.itemID, 0, false, 1024,
            "Unlock direct plant-fiber-to-straw crafting.", none(),
            HUSBANDRY, false);

    public static final SkillNode RARE_FISH_TROPHIES = specialBring(
            "rare_fish_trophies",
            "Trophy Angler",
            NMItems.swordfish,
            2, -2,
            "Bring 32 rare fish.",
            32,
            "+5% rare-fish chance.", SkillRewardActions.addRareFishChance(0.05F),
            HUSBANDRY, false,
            RARE_FISHING);

    public static final SkillNode OXYGEN_MASK_RECIPE = bring(
            "oxygen_mask_recipe",
            "Fiber Filtration",
            NMItems.driedPlantFiber,
            -3, 2,
            "Bring 300 dried plant fiber.",
            NMItems.driedPlantFiber.itemID, 0, false, 300,
            "Unlock the Oxygen Mask recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode LEAF_TWIGS = counter(
            "leaf_twigs",
            "Leaf Sifter",
            Block.leaves,
            0, -2,
            "Break 500 leaves.",
            (p, w) -> SkillHandler.getPlayerData(p).leavesMined >= 500,
            "+5% twig drop chance.", SkillRewardActions.addTwigDropChance(0.05F),
            HUSBANDRY, false,
            SAPLING_PLANTER);

    public static final SkillNode MELON_DAMAGE = bring(
            "melon_damage",
            "Melon Musculature",
            Block.melon,
            3, 2,
            "Bring 64 melon blocks.",
            Block.melon.blockID, 0, false, 64,
            "+1% melee damage.", SkillRewardActions.addMeleeDamage(0.01F),
            HUSBANDRY, false);

    public static final SkillNode MATURE_CROP_HEMP = counter(
            "mature_crop_hemp",
            "Mature Harvest",
            Block.crops,
            0, 2,
            "Harvest 500 fully-grown crops.",
            (p, w) -> SkillHandler.getPlayerData(p).fullyGrownCropsHarvested >= 500,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false,
            CROP_HEMP);

    public static final SkillNode CURED_PRESERVATION = bring(
            "cured_preservation",
            "Cured Example",
            BTWItems.curedMeat,
            -1, -3,
            "Bring 16 cured meat.",
            BTWItems.curedMeat.itemID, 0, false, 16,
            "Raw food spoils 5% slower.", SkillRewardActions.slowFoodSpoilage(),
            HUSBANDRY, false,
            COOKED_PRESERVATION);

    public static final SkillNode BONEMEAL_FIBER = bring(
            "bonemeal_fiber",
            "Bonemeal Fiber",
            new ItemStack(Item.dyePowder, 1, 15),
            1, 3,
            "Bring 256 bonemeal.",
            Item.dyePowder.itemID, 15, true, 256,
            "+10% tall-grass plant-fiber chance.", SkillRewardActions.addTallGrassPlantFiberChance(0.10F),
            HUSBANDRY, false);

    public static final SkillNode NETHER_WART_FARMING = counter(
            "nether_wart_farming",
            "Weed Sovereignty",
            Item.netherStalkSeeds,
            -3, -1,
            "Remove weeds 1,000 times.",
            (p, w) -> SkillHandler.getPlayerData(p).weedsRemoved >= 1000,
            "Nether wart can be farmed.", SkillRewardActions.unlockNetherWartFarming(),
            HUSBANDRY, false,
            XP_CAP_REMOVAL);

    public static final SkillNode FOUL_PRESERVATION = bring(
            "foul_preservation",
            "Spoilage Autopsy",
            BTWItems.foulFood,
            -2, -3,
            "Bring 256 foul food.",
            BTWItems.foulFood.itemID, 0, false, 256,
            "Raw food spoils 5% slower.", SkillRewardActions.slowFoodSpoilage(),
            HUSBANDRY, false,
            CURED_PRESERVATION);


    // Knowledge
    public static final SkillNode EXPERIENCE_PRIMER = bring(
            "experience_primer",
            "Experience Primer",
            Item.book,
            0, 0,
            "Bring 1 book.",
            Item.book.itemID, 0, false, 1,
            "Experience points can be gained.", SkillRewardActions.unlockExperienceGain(),
            KNOWLEDGE, false);

    public static final SkillNode WOOD_GRAVITY_BOOKS = bring(
            "wood_gravity_books",
            "Structural Library",
            Item.book,
            -1, 0,
            "Bring 16 books.",
            Item.book.itemID, 0, false, 16,
            "+1/4 wood-gravity progress.", SkillRewardActions.addWoodGravityProgress(),
            KNOWLEDGE, true,
            EXPERIENCE_PRIMER);

    public static final SkillNode ENCHANT_BOOKS_32 = bring(
            "enchant_books_32",
            "Enchanting Margins",
            Item.book,
            -2, 1,
            "Bring 32 books.",
            Item.book.itemID, 0, false, 32,
            "2% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.02F),
            KNOWLEDGE, false,
            WOOD_GRAVITY_BOOKS);

    public static final SkillNode HOTBAR_BOOKS = bring(
            "hotbar_books",
            "Indexed Hotbar",
            Item.book,
            -3, 1,
            "Bring 128 books.",
            Item.book.itemID, 0, false, 128,
            "+1 hotbar slot.", SkillRewardActions.addHotbarSlots(1),
            KNOWLEDGE, false,
            ENCHANT_BOOKS_32);

    public static final SkillNode CISTERN_USE = bring(
            "cistern_use",
            "Redstone Hydraulics",
            Item.redstone,
            1, 0,
            "Bring 16 redstone.",
            Item.redstone.itemID, 0, false, 16,
            "Cisterns can be used.", SkillRewardActions.unlockCisternUse(),
            KNOWLEDGE, false);

    public static final SkillNode DIAMOND_PRECISION_GEAR = bring(
            "diamond_precision_gear",
            "Precision Diamond Theory",
            NMItems.precisionCrystalGear,
            0, -1,
            "Bring 1 precision crystal gear.",
            NMItems.precisionCrystalGear.itemID, 0, false, 1,
            "+1/5 Diamond Extraction progress.", SkillRewardActions.addDiamondHarvestProgress(),
            KNOWLEDGE, false);

    public static final SkillNode THIRD_INVENTORY_ROW = counter(
            "third_inventory_row",
            "Expanded Studies",
            BTWBlocks.chest,
            -1, -1,
            "Reach 30 XP levels.",
            (p, w) -> p.experienceLevel >= 30,
            "Permanently unlock the third inventory row.", SkillRewardActions.unlockThirdInventoryRow(),
            KNOWLEDGE, false,
            EXPERIENCE_PRIMER);

    public static final SkillNode TRADE_100 = counter(
            "trade_100",
            "Market Observer",
            Item.emerald,
            2, 0,
            "Trade 100 times.",
            (p, w) -> SkillHandler.getPlayerData(p).tradesCompleted >= 100,
            "Villager profession-change chance falls to 30%.", SkillRewardActions.setVillagerProfessionChangeChance(0.30F),
            KNOWLEDGE, false);

    public static final SkillNode TRADE_250 = counter(
            "trade_250",
            "Market Analyst",
            Item.emerald,
            3, -1,
            "Trade 250 times.",
            (p, w) -> SkillHandler.getPlayerData(p).tradesCompleted >= 250,
            "Villager profession-change chance falls to 10%.", SkillRewardActions.setVillagerProfessionChangeChance(0.10F),
            KNOWLEDGE, false,
            TRADE_100);

    public static final SkillNode ENCHANTMENT_TABLE_USE = bring(
            "enchantment_table_use",
            "Ancient Enchanting",
            Item.enchantedBook,
            -2, 2,
            "Bring 1 ancient manuscript.",
            Item.enchantedBook.itemID, 0, false, 1,
            "The enchantment table can be used.", SkillRewardActions.unlockEnchantmentTableUse(),
            KNOWLEDGE, false);

    public static final SkillNode WITHER_XP_BOTTLES = bring(
            "wither_xp_bottles",
            "Bottled Invocation",
            Item.expBottle,
            1, -2,
            "Bring 64 bottles of enchanting.",
            Item.expBottle.itemID, 0, false, 64,
            "+1/5 Wither-summoning progress.", SkillRewardActions.addWitherSummonProgress(),
            KNOWLEDGE, true);

    public static final SkillNode WITHER_XP_LEVELS = counter(
            "wither_xp_levels",
            "Experienced Invocation",
            Item.expBottle,
            0, -2,
            "Reach 50 XP levels.",
            (p, w) -> p.experienceLevel >= 50,
            "+1/5 Wither-summoning progress.", SkillRewardActions.addWitherSummonProgress(),
            KNOWLEDGE, true,
            XP_CAP_REMOVAL);

    public static final SkillNode ENCHANT_MANUSCRIPTS_10 = bring(
            "enchant_manuscripts_10",
            "Manuscript Corpus",
            Item.enchantedBook,
            -3, 2,
            "Bring 10 ancient manuscripts.",
            Item.enchantedBook.itemID, 0, false, 10,
            "10% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.10F),
            KNOWLEDGE, false,
            ENCHANTMENT_TABLE_USE);

    public static final SkillNode TRADE_500 = counter(
            "trade_500",
            "Market Certainty",
            Item.emerald,
            4, -1,
            "Trade 500 times.",
            (p, w) -> SkillHandler.getPlayerData(p).tradesCompleted >= 500,
            "Villagers never change profession on level-up.", SkillRewardActions.setVillagerProfessionChangeChance(0.0F),
            KNOWLEDGE, false,
            TRADE_250);

    public static final SkillNode BOOKSHELF_XP = counter(
            "bookshelf_xp",
            "Shelf Scholar",
            Block.bookShelf,
            -1, 1,
            "Craft 64 bookshelves.",
            (p, w) -> SkillHandler.getPlayerData(p).bookshelvesCrafted >= 64,
            "+10% experience gained.", SkillRewardActions.addXpGain(0.10F),
            KNOWLEDGE, false);

    public static final SkillNode LAPIS_64 = bring(
            "lapis_64",
            "Lapis Notes",
            new ItemStack(Item.dyePowder, 1, 4),
            -2, -1,
            "Bring 64 lapis lazuli.",
            Item.dyePowder.itemID, 4, true, 64,
            "2% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.02F),
            KNOWLEDGE, false);

    public static final SkillNode LAPIS_512 = bring(
            "lapis_512",
            "Lapis Thesis",
            new ItemStack(Item.dyePowder, 1, 4),
            -3, -2,
            "Bring 512 lapis lazuli.",
            Item.dyePowder.itemID, 4, true, 512,
            "3% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.03F),
            KNOWLEDGE, false,
            LAPIS_64);

    public static final SkillNode NICKEL_MACHINE_RECIPE = bring(
            "nickel_machine_recipe",
            "Redstone Machining",
            Item.redstone,
            2, 1,
            "Bring 256 redstone.",
            Item.redstone.itemID, 0, false, 256,
            "Unlock the Nickel Machine Part recipe.", none(),
            KNOWLEDGE, false,
            CISTERN_USE);

    public static final SkillNode ENCHANTED_APPLE_XP = bring(
            "enchanted_apple_xp",
            "Enchanted Nutrition",
            new ItemStack(Item.appleGold, 1, 1),
            1, 2,
            "Bring 1 enchanted golden apple.",
            Item.appleGold.itemID, 1, true, 1,
            "+10% experience gained.", SkillRewardActions.addXpGain(0.10F),
            KNOWLEDGE, false);

    public static final SkillNode VILLAGER_CURING = bring(
            "villager_curing",
            "Golden Cure",
            new ItemStack(Item.appleGold, 1, 0),
            3, 1,
            "Bring 4 regular golden apples.",
            Item.appleGold.itemID, 0, true, 4,
            "Villagers can be cured.", SkillRewardActions.unlockVillagerCuring(),
            KNOWLEDGE, false);

    public static final SkillNode CRYSTAL_LENS_RECIPE = bring(
            "crystal_lens_recipe",
            "Glass Optics",
            Block.glass,
            0, 1,
            "Bring 64 glass.",
            Block.glass.blockID, 0, false, 64,
            "Unlock the Crystal Lens recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode CHEST_RECIPE = bring(
            "chest_recipe",
            "Framed Storage",
            Item.itemFrame,
            2, -1,
            "Bring 27 item frames.",
            Item.itemFrame.itemID, 0, false, 27,
            "Unlock the chest recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BOOKSHELF_RECIPE = bring(
            "bookshelf_recipe",
            "Authored Shelving",
            Item.writtenBook,
            -1, 2,
            "Bring 3 written books.",
            Item.writtenBook.itemID, 0, false, 3,
            "Unlock the bookshelf recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BOOK_QUILL_RECIPE = bring(
            "book_quill_recipe",
            "Paperwork",
            Item.paper,
            -2, 0,
            "Bring 64 paper.",
            Item.paper.itemID, 0, false, 64,
            "Unlock the book-and-quill recipe.", none(),
            KNOWLEDGE, false);


    // Ritual
    public static final SkillNode BREWING_STAND_USE = bring(
            "brewing_stand_use",
            "Witch Wart Alchemy",
            BTWItems.witchWart,
            0, 0,
            "Bring 64 witch warts.",
            BTWItems.witchWart.itemID, 0, false, 64,
            "Brewing stands can be used.", SkillRewardActions.unlockBrewingStandUse(),
            RITUAL, false);

    public static final SkillNode NETHER_ENCHANT_TABLE = bring(
            "nether_enchant_table",
            "Portal Enchantment",
            Block.enchantmentTable,
            -1, 0,
            "Bring 1 enchantment table.",
            Block.enchantmentTable.blockID, 0, false, 1,
            "+1/7 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            RITUAL, true);

    public static final SkillNode NETHER_BLOOD_ORBS = bring(
            "nether_blood_orbs",
            "Blood Portal",
            NMItems.bloodOrb,
            -2, -1,
            "Bring 64 blood orbs.",
            NMItems.bloodOrb.itemID, 0, false, 64,
            "+1/7 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            RITUAL, true);

    public static final SkillNode POTIONS_8_XP = bring(
            "potions_8_xp",
            "Tasted Experience",
            Item.potion,
            0, 1,
            "Bring 8 potions of any kind.",
            Item.potion.itemID, 0, false, 8,
            "+10% experience gained.", SkillRewardActions.addXpGain(0.10F),
            RITUAL, false,
            BREWING_STAND_USE);

    public static final SkillNode BLAZE_POWDER_RODS = bring(
            "blaze_powder_rods",
            "Blaze Distillation",
            Item.blazePowder,
            1, 1,
            "Bring 16 blaze powder.",
            Item.blazePowder.itemID, 0, false, 16,
            "+10% blaze-rod drop chance.", SkillRewardActions.addBlazeRodDropChance(0.10F),
            RITUAL, false);

    public static final SkillNode NETHER_DRAGON_VESSEL = bring(
            "nether_dragon_vessel",
            "Vessel Portal",
            BTWBlocks.dragonVessel,
            -2, 0,
            "Bring 1 Vessel of the Dragon.",
            BTWBlocks.dragonVessel.blockID, 0, false, 1,
            "+1/7 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            RITUAL, true);

    public static final SkillNode NETHERWART_BREW_SPEED = bring(
            "netherwart_brew_speed",
            "Wart Fermentation",
            Item.netherStalkSeeds,
            -1, 1,
            "Bring 64 nether wart.",
            Item.netherStalkSeeds.itemID, 0, false, 64,
            "+10% brewing speed.", SkillRewardActions.addBrewingSpeed(0.10F),
            RITUAL, false,
            BREWING_STAND_USE);

    public static final SkillNode LITHIUM_STABILIZER_RECIPE = counter(
            "lithium_stabilizer_recipe",
            "Cauldron Stabilization",
            BTWBlocks.cauldron,
            1, 0,
            "Craft a cauldron and complete its achievement.",
            (p, w) -> AchievementHandler.hasUnlocked(p, BTWAchievements.CRAFT_CAULDRON),
            "Unlock the Lithium Stabilizer recipe.", none(),
            RITUAL, false);

    public static final SkillNode POTIONS_40_DAMAGE = bring(
            "potions_40_damage",
            "Combat Draughts",
            Item.potion,
            0, 2,
            "Bring 40 potions of any kind.",
            Item.potion.itemID, 0, false, 40,
            "+2% melee damage.", SkillRewardActions.addMeleeDamage(0.02F),
            RITUAL, false,
            POTIONS_8_XP);

    public static final SkillNode BLOOD_ORBS_128_DAMAGE = bring(
            "blood_orbs_128_damage",
            "Blood Strength",
            NMItems.bloodOrb,
            -3, -1,
            "Bring 128 blood orbs.",
            NMItems.bloodOrb.itemID, 0, false, 128,
            "+5% melee damage.", SkillRewardActions.addMeleeDamage(0.05F),
            RITUAL, false,
            NETHER_BLOOD_ORBS);

    public static final SkillNode SPIDER_EYE_LOOT = bring(
            "spider_eye_loot",
            "Arachnid Offering",
            Item.spiderEye,
            2, 0,
            "Bring 64 spider eyes.",
            Item.spiderEye.itemID, 0, false, 64,
            "+5% mob drops.", SkillRewardActions.addMobLootChance(0.05F),
            RITUAL, false);

    public static final SkillNode END_BEACON = bring(
            "end_beacon",
            "Beacon Offering",
            Block.beacon,
            -3, 0,
            "Bring 1 beacon.",
            Block.beacon.blockID, 0, false, 1,
            "Unlock End access.", SkillRewardActions.addEndAccessProgress(),
            RITUAL, true);

    public static final SkillNode GHAST_BREW_SPEED = bring(
            "ghast_brew_speed",
            "Tear Catalyst",
            Item.ghastTear,
            2, -1,
            "Bring 16 ghast tears.",
            Item.ghastTear.itemID, 0, false, 16,
            "+20% brewing speed.", SkillRewardActions.addBrewingSpeed(0.20F),
            RITUAL, false);

    public static final SkillNode POWDER_KEG_RECIPE = bring(
            "powder_keg_recipe",
            "Powder Keg",
            Item.gunpowder,
            1, -2,
            "Bring 64 gunpowder.",
            Item.gunpowder.itemID, 0, false, 64,
            "Unlock the powder-keg recipe.", none(),
            RITUAL, false);

    public static final SkillNode WITHER_SKULL_PROGRESS = bring(
            "wither_skull_progress",
            "Runed Skull Invocation",
            new ItemStack(Item.skull, 1, 1),
            -2, -2,
            "Bring 1 wither skeleton (runed) skull.",
            Item.skull.itemID, 1, true, 1,
            "+10% global mob drops and +1/5 Wither progress.", combine(SkillRewardActions.addGlobalMobLootChance(0.10F), SkillRewardActions.addWitherSummonProgress()),
            RITUAL, true);

    public static final SkillNode DIAMOND_BLOOD_ORB = bring(
            "diamond_blood_orb",
            "Blood Diamond Theory",
            NMItems.bloodOrb,
            -1, -2,
            "Bring 1 blood orb.",
            NMItems.bloodOrb.itemID, 0, false, 1,
            "+1/5 Diamond Extraction progress.", SkillRewardActions.addDiamondHarvestProgress(),
            RITUAL, false);

    public static final SkillNode WITHER_VESSELS = bring(
            "wither_vessels",
            "Twin Vessels",
            BTWBlocks.dragonVessel,
            -3, -2,
            "Bring 2 Vessels of the Dragon.",
            BTWBlocks.dragonVessel.blockID, 0, false, 2,
            "+1/5 Wither progress and +10% global XP gained.", combine(SkillRewardActions.addWitherSummonProgress(), SkillRewardActions.addGlobalXpGain(0.10F)),
            RITUAL, true,
            NETHER_DRAGON_VESSEL);

    public static final SkillNode SOUL_SAND_XP = bring(
            "soul_sand_xp",
            "Soul Accounting",
            Block.slowSand,
            0, -2,
            "Bring 512 soul sand.",
            Block.slowSand.blockID, 0, false, 512,
            "+1% experience gained.", SkillRewardActions.addXpGain(0.01F),
            RITUAL, false);


    // Combat
    public static final SkillNode WITCH_HUNTER = counter(
            "witch_hunter",
            "Witch Hunter",
            BTWItems.witchWart,
            0, 0,
            "Kill 4 witches.",
            (p, w) -> SkillHandler.getPlayerData(p).witchesKilled >= 4,
            "Crystal pockets can be mined.", SkillRewardActions.unlockCrystalMining(),
            COMBAT, false);

    public static final SkillNode NETHER_MOB_KILLS = counter(
            "nether_mob_kills",
            "Portal Slayer",
            Item.swordIron,
            1, -1,
            "Kill 250 mobs.",
            (p, w) -> SkillHandler.getPlayerData(p).mobsKilled >= 250,
            "+1/7 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            COMBAT, true);

    public static final SkillNode BLAZE_MOB_KILLS = counter(
            "blaze_mob_kills",
            "Thousand-Kill Pyrology",
            Item.blazeRod,
            2, -2,
            "Kill 1,000 mobs.",
            (p, w) -> SkillHandler.getPlayerData(p).mobsKilled >= 1000,
            "+10% blaze-rod drop chance.", SkillRewardActions.addBlazeRodDropChance(0.10F),
            COMBAT, false,
            NETHER_MOB_KILLS);

    public static final SkillNode WOOD_GRAVITY_PEARL = bring(
            "wood_gravity_pearl",
            "Ender Architecture",
            Item.enderPearl,
            -1, -1,
            "Bring 1 ender pearl.",
            Item.enderPearl.itemID, 0, false, 1,
            "+1/4 wood-gravity progress.", SkillRewardActions.addWoodGravityProgress(),
            COMBAT, true);

    public static final SkillNode ROTTEN_BLOCK_SPOILAGE = bring(
            "rotten_block_spoilage",
            "Rotten Preservation",
            BTWBlocks.rottenFleshBlock,
            -2, -2,
            "Bring 64 rotten-flesh blocks.",
            BTWBlocks.rottenFleshBlock.blockID, 0, false, 64,
            "Raw food spoils 5% slower.", SkillRewardActions.slowFoodSpoilage(),
            COMBAT, false);

    public static final SkillNode OYSTER_DIAMOND = bring(
            "oyster_diamond",
            "Oyster Abrasives",
            BTWItems.creeperOysters,
            -1, 0,
            "Bring 64 creeper oysters.",
            BTWItems.creeperOysters.itemID, 0, false, 64,
            "+5% diamond-bearing-rock chance.", SkillRewardActions.addDiamondRockDropChance(0.05F),
            COMBAT, false);

    public static final SkillNode GLAND_BREW_SPEED = bring(
            "gland_brew_speed",
            "Glandular Catalyst",
            BTWItems.mysteriousGland,
            1, 0,
            "Bring 64 mysterious glands.",
            BTWItems.mysteriousGland.itemID, 0, false, 64,
            "+15% brewing speed.", SkillRewardActions.addBrewingSpeed(0.15F),
            COMBAT, false);

    public static final SkillNode LEATHER_HANDIN = bring(
            "leather_handin",
            "Leather Armorer",
            Item.leather,
            0, 1,
            "Bring 16 leather.",
            Item.leather.itemID, 0, false, 16,
            "+1/2 leather-armor recipe progress.", SkillRewardActions.addLeatherArmorProgress(),
            COMBAT, false);

    public static final SkillNode IRON_SHOVEL_RECIPE = bring(
            "iron_shovel_recipe",
            "Shovel Pattern",
            Item.shovelIron,
            -1, 2,
            "Bring 1 iron shovel.",
            Item.shovelIron.itemID, 0, false, 1,
            "Unlock the iron-shovel recipe.", none(),
            COMBAT, false);

    public static final SkillNode IRON_SWORD_RECIPE = bring(
            "iron_sword_recipe",
            "Sword Pattern",
            Item.swordIron,
            1, 2,
            "Bring 1 iron sword.",
            Item.swordIron.itemID, 0, false, 1,
            "Unlock the iron-sword recipe.", none(),
            COMBAT, false);

    public static final SkillNode IRON_HELMET_PROGRESS = bring(
            "iron_helmet_progress",
            "Helmet Metallurgy",
            Item.helmetIron,
            -2, 1,
            "Bring 1 iron helmet.",
            Item.helmetIron.itemID, 0, false, 1,
            "+1/5 iron-ingot recipe progress.", SkillRewardActions.addIronIngotRecipeProgress(),
            COMBAT, false);

    public static final SkillNode IRON_CHEST_PROGRESS = bring(
            "iron_chest_progress",
            "Chestplate Metallurgy",
            Item.plateIron,
            -3, 2,
            "Bring 1 iron chestplate.",
            Item.plateIron.itemID, 0, false, 1,
            "+1/5 iron-ingot recipe progress.", SkillRewardActions.addIronIngotRecipeProgress(),
            COMBAT, false);

    public static final SkillNode IRON_LEGS_PROGRESS = bring(
            "iron_legs_progress",
            "Leggings Metallurgy",
            Item.legsIron,
            2, 1,
            "Bring 1 iron leggings.",
            Item.legsIron.itemID, 0, false, 1,
            "+1/5 iron-ingot recipe progress.", SkillRewardActions.addIronIngotRecipeProgress(),
            COMBAT, false);

    public static final SkillNode IRON_BOOTS_PROGRESS = bring(
            "iron_boots_progress",
            "Boot Metallurgy",
            Item.bootsIron,
            3, 2,
            "Bring 1 iron boots.",
            Item.bootsIron.itemID, 0, false, 1,
            "+1/5 iron-ingot recipe progress.", SkillRewardActions.addIronIngotRecipeProgress(),
            COMBAT, false);

    public static final SkillNode WITHER_ENDERMEN = counter(
            "wither_endermen",
            "Enderman Invocation",
            Item.enderPearl,
            0, -1,
            "Kill 50 Endermen.",
            (p, w) -> SkillHandler.getPlayerData(p).endermenKilled >= 50,
            "+1/5 Wither-summoning progress.", SkillRewardActions.addWitherSummonProgress(),
            COMBAT, true);

    public static final SkillNode SPIDER_LOOT = counter(
            "spider_loot",
            "Spider Exterminator",
            Item.spiderEye,
            -2, 0,
            "Kill 100 spiders.",
            (p, w) -> SkillHandler.getPlayerData(p).spidersKilled >= 100,
            "+2% mob drops.", SkillRewardActions.addMobLootChance(0.02F),
            COMBAT, false);

    public static final SkillNode WITCH_BREW_SPEED = counter(
            "witch_brew_speed",
            "Witch Exterminator",
            BTWItems.witchWart,
            -1, 1,
            "Kill 30 witches.",
            (p, w) -> SkillHandler.getPlayerData(p).witchesKilled >= 30,
            "+10% brewing speed.", SkillRewardActions.addBrewingSpeed(0.10F),
            COMBAT, false,
            WITCH_HUNTER);

    public static final SkillNode SLIME_SHOVEL = counter(
            "slime_shovel",
            "Slime Lubrication",
            Item.slimeBall,
            2, 0,
            "Kill 64 slimes.",
            (p, w) -> SkillHandler.getPlayerData(p).slimesKilled >= 64,
            "Shovels mine 5% faster.", SkillRewardActions.addShovelSpeed(0.05F),
            COMBAT, false);

    public static final SkillNode BONE_HEMP = bring(
            "bone_hemp",
            "Bone Seed Divination",
            Item.bone,
            1, 1,
            "Bring 128 bones.",
            Item.bone.itemID, 0, false, 128,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            COMBAT, false);

    public static final SkillNode WITHER_KILL_LOOT = counter(
            "wither_kill_loot",
            "Wither Victor",
            Item.netherStar,
            3, -1,
            "Kill the Wither.",
            (p, w) -> SkillHandler.getPlayerData(p).withersKilled >= 1,
            "+2% mob drops.", SkillRewardActions.addMobLootChance(0.02F),
            COMBAT, false);

    public static final SkillNode NETHERRACK_MINING = bring(
            "netherrack_mining",
            "Blaze-Hardened Pick",
            Item.blazeRod,
            3, -2,
            "Bring 16 blaze rods.",
            Item.blazeRod.itemID, 0, false, 16,
            "Netherrack can be mined.", SkillRewardActions.unlockNetherrackMining(),
            COMBAT, false);


    private NMSkillNodes() {
    }

    public static void initialize() {
        // Forces static initialization.
    }

    private static SkillNode bring(String id, String name, Object icon, int x, int y, String requirement,
                                   int itemId, int damage, boolean matchDamage, int count,
                                   String rewardText, SkillUnlockAction reward, SkillBranch branch,
                                   boolean worldReward, SkillNode... parents) {
        SkillNodeProvider.BuildStep step = getBuilder().id(loc(id)).name(name).icon(stack(icon)).displayLocation(x, y)
                .requirementText(requirement)
                .triggerCondition((player, world) -> SkillInventory.has(player, itemId, damage, matchDamage, count))
                .onUnlockConsume((player, world) -> SkillInventory.consume(player, itemId, damage, matchDamage, count));
        if (parents.length > 0) step.parents(parents);
        step.reward(rewardText, reward);
        if (worldReward) step.worldReward();
        return step.build().register(branch);
    }

    private static SkillNode specialBring(String id, String name, Object icon, int x, int y, String requirement,
                                          int count, String rewardText, SkillUnlockAction reward, SkillBranch branch,
                                          boolean worldReward, SkillNode... parents) {
        Item[] rareFish = rareFish();
        SkillNodeProvider.BuildStep step = getBuilder().id(loc(id)).name(name).icon(stack(icon)).displayLocation(x, y)
                .requirementText(requirement)
                .triggerCondition((player, world) -> SkillInventory.hasAny(player, count, rareFish))
                .onUnlockConsume((player, world) -> SkillInventory.consumeAny(player, count, rareFish));
        if (parents.length > 0) step.parents(parents);
        step.reward(rewardText, reward);
        if (worldReward) step.worldReward();
        return step.build().register(branch);
    }

    private static SkillNode counter(String id, String name, Object icon, int x, int y, String requirement,
                                     SkillCondition condition, String rewardText, SkillUnlockAction reward,
                                     SkillBranch branch, boolean worldReward, SkillNode... parents) {
        SkillNodeProvider.BuildStep step = getBuilder().id(loc(id)).name(name).icon(stack(icon)).displayLocation(x, y)
                .requirementText(requirement).triggerCondition(condition);
        if (parents.length > 0) step.parents(parents);
        step.reward(rewardText, reward);
        if (worldReward) step.worldReward();
        return step.build().register(branch);
    }

    private static ItemStack stack(Object icon) {
        if (icon instanceof ItemStack stack) return stack;
        if (icon instanceof Item item) return new ItemStack(item);
        if (icon instanceof Block block) return new ItemStack(block);
        throw new IllegalArgumentException("Unsupported skill icon: " + icon);
    }

    private static SkillUnlockAction combine(SkillUnlockAction... actions) {
        return (player, world) -> {
            for (SkillUnlockAction action : actions) action.apply(player, world);
        };
    }

    private static SkillUnlockAction none() {
        return (player, world) -> { };
    }

    private static Item[] rareFish() {
        return new Item[]{NMItems.swordfish, NMItems.goldenCarp, NMItems.alligatorGar,
                NMItems.arapaima, NMItems.frostfish, NMItems.duneKoi};
    }

    private static ResourceLocation loc(String path) {
        return new ResourceLocation(NMFields.modID, "skill/" + path);
    }
}
