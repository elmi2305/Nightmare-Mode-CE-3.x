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

    public static final SkillNode BRING_CLAY_BLOCK_32 = bring(
            "clay_bulk",
            "Clay Stockpile",
            Block.blockClay,
            2, 3,
            "Bring 32 clay blocks.",
            Block.blockClay.blockID, 0, false, 32,
            "Clay cooks 12,000 ticks faster.", SkillRewardActions.addClayCookTimeReduction(12000),
            MINING, false);

    public static final SkillNode MINE_CLAY_BLOCK_1500 = deferred(counter(
            "clay_excavator",
            "Clay Excavator",
            Block.blockClay,
            5, 3,
            "Mine 1,500 clay blocks.",
            (p, w) -> SkillHandler.getPlayerData(p).clayMined >= 1500,
            "Clay cooks another 12,000 ticks faster.", SkillRewardActions.addClayCookTimeReduction(12000),
            MINING, false),
            () -> NMSkillNodes.BRING_CLAY_BLOCK_32);

    public static final SkillNode MINE_STONE_1000 = counter(
            "stone_marathon",
            "Stone Marathon",
            Block.stone,
            0, 2,
            "Mine 1,000 stone of any strata.",
            (p, w) -> SkillHandler.getPlayerData(p).stoneMined >= 1000,
            "+5% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.05F),
            MINING, false);

    public static final SkillNode BRING_FLINT_4 = bring(
            "flint_toolmaking",
            "Flint Toolmaking",
            Item.flint,
            4, 0,
            "Bring 4 flint.",
            Item.flint.itemID, 0, false, 4,
            "Unlock the flint axe crafting recipe.", none(),
            MINING, false);

    public static final SkillNode BRING_IRON_INGOT = bring(
            "iron_sample",
            "Iron Sample",
            Item.ingotIron,
            4, -1,
            "Bring 1 iron ingot.",
            Item.ingotIron.itemID, 0, false, 1,
            "+5% global iron-pile chance and 1 wood-gravity progress.", combine(SkillRewardActions.addGlobalIronPileChance(0.05F), SkillRewardActions.addWoodGravityProgress()),
            MINING, true);

    public static final SkillNode BRING_IRON_ORE_PILE_8 = deferred(bring(
            "iron_pile_cache",
            "Pile Preference",
            BTWItems.ironOrePile,
            5, -1,
            "Bring 8 iron ore piles.",
            BTWItems.ironOrePile.itemID, 0, false, 8,
            "+5% iron-pile chance.", SkillRewardActions.addIronPileChance(0.05F),
            MINING, false),
            () -> NMSkillNodes.BRING_IRON_INGOT);

    public static final SkillNode BRING_RAW_LITHIUM_64 = bring(
            "lithium_crafting",
            "Lithium Tempering",
            NMItems.lithiumRaw,
            -1, 3,
            "Bring 64 raw lithium.",
            NMItems.lithiumRaw.itemID, 0, false, 64,
            "+5% average crafted-item durability.", SkillRewardActions.addCraftingDurability(0.05F),
            MINING, false);

    public static final SkillNode BRING_REFINED_LITHIUM_256 = deferred(bring(
            "lithium_doubling",
            "Lithium Prospector",
            NMItems.lithiumRefined,
            -1, -2,
            "Bring 256 refined lithium.",
            NMItems.lithiumRefined.itemID, 0, false, 256,
            "Lithium ore drops twice as much raw lithium.", SkillRewardActions.doubleLithiumDrops(),
            MINING, false),
            () -> NMSkillNodes.BRING_RAW_LITHIUM_64);

    public static final SkillNode BRING_BLACKSTONE_64 = deferred(bring(
            "blackstone_authority",
            "Blackstone Authority",
            new ItemStack(Block.cobblestone, 1, 2),
            4, -2,
            "Bring 64 blackstone (strata-three cobblestone).",
            Block.cobblestone.blockID, 2, true, 64,
            "Strata-three ore can be mined.", SkillRewardActions.unlockStrataThreeOre(),
            MINING, false),
            () -> NMSkillNodes.MINE_STONE_1000);

    public static final SkillNode BRING_REFINED_DIAMOND_INGOT = bring(
            "perfect_diamond_yield",
            "Perfect Diamond Yield",
            NMItems.refinedDiamondIngot,
            -3, 3,
            "Bring 1 refined diamond ingot.",
            NMItems.refinedDiamondIngot.itemID, 0, false, 1,
            "Diamond ore always drops diamond-bearing rock.", SkillRewardActions.guaranteeDiamondRockDrop(),
            MINING, false);

    public static final SkillNode MINE_NICKEL_ORE_500 = deferred(counter(
            "nickel_duplication",
            "Nickel Duplication",
            NMBlocks.nickelOre,
            5, -2,
            "Mine 500 nickel ore.",
            (p, w) -> SkillHandler.getPlayerData(p).nickelOreMined >= 500,
            "Nickel ore gains a 5% chance to drop a second rock.", SkillRewardActions.addDoubleNickelRockChance(0.05F),
            MINING, false),
            () -> NMSkillNodes.BRING_BLACKSTONE_64);

    public static final SkillNode BRING_REFINED_LITHIUM = bring(
            "diamond_lithium",
            "Lithium Diamond Theory",
            NMItems.lithiumRefined,
            -1, -1,
            "Bring 1 refined lithium.",
            NMItems.lithiumRefined.itemID, 0, false, 1,
            "+1/5 Diamond Extraction progress.", SkillRewardActions.addDiamondHarvestProgress(),
            MINING, false);

    public static final SkillNode BRING_CRUDE_OBSIDIAN_16 = bring(
            "nether_obsidian",
            "Crude Portal Theory",
            new ItemStack(Block.obsidian, 1, 1),
            6, 2,
            "Bring 16 crude obsidian.",
            Block.obsidian.blockID, 1, true, 16,
            "+1/8 Nether access progress. Diamond tools and food can survive Nether entry.",
            SkillRewardActions.addNetherAccessProgress(),
            MINING, true);

    public static final SkillNode BRING_POLISHED_CRYSTAL_SHARD_4 = bring(
            "diamond_crystals",
            "Crystal Diamond Theory",
            NMItems.crystalPolishedShard,
            1, -2,
            "Bring 4 polished crystal shards.",
            NMItems.crystalPolishedShard.itemID, 0, false, 4,
            "+1/5 Diamond Extraction progress.", SkillRewardActions.addDiamondHarvestProgress(),
            MINING, false);

    public static final SkillNode BRING_IRON_ANVIL = bring(
            "diamond_anvil",
            "Anvil Diamond Theory",
            NMBlocks.ironAnvil,
            6, -2,
            "Bring 1 iron anvil.",
            NMBlocks.ironAnvil.blockID, 0, false, 1,
            "+1/5 Diamond Extraction progress.", SkillRewardActions.addDiamondHarvestProgress(),
            MINING, false);

    public static final SkillNode MINE_STRATA_ONE_COBBLESTONE_3000 = deferred(counter(
            "hammer_preservation",
            "Hammer Preservation",
            Block.cobblestone,
            5, 4,
            "Mine 3,000 strata-one cobblestone.",
            (p, w) -> SkillHandler.getPlayerData(p).strataOneCobblestoneMined >= 3000,
            "10% chance not to consume hammer durability.", SkillRewardActions.addHammerDurabilitySaveChance(0.10F),
            MINING, false),
            () -> NMSkillNodes.MINE_STONE_1000);

    public static final SkillNode BRING_REFINEMENT_WASTE_32 = bring(
            "waste_efficiency",
            "Waste Efficiency",
            NMItems.refinementWaste,
            7, -3,
            "Bring 32 refinement waste.",
            NMItems.refinementWaste.itemID, 0, false, 32,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F),
            MINING, false);

    public static final SkillNode BRING_FAILED_DIAMOND_REFINEMENT_16 = bring(
            "failed_refinement",
            "Failure Analysis",
            NMItems.failedDiamondRefinement,
            7, -2,
            "Bring 16 failed diamond refinement.",
            NMItems.failedDiamondRefinement.itemID, 0, false, 16,
            "+10% cistern processing speed.", SkillRewardActions.addCisternSpeed(0.10F),
            MINING, false);

    public static final SkillNode BRING_DIAMOND_BEARING_ROCK_64 = bring(
            "cave_oxygen",
            "Cave Breathing",
            NMItems.diamondBearingRock,
            -1, 0,
            "Bring 64 diamond-bearing rock.",
            NMItems.diamondBearingRock.itemID, 0, false, 64,
            "10% less oxygen loss in caves.", SkillRewardActions.addOxygenLossReduction(0.10F),
            MINING, false);

    public static final SkillNode BRING_NICKEL_PLATE_4 = bring(
            "nickel_heat_recipe",
            "Nickel Heatwork",
            NMItems.nickelPlate,
            0, -2,
            "Bring 4 nickel plates.",
            NMItems.nickelPlate.itemID, 0, false, 4,
            "Unlock the Heat-Resistant Nickel Component recipe.", none(),
            MINING, false);

    public static final SkillNode BRING_COAL_DUST_64 = bring(
            "coal_recipe",
            "Coal Reconstitution",
            BTWItems.coalDust,
            1, -1,
            "Bring 64 coal dust.",
            BTWItems.coalDust.itemID, 0, false, 64,
            "Unlock the coal recipe.", none(),
            MINING, false);

    public static final SkillNode BRING_LITHIUM_SALT_16 = deferred(bring(
            "lithium_cistern",
            "Lithium Brine Control",
            NMItems.lithiumSalt,
            7, -1,
            "Bring 16 lithium salt.",
            NMItems.lithiumSalt.itemID, 0, false, 16,
            "+10% cistern processing speed.", SkillRewardActions.addCisternSpeed(0.10F),
            MINING, false),
            () -> NMSkillNodes.BRING_FAILED_DIAMOND_REFINEMENT_16);

    public static final SkillNode BRING_IRON_BLOOM_8 = bring(
            "iron_bloom_recipe",
            "Bloom Consolidation",
            NMItems.ironBloom,
            3, -1,
            "Bring 8 iron bloom items.",
            NMItems.ironBloom.itemID, 0, false, 8,
            "+1/5 iron-ingot recipe progress.", SkillRewardActions.addIronIngotRecipeProgress(),
            MINING, false);

    public static final SkillNode BRING_DIAMOND_HAMMER = bring(
            "nether_diamond_hammer",
            "Portal Hammer",
            NMItems.diamondHammer,
            6, 1,
            "Bring 1 diamond hammer.",
            NMItems.diamondHammer.itemID, 0, false, 1,
            "+1/8 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            MINING, true);

    public static final SkillNode BRING_UNCLEANED_CRYSTAL_SHARD_32 = bring(
            "crystal_pocket_skill",
            "Pocket Appraisal",
            NMItems.crystalUncleanedShard,
            -1, 1,
            "Bring 32 uncleaned crystal shards.",
            NMItems.crystalUncleanedShard.itemID, 0, false, 32,
            "+10% crystal-pocket shard chance.", SkillRewardActions.addCrystalDropChance(0.10F),
            MINING, false);

    public static final SkillNode BRING_STEEL_HAMMER = bring(
            "steel_hammer_damage",
            "Steel-Driven Violence",
            NMItems.steelHammer,
            2, -4,
            "Bring 1 steel hammer.",
            NMItems.steelHammer.itemID, 0, false, 1,
            "+5% melee damage.", SkillRewardActions.addMeleeDamage(0.05F),
            MINING, false);

    public static final SkillNode BRING_GRAVEL_64 = bring(
            "gravel_shovel",
            "Gravel Familiarity",
            Block.gravel,
            4, 1,
            "Bring 64 gravel.",
            Block.gravel.blockID, 0, false, 64,
            "Shovels mine 5% faster.", SkillRewardActions.addShovelSpeed(0.05F),
            MINING, false);

    public static final SkillNode BRING_CLAY_64 = deferred(bring(
            "clay_shovel",
            "Clay Familiarity",
            Item.clay,
            4, 3,
            "Bring 64 clay.",
            Item.clay.itemID, 0, false, 64,
            "Shovels mine 5% faster.", SkillRewardActions.addShovelSpeed(0.05F),
            MINING, false),
            () -> NMSkillNodes.BRING_GRAVEL_64);

    public static final SkillNode BRING_NICKEL_BEARING_ROCK_64 = bring(
            "nickel_blaze",
            "Nickel Pyrology",
            NMItems.nickelRawRock,
            -1, 2,
            "Bring 64 nickel-bearing rock.",
            NMItems.nickelRawRock.itemID, 0, false, 64,
            "+5% blaze-rod drop chance.", SkillRewardActions.addBlazeRodDropChance(0.05F),
            MINING, false);

    public static final SkillNode BRING_DENSE_NETHERRACK_CORE_16 = deferred(bring(
            "dense_core_metallurgy",
            "Dense-Core Metallurgy",
            NMItems.denseNetherrackCore,
            -3, 4,
            "Bring 16 dense netherrack cores.",
            NMItems.denseNetherrackCore.itemID, 0, false, 16,
            "Unlock dense-core machinery and steel-nugget consolidation.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_NICKEL_PLATE_4);

    public static final SkillNode BRING_FLINT_CHIP = bring(
            "flint_chip_notes",
            "Flint-Chip Notes",
            NMItems.flintChip,
            2, 1,
            "Bring 1 flint chip.",
            NMItems.flintChip.itemID, 0, false, 1,
            "Unlock the crude torch recipe.", none(),
            MINING, false);

    public static final SkillNode JUMP_1000 = counter(
            "jump_cut_slabs",
            "Repeated Compression",
            BTWBlocks.dirtSlab,
            1, 3,
            "Jump 1,000 times.",
            (p, w) -> SkillHandler.getPlayerData(p).jumps >= 1000,
            "+1 to unlock the dirt, sand, and gravel-slab recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_DIAMOND_INGOT_2 = deferred(bring(
            "diamond_toolmaking",
            "Diamond Toolmaking",
            BTWItems.diamondIngot,
            7, 0,
            "Bring 2 diamond ingots.",
            BTWItems.diamondIngot.itemID, 0, false, 2,
            "Unlock precision diamond tools and armor plates.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4,
            () -> NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR,
            () -> NMSkillNodes.BRING_NICKEL_PLATE_4);

    public static final SkillNode BRING_HEAT_RESISTANT_NICKEL_COMPONENT_2 = deferred(bring(
            "thermal_engineering",
            "Thermal Engineering",
            NMItems.nickelHeatComponent,
            -3, 5,
            "Bring 2 heat-resistant nickel components.",
            NMItems.nickelHeatComponent.itemID, 0, false, 2,
            "Unlock high-temperature machinery.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_NICKEL_PLATE_4,
            () -> NMSkillNodes.CRAFT_CAULDRON);

    public static final SkillNode BRING_DEADZONE_SHARD_16 = deferred(bring(
            "deadzone_foundry",
            "Deadzone Foundry",
            NMItems.deadzoneShard,
            -3, 0,
            "Bring 16 deadzone shards.",
            NMItems.deadzoneShard.itemID, 0, false, 16,
            "Unlock deadzone-reinforced late metallurgy.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16,
            () -> NMSkillNodes.BRING_HEAT_RESISTANT_NICKEL_COMPONENT_2);

    public static final SkillNode BRING_ROAD_BLOCK_64 = deferred(bring(
            "road_engineering",
            "Road Engineering",
            NMBlocks.blockRoad,
            -3, 2,
            "Bring 64 road blocks.",
            NMBlocks.blockRoad.blockID, 0, false, 64,
            "Unlock heat-treated asphalt.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_HEAT_RESISTANT_NICKEL_COMPONENT_2,
            () -> NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_2);

    public static final SkillNode BRING_SOULFORGED_STEEL_INGOT_8 = deferred(bring(
            "soulforged_armory",
            "Soulforged Armory",
            BTWItems.soulforgedSteelIngot,
            -3, -1,
            "Bring 8 soulforged steel ingots.",
            BTWItems.soulforgedSteelIngot.itemID, 0, false, 8,
            "Unlock reinforced steel armor plates and equipment patterns.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_REFINED_DIAMOND_INGOT_AFTER_WITHER,
            () -> NMSkillNodes.BRING_DENSE_NETHERRACK_CORE_16);

    public static final SkillNode BRING_STEEL_BUNCH_8 = deferred(bring(
            "steel_logistics",
            "Steel Logistics",
            NMItems.steelBunch,
            1, -4,
            "Bring 8 steel bunches.",
            NMItems.steelBunch.itemID, 0, false, 8,
            "Unlock the steel locker.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_8,
            () -> NMSkillNodes.BRING_BLOOD_ORB_128_II,
            () -> NMSkillNodes.BRING_DEADZONE_SHARD_16);

    public static final SkillNode BRING_LOOSE_STONE_2 = deferred(bring(
            "loose_stones_2",
            "Loose Stone Sampling",
            BTWItems.stone,
            3, 1,
            "Bring 2 loose stones.",
            BTWItems.stone.itemID, 0, true, 2,
            "Unlock the sharp stone recipe.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_STICK_4);

    public static final SkillNode BRING_LOOSE_STONE_64 = deferred(bring(
            "loose_stones_64",
            "Cobble Consolidation",
            BTWItems.stone,
            2, 0,
            "Bring 64 loose stones.",
            BTWItems.stone.itemID, 0, true, 64,
            "Unlock regular cobblestone block and slab recipes.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_LOOSE_STONE_2);

    public static final SkillNode BRING_STRATA_TWO_LOOSE_STONE_128 = deferred(bring(
            "mid_strata_stones_128",
            "Mid-Strata Consolidation",
            new ItemStack(BTWItems.stone, 1, 1),
            1, 4,
            "Bring 128 loose stones from strata two.",
            BTWItems.stone.itemID, 1, true, 128,
            "Unlock strata-two cobblestone block and slab recipes.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_LOOSE_STONE_64);

    public static final SkillNode BRING_STRATA_THREE_LOOSE_STONE_256 = deferred(bring(
            "deep_strata_stones_256",
            "Deep-Strata Consolidation",
            new ItemStack(BTWItems.stone, 1, 2),
            3, -2,
            "Bring 256 loose stones from strata three.",
            BTWItems.stone.itemID, 2, true, 256,
            "Unlock strata-three cobblestone block and slab recipes.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_STRATA_TWO_LOOSE_STONE_128);

    public static final SkillNode BRING_MAIL_16 = bring(
            "mail_16",
            "Mail Assembly",
            BTWItems.mail,
            3, -4,
            "Bring 16 mail.",
            BTWItems.mail.itemID, 0, false, 16,
            "Unlock chain armor recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_CLAY_PILE_16 = bring(
            "clay_piles_16",
            "Clay Consolidation",
            BTWItems.clayPile,
            3, 2,
            "Bring 16 clay piles.",
            BTWItems.clayPile.itemID, 0, false, 16,
            "Unlock the clay ball consolidation recipe.", none(),
            MINING, false);

    public static final SkillNode BRING_CLAY_BALL_32 = deferred(bring(
            "clay_balls_32",
            "Potter's Feedstock",
            Item.clay,
            4, 2,
            "Bring 32 clay balls.",
            Item.clay.itemID, 0, false, 32,
            "Unlock turntable pottery recipes.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_CLAY_PILE_16);

    public static final SkillNode BRING_UNFIRED_CRUDE_BRICK_16 = bring(
            "unfired_crude_bricks_16",
            "Crude Kiln Load",
            BTWItems.unfiredCrudeBrick,
            3, 3,
            "Bring 16 unfired crude bricks.",
            BTWItems.unfiredCrudeBrick.itemID, 0, false, 16,
            "Unlock crude brick kiln recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_BRICK_32 = bring(
            "bricks_32",
            "Brick Architecture",
            Item.brick,
            0, 1,
            "Bring 32 bricks.",
            Item.brick.itemID, 0, false, 32,
            "Unlock brick block, slab, siding, moulding, corner, and stair recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_STONE_BRICK_32 = bring(
            "stone_bricks_32",
            "Stone Architecture",
            BTWItems.stoneBrick,
            0, 0,
            "Bring 32 stone brick items.",
            BTWItems.stoneBrick.itemID, 0, false, 32,
            "Unlock stone brick decorative recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_SNOWBALL_32 = bring(
            "snowballs_32",
            "Snow Packing",
            Item.snowball,
            1, 0,
            "Bring 32 snowballs.",
            Item.snowball.itemID, 0, false, 32,
            "Unlock the snow block piston packing recipe.", none(),
            MINING, false);

    public static final SkillNode BRING_DIRT_PILE_32 = bring(
            "dirt_piles_32",
            "Earth Packing",
            BTWItems.dirtPile,
            2, 2,
            "Bring 32 dirt piles.",
            BTWItems.dirtPile.itemID, 0, false, 32,
            "Unlock dirt piston packing recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_SAND_PILE_32 = bring(
            "sand_piles_32",
            "Sand Packing",
            BTWItems.sandPile,
            1, 2,
            "Bring 32 sand piles.",
            BTWItems.sandPile.itemID, 0, false, 32,
            "Unlock sand piston packing recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_GRAVEL_PILE_32 = bring(
            "gravel_piles_32",
            "Gravel Packing",
            BTWItems.gravelPile,
            1, 1,
            "Bring 32 gravel piles.",
            BTWItems.gravelPile.itemID, 0, false, 32,
            "Unlock gravel and flint piston packing recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_IRON_ORE_PILE_32 = bring(
            "iron_ore_piles_32",
            "Iron Ore Packing",
            BTWItems.ironOrePile,
            2, -1,
            "Bring 32 iron ore piles.",
            BTWItems.ironOrePile.itemID, 0, false, 32,
            "Unlock the iron ore chunk piston packing recipe.", none(),
            MINING, false);

    public static final SkillNode BRING_EMERALD_16 = bring(
            "emeralds_16",
            "Emerald Reclamation",
            Item.emerald,
            2, -2,
            "Bring 16 emeralds.",
            Item.emerald.itemID, 0, false, 16,
            "Unlock the emerald pile crucible recovery recipe.", none(),
            MINING, false);

    public static final SkillNode BRING_DIAMOND_16 = bring(
            "diamonds_16",
            "Diamond Reclamation",
            Item.diamond,
            6, -1,
            "Bring 16 diamonds.",
            Item.diamond.itemID, 0, false, 16,
            "Unlock the diamond pile crucible recovery recipe.", none(),
            MINING, false);

    public static final SkillNode BRING_IRON_INGOT_16 = bring(
            "iron_ingots_16",
            "Iron Toolmaking",
            Item.ingotIron,
            5, 0,
            "Bring 16 iron ingots.",
            Item.ingotIron.itemID, 0, false, 16,
            "Unlock anvil, anchor, and iron tool recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_DIAMOND_INGOT_8 = bring(
            "diamond_ingots_8",
            "Diamond Industry",
            BTWItems.diamondIngot,
            7, 1,
            "Bring 8 diamond ingots.",
            BTWItems.diamondIngot.itemID, 0, false, 8,
            "Unlock diamond tool, diamond armor plate, and diamond ingot block recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_STEEL_NUGGET_32 = bring(
            "steel_nuggets_32",
            "Steel Consolidation",
            BTWItems.steelNugget,
            -2, -4,
            "Bring 32 steel nuggets.",
            BTWItems.steelNugget.itemID, 0, false, 32,
            "Unlock the soulforged steel ingot crucible recipe.", none(),
            MINING, false);

    public static final SkillNode BRING_SOULFORGED_STEEL_INGOT_16 = bring(
            "soulforged_ingots_16",
            "Soulforged Toolmaking",
            BTWItems.soulforgedSteelIngot,
            -1, -4,
            "Bring 16 soulforged steel ingots.",
            BTWItems.soulforgedSteelIngot.itemID, 0, false, 16,
            "Unlock steel tool recipes.", none(),
            MINING, false);

    public static final SkillNode BRING_STEEL_ARMOR_PLATE_16 = deferred(bring(
            "steel_armor_plates_16",
            "Plate Armoring",
            BTWItems.steelArmorPlate,
            0, -4,
            "Bring 16 steel armor plates.",
            BTWItems.steelArmorPlate.itemID, 0, false, 16,
            "Unlock plate armor recipes.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_16);

    public static final SkillNode SMELT_IRON_NUGGET_128 = deferred(counter(
            "kiln_iron_128",
            "Kiln Ironmaster",
            BTWItems.ironNugget,
            4, 4,
            "Smelt 128 iron nuggets in a kiln.",
            (p, w) -> SkillHandler.getPlayerData(p).ironNuggetsKilned >= 128,
            "Unlock the anvil recipe.", none(),
            MINING, false),
            () -> NMSkillNodes.BRING_IRON_INGOT_16);

    // Husbandry

    public static final SkillNode HARVEST_TALL_GRASS_1000 = counter(
            "grass_harvest",
            "Grass Reaper",
            Block.tallGrass,
            4, 1,
            "Harvest 1,000 tall grass.",
            (p, w) -> SkillHandler.getPlayerData(p).tallGrassMined >= 1000,
            "+2% hemp-seed chance when hoeing grass.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false);


    public static final SkillNode BRING_DRIED_PLANT_FIBER_64 = deferred(bring(
            "dried_fiber_hemp",
            "Fiber Seed Lore",
            NMItems.driedPlantFiber,
            4, 2,
            "Bring 64 dried plant fibers.",
            NMItems.driedPlantFiber.itemID, 0, false, 64,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false),
            () -> NMSkillNodes.HARVEST_TALL_GRASS_1000);

    public static final SkillNode PLANT_SAPLING_100 = counter(
            "sapling_planter",
            "Forest Planter",
            Block.sapling,
            2, 3,
            "Plant 100 saplings.",
            (p, w) -> SkillHandler.getPlayerData(p).saplingsPlanted >= 100,
            "+5% twig drop chance.", SkillRewardActions.addTwigDropChance(0.05F),
            HUSBANDRY, false);

    public static final SkillNode BRING_GRASS_BLOCK = deferred(bring(
            "grass_block_hemp",
            "Sod Examination",
            Block.grass,
            1, 4,
            "Bring 1 grass block.",
            Block.grass.blockID, 0, false, 1,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false),
            () -> NMSkillNodes.HARVEST_TALL_GRASS_1000);

    public static final SkillNode TAME_ANIMAL_8 = counter(
            "nether_tamer_8",
            "Dimensional Tamer",
            Item.leash,
            0, -2,
            "Tame 8 animals.",
            (p, w) -> SkillHandler.getPlayerData(p).animalsTamed >= 8,
            "+1 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            HUSBANDRY, true);

    public static final SkillNode TAME_ANIMAL_1 = counter(
            "nether_tamer",
            "Dimensional Tamer",
            Item.leash,
            3, 0,
            "Tame 1 animal.",
            (p, w) -> SkillHandler.getPlayerData(p).animalsTamed >= 1,
            "Reduced Food Spoil Rate", SkillRewardActions.slowFoodSpoilageGlobally(),
            HUSBANDRY, true);

    public static final SkillNode REMOVE_WEED_500 = counter(
            "xp_cap_removal",
            "Weed Transcendence",
            BTWItems.hempSeeds,
            2, -2,
            "Remove weeds 500 times.",
            (p, w) -> SkillHandler.getPlayerData(p).weedsRemoved >= 500,
            "Experience level can exceed 30.", SkillRewardActions.unlockXpAboveThirty(),
            HUSBANDRY, false);

    public static final SkillNode CATCH_FISH_50 = counter(
            "rare_fishing",
            "Rare Angler",
            Item.fishingRod,
            5, 0,
            "Catch 50 fish.",
            (p, w) -> SkillHandler.getPlayerData(p).fishCaught >= 50,
            "+5% rare-fish chance.", SkillRewardActions.addRareFishChance(0.05F),
            HUSBANDRY, false);

    public static final SkillNode COOK_FOOD_200 = counter(
            "cooked_preservation",
            "Preserving Cook",
            Item.beefCooked,
            1, -1,
            "Cook 200 food items.",
            (p, w) -> SkillHandler.getPlayerData(p).foodCooked >= 200,
            "Raw food globally spoils 5% slower and grants 1 wood-gravity progress.", combine(SkillRewardActions.slowFoodSpoilageGlobally(), SkillRewardActions.addWoodGravityProgress()),
            HUSBANDRY, true);

    public static final SkillNode BREAK_DIRT_1000 = counter(
            "dirt_shovel",
            "Dirt Familiarity",
            Block.dirt,
            3, 3,
            "Break 1,000 dirt.",
            (p, w) -> SkillHandler.getPlayerData(p).dirtMined >= 1000,
            "Shovels mine 5% faster.", SkillRewardActions.addShovelSpeed(0.05F),
            HUSBANDRY, false);

    public static final SkillNode BREAK_DIRT_2000 = deferred(counter(
            "dirt_fiber",
            "Soil Fiber Mastery",
            Block.dirt,
            0, 4,
            "Break 2,000 dirt.",
            (p, w) -> SkillHandler.getPlayerData(p).dirtMined >= 2000,
            "Tall grass always drops plant fiber.", SkillRewardActions.alwaysDropPlantFiberFromTallGrass(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BREAK_DIRT_1000);

    public static final SkillNode BRING_LOG_64 = deferred(bring(
            "log_twigs",
            "Logged Branches",
            Block.wood,
            1, 3,
            "Bring 64 logs.",
            Block.wood.blockID, 0, false, 64,
            "+5% twig drop chance.", SkillRewardActions.addTwigDropChance(0.05F),
            HUSBANDRY, false),
            () -> NMSkillNodes.PLANT_SAPLING_100);

    public static final SkillNode BRING_CALAMARI_16 = bring(
            "calamari_loot",
            "Calamari Tribute",
            NMItems.calamari,
            5, 1,
            "Bring 16 calamari.",
            NMItems.calamari.itemID, 0, false, 16,
            "+5% mob drops.", SkillRewardActions.addMobLootChance(0.05F),
            HUSBANDRY, false);

    public static final SkillNode PLANT_CROP_200 = counter(
            "crop_hemp",
            "Crop Rotation",
            Block.crops,
            4, -1,
            "Plant 200 crops.",
            (p, w) -> SkillHandler.getPlayerData(p).cropsPlanted >= 200,
            "+5% hemp-seed chance when hoeing grass.", SkillRewardActions.addHempSeedChance(0.05F),
            HUSBANDRY, false);

    public static final SkillNode BRING_WOOL_128 = bring(
            "bedroll_recipe",
            "Portable Bedding",
            BTWItems.wool,
            6, -1,
            "Bring 128 wool.",
            BTWItems.wool.itemID, 0, false, 128,
            "Unlock the bedroll recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_FEATHER_64 = bring(
            "chicken_feed_recipe",
            "Feathered Nutrition",
            Item.feather,
            -1, -1,
            "Bring 64 feathers.",
            Item.feather.itemID, 0, false, 64,
            "Unlock the chicken-feed recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BREED_ANIMAL_50 = counter(
            "leather_breeding",
            "Breeder's Leather",
            Item.leather,
            1, -2,
            "Breed 50 animals.",
            (p, w) -> SkillHandler.getPlayerData(p).animalsBred >= 50,
            "+1 leather-armor recipe progress.", SkillRewardActions.addLeatherArmorProgress(),
            HUSBANDRY, false);

    public static final SkillNode BRING_SUGAR_CANE_256 = bring(
            "better_lithium_salt",
            "Sweet Lithium",
            Item.reed,
            -1, 0,
            "Bring 256 sugar cane.",
            Item.reed.itemID, 0, false, 256,
            "Unlock a lithium-salt recipe yielding 3.", none(),
            HUSBANDRY, false);

    public static final SkillNode MILK_COW_100 = counter(
            "cake_recipe",
            "Dairy Patissier",
            Item.bucketMilk,
            -1, -2,
            "Milk cows 100 times.",
            (p, w) -> SkillHandler.getPlayerData(p).cowsMilked >= 100,
            "Unlock the cake recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_PUMPKIN_64 = bring(
            "pumpkin_fiber",
            "Pumpkin Mulch",
            Block.pumpkin,
            -1, 2,
            "Bring 64 pumpkins.",
            Block.pumpkin.blockID, 0, false, 64,
            "+10% tall-grass plant-fiber chance.", SkillRewardActions.addTallGrassPlantFiberChance(0.10F),
            HUSBANDRY, false);

    public static final SkillNode BRING_PLANT_FIBER_1024 = bring(
            "fiber_to_straw",
            "Fiber Compression",
            NMItems.plantFiber,
            4, 3,
            "Bring 1,024 plant fibers.",
            NMItems.plantFiber.itemID, 0, false, 1024,
            "Unlock direct plant-fiber-to-straw crafting.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_RARE_FISH_32 = deferred(specialBring(
            "rare_fish_trophies",
            "Trophy Angler",
            NMItems.swordfish,
            5, 2,
            "Bring 32 rare fish.",
            32,
            "+5% rare-fish chance.", SkillRewardActions.addRareFishChance(0.05F),
            HUSBANDRY, false),
            () -> NMSkillNodes.CATCH_FISH_50);

    public static final SkillNode BRING_DRIED_PLANT_FIBER_300 = bring(
            "oxygen_mask_recipe",
            "Fiber Filtration",
            NMItems.driedPlantFiber,
            6, 1,
            "Bring 300 dried plant fiber.",
            NMItems.driedPlantFiber.itemID, 0, false, 300,
            "Unlock the Oxygen Mask recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BREAK_LEAF_500 = deferred(counter(
            "leaf_twigs",
            "Leaf Sifter",
            Block.leaves,
            0, 3,
            "Break 500 leaves.",
            (p, w) -> SkillHandler.getPlayerData(p).leavesMined >= 500,
            "+5% twig drop chance.", SkillRewardActions.addTwigDropChance(0.05F),
            HUSBANDRY, false),
            () -> NMSkillNodes.PLANT_SAPLING_100);

    public static final SkillNode BRING_MELON_BLOCK_64 = bring(
            "melon_damage",
            "Melon Musculature",
            Block.melon,
            -1, 1,
            "Bring 64 melon blocks.",
            Block.melon.blockID, 0, false, 64,
            "+1% melee damage.", SkillRewardActions.addMeleeDamage(0.01F),
            HUSBANDRY, false);

    public static final SkillNode HARVEST_MATURE_CROP_500 = deferred(counter(
            "mature_crop_hemp",
            "Mature Harvest",
            Block.crops,
            5, -1,
            "Harvest 500 fully-grown crops.",
            (p, w) -> SkillHandler.getPlayerData(p).fullyGrownCropsHarvested >= 500,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            HUSBANDRY, false),
            () -> NMSkillNodes.PLANT_CROP_200);

    public static final SkillNode BRING_CURED_MEAT_16 = deferred(bring(
            "cured_preservation",
            "Cured Example",
            BTWItems.curedMeat,
            2, -1,
            "Bring 16 cured meat.",
            BTWItems.curedMeat.itemID, 0, false, 16,
            "Raw food spoils 5% slower.", SkillRewardActions.slowFoodSpoilage(),
            HUSBANDRY, false),
            () -> NMSkillNodes.COOK_FOOD_200);

    public static final SkillNode BRING_BONEMEAL_256 = bring(
            "bonemeal_fiber",
            "Bonemeal Fiber",
            new ItemStack(Item.dyePowder, 1, 15),
            4, 5,
            "Bring 256 bonemeal.",
            Item.dyePowder.itemID, 15, true, 256,
            "+10% tall-grass plant-fiber chance.", SkillRewardActions.addTallGrassPlantFiberChance(0.10F),
            HUSBANDRY, false);

    public static final SkillNode REMOVE_WEED_1000 = deferred(counter(
            "nether_wart_farming",
            "Weed Sovereignty",
            Item.netherStalkSeeds,
            3, -2,
            "Remove weeds 1,000 times.",
            (p, w) -> SkillHandler.getPlayerData(p).weedsRemoved >= 1000,
            "Nether wart can be farmed.", SkillRewardActions.unlockNetherWartFarming(),
            HUSBANDRY, false),
            () -> NMSkillNodes.REMOVE_WEED_500);

    public static final SkillNode BRING_FOUL_FOOD_256 = deferred(bring(
            "foul_preservation",
            "Spoilage Autopsy",
            BTWItems.foulFood,
            3, -1,
            "Bring 256 foul food.",
            BTWItems.foulFood.itemID, 0, false, 256,
            "Raw food spoils 5% slower.", SkillRewardActions.slowFoodSpoilage(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_CURED_MEAT_16);

    public static final SkillNode BRING_DANDELION_16 = bring(
            "dandelion_notes_i",
            "Dandelion Notes I",
            Block.plantYellow,
            2, 1,
            "Bring 16 dandelions.",
            Block.plantYellow.blockID, 0, false, 16,
            "+1 to unlock yellow-dye milling.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_ANOTHER_DANDELION_16 = deferred(bring(
            "dandelion_notes_ii",
            "Dandelion Notes II",
            Block.plantYellow,
            4, 0,
            "Bring another 16 dandelions.",
            Block.plantYellow.blockID, 0, false, 16,
            "+1 to unlock yellow-dye milling.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_DANDELION_16);

    public static final SkillNode BRING_SUGAR_CANE = bring(
            "sugar_cane_notes",
            "Sugar-Cane Notes",
            Item.reed,
            3, 1,
            "Bring 1 sugar cane.",
            Item.reed.itemID, 0, false, 1,
            "Unlock the paper recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_POPPY_16 = bring(
            "poppy_notes",
            "Poppy Notes",
            Block.plantRed,
            3, 2,
            "Bring 16 poppies.",
            Block.plantRed.blockID, 0, false, 16,
            "Unlock red-dye milling.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_SPIDER_SILK_2 = bring(
            "spider_silk_string",
            "Spider-Silk Twisting",
            NMItems.spiderSilk,
            2, 2,
            "Bring 2 spider silk.",
            NMItems.spiderSilk.itemID, 0, false, 2,
            "+1 to unlock the string recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_RAW_PORKCHOP_16 = bring(
            "pork_oven_pattern",
            "Pork Roasting",
            Item.porkRaw,
            1, 2,
            "Bring 16 raw porkchops.",
            Item.porkRaw.itemID, 0, false, 16,
            "+1 to the brick-oven recipe unlock.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_RED_MUSHROOM_32 = bring(
            "mushroom_hotbar",
            "Mushroom Foraging",
            Block.mushroomRed,
            1, 1,
            "Bring 32 red mushrooms.",
            Block.mushroomRed.blockID, 0, false, 32,
            "+1 hotbar slot.", SkillRewardActions.addHotbarSlots(1),
            HUSBANDRY, false);

    public static final SkillNode BRING_SAWDUST_16 = bring(
            "sawdust_campfire",
            "Dry Tinder",
            BTWItems.sawDust,
            1, 0,
            "Bring 16 sawdust.",
            BTWItems.sawDust.itemID, 0, false, 16,
            "Unlock the campfire recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode VISIT_UNIQUE_BIOME_10 = deferred(counter(
            "biome_nether_progress",
            "Wide-Ranging Survey",
            Item.map,
            6, 2,
            "Visit 10 unique biomes.",
            (p, w) -> SkillHandler.getPlayerData(p).getVisitedBiomeCount() >= 10,
            "+1 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            HUSBANDRY, true),
            () -> NMSkillNodes.VISIT_UNIQUE_BIOME_4);

    public static final SkillNode BRING_DRILL_1 = bring(
            "bring_drill",
            "Driller",
            NMItems.drill,
            0, 2,
            "Bring 1 Drill.",
            NMItems.drill.itemID, 0, false, 1,
            "Food spoils 10% faster, but many recipes are unlocked", SkillRewardActions.multiplyFoodSpoilageRate(1.1f),
            HUSBANDRY, false);

    public static final SkillNode BRING_BARK_64 = bring(
            "bark_64",
            "Bark Bundling",
            BTWItems.bark,
            2, 0,
            "Bring 64 bark.",
            BTWItems.bark.itemID, 0, false, 64,
            "Unlock bark storage recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_BONE_CARVING_16 = bring(
            "bone_carvings_16",
            "Hook Carving",
            BTWItems.boneCarving,
            0, 1,
            "Bring 16 bone carvings.",
            BTWItems.boneCarving.itemID, 0, false, 16,
            "Unlock the bone fish hook recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_BONE_FISH_HOOK_8 = deferred(bring(
            "bone_hooks_8",
            "Primitive Angling",
            BTWItems.boneFishHook,
            0, 0,
            "Bring 8 bone fish hooks.",
            BTWItems.boneFishHook.itemID, 0, false, 8,
            "Unlock the fishing rod recipe.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_BONE_CARVING_16);

    public static final SkillNode BRING_BAT_WING_16 = deferred(bring(
            "bat_wings_16",
            "Nocturnal Baiting",
            BTWItems.batWing,
            0, -1,
            "Bring 16 bat wings.",
            BTWItems.batWing.itemID, 0, false, 16,
            "Unlock the baited fishing rod recipe.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_BONE_FISH_HOOK_8);

    public static final SkillNode BRING_HEMP_FIBER_32 = bring(
            "hemp_fibers_32",
            "Ropework",
            BTWItems.hempFibers,
            0, 5,
            "Bring 32 hemp fibers.",
            BTWItems.hempFibers.itemID, 0, false, 32,
            "Unlock the rope recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_HEMP_32 = bring(
            "hemp_32",
            "Hemp Textile Stockpile",
            BTWItems.hemp,
            1, 5,
            "Bring 32 hemp.",
            BTWItems.hemp.itemID, 0, false, 32,
            "Unlock the fabric recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_LEATHER_STRAP_16 = bring(
            "leather_straps_16",
            "Strapped Joinery",
            BTWItems.leatherStrap,
            -2, -1,
            "Bring 16 leather straps.",
            BTWItems.leatherStrap.itemID, 0, false, 16,
            "Unlock belt, haft, and breeding harness recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_WOOL_16 = bring(
            "wool_16_needles",
            "Knitting Materials",
            BTWItems.wool,
            6, -2,
            "Bring 16 wool.",
            BTWItems.wool.itemID, 0, false, 16,
            "Unlock the knitting needles recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_KNITTING_NEEDLE_4 = deferred(bring(
            "knitting_needles_4",
            "Needlework Practice",
            BTWItems.knittingNeedles,
            6, 3,
            "Bring 4 knitting needles.",
            BTWItems.knittingNeedles.itemID, 0, false, 4,
            "Unlock wool knit recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_WOOL_16);

    public static final SkillNode BRING_WOOL_KNIT_16 = deferred(bring(
            "wool_knit_16",
            "Knitted Protection",
            BTWItems.woolKnit,
            6, 4,
            "Bring 16 wool knit.",
            BTWItems.woolKnit.itemID, 0, false, 16,
            "Unlock wool armor recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_KNITTING_NEEDLE_4);

    public static final SkillNode BRING_PADDING_16 = deferred(bring(
            "padding_16",
            "Padded Protection",
            BTWItems.padding,
            -2, 4,
            "Bring 16 padding.",
            BTWItems.padding.itemID, 0, false, 16,
            "Unlock padded armor recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_FABRIC_16);

    public static final SkillNode BRING_TANNED_LEATHER_16 = bring(
            "tanned_leather_16",
            "Tanned Armoring",
            BTWItems.tannedLeather,
            -2, 1,
            "Bring 16 tanned leather.",
            BTWItems.tannedLeather.itemID, 0, false, 16,
            "Unlock tanned leather armor recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_SUGAR_CANE_16 = bring(
            "sugar_cane_16_wicker",
            "Wicker Supply",
            Item.reed,
            4, -2,
            "Bring 16 sugar cane.",
            Item.reed.itemID, 0, false, 16,
            "Unlock wicker pane loom recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_WICKER_PANE_16 = deferred(bring(
            "wicker_panes_16",
            "Wicker Architecture",
            BTWItems.wickerPane,
            0, -3,
            "Bring 16 wicker panes.",
            BTWItems.wickerPane.itemID, 0, false, 16,
            "Unlock wicker block, slab, and pane recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_SUGAR_CANE_16);

    public static final SkillNode BRING_STRAW_32 = bring(
            "straw_32",
            "Thatching Stockpile",
            BTWItems.straw,
            -1, 4,
            "Bring 32 straw.",
            BTWItems.straw.itemID, 0, false, 32,
            "Unlock thatch recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_POTASH_16 = bring(
            "potash_16",
            "Alkaline Chemistry",
            BTWItems.potash,
            2, -3,
            "Bring 16 potash.",
            BTWItems.potash.itemID, 0, false, 16,
            "Unlock soap and nether sludge recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_DUNG_16 = bring(
            "dung_16",
            "Tanning Reagent",
            BTWItems.dung,
            -1, 3,
            "Bring 16 dung.",
            BTWItems.dung.itemID, 0, false, 16,
            "Unlock tanned leather and golden dung recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_SCOURED_LEATHER_16 = bring(
            "scoured_leather_16",
            "Whole-Hide Tanning",
            BTWItems.scouredLeather,
            -2, 3,
            "Bring 16 scoured leather.",
            BTWItems.scouredLeather.itemID, 0, false, 16,
            "Unlock the tanned leather recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_CUT_SCOURED_LEATHER_16 = bring(
            "cut_scoured_leather_16",
            "Cut-Hide Tanning",
            BTWItems.cutScouredLeather,
            -2, 2,
            "Bring 16 cut scoured leather.",
            BTWItems.cutScouredLeather.itemID, 0, false, 16,
            "Unlock the cut tanned leather recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_TALLOW_16 = bring(
            "tallow_16",
            "Chandler's Reserve",
            BTWItems.tallow,
            1, -3,
            "Bring 16 tallow.",
            BTWItems.tallow.itemID, 0, false, 16,
            "Unlock the candle recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_FLOUR_32 = bring(
            "flour_32",
            "Milled Flour Reserve",
            BTWItems.flour,
            6, 5,
            "Bring 32 flour.",
            BTWItems.flour.itemID, 0, false, 32,
            "Unlock bread dough and donut recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_BREAD_DOUGH_16 = deferred(bring(
            "bread_dough_16",
            "Baker's Batch",
            BTWItems.breadDough,
            5, 5,
            "Bring 16 bread dough.",
            BTWItems.breadDough.itemID, 0, false, 16,
            "Unlock kiln-baked bread recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_FLOUR_32);

    public static final SkillNode BRING_RAW_EGG_16 = bring(
            "raw_eggs_16",
            "Egg Cookery",
            BTWItems.rawEgg,
            5, 4,
            "Bring 16 raw eggs.",
            BTWItems.rawEgg.itemID, 0, false, 16,
            "Unlock hard-boiled egg, omelet, scrambled egg, pumpkin pie, and cake recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_COCOA_BEAN_16 = bring(
            "cocoa_beans_16",
            "Chocolate Cookery",
            BTWItems.cocoaBeans,
            5, -2,
            "Bring 16 cocoa beans.",
            BTWItems.cocoaBeans.itemID, 0, false, 16,
            "Unlock the chocolate recipe.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_CHOCOLATE_16 = deferred(bring(
            "chocolate_16",
            "Cookie Dough Cookery",
            BTWItems.chocolate,
            2, 5,
            "Bring 16 chocolate.",
            BTWItems.chocolate.itemID, 0, false, 16,
            "Unlock the unbaked cookie recipe.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_COCOA_BEAN_16);

    public static final SkillNode BRING_FRESH_PUMPKIN_16 = bring(
            "pumpkins_16",
            "Pumpkin Cookery",
            BTWBlocks.freshPumpkin,
            4, 4,
            "Bring 16 fresh pumpkins.",
            BTWBlocks.freshPumpkin.blockID, 0, false, 16,
            "Unlock carved pumpkin and unbaked pumpkin pie recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_BROWN_MUSHROOM_32 = bring(
            "brown_mushrooms_32",
            "Mushroom Cookery",
            BTWItems.brownMushroom,
            3, 4,
            "Bring 32 brown mushrooms.",
            BTWItems.brownMushroom.itemID, 0, false, 32,
            "Unlock kebab, omelet, mushroom stew, and hearty stew recipes.", none(),
            HUSBANDRY, false);

    public static final SkillNode BRING_RAW_MUTTON_16 = deferred(bring(
            "raw_mutton_16",
            "Kebab Butchery",
            BTWItems.rawMutton,
            2, 4,
            "Bring 16 raw mutton.",
            BTWItems.rawMutton.itemID, 0, false, 16,
            "Unlock the raw kebab recipe.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_BROWN_MUSHROOM_32);

    public static final SkillNode BRING_TANNED_LEATHER_ARMOR_SET = deferred(armorSet(
            "tanned_armor_set",
            "Complete Tanned Harness",
            BTWItems.tannedLeatherChest,
            -2, 0,
            "Bring a full set of tanned leather armor.",
            "Unlock gimp armor recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_TANNED_LEATHER_16);

    // Ritual

    public static final SkillNode BRING_WITCH_WART_64 = bring(
            "brewing_stand_use",
            "Witch Wart Alchemy",
            BTWItems.witchWart,
            1, 1,
            "Bring 64 witch warts.",
            BTWItems.witchWart.itemID, 0, false, 64,
            "Brewing stands can be used.", SkillRewardActions.unlockBrewingStandUse(),
            RITUAL, false);

    public static final SkillNode BRING_ENCHANTMENT_TABLE = bring(
            "nether_enchant_table",
            "Portal Enchantment",
            Block.enchantmentTable,
            3, 0,
            "Bring 1 enchantment table.",
            Block.enchantmentTable.blockID, 0, false, 1,
            "+1 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            RITUAL, true);

    public static final SkillNode BRING_BLOOD_ORB_64 = bring(
            "nether_blood_orbs",
            "Blood Portal",
            NMItems.bloodOrb,
            4, 0,
            "Bring 64 blood orbs.",
            NMItems.bloodOrb.itemID, 0, false, 64,
            "+1 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            RITUAL, true);

    public static final SkillNode BRING_POTION_8 = deferred(bring(
            "potions_8_xp",
            "Tasted Experience",
            Item.potion,
            5, 0,
            "Bring 8 potions of any kind.",
            Item.potion.itemID, 0, false, 8,
            "+10% experience gained.", SkillRewardActions.addXpGain(0.10F),
            RITUAL, false),
            () -> NMSkillNodes.BRING_WITCH_WART_64);

    public static final SkillNode BRING_BLAZE_POWDER_16 = bring(
            "blaze_powder_rods",
            "Blaze Distillation",
            Item.blazePowder,
            3, 3,
            "Bring 16 blaze powder.",
            Item.blazePowder.itemID, 0, false, 16,
            "+10% blaze-rod drop chance.", SkillRewardActions.addBlazeRodDropChance(0.10F),
            RITUAL, false);

    public static final SkillNode BRING_VESSEL_OF_THE_DRAGON = bring(
            "nether_dragon_vessel",
            "Vessel Portal",
            BTWBlocks.dragonVessel,
            -1, 2,
            "Bring 1 Vessel of the Dragon.",
            BTWBlocks.dragonVessel.blockID, 0, false, 1,
            "+1/8 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            RITUAL, true);

    public static final SkillNode BRING_NETHER_WART_64 = deferred(bring(
            "netherwart_brew_speed",
            "Wart Fermentation",
            Item.netherStalkSeeds,
            4, 3,
            "Bring 64 nether wart.",
            Item.netherStalkSeeds.itemID, 0, false, 64,
            "+10% brewing speed.", SkillRewardActions.addBrewingSpeed(0.10F),
            RITUAL, false),
            () -> NMSkillNodes.BRING_WITCH_WART_64);

    public static final SkillNode CRAFT_CAULDRON = counter(
            "lithium_stabilizer_recipe",
            "Cauldron Stabilization",
            BTWBlocks.cauldron,
            2, 2,
            "Craft a cauldron and complete its achievement.",
            (p, w) -> AchievementHandler.hasUnlocked(p, BTWAchievements.CRAFT_CAULDRON),
            "Unlock the Lithium Stabilizer recipe.", none(),
            RITUAL, false);

    public static final SkillNode BRING_POTION_40 = deferred(bring(
            "potions_40_damage",
            "Combat Draughts",
            Item.potion,
            5, 1,
            "Bring 40 potions of any kind.",
            Item.potion.itemID, 0, false, 40,
            "+2% melee damage.", SkillRewardActions.addMeleeDamage(0.02F),
            RITUAL, false),
            () -> NMSkillNodes.BRING_POTION_8);

    public static final SkillNode BRING_BLOOD_ORB_128_I = deferred(bring(
            "blood_orbs_128_damage",
            "Blood Strength",
            NMItems.bloodOrb,
            0, 4,
            "Bring 128 blood orbs.",
            NMItems.bloodOrb.itemID, 0, false, 128,
            "+5% melee damage.", SkillRewardActions.addMeleeDamage(0.05F),
            RITUAL, false),
            () -> NMSkillNodes.BRING_BLOOD_ORB_64);

    public static final SkillNode BRING_BLOOD_INGOT_16 = deferred(bring(
            "blood_armory",
            "Blood Armory",
            NMItems.bloodIngot,
            -1, 4,
            "Bring 16 blood ingots.",
            NMItems.bloodIngot.itemID, 0, false, 16,
            "Unlock blood armor and weapon patterns.", none(),
            RITUAL, false),
            () -> NMSkillNodes.BRING_BLOOD_ORB_64);

    public static final SkillNode BRING_SPIDER_EYE_64 = bring(
            "spider_eye_loot",
            "Arachnid Offering",
            Item.spiderEye,
            1, 2,
            "Bring 64 spider eyes.",
            Item.spiderEye.itemID, 0, false, 64,
            "+5% mob drops.", SkillRewardActions.addMobLootChance(0.05F),
            RITUAL, false);

    public static final SkillNode BRING_END_ACCORD = bring(
            "end_accord",
            "End Accord",
            NMItems.endAccord,
            -1, 0,
            "Bring the accord assembled from all four Tier 3 commissions.",
            NMItems.endAccord.itemID, 0, false, 1,
            "Unlock End access.", SkillRewardActions.addEndAccessProgress(),
            RITUAL, true);

    public static final SkillNode BRING_NETHER_INVOCATION_SEAL = bring(
            "nether_invocation_seal",
            "Nether Invocation",
            NMItems.invocationSeal,
            4, 4,
            "Bring the seal assembled from all four Tier 2 commissions.",
            NMItems.invocationSeal.itemID, 0, false, 1,
            "+1/6 Wither-summoning progress.", SkillRewardActions.addWitherSummonProgress(),
            RITUAL, true);

    public static final SkillNode BRING_GHAST_TEAR_16 = bring(
            "ghast_brew_speed",
            "Tear Catalyst",
            Item.ghastTear,
            2, 3,
            "Bring 16 ghast tears.",
            Item.ghastTear.itemID, 0, false, 16,
            "+20% brewing speed.", SkillRewardActions.addBrewingSpeed(0.20F),
            RITUAL, false);

    public static final SkillNode BRING_GUNPOWDER_64 = bring(
            "powder_keg_recipe",
            "Powder Keg",
            Item.gunpowder,
            1, 0,
            "Bring 64 gunpowder.",
            Item.gunpowder.itemID, 0, false, 64,
            "Unlock the powder-keg recipe.", none(),
            RITUAL, false);

    public static final SkillNode BRING_RUNED_WITHER_SKELETON_SKULL = bring(
            "wither_skull_progress",
            "Runed Skull Invocation",
            new ItemStack(Item.skull, 1, 1),
            3, 4,
            "Bring 1 wither skeleton (runed) skull.",
            Item.skull.itemID, 1, true, 1,
            "+10% global mob drops and +1/6 Wither progress.", combine(SkillRewardActions.addGlobalMobLootChance(0.10F), SkillRewardActions.addWitherSummonProgress()),
            RITUAL, true);

    public static final SkillNode BRING_BLOOD_ORB = bring(
            "diamond_blood_orb",
            "Blood Diamond Theory",
            NMItems.bloodOrb,
            1, 4,
            "Bring 1 blood orb.",
            NMItems.bloodOrb.itemID, 0, false, 1,
            "+1/5 Diamond Extraction progress.", SkillRewardActions.addDiamondHarvestProgress(),
            RITUAL, false);

    public static final SkillNode BRING_VESSEL_OF_THE_DRAGON_2 = deferred(bring(
            "wither_vessels",
            "Twin Vessels",
            BTWBlocks.dragonVessel,
            -1, 1,
            "Bring 2 Vessels of the Dragon.",
            BTWBlocks.dragonVessel.blockID, 0, false, 2,
            "+1/6 Wither progress and +10% global XP gained.", combine(SkillRewardActions.addWitherSummonProgress(), SkillRewardActions.addGlobalXpGain(0.10F)),
            RITUAL, true),
            () -> NMSkillNodes.BRING_VESSEL_OF_THE_DRAGON);

    public static final SkillNode BRING_SOUL_SAND_512 = bring(
            "soul_sand_xp",
            "Soul Accounting",
            Block.slowSand,
            0, 3,
            "Bring 512 soul sand.",
            Block.slowSand.blockID, 0, false, 512,
            "+1% experience gained.", SkillRewardActions.addXpGain(0.01F),
            RITUAL, false);

    public static final SkillNode BRING_REFINED_DIAMOND_INGOT_AFTER_WITHER = deferred(bring(
            "soulforge_engineering",
            "Soulforge Engineering",
            NMItems.refinedDiamondIngot,
            2, 4,
            "Bring 1 refined diamond ingot after defeating the Wither.",
            NMItems.refinedDiamondIngot.itemID, 0, false, 1,
            "Unlock the Soulforge conversion recipe.", none(),
            RITUAL, false),
            () -> NMSkillNodes.BRING_DEADZONE_SHARD_16,
            () -> NMSkillNodes.KILL_WITHER);

    public static final SkillNode BRING_GUNPOWDER_256 = deferred(bring(
            "explosives_engineering",
            "Explosives Engineering",
            Item.gunpowder,
            5, 4,
            "Bring 256 gunpowder.",
            Item.gunpowder.itemID, 0, false, 256,
            "Unlock dynamite and reinforced powder charges.", none(),
            RITUAL, false),
            () -> NMSkillNodes.BRING_GUNPOWDER_64,
            () -> NMSkillNodes.BRING_POLISHED_CRYSTAL_SHARD_4);

    public static final SkillNode BRING_BLOOD_ORB_128_II = deferred(bring(
            "blood_storage",
            "Blood Storage",
            NMBlocks.bloodChest,
            -1, 3,
            "Bring 128 blood orbs.",
            NMItems.bloodOrb.itemID, 0, false, 128,
            "Unlock blood-bound storage.", none(),
            RITUAL, false),
            () -> NMSkillNodes.BRING_BLOOD_INGOT_16,
            () -> NMSkillNodes.BRING_BLOOD_ORB_128_I,
            () -> NMSkillNodes.BRING_ITEM_FRAME_27);

    public static final SkillNode BRING_SILK_16 = bring(
            "silk_16_stakes",
            "Stake Binding",
            Item.silk,
            3, 2,
            "Bring 16 silk.",
            Item.silk.itemID, 0, false, 16,
            "Unlock the stake recipe.", none(),
            RITUAL, false);

    public static final SkillNode BRING_UNFIRED_NETHER_BRICK_16 = bring(
            "unfired_nether_bricks_16",
            "Infernal Kiln Load",
            BTWItems.unfiredNetherBrick,
            1, -1,
            "Bring 16 unfired nether bricks.",
            BTWItems.unfiredNetherBrick.itemID, 0, false, 16,
            "Unlock nether brick kiln recipes.", none(),
            RITUAL, false);

    public static final SkillNode BRING_NETHER_BRICK_32 = deferred(bring(
            "nether_bricks_32",
            "Infernal Architecture",
            BTWItems.netherBrick,
            2, -1,
            "Bring 32 nether bricks.",
            BTWItems.netherBrick.itemID, 0, false, 32,
            "Unlock nether brick block, slab, siding, moulding, corner, and stair recipes.", none(),
            RITUAL, false),
            () -> NMSkillNodes.BRING_UNFIRED_NETHER_BRICK_16);

    public static final SkillNode BRING_SOUL_SAND_PILE_32 = bring(
            "soul_sand_piles_32",
            "Soul Sand Packing",
            BTWItems.soulSandPile,
            1, 3,
            "Bring 32 soul sand piles.",
            BTWItems.soulSandPile.itemID, 0, false, 32,
            "Unlock the soul sand piston packing recipe.", none(),
            RITUAL, false);

    public static final SkillNode BRING_CREEPER_OYSTER_16 = bring(
            "creeper_oysters_16",
            "Volatile Alloying",
            BTWItems.creeperOysters,
            4, -1,
            "Bring 16 creeper oysters.",
            BTWItems.creeperOysters.itemID, 0, false, 16,
            "+1 to diamond ingot and stump remover recipe unlocks.", none(),
            RITUAL, false);

    public static final SkillNode BRING_SOUL_URN_16 = bring(
            "soul_urns_16",
            "Soul Mechanisms",
            BTWItems.soulUrn,
            4, 2,
            "Bring 16 soul urns.",
            BTWItems.soulUrn.itemID, 0, false, 16,
            "+1 to piston, corpse eye, and runed skull recipe unlocks.", none(),
            RITUAL, false);

    public static final SkillNode BRING_ENDER_PEARL_16 = bring(
            "ender_pearls_16",
            "Ender Optics",
            Item.enderPearl,
            5, -1,
            "Bring 16 ender pearls.",
            Item.enderPearl.itemID, 0, false, 16,
            "Unlock the ocular of ender recipe.", none(),
            RITUAL, false);

    public static final SkillNode BRING_NITRE_16 = bring(
            "nitre_16",
            "Nitre Proportioning",
            BTWItems.nitre,
            0, 2,
            "Bring 16 nitre.",
            BTWItems.nitre.itemID, 0, false, 16,
            "+1 to the gunpowder recipe unlock.", none(),
            RITUAL, false);

    public static final SkillNode BRING_BRIMSTONE_16 = bring(
            "brimstone_16",
            "Brimstone Proportioning",
            BTWItems.brimstone,
            0, 1,
            "Bring 16 brimstone.",
            BTWItems.brimstone.itemID, 0, false, 16,
            "+1 to the gunpowder recipe unlock.", none(),
            RITUAL, false);

    public static final SkillNode BRING_COAL_DUST_32 = bring(
            "coal_dust_32",
            "Carbon Proportioning",
            BTWItems.coalDust,
            2, 1,
            "Bring 32 coal dust.",
            BTWItems.coalDust.itemID, 0, false, 32,
            "+1 to the gunpowder recipe unlock.", none(),
            RITUAL, false);

    public static final SkillNode BRING_GUNPOWDER_16 = bring(
            "gunpowder_16",
            "Fuse Chemistry",
            Item.gunpowder,
            3, 1,
            "Bring 16 gunpowder.",
            Item.gunpowder.itemID, 0, false, 16,
            "Unlock the fuse recipe.", none(),
            RITUAL, false);

    public static final SkillNode BRING_FUSE_16 = deferred(bring(
            "fuse_16",
            "Ordnance Fusing",
            BTWItems.fuse,
            2, 0,
            "Bring 16 fuse.",
            BTWItems.fuse.itemID, 0, false, 16,
            "Unlock dynamite and TNT recipes.", none(),
            RITUAL, false),
            () -> NMSkillNodes.BRING_GUNPOWDER_16);

    public static final SkillNode BRING_BLASTING_OIL_16 = bring(
            "blasting_oil_16",
            "Blasting Oil Reserve",
            BTWItems.blastingOil,
            3, -1,
            "Bring 16 blasting oil.",
            BTWItems.blastingOil.itemID, 0, false, 16,
            "+1 to the dynamite recipe unlock.", none(),
            RITUAL, false);

    public static final SkillNode BRING_HELLFIRE_DUST_32 = bring(
            "hellfire_dust_32",
            "Hellfire Chemistry",
            BTWItems.hellfireDust,
            0, 0,
            "Bring 32 hellfire dust.",
            BTWItems.hellfireDust.itemID, 0, false, 32,
            "Unlock blasting oil and concentrated hellfire recipes.", none(),
            RITUAL, false);

    public static final SkillNode BRING_GROUND_NETHERRACK_32 = bring(
            "ground_netherrack_32",
            "Netherrack Reagent",
            BTWItems.groundNetherrack,
            0, -1,
            "Bring 32 ground netherrack.",
            BTWItems.groundNetherrack.itemID, 0, false, 32,
            "Unlock the nether sludge recipe.", none(),
            RITUAL, false);

    public static final SkillNode BRING_CANDLE_16 = bring(
            "candles_16",
            "Infernal Illumination",
            BTWItems.candle,
            4, 1,
            "Bring 16 candles.",
            BTWItems.candle.itemID, 0, false, 16,
            "+1 to the infernal enchanter recipe unlock.", none(),
            RITUAL, false);

    // Knowledge

    public static final SkillNode BRING_BOOK = bring(
            "experience_primer",
            "Experience Primer",
            Item.book,
            1, 0,
            "Bring 1 book.",
            Item.book.itemID, 0, false, 1,
            "Experience points can be gained.", SkillRewardActions.unlockExperienceGain(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_BOOK_16 = deferred(bring(
            "wood_gravity_books",
            "Structural Library",
            Item.book,
            2, 0,
            "Bring 16 books.",
            Item.book.itemID, 0, false, 16,
            "+1 wood-gravity progress.", SkillRewardActions.addWoodGravityProgress(),
            KNOWLEDGE, true),
            () -> NMSkillNodes.BRING_BOOK);

    public static final SkillNode BRING_BOOK_32 = deferred(bring(
            "enchant_books_32",
            "Enchanting Margins",
            Item.book,
            4, 3,
            "Bring 32 books.",
            Item.book.itemID, 0, false, 32,
            "2% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.02F),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_BOOK_16);

    public static final SkillNode BRING_BOOK_128 = deferred(bring(
            "hotbar_books",
            "Indexed Hotbar",
            Item.book,
            5, 3,
            "Bring 128 books.",
            Item.book.itemID, 0, false, 128,
            "+1 hotbar slot.", SkillRewardActions.addHotbarSlots(1),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_BOOK_32);

    public static final SkillNode BRING_REDSTONE_16 = bring(
            "cistern_use",
            "Redstone Hydraulics",
            Item.redstone,
            3, 3,
            "Bring 16 redstone.",
            Item.redstone.itemID, 0, false, 16,
            "Cisterns can be used.", SkillRewardActions.unlockCisternUse(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_PRECISION_CRYSTAL_GEAR = bring(
            "diamond_precision_gear",
            "Precision Diamond Theory",
            NMItems.crystalPrecisionGear,
            0, -2,
            "Bring 1 precision crystal gear.",
            NMItems.crystalPrecisionGear.itemID, 0, false, 1,
            "+1/5 Diamond Extraction progress.", SkillRewardActions.addDiamondHarvestProgress(),
            KNOWLEDGE, false);

    public static final SkillNode REACH_XP_LEVEL_30 = deferred(counter(
            "third_inventory_row",
            "Expanded Studies",
            BTWBlocks.chest,
            4, 0,
            "Reach 30 XP levels.",
            (p, w) -> p.experienceLevel >= 30,
            "Permanently unlock the third inventory row.", SkillRewardActions.unlockThirdInventoryRow(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_BOOK);

    public static final SkillNode TRADE_100 = counter(
            "trade_100",
            "Market Observer",
            Item.emerald,
            1, -1,
            "Trade 100 times.",
            (p, w) -> SkillHandler.getPlayerData(p).tradesCompleted >= 100,
            "Villager profession-change chance falls to 30%.", SkillRewardActions.setVillagerProfessionChangeChance(0.30F),
            KNOWLEDGE, false);

    public static final SkillNode TRADE_250 = deferred(counter(
            "trade_250",
            "Market Analyst",
            Item.emerald,
            2, -1,
            "Trade 250 times.",
            (p, w) -> SkillHandler.getPlayerData(p).tradesCompleted >= 250,
            "Villager profession-change chance falls to 10%.", SkillRewardActions.setVillagerProfessionChangeChance(0.10F),
            KNOWLEDGE, false),
            () -> NMSkillNodes.TRADE_100);

    public static final SkillNode BRING_ANCIENT_MANUSCRIPT = bring(
            "enchantment_table_use",
            "Ancient Enchanting",
            Item.enchantedBook,
            3, -1,
            "Bring 1 ancient manuscript.",
            Item.enchantedBook.itemID, 0, false, 1,
            "The enchantment table can be used.", SkillRewardActions.unlockEnchantmentTableUse(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_BOTTLE_OF_ENCHANTING_64 = bring(
            "wither_xp_bottles",
            "Bottled Invocation",
            Item.expBottle,
            5, -1,
            "Bring 64 bottles of enchanting.",
            Item.expBottle.itemID, 0, false, 64,
            "+1/6 Wither-summoning progress.", SkillRewardActions.addWitherSummonProgress(),
            KNOWLEDGE, true);

    public static final SkillNode REACH_XP_LEVEL_50 = deferred(counter(
            "wither_xp_levels",
            "Experienced Invocation",
            Item.expBottle,
            5, 2,
            "Reach 50 XP levels.",
            (p, w) -> p.experienceLevel >= 50,
            "+1/6 Wither-summoning progress.", SkillRewardActions.addWitherSummonProgress(),
            KNOWLEDGE, true),
            () -> NMSkillNodes.REMOVE_WEED_500);

    public static final SkillNode BRING_ANCIENT_MANUSCRIPT_10 = deferred(bring(
            "enchant_manuscripts_10",
            "Manuscript Corpus",
            Item.enchantedBook,
            4, -1,
            "Bring 10 ancient manuscripts.",
            Item.enchantedBook.itemID, 0, false, 10,
            "10% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.10F),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_ANCIENT_MANUSCRIPT);

    public static final SkillNode TRADE_500 = deferred(counter(
            "trade_500",
            "Market Certainty",
            Item.emerald,
            -2, -2,
            "Trade 500 times.",
            (p, w) -> SkillHandler.getPlayerData(p).tradesCompleted >= 500,
            "Villagers never change profession on level-up.", SkillRewardActions.setVillagerProfessionChangeChance(0.0F),
            KNOWLEDGE, false),
            () -> NMSkillNodes.TRADE_250);

    public static final SkillNode CRAFT_BOOKSHELF_64 = counter(
            "bookshelf_xp",
            "Shelf Scholar",
            Block.bookShelf,
            5, 0,
            "Craft 64 bookshelves.",
            (p, w) -> SkillHandler.getPlayerData(p).bookshelvesCrafted >= 64,
            "+10% experience gained.", SkillRewardActions.addXpGain(0.10F),
            KNOWLEDGE, false);

    public static final SkillNode BRING_LAPIS_LAZULI_64 = bring(
            "lapis_64",
            "Lapis Notes",
            new ItemStack(Item.dyePowder, 1, 4),
            6, 3,
            "Bring 64 lapis lazuli.",
            Item.dyePowder.itemID, 4, true, 64,
            "2% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.02F),
            KNOWLEDGE, false);

    public static final SkillNode BRING_LAPIS_LAZULI_512 = deferred(bring(
            "lapis_512",
            "Lapis Thesis",
            new ItemStack(Item.dyePowder, 1, 4),
            6, 4,
            "Bring 512 lapis lazuli.",
            Item.dyePowder.itemID, 4, true, 512,
            "3% enchantment-cost reduction.", SkillRewardActions.addEnchantCostReduction(0.03F),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_LAPIS_LAZULI_64);

    public static final SkillNode BRING_REDSTONE_256 = deferred(bring(
            "nickel_machine_recipe",
            "Redstone Machining",
            Item.redstone,
            0, 0,
            "Bring 256 redstone.",
            Item.redstone.itemID, 0, false, 256,
            "Unlock the Nickel Machine Part recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_REDSTONE_16);

    public static final SkillNode BRING_ENCHANTED_GOLDEN_APPLE = bring(
            "enchanted_apple_xp",
            "Enchanted Nutrition",
            new ItemStack(Item.appleGold, 1, 1),
            0, 4,
            "Bring 1 enchanted golden apple.",
            Item.appleGold.itemID, 1, true, 1,
            "+10% experience gained.", SkillRewardActions.addXpGain(0.10F),
            KNOWLEDGE, false);

    public static final SkillNode BRING_GOLDEN_APPLE_4 = bring(
            "villager_curing",
            "Golden Cure",
            new ItemStack(Item.appleGold, 1, 0),
            1, 4,
            "Bring 4 regular golden apples.",
            Item.appleGold.itemID, 0, true, 4,
            "Villagers can be cured.", SkillRewardActions.unlockVillagerCuring(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_GLASS_64 = bring(
            "crystal_lens_recipe",
            "Glass Optics",
            Block.glass,
            -1, -2,
            "Bring 64 glass.",
            Block.glass.blockID, 0, false, 64,
            "Unlock the Crystal Lens recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_PRECISION_CRYSTAL_GEAR_4 = deferred(bring(
            "calibrated_cistern",
            "Calibrated Hydraulics",
            NMItems.crystalPrecisionGear,
            2, -2,
            "Bring 4 precision crystal gears.",
            NMItems.crystalPrecisionGear.itemID, 0, false, 4,
            "Unlock calibrated cistern automation and fluid gauges.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_GLASS_64,
            () -> NMSkillNodes.BRING_REDSTONE_256);

    public static final SkillNode BRING_ITEM_FRAME_27 = bring(
            "chest_recipe",
            "Framed Storage",
            Item.itemFrame,
            0, 1,
            "Bring 27 item frames.",
            Item.itemFrame.itemID, 0, false, 27,
            "Unlock the chest recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_WRITTEN_BOOK_3 = bring(
            "bookshelf_recipe",
            "Authored Shelving",
            Item.writtenBook,
            3, 0,
            "Bring 3 written books.",
            Item.writtenBook.itemID, 0, false, 3,
            "Unlock the bookshelf recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_PAPER_64 = bring(
            "book_quill_recipe",
            "Paperwork",
            Item.paper,
            1, 2,
            "Bring 64 paper.",
            Item.paper.itemID, 0, false, 64,
            "Unlock the book-and-quill recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode VISIT_UNIQUE_BIOME_4 = counter(
            "biome_field_notes",
            "Biome Field Notes",
            Item.map,
            3, 1,
            "Visit 4 unique biomes.",
            (p, w) -> SkillHandler.getPlayerData(p).getVisitedBiomeCount() >= 4,
            "Unlock map recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_BURNING_CRUDE_TORCH = bring(
            "burning_torch_bow_drill",
            "Carried Flame",
            BTWBlocks.finiteBurningTorch,
            2, 2,
            "Bring 1 burning crude torch.",
            BTWBlocks.finiteBurningTorch.blockID, 0, false, 1,
            "Unlock the bow-drill recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_PRECISION_CRYSTAL_GEAR_2 = deferred(bring(
            "precision_mechanics",
            "Precision Mechanics",
            NMItems.crystalPrecisionGear,
            1, -2,
            "Bring 2 precision crystal gears.",
            NMItems.crystalPrecisionGear.itemID, 0, false, 2,
            "Unlock precision mechanical machinery.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR,
            () -> NMSkillNodes.BRING_REDSTONE_256,
            () -> NMSkillNodes.BRING_GLASS_64);

    public static final SkillNode BRING_WOODEN_GEAR_12 = deferred(bring(
            "mechanical_apprenticeship",
            "Mechanical Apprenticeship",
            BTWItems.gear,
            -1, 4,
            "Bring 12 wooden gears.",
            BTWItems.gear.itemID, 0, false, 12,
            "Unlock foundational mechanical machinery.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_FLINT_4);

    public static final SkillNode BRING_WINDMILL_BLADE_8 = deferred(bring(
            "wind_engineering",
            "Wind Engineering",
            BTWItems.windMillBlade,
            -1, 3,
            "Bring 8 windmill blades.",
            BTWItems.windMillBlade.itemID, 0, false, 8,
            "Unlock wind-powered machinery and the saw.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_WOODEN_GEAR_12);

    public static final SkillNode BRING_GOLD_ORE_PILE_32 = deferred(bring(
            "gold_assaying",
            "Gold Assaying",
            BTWItems.goldOrePile,
            0, -1,
            "Bring 32 gold ore piles.",
            BTWItems.goldOrePile.itemID, 0, false, 32,
            "Unlock precision gold components.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_IRON_INGOT,
            () -> NMSkillNodes.BRING_REDSTONE_16);

    public static final SkillNode BRING_REFINED_REDSTONE_16 = deferred(bring(
            "signal_engineering",
            "Signal Engineering",
            NMItems.refinedRedstone,
            3, -2,
            "Bring 16 refined redstone.",
            NMItems.refinedRedstone.itemID, 0, false, 16,
            "Unlock calibrated redstone devices.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_GOLD_ORE_PILE_32,
            () -> NMSkillNodes.BRING_PRECISION_CRYSTAL_GEAR_4,
            () -> NMSkillNodes.BRING_GLASS_64);

    public static final SkillNode BRING_ANCIENT_MANUSCRIPT_16 = deferred(bring(
            "infernal_scholarship",
            "Infernal Scholarship",
            BTWBlocks.infernalEnchanter,
            -2, -3,
            "Bring 16 ancient manuscripts.",
            Item.enchantedBook.itemID, 0, false, 16,
            "Unlock the Infernal Enchanter.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_ANCIENT_MANUSCRIPT_10,
            () -> NMSkillNodes.BRING_SOULFORGED_STEEL_INGOT_8,
            () -> NMSkillNodes.BRING_DEADZONE_SHARD_16);

    public static final SkillNode BRING_STICK_4 = bring(
            "stick_primitives",
            "Primitive Stockpile",
            Item.stick,
            2, 1,
            "Bring 4 sticks.",
            Item.stick.itemID, 0, false, 4,
            "Unlock pointy stick, sharp stone, fire plough, and drill recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_SHARP_STONE_4 = deferred(bring(
            "sharp_stones_4",
            "Friction Fire Kit",
            BTWItems.sharpStone,
            3, 2,
            "Bring 4 sharp stones.",
            BTWItems.sharpStone.itemID, 0, false, 4,
            "Unlock the fire plough recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_LOOSE_STONE_2);

    public static final SkillNode BRING_GLUE_16 = bring(
            "glue_16",
            "Adhesive Joinery",
            BTWItems.glue,
            4, 5,
            "Bring 16 glue.",
            BTWItems.glue.itemID, 0, false, 16,
            "+1 to composite bow, wooden blade, and haft recipe unlocks.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_ROPE_8 = deferred(bring(
            "rope_8",
            "Heavy Cordage",
            BTWItems.rope,
            -1, 5,
            "Bring 8 rope.",
            BTWItems.rope.itemID, 0, false, 8,
            "Unlock rope block, name tag, and gearbox recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_HEMP_FIBER_32);

    public static final SkillNode BRING_FABRIC_16 = deferred(bring(
            "fabric_16",
            "Structural Fabric",
            BTWItems.fabric,
            -2, 5,
            "Bring 16 fabric.",
            BTWItems.fabric.itemID, 0, false, 16,
            "Unlock windmill blade, axle, and bed recipes.", none(),
            HUSBANDRY, false),
            () -> NMSkillNodes.BRING_HEMP_32);

    public static final SkillNode BRING_BELT_8 = deferred(bring(
            "belts_8",
            "Transmission Belting",
            BTWItems.belt,
            -1, 0,
            "Bring 8 belts.",
            BTWItems.belt.itemID, 0, false, 8,
            "+1 to the loom recipe unlock.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_LEATHER_STRAP_16);

    public static final SkillNode BRING_WOODEN_BLADE_16 = deferred(bring(
            "wooden_blades_16",
            "Waterwheel Vanes",
            BTWItems.woodenBlade,
            3, 5,
            "Bring 16 wooden blades.",
            BTWItems.woodenBlade.itemID, 0, false, 16,
            "Unlock the water wheel recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_GLUE_16);

    public static final SkillNode BRING_GEAR_64 = bring(
            "gears_64",
            "Automation Stockpile",
            BTWItems.gear,
            -1, 1,
            "Bring 64 gears.",
            BTWItems.gear.itemID, 0, false, 64,
            "Unlock screw pump, gearbox, axle, hibachi, and bellows recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_SCREW_16 = deferred(bring(
            "screws_16",
            "Pump Fasteners",
            BTWItems.screw,
            -1, -1,
            "Bring 16 screws.",
            BTWItems.screw.itemID, 0, false, 16,
            "+1 to the screw pump recipe unlock.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_GEAR_64);

    public static final SkillNode BRING_SCREW_PUMP_4 = deferred(bring(
            "screw_pumps_4",
            "Hydraulic Automation",
            BTWBlocks.screwPump,
            -1, 5,
            "Bring 4 screw pumps.",
            BTWBlocks.screwPump.blockID, 0, false, 4,
            "+1 to the Eye of Ender recipe unlock.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_SCREW_16);

    public static final SkillNode BRING_WINDMILL_4 = deferred(bring(
            "wind_mills_4",
            "Vertical Windworks",
            BTWItems.windMill,
            -1, 2,
            "Bring 4 windmills.",
            BTWItems.windMill.itemID, 0, false, 4,
            "Unlock the vertical windmill recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_WINDMILL_BLADE_8);

    public static final SkillNode BRING_IRON_NUGGET_32 = bring(
            "iron_nuggets_32",
            "Fine Ironwork",
            BTWItems.ironNugget,
            2, 3,
            "Bring 32 iron nuggets.",
            BTWItems.ironNugget.itemID, 0, false, 32,
            "Unlock compass, screw, rail, iron spike, and detector rail recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_GOLD_NUGGET_32 = bring(
            "gold_nuggets_32",
            "Fine Goldwork",
            Item.goldNugget,
            1, 3,
            "Bring 32 gold nuggets.",
            Item.goldNugget.itemID, 0, false, 32,
            "Unlock redstone latch, ocular of ender, and pocket sundial recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_GOLD_INGOT_16 = bring(
            "gold_ingots_16",
            "Gold Engineering",
            Item.ingotGold,
            0, 3,
            "Bring 16 gold ingots.",
            Item.ingotGold.itemID, 0, false, 16,
            "Unlock lens and lightning rod recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_DIAMOND_8 = bring(
            "diamonds_8_precision",
            "Diamond Optics",
            Item.diamond,
            2, 4,
            "Bring 8 diamonds.",
            Item.diamond.itemID, 0, false, 8,
            "+1 to lens and diamond ingot recipe unlocks.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_STEEL_PRESSURE_PLATE_8 = bring(
            "steel_pressure_plates_8",
            "Steel Detection",
            BTWBlocks.steelPressurePlate,
            1, -3,
            "Bring 8 steel pressure plates.",
            BTWBlocks.steelPressurePlate.blockID, 0, false, 8,
            "Unlock the detector block recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_REDSTONE_LATCH_16 = bring(
            "redstone_latches_16",
            "Latched Logic",
            BTWItems.redstoneLatch,
            -2, 4,
            "Bring 16 redstone latches.",
            BTWItems.redstoneLatch.itemID, 0, false, 16,
            "Unlock piston and music block recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_REDSTONE_EYE_16 = deferred(bring(
            "redstone_eyes_16",
            "Visual Logic",
            BTWItems.redstoneEye,
            -2, 3,
            "Bring 16 redstone eyes.",
            BTWItems.redstoneEye.itemID, 0, false, 16,
            "Unlock the comparator recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_REDSTONE_LATCH_16);

    public static final SkillNode BRING_COMPARATOR_8 = deferred(bring(
            "comparators_8",
            "Comparative Detection",
            Item.comparator,
            -2, 2,
            "Bring 8 comparators.",
            Item.comparator.itemID, 0, false, 8,
            "Unlock the detector rail recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_REDSTONE_EYE_16);

    public static final SkillNode BRING_OCULAR_OF_ENDER_8 = deferred(bring(
            "oculars_8",
            "Binocular Ender Optics",
            BTWItems.ocularOfEnder,
            -2, 1,
            "Bring 8 oculars of ender.",
            BTWItems.ocularOfEnder.itemID, 0, false, 8,
            "Unlock the ender spectacles recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_ENDER_PEARL_16);

    public static final SkillNode BRING_POCKET_SUNDIAL_8 = bring(
            "sundials_8",
            "Timed Logic",
            Item.pocketSundial,
            6, -2,
            "Bring 8 pocket sundials.",
            Item.pocketSundial.itemID, 0, false, 8,
            "Unlock the redstone repeater recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_COMPASS_8 = bring(
            "compasses_8",
            "Cartographic Orientation",
            Item.compass,
            6, -1,
            "Bring 8 compasses.",
            Item.compass.itemID, 0, false, 8,
            "Unlock map recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_RAIL_32 = bring(
            "rails_32",
            "Rail Logistics",
            Block.rail,
            4, -2,
            "Bring 32 rails.",
            Block.rail.blockID, 0, false, 32,
            "Unlock minecart recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_MINECART_8 = deferred(bring(
            "minecarts_8",
            "Crated Transit",
            Item.minecartEmpty,
            5, -2,
            "Bring 8 minecarts.",
            Item.minecartEmpty.itemID, 0, false, 8,
            "Unlock the minecart with crate recipe.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_RAIL_32);

    public static final SkillNode BRING_WOODEN_SIDING_32 = bring(
            "wood_sidings_32",
            "Sawn Household Joinery",
            Item.itemsList[BTWItems.woodSidingStubID],
            6, 1,
            "Bring 32 wooden sidings.",
            BTWItems.woodSidingStubID, 0, false, 32,
            "Unlock sign, wooden door, trapdoor, bowl, and boat recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_SOAP_16 = bring(
            "soap_16",
            "Industrial Cleaning",
            BTWItems.soap,
            6, 2,
            "Bring 16 soap.",
            BTWItems.soap.itemID, 0, false, 16,
            "Unlock batch piston and hardened clay reclamation recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_LIBRARIAN_ENDER_TREATISE = bring(
            "librarian_ender_treatise",
            "Forbidden Ender Treatise",
            NMItems.librarianEnderTreatise,
            -1, -3,
            "Bring the Librarian's Ender Treatise.",
            NMItems.librarianEnderTreatise.itemID, 0, false, 1,
            "+1 to the Eye of Ender recipe unlock.", none(),
            KNOWLEDGE, false);

    public static final SkillNode BRING_MUSIC_RECORD_16 = recordBring(
            "music_records_16",
            "Discographic Metallurgy",
            Item.record13,
            2, -3,
            "Bring 16 music records.",
            "Unlock the tuning fork recipe.", none(),
            KNOWLEDGE, false);

    public static final SkillNode CRAFT_UNIQUE_RECIPE_OUTPUT_64 = counter(
            "unique_recipes_64",
            "Improvised Curriculum",
            Block.workbench,
            4, 2,
            "Craft 64 unique recipe outputs.",
            (p, w) -> SkillHandler.getPlayerData(p).getUniqueCraftedOutputCount() >= 64,
            "Unlock more recipes.", none(),
            KNOWLEDGE, false);

    public static final SkillNode CRAFT_UNIQUE_RECIPE_OUTPUT_256 = counter(
            "unique_recipes_256",
            "EMI Fixed Your Game",
            Block.workbench,
            -2, -1,
            "Craft 256 unique recipe outputs.",
            (p, w) -> SkillHandler.getPlayerData(p).getUniqueCraftedOutputCount() >= 256,
            "Unlock even more recipes.", none(),
            KNOWLEDGE, false);


    public static final SkillNode COMPLETE_TURNTABLE_ROTATION_128 = deferred(counter(
            "turntable_rotations_128",
            "Production Potter",
            BTWBlocks.turntable,
            -2, 5,
            "Complete 128 turntable rotations.",
            (p, w) -> SkillHandler.getPlayerData(p).turntableRotations >= 128,
            "Unlock crucible, planter, vase, and urn pottery recipes.", none(),
            KNOWLEDGE, false),
            () -> NMSkillNodes.BRING_CLAY_BALL_32);

    public static final SkillNode COMPLETE_AUTOMATION_ACHIEVEMENTS = counter(
            "automation_achievements",
            "Automation Completionist",
            BTWBlocks.blockDispenser,
            0, -3,
            "Complete every achievement in the Automation category.",
            (p, w) -> BTWAchievements.TAB_AUTOMATION.achievementList.stream()
                    .allMatch(achievement -> AchievementHandler.hasUnlocked(p, achievement)),
            "+1 to the Eye of Ender recipe unlock.", none(),
            KNOWLEDGE, false);

    // Combat

    public static final SkillNode KILL_WITCH_4 = counter(
            "witch_hunter",
            "Witch Hunter",
            BTWItems.witchWart,
            5, 1,
            "Kill 4 witches.",
            (p, w) -> SkillHandler.getPlayerData(p).witchesKilled >= 4,
            "Crystal pockets can be mined.", SkillRewardActions.unlockCrystalMining(),
            COMBAT, false);

    public static final SkillNode KILL_MOB_250 = counter(
            "nether_mob_kills",
            "Portal Slayer",
            Item.swordIron,
            1, 3,
            "Kill 250 mobs.",
            (p, w) -> SkillHandler.getPlayerData(p).mobsKilled >= 250,
            "+1 Nether access progress.", SkillRewardActions.addNetherAccessProgress(),
            COMBAT, true);

    public static final SkillNode KILL_MOB_1000 = deferred(counter(
            "blaze_mob_kills",
            "Thousand-Kill Pyrology",
            Item.blazeRod,
            5, 4,
            "Kill 1,000 mobs.",
            (p, w) -> SkillHandler.getPlayerData(p).mobsKilled >= 1000,
            "+10% blaze-rod drop chance.", SkillRewardActions.addBlazeRodDropChance(0.10F),
            COMBAT, false),
            () -> NMSkillNodes.KILL_MOB_250);

    public static final SkillNode BRING_ENDER_PEARL = bring(
            "wood_gravity_pearl",
            "Ender Architecture",
            Item.enderPearl,
            -1, 2,
            "Bring 1 ender pearl.",
            Item.enderPearl.itemID, 0, false, 1,
            "+1 wood-gravity progress.", SkillRewardActions.addWoodGravityProgress(),
            COMBAT, true);

    public static final SkillNode BRING_ROTTEN_FLESH_BLOCK_64 = bring(
            "rotten_block_spoilage",
            "Rotten Preservation",
            BTWBlocks.rottenFleshBlock,
            -1, -1,
            "Bring 64 rotten-flesh blocks.",
            BTWBlocks.rottenFleshBlock.blockID, 0, false, 64,
            "Raw food spoils 5% slower.", SkillRewardActions.slowFoodSpoilage(),
            COMBAT, false);

    public static final SkillNode BRING_CREEPER_OYSTER_64 = bring(
            "oyster_diamond",
            "Oyster Abrasives",
            BTWItems.creeperOysters,
            -1, -2,
            "Bring 64 creeper oysters.",
            BTWItems.creeperOysters.itemID, 0, false, 64,
            "+5% diamond-bearing-rock chance.", SkillRewardActions.addDiamondRockDropChance(0.05F),
            COMBAT, false);

    public static final SkillNode BRING_MYSTERIOUS_GLAND_64 = bring(
            "gland_brew_speed",
            "Glandular Catalyst",
            BTWItems.mysteriousGland,
            0, -2,
            "Bring 64 mysterious glands.",
            BTWItems.mysteriousGland.itemID, 0, false, 64,
            "+15% brewing speed.", SkillRewardActions.addBrewingSpeed(0.15F),
            COMBAT, false);

    public static final SkillNode BRING_LEATHER_16 = bring(
            "leather_handin",
            "Leather Armorer",
            Item.leather,
            2, 2,
            "Bring 16 leather.",
            Item.leather.itemID, 0, false, 16,
            "+1/2 leather-armor recipe progress.", SkillRewardActions.addLeatherArmorProgress(),
            COMBAT, false);

    public static final SkillNode BRING_IRON_SHOVEL = bring(
            "iron_shovel_recipe",
            "Shovel Pattern",
            Item.shovelIron,
            1, -1,
            "Bring 1 iron shovel.",
            Item.shovelIron.itemID, 0, false, 1,
            "Unlock the iron-shovel recipe.", none(),
            COMBAT, false);

    public static final SkillNode BRING_IRON_SWORD = bring(
            "iron_sword_recipe",
            "Sword Pattern",
            Item.swordIron,
            2, -1,
            "Bring 1 iron sword.",
            Item.swordIron.itemID, 0, false, 1,
            "Unlock the iron-sword recipe.", none(),
            COMBAT, false);

    public static final SkillNode BRING_IRON_HELMET = bring(
            "iron_helmet_progress",
            "Helmet Metallurgy",
            Item.helmetIron,
            3, -1,
            "Bring 1 iron helmet.",
            Item.helmetIron.itemID, 0, false, 1,
            "+1/5 iron-ingot recipe progress.", SkillRewardActions.addIronIngotRecipeProgress(),
            COMBAT, false);

    public static final SkillNode BRING_IRON_CHESTPLATE = bring(
            "iron_chest_progress",
            "Chestplate Metallurgy",
            Item.plateIron,
            4, -1,
            "Bring 1 iron chestplate.",
            Item.plateIron.itemID, 0, false, 1,
            "+1/5 iron-ingot recipe progress.", SkillRewardActions.addIronIngotRecipeProgress(),
            COMBAT, false);

    public static final SkillNode BRING_IRON_LEGGINGS = bring(
            "iron_legs_progress",
            "Leggings Metallurgy",
            Item.legsIron,
            5, -1,
            "Bring 1 iron leggings.",
            Item.legsIron.itemID, 0, false, 1,
            "+1/5 iron-ingot recipe progress.", SkillRewardActions.addIronIngotRecipeProgress(),
            COMBAT, false);

    public static final SkillNode BRING_IRON_BOOTS = bring(
            "iron_boots_progress",
            "Boot Metallurgy",
            Item.bootsIron,
            5, 0,
            "Bring 1 iron boots.",
            Item.bootsIron.itemID, 0, false, 1,
            "+1/5 iron-ingot recipe progress.", SkillRewardActions.addIronIngotRecipeProgress(),
            COMBAT, false);

    public static final SkillNode KILL_ENDERMAN_50 = counter(
            "wither_endermen",
            "Enderman Invocation",
            Item.enderPearl,
            -1, 1,
            "Kill 50 Endermen.",
            (p, w) -> SkillHandler.getPlayerData(p).endermenKilled >= 50,
            "+1/6 Wither-summoning progress.", SkillRewardActions.addWitherSummonProgress(),
            COMBAT, true);

    public static final SkillNode KILL_SPIDER_100 = counter(
            "spider_loot",
            "Spider Exterminator",
            Item.spiderEye,
            0, 1,
            "Kill 100 spiders.",
            (p, w) -> SkillHandler.getPlayerData(p).spidersKilled >= 100,
            "+2% mob drops.", SkillRewardActions.addMobLootChance(0.02F),
            COMBAT, false);

    public static final SkillNode KILL_WITCH_30 = deferred(counter(
            "witch_brew_speed",
            "Witch Exterminator",
            BTWItems.witchWart,
            4, 4,
            "Kill 30 witches.",
            (p, w) -> SkillHandler.getPlayerData(p).witchesKilled >= 30,
            "+10% brewing speed.", SkillRewardActions.addBrewingSpeed(0.10F),
            COMBAT, false),
            () -> NMSkillNodes.KILL_WITCH_4);

    public static final SkillNode KILL_SLIME_64 = counter(
            "slime_shovel",
            "Slime Lubrication",
            Item.slimeBall,
            5, 2,
            "Kill 64 slimes.",
            (p, w) -> SkillHandler.getPlayerData(p).slimesKilled >= 64,
            "Shovels mine 5% faster.", SkillRewardActions.addShovelSpeed(0.05F),
            COMBAT, false);

    public static final SkillNode BRING_BONE_128 = bring(
            "bone_hemp",
            "Bone Seed Divination",
            Item.bone,
            0, 0,
            "Bring 128 bones.",
            Item.bone.itemID, 0, false, 128,
            "+2% hemp-seed chance.", SkillRewardActions.addHempSeedChance(0.02F),
            COMBAT, false);

    public static final SkillNode KILL_WITHER = counter(
            "wither_kill_loot",
            "Wither Victor",
            Item.netherStar,
            3, -2,
            "Kill the Wither.",
            (p, w) -> SkillHandler.getPlayerData(p).withersKilled >= 1,
            "+2% mob drops.", SkillRewardActions.addMobLootChance(0.02F),
            COMBAT, false);

    public static final SkillNode BRING_BLAZE_ROD_16 = bring(
            "netherrack_mining",
            "Blaze-Hardened Pick",
            Item.blazeRod,
            -1, 0,
            "Bring 16 blaze rods.",
            Item.blazeRod.itemID, 0, false, 16,
            "Netherrack can be mined.", SkillRewardActions.unlockNetherrackMining(),
            COMBAT, false);

    public static final SkillNode BRING_ROTTEN_FLESH = bring(
            "rotten_flesh_notes",
            "Rotten-Flesh Notes",
            Item.rottenFlesh,
            2, 1,
            "Bring 1 rotten flesh.",
            Item.rottenFlesh.itemID, 0, false, 1,
            "Unlock the kibble cauldron recipe.", none(),
            COMBAT, false);

    public static final SkillNode BRING_STICK_16 = bring(
            "stick_club_patterns",
            "Stick Club Patterns",
            Item.stick,
            3, 1,
            "Bring 16 sticks.",
            Item.stick.itemID, 0, false, 16,
            "+1 to the wooden- and bone-club recipe unlocks.", none(),
            COMBAT, false);

    public static final SkillNode KILL_MOB_16 = counter(
            "mob_club_patterns",
            "Practical Bludgeoning",
            BTWItems.woodenClub,
            3, 2,
            "Kill 16 mobs.",
            (p, w) -> SkillHandler.getPlayerData(p).mobsKilled >= 16,
            "+1 to the wooden- and bone-club recipe unlocks.", none(),
            COMBAT, false);

    public static final SkillNode BRING_BONE_CLUB_4 = bring(
            "bone_club_sword_pattern",
            "Bone-Club Sword Pattern",
            BTWItems.boneClub,
            1, 1,
            "Bring 4 bone clubs.",
            BTWItems.boneClub.itemID, 0, false, 4,
            "+1 to the iron-sword recipe unlock.", none(),
            COMBAT, false);

    public static final SkillNode BRING_WOODEN_CLUB_4 = bring(
            "wood_club_sword_pattern",
            "Wood-Club Sword Pattern",
            BTWItems.woodenClub,
            1, 2,
            "Bring 4 wooden clubs.",
            BTWItems.woodenClub.itemID, 0, false, 4,
            "+1 to the iron-sword recipe unlock.", none(),
            COMBAT, false);

    public static final SkillNode BRING_FLINT_64 = bring(
            "flint_64_arrows",
            "Flint Fletching",
            Item.flint,
            1, 0,
            "Bring 64 flint.",
            Item.flint.itemID, 0, false, 64,
            "+1 to the arrow recipe unlock.", none(),
            COMBAT, false);

    public static final SkillNode BRING_FEATHER_32 = bring(
            "feathers_32_arrows",
            "Feather Fletching",
            Item.feather,
            2, 0,
            "Bring 32 feathers.",
            Item.feather.itemID, 0, false, 32,
            "+1 to the arrow recipe unlock.", none(),
            COMBAT, false);

    public static final SkillNode BRING_STRING_32 = bring(
            "string_32_arrows",
            "String Fletching",
            Item.silk,
            3, 0,
            "Bring 32 string.",
            Item.silk.itemID, 0, false, 32,
            "+1 to the arrow recipe unlock.", none(),
            COMBAT, false);

    public static final SkillNode BRING_ARROW_64 = deferred(bring(
            "arrows_64",
            "Archery Stockpile",
            Item.arrow,
            4, 0,
            "Bring 64 arrows.",
            Item.arrow.itemID, 0, false, 64,
            "Unlock the bow recipe.", none(),
            COMBAT, false),
            () -> NMSkillNodes.BRING_FLINT_64);

    public static final SkillNode BRING_BOW_36 = deferred(bring(
            "bows_36",
            "Bowyer's Ordeal",
            Item.bow,
            4, 2,
            "Bring 36 bows.",
            Item.bow.itemID, 0, false, 36,
            "+1 to the composite bow recipe unlock.", none(),
            COMBAT, false),
            () -> NMSkillNodes.BRING_ARROW_64);

    public static final SkillNode BRING_BONE_16 = deferred(bring(
            "bones_16_composite",
            "Composite Bone Lamination",
            Item.bone,
            4, 1,
            "Bring 16 bones.",
            Item.bone.itemID, 0, false, 16,
            "+1 to the composite bow recipe unlock.", none(),
            COMBAT, false),
            () -> NMSkillNodes.BRING_ARROW_64);

    public static final SkillNode BRING_SINEW_16 = deferred(bring(
            "sinew_16",
            "Sinew Backing",
            BTWItems.sinew,
            4, 3,
            "Bring 16 sinew.",
            BTWItems.sinew.itemID, 0, false, 16,
            "+1 to the composite bow recipe unlock.", none(),
            COMBAT, false),
            () -> NMSkillNodes.BRING_BONE_16);

    public static final SkillNode BRING_BROADHEAD_ARROWHEAD_16 = deferred(bring(
            "broadheads_16",
            "Broadhead Assembly",
            BTWItems.broadheadArrowHead,
            5, 3,
            "Bring 16 broadhead arrowheads.",
            BTWItems.broadheadArrowHead.itemID, 0, false, 16,
            "Unlock the broadhead arrow recipe.", none(),
            COMBAT, false),
            () -> NMSkillNodes.BRING_BOW_36);

    public static final SkillNode BRING_ROTTEN_ARROW_16 = bring(
            "rotten_arrows_16",
            "Arrow Reclamation",
            BTWItems.rottenArrow,
            3, 3,
            "Bring 16 rotten arrows.",
            BTWItems.rottenArrow.itemID, 0, false, 16,
            "Unlock rotten arrow reclamation recipes.", none(),
            COMBAT, false);

    public static final SkillNode FIRE_ARROW_256 = deferred(counter(
            "arrows_fired_256",
            "Practiced Archer",
            Item.bow,
            2, 3,
            "Fire 256 arrows.",
            (p, w) -> SkillHandler.getPlayerData(p).arrowsFired >= 256,
            "+1 to the composite bow recipe unlock.", none(),
            COMBAT, false),
            () -> NMSkillNodes.BRING_ARROW_64);

    public static final SkillNode KILL_HOSTILE_MOB_10000 = counter(
            "mobs_killed_10000",
            "Apocalyptic Census",
            Item.swordDiamond,
            6, -2,
            "Kill 10,000 hostile mobs.",
            (p, w) -> SkillHandler.getPlayerData(p).mobsKilled >= 10000,
            "+1 to the Eye of Ender recipe unlock.", none(),
            COMBAT, false);

    // compact bottom-right progression clusters

    public static final SkillNode MINE_BLOCK_1000 = counter(
            "mine_blocks_1000", "Working Rhythm", Block.stone, 0, 3,
            "Mine 1,000 blocks.",
            (p, w) -> SkillHandler.getPlayerData(p).blocksMined >= 1000,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F), MINING, false);

    public static final SkillNode MINE_BLOCK_10000 = counter(
            "mine_blocks_10000", "Industrial Rhythm", Item.pickaxeDiamond, 0, 4,
            "Mine 10,000 blocks.",
            (p, w) -> SkillHandler.getPlayerData(p).blocksMined >= 10000,
            "+2% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.02F), MINING, false,
            MINE_BLOCK_1000);

    public static final SkillNode MINE_COAL_ORE_256 = counter(
            "mine_coal_ore_256", "Coal Survey", Block.oreCoal, 5, 1,
            "Mine 256 coal ore.",
            (p, w) -> SkillHandler.getPlayerData(p).coalOreMined >= 256,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F), MINING, false);

    public static final SkillNode MINE_IRON_ORE_256 = counter(
            "mine_iron_ore_256", "Iron Survey", Block.oreIron, 5, 2,
            "Mine 256 iron ore.",
            (p, w) -> SkillHandler.getPlayerData(p).ironOreMined >= 256,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F), MINING, false);

    public static final SkillNode MINE_IRON_ORE_1000 = counter(
            "mine_iron_ore_1000", "Iron Census", Block.oreIron, -1, 4,
            "Mine 1,000 iron ore.",
            (p, w) -> SkillHandler.getPlayerData(p).ironOreMined >= 1000,
            "+2% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.02F), MINING, false,
            MINE_IRON_ORE_256);

    public static final SkillNode MINE_DIAMOND_ORE_100 = counter(
            "mine_diamond_ore_100", "Diamond Census", Block.oreDiamond, 6, 0,
            "Mine 100 diamond ore.",
            (p, w) -> SkillHandler.getPlayerData(p).diamondOreMined >= 100,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F), MINING, false);

    public static final SkillNode BRING_STONE_STICK_64 = bring(
            "stone_sticks_64", "Stone Shafts", NMItems.stoneStick, 3, 0,
            "Bring 64 stone sticks.", NMItems.stoneStick.itemID, 0, false, 64,
            "No reward", none(), MINING, false);

    public static final SkillNode BRING_IRON_STICK_64 = bring(
            "iron_sticks_64", "Iron Shafts", NMItems.ironStick, 3, 4,
            "Bring 64 iron sticks.", NMItems.ironStick.itemID, 0, false, 64,
            "No reward", none(), MINING, false, BRING_STONE_STICK_64);

    public static final SkillNode BRING_DIAMOND_STICK_16 = bring(
            "diamond_sticks_16", "Diamond Shafts", NMItems.diamondStick, 7, 2,
            "Bring 16 diamond sticks.", NMItems.diamondStick.itemID, 0, false, 16,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F), MINING, false,
            BRING_IRON_STICK_64);

    public static final SkillNode BRING_STONE_BRICK_64 = bring(
            "stone_bricks_64", "Masonry Stockpile", BTWItems.stoneBrick, 0, -1,
            "Bring 64 stone bricks.", BTWItems.stoneBrick.itemID, 0, false, 64,
            "No reward", none(), MINING, false);

    public static final SkillNode BRING_IRON_BRICK_64 = bring(
            "iron_bricks_64", "Iron Masonry", NMItems.ironBrick, 2, 4,
            "Bring 64 iron bricks.", NMItems.ironBrick.itemID, 0, false, 64,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F), MINING, false,
            BRING_STONE_BRICK_64);

    public static final SkillNode BRING_DIAMOND_BRICK_4 = bring(
            "diamond_bricks_4", "Diamond Masonry", NMItems.diamondBrick, 7, 3,
            "Bring 4 diamond bricks.", NMItems.diamondBrick.itemID, 0, false, 4,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F), MINING, false,
            BRING_IRON_BRICK_64);

    public static final SkillNode BRING_CRYSTAL_POWDER_32 = bring(
            "crystal_powder_32", "Crystal Frit", NMItems.crystalPowder, 7, 4,
            "Bring 32 crystal powder.", NMItems.crystalPowder.itemID, 0, false, 32,
            "No reward", none(), MINING, false);

    public static final SkillNode BRING_GLASS_BATCH_32 = bring(
            "glass_batch_32", "Glass Batch", NMItems.glassBatch, 7, 5,
            "Bring 32 glass batches.", NMItems.glassBatch.itemID, 0, false, 32,
            "No reward", none(), MINING, false, BRING_CRYSTAL_POWDER_32);

    public static final SkillNode BRING_BEDROLL = bring(
            "bedroll_1", "Field Bedding", BTWItems.bedroll, -2, -2,
            "Bring 1 bedroll.", BTWItems.bedroll.itemID, 0, false, 1,
            "Unlock bandage, bed, and padding recipes.", none(), HUSBANDRY, false);

    public static final SkillNode BRING_BED = bring(
            "bed_1", "Permanent Bedding", Item.bed, -2, -3,
            "Bring 1 bed.", Item.bed.itemID, 0, false, 1,
            "No reward", none(), HUSBANDRY, false, BRING_BEDROLL);

    public static final SkillNode BRING_BED_36 = bring(
            "beds_36", "Dormitory Quartermaster", Item.bed, -1, -3,
            "Bring 36 beds.", Item.bed.itemID, 0, false, 36,
            "No reward", none(), HUSBANDRY, false, BRING_BED);

    public static final SkillNode BRING_VINE_TRAP_16 = bring(
            "vine_traps_16", "Vine Trapper", new ItemStack(BTWBlocks.aestheticVegetation, 1, 0), 3, -3,
            "Bring 16 vine traps.", BTWBlocks.aestheticVegetation.blockID, 0, true, 16,
            "No reward", none(), HUSBANDRY, false);

    public static final SkillNode BRING_VINE_256 = bring(
            "vines_256", "Vine Stockpile", Block.vine, 6, 0,
            "Bring 256 vines.", Block.vine.blockID, 0, false, 256,
            "No reward", none(), HUSBANDRY, false);

    public static final SkillNode BRING_COCOA_POWDER_256 = bring(
            "cocoa_powder_256", "Cocoa Milling", new ItemStack(Item.dyePowder, 1, 3), 3, 5,
            "Bring 256 cocoa powder.", Item.dyePowder.itemID, 3, true, 256,
            "Unlock chocolate processing.", none(), HUSBANDRY, false);

    public static final SkillNode BRING_GLUE_SLURRY_16 = bring(
            "glue_slurry_16", "Adhesive Slurry", NMItems.glueSlurry, 4, -3,
            "Bring 16 glue slurry.", NMItems.glueSlurry.itemID, 0, false, 16,
            "No reward", none(), HUSBANDRY, false);

    public static final SkillNode BRING_PRESSED_GLUE_CAKE_16 = bring(
            "pressed_glue_cakes_16", "Pressed Adhesive", NMItems.pressedGlueCake, 5, -3,
            "Bring 16 pressed glue cakes.", NMItems.pressedGlueCake.itemID, 0, false, 16,
            "No reward", none(), HUSBANDRY, false, BRING_GLUE_SLURRY_16);

    public static final SkillNode CATCH_RARE_ITEM_16 = counter(
            "catch_rare_items_16", "Rare Catch Ledger", Item.fishingRod, 5, 3,
            "Catch 16 rare fishing items.",
            (p, w) -> SkillHandler.getPlayerData(p).rareItemsCaught >= 16,
            "No reward", none(), HUSBANDRY, false);

    public static final SkillNode BRING_DYE_64 = bring(
            "dyes_64", "Dyer's Stockpile", Item.dyePowder, 1, 1,
            "Bring 64 dyes of any color.", Item.dyePowder.itemID, 0, false, 64,
            "No reward", none(), KNOWLEDGE, false);

    public static final SkillNode BRING_DYE_BLEND_16 = bring(
            "dye_blends_16", "Chromatic Binder", NMItems.dyeBlend, 5, 5,
            "Bring 16 dye blends.", NMItems.dyeBlend.itemID, 0, false, 16,
            "No reward", none(), KNOWLEDGE, false, BRING_DYE_64);

    public static final SkillNode BRING_SLAB_1000 = bringAny(
            "slabs_1000", "Slab Logistics", Block.woodSingleSlab, 4, 1,
            "Bring 1,000 slabs of any kind.", 1000,
            new Item[]{Item.itemsList[Block.woodSingleSlab.blockID], Item.itemsList[Block.stoneSingleSlab.blockID],
                    Item.itemsList[BTWBlocks.stoneSlab.blockID], Item.itemsList[BTWBlocks.stoneBrickSlab.blockID]},
            "No reward", none(), KNOWLEDGE, false);

    public static final SkillNode BRING_SAW = bring(
            "saw_1", "Saw Ownership", BTWBlocks.saw, 6, 0,
            "Bring 1 saw.", BTWBlocks.saw.blockID, 0, false, 1,
            "No reward", none(), KNOWLEDGE, false);

    public static final SkillNode BRING_LADDER_64 = bring(
            "ladders_64", "Wooden Ascent", BTWBlocks.ladder, 2, 5,
            "Bring 64 ladders.", BTWBlocks.ladder.blockID, 0, false, 64,
            "No reward", none(), KNOWLEDGE, false);

    public static final SkillNode BRING_STONE_LADDER_64 = bring(
            "stone_ladders_64", "Masonry Ascent", NMBlocks.stoneLadder, 1, 5,
            "Bring 64 stone ladders.", NMBlocks.stoneLadder.blockID, 0, false, 64,
            "No reward", none(), KNOWLEDGE, false, BRING_LADDER_64);

    public static final SkillNode BRING_IRON_LADDER_64 = bring(
            "iron_ladders_64", "Industrial Ascent", NMBlocks.ironLadder, 0, 5,
            "Bring 64 iron ladders.", NMBlocks.ironLadder.blockID, 0, false, 64,
            "No reward", none(), KNOWLEDGE, false, BRING_STONE_LADDER_64);

    public static final SkillNode BRING_STATION_RAIL_16 = bring(
            "station_rails_16", "Freight Station", NMBlocks.stationRail, -2, 0,
            "Bring 16 station rails.", NMBlocks.stationRail.blockID, 0, false, 16,
            "No reward", none(), KNOWLEDGE, false);

    public static final SkillNode BRING_AQUAMARINE_16 = bring(
            "aquamarine_16", "Aquamarine Sample", NMItems.aquamarine, 5, 4,
            "Bring 16 aquamarine.", NMItems.aquamarine.itemID, 0, false, 16,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F), KNOWLEDGE, false);

    public static final SkillNode BRING_AQUAMARINE_64 = bring(
            "aquamarine_64", "Aquamarine Stockpile", NMItems.aquamarine, 4, 4,
            "Bring 64 aquamarine.", NMItems.aquamarine.itemID, 0, false, 64,
            "+1% block breaking speed.", SkillRewardActions.addBlockBreakSpeed(0.01F), KNOWLEDGE, false,
            BRING_AQUAMARINE_16);

    public static final SkillNode BRING_REDSTONE_BLOCK_16 = bring(
            "redstone_blocks_16", "Compressed Signal", Block.blockRedstone, 3, 4,
            "Bring 16 redstone blocks.", Block.blockRedstone.blockID, 0, false, 16,
            "No reward", none(), KNOWLEDGE, false);

    public static final SkillNode BRING_LAPIS_BLOCK_16 = bring(
            "lapis_blocks_16", "Compressed Azure", Block.blockLapis, 6, 5,
            "Bring 16 lapis blocks.", Block.blockLapis.blockID, 0, false, 16,
            "No reward", none(), KNOWLEDGE, false);

    public static final SkillNode CRAFT_BOOK_64 = counter(
            "craft_books_64", "Bookbinder", Item.book, 0, 2,
            "Craft 64 books.", (p, w) -> SkillHandler.getPlayerData(p).booksCrafted >= 64,
            "No reward", none(), KNOWLEDGE, false);

    public static final SkillNode CRAFT_BOOK_256 = counter(
            "craft_books_256", "Archive Binder", Item.book, 5, 1,
            "Craft 256 books.", (p, w) -> SkillHandler.getPlayerData(p).booksCrafted >= 256,
            "No reward", none(), KNOWLEDGE, false, CRAFT_BOOK_64);

    public static final SkillNode BREW_POTION_64 = counter(
            "brew_potions_64", "Working Brewer", Item.potion, 5, 2,
            "Brew 64 potions.", (p, w) -> SkillHandler.getPlayerData(p).potionsBrewed >= 64,
            "No reward", none(), RITUAL, false);

    public static final SkillNode BREW_POTION_256 = counter(
            "brew_potions_256", "Master Brewer", Item.potion, 5, 3,
            "Brew 256 potions.", (p, w) -> SkillHandler.getPlayerData(p).potionsBrewed >= 256,
            "No reward", none(), RITUAL, false, BREW_POTION_64);

    public static final SkillNode KILL_ZOMBIE_100 = counter(
            "kill_zombies_100", "Zombie Cull", Item.rottenFlesh, 0, 3,
            "Kill 100 zombies.", (p, w) -> SkillHandler.getPlayerData(p).zombiesKilled >= 100,
            "No reward", none(), COMBAT, false);

    public static final SkillNode KILL_ZOMBIE_1000 = counter(
            "kill_zombies_1000", "Zombie Extirpation", Item.rottenFlesh, 3, 4,
            "Kill 1,000 zombies.", (p, w) -> SkillHandler.getPlayerData(p).zombiesKilled >= 1000,
            "No reward", none(), COMBAT, false, KILL_ZOMBIE_100);

    public static final SkillNode KILL_SKELETON_100 = counter(
            "kill_skeletons_100", "Skeleton Cull", Item.bone, 0, 2,
            "Kill 100 skeletons.", (p, w) -> SkillHandler.getPlayerData(p).skeletonsKilled >= 100,
            "No reward", none(), COMBAT, false);

    public static final SkillNode KILL_SKELETON_1000 = counter(
            "kill_skeletons_1000", "Skeleton Extirpation", Item.bone, 2, 4,
            "Kill 1,000 skeletons.", (p, w) -> SkillHandler.getPlayerData(p).skeletonsKilled >= 1000,
            "No reward", none(), COMBAT, false, KILL_SKELETON_100);

    public static final SkillNode BRING_LEATHER_ARMOR_SET = itemSet(
            "leather_armor_set", "Leather Wardrobe", Item.helmetLeather, 0, -1,
            "Bring a full set of leather armor.",
            new Item[]{Item.helmetLeather, Item.plateLeather, Item.legsLeather, Item.bootsLeather}, COMBAT);

    public static final SkillNode BRING_PADDED_ARMOR_SET = itemSet(
            "padded_armor_set", "Padded Wardrobe", BTWItems.paddedHelmet, 1, 4,
            "Bring a full set of padded armor.",
            new Item[]{BTWItems.paddedHelmet, BTWItems.paddedChest, BTWItems.paddedLeggings, BTWItems.paddedBoots}, COMBAT);

    public static final SkillNode BRING_GIMP_ARMOR_SET = itemSet(
            "gimp_armor_set", "Reinforced Leather Wardrobe", BTWItems.gimpHelmet, 0, 4,
            "Bring a full set of gimp armor.",
            new Item[]{BTWItems.gimpHelmet, BTWItems.gimpChest, BTWItems.gimpLeggings, BTWItems.gimpBoots}, COMBAT);

    public static final SkillNode BRING_CHAIN_ARMOR_SET = itemSet(
            "chain_armor_set", "Chain Wardrobe", Item.helmetChain, -1, 4,
            "Bring a full set of chain armor.",
            new Item[]{Item.helmetChain, Item.plateChain, Item.legsChain, Item.bootsChain}, COMBAT);

    public static final SkillNode BRING_IRON_ARMOR_SET = itemSet(
            "iron_armor_set", "Iron Wardrobe", Item.helmetIron, -1, 3,
            "Bring a full set of iron armor.",
            new Item[]{Item.helmetIron, Item.plateIron, Item.legsIron, Item.bootsIron}, COMBAT);

    public static final SkillNode BRING_GOLD_ARMOR_SET = itemSet(
            "gold_armor_set", "Gold Wardrobe", Item.helmetGold, 1, -2,
            "Bring a full set of gold armor.",
            new Item[]{Item.helmetGold, Item.plateGold, Item.legsGold, Item.bootsGold}, COMBAT);

    public static final SkillNode BRING_DIAMOND_ARMOR_SET = itemSet(
            "diamond_armor_set", "Diamond Wardrobe", Item.helmetDiamond, 2, -2,
            "Bring a full set of diamond armor.",
            new Item[]{Item.helmetDiamond, Item.plateDiamond, Item.legsDiamond, Item.bootsDiamond}, COMBAT);

    public static final SkillNode BRING_STEEL_ARMOR_SET = itemSet(
            "steel_armor_set", "Soulforged Wardrobe", BTWItems.plateHelmet, 4, -2,
            "Bring a full set of soulforged steel armor.",
            new Item[]{BTWItems.plateHelmet, BTWItems.plateBreastplate, BTWItems.plateLeggings, BTWItems.plateBoots}, COMBAT);

    public static final SkillNode BRING_BLOOD_ARMOR_SET = itemSet(
            "blood_armor_set", "Blood Wardrobe", NMItems.bloodHelmet, 5, -2,
            "Bring a full set of blood armor.",
            new Item[]{NMItems.bloodHelmet, NMItems.bloodChestplate, NMItems.bloodLeggings, NMItems.bloodBoots}, COMBAT);

    public static final SkillNode BRING_NETHERRACK_TIER_ONE_64 = bring(
            "netherrack_tier_one_64", "First-Ring Stone", new ItemStack(Block.netherrack, 1, 2), 6, 4,
            "Bring 64 tier-one netherrack.", Block.netherrack.blockID, 2, true, 64,
            "Unlock first-ring netherrack processing.", none(), MINING, false, BRING_CRUDE_OBSIDIAN_16);

    public static final SkillNode BRING_NETHERRACK_TIER_ONE_256 = bring(
            "netherrack_tier_one_256", "First-Ring Mason", new ItemStack(Block.netherrack, 1, 2), 6, 5,
            "Bring 256 tier-one netherrack.", Block.netherrack.blockID, 2, true, 256,
            "Unlock bulk first-ring compression.", none(), MINING, false, BRING_NETHERRACK_TIER_ONE_64);

    public static final SkillNode BRING_NETHERRACK_TIER_ONE_1024 = bring(
            "netherrack_tier_one_1024", "First-Ring Quarry", new ItemStack(Block.netherrack, 1, 2), 5, 5,
            "Bring 1,024 tier-one netherrack.", Block.netherrack.blockID, 2, true, 1024,
            "Unlock industrial first-ring masonry.", none(), MINING, false, BRING_NETHERRACK_TIER_ONE_256);

    public static final SkillNode BRING_NETHERRACK_TIER_TWO_64 = bring(
            "netherrack_tier_two_64", "Second-Ring Stone", new ItemStack(Block.netherrack, 1, 3), 7, 6,
            "Bring 64 tier-two netherrack.", Block.netherrack.blockID, 3, true, 64,
            "Unlock second-ring netherrack processing.", none(), MINING, false, BRING_DIAMOND_16, TRADE_100);

    public static final SkillNode BRING_NETHERRACK_TIER_TWO_256 = bring(
            "netherrack_tier_two_256", "Second-Ring Mason", new ItemStack(Block.netherrack, 1, 3), 6, 6,
            "Bring 256 tier-two netherrack.", Block.netherrack.blockID, 3, true, 256,
            "Unlock bulk second-ring compression.", none(), MINING, false, BRING_NETHERRACK_TIER_TWO_64);

    public static final SkillNode BRING_NETHERRACK_TIER_TWO_1024 = bring(
            "netherrack_tier_two_1024", "Second-Ring Quarry", new ItemStack(Block.netherrack, 1, 3), 5, 6,
            "Bring 1,024 tier-two netherrack.", Block.netherrack.blockID, 3, true, 1024,
            "Unlock industrial second-ring masonry.", none(), MINING, false, BRING_NETHERRACK_TIER_TWO_256);

    public static final SkillNode BRING_NETHERRACK_TIER_THREE_64 = bring(
            "netherrack_tier_three_64", "Third-Ring Stone", new ItemStack(Block.netherrack, 1, 4), -3, 1,
            "Bring 64 tier-three netherrack.", Block.netherrack.blockID, 4, true, 64,
            "Unlock third-ring netherrack processing.", none(), MINING, false, BRING_DENSE_NETHERRACK_CORE_16);

    public static final SkillNode BRING_NETHERRACK_TIER_THREE_256 = bring(
            "netherrack_tier_three_256", "Third-Ring Mason", new ItemStack(Block.netherrack, 1, 4), -3, -2,
            "Bring 256 tier-three netherrack.", Block.netherrack.blockID, 4, true, 256,
            "Unlock bulk third-ring compression.", none(), MINING, false,
            BRING_NETHERRACK_TIER_THREE_64, KILL_WITHER, BRING_SOULFORGED_STEEL_INGOT_8);

    public static final SkillNode BRING_NETHERRACK_TIER_THREE_1024 = bring(
            "netherrack_tier_three_1024", "Third-Ring Quarry", new ItemStack(Block.netherrack, 1, 4), -3, -3,
            "Bring 1,024 tier-three netherrack.", Block.netherrack.blockID, 4, true, 1024,
            "Unlock industrial third-ring masonry.", none(), MINING, false,
            BRING_NETHERRACK_TIER_THREE_256, BRING_DEADZONE_SHARD_16);

    public static final SkillNode BRING_QUARTZ_16 = bring(
            "nether_quartz_16", "Quartz Survey", Item.netherQuartz, 1, 5,
            "Bring 16 nether quartz.", Item.netherQuartz.itemID, 0, false, 16,
            "Unlock quartz-based Nether processing.", none(), MINING, false, BRING_NETHERRACK_TIER_ONE_64);

    public static final SkillNode BRING_QUARTZ_DUST_32 = bring(
            "quartz_dust_32", "Quartz Refining", NMItems.quartzDust, 0, 5,
            "Bring 32 quartz dust.", NMItems.quartzDust.itemID, 0, false, 32,
            "Unlock quartz-dust components.", none(), MINING, false, BRING_QUARTZ_16);

    public static final SkillNode BRING_NETHER_STICK_16 = bring(
            "nether_stick_16", "Infernal Handles", NMItems.netherStick, -1, -3,
            "Bring 16 nether sticks.", NMItems.netherStick.itemID, 0, false, 16,
            "Unlock Nether-handled tools.", none(), MINING, false, BRING_NETHERRACK_TIER_ONE_64);

    public static final SkillNode BRING_NETHERRACK_CHUNK_16 = bring(
            "netherrack_chunk_16", "Netherrack Aggregate", NMItems.netherrackChunk, 0, -3,
            "Bring 16 netherrack chunks.", NMItems.netherrackChunk.itemID, 0, false, 16,
            "Unlock netherrack construction recipes.", none(), MINING, false, BRING_NETHERRACK_TIER_ONE_64);

    public static final SkillNode BRING_TUNGSTEN_CHUNK_16 = bring(
            "tungsten_chunk_16", "Tungsten Prospecting", NMItems.tungstenChunk, -2, 5,
            "Bring 16 tungsten chunks.", NMItems.tungstenChunk.itemID, 0, false, 16,
            "Unlock early tungsten processing.", none(), MINING, false, BRING_QUARTZ_16);

    public static final SkillNode BRING_TUNGSTEN_CONCENTRATE_16 = bring(
            "tungsten_concentrate_16", "Tungsten Concentration", NMItems.tungstenConcentrate, -2, 2,
            "Bring 16 tungsten concentrate.", NMItems.tungstenConcentrate.itemID, 0, false, 16,
            "Unlock concentrated tungsten recipes.", none(), MINING, false, BRING_TUNGSTEN_CHUNK_16);

    public static final SkillNode BRING_TUNGSTEN_POWDER_32 = bring(
            "tungsten_powder_32", "Tungsten Powderwork", NMItems.tungstenPowder, -2, 0,
            "Bring 32 tungsten powder.", NMItems.tungstenPowder.itemID, 0, false, 32,
            "Unlock pure tungsten consolidation.", none(), MINING, false, BRING_TUNGSTEN_CONCENTRATE_16);

    public static final SkillNode BRING_TUNGSTEN_INGOT_8 = bring(
            "tungsten_ingot_8", "Tungsten Metallurgy", NMItems.tungstenIngot, -2, -3,
            "Bring 8 tungsten ingots.", NMItems.tungstenIngot.itemID, 0, false, 8,
            "Unlock tungsten tools and machinery.", none(), MINING, false, BRING_TUNGSTEN_POWDER_32);

    public static final SkillNode BRING_AZURE_SALT_16 = bring(
            "azure_salt_16", "Azure Chemistry", NMItems.azureSalt, 4, 6,
            "Bring 16 azure salt.", NMItems.azureSalt.itemID, 0, false, 16,
            "Unlock azure chemical recipes.", none(), MINING, false, BRING_NETHERRACK_TIER_TWO_64);

    public static final SkillNode BRING_SEARING_SILVER_SCALE_4 = bring(
            "searing_silver_scale_4", "Searing Silver", NMItems.searingSilverScale, 3, 6,
            "Bring 4 searing silver scales.", NMItems.searingSilverScale.itemID, 0, false, 4,
            "Unlock searing-scale components.", none(), MINING, false, BRING_AZURE_SALT_16);

    public static final SkillNode BRING_POTASSIUM_CRYSTAL_16 = bring(
            "potassium_crystal_16", "Potassium Extraction", NMItems.potassiumCrystal, 0, 6,
            "Bring 16 potassium crystals.", NMItems.potassiumCrystal.itemID, 0, false, 16,
            "Unlock potassium fertilizer production.", none(), MINING, false, CRAFT_BOOK_64);

    public static final SkillNode BRING_NITROGEN_CRYSTAL_16 = bring(
            "nitrogen_crystal_16", "Nitrogen Extraction", NMItems.nitrogenCrystal, -1, 6,
            "Bring 16 nitrogen crystals.", NMItems.nitrogenCrystal.itemID, 0, false, 16,
            "Unlock crystallized nitrogen processing.", none(), MINING, false, CRAFT_BOOK_64);

    public static final SkillNode BRING_ACID_CRYSTAL_16 = bring(
            "acid_crystal_16", "Acidity Extraction", NMItems.acidCrystal, -2, 6,
            "Bring 16 acid crystals.", NMItems.acidCrystal.itemID, 0, false, 16,
            "Unlock acid fertilizer production.", none(), MINING, false, CRAFT_BOOK_64);

    public static final SkillNode BRING_POROSITY_AGGREGATE_16 = bring(
            "porosity_aggregate_16", "Porosity Extraction", NMItems.porosityAggregate, -3, 6,
            "Bring 16 porosity aggregate.", NMItems.porosityAggregate.itemID, 0, false, 16,
            "Unlock porosity fertilizer production.", none(), MINING, false, CRAFT_BOOK_64);

    public static final SkillNode BRING_ASH_16 = bring(
            "nether_ash_16", "Ash Gathering", NMItems.ash, 1, -3,
            "Bring 16 ash.", NMItems.ash.itemID, 0, false, 16,
            "Unlock ash-based Nether recipes.", none(), MINING, false, BRING_NETHERRACK_TIER_ONE_64);

    public static final SkillNode BRING_SOUL_CHIP_16 = bring(
            "soul_chip_16", "Soul Knapping", NMItems.soulChip, 3, -3,
            "Bring 16 soul chips.", NMItems.soulChip.itemID, 0, false, 16,
            "Unlock soul-flint recipes.", none(), MINING, false, BRING_NETHERRACK_TIER_ONE_64);

    public static final SkillNode BRING_PIG_HIDE_16 = bring(
            "pig_hide_16", "Infernal Hidework", NMItems.pigHide, 4, -3,
            "Bring 16 pig hides.", NMItems.pigHide.itemID, 0, false, 16,
            "Unlock pig-hide cordage.", none(), MINING, false, BRING_NETHERRACK_TIER_ONE_64);

    public static final SkillNode BRING_BONE_SHARD_16 = bring(
            "bone_shard_16", "Infernal Bonework", NMItems.boneShard, 6, -3,
            "Bring 16 bone shards.", NMItems.boneShard.itemID, 0, false, 16,
            "Unlock Nether fishing components.", none(), MINING, false, BRING_NETHERRACK_TIER_ONE_64);

    public static final SkillNode BRING_TUNGSTEN_DUST_32 = bring(
            "tungsten_dust_32", "Tungsten Dust Survey", NMItems.tungstenDust, -1, 5,
            "Bring 32 tungsten dust.", NMItems.tungstenDust.itemID, 0, false, 32,
            "Unlock tungsten-dust consolidation.", none(), MINING, false, BRING_QUARTZ_DUST_32);

    public static final SkillNode BRING_CRUSHED_TUNGSTEN_16 = bring(
            "crushed_tungsten_16", "Tungsten Crushing", NMItems.crushedTungsten, -2, 3,
            "Bring 16 crushed tungsten.", NMItems.crushedTungsten.itemID, 0, false, 16,
            "Unlock tungsten concentration.", none(), MINING, false, BRING_TUNGSTEN_CHUNK_16);

    public static final SkillNode BRING_BRITTLE_TUNGSTEN_CAKE_16 = bring(
            "brittle_tungsten_cake_16", "Brittle Tungsten", NMItems.brittleTungstenCake, -2, 1,
            "Bring 16 brittle tungsten cakes.", NMItems.brittleTungstenCake.itemID, 0, false, 16,
            "Unlock late tungsten refining.", none(), MINING, false, BRING_TUNGSTEN_CONCENTRATE_16);

    public static final SkillNode BRING_PURE_TUNGSTEN_CHUNK_16 = bring(
            "pure_tungsten_chunk_16", "Pure Tungsten", NMItems.pureTungstenChunk, -2, -1,
            "Bring 16 pure tungsten chunks.", NMItems.pureTungstenChunk.itemID, 0, false, 16,
            "Unlock pure tungsten smelting.", none(), MINING, false, BRING_TUNGSTEN_POWDER_32);

    public static final SkillNode BRING_TUNGSTEN_NUGGET_32 = bring(
            "tungsten_nugget_32", "Tungsten Casting", NMItems.tungstenNugget, -2, -2,
            "Bring 32 tungsten nuggets.", NMItems.tungstenNugget.itemID, 0, false, 32,
            "Unlock tungsten ingot consolidation.", none(), MINING, false, BRING_PURE_TUNGSTEN_CHUNK_16);

    public static final SkillNode BRING_OBSIDIAN_POWDER_32 = bring(
            "obsidian_powder_32", "Obsidian Grinding", NMItems.obsidianPowder, 4, 5,
            "Bring 32 obsidian powder.", NMItems.obsidianPowder.itemID, 0, false, 32,
            "Unlock obsidian paste.", none(), MINING, false, BRING_NETHERRACK_TIER_ONE_256);

    public static final SkillNode BRING_OBSIDIAN_PASTE_16 = bring(
            "obsidian_paste_16", "Obsidian Binding", NMItems.obsidianPaste, 3, 5,
            "Bring 16 obsidian paste.", NMItems.obsidianPaste.itemID, 0, false, 16,
            "Unlock fired obsidian bricks.", none(), MINING, false, BRING_OBSIDIAN_POWDER_32);

    public static final SkillNode BRING_OBSIDIAN_BRICK_16 = bring(
            "obsidian_brick_16", "Obsidian Masonry", NMItems.obsidianBrick, 2, 5,
            "Bring 16 obsidian bricks.", NMItems.obsidianBrick.itemID, 0, false, 16,
            "Unlock obsidian machinery.", none(), MINING, false, BRING_OBSIDIAN_PASTE_16);

    public static final SkillNode BRING_AZURE_SLAG_16 = bring(
            "azure_slag_16", "Azure Slagwork", NMItems.azureSlag, 2, 6,
            "Bring 16 azure slag.", NMItems.azureSlag.itemID, 0, false, 16,
            "Unlock fired azure materials.", none(), MINING, false, BRING_AZURE_SALT_16);

    public static final SkillNode BRING_BRITTLE_AZURE_CAKE_16 = bring(
            "brittle_azure_cake_16", "Brittle Azure", NMItems.brittleAzureCake, 1, 6,
            "Bring 16 brittle azure cakes.", NMItems.brittleAzureCake.itemID, 0, false, 16,
            "Unlock advanced azure components.", none(), MINING, false, BRING_AZURE_SLAG_16);

    public static final SkillNode BRING_ASH_CLUMP_16 = bring(
            "ash_clump_16", "Compacted Ash", NMItems.ashClump, 2, -3,
            "Bring 16 ash clumps.", NMItems.ashClump.itemID, 0, false, 16,
            "Unlock ash-sludge mixtures.", none(), MINING, false, BRING_ASH_16);

    public static final SkillNode BRING_PIGHIDE_STRING_16 = bring(
            "pighide_string_16", "Infernal Cordage", NMItems.pighideString, 5, -3,
            "Bring 16 pig-hide string.", NMItems.pighideString.itemID, 0, false, 16,
            "Unlock corded Nether tools.", none(), MINING, false, BRING_PIG_HIDE_16);

    public static final SkillNode BRING_NETHER_WORKBENCH_PART_4 = bring(
            "nether_workbench_part_4", "Infernal Joinery", NMItems.netherWorkbenchPart, -2, 4,
            "Bring 4 Nether workbench parts.", NMItems.netherWorkbenchPart.itemID, 0, false, 4,
            "Unlock the Nether crafting table.", none(), MINING, false,
            BRING_QUARTZ_DUST_32, BRING_TUNGSTEN_CHUNK_16);

    public static final SkillNode BRING_OBSIDIAN_SHARD_16 = bring(
            "obsidian_shard_16", "Obsidian Shards", NMItems.obsidianShard, 6, 3,
            "Bring 16 obsidian shards.", NMItems.obsidianShard.itemID, 0, false, 16,
            "Unlock shard consolidation.", none(), MINING, false, BRING_CRUDE_OBSIDIAN_16);

    public static final SkillNode BRING_INVOCATION_FRAGMENT_4 = bring(
            "invocation_fragment_4", "Invocation Fragments", NMItems.invocationFragment, 4, -4,
            "Bring 4 invocation fragments.", NMItems.invocationFragment.itemID, 0, false, 4,
            "Unlock invocation-seal assembly.", none(), MINING, false, BRING_RUNED_WITHER_SKELETON_SKULL);

    public static final SkillNode BRING_END_ACCORD_FRAGMENT_4 = bring(
            "end_accord_fragment_4", "Accord Fragments", NMItems.endAccordFragment, 5, -4,
            "Bring 4 End Accord fragments.", NMItems.endAccordFragment.itemID, 0, false, 4,
            "Unlock End Accord assembly.", none(), MINING, false, BRING_VESSEL_OF_THE_DRAGON_2);

    public static final SkillNode BRING_NETHER_TRADE_PLACEHOLDER_4 = bring(
            "nether_trade_component_4", "Infernal Trade Components", NMItems.netherTradePlaceholder, -3, -4,
            "Bring 4 unfinished Nether trade components.", NMItems.netherTradePlaceholder.itemID, 0, false, 4,
            "Unlock late Nether trade-component recipes.", none(), MINING, false, BRING_NETHERRACK_TIER_THREE_64);

    // Post-dragon progression is grouped by activity instead of being treated as one
    // oversized mining chain. Each branch gets a compact late-game cluster.
    public static final SkillNode BRING_EYE_OF_ENDER_ECLIPSE = bring(
            "eclipse_eye", "Beyond the Dragon", Item.eyeOfEnder, -1, -1,
            "Bring 1 eye of ender.", Item.eyeOfEnder.itemID, 0, false, 1,
            "Unlock post-dragon End crafting.", none(), RITUAL, false, BRING_END_ACCORD_FRAGMENT_4);

    public static final SkillNode BRING_RAW_MERCURY_16 = bring(
            "raw_mercury_16", "Mercury Survey", NMItems.rawMercuryCrystal, 6, -4,
            "Bring 16 raw Mercury crystals.", NMItems.rawMercuryCrystal.itemID, 0, false, 16,
            "Record Mercury extraction.", none(), MINING, false, BRING_EYE_OF_ENDER_ECLIPSE);

    public static final SkillNode BRING_ENDER_CRYSTAL_16 = bring(
            "ender_crystal_16", "Automated Endermen", NMItems.enderCrystal, 6, -1,
            "Bring 16 Ender Crystals.", NMItems.enderCrystal.itemID, 0, false, 16,
            "Record post-dragon Enderman farming.", none(), COMBAT, false, BRING_EYE_OF_ENDER_ECLIPSE);

    public static final SkillNode BRING_ENDER_SHELL_16 = bring(
            "ender_shell_16", "Nest Breaker", NMItems.enderShell, 6, 0,
            "Bring 16 Ender Shells.", NMItems.enderShell.itemID, 0, false, 16,
            "Record Ender Nest harvesting.", none(), COMBAT, false, BRING_EYE_OF_ENDER_ECLIPSE);

    public static final SkillNode BRING_DARKSUN_FRAGMENT_16 = bring(
            "darksun_fragment_16", "Eclipse Cull", NMItems.darksunFragment, 6, 1,
            "Bring 16 Darksun Fragments.", NMItems.darksunFragment.itemID, 0, false, 16,
            "Record sustained Eclipse combat.", none(), COMBAT, false, BRING_EYE_OF_ENDER_ECLIPSE);

    public static final SkillNode BRING_PALE_ROOT_SEEDS_8 = bring(
            "pale_root_seeds_8", "Endstone Cultivation", NMItems.paleRootSeeds, 6, -3,
            "Bring 8 Pale Root Seeds.", NMItems.paleRootSeeds.itemID, 0, false, 8,
            "Record a renewable Pale Root seed stock.", none(), HUSBANDRY, false, BRING_EYE_OF_ENDER_ECLIPSE);

    public static final SkillNode BRING_PALE_ROOT_32 = bring(
            "pale_root_32", "Deep-End Husbandry", NMItems.paleRoot, 7, -3,
            "Bring 32 Pale Roots.", NMItems.paleRoot.itemID, 0, false, 32,
            "Record Pale Root agriculture.", none(), HUSBANDRY, false, BRING_PALE_ROOT_SEEDS_8);

    public static final SkillNode BRING_WASHED_MERCURY_8 = bring(
            "washed_mercury_8", "Mercury Washing", NMItems.washedMercuryConcentrate, 7, -4,
            "Bring 8 washed Mercury concentrate.", NMItems.washedMercuryConcentrate.itemID, 0, false, 8,
            "Record cistern Mercury refinement.", none(), MINING, false, BRING_RAW_MERCURY_16);

    public static final SkillNode BRING_ENDER_DUST_16 = bring(
            "ender_dust_16", "Crystal Milling", NMItems.enderDust, 3, -3,
            "Bring 16 Ender Dust.", NMItems.enderDust.itemID, 0, false, 16,
            "Record Ender Crystal milling.", none(), KNOWLEDGE, false, BRING_ENDER_CRYSTAL_16);

    public static final SkillNode BRING_ENDER_SHELL_POWDER_16 = bring(
            "ender_shell_powder_16", "Shell Milling", NMItems.enderShellPowder, 4, -3,
            "Bring 16 Ender Shell Powder.", NMItems.enderShellPowder.itemID, 0, false, 16,
            "Record shell processing.", none(), KNOWLEDGE, false, BRING_ENDER_SHELL_16);

    public static final SkillNode BRING_MERCURY_AMALGAM_8 = bring(
            "mercury_amalgam_8", "Mercury Amalgamation", NMItems.mercuryAmalgam, 8, -4,
            "Bring 8 Mercury Amalgam.", NMItems.mercuryAmalgam.itemID, 0, false, 8,
            "Record acidic Mercury refinement.", none(), MINING, false, BRING_WASHED_MERCURY_8);

    public static final SkillNode BRING_PALE_ROOT_PULP_16 = bring(
            "pale_root_pulp_16", "Root Pulping", NMItems.paleRootPulp, 7, -2,
            "Bring 16 Pale Root Pulp.", NMItems.paleRootPulp.itemID, 0, false, 16,
            "Record mechanical Pale Root processing.", none(), HUSBANDRY, false, BRING_PALE_ROOT_32);

    public static final SkillNode BRING_PALE_ROOT_RESIN_8 = bring(
            "pale_root_resin_8", "Root Resin", NMItems.paleRootResin, 7, -1,
            "Bring 8 Pale Root Resin.", NMItems.paleRootResin.itemID, 0, false, 8,
            "Record brine resin processing.", none(), HUSBANDRY, false, BRING_PALE_ROOT_PULP_16);

    public static final SkillNode BRING_FIRED_CRUCIBLE_LINER = bring(
            "fired_crucible_liner", "Ender Ceramics", NMItems.firedCrucibleLiner, 5, -3,
            "Bring 1 fired Crucible Liner.", NMItems.firedCrucibleLiner.itemID, 0, false, 1,
            "Record turntable shaping and kiln firing.", none(), KNOWLEDGE, false, BRING_ENDER_SHELL_POWDER_16);

    public static final SkillNode BRING_PHASE_STEEL_CHARGE_4 = bring(
            "phase_steel_charge_4", "Ender Assembly", NMItems.phaseSteelCharge, 6, -3,
            "Bring 4 Phase Steel Charges.", NMItems.phaseSteelCharge.itemID, 0, false, 4,
            "Record powered Ender Assembler work.", none(), KNOWLEDGE, false,
            BRING_MERCURY_AMALGAM_8, BRING_ENDER_DUST_16, BRING_PALE_ROOT_RESIN_8, BRING_FIRED_CRUCIBLE_LINER);

    public static final SkillNode BRING_PHASE_STEEL_8 = bring(
            "phase_steel_8", "Phase Steel", NMItems.phaseSteelIngot, 8, -3,
            "Bring 8 Phase Steel ingots.", NMItems.phaseSteelIngot.itemID, 0, false, 8,
            "Unlock the final End equipment recipes.", none(), MINING, false, BRING_PHASE_STEEL_CHARGE_4);

    public static final SkillNode BRING_ENDER_MECHANISM_4 = bring(
            "ender_mechanism_4", "Ender Mechanisms", NMItems.enderMechanism, 7, -3,
            "Bring 4 Ender Mechanisms.", NMItems.enderMechanism.itemID, 0, false, 4,
            "Complete the End industry cluster.", none(), KNOWLEDGE, false, BRING_PHASE_STEEL_8);

    public static final SkillNode POSSESS_BLOOD_BONE_4 = counter(
            "blood_bone_altar_4", "Bloodwither Altar", NMBlocks.bloodBones, -1, -2,
            "Possess 4 Blood Bone Blocks.",
            (player, world) -> SkillInventory.has(player, NMBlocks.bloodBones.blockID, 0, false, 4),
            "Record the completed Bloodwither altar without consuming it.", none(),
            RITUAL, false, BRING_ENDER_MECHANISM_4);

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

    private static SkillNode bringAny(
            String id, String name, Object icon, int x, int y, String requirement, int count, Item[] items,
            String rewardText, SkillUnlockAction reward, SkillBranch branch, boolean worldReward,
            SkillNode... parents) {
        SkillNodeProvider.BuildStep step = getBuilder().id(loc(id)).name(name).icon(stack(icon)).displayLocation(x, y)
                .requirementText(requirement)
                .triggerCondition((player, world) -> SkillInventory.hasAny(player, count, items))
                .onUnlockConsume((player, world) -> SkillInventory.consumeAny(player, count, items));
        if (parents.length > 0) step.parents(parents);
        step.reward(rewardText, reward);
        if (worldReward) step.worldReward();
        return step.build().register(branch);
    }

    private static SkillNode itemSet(
            String id, String name, Object icon, int x, int y, String requirement, Item[] items,
            SkillBranch branch) {
        SkillNodeProvider.BuildStep step = getBuilder().id(loc(id)).name(name).icon(stack(icon)).displayLocation(x, y)
                .requirementText(requirement)
                .triggerCondition((player, world) -> {
                    for (Item item : items) {
                        if (!SkillInventory.has(player, item, 1)) return false;
                    }
                    return true;
                })
                .onUnlockConsume((player, world) -> {
                    for (Item item : items) SkillInventory.consume(player, item, 1);
                })
                .reward("No reward", none());
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
