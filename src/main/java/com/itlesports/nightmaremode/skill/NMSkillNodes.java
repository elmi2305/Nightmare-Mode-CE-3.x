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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.itlesports.nightmaremode.skill.SkillNodeProvider.getBuilder;

public final class NMSkillNodes {
    private static final List<PendingParents> PENDING_PARENTS = new ArrayList<>();

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

    public static final SkillNode CLAY_EXCAVATOR = deferred(counter(
            "clay_excavator",
            "Clay Excavator",
            Block.blockClay,
            -1, 1,
            "Mine 1,500 clay blocks.",
            (p, w) -> SkillHandler.getPlayerData(p).clayMined >= 1500,
            "Clay cooks another 12,000 ticks faster.", SkillRewardActions.addClayCookTimeReduction(12000),
            MINING, false),
            () -> NMSkillNodes.CLAY_BULK);

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

    public static final SkillNode IRON_PILE_CACHE = deferred(bring(
            "iron_pile_cache",
            "Pile Preference",
            BTWItems.ironOrePile,
            2, 0,
            "Bring 8 iron ore piles.",
            BTWItems.ironOrePile.itemID, 0, false, 8,
            "+5% iron-pile chance.", SkillRewardActions.addIronPileChance(0.05F),
            MINING, false),
            () -> NMSkillNodes.IRON_SAMPLE);

    public static final SkillNode LITHIUM_CRAFTING = bring(
            "lithium_crafting",
            "Lithium Tempering",
            NMItems.rawLithium,
            -1, 0,
            "Bring 64 raw lithium.",
            NMItems.rawLithium.itemID, 0, false, 64,
            "+5% average crafted-item durability.", SkillRewardActions.addCraftingDurability(0.05F),
            MINING, false);

    public static final SkillNode LITHIUM_DOUBLING = deferred(bring(
            "lithium_doubling",
            "Lithium Prospector",
            NMItems.refinedLithium,
            -2, 0,
            "Bring 256 refined lithium.",
            NMItems.refinedLithium.itemID, 0, false, 256,
            "Lithium ore drops twice as much raw lithium.", SkillRewardActions.doubleLithiumDrops(),
            MINING, false),
            () -> NMSkillNodes.LITHIUM_CRAFTING);

    public static final SkillNode BLACKSTONE_AUTHORITY = deferred(bring(
            "blackstone_authority",
            "Blackstone Authority",
            new ItemStack(Block.cobblestone, 1, 2),
            -1, -1,
            "Bring 64 blackstone (strata-three cobblestone).",
            Block.cobblestone.blockID, 2, true, 64,
            "Strata-three ore can be mined.", SkillRewardActions.unlockStrataThreeOre(),
            MINING, false),
            () -> NMSkillNodes.STONE_MARATHON);

    public static final SkillNode PERFECT_DIAMOND_YIELD = bring(
            "perfect_diamond_yield",
            "Perfect Diamond Yield",
            NMItems.refinedDiamondIngot,
            1, 2,
            "Bring 1 refined diamond ingot.",
            NMItems.refinedDiamondIngot.itemID, 0, false, 1,
            "Diamond ore always drops diamond-bearing rock.", SkillRewardActions.guaranteeDiamondRockDrop(),
            MINING, false);

    public static final SkillNode NICKEL_DUPLICATION = deferred(counter(
            "nickel_duplication",
            "Nickel Duplication",
            NMBlocks.nickelOre,
            -2, -2,
            "Mine 500 nickel ore.",
            (p, w) -> SkillHandler.getPlayerData(p).nickelOreMined >= 500,
            "Nickel ore gains a 5% chance to drop a second rock.", SkillRewardActions.addDoubleNickelRockChance(0.05F),
            MINING, false),
            () -> NMSkillNodes.BLACKSTONE_AUTHORITY);

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
            "+1/7 Nether access progress. Diamond tools and food can survive Nether entry.",
            SkillRewardActions.addNetherAccessProgress(),
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

    public static final SkillNode HAMMER_PRESERVATION = deferred(counter(
            "hammer_preservation",
            "Hammer Preservation",
            Block.cobblestone,
            1, -2,
            "Mine 3,000 strata-one cobblestone.",
            (p, w) -> SkillHandler.getPlayerData(p).strataOneCobblestoneMined >= 3000,
            "10% chance not to consume hammer durability.", SkillRewardActions.addHammerDurabilitySaveChance(0.10F),
            MINING, false),
            () -> NMSkillNodes.STONE_MARATHON);

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

    public static final SkillNode LITHIUM_CISTERN = deferred(bring(
            "lithium_cistern",
            "Lithium Brine Control",
            NMItems.lithiumSalt,
            3, -2,
            "Bring 16 lithium salt.",
            NMItems.lithiumSalt.itemID, 0, false, 16,
            "+10% cistern processing speed.", SkillRewardActions.addCisternSpeed(0.10F),
            MINING, false),
            () -> NMSkillNodes.FAILED_REFINEMENT);

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

    public static final SkillNode CLAY_SHOVEL = deferred(bring(
            "clay_shovel",
            "Clay Familiarity",
            Item.clay,
            0, -3,
            "Bring 64 clay.",
            Item.clay.itemID, 0, false, 64,
            "Shovels mine 5% faster.", SkillRewardActions.addShovelSpeed(0.05F),
            MINING, false),
            () -> NMSkillNodes.GRAVEL_SHOVEL);

    public static final SkillNode NICKEL_BLAZE = bring(
            "nickel_blaze",
            "Nickel Pyrology",
            NMItems.rawNickelRock,
            -3, -3,
            "Bring 64 nickel-bearing rock.",
            NMItems.rawNickelRock.itemID, 0, false, 64,
            "+5% blaze-rod drop chance.", SkillRewardActions.addBlazeRodDropChance(0.05F),
            MINING, false);

    public static final SkillNode DENSE_CORE_METALLURGY = deferred(bring(
            "dense_core_metallurgy",
            "Dense-Core Metallurgy",
            NMItems.denseNetherrackCore,
            -4, -2,
            "Bring 16 dense netherrack cores.",
            NMItems.denseNetherrackCore.itemID, 0, false, 16,
            "Unlock dense-core machinery and steel-nugget consolidation.", none(),
            MINING, false),
            () -> NMSkillNodes.NICKEL_HEAT_RECIPE);

    public static final SkillNode FLINT_CHIP_NOTES = bring(
            "flint_chip_notes",
            "Flint-Chip Notes",
            NMItems.flintChip,
            0, 4,
            "Bring 1 flint chip.",
            NMItems.flintChip.itemID, 0, false, 1,
            "Unlock the crude torch recipe.", none(),
            MINING, false);

    public static final SkillNode JUMP_CUT_SLABS = counter(
            "jump_cut_slabs",
            "Repeated Compression",
            BTWBlocks.dirtSlab,
            0, -4,
            "Jump 1,000 times.",
            (p, w) -> SkillHandler.getPlayerData(p).jumps >= 1000,
            "+1 to unlock the dirt-, sand-, and gravel-slab recipes.", none(),
            MINING, false);

    public static final SkillNode DIAMOND_TOOLMAKING = deferred(bring(
            "diamond_toolmaking",
            "Diamond Toolmaking",
            BTWItems.diamondIngot,
            4, 0,
            "Bring 2 diamond ingots.",
            BTWItems.diamondIngot.itemID, 0, false, 2,
            "Unlock precision diamond tools and armor plates.", none(),
            MINING, false),
            () -> NMSkillNodes.DIAMOND_CRYSTALS,
            () -> NMSkillNodes.DIAMOND_PRECISION_GEAR,
            () -> NMSkillNodes.NICKEL_HEAT_RECIPE);

    public static final SkillNode THERMAL_ENGINEERING = deferred(bring(
            "thermal_engineering",
            "Thermal Engineering",
            NMItems.nickelHeatComponent,
            -4, -3,
            "Bring 2 heat-resistant nickel components.",
            NMItems.nickelHeatComponent.itemID, 0, false, 2,
            "Unlock high-temperature machinery.", none(),
            MINING, false),
            () -> NMSkillNodes.NICKEL_HEAT_RECIPE,
            () -> NMSkillNodes.LITHIUM_STABILIZER_RECIPE);

    public static final SkillNode DEADZONE_FOUNDRY = deferred(bring(
            "deadzone_foundry",
            "Deadzone Foundry",
            NMItems.deadzoneShard,
            -5, -3,
            "Bring 16 deadzone shards.",
            NMItems.deadzoneShard.itemID, 0, false, 16,
            "Unlock deadzone-reinforced late metallurgy.", none(),
            MINING, false),
            () -> NMSkillNodes.DENSE_CORE_METALLURGY,
            () -> NMSkillNodes.THERMAL_ENGINEERING);

    public static final SkillNode ROAD_ENGINEERING = deferred(bring(
            "road_engineering",
            "Road Engineering",
            NMBlocks.blockRoad,
            -4, -4,
            "Bring 64 road blocks.",
            NMBlocks.blockRoad.blockID, 0, false, 64,
            "Unlock heat-treated asphalt.", none(),
            MINING, false),
            () -> NMSkillNodes.THERMAL_ENGINEERING,
            () -> NMSkillNodes.PRECISION_MECHANICS);

    public static final SkillNode SOULFORGED_ARMORY = deferred(bring(
            "soulforged_armory",
            "Soulforged Armory",
            BTWItems.soulforgedSteelIngot,
            -6, -2,
            "Bring 8 soulforged steel ingots.",
            BTWItems.soulforgedSteelIngot.itemID, 0, false, 8,
            "Unlock reinforced steel armor plates and equipment patterns.", none(),
            MINING, false),
            () -> NMSkillNodes.SOULFORGE_ENGINEERING,
            () -> NMSkillNodes.DENSE_CORE_METALLURGY);

    public static final SkillNode STEEL_LOGISTICS = deferred(bring(
            "steel_logistics",
            "Steel Logistics",
            NMItems.steelBunch,
            -6, -3,
            "Bring 8 steel bunches.",
            NMItems.steelBunch.itemID, 0, false, 8,
            "Unlock the steel locker.", none(),
            MINING, false),
            () -> NMSkillNodes.SOULFORGED_ARMORY,
            () -> NMSkillNodes.BLOOD_STORAGE,
            () -> NMSkillNodes.DEADZONE_FOUNDRY);

    public static final SkillNode LOOSE_STONES_2 = deferred(bring(
            "loose_stones_2",
            "Loose Stone Sampling",
            BTWItems.stone,
            1, -1,
            "Bring 2 loose stones.",
            BTWItems.stone.itemID, 0, true, 2,
            "Unlock the sharp stone recipe.", none(),
            MINING, false),
            () -> NMSkillNodes.STICK_PRIMITIVES);

    public static final SkillNode LOOSE_STONES_64 = deferred(bring(
            "loose_stones_64",
            "Cobble Consolidation",
            BTWItems.stone,
            -1, -2,
            "Bring 64 loose stones.",
            BTWItems.stone.itemID, 0, true, 64,
            "Unlock regular cobblestone block and slab recipes.", none(),
            MINING, false),
            () -> NMSkillNodes.LOOSE_STONES_2);

    public static final SkillNode MID_STRATA_STONES_128 = deferred(bring(
            "mid_strata_stones_128",
            "Mid-Strata Consolidation",
            new ItemStack(BTWItems.stone, 1, 1),
            -2, -1,
            "Bring 128 loose stones from strata two.",
            BTWItems.stone.itemID, 1, true, 128,
            "Unlock strata-two cobblestone block and slab recipes.", none(),
            MINING, false),
            () -> NMSkillNodes.LOOSE_STONES_64);

    public static final SkillNode DEEP_STRATA_STONES_256 = deferred(bring(
            "deep_strata_stones_256",
            "Deep-Strata Consolidation",
            new ItemStack(BTWItems.stone, 1, 2),
            -1, 2,
            "Bring 256 loose stones from strata three.",
            BTWItems.stone.itemID, 2, true, 256,
            "Unlock strata-three cobblestone block and slab recipes.", none(),
            MINING, false),
            () -> NMSkillNodes.MID_STRATA_STONES_128);

    public static final SkillNode MAIL_16 = bring(
            "mail_16",
            "Mail Assembly",
            BTWItems.mail,
            2, -2,
            "Bring 16 mail.",
            BTWItems.mail.itemID, 0, false, 16,
            "Unlock chain armor recipes.", none(),
            MINING, false);

    public static final SkillNode CLAY_PILES_16 = bring(
            "clay_piles_16",
            "Clay Consolidation",
            BTWItems.clayPile,
            -2, 2,
            "Bring 16 clay piles.",
            BTWItems.clayPile.itemID, 0, false, 16,
            "Unlock the clay ball consolidation recipe.", none(),
            MINING, false);

    public static final SkillNode CLAY_BALLS_32 = deferred(bring(
            "clay_balls_32",
            "Potter's Feedstock",
            Item.clay,
            -3, 0,
            "Bring 32 clay balls.",
            Item.clay.itemID, 0, false, 32,
            "Unlock turntable pottery recipes.", none(),
            MINING, false),
            () -> NMSkillNodes.CLAY_PILES_16);

    public static final SkillNode UNFIRED_CRUDE_BRICKS_16 = bring(
            "unfired_crude_bricks_16",
            "Crude Kiln Load",
            BTWItems.unfiredCrudeBrick,
            3, 0,
            "Bring 16 unfired crude bricks.",
            BTWItems.unfiredCrudeBrick.itemID, 0, false, 16,
            "Unlock crude brick kiln recipes.", none(),
            MINING, false);

    public static final SkillNode BRICKS_32 = bring(
            "bricks_32",
            "Brick Architecture",
            Item.brick,
            0, 3,
            "Bring 32 bricks.",
            Item.brick.itemID, 0, false, 32,
            "Unlock brick block, slab, siding, moulding, corner, and stair recipes.", none(),
            MINING, false);

    public static final SkillNode STONE_BRICKS_32 = bring(
            "stone_bricks_32",
            "Stone Architecture",
            BTWItems.stoneBrick,
            1, -3,
            "Bring 32 stone brick items.",
            BTWItems.stoneBrick.itemID, 0, false, 32,
            "Unlock stone brick decorative recipes.", none(),
            MINING, false);

    public static final SkillNode SNOWBALLS_32 = bring(
            "snowballs_32",
            "Snow Packing",
            Item.snowball,
            -3, 1,
            "Bring 32 snowballs.",
            Item.snowball.itemID, 0, false, 32,
            "Unlock the snow block piston packing recipe.", none(),
            MINING, false);

    public static final SkillNode DIRT_PILES_32 = bring(
            "dirt_piles_32",
            "Earth Packing",
            BTWItems.dirtPile,
            3, 1,
            "Bring 32 dirt piles.",
            BTWItems.dirtPile.itemID, 0, false, 32,
            "Unlock dirt piston packing recipes.", none(),
            MINING, false);

    public static final SkillNode SAND_PILES_32 = bring(
            "sand_piles_32",
            "Sand Packing",
            BTWItems.sandPile,
            -1, 3,
            "Bring 32 sand piles.",
            BTWItems.sandPile.itemID, 0, false, 32,
            "Unlock sand piston packing recipes.", none(),
            MINING, false);

    public static final SkillNode GRAVEL_PILES_32 = bring(
            "gravel_piles_32",
            "Gravel Packing",
            BTWItems.gravelPile,
            -2, -3,
            "Bring 32 gravel piles.",
            BTWItems.gravelPile.itemID, 0, false, 32,
            "Unlock gravel and flint piston packing recipes.", none(),
            MINING, false);

    public static final SkillNode IRON_ORE_PILES_32 = bring(
            "iron_ore_piles_32",
            "Iron Ore Packing",
            BTWItems.ironOrePile,
            2, -3,
            "Bring 32 iron ore piles.",
            BTWItems.ironOrePile.itemID, 0, false, 32,
            "Unlock the iron ore chunk piston packing recipe.", none(),
            MINING, false);

    public static final SkillNode EMERALDS_16 = bring(
            "emeralds_16",
            "Emerald Reclamation",
            Item.emerald,
            3, 2,
            "Bring 16 emeralds.",
            Item.emerald.itemID, 0, false, 16,
            "Unlock the emerald pile crucible recovery recipe.", none(),
            MINING, false);

    public static final SkillNode DIAMONDS_16 = bring(
            "diamonds_16",
            "Diamond Reclamation",
            Item.diamond,
            -2, 3,
            "Bring 16 diamonds.",
            Item.diamond.itemID, 0, false, 16,
            "Unlock the diamond pile crucible recovery recipe.", none(),
            MINING, false);

    public static final SkillNode IRON_INGOTS_16 = bring(
            "iron_ingots_16",
            "Iron Toolmaking",
            Item.ingotIron,
            3, -3,
            "Bring 16 iron ingots.",
            Item.ingotIron.itemID, 0, false, 16,
            "Unlock anvil, anchor, and iron tool recipes.", none(),
            MINING, false);

    public static final SkillNode DIAMOND_INGOTS_8 = bring(
            "diamond_ingots_8",
            "Diamond Industry",
            BTWItems.diamondIngot,
            -3, 3,
            "Bring 8 diamond ingots.",
            BTWItems.diamondIngot.itemID, 0, false, 8,
            "Unlock diamond tool, diamond armor plate, and diamond ingot block recipes.", none(),
            MINING, false);

    public static final SkillNode STEEL_NUGGETS_32 = bring(
            "steel_nuggets_32",
            "Steel Consolidation",
            BTWItems.steelNugget,
            3, 3,
            "Bring 32 steel nuggets.",
            BTWItems.steelNugget.itemID, 0, false, 32,
            "Unlock the soulforged steel ingot crucible recipe.", none(),
            MINING, false);

    public static final SkillNode SOULFORGED_INGOTS_16 = bring(
            "soulforged_ingots_16",
            "Soulforged Toolmaking",
            BTWItems.soulforgedSteelIngot,
            -4, 0,
            "Bring 16 soulforged steel ingots.",
            BTWItems.soulforgedSteelIngot.itemID, 0, false, 16,
            "Unlock steel tool recipes.", none(),
            MINING, false);

    public static final SkillNode STEEL_ARMOR_PLATES_16 = deferred(bring(
            "steel_armor_plates_16",
            "Plate Armoring",
            BTWItems.steelArmorPlate,
            -1, -4,
            "Bring 16 steel armor plates.",
            BTWItems.steelArmorPlate.itemID, 0, false, 16,
            "Unlock plate armor recipes.", none(),
            MINING, false),
            () -> NMSkillNodes.SOULFORGED_INGOTS_16);

    public static final SkillNode KILN_IRON_128 = deferred(counter(
            "kiln_iron_128",
            "Kiln Ironmaster",
            BTWItems.ironNugget,
            1, -4,
            "Smelt 128 iron nuggets in a kiln.",
            (p, w) -> SkillHandler.getPlayerData(p).ironNuggetsKilned >= 128,
            "Unlock the anvil recipe.", none(),
            MINING, false),
            () -> NMSkillNodes.IRON_INGOTS_16);

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

    public static final SkillNode DRIED_FIBER_HEMP = deferred(bring(
            "dried_fiber_hemp",
            "Fiber Seed Lore",
            NMItems.driedPlantFiber,
            -1, 1,
            "Bring 64 dried plant fibers.",
            NMItems.driedPlantFiber.itemID, 0, false, 64,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false),
            () -> NMSkillNodes.GRASS_HARVEST);

    public static final SkillNode SAPLING_PLANTER = counter(
            "sapling_planter",
            "Forest Planter",
            Block.sapling,
            0, -1,
            "Plant 100 saplings.",
            (p, w) -> SkillHandler.getPlayerData(p).saplingsPlanted >= 100,
            "+5% twig drop chance.", SkillRewardActions.addTwigDropChance(0.05F),
            HUSBANDRY, false);

    public static final SkillNode GRASS_BLOCK_HEMP = deferred(bring(
            "grass_block_hemp",
            "Sod Examination",
            Block.grass,
            1, 1,
            "Bring 1 grass block.",
            Block.grass.blockID, 0, false, 1,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false),
            () -> NMSkillNodes.GRASS_HARVEST);

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

    public static final SkillNode DIRT_FIBER = deferred(counter(
            "dirt_fiber",
            "Soil Fiber Mastery",
            Block.dirt,
            2, 0,
            "Break 2,000 dirt.",
            (p, w) -> SkillHandler.getPlayerData(p).dirtMined >= 2000,
            "Tall grass always drops plant fiber.", SkillRewardActions.alwaysDropPlantFiberFromTallGrass(),
            HUSBANDRY, false),
            () -> NMSkillNodes.DIRT_SHOVEL);

    public static final SkillNode LOG_TWIGS = deferred(bring(
            "log_twigs",
            "Logged Branches",
            Block.wood,
            -1, -2,
            "Bring 64 logs.",
            Block.wood.blockID, 0, false, 64,
            "+5% twig drop chance.", SkillRewardActions.addTwigDropChance(0.05F),
            HUSBANDRY, false),
            () -> NMSkillNodes.SAPLING_PLANTER);

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

    public static final SkillNode RARE_FISH_TROPHIES = deferred(specialBring(
            "rare_fish_trophies",
            "Trophy Angler",
            NMItems.swordfish,
            2, -2,
            "Bring 32 rare fish.",
            32,
            "+5% rare-fish chance.", SkillRewardActions.addRareFishChance(0.05F),
            HUSBANDRY, false),
            () -> NMSkillNodes.RARE_FISHING);

    public static final SkillNode OXYGEN_MASK_RECIPE = bring(
            "oxygen_mask_recipe",
            "Fiber Filtration",
            NMItems.driedPlantFiber,
            -3, 2,
            "Bring 300 dried plant fiber.",
            NMItems.driedPlantFiber.itemID, 0, false, 300,
            "Unlock the Oxygen Mask recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode LEAF_TWIGS = deferred(counter(
            "leaf_twigs",
            "Leaf Sifter",
            Block.leaves,
            0, -2,
            "Break 500 leaves.",
            (p, w) -> SkillHandler.getPlayerData(p).leavesMined >= 500,
            "+5% twig drop chance.", SkillRewardActions.addTwigDropChance(0.05F),
            HUSBANDRY, false),
            () -> NMSkillNodes.SAPLING_PLANTER);

    public static final SkillNode MELON_DAMAGE = bring(
            "melon_damage",
            "Melon Musculature",
            Block.melon,
            3, 2,
            "Bring 64 melon blocks.",
            Block.melon.blockID, 0, false, 64,
            "+1% melee damage.", SkillRewardActions.addMeleeDamage(0.01F),
            HUSBANDRY, false);

    public static final SkillNode MATURE_CROP_HEMP = deferred(counter(
            "mature_crop_hemp",
            "Mature Harvest",
            Block.crops,
            0, 2,
            "Harvest 500 fully-grown crops.",
            (p, w) -> SkillHandler.getPlayerData(p).fullyGrownCropsHarvested >= 500,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false),
            () -> NMSkillNodes.CROP_HEMP);

    public static final SkillNode CURED_PRESERVATION = deferred(bring(
            "cured_preservation",
            "Cured Example",
            BTWItems.curedMeat,
            -1, -3,
            "Bring 16 cured meat.",
            BTWItems.curedMeat.itemID, 0, false, 16,
            "Raw food spoils 5% slower.", SkillRewardActions.slowFoodSpoilage(),
            HUSBANDRY, false),
            () -> NMSkillNodes.COOKED_PRESERVATION);

    public static final SkillNode BONEMEAL_FIBER = bring(
            "bonemeal_fiber",
            "Bonemeal Fiber",
            new ItemStack(Item.dyePowder, 1, 15),
            1, 3,
            "Bring 256 bonemeal.",
            Item.dyePowder.itemID, 15, true, 256,
            "+10% tall-grass plant-fiber chance.", SkillRewardActions.addTallGrassPlantFiberChance(0.10F),
            HUSBANDRY, false);

    public static final SkillNode NETHER_WART_FARMING = deferred(counter(
            "nether_wart_farming",
            "Weed Sovereignty",
            Item.netherStalkSeeds,
            -3, -1,
            "Remove weeds 1,000 times.",
            (p, w) -> SkillHandler.getPlayerData(p).weedsRemoved >= 1000,
            "Nether wart can be farmed.", SkillRewardActions.unlockNetherWartFarming(),
            HUSBANDRY, false),
            () -> NMSkillNodes.XP_CAP_REMOVAL);

    public static final SkillNode FOUL_PRESERVATION = deferred(bring(
            "foul_preservation",
            "Spoilage Autopsy",
            BTWItems.foulFood,
            -2, -3,
            "Bring 256 foul food.",
            BTWItems.foulFood.itemID, 0, false, 256,
            "Raw food spoils 5% slower.", SkillRewardActions.slowFoodSpoilage(),
            HUSBANDRY, false),
            () -> NMSkillNodes.CURED_PRESERVATION);

    public static final SkillNode DANDELION_NOTES_I = bring(
            "dandelion_notes_i",
            "Dandelion Notes I",
            Block.plantYellow,
            -4, 0,
            "Bring 16 dandelions.",
            Block.plantYellow.blockID, 0, false, 16,
            "+1 to unlock yellow-dye milling.", none(),
            HUSBANDRY, false);

    public static final SkillNode DANDELION_NOTES_II = deferred(bring(
            "dandelion_notes_ii",
            "Dandelion Notes II",
            Block.plantYellow,
            -5, 1,
            "Bring another 16 dandelions.",
            Block.plantYellow.blockID, 0, false, 16,
            "+1 to unlock yellow-dye milling.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.DANDELION_NOTES_I);

    public static final SkillNode SUGAR_CANE_NOTES = bring(
            "sugar_cane_notes",
            "Sugar-Cane Notes",
            Item.reed,
            2, 3,
            "Bring 1 sugar cane.",
            Item.reed.itemID, 0, false, 1,
            "Unlock the paper recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode POPPY_NOTES = bring(
            "poppy_notes",
            "Poppy Notes",
            Block.plantRed,
            -2, 3,
            "Bring 16 poppies.",
            Block.plantRed.blockID, 0, false, 16,
            "Unlock red-dye milling.", none(),
            HUSBANDRY, false);

    public static final SkillNode SPIDER_SILK_STRING = bring(
            "spider_silk_string",
            "Spider-Silk Twisting",
            NMItems.spiderSilk,
            3, -2,
            "Bring 2 spider silk.",
            NMItems.spiderSilk.itemID, 0, false, 2,
            "+1 to unlock the string recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode PORK_OVEN_PATTERN = bring(
            "pork_oven_pattern",
            "Pork Roasting",
            Item.porkRaw,
            4, 1,
            "Bring 16 raw porkchops.",
            Item.porkRaw.itemID, 0, false, 16,
            "+1 to the brick-oven recipe unlock.", none(),
            HUSBANDRY, false);

    public static final SkillNode MUSHROOM_HOTBAR = bring(
            "mushroom_hotbar",
            "Mushroom Foraging",
            Block.mushroomRed,
            4, 0,
            "Bring 32 red mushrooms.",
            Block.mushroomRed.blockID, 0, false, 32,
            "+1 hotbar slot.", SkillRewardActions.addHotbarSlots(1),
            HUSBANDRY, false);

    public static final SkillNode SAWDUST_CAMPFIRE = bring(
            "sawdust_campfire",
            "Dry Tinder",
            BTWItems.sawDust,
            -3, -2,
            "Bring 16 sawdust.",
            BTWItems.sawDust.itemID, 0, false, 16,
            "Unlock the campfire recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BIOME_NETHER_PROGRESS = deferred(counter(
            "biome_nether_progress",
            "Wide-Ranging Survey",
            Item.map,
            4, 2,
            "Visit 10 unique biomes.",
            (p, w) -> SkillHandler.getPlayerData(p).getVisitedBiomeCount() >= 10,
            "+1/7 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            HUSBANDRY, true),
            () -> NMSkillNodes.BIOME_FIELD_NOTES);

    public static final SkillNode BARK_64 = bring(
            "bark_64",
            "Bark Bundling",
            BTWItems.bark,
            -1, 0,
            "Bring 64 bark.",
            BTWItems.bark.itemID, 0, false, 64,
            "Unlock bark storage recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BONE_CARVINGS_16 = bring(
            "bone_carvings_16",
            "Hook Carving",
            BTWItems.boneCarving,
            1, -1,
            "Bring 16 bone carvings.",
            BTWItems.boneCarving.itemID, 0, false, 16,
            "Unlock the bone fish hook recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BONE_HOOKS_8 = deferred(bring(
            "bone_hooks_8",
            "Primitive Angling",
            BTWItems.boneFishHook,
            1, -2,
            "Bring 8 bone fish hooks.",
            BTWItems.boneFishHook.itemID, 0, false, 8,
            "Unlock the fishing rod recipe.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BONE_CARVINGS_16);

    public static final SkillNode BAT_WINGS_16 = deferred(bring(
            "bat_wings_16",
            "Nocturnal Baiting",
            BTWItems.batWing,
            2, 1,
            "Bring 16 bat wings.",
            BTWItems.batWing.itemID, 0, false, 16,
            "Unlock the baited fishing rod recipe.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BONE_HOOKS_8);

    public static final SkillNode HEMP_FIBERS_32 = bring(
            "hemp_fibers_32",
            "Ropework",
            BTWItems.hempFibers,
            -2, -2,
            "Bring 32 hemp fibers.",
            BTWItems.hempFibers.itemID, 0, false, 32,
            "Unlock the rope recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode HEMP_32 = bring(
            "hemp_32",
            "Hemp Textile Stockpile",
            BTWItems.hemp,
            0, -3,
            "Bring 32 hemp.",
            BTWItems.hemp.itemID, 0, false, 32,
            "Unlock the fabric recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode LEATHER_STRAPS_16 = bring(
            "leather_straps_16",
            "Strapped Joinery",
            BTWItems.leatherStrap,
            -3, 0,
            "Bring 16 leather straps.",
            BTWItems.leatherStrap.itemID, 0, false, 16,
            "Unlock belt, haft, and breeding harness recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode WOOL_16_NEEDLES = bring(
            "wool_16_needles",
            "Knitting Materials",
            BTWItems.wool,
            3, 0,
            "Bring 16 wool.",
            BTWItems.wool.itemID, 0, false, 16,
            "Unlock the knitting needles recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode KNITTING_NEEDLES_4 = deferred(bring(
            "knitting_needles_4",
            "Needlework Practice",
            BTWItems.knittingNeedles,
            0, 3,
            "Bring 4 knitting needles.",
            BTWItems.knittingNeedles.itemID, 0, false, 4,
            "Unlock wool knit recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.WOOL_16_NEEDLES);

    public static final SkillNode WOOL_KNIT_16 = deferred(bring(
            "wool_knit_16",
            "Knitted Protection",
            BTWItems.woolKnit,
            1, -3,
            "Bring 16 wool knit.",
            BTWItems.woolKnit.itemID, 0, false, 16,
            "Unlock wool armor recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.KNITTING_NEEDLES_4);

    public static final SkillNode PADDING_16 = deferred(bring(
            "padding_16",
            "Padded Protection",
            BTWItems.padding,
            -1, 3,
            "Bring 16 padding.",
            BTWItems.padding.itemID, 0, false, 16,
            "Unlock padded armor recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.FABRIC_16);

    public static final SkillNode TANNED_LEATHER_16 = bring(
            "tanned_leather_16",
            "Tanned Armoring",
            BTWItems.tannedLeather,
            2, -3,
            "Bring 16 tanned leather.",
            BTWItems.tannedLeather.itemID, 0, false, 16,
            "Unlock tanned leather armor recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode SUGAR_CANE_16_WICKER = bring(
            "sugar_cane_16_wicker",
            "Wicker Supply",
            Item.reed,
            -3, -3,
            "Bring 16 sugar cane.",
            Item.reed.itemID, 0, false, 16,
            "Unlock wicker pane loom recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode WICKER_PANES_16 = deferred(bring(
            "wicker_panes_16",
            "Wicker Architecture",
            BTWItems.wickerPane,
            3, -3,
            "Bring 16 wicker panes.",
            BTWItems.wickerPane.itemID, 0, false, 16,
            "Unlock wicker block, slab, and pane recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.SUGAR_CANE_16_WICKER);

    public static final SkillNode STRAW_32 = bring(
            "straw_32",
            "Thatching Stockpile",
            BTWItems.straw,
            -3, 3,
            "Bring 32 straw.",
            BTWItems.straw.itemID, 0, false, 32,
            "Unlock thatch recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode POTASH_16 = bring(
            "potash_16",
            "Alkaline Chemistry",
            BTWItems.potash,
            3, 3,
            "Bring 16 potash.",
            BTWItems.potash.itemID, 0, false, 16,
            "Unlock soap and nether sludge recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode DUNG_16 = bring(
            "dung_16",
            "Tanning Reagent",
            BTWItems.dung,
            0, -4,
            "Bring 16 dung.",
            BTWItems.dung.itemID, 0, false, 16,
            "Unlock tanned leather and golden dung recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode SCOURED_LEATHER_16 = bring(
            "scoured_leather_16",
            "Whole-Hide Tanning",
            BTWItems.scouredLeather,
            0, 4,
            "Bring 16 scoured leather.",
            BTWItems.scouredLeather.itemID, 0, false, 16,
            "Unlock the tanned leather recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode CUT_SCOURED_LEATHER_16 = bring(
            "cut_scoured_leather_16",
            "Cut-Hide Tanning",
            BTWItems.cutScouredLeather,
            -1, -4,
            "Bring 16 cut scoured leather.",
            BTWItems.cutScouredLeather.itemID, 0, false, 16,
            "Unlock the cut tanned leather recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode TALLOW_16 = bring(
            "tallow_16",
            "Chandler's Reserve",
            BTWItems.tallow,
            1, -4,
            "Bring 16 tallow.",
            BTWItems.tallow.itemID, 0, false, 16,
            "Unlock the candle recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode FLOUR_32 = bring(
            "flour_32",
            "Milled Flour Reserve",
            BTWItems.flour,
            -4, -1,
            "Bring 32 flour.",
            BTWItems.flour.itemID, 0, false, 32,
            "Unlock bread dough and donut recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BREAD_DOUGH_16 = deferred(bring(
            "bread_dough_16",
            "Baker's Batch",
            BTWItems.breadDough,
            4, -1,
            "Bring 16 bread dough.",
            BTWItems.breadDough.itemID, 0, false, 16,
            "Unlock kiln-baked bread recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.FLOUR_32);

    public static final SkillNode RAW_EGGS_16 = bring(
            "raw_eggs_16",
            "Egg Cookery",
            BTWItems.rawEgg,
            -4, 1,
            "Bring 16 raw eggs.",
            BTWItems.rawEgg.itemID, 0, false, 16,
            "Unlock hard-boiled egg, omelet, scrambled egg, pumpkin pie, and cake recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode COCOA_BEANS_16 = bring(
            "cocoa_beans_16",
            "Chocolate Cookery",
            BTWItems.cocoaBeans,
            -1, 4,
            "Bring 16 cocoa beans.",
            BTWItems.cocoaBeans.itemID, 0, false, 16,
            "Unlock the chocolate recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode CHOCOLATE_16 = deferred(bring(
            "chocolate_16",
            "Cookie Dough Cookery",
            BTWItems.chocolate,
            1, 4,
            "Bring 16 chocolate.",
            BTWItems.chocolate.itemID, 0, false, 16,
            "Unlock the unbaked cookie recipe.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.COCOA_BEANS_16);

    public static final SkillNode PUMPKINS_16 = bring(
            "pumpkins_16",
            "Pumpkin Cookery",
            BTWBlocks.freshPumpkin,
            -2, -4,
            "Bring 16 fresh pumpkins.",
            BTWBlocks.freshPumpkin.blockID, 0, false, 16,
            "Unlock carved pumpkin and unbaked pumpkin pie recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BROWN_MUSHROOMS_32 = bring(
            "brown_mushrooms_32",
            "Mushroom Cookery",
            BTWItems.brownMushroom,
            2, -4,
            "Bring 32 brown mushrooms.",
            BTWItems.brownMushroom.itemID, 0, false, 32,
            "Unlock kebab, omelet, mushroom stew, and hearty stew recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode RAW_MUTTON_16 = deferred(bring(
            "raw_mutton_16",
            "Kebab Butchery",
            BTWItems.rawMutton,
            -4, -2,
            "Bring 16 raw mutton.",
            BTWItems.rawMutton.itemID, 0, false, 16,
            "Unlock the raw kebab recipe.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BROWN_MUSHROOMS_32);

    public static final SkillNode TANNED_ARMOR_SET = deferred(armorSet(
            "tanned_armor_set",
            "Complete Tanned Harness",
            BTWItems.tannedLeatherChest,
            4, -2,
            "Bring a full set of tanned leather armor.",
            "Unlock gimp armor recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.TANNED_LEATHER_16);

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

    public static final SkillNode POTIONS_8_XP = deferred(bring(
            "potions_8_xp",
            "Tasted Experience",
            Item.potion,
            0, 1,
            "Bring 8 potions of any kind.",
            Item.potion.itemID, 0, false, 8,
            "+10% experience gained.", SkillRewardActions.addXpGain(0.10F),
            RITUAL, false),
            () -> NMSkillNodes.BREWING_STAND_USE);

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

    public static final SkillNode NETHERWART_BREW_SPEED = deferred(bring(
            "netherwart_brew_speed",
            "Wart Fermentation",
            Item.netherStalkSeeds,
            -1, 1,
            "Bring 64 nether wart.",
            Item.netherStalkSeeds.itemID, 0, false, 64,
            "+10% brewing speed.", SkillRewardActions.addBrewingSpeed(0.10F),
            RITUAL, false),
            () -> NMSkillNodes.BREWING_STAND_USE);

    public static final SkillNode LITHIUM_STABILIZER_RECIPE = counter(
            "lithium_stabilizer_recipe",
            "Cauldron Stabilization",
            BTWBlocks.cauldron,
            1, 0,
            "Craft a cauldron and complete its achievement.",
            (p, w) -> AchievementHandler.hasUnlocked(p, BTWAchievements.CRAFT_CAULDRON),
            "Unlock the Lithium Stabilizer recipe.", none(),
            RITUAL, false);

    public static final SkillNode POTIONS_40_DAMAGE = deferred(bring(
            "potions_40_damage",
            "Combat Draughts",
            Item.potion,
            0, 2,
            "Bring 40 potions of any kind.",
            Item.potion.itemID, 0, false, 40,
            "+2% melee damage.", SkillRewardActions.addMeleeDamage(0.02F),
            RITUAL, false),
            () -> NMSkillNodes.POTIONS_8_XP);

    public static final SkillNode BLOOD_ORBS_128_DAMAGE = deferred(bring(
            "blood_orbs_128_damage",
            "Blood Strength",
            NMItems.bloodOrb,
            -3, -1,
            "Bring 128 blood orbs.",
            NMItems.bloodOrb.itemID, 0, false, 128,
            "+5% melee damage.", SkillRewardActions.addMeleeDamage(0.05F),
            RITUAL, false),
            () -> NMSkillNodes.NETHER_BLOOD_ORBS);

    public static final SkillNode BLOOD_ARMORY = deferred(bring(
            "blood_armory",
            "Blood Armory",
            NMItems.bloodIngot,
            -4, -2,
            "Bring 16 blood ingots.",
            NMItems.bloodIngot.itemID, 0, false, 16,
            "Unlock blood armor and weapon patterns.", none(),
            RITUAL, false),
            () -> NMSkillNodes.NETHER_BLOOD_ORBS);

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
            "end_accord",
            "End Accord",
            NMItems.endAccord,
            -3, 0,
            "Bring the accord assembled from all four Tier 3 commissions.",
            NMItems.endAccord.itemID, 0, false, 1,
            "Unlock End access.", SkillRewardActions.addEndAccessProgress(),
            RITUAL, true);

    public static final SkillNode NETHER_INVOCATION_SEAL = bring(
            "nether_invocation_seal",
            "Nether Invocation",
            NMItems.invocationSeal,
            -4, -1,
            "Bring the seal assembled from all four Tier 2 commissions.",
            NMItems.invocationSeal.itemID, 0, false, 1,
            "+1/5 Wither-summoning progress.", SkillRewardActions.addWitherSummonProgress(),
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

    public static final SkillNode WITHER_VESSELS = deferred(bring(
            "wither_vessels",
            "Twin Vessels",
            BTWBlocks.dragonVessel,
            -3, -2,
            "Bring 2 Vessels of the Dragon.",
            BTWBlocks.dragonVessel.blockID, 0, false, 2,
            "+1/5 Wither progress and +10% global XP gained.", combine(SkillRewardActions.addWitherSummonProgress(), SkillRewardActions.addGlobalXpGain(0.10F)),
            RITUAL, true),
            () -> NMSkillNodes.NETHER_DRAGON_VESSEL);

    public static final SkillNode SOUL_SAND_XP = bring(
            "soul_sand_xp",
            "Soul Accounting",
            Block.slowSand,
            0, -2,
            "Bring 512 soul sand.",
            Block.slowSand.blockID, 0, false, 512,
            "+1% experience gained.", SkillRewardActions.addXpGain(0.01F),
            RITUAL, false);

    public static final SkillNode SOULFORGE_ENGINEERING = deferred(bring(
            "soulforge_engineering",
            "Soulforge Engineering",
            NMItems.refinedDiamondIngot,
            -5, -2,
            "Bring 1 refined diamond ingot after defeating the Wither.",
            NMItems.refinedDiamondIngot.itemID, 0, false, 1,
            "Unlock the Soulforge conversion recipe.", none(),
            RITUAL, false),
            () -> NMSkillNodes.DEADZONE_FOUNDRY,
            () -> NMSkillNodes.WITHER_KILL_LOOT);

    public static final SkillNode EXPLOSIVES_ENGINEERING = deferred(bring(
            "explosives_engineering",
            "Explosives Engineering",
            Item.gunpowder,
            2, -3,
            "Bring 128 gunpowder.",
            Item.gunpowder.itemID, 0, false, 128,
            "Unlock dynamite and reinforced powder charges.", none(),
            RITUAL, false),
            () -> NMSkillNodes.POWDER_KEG_RECIPE,
            () -> NMSkillNodes.DIAMOND_CRYSTALS);

    public static final SkillNode BLOOD_STORAGE = deferred(bring(
            "blood_storage",
            "Blood Storage",
            NMBlocks.bloodChest,
            -5, -1,
            "Bring 128 blood orbs.",
            NMItems.bloodOrb.itemID, 0, false, 128,
            "Unlock blood-bound storage.", none(),
            RITUAL, false),
            () -> NMSkillNodes.BLOOD_ARMORY,
            () -> NMSkillNodes.BLOOD_ORBS_128_DAMAGE,
            () -> NMSkillNodes.ITEM_FRAMES);

    public static final SkillNode SILK_16_STAKES = bring(
            "silk_16_stakes",
            "Stake Binding",
            Item.silk,
            0, -1,
            "Bring 16 silk.",
            Item.silk.itemID, 0, false, 16,
            "Unlock the stake recipe.", none(),
            RITUAL, false);

    public static final SkillNode UNFIRED_NETHER_BRICKS_16 = bring(
            "unfired_nether_bricks_16",
            "Infernal Kiln Load",
            BTWItems.unfiredNetherBrick,
            -1, -1,
            "Bring 16 unfired nether bricks.",
            BTWItems.unfiredNetherBrick.itemID, 0, false, 16,
            "Unlock nether brick kiln recipes.", none(),
            RITUAL, false);

    public static final SkillNode NETHER_BRICKS_32 = deferred(bring(
            "nether_bricks_32",
            "Infernal Architecture",
            BTWItems.netherBrick,
            1, -1,
            "Bring 32 nether bricks.",
            BTWItems.netherBrick.itemID, 0, false, 32,
            "Unlock nether brick block, slab, siding, moulding, corner, and stair recipes.", none(),
            RITUAL, false),
            () -> NMSkillNodes.UNFIRED_NETHER_BRICKS_16);

    public static final SkillNode SOUL_SAND_PILES_32 = bring(
            "soul_sand_piles_32",
            "Soul Sand Packing",
            BTWItems.soulSandPile,
            -2, 1,
            "Bring 32 soul sand piles.",
            BTWItems.soulSandPile.itemID, 0, false, 32,
            "Unlock the soul sand piston packing recipe.", none(),
            RITUAL, false);

    public static final SkillNode CREEPER_OYSTERS_16 = bring(
            "creeper_oysters_16",
            "Volatile Alloying",
            BTWItems.creeperOysters,
            2, 1,
            "Bring 16 creeper oysters.",
            BTWItems.creeperOysters.itemID, 0, false, 16,
            "+1 to diamond ingot and stump remover recipe unlocks.", none(),
            RITUAL, false);

    public static final SkillNode SOUL_URNS_16 = bring(
            "soul_urns_16",
            "Soul Mechanisms",
            BTWItems.soulUrn,
            -1, 2,
            "Bring 16 soul urns.",
            BTWItems.soulUrn.itemID, 0, false, 16,
            "+1 to piston, corpse eye, and runed skull recipe unlocks.", none(),
            RITUAL, false);

    public static final SkillNode ENDER_PEARLS_16 = bring(
            "ender_pearls_16",
            "Ender Optics",
            Item.enderPearl,
            1, 2,
            "Bring 16 ender pearls.",
            Item.enderPearl.itemID, 0, false, 16,
            "Unlock the ocular of ender recipe.", none(),
            RITUAL, false);

    public static final SkillNode NITRE_16 = bring(
            "nitre_16",
            "Nitre Proportioning",
            BTWItems.nitre,
            2, -2,
            "Bring 16 nitre.",
            BTWItems.nitre.itemID, 0, false, 16,
            "+1 to the gunpowder recipe unlock.", none(),
            RITUAL, false);

    public static final SkillNode BRIMSTONE_16 = bring(
            "brimstone_16",
            "Brimstone Proportioning",
            BTWItems.brimstone,
            -2, 2,
            "Bring 16 brimstone.",
            BTWItems.brimstone.itemID, 0, false, 16,
            "+1 to the gunpowder recipe unlock.", none(),
            RITUAL, false);

    public static final SkillNode COAL_DUST_32 = bring(
            "coal_dust_32",
            "Carbon Proportioning",
            BTWItems.coalDust,
            2, 2,
            "Bring 32 coal dust.",
            BTWItems.coalDust.itemID, 0, false, 32,
            "+1 to the gunpowder recipe unlock.", none(),
            RITUAL, false);

    public static final SkillNode GUNPOWDER_16 = bring(
            "gunpowder_16",
            "Fuse Chemistry",
            Item.gunpowder,
            0, -3,
            "Bring 16 gunpowder.",
            Item.gunpowder.itemID, 0, false, 16,
            "Unlock the fuse recipe.", none(),
            RITUAL, false);

    public static final SkillNode FUSE_16 = deferred(bring(
            "fuse_16",
            "Ordnance Fusing",
            BTWItems.fuse,
            3, 0,
            "Bring 16 fuse.",
            BTWItems.fuse.itemID, 0, false, 16,
            "Unlock dynamite and TNT recipes.", none(),
            RITUAL, false),
            () -> NMSkillNodes.GUNPOWDER_16);

    public static final SkillNode BLASTING_OIL_16 = bring(
            "blasting_oil_16",
            "Blasting Oil Reserve",
            BTWItems.blastingOil,
            0, 3,
            "Bring 16 blasting oil.",
            BTWItems.blastingOil.itemID, 0, false, 16,
            "+1 to the dynamite recipe unlock.", none(),
            RITUAL, false);

    public static final SkillNode HELLFIRE_DUST_32 = bring(
            "hellfire_dust_32",
            "Hellfire Chemistry",
            BTWItems.hellfireDust,
            -1, -3,
            "Bring 32 hellfire dust.",
            BTWItems.hellfireDust.itemID, 0, false, 32,
            "Unlock blasting oil and concentrated hellfire recipes.", none(),
            RITUAL, false);

    public static final SkillNode GROUND_NETHERRACK_32 = bring(
            "ground_netherrack_32",
            "Netherrack Reagent",
            BTWItems.groundNetherrack,
            1, -3,
            "Bring 32 ground netherrack.",
            BTWItems.groundNetherrack.itemID, 0, false, 32,
            "Unlock the nether sludge recipe.", none(),
            RITUAL, false);

    public static final SkillNode CANDLES_16 = bring(
            "candles_16",
            "Infernal Illumination",
            BTWItems.candle,
            3, -1,
            "Bring 16 candles.",
            BTWItems.candle.itemID, 0, false, 16,
            "+1 to the infernal enchanter recipe unlock.", none(),
            RITUAL, false);

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

    public static final SkillNode WOOD_GRAVITY_BOOKS = deferred(bring(
            "wood_gravity_books",
            "Structural Library",
            Item.book,
            -1, 0,
            "Bring 16 books.",
            Item.book.itemID, 0, false, 16,
            "+1/4 wood-gravity progress.", SkillRewardActions.addWoodGravityProgress(),
            KNOWLEDGE, true),
            () -> NMSkillNodes.EXPERIENCE_PRIMER);

    public static final SkillNode ENCHANT_BOOKS_32 = deferred(bring(
            "enchant_books_32",
            "Enchanting Margins",
            Item.book,
            -2, 1,
            "Bring 32 books.",
            Item.book.itemID, 0, false, 32,
            "2% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.02F),
            KNOWLEDGE, false),
            () -> NMSkillNodes.WOOD_GRAVITY_BOOKS);

    public static final SkillNode HOTBAR_BOOKS = deferred(bring(
            "hotbar_books",
            "Indexed Hotbar",
            Item.book,
            -3, 1,
            "Bring 128 books.",
            Item.book.itemID, 0, false, 128,
            "+1 hotbar slot.", SkillRewardActions.addHotbarSlots(1),
            KNOWLEDGE, false),
            () -> NMSkillNodes.ENCHANT_BOOKS_32);

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

    public static final SkillNode THIRD_INVENTORY_ROW = deferred(counter(
            "third_inventory_row",
            "Expanded Studies",
            BTWBlocks.chest,
            -1, -1,
            "Reach 30 XP levels.",
            (p, w) -> p.experienceLevel >= 30,
            "Permanently unlock the third inventory row.", SkillRewardActions.unlockThirdInventoryRow(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.EXPERIENCE_PRIMER);

    public static final SkillNode TRADE_100 = counter(
            "trade_100",
            "Market Observer",
            Item.emerald,
            2, 0,
            "Trade 100 times.",
            (p, w) -> SkillHandler.getPlayerData(p).tradesCompleted >= 100,
            "Villager profession-change chance falls to 30%.", SkillRewardActions.setVillagerProfessionChangeChance(0.30F),
            KNOWLEDGE, false);

    public static final SkillNode TRADE_250 = deferred(counter(
            "trade_250",
            "Market Analyst",
            Item.emerald,
            3, -1,
            "Trade 250 times.",
            (p, w) -> SkillHandler.getPlayerData(p).tradesCompleted >= 250,
            "Villager profession-change chance falls to 10%.", SkillRewardActions.setVillagerProfessionChangeChance(0.10F),
            KNOWLEDGE, false),
            () -> NMSkillNodes.TRADE_100);

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

    public static final SkillNode WITHER_XP_LEVELS = deferred(counter(
            "wither_xp_levels",
            "Experienced Invocation",
            Item.expBottle,
            0, -2,
            "Reach 50 XP levels.",
            (p, w) -> p.experienceLevel >= 50,
            "+1/5 Wither-summoning progress.", SkillRewardActions.addWitherSummonProgress(),
            KNOWLEDGE, true),
            () -> NMSkillNodes.XP_CAP_REMOVAL);

    public static final SkillNode ENCHANT_MANUSCRIPTS_10 = deferred(bring(
            "enchant_manuscripts_10",
            "Manuscript Corpus",
            Item.enchantedBook,
            -3, 2,
            "Bring 10 ancient manuscripts.",
            Item.enchantedBook.itemID, 0, false, 10,
            "10% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.10F),
            KNOWLEDGE, false),
            () -> NMSkillNodes.ENCHANTMENT_TABLE_USE);

    public static final SkillNode TRADE_500 = deferred(counter(
            "trade_500",
            "Market Certainty",
            Item.emerald,
            4, -1,
            "Trade 500 times.",
            (p, w) -> SkillHandler.getPlayerData(p).tradesCompleted >= 500,
            "Villagers never change profession on level-up.", SkillRewardActions.setVillagerProfessionChangeChance(0.0F),
            KNOWLEDGE, false),
            () -> NMSkillNodes.TRADE_250);

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

    public static final SkillNode LAPIS_512 = deferred(bring(
            "lapis_512",
            "Lapis Thesis",
            new ItemStack(Item.dyePowder, 1, 4),
            -3, -2,
            "Bring 512 lapis lazuli.",
            Item.dyePowder.itemID, 4, true, 512,
            "3% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.03F),
            KNOWLEDGE, false),
            () -> NMSkillNodes.LAPIS_64);

    public static final SkillNode NICKEL_MACHINE_RECIPE = deferred(bring(
            "nickel_machine_recipe",
            "Redstone Machining",
            Item.redstone,
            2, 1,
            "Bring 256 redstone.",
            Item.redstone.itemID, 0, false, 256,
            "Unlock the Nickel Machine Part recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.CISTERN_USE);

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

    public static final SkillNode CALIBRATED_CISTERN = deferred(bring(
            "calibrated_cistern",
            "Calibrated Hydraulics",
            NMItems.precisionCrystalGear,
            1, 1,
            "Bring 4 precision crystal gears.",
            NMItems.precisionCrystalGear.itemID, 0, false, 4,
            "Unlock calibrated cistern automation and fluid gauges.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.CRYSTAL_LENS_RECIPE,
            () -> NMSkillNodes.NICKEL_MACHINE_RECIPE);

    public static final SkillNode ITEM_FRAMES = bring(
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

    public static final SkillNode BIOME_FIELD_NOTES = counter(
            "biome_field_notes",
            "Biome Field Notes",
            Item.map,
            -4, 0,
            "Visit 4 unique biomes.",
            (p, w) -> SkillHandler.getPlayerData(p).getVisitedBiomeCount() >= 4,
            "Unlock map recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BURNING_TORCH_BOW_DRILL = bring(
            "burning_torch_bow_drill",
            "Carried Flame",
            BTWBlocks.finiteBurningTorch,
            0, 3,
            "Bring 1 burning crude torch.",
            BTWBlocks.finiteBurningTorch.blockID, 0, false, 1,
            "Unlock the bow-drill recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode PRECISION_MECHANICS = deferred(bring(
            "precision_mechanics",
            "Precision Mechanics",
            NMItems.precisionCrystalGear,
            3, 2,
            "Bring 2 precision crystal gears.",
            NMItems.precisionCrystalGear.itemID, 0, false, 2,
            "Unlock precision mechanical machinery.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.DIAMOND_PRECISION_GEAR,
            () -> NMSkillNodes.NICKEL_MACHINE_RECIPE,
            () -> NMSkillNodes.CRYSTAL_LENS_RECIPE);

    public static final SkillNode MECHANICAL_APPRENTICESHIP = deferred(bring(
            "mechanical_apprenticeship",
            "Mechanical Apprenticeship",
            BTWItems.gear,
            3, 3,
            "Bring 12 wooden gears.",
            BTWItems.gear.itemID, 0, false, 12,
            "Unlock foundational mechanical machinery.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.FLINT_TOOLMAKING);

    public static final SkillNode WIND_ENGINEERING = deferred(bring(
            "wind_engineering",
            "Wind Engineering",
            BTWItems.windMillBlade,
            4, 3,
            "Bring 8 windmill blades.",
            BTWItems.windMillBlade.itemID, 0, false, 8,
            "Unlock wind-powered machinery and the saw.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.MECHANICAL_APPRENTICESHIP);

    public static final SkillNode GOLD_ASSAYING = deferred(bring(
            "gold_assaying",
            "Gold Assaying",
            BTWItems.goldOrePile,
            2, 3,
            "Bring 32 gold ore piles.",
            BTWItems.goldOrePile.itemID, 0, false, 32,
            "Unlock precision gold components.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.IRON_SAMPLE,
            () -> NMSkillNodes.CISTERN_USE);

    public static final SkillNode SIGNAL_ENGINEERING = deferred(bring(
            "signal_engineering",
            "Signal Engineering",
            NMItems.refinedRedstone,
            3, 4,
            "Bring 16 refined redstone.",
            NMItems.refinedRedstone.itemID, 0, false, 16,
            "Unlock calibrated redstone devices.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.GOLD_ASSAYING,
            () -> NMSkillNodes.CALIBRATED_CISTERN,
            () -> NMSkillNodes.CRYSTAL_LENS_RECIPE);

    public static final SkillNode INFERNAL_SCHOLARSHIP = deferred(bring(
            "infernal_scholarship",
            "Infernal Scholarship",
            BTWBlocks.infernalEnchanter,
            -4, 3,
            "Bring 16 ancient manuscripts.",
            Item.enchantedBook.itemID, 0, false, 16,
            "Unlock the Infernal Enchanter.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.ENCHANT_MANUSCRIPTS_10,
            () -> NMSkillNodes.SOULFORGED_ARMORY,
            () -> NMSkillNodes.DEADZONE_FOUNDRY);

    public static final SkillNode STICK_PRIMITIVES = bring(
            "stick_primitives",
            "Primitive Stockpile",
            Item.stick,
            1, -1,
            "Bring 4 sticks.",
            Item.stick.itemID, 0, false, 4,
            "Unlock pointy stick, sharp stone, fire plough, and drill recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode SHARP_STONES_4 = deferred(bring(
            "sharp_stones_4",
            "Friction Fire Kit",
            BTWItems.sharpStone,
            0, 2,
            "Bring 4 sharp stones.",
            BTWItems.sharpStone.itemID, 0, false, 4,
            "Unlock the fire plough recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.LOOSE_STONES_2);

    public static final SkillNode GLUE_16 = bring(
            "glue_16",
            "Adhesive Joinery",
            BTWItems.glue,
            -1, -2,
            "Bring 16 glue.",
            BTWItems.glue.itemID, 0, false, 16,
            "+1 to composite bow, wooden blade, and haft recipe unlocks.", none(),
            KNOWLEDGE, false);

    public static final SkillNode ROPE_8 = deferred(bring(
            "rope_8",
            "Heavy Cordage",
            BTWItems.rope,
            -2, -2,
            "Bring 8 rope.",
            BTWItems.rope.itemID, 0, false, 8,
            "Unlock rope block, name tag, and gearbox recipes.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.HEMP_FIBERS_32);

    public static final SkillNode FABRIC_16 = deferred(bring(
            "fabric_16",
            "Structural Fabric",
            BTWItems.fabric,
            2, -2,
            "Bring 16 fabric.",
            BTWItems.fabric.itemID, 0, false, 16,
            "Unlock windmill blade, axle, and bed recipes.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.HEMP_32);

    public static final SkillNode BELTS_8 = deferred(bring(
            "belts_8",
            "Transmission Belting",
            BTWItems.belt,
            2, 2,
            "Bring 8 belts.",
            BTWItems.belt.itemID, 0, false, 8,
            "+1 to the loom recipe unlock.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.LEATHER_STRAPS_16);

    public static final SkillNode WOODEN_BLADES_16 = deferred(bring(
            "wooden_blades_16",
            "Waterwheel Vanes",
            BTWItems.woodenBlade,
            0, -3,
            "Bring 16 wooden blades.",
            BTWItems.woodenBlade.itemID, 0, false, 16,
            "Unlock the water wheel recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.GLUE_16);

    public static final SkillNode GEARS_64 = bring(
            "gears_64",
            "Automation Stockpile",
            BTWItems.gear,
            -3, 0,
            "Bring 64 gears.",
            BTWItems.gear.itemID, 0, false, 64,
            "Unlock screw pump, gearbox, axle, hibachi, and bellows recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode SCREWS_16 = deferred(bring(
            "screws_16",
            "Pump Fasteners",
            BTWItems.screw,
            3, 0,
            "Bring 16 screws.",
            BTWItems.screw.itemID, 0, false, 16,
            "+1 to the screw pump recipe unlock.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.GEARS_64);

    public static final SkillNode SCREW_PUMPS_4 = deferred(bring(
            "screw_pumps_4",
            "Hydraulic Automation",
            BTWBlocks.screwPump,
            -1, -3,
            "Bring 4 screw pumps.",
            BTWBlocks.screwPump.blockID, 0, false, 4,
            "+1 to the Eye of Ender recipe unlock.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.SCREWS_16);

    public static final SkillNode WIND_MILLS_4 = deferred(bring(
            "wind_mills_4",
            "Vertical Windworks",
            BTWItems.windMill,
            1, -3,
            "Bring 4 windmills.",
            BTWItems.windMill.itemID, 0, false, 4,
            "Unlock the vertical windmill recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.WIND_ENGINEERING);

    public static final SkillNode IRON_NUGGETS_32 = bring(
            "iron_nuggets_32",
            "Fine Ironwork",
            BTWItems.ironNugget,
            -3, -1,
            "Bring 32 iron nuggets.",
            BTWItems.ironNugget.itemID, 0, false, 32,
            "Unlock compass, screw, rail, iron spike, and detector rail recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode GOLD_NUGGETS_32 = bring(
            "gold_nuggets_32",
            "Fine Goldwork",
            Item.goldNugget,
            -1, 3,
            "Bring 32 gold nuggets.",
            Item.goldNugget.itemID, 0, false, 32,
            "Unlock redstone latch, ocular of ender, and pocket sundial recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode GOLD_INGOTS_16 = bring(
            "gold_ingots_16",
            "Gold Engineering",
            Item.ingotGold,
            1, 3,
            "Bring 16 gold ingots.",
            Item.ingotGold.itemID, 0, false, 16,
            "Unlock lens and lightning rod recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode DIAMONDS_8_PRECISION = bring(
            "diamonds_8_precision",
            "Diamond Optics",
            Item.diamond,
            -2, -3,
            "Bring 8 diamonds.",
            Item.diamond.itemID, 0, false, 8,
            "+1 to lens and diamond ingot recipe unlocks.", none(),
            KNOWLEDGE, false);

    public static final SkillNode STEEL_PRESSURE_PLATES_8 = bring(
            "steel_pressure_plates_8",
            "Steel Detection",
            BTWBlocks.steelPressurePlate,
            2, -3,
            "Bring 8 steel pressure plates.",
            BTWBlocks.steelPressurePlate.blockID, 0, false, 8,
            "Unlock the detector block recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode REDSTONE_LATCHES_16 = bring(
            "redstone_latches_16",
            "Latched Logic",
            BTWItems.redstoneLatch,
            3, -2,
            "Bring 16 redstone latches.",
            BTWItems.redstoneLatch.itemID, 0, false, 16,
            "Unlock piston and music block recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode REDSTONE_EYES_16 = deferred(bring(
            "redstone_eyes_16",
            "Visual Logic",
            BTWItems.redstoneEye,
            -2, 3,
            "Bring 16 redstone eyes.",
            BTWItems.redstoneEye.itemID, 0, false, 16,
            "Unlock the comparator recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.REDSTONE_LATCHES_16);

    public static final SkillNode COMPARATORS_8 = deferred(bring(
            "comparators_8",
            "Comparative Detection",
            Item.comparator,
            -3, -3,
            "Bring 8 comparators.",
            Item.comparator.itemID, 0, false, 8,
            "Unlock the detector rail recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.REDSTONE_EYES_16);

    public static final SkillNode OCULARS_8 = deferred(bring(
            "oculars_8",
            "Binocular Ender Optics",
            BTWItems.ocularOfEnder,
            3, -3,
            "Bring 8 oculars of ender.",
            BTWItems.ocularOfEnder.itemID, 0, false, 8,
            "Unlock the ender spectacles recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.ENDER_PEARLS_16);

    public static final SkillNode SUNDIALS_8 = bring(
            "sundials_8",
            "Timed Logic",
            Item.pocketSundial,
            -3, 3,
            "Bring 8 pocket sundials.",
            Item.pocketSundial.itemID, 0, false, 8,
            "Unlock the redstone repeater recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode COMPASSES_8 = bring(
            "compasses_8",
            "Cartographic Orientation",
            Item.compass,
            0, -4,
            "Bring 8 compasses.",
            Item.compass.itemID, 0, false, 8,
            "Unlock map recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode RAILS_32 = bring(
            "rails_32",
            "Rail Logistics",
            Block.rail,
            4, 0,
            "Bring 32 rails.",
            Block.rail.blockID, 0, false, 32,
            "Unlock minecart recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode MINECARTS_8 = deferred(bring(
            "minecarts_8",
            "Crated Transit",
            Item.minecartEmpty,
            0, 4,
            "Bring 8 minecarts.",
            Item.minecartEmpty.itemID, 0, false, 8,
            "Unlock the minecart with crate recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.RAILS_32);

    public static final SkillNode WOOD_SIDINGS_32 = bring(
            "wood_sidings_32",
            "Sawn Household Joinery",
            Item.itemsList[BTWItems.woodSidingStubID],
            -1, -4,
            "Bring 32 wooden sidings.",
            BTWItems.woodSidingStubID, 0, false, 32,
            "Unlock sign, wooden door, trapdoor, bowl, and boat recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode SOAP_16 = bring(
            "soap_16",
            "Industrial Cleaning",
            BTWItems.soap,
            1, -4,
            "Bring 16 soap.",
            BTWItems.soap.itemID, 0, false, 16,
            "Unlock batch piston and hardened clay reclamation recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode LIBRARIAN_ENDER_TREATISE = bring(
            "librarian_ender_treatise",
            "Forbidden Ender Treatise",
            NMItems.librarianEnderTreatise,
            -4, -1,
            "Bring the Librarian's Ender Treatise.",
            NMItems.librarianEnderTreatise.itemID, 0, false, 1,
            "+1 to the Eye of Ender recipe unlock.", none(),
            KNOWLEDGE, false);

    public static final SkillNode MUSIC_RECORDS_16 = recordBring(
            "music_records_16",
            "Discographic Metallurgy",
            Item.record13,
            -4, 1,
            "Bring 16 music records.",
            "Unlock the tuning fork recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode UNIQUE_RECIPES_64 = counter(
            "unique_recipes_64",
            "Improvised Curriculum",
            Block.workbench,
            4, 1,
            "Craft 64 unique recipe outputs.",
            (p, w) -> SkillHandler.getPlayerData(p).getUniqueCraftedOutputCount() >= 64,
            "Unlock the workbench recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode TURNTABLE_ROTATIONS_128 = deferred(counter(
            "turntable_rotations_128",
            "Production Potter",
            BTWBlocks.turntable,
            -1, 4,
            "Complete 128 turntable rotations.",
            (p, w) -> SkillHandler.getPlayerData(p).turntableRotations >= 128,
            "Unlock crucible, planter, vase, and urn pottery recipes.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.CLAY_BALLS_32);

    public static final SkillNode AUTOMATION_ACHIEVEMENTS = counter(
            "automation_achievements",
            "Automation Completionist",
            BTWBlocks.blockDispenser,
            1, 4,
            "Complete every achievement in the Automation category.",
            (p, w) -> BTWAchievements.TAB_AUTOMATION.achievementList.stream()
                    .allMatch(achievement -> AchievementHandler.hasUnlocked(p, achievement)),
            "+1 to the Eye of Ender recipe unlock.", none(),
            KNOWLEDGE, false);

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

    public static final SkillNode BLAZE_MOB_KILLS = deferred(counter(
            "blaze_mob_kills",
            "Thousand-Kill Pyrology",
            Item.blazeRod,
            2, -2,
            "Kill 1,000 mobs.",
            (p, w) -> SkillHandler.getPlayerData(p).mobsKilled >= 1000,
            "+10% blaze-rod drop chance.", SkillRewardActions.addBlazeRodDropChance(0.10F),
            COMBAT, false),
            () -> NMSkillNodes.NETHER_MOB_KILLS);

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

    public static final SkillNode WITCH_BREW_SPEED = deferred(counter(
            "witch_brew_speed",
            "Witch Exterminator",
            BTWItems.witchWart,
            -1, 1,
            "Kill 30 witches.",
            (p, w) -> SkillHandler.getPlayerData(p).witchesKilled >= 30,
            "+10% brewing speed.", SkillRewardActions.addBrewingSpeed(0.10F),
            COMBAT, false),
            () -> NMSkillNodes.WITCH_HUNTER);

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

    public static final SkillNode ROTTEN_FLESH_NOTES = bring(
            "rotten_flesh_notes",
            "Rotten-Flesh Notes",
            Item.rottenFlesh,
            -3, -1,
            "Bring 1 rotten flesh.",
            Item.rottenFlesh.itemID, 0, false, 1,
            "Unlock the kibble cauldron recipe.", none(),
            COMBAT, false);

    public static final SkillNode STICK_CLUB_PATTERNS = bring(
            "stick_club_patterns",
            "Stick Club Patterns",
            Item.stick,
            0, 3,
            "Bring 16 sticks.",
            Item.stick.itemID, 0, false, 16,
            "+1 to the wooden- and bone-club recipe unlocks.", none(),
            COMBAT, false);

    public static final SkillNode MOB_CLUB_PATTERNS = counter(
            "mob_club_patterns",
            "Practical Bludgeoning",
            BTWItems.woodenClub,
            1, 3,
            "Kill 16 mobs.",
            (p, w) -> SkillHandler.getPlayerData(p).mobsKilled >= 16,
            "+1 to the wooden- and bone-club recipe unlocks.", none(),
            COMBAT, false);

    public static final SkillNode BONE_CLUB_SWORD_PATTERN = bring(
            "bone_club_sword_pattern",
            "Bone-Club Sword Pattern",
            BTWItems.boneClub,
            2, 3,
            "Bring 4 bone clubs.",
            BTWItems.boneClub.itemID, 0, false, 4,
            "+1 to the iron-sword recipe unlock.", none(),
            COMBAT, false);

    public static final SkillNode WOOD_CLUB_SWORD_PATTERN = bring(
            "wood_club_sword_pattern",
            "Wood-Club Sword Pattern",
            BTWItems.woodenClub,
            3, 3,
            "Bring 4 wooden clubs.",
            BTWItems.woodenClub.itemID, 0, false, 4,
            "+1 to the iron-sword recipe unlock.", none(),
            COMBAT, false);

    public static final SkillNode FLINT_64_ARROWS = bring(
            "flint_64_arrows",
            "Flint Fletching",
            Item.flint,
            0, -2,
            "Bring 64 flint.",
            Item.flint.itemID, 0, false, 64,
            "+1 to the arrow recipe unlock.", none(),
            COMBAT, false);

    public static final SkillNode FEATHERS_32_ARROWS = bring(
            "feathers_32_arrows",
            "Feather Fletching",
            Item.feather,
            0, 2,
            "Bring 32 feathers.",
            Item.feather.itemID, 0, false, 32,
            "+1 to the arrow recipe unlock.", none(),
            COMBAT, false);

    public static final SkillNode STRING_32_ARROWS = bring(
            "string_32_arrows",
            "String Fletching",
            Item.silk,
            -1, -2,
            "Bring 32 string.",
            Item.silk.itemID, 0, false, 32,
            "+1 to the arrow recipe unlock.", none(),
            COMBAT, false);

    public static final SkillNode ARROWS_64 = deferred(bring(
            "arrows_64",
            "Archery Stockpile",
            Item.arrow,
            1, -2,
            "Bring 64 arrows.",
            Item.arrow.itemID, 0, false, 64,
            "Unlock the bow recipe.", none(),
            COMBAT, false),
            () -> NMSkillNodes.FLINT_64_ARROWS);

    public static final SkillNode BOWS_36 = deferred(bring(
            "bows_36",
            "Bowyer's Ordeal",
            Item.bow,
            -2, -1,
            "Bring 36 bows.",
            Item.bow.itemID, 0, false, 36,
            "+1 to the composite bow recipe unlock.", none(),
            COMBAT, false),
            () -> NMSkillNodes.ARROWS_64);

    public static final SkillNode BONES_16_COMPOSITE = deferred(bring(
            "bones_16_composite",
            "Composite Bone Lamination",
            Item.bone,
            2, -1,
            "Bring 16 bones.",
            Item.bone.itemID, 0, false, 16,
            "+1 to the composite bow recipe unlock.", none(),
            COMBAT, false),
            () -> NMSkillNodes.ARROWS_64);

    public static final SkillNode SINEW_16 = deferred(bring(
            "sinew_16",
            "Sinew Backing",
            BTWItems.sinew,
            -2, 2,
            "Bring 16 sinew.",
            BTWItems.sinew.itemID, 0, false, 16,
            "+1 to the composite bow recipe unlock.", none(),
            COMBAT, false),
            () -> NMSkillNodes.BONES_16_COMPOSITE);

    public static final SkillNode BROADHEADS_16 = deferred(bring(
            "broadheads_16",
            "Broadhead Assembly",
            BTWItems.broadheadArrowHead,
            2, 2,
            "Bring 16 broadhead arrowheads.",
            BTWItems.broadheadArrowHead.itemID, 0, false, 16,
            "Unlock the broadhead arrow recipe.", none(),
            COMBAT, false),
            () -> NMSkillNodes.BOWS_36);

    public static final SkillNode ROTTEN_ARROWS_16 = bring(
            "rotten_arrows_16",
            "Arrow Reclamation",
            BTWItems.rottenArrow,
            0, -3,
            "Bring 16 rotten arrows.",
            BTWItems.rottenArrow.itemID, 0, false, 16,
            "Unlock rotten arrow reclamation recipes.", none(),
            COMBAT, false);

    public static final SkillNode ARROWS_FIRED_256 = deferred(counter(
            "arrows_fired_256",
            "Practiced Archer",
            Item.bow,
            -3, 0,
            "Fire 256 arrows.",
            (p, w) -> SkillHandler.getPlayerData(p).arrowsFired >= 256,
            "+1 to the composite bow recipe unlock.", none(),
            COMBAT, false),
            () -> NMSkillNodes.ARROWS_64);

    public static final SkillNode MOBS_KILLED_10000 = counter(
            "mobs_killed_10000",
            "Apocalyptic Census",
            Item.swordDiamond,
            3, 0,
            "Kill 10,000 hostile mobs.",
            (p, w) -> SkillHandler.getPlayerData(p).mobsKilled >= 10000,
            "+1 to the Eye of Ender recipe unlock.", none(),
            COMBAT, false);

    private NMSkillNodes() {
    }

    static {
        for (PendingParents pending : PENDING_PARENTS) {
            SkillNode[] parents = new SkillNode[pending.parents.length];
            for (int i = 0; i < pending.parents.length; i++) {
                parents[i] = pending.parents[i].get();
                if (parents[i] == null) {
                    throw new IllegalStateException("Unresolved parent for skill " + pending.node.id);
                }
            }
            pending.node.parents = parents;
        }
        PENDING_PARENTS.clear();
    }

    public static void initialize() {
    }

    @SafeVarargs
    private static SkillNode deferred(SkillNode node, Supplier<SkillNode>... parents) {
        PENDING_PARENTS.add(new PendingParents(node, parents));
        return node;
    }

    private static final class PendingParents {
        private final SkillNode node;
        private final Supplier<SkillNode>[] parents;

        private PendingParents(SkillNode node, Supplier<SkillNode>[] parents) {
            this.node = node;
            this.parents = parents;
        }
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

    private static SkillNode armorSet(
            String id,
            String name,
            Object icon,
            int x,
            int y,
            String requirement,
            String rewardText,
            SkillUnlockAction reward,
            SkillBranch branch,
            boolean worldReward,
            SkillNode... parents) {
        Item[] armor = {
                BTWItems.tannedLeatherHelmet,
                BTWItems.tannedLeatherChest,
                BTWItems.tannedLeatherLeggings,
                BTWItems.tannedLeatherBoots
        };
        SkillNodeProvider.BuildStep step = getBuilder()
                .id(loc(id))
                .name(name)
                .icon(stack(icon))
                .displayLocation(x, y)
                .requirementText(requirement)
                .triggerCondition((player, world) -> {
                    for (Item item : armor) {
                        if (!SkillInventory.has(player, item, 1)) {
                            return false;
                        }
                    }
                    return true;
                })
                .onUnlockConsume((player, world) -> {
                    for (Item item : armor) {
                        SkillInventory.consume(player, item, 1);
                    }
                });
        if (parents.length > 0) {
            step.parents(parents);
        }
        step.reward(rewardText, reward);
        if (worldReward) {
            step.worldReward();
        }
        return step.build().register(branch);
    }

    private static SkillNode recordBring(
            String id,
            String name,
            Object icon,
            int x,
            int y,
            String requirement,
            String rewardText,
            SkillUnlockAction reward,
            SkillBranch branch,
            boolean worldReward,
            SkillNode... parents) {
        Item[] records = {
                Item.record13,
                Item.recordCat,
                Item.recordBlocks,
                Item.recordChirp,
                Item.recordFar,
                Item.recordMall,
                Item.recordMellohi,
                Item.recordStal,
                Item.recordStrad,
                Item.recordWard,
                Item.record11,
                Item.recordWait
        };
        SkillNodeProvider.BuildStep step = getBuilder()
                .id(loc(id))
                .name(name)
                .icon(stack(icon))
                .displayLocation(x, y)
                .requirementText(requirement)
                .triggerCondition((player, world) -> SkillInventory.hasAny(player, 16, records))
                .onUnlockConsume((player, world) -> SkillInventory.consumeAny(player, 16, records));
        if (parents.length > 0) {
            step.parents(parents);
        }
        step.reward(rewardText, reward);
        if (worldReward) {
            step.worldReward();
        }
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
